import pandas as pd
import json
from utils.bucket import Bucket


def secondary_genre(x):
    if pd.isna(x).all():
        return x
    if len(x) == 0:
        return pd.np.nan
    if len(x) > 1:
        return x
    elif len(x) == 1:
        return x[0]


def movie_df():
    mv = pd.read_csv(
        "https://raw.githubusercontent.com/sidooms/MovieTweetings/master/latest/movies.dat",
        sep="::",
        names=["imdb_id", "title", "genres"],
        dtype={"imdb_id": str},
    )
    mv["year"] = mv.title.apply(lambda x: x[-5:-1])
    mv["parsed_title"] = mv.title.apply(lambda x: x[:-6])
    mv["primary_genre"] = mv.genres.apply(
        lambda x: x.split("|")[0] if type(x) == str else x
    )
    mv["secondary_genre"] = mv.genres.apply(
        lambda x: secondary_genre(x.split("|")[1:]) if type(x) == str else x
    )
    return mv


def get_json_files():
    pass


def imdb_df(data):
    return pd.DataFrame.from_dict(json.loads(data))


if __name__ == "__main__":
    imdb = imdb_df()
