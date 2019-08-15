import boto3
import pandas as pd
import json
import os
import pathlib
from tqdm import tqdm
# TODO: Resolve local import errors with/without '.' before bucket
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


def get_json_files(n: int = None, s3_folder="data/movies_json", **kwargs) -> list:
    list_of_dicts = []
    print(f"Getting data from {s3_folder}")

    b = Bucket("rdso-challenge2", s3_folder, quiet=True, **kwargs)
    file_list = list(b)[:n]
    print("Done getting s3 file list.")
    for jfile in tqdm(file_list):
        shortened_file = jfile[len(s3_folder) + 1:]
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
    df["imdb_id"] = df.film_id.apply(lambda x: x[2:])

    try:
        df["film_director"] = df.director.apply(lambda x: x["name"])
        df["director_id"] = df.director.apply(lambda x: x["url"].strip("/name/nm"))
    except TypeError as e:
        pass
    # df["director_id"] = df.director.apply(lambda x: x["url"].strip("/name/nm"))
    # df["num_directors"] = df.director.apply(lambda x: len(x["name"]))
    return df


def merged_movie_data(n):
    imdf = imdb_df(get_json_files(n))
    mv = movie_df()
    mdf = imdb.merge(mv, how="left", on="imdb_id")
    return mdf


if __name__ == "__main__":

    movies_df = imdb_df(get_json_files())
    cwd = pathlib.Path('.').resolve()
    data_dir = cwd.parents[0] / 'data'
    movies_df_filename = data_dir / 'movies_df.pkl'
    movies_df.to_pickle(str(movies_df_filename))
    print(f'Saved to {str(movies_df_filename)}')

    # Push movies_df.pkl to S3
    bucket_name = 'rdso-challenge2'
    try:
        profile = os.environ['AWS_PROFILE']
        session = boto3.Session(profile_name=profile)
        s3 = session.client('s3')
    except KeyError:
        try:
            s3 = boto3.client(
                "s3",
                aws_access_key_id=os.environ["AWS_ACCESS_KEY_ID"],
                aws_secret_access_key=os.environ["AWS_SECRET_ACCESS_KEY"],
            )
        except KeyError:
            raise ValueError("No AWS credentials found")

    with open(str(movies_df_filename), 'rb') as outfile:
        s3.upload_fileobj(outfile, bucket_name,
                          'data/' + str(movies_df_filename.stem + '.pkl'))

    print(f'Pushed movies_df.pkl to S3')
