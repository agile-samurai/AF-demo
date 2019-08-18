import boto3
from fuzzywuzzy import fuzz
from gensim.models.doc2vec import Doc2Vec
import hug
import json
import os
import pandas as pd
import pathlib
import plot
import movies

doc2vec_model = None
movies_df = None
metrics = None

@hug.startup()
def load_movies_df(api):
    """
    Loads the movies_df.pkl file created by movies.py.
    """
    global movies_df
    cwd = pathlib.Path(".").resolve()
    data_dir = cwd.parents[0] /"data"
    if not data_dir.is_dir():
        data_dir = cwd / "data"
        if not data_dir.is_dir():
            data_dir.mkdir()
    movies_df_file = data_dir / "movies_df.pkl"
    movies_df = pd.read_pickle(str(movies_df_file))
    print()


@hug.startup()
def load_metrics(api):
    global metrics
    cwd = pathlib.Path(".").resolve()
    models_dir = cwd / "models"
    latest_metrics = "0.0.0"
    for file in models_dir.iterdir():
        # Find the highest version of the metrics file
        if file.stem.startswith("metrics"):
            metrics_version = file.stem.strip("metrics.")
            if metrics_version > latest_metrics:
                latest_metrics = metrics_version
    # open file
    metrics_file = models_dir / f"metrics.{latest_metrics}.json"
    with metrics_file.open("r") as infile:
        metrics = json.load(infile)

@hug.startup()
def load_model(api):
    """
    Loads the Doc2Vec model file created by vectorize.py.
    """
    global doc2vec_model
    try:
        model_version = os.environ["MODEL_VERSION"]
    except KeyError:
        model_version = "0.0.0"

    model_filename = "movies_doc2vec." + model_version + ".model"
    trainables_filename = model_filename + ".trainables.syn1neg.npy"
    vectors_filename = model_filename + ".wv.vectors.npy"
    cwd = pathlib.Path(".").resolve()
    models_dir = cwd.parents[0] / "models"
    if not models_dir.is_dir():
        models_dir = cwd / "models"
        if not models_dir.is_dir():
            models_dir.mkdir()
    models_file = models_dir / model_filename
    doc2vec_model = Doc2Vec.load(str(models_file))


@hug.get('/most_similar/{imdbID}')
def most_similar_movies(imdbID: str):
    if not imdbID.startswith("tt"):
        imdbID = "tt" + imdbID

    print('this is the id ' + imdbID)
    print(movies_df)

    selected_movie = movies_df[movies_df["film_id"] == imdbID]
    if len(selected_movie) == 0:
        return {"Error": "Movie ID not found in dataset"}

    most_similar = doc2vec_model.docvecs.most_similar(imdbID)

    most_similar_movies = []
    for sim_movie in most_similar:
        sim_id = sim_movie[0]
        movie = movies_df[movies_df["film_id"] == sim_id]
        most_similar_movies.append(movie.to_dict())

    return most_similar_movies

@hug.get("/all_movie_scatter_plot")
def get_all_movie_plot():
    mdf = movies_df  # global, set at startup
    # mdf = movies.merged_movie_data(1000)
    p = plot.sc_plot_genre_colors(mdf)
    return plot.jsonify_image(p)


@hug.post("/highlighted_film_plot")
def get_highlighted_plot(imdbID: hug.types.text):
    mdf = movies.merged_movie_data(1000)
    p = plot.sc_plot_for_one(mdf, imdbID)
    return plot.jsonify_image(p)


@hug.get("/metrics")
def metrics():
    return metrics
