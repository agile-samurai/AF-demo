# This script requires AWS credentials available via environment variables,
# either AWS_PROFILE and a credentials file or
# AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY
# in order to load or save data to/from S3.

import boto3
import click
from gensim.models.doc2vec import Doc2Vec, LabeledSentence, TaggedDocument
import json
import multiprocessing
import os
import pandas as pd
import pathlib
from tqdm import tqdm
from nltk import RegexpTokenizer
from nltk.corpus import stopwords

# Local import
import movies


def nlp_clean(data: iter):
    """
    This function does all text cleaning using two objects above
    :param data:
    :return:
    """
    tokenizer = RegexpTokenizer(r'\w+')
    stopword_set = set(stopwords.words('english'))

    clean_data = []
    for d in data:
        new_str = d.lower()
        dlist = tokenizer.tokenize(new_str)
        dlist = list(set(dlist).difference(stopword_set))
        clean_data.append(dlist)
    return clean_data


class TaggedLineDocument(object):
    def __init__(self, doc_list, labels_list):
        self.labels_list = labels_list
        self.doc_list = doc_list

    def __iter__(self):
        for idx, doc in enumerate(self.doc_list):
            yield TaggedDocument(doc, [self.labels_list[idx]])


def train_doc2vec(corpus: TaggedLineDocument):
    # Train model
    print('Training model')
    cores = multiprocessing.cpu_count()
    model = Doc2Vec(vector_size=300, window=10, min_count=0, alpha=0.025, min_alpha=0.025,
                    workers=cores, epochs=20)
    model.build_vocab(corpus)
    model.train(corpus, epochs=model.epochs, total_examples=model.corpus_count)
    return model


@click.command()
@click.option('--local', '-l', is_flag=True, help="Load local data files instead of from S3")
@click.option('--version', '-v', default='0.0.0', help='Version number to use in model filename')
@click.option('--push', '-p', is_flag=True, help="Push model to S3 when finished training")
def cli(local, version, push):
    cwd = pathlib.Path('.').resolve()
    models_dir = cwd.parents[0] / 'models'
    if not models_dir.is_dir():
        models_dir.mkdir()

    if local:
        print("Loading local movies_json data files")
        data_dir = cwd.parents[0] / 'data'
        json_dir = data_dir / 'movies_json'
        movies_list = []
        for json_file in tqdm(json_dir.iterdir()):
            if json_file.is_file():
                with json_file.open() as infile:
                    json_str = infile.read()
                    movies_list.append(json.loads(json_str))
    else:
        # get_json_files loads from S3 and can take ~15 minutes on good connection
        movies_list = movies.get_json_files()

    movies_df = pd.DataFrame(movies_list)
    # Drop movies that don't have a description or a genre
    movies_df.dropna(subset=['description', 'genre'], inplace=True)
    # Replace NaN with empty string for keywords
    movies_df['keywords'].fillna(value='', inplace=True)
    # Concat descriptions & keywords
    movies_text = list(movies_df['description'] + ' ' + movies_df['keywords'])
    movies_text = nlp_clean(movies_text)
    # Attach labels to keep with the documents
    movies_df['imdb_id'] = movies_df['url'].apply(lambda x: pd.Series(x.split('/')[2]))
    movies_df['top_genre'] = movies_df['genre'].apply(lambda x: pd.Series(x[0]))
    movies_labels = list(movies_df['imdb_id'])
    tagged_corpus = TaggedLineDocument(movies_text, movies_labels)

    d2v_model = train_doc2vec(tagged_corpus)

    # Saving the model
    print(f"Saving model version {version}")
    model_filename = f'movies_doc2vec.{version}.model'
    d2v_model.save(str(models_dir / model_filename))

    # Push model to S3
    if push:
        bucket_name = 'rdso-challenge2'
        try:
            profile = os.environ['AWS_PROFILE']
            session = boto3.Session(profile_name=profile)
            s3 = session.client('s3')
        except KeyError:
            try:
                s3 = boto3.client(
                    "s3",
                    aws_access_key_id=os.environ["AWS_ACCESS_KEY_ID"],
                    aws_secret_access_key=os.environ["AWS_SECRET_ACCESS_KEY"],
                )
            except KeyError:
                raise ValueError("No AWS credentials found")

        for model_f in models_dir.iterdir():
            if model_filename in str(model_f):
                with open(str(model_f), 'rb') as outfile:
                    s3.upload_fileobj(outfile, bucket_name,
                                      'models/' + str(model_f.stem) + str(model_f.suffix))
        print(f'Pushed model version {version} to S3')


if __name__ == "__main__":
    # For local development
    # os.environ['AWS_PROFILE'] = 'mdas-ravi10'
    cli()
