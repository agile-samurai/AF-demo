# The following imports are only needed for S3:
import boto3
import os


import click
from gensim.models.doc2vec import Doc2Vec, TaggedDocument
import json
import multiprocessing
import numpy as np
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
    model = Doc2Vec(
        vector_size=300,
        window=10,
        min_count=0,
        alpha=0.025,
        min_alpha=0.025,
        workers=cores,
        epochs=20,
    )
    model.build_vocab(corpus)
    model.train(corpus, epochs=model.epochs, total_examples=model.corpus_count)
    return model


def get_genre_distance_metrics(d2v_model, movies_df):
    """
    Takes the doc vectors and clusters them by genre, then finds the centroid of each cluster.
    Uses this to find the average distance and st. deviation of distance for each genre.

    :return: dict of shape {cluster_name: {mean: X, stdev: Y}}
    """
    # movies_df = movies_df[movies_df["top_genre"].notnull()]
    genres = movies_df["top_genre"].unique()
    centroids = {}
    distance_metrics = {}
    for genre in genres:
        # Get all the vectors for the genre
        genre_movies_list = movies_df[movies_df["top_genre"] == genre][
            "film_id"
        ].tolist()
        vectors_list = [d2v_model.docvecs[film_id] for film_id in genre_movies_list]

        # Find the genre centroid
        centroid = np.mean(vectors_list, axis=0)
        centroids[genre] = centroid

        # Find the distance mean and standard deviation within the genre
        distances = []
        for vector in vectors_list:
            distances.append(np.linalg.norm(vector - centroid))
        distance_average = np.mean(distances)
        distance_stdev = np.std(distances)
        distance_metrics[genre] = {
            "mean": distance_average.item(),
            "stdev": distance_stdev.item(),
        }

    return distance_metrics


@click.command()
@click.option(
    "--version", "-v", default="0.0.0", help="Version number to use in model filename"
)
# @click.option('--local', '-l', is_flag=True, help="Load local data files instead of from S3")
# @click.option('--push', '-p', is_flag=True, help="Push model to S3 when finished training")
def cli(version):
    """
    Main block with CLI functionality
    """

    # Load movies_df from .pkl file
    cwd = pathlib.Path(".").resolve()
    data_dir = cwd / "data"
    models_dir = cwd / "models"
    movies_df_file = data_dir / "movies_df.pkl"
    movies_df = pd.read_pickle(str(movies_df_file))
    print(f"Loaded {len(movies_df)} movies")

    # Convert movies_df to tagged_corpus
    movies_labels = list(movies_df["film_id"])
    movie_tokens = movies_df["movie_tokens"].tolist()
    tagged_corpus = TaggedLineDocument(movie_tokens, movies_labels)

    print("Training model")
    d2v_model = train_doc2vec(tagged_corpus)

    # Saving the model
    print(f"Saving model version {version}")
    model_filename = f"movies_doc2vec.{version}.model"
    d2v_model.save(str(models_dir / model_filename))

    # For testing, loading d2v model from memory

    # Find genre centroids and use them to compute distance metrics for model performance
    genre_metrics = get_genre_distance_metrics(d2v_model, movies_df)
    genre_metrics["model_version"] = version
    metrics_file = models_dir / f"metrics.{version}.json"
    with metrics_file.open("w") as outfile:
        json.dump(genre_metrics, outfile, indent=2)

if __name__ == "__main__":
    cli()
