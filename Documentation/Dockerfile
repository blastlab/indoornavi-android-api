FROM ubuntu:16.04 AS build

RUN apt-get update \
    && apt-get -y install \
    make \
    python-pip

RUN pip install sphinx sphinx_rtd_theme recommonmark
