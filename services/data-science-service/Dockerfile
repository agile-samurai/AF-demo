FROM ubuntu:latest
RUN apt-get update && apt-get -y upgrade
RUN apt-get install -y build-essential python3.6 python3-pip python3-dev
RUN pip3 -q install pip --upgrade

RUN mkdir data-science-service
WORKDIR data-science-service

COPY . .

RUN pip3 install -r rdso/requirements.txt
RUN pip3 install .
RUN pip3 install awscli --upgrade
ARG AWS_ACCESS_KEY_ID
ARG AWS_SECRET_ACCESS_KEY
RUN aws s3 cp s3://ugroup-rdso-challenge-data/data/omdb_json data/omdb_json --recursive

RUN ls -la data
RUN cp -r data/omdb_json omdb_json
RUN mkdir /data
RUN mkdir models

RUN locale -a
ENV LC_ALL C.UTF-8
ENV LANG C.UTF-8

RUN python3 -m nltk.downloader stopwords
RUN python3 rdso/movies.py
RUN cp /data/movies_df.pkl data/movies_df.pkl
RUN python3 rdso/vectorize.py
RUN pip3 -q install jupyter

# Add Tini. Tini operates as a process subreaper for jupyter. This prevents
# kernel crashes.
ENV TINI_VERSION v0.6.0
ADD https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini /usr/bin/tini
RUN chmod +x /usr/bin/tini
ENTRYPOINT ["/usr/bin/tini", "--"]

CMD ["jupyter", "notebook", "--port=8888", "--no-browser", "--ip=0.0.0.0", "--allow-root"]
