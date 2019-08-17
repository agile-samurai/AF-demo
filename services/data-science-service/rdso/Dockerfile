FROM python:3.7


WORKDIR /app

COPY ./requirements.txt /app/requirements.txt
RUN pip install --quiet --upgrade pip && pip install -r requirements.txt

COPY . /app

RUN python -m nltk.downloader stopwords
EXPOSE 8000

CMD hug -f api.py
