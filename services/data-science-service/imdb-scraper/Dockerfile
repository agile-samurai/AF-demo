FROM python:3.7

COPY . /app
COPY ../push_to_s3.sh /app
RUN mkdir data
WORKDIR /app
RUN pip install --upgrade pip && pip install -r requirements.txt && ./setup.sh
RUN python scraper.py
RUN python parser.py
RUN ./push_to_s3.sh
