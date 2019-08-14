import json
import os
import pathlib
import pandas as pd
import requests
from tqdm import tqdm


def scrape_movie(movie_id: str):
    """
    Scrapes the OMDB api for a given movie_id

    :param movie_id: 7 digit number that represents IMDB id from URL
    :return: dict of OMDB movie data
    """
    omdb_api_key = os.environ['OMDB_API_KEY']
    base_url = f'http://www.omdbapi.com/?apikey={omdb_api_key}&plot=full&i='
    imdb_id = f'tt{movie_id}'
    r = requests.get(f'{base_url}{imdb_id}')
    if r.status_code == 200:
        return json.loads(r.text)
    else:
        return


def extract_title_year(title: str):
    """
    Helper function to extract the year from the end of the movie title string

    :param title: Expects title string in format "Movie Title (YYYY)"
    :return: 4 digit year
    """
    if isinstance(title, str) and title[-5:-1].isdigit():
        return int(title[-5:-1])
    else:
        return 0


def remove_year_from_title(title):
    """
    Helper function to remove the year from the movie title

    :param title: Expects title string in format "Movie Title (YYYY)"
    :return: Title string in format "Movie Title"
    """
    if title.endswith(')'):
        return title[:-7]
    else:
        return title


def split_genre(genre):
    """
    Helper function to take a string of pipe-delimited genres and split into list
    :param genre: String in format "genre1|genre2|...|genreN"
    :return: List in format ['genre1', 'genre2', ..., 'genreN']
    """
    if not pd.isna(genre):
        return genre.split('|')
    else:
        return []


if __name__ == '__main__':

    json_dir = pathlib.Path('data', 'omdb_json')
    if not json_dir.is_dir():
        json_dir.mkdir()
    # Read MovieTweetings data file
    movie_tweets_data = pd.read_csv("https://raw.githubusercontent.com/sidooms/"
                                    "MovieTweetings/master/latest/movies.dat", sep='::',
                                    names=['imdb_id', 'title', 'genres'],
                                    dtype={'imdb_id': str},
                                    engine='python')

    movie_tweets_data['year'] = movie_tweets_data['title'].apply(extract_title_year)
    movie_tweets_data['title'] = movie_tweets_data['title'].apply(remove_year_from_title)
    movie_tweets_data['genres'] = movie_tweets_data['genres'].map(split_genre)

    movies_to_scrape = movie_tweets_data[movie_tweets_data['year'] > 2008]

    for index, movie_row in tqdm(movies_to_scrape.iterrows(), total=len(movies_to_scrape)):
        if len(movie_row['imdb_id']) != 7:
            continue
        else:
            ttid = 'tt' + movie_row['imdb_id']
            writefile = json_dir / f'{ttid}.json'
            if not writefile.is_file():
                movie_json = scrape_movie(movie_row['imdb_id'])
                with writefile.open('w') as outfile:
                    json.dump(movie_json, outfile, indent=2)
