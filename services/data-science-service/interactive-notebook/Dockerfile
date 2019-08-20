FROM ubuntu:latest
RUN apt-get update && apt-get -y upgrade
RUN apt-get install -y build-essential python3.6 python3-pip python3-dev
RUN pip3 -q install pip --upgrade

RUN mkdir data-science-service
WORKDIR data-science-service
COPY data data
COPY models models
COPY rdso rdso
COPY setup.py .
RUN ls -al
RUN pip3 install .
WORKDIR rdso
RUN pip3 install -r requirements.txt
RUN pip3 -q install jupyter

RUN locale -a
ENV LC_ALL C.UTF-8
ENV LANG C.UTF-8
RUN python3 vectorize.py

RUN mkdir /notebooks

ENTRYPOINT ["jupyter", "notebook", "--ip=*"]
# CMD jupyter notebook --no-browser --ip 0.0.0.0 --port 8888 /notebooks

# RUN pip install -q --upgrade pip && pip -q install -r rdso/requirements.txt
# RUN rdso/movies.py && rdso/vectorize.py
#
# RUN pip install -q jupyter, ipykernel
# RUN pip install rdso
