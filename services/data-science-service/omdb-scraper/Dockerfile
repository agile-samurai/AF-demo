FROM python:3.7

COPY . /app
WORKDIR /app

# OMDB_API_KEY must be exported at runtime for scraper.py to work
RUN pip install --upgrade pip && pip install -r requirements.txt
RUN ./setup.sh && python scraper.py
RUN ./push_to_s3.sh

