FROM jeanblanchard/java:jdk-8u131


EXPOSE 80

ENTRYPOINT ["bin/entrypoint.sh"]
RUN apk add --update bash && rm -rf /var/cache/apk/*

RUN mkdir  /opt/app
WORKDIR /opt/app/

COPY bin/entrypoint.sh /opt/app/bin/entrypoint.sh
COPY app/ /opt/app/
RUN chmod a+x bin/entrypoint.sh
RUN chmod a+x bin/webapp



