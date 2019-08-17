FROM python:3.7

COPY . /app
RUN mkdir data
WORKDIR /app
RUN pip install --upgrade pip && pip install -r requirements.txt
ENTRYPOINT python scraper.py && python parser.py
