import pandas as pd
import pathlib
import json


def convert_data(datafile) -> pd.DataFrame:
    """Converts training data into a tidy dataframe, with each rating tweet
    as a separate line in the data. Extracts only the user_id, imdb_id, rating,
    scraping time, and the twitter id of the tweet.

    Parameters
    ----------
    datafile :
        Training data from the recsys challenge dataset

    Returns
    -------
    pd.DataFrame

    """
    cols = ["user_id", "imdb_id", "rating", "scraping_time", "tweet_id"]
    rating_data = pd.DataFrame(columns=cols)
    tweets = []
    with open(datafile, "r") as f:
        for row in f.readlines()[1:]:
            user, item, rating, time, *tweet = row.strip("\n").strip("{}").split(",")
            tweet_id = tweet[4].split(":")[1].strip()
            info = [user, item, rating, time, tweet_id]
            rating_data = rating_data.append(dict(zip(cols, info)), ignore_index=True)

    rating_data["timestamp"] = pd.to_datetime(
        rating_data["scraping_time"].astype("int"), unit="s"
    )
    return rating_data


def convert_to_json(df: pd.DataFrame) -> dict:
    """Generates a grouped DataFrame where each of the imdb_ids is associated with
    a list of the tweets about that film. This is then converted to a dictionary object
    suitable for a dump to json.

    Parameters
    ----------
    df : pd.DataFrame
        The pandas DataFrame producd by convert_data

    Returns
    -------
    dict

    """
    gdf = pd.DataFrame(rd.groupby("imdb_id")["tweet_id"].apply(list)).reset_index()
    return gdf.to_dict(orient="records")


def convert_with_ratings_avg(df: pd.DataFrame) -> dict:
    """Generates a grouped DataFrame where each of the imdb_ids is associated with
    a list of the tweets about that film, as well as the average rating for each
    movie over the entire dataset. This is then converted to a dictionary object
    suitable for a dump to json.

    Parameters
    ----------
    df : pd.DataFrame
        The pandas DataFrame producd by convert_data

    Returns
    -------
    dict

    """
    gdf = pd.DataFrame(
        [
            rd.groupby("imdb_id")["rating"].mean(),
            rd.groupby("imdb_id")["tweet_id"].apply(list),
        ],
        index=["avg_rating", "tweet_id"],
    ).T.reset_index()
    return gdf.to_dict(orient="records")


if __name__ == "__main__":
    cwd = pathlib.Path(".").resolve()
    data_dir = cwd.parents[0] / "data/tweets/recsys_challenge_2014_dataset"
    data_file = data_dir / "training.dat"
    df = convert_data(data_file)
    tweets = convert_to_json(df=df)
    with open("tweet_data.json", "w") as f:
        json.dump(data, f)
