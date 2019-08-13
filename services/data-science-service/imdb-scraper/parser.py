from bs4 import BeautifulSoup
from tqdm import tqdm
import json
import pathlib


def parse_movie(html: str):
    """
    Parses HTML string and extracts movie JSON

    :param html: String of IMDB movie page HTML
    :return: dict of movie JSON
    """
    bs = BeautifulSoup(html, features='lxml')
    tag = 'script'
    movie = {}
    for script_tag in bs.find_all(tag):
        try:
            if script_tag.attrs['type'] == 'application/ld+json':
                movie = json.loads(script_tag.string)
            else:
                continue
        except KeyError:
            continue

    return movie


if __name__ == "__main__":
    scraped_htmls = []
    html_dir = pathlib.Path('data', 'movie_html')
    json_dir = pathlib.Path('data', 'movie_json')

    file_list = []
    for x in html_dir.iterdir():
        if x.is_file():
            file_list.append(x)

    for readfile in file_list:
        with readfile.open('r') as infile:
            scraped_htmls.append(infile.read())

    for movie_html in tqdm(scraped_htmls):
        parsed_movie = parse_movie(movie_html)
        ttid = parsed_movie['url'].split('/')[2]

        # Objective 2 date requirements:
        release_date = parsed_movie['datePublished']
        if "2009-06-01" <= release_date <= "2019-06-01":
            writefile = json_dir / f'{ttid}.json'
            with writefile.open('w') as outfile:
                json.dump(parsed_movie, outfile, indent=2)
