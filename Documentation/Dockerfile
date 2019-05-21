FROM ubuntu:16.04 AS build

RUN apt-get update \
    && apt-get -y install \
    make \
    python-pip

RUN pip install sphinx==1.7.6 sphinx_rtd_theme recommonmark
