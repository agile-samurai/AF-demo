# The following imports are only needed for S3:
import boto3
from bucket import Bucket
import os


"""
This script reads JSON files from ../data/movies_json into a pandas DataFrame,
parses the data, and performs text cleaning/preprocessing. The resulting DataFrame
is saved to ../data/movies_df.pkl
"""
import pandas as pd
import json
import pathlib
from tqdm import tqdm
from nltk import RegexpTokenizer
from nltk.corpus import stopwords


def secondary_genre(x):
    if pd.isna(x).all():
        return x
    if len(x) == 0:
        return pd.np.nan
    if len(x) > 1:
        return x
    elif len(x) == 1:
        return x[0]


def load_movietweetings_df():
    """
    Load the data from the MovieTweetings repo to a DataFrame and perform basic cleaning
    :return: pandas DataFrame
    """
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


def load_json_files(n: int = None, folder="../data/movies_json") -> list:
    """
    Read all the JSON files in folder to a list of dicts
    :param n: Maximum number of files to load
    :param folder: Location of files to load
    :return: list of dicts of JSON contents
    """
    list_of_dicts = []
    json_path = pathlib.Path(folder).resolve()
    print(f"Loading JSON files from {folder}")
    for json_file in tqdm(json_path.iterdir()):
        if json_file.is_file() and json_file.suffix == '.json':
            with json_file.open('r') as infile:
                json_string = infile.read()
            movie_dict = json.loads(json_string)
            list_of_dicts.append(movie_dict)
        if n is not None and len(list_of_dicts) > n:
            break
    return list_of_dicts


def translate_duration(duration):
    """
    Convert duration string from hours & minutes to just minutes
    """
    duration = duration.strip("PT")
    if "H" in duration:
        hours, mins = duration.split("H")
    else:
        hours = 0
        mins = duration
    if "M" in duration:
        mins, _ = mins.split("M")
    else:
        mins = 0
    total_time = int(hours) * 60 + int(mins)
    return total_time


def get_top_genre(genre):
    """
    Takes a genre string or list of strings
    Returns just the top (first) genre.
    """
    if isinstance(genre, list):
        return genre[0]
    elif isinstance(genre, str):
        return genre
    else:
        return ''


def nlp_clean(doc: str):
    """
    Do all text cleaning to one document
    :param doc: A string of text
    :return: list of cleaned tokens
    """
    tokenizer = RegexpTokenizer(r'\w+')
    stopword_set = set(stopwords.words('english'))

    token_list = tokenizer.tokenize(doc.lower())
    token_list = list(set(token_list).difference(stopword_set))
    return token_list


def process_movie_list_to_df(data):
    """
    Do all of the parsing/cleaning/preprocessing required to get movie data ready for
    machine learning
    :param data: list of dicts of movie JSON data
    :return: pandas DataFrame of cleaned movie data
    """
    movies_df = pd.DataFrame(data)
    # Convert IMDB url to ID number
    movies_df["film_id"] = movies_df.url.apply(lambda x: "tt" + x.strip("/title/"))
    # Convert hours-minutes to minutes
    movies_df["total_min"] = movies_df.duration.apply(
        lambda x: translate_duration(x) if type(x) == str else x
    )
    # Convert list of genres to one top genre
    movies_df['top_genre'] = movies_df['genre'].apply(get_top_genre)
    # Convert ID number to IMDB ID
    movies_df["imdb_id"] = movies_df.film_id.apply(lambda x: x[2:])
    # Drop movies that don't have a description or a genre
    movies_df.dropna(subset=['description', 'genre'], inplace=True)
    # Replace NaN with empty string for keywords
    movies_df['keywords'].fillna(value='', inplace=True)
    # Concat descriptions & keywords into movie_text field
    movies_df['movie_text'] = list(movies_df['description'] + ' ' + movies_df['keywords'])
    movies_df['movie_tokens'] = movies_df['movie_text'].apply(nlp_clean)
    movies_df.drop(["@context", "@type"], axis=1, inplace=True)

    try:
        movies_df["film_director"] = movies_df.director.apply(lambda x: x["name"])
        movies_df["director_id"] = movies_df.director.apply(lambda x: x["url"].strip("/name/nm"))
    except TypeError:
        pass
    # movies_df["director_id"] = movies_df.director.apply(lambda x: x["url"].strip("/name/nm"))
    # movies_df["num_directors"] = movies_df.director.apply(lambda x: len(x["name"]))
    return movies_df


# def merged_movie_data(n):
#     imdf = imdb_df(get_json_files(n))
#     mv = movie_df()
#     mdf = imdb.merge(mv, how="left", on="imdb_id")
#     return mdf


if __name__ == "__main__":
    # Read all movie JSON files
    movies_json_list = load_json_files()
    print(f"Loaded {len(movies_json_list)} JSON files")
    # Convert list of JSON data to cleaned DataFrame
    movies_dataframe = process_movie_list_to_df(movies_json_list)

    # Save movies_df to local .pkl file
    cwd = pathlib.Path('.').resolve()
    data_dir = cwd.parents[0] / 'data'
    movies_df_filename = data_dir / 'movies_df.pkl'
    movies_dataframe.to_pickle(str(movies_df_filename))
    print(f'Saved to {str(movies_df_filename)}')

# S3 file handling to be pushed off to the pipeline --------------------------
    # Push movies_df.pkl to S3
    # bucket_name = 'rdso-challenge2'
    # try:
    #     profile = os.environ['AWS_PROFILE']
    #     session = boto3.Session(profile_name=profile)
    #     s3 = session.client('s3')
    # except KeyError:
    #     try:
    #         s3 = boto3.client(
    #             "s3",
    #             aws_access_key_id=os.environ["AWS_ACCESS_KEY_ID"],
    #             aws_secret_access_key=os.environ["AWS_SECRET_ACCESS_KEY"],
    #         )
    #     except KeyError:
    #         raise ValueError("No AWS credentials found")
    #
    # with open(str(movies_df_filename), 'rb') as outfile:
    #     s3.upload_fileobj(outfile, bucket_name,
    #                       'data/' + str(movies_df_filename.stem + '.pkl'))
    #
    # print(f'Pushed movies_df.pkl to S3')
