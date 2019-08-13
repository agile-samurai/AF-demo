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


def get_json_files(n: int = None, s3_folder="data/movie_json") -> list:
    list_of_dicts = []
    # mv = movie_df()
    b = Bucket("rdso-challenge2", s3_folder)
    file_list = list(b)[:n]
    for jfile in file_list:
        shortened_file = jfile[len(s3_folder) + 1 :]
        f = b[str(shortened_file)]
        json_string = f.read()
        movie_dict = json.loads(json_string)
        list_of_dicts.append(movie_dict)
    return list_of_dicts


def imdb_df(data):
    return pd.DataFrame(data)


if __name__ == "__main__":

    imdb = imdb_df(get_json_files(50))
