import pandas as pd
import json
from tqdm import tqdm
from bucket import Bucket


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


def get_json_files(n: int = None, s3_folder="data/imdb_json", **kwargs) -> list:
    list_of_dicts = []
    print(f"Getting data from {s3_folder}")

    b = Bucket("rdso-challenge2", s3_folder, quiet=True, **kwargs)
    file_list = list(b)[:n]
    print("Done getting s3 file list.")
    for jfile in tqdm(file_list):
        shortened_file = jfile[len(s3_folder) + 1 :]
        f = b[str(shortened_file)]
        json_string = f.read()
        movie_dict = json.loads(json_string)
        list_of_dicts.append(movie_dict)
    return list_of_dicts


def translate_duration(x):
    x = x.strip("PT")
    if "H" in x:
        hours, mins = x.split("H")
    else:
        hours = 0
        mins = x
    if "M" in x:
        mins, _ = mins.split("M")
    else:
        mins = 0
    total_time = int(hours) * 60 + int(mins)
    return total_time


def imdb_df(data):
    df = pd.DataFrame(data)
    df.drop(["@context", "@type"], axis=1, inplace=True)
    df["film_id"] = df.url.apply(lambda x: "tt" + x.strip("/title/"))
    df["total_min"] = df.duration.apply(
        lambda x: translate_duration(x) if type(x) == str else x
    )
    try:
        df["film_director"] = df.director.apply(lambda x: x["name"])
        df["director_id"] = df.director.apply(lambda x: x["url"].strip("/name/nm"))
    except TypeError as e:
        pass
    # df["director_id"] = df.director.apply(lambda x: x["url"].strip("/name/nm"))
    # df["num_directors"] = df.director.apply(lambda x: len(x["name"]))
    return df


if __name__ == "__main__":

    imdb = imdb_df(get_json_files(50))
