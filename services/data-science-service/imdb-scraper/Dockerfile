FROM python:3.7

COPY . /app
# TODO: Figure out how to get push_to_s3 script into this folder
# COPY push_to_s3 /app
RUN mkdir data
WORKDIR /app
RUN pip install --upgrade pip && pip install -r requirements.txt
RUN python scraper.py
RUN python parser.py
RUN ./push_to_s3.sh
