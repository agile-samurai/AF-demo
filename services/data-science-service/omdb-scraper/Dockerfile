FROM python:3.7

COPY . /app
RUN mkdir data
WORKDIR /app

# OMDB_API_KEY must be exported at runtime for scraper.py to work
RUN pip install --upgrade pip && pip install -r requirements.txt
ENTRYPOINT python scraper.py

