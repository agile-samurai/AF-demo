from bs4 import BeautifulSoup
import json
import pathlib
from tqdm import tqdm


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
    cwd = pathlib.Path('.').resolve()
    data_dir = cwd.parents[0] / 'data'
    html_dir = data_dir / 'imdb_html'
    json_dir = data_dir / 'imdb_json'
    if not json_dir.is_dir():
        json_dir.mkdir()

    file_list = []
    for html_file in html_dir.iterdir():
        if html_file.is_file():
            json_file = json_dir / html_file.stem
            json_file = json_file.with_suffix('.json')
            if not json_file.is_file():
                file_list.append(html_file)

    for readfile in file_list:
        with readfile.open('r') as infile:
            scraped_htmls.append(infile.read())

    for movie_html in tqdm(scraped_htmls):
        parsed_movie = parse_movie(movie_html)
        try:
            ttid = parsed_movie['url'].split('/')[2]
        except KeyError:
            continue

        # Objective 2 date requirements:
        try:
            release_date = parsed_movie['datePublished']
            if "2009-06-01" <= release_date <= "2019-06-01":
                writefile = json_dir / f'{ttid}.json'
                with writefile.open('w') as outfile:
                    json.dump(parsed_movie, outfile, indent=2)
        except KeyError:
            continue
