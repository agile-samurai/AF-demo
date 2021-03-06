from bs4 import BeautifulSoup
import json
import pathlib
import requests
from tqdm import tqdm


def parse_movie(html: str):
    """
    Parses HTML string and extracts movie JSON

    :param html: String of IMDB movie page HTML
    :return: dict of movie JSON
    """
    bs = BeautifulSoup(html, features="lxml")
    movie = {}
    for script_tag in bs.find_all("script"):
        try:
            if script_tag.attrs["type"] == "application/ld+json":
                movie = json.loads(script_tag.string)
            else:
                continue
        except KeyError:
            continue

    bs = BeautifulSoup(html, "html.parser")
    try:
        movie["longer_desc"] = bs.find("meta", {"property": "og:description"})[
            "content"
        ]
    except TypeError:
        movie["longer_desc"] = ""
        pass
    movie["short_desc"] = movie.get("description", "")
    movie["description"] = "\n".join([movie["longer_desc"], movie["short_desc"]])

    return movie


if __name__ == "__main__":
    scraped_htmls = []
    cwd = pathlib.Path(".").resolve()
    data_dir = cwd.parents[0] / "data"
    html_dir = data_dir / "movies_html"
    json_dir = data_dir / "longer_desc_json"
    if not json_dir.is_dir():
        json_dir.mkdir()

    file_list = []  # list of tuples
    for html_file in html_dir.iterdir():
        if html_file.is_file():
            film_id = html_file.stem
            json_file = json_dir / html_file.stem
            json_file = json_file.with_suffix(".json")
            if not json_file.is_file():
                file_list.append((film_id, html_file))

    for film_id, readfile in file_list:
        with readfile.open("r") as infile:
            scraped_htmls.append((film_id, infile.read()))

    for film_id, movie_html in tqdm(scraped_htmls):
        parsed_movie = parse_movie(movie_html)
        if len(parsed_movie) > 0:
            writefile = json_dir / f"{film_id}.json"
            with writefile.open("w") as outfile:
                json.dump(parsed_movie, outfile, indent=2)
        else:
            continue
