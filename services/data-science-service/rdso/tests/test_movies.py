from rdso.movies import secondary_genre, load_movietweetings_df
from ..movies import translate_duration
import requests
import pandas as pd
import pytest


def test_secondary_genre():
    assert secondary_genre("Action|Adventure".split("|")[1:]) == "Adventure"
    assert secondary_genre("Action".split("|")[1:]) == []
    assert secondary_genre("Action|Adventure|Comedy|Drama".split("|")[1:]) == [
        "Adventure",
        "Comedy",
        "Drama",
    ]


def test_access_to_movietweetings():
    res = requests.get(
        "https://raw.githubusercontent.com/sidooms/MovieTweetings/master/latest/movies.dat"
    )
    assert res.status_code == requests.codes.ok
    mv = load_movietweetings_df()
    assert type(mv) == pd.DataFrame


def test_translate_duration():
    assert translate_duration("PT2H22M") == 142
    assert translate_duration("PT2H") == 120
    assert translate_duration("PT23M") == 23
