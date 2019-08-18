import hug
from rdso import api


# def test_similarity_score():
#     comparison_string = "Hello World."
#     strings = [
#         {"id": 1, "body": "Hello Wrrld."},
#         {"id": 2, "body": "H3ll0 W0rld#"},
#         {"id": 3, "body": "Heck no dog."},
#     ]
#
#     for string in strings:
#         response = hug.test.post(
#             api,
#             "similarity_score",
#             {"comparison_text": comparison_string, "text": string["body"]},
#         )
#         assert response.status == "200 OK"
#         score = response.data
#         assert isinstance(score, float)
#         assert 0 <= score <= 1


def test_metrics():
    metrics = hug.test.get(api, "metrics")
    assert metrics.status == "200 OK"


def test_get_all_available_movies():
    mv = hug.test.get(api, "all_available_movies", params={"n": 6})
    assert mv.status == "200 OK"
    available_movies = mv.data
    assert len(available_movies) == 6
