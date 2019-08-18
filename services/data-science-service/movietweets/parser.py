import pandas as pd
import pathlib


def convert_data(datafile):
    cols = ["user_id", "item_id", "rating", "scraping_time"]
    rating_data = pd.DataFrame(columns=cols)
    tweets = []
    with open(datafile, "r") as f:
        for row in f.readlines()[1:]:
            user, item, rating, time, *tweet = row.strip("\n").split(",")
            info = [user, item, rating, time]
            rating_data = rating_data.append(dict(zip(cols, info)), ignore_index=True)
            tweets.append("".join(tweet))

    # multiplied the actual number by a billion to get date/time that looked right ¯\_(ツ)_/¯
    rating_data["timestamp"] = pd.to_datetime(
        rating_data["scraping_time"].astype("int"), unit="s"
    )
    return rating_data, tweets


if __name__ == "__main__":
    cwd = pathlib.Path(".").resolve()
    data_dir = cwd.parents[0] / "data"
    html_dir = data_dir / "movies_html"

    rd, tweets = convert_data("tweets/recsys_challenge_2014_dataset/training.dat")
