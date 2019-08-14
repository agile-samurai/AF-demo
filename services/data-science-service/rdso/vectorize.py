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


tokenizer = RegexpTokenizer(r'\w+')
stopword_set = set(stopwords.words('english'))


def nlp_clean(data: iter):
    """
    This function does all text cleaning using two objects above
    :param data:
    :return:
    """
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


@click.command()
@click.option('--local', '-l', is_flag=True, help="Load local data files instead of from S3")
def cli(local):
    cwd = pathlib.Path('.').resolve()
    models_dir = cwd.parents[0] / 'models'
    if local:
        print("Loading local imdb_json data files")
        data_dir = cwd.parents[0] / 'data'
        json_dir = data_dir / 'imdb_json'
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
    movies_df.dropna(subset=['description'], inplace=True)
    movies_df['keywords'].fillna(value='', inplace=True)
    movies_text = list(movies_df['description'] + ' ' + movies_df['keywords'])
    movies_text = nlp_clean(movies_text)
    movies_df['imdb_id'] = movies_df['url'].apply(lambda x: pd.Series(x.split('/')[2]))
    movies_labels = list(zip(movies_df.index, movies_df['imdb_id']))
    tagged_corpus = TaggedLineDocument(movies_text, movies_labels)

    # Train model
    print('Training model')
    cores = multiprocessing.cpu_count()
    model = Doc2Vec(vector_size=300, window=10, min_count=0, alpha=0.025, min_alpha=0.025,
                    workers=cores, epochs=20)
    model.build_vocab(tagged_corpus)
    model.train(tagged_corpus, epochs=model.epochs, total_examples=model.corpus_count)

    # Saving the model
    model.save(str(models_dir / 'movies_doc2vec.model'))
    print("Model saved.")


if __name__ == "__main__":
    os.environ['AWS_PROFILE'] = 'mdas-ravi10'
    cli()
