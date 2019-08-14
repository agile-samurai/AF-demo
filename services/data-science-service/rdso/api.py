from fuzzywuzzy import fuzz
import hug
from plot import make_test_image, save_image, jsonify_image

def load_model():
    pass


@hug.post("/most_similar")
def most_similar_movies(imdbID: hug.types.text):
    model = load_model()

    most_similar_movies = [
        {"imdbID": "tt039", "title": "The Social Network", "director": "David Fincher"},
        {"imdbID": "tt049983", "title": "Zodiac", "director": "David Fincher"},
        {"imdbID": "tt04443", "title": "Apollo 13", "director": "Harold Zemeckis"},
    ]
    return most_similar_movies


def make_plot(n):
    p = make_test_image(n=n)
    return jsonify_image(p)


@hug.post("/make_plot")
def show_test_plot(n):
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
    return make_plot(n=400)


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
