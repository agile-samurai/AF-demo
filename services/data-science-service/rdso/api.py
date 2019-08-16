import boto3
from fuzzywuzzy import fuzz
from gensim.models.doc2vec import Doc2Vec
import hug
import os
import pandas as pd
import pathlib
import plot
import movies

doc2vec_model = None
movies_df = None


@hug.startup()
def load_movies_df(api):
    """
    Loads the movies_df.pkl file created by movies.py.
    """
    global movies_df
    cwd = pathlib.Path('.').resolve()
    data_dir = cwd.parents[0] / 'data'
    if not data_dir.is_dir():
        data_dir.mkdir()
    movies_df_file = data_dir / 'movies_df.pkl'

# S3 file handling to be pushed off to the pipeline --------------------------
    # If movies_df.pkl is not in the data directory, download it from S3
    # if not movies_df_file.is_file():
    #
    #     bucket_name = 'rdso-challenge2'
    #     s3 = None
    #     try:
    #         profile = os.environ['AWS_PROFILE']
    #         session = boto3.Session(profile_name=profile)
    #         s3 = session.client('s3')
    #     except KeyError:
    #         pass
    #     if s3 is None:
    #         try:
    #             access_key_id = os.environ["AWS_ACCESS_KEY_ID"]
    #             access_key = os.environ["AWS_SECRET_ACCESS_KEY"]
    #             session = boto3.Session(aws_access_key_id=access_key_id,
    #                                     aws_secret_access_key=access_key)
    #             s3 = session.client('s3')
    #         except KeyError:
    #             raise ValueError("No AWS credentials found")
    #
    #     with open(str(movies_df_file), 'wb') as infile:
    #         s3.download_fileobj(bucket_name, 'data/movies_df.pkl', infile)
    #     print('Downloaded movies_df.pkl from S3')

    movies_df = pd.read_pickle(str(movies_df_file))


@hug.startup()
def load_model(api):
    """
    Loads the Doc2Vec model file created by vectorize.py.
    """
    global doc2vec_model
    try:
        model_version = os.environ['MODEL_VERSION']
    except KeyError:
        model_version = '0.0.0'

    model_filename = 'movies_doc2vec.' + model_version + '.model'
    trainables_filename = model_filename + '.trainables.syn1neg.npy'
    vectors_filename = model_filename + '.wv.vectors.npy'
    cwd = pathlib.Path('.').resolve()
    models_dir = cwd.parents[0] / 'models'
    if not models_dir.is_dir():
        models_dir.mkdir()
    models_file = models_dir / model_filename

# S3 file handling to be pushed off to the pipeline --------------------------
#     models_trainables_file = models_dir / trainables_filename
#     models_vectors_file = models_dir / vectors_filename
#     if not models_file.is_file():
#         bucket_name = 'rdso-challenge2'
#         s3 = None
#         try:
#             profile = os.environ['AWS_PROFILE']
#             session = boto3.Session(profile_name=profile)
#             s3 = session.client('s3')
#         except KeyError:
#             pass
#         if s3 is None:
#             try:
#                 access_key_id = os.environ["AWS_ACCESS_KEY_ID"]
#                 access_key = os.environ["AWS_SECRET_ACCESS_KEY"]
#                 session = boto3.Session(aws_access_key_id=access_key_id,
#                                         aws_secret_access_key=access_key)
#                 s3 = session.client('s3')
#             except KeyError:
#                 raise ValueError("No AWS credentials found")
#
#         with open(str(models_file), 'wb') as inf:
#             s3.download_fileobj(bucket_name, 'models/' + model_filename, inf)
#         with open(str(models_vectors_file), 'wb') as inf:
#             s3.download_fileobj(bucket_name, 'models/' + vectors_filename, inf)
#         with open(str(models_trainables_file), 'wb') as inf:
#             s3.download_fileobj(bucket_name, 'models/' + trainables_filename, inf)
#         print('Downloaded Doc2Vec model from S3')

    doc2vec_model = Doc2Vec.load(str(models_file))


@hug.post("/most_similar")
def most_similar_movies(imdbID: hug.types.text):
    if not imdbID.startswith('tt'):
        imdbID = 'tt' + imdbID
    selected_movie = movies_df[movies_df['film_id'] == imdbID]
    if len(selected_movie) == 0:
        return {'Error': 'Movie ID not found in dataset'}

    most_similar = doc2vec_model.docvecs.most_similar(imdbID)

    most_similar_movies = []
    for sim_movie in most_similar:
        sim_id = sim_movie[0]
        movie = movies_df[movies_df['film_id'] == sim_id]
        most_similar_movies.append(movie.to_dict())

    return most_similar_movies


@hug.post("/make_test_plot")
def show_test_plot(n=None):
    """Creates test plot with anything posted

    Parameters
    ----------
    n : int
        Dummy parameter that should be an integer

    Returns
    -------
    dict
        Returns a JSON dict that contains a test plot; should be paired with
        BokehJS library and React component on the front-end
    """
    if not n:
        n = 500
    p = plot.make_test_image(n=n)
    return plot.jsonify_image(p)


@hug.get("/all_movie_scatter_plot")
def get_all_movie_plot():
    mdf = movies.merged_movie_data(1000)
    p = plot.sc_plot_genre_colors(mdf)
    return plot.jsonify_image(p)


@hug.post("/highlighted_film_plot")
def get_highlighted_plot(imdbID: hug.types.text):
    mdf = movies.merged_movie_data(1000)
    p = plot.sc_plot_for_one(mdf, imdbID)
    return plot.jsonify_image(p)


def compare(primary_string, secondary_string):
    """
    Helper method with the scoring logic for the /similarity_score endpoint.
    """
    fuzz_score = fuzz.ratio(primary_string, secondary_string) / 100
    return fuzz_score


@hug.post("/similarity_score")
def similarity_score(comparison_text: hug.types.text, text: hug.types.text):
    """
    Score the comparison_string for similarity to the others. Can be used for
    comparing company names, sentences, etc. Scale is 0 to 1 (1 is perfect match).

    Parameters
    ----------
    comparison_text:
        String to compare to the other
    text:
        String to compare with

    Returns
    -------
    output_score:
        float from 0 to 1

    """
    output_score = compare(comparison_text, text)
    return output_score


@hug.get("/metrics")
def metrics():
    """
    Endpoint to get model scores in order to track performance over time.

    Currently has dummy values.
    """
    return {"Model 1": 10, "Model 2": 100, "Model 3": 1000}


if __name__ == "__main__":
    """
    Demonstrate use of api as a module.
    """
    comparison_string = "Hello World."
    strings = [
        {"id": 1, "body": "Hello Wrrld."},
        {"id": 2, "body": "H3ll0 W0rld#"},
        {"id": 3, "body": "Heck no dog."},
    ]

    print(f"Similarity to '{comparison_string}':")
    for string in strings:
        score = similarity_score(comparison_string, string["body"])
        print(f"id {string['id']}, '{string['body']}': {score}")
