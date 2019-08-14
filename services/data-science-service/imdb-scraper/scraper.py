import pandas as pd
import pathlib
import requests
from tqdm import tqdm


def scrape_movie(movie_id: str):
    """
    Scrapes the IMDB movie page for a given movie_id

    :param movie_id: 7 digit number that represents IMDB id from URL
    :return: str of page HTML
    """
    url = 'https://www.imdb.com/title/tt' + movie_id + '/'
    r = requests.get(url)
    return r.text


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
    cwd = pathlib.Path('.').resolve()
    data_dir = cwd.parents[0] / 'data'
    html_dir = data_dir / 'imdb_html'
    if not html_dir.is_dir():
        html_dir.mkdir()

    # Read MovieTweetings data file
    movie_tweets_file = data_dir / 'movies.dat'
    movie_tweets_data = pd.read_csv(str(movie_tweets_file), sep='::',
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
            writefile = html_dir / f'{ttid}.html'
            if not writefile.is_file():
                movie_html = scrape_movie(movie_row['imdb_id'])
                with writefile.open('w') as outfile:
                    outfile.write(movie_html)
