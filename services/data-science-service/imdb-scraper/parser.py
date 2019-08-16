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
    bs = BeautifulSoup(html, features='lxml')
    movie = {}
    for script_tag in bs.find_all('script'):
        try:
            if script_tag.attrs['type'] == 'application/ld+json':
                movie = json.loads(script_tag.string)
            else:
                continue
        except KeyError:
            continue

    # Try to find the link to the Amazon Video product page
    all_reviews = []
    for div_tag in bs.find_all('div'):
        if 'data-href' in div_tag.attrs:
            if not div_tag.attrs['data-href'].startswith("/offsite"):
                # Some TV shows don't link to an Amazon product page so skip them
                continue
            amazon_link = 'http://imdb.com' + div_tag.attrs['data-href']

            # Amazon needs to think this is a browser to follow redirects properly
            headers = {'User-agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) '
                                     'AppleWebKit/537.36 (KHTML, like Gecko) '
                                     'Chrome/76.0.3809.100 Safari/537.36'}
            try:
                req = requests.get(amazon_link, headers=headers)
            except ConnectionError:
                print(f"Error loading {amazon_link}")
                continue

            # Get the HTML of the Amazon Video product page
            amazon_html = req.text
            am_bs = BeautifulSoup(amazon_html, features='lxml')

            # Try to find star ratings in the product page
            star_ratings = []
            for i_tag in am_bs.find_all('i'):
                if 'data-hook' in i_tag.attrs and i_tag.attrs['data-hook'] == 'review-star-rating':
                    star_ratings.append([i for i in i_tag.stripped_strings][0][0])

            # Try to find review titles in the product page
            review_titles = []
            for a_tag in am_bs.find_all('a'):
                if 'data-hook' in a_tag.attrs and a_tag.attrs['data-hook'] == 'review-title':
                    review_titles.append([a for a in a_tag.stripped_strings][0])

            # Try to find reviews in the product page
            reviews = []
            for rev_div_tag in am_bs.find_all('div'):
                if 'data-hook' in rev_div_tag.attrs and \
                        rev_div_tag.attrs['data-hook'] == 'review-collapsed':
                    reviews.append([rev for rev in rev_div_tag.stripped_strings][0])

            for star, title, review in zip(star_ratings, review_titles, reviews):
                all_reviews.append({'star_rating': star,
                                    'review_title': title,
                                    'review_text': review})

    movie['reviews'] = all_reviews
    return movie


if __name__ == "__main__":
    scraped_htmls = []
    cwd = pathlib.Path('.').resolve()
    data_dir = cwd.parents[0] / 'data'
    html_dir = data_dir / 'movies_html'
    json_dir = data_dir / 'movies_json'
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
