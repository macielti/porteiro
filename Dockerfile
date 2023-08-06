FROM alpine

COPY . /usr/src/app

WORKDIR /usr/src/app

RUN apk add clojure

RUN apk add leiningen

RUN lein deps

CMD ["lein", "run"]
