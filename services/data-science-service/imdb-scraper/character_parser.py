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
    bs = BeautifulSoup(html, "html.parser")
    characters = []
    table = bs.find("table", {"class": "cast_list"})
    for row in table.find_all("tr")[1:]:
        photo, name, _, character = row.find_all("td")
        photo_img = photo.find("img")["src"]
        actor_id = photo.find("a")["href"]
        character = character.text.strip("\n ")
        name = name.text.strip("\n ")
        cdict = dict(
            zip(
                ("photo_img", "actor_id", "name", "character"),
                (photo_img, actor_id, name, character),
            )
        )
        characters.append(cdict)
        print(cdict)
        # print("photo:", photo.text.strip())
        # print("name:", name.text.strip())
        # print("character:", character.text.strip())


if __name__ == "__main__":
    html = []
    testfile = "/Users/brianaustin/Downloads/tt0069049.html"
    with open(testfile) as f:
        html = f.read()
    parsed_movie = parse_movie(html)

    # scraped_htmls = []
    # cwd = pathlib.Path('.').resolve()
    # data_dir = cwd.parents[0]/'data'
    # html_dir = data_dir / 'movies_html'
    # json_dir = data_dir / 'movies_json'
    # if not json_dir.is_dir():
    #     json_dir.mkdir()
    #
    # file_list = []
    # for html_file in html_dir.iterdir():
    #     if html_file.is_file():
    #         json_file = json_dir / html_file.stem
    #         json_file = json_file.with_suffix('.json')
    #         if not json_file.is_file():
    #             file_list.append(html_file)
    #
    # for readfile in file_list:
    #     with readfile.open('r') as infile:
    #         scraped_htmls.append(infile.read())
    #
    # for movie_html in tqdm(scraped_htmls):
    #     parsed_movie = parse_movie(movie_html)
    #     try:
    #         ttid = parsed_movie['url'].split('/')[2]
    #     except KeyError:
    #         continue
    #
