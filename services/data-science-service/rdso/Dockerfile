FROM python:3.7

WORKDIR /app


COPY ./requirements.txt /app/requirements.txt
RUN pipenv pip install --upgrade pip && pip install -r requirements.txt

COPY . /app
RUN mkdir data
RUN mkdir models

RUN python -m nltk.downloader stopwords
EXPOSE 8000

#ENTRYPOINT gunicorn --workers=2 -k gevent --bind 0.0.0.0:8000 --timeout 360 --log-level=info --preload 'api:__hug_wsgi__'
ENTRYPOINT hug -f api.py