from fuzzywuzzy import fuzz
from gensim.models.doc2vec import Doc2Vec
import hug
import os
import pathlib
import plot
import movies


doc2vec_model = None


@hug.startup
def load_model():
    global doc2vec_model

    try:
        model_version = os.environ['MODEL_VERSION']
    except KeyError:
        model_version = '0.0.0'

    filename = 'movies_doc2vec.' + model_version + '.model'
    cwd = pathlib.Path('.').resolve()
    models_dir = cwd.parents[0] / 'models'
    models_file = models_dir / filename
    doc2vec_model = Doc2Vec.load(str(models_file))


@hug.post("/infer_vectors")
def infer_vectors(doc: hug.types.text):
    return doc2vec_model.infer_vector(doc)


@hug.post("/most_similar")
def most_similar_movies(imdbID: hug.types.text):
    most_similar_movies = [
        {"imdbID": "tt039", "title": "The Social Network", "director": "David Fincher"},
        {"imdbID": "tt049983", "title": "Zodiac", "director": "David Fincher"},
        {"imdbID": "tt04443", "title": "Apollo 13", "director": "Harold Zemeckis"},
    ]
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
