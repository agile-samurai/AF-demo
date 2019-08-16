# The following imports are only needed for S3:
import boto3
import os


import click
from gensim.models.doc2vec import Doc2Vec, TaggedDocument
import multiprocessing
import pandas as pd
import pathlib


class TaggedLineDocument(object):
    def __init__(self, doc_list, labels_list):
        self.labels_list = labels_list
        self.doc_list = doc_list

    def __iter__(self):
        for idx, doc in enumerate(self.doc_list):
            yield TaggedDocument(doc, [self.labels_list[idx]])


def train_doc2vec(corpus: TaggedLineDocument):
    """
    Train Doc2Vec model
    :param corpus: Corpus of tagged documents
    :return: trained Doc2Vec model
    """
    cores = multiprocessing.cpu_count()
    model = Doc2Vec(vector_size=300, window=10, min_count=0, alpha=0.025, min_alpha=0.025,
                    workers=cores, epochs=20)
    model.build_vocab(corpus)
    model.train(corpus, epochs=model.epochs, total_examples=model.corpus_count)
    return model


def find_centroids(d2v_model, movies_df):
    """
    Takes the doc vectors and clusters them by genre, then finds the centroid of each cluster.

    :return: dict of cluster_name: centroid_vector pairs.
    """
    genres = movies_df['top_genre'].unique()
    for genre in genres:
        genre_movies_df = movies_df[movies_df['top_genre'] == genre]



@click.command()
@click.option('--version', '-v', default='0.0.0', help='Version number to use in model filename')
# @click.option('--local', '-l', is_flag=True, help="Load local data files instead of from S3")
# @click.option('--push', '-p', is_flag=True, help="Push model to S3 when finished training")
def cli(version):
    """
    Main block with CLI functionality
    """

    # Load movies_df from .pkl file
    cwd = pathlib.Path('.').resolve()
    data_dir = cwd.parents[0] / 'data'
    models_dir = cwd.parents[0] / 'models'
    movies_df_file = data_dir / 'movies_df.pkl'
    movies_df = pd.read_pickle(str(movies_df_file))
    print(f"Loaded {len(movies_df)} movies")

    # Convert movies_df to tagged_corpus
    movies_labels = list(movies_df['film_id'])
    movie_tokens = movies_df['movie_tokens'].tolist()
    tagged_corpus = TaggedLineDocument(movie_tokens, movies_labels)

    print('Training model')
    d2v_model = train_doc2vec(tagged_corpus)

    # Saving the model
    print(f"Saving model version {version}")
    model_filename = f'movies_doc2vec.{version}.model'
    d2v_model.save(str(models_dir / model_filename))

# S3 file handling to be pushed off to the pipeline --------------------------
    # Push model to S3
    # if push:
    #     bucket_name = 'rdso-challenge2'
    #     try:
    #         profile = os.environ['AWS_PROFILE']
    #         session = boto3.Session(profile_name=profile)
    #         s3 = session.client('s3')
    #     except KeyError:
    #         try:
    #             s3 = boto3.client(
    #                 "s3",
    #                 aws_access_key_id=os.environ["AWS_ACCESS_KEY_ID"],
    #                 aws_secret_access_key=os.environ["AWS_SECRET_ACCESS_KEY"],
    #             )
    #         except KeyError:
    #             raise ValueError("No AWS credentials found")
    #
    #     for model_f in models_dir.iterdir():
    #         if model_filename in str(model_f):
    #             with open(str(model_f), 'rb') as outfile:
    #                 s3.upload_fileobj(outfile, bucket_name,
    #                                   'models/' + str(model_f.stem) + str(model_f.suffix))
    #     print(f'Pushed model version {version} to S3')


if __name__ == "__main__":
    cli()
