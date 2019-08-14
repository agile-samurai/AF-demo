FROM python:3.7

COPY . /app
# TODO: Figure out how to get push_to_s3 script into this folder
RUN mkdir data
WORKDIR /app

# OMDB_API_KEY must be exported at runtime for scraper.py to work
RUN pip install --upgrade pip && pip install -r requirements.txt
RUN python scraper.py
RUN ./push_to_s3.sh

