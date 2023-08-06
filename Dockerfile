FROM clojure

COPY . /usr/src/app

WORKDIR /usr/src/app

RUN apt-get -y update

RUN lein deps

CMD ["lein", "run"]
