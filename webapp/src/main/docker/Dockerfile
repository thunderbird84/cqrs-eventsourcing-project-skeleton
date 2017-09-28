FROM indexing/jvm


EXPOSE 80

ENTRYPOINT ["bin/entrypoint.sh"]

RUN mkdir  /opt/app
WORKDIR /opt/app/

COPY bin/entrypoint.sh /opt/app/bin/entrypoint.sh
COPY app/ /opt/app/
RUN chmod a+x bin/entrypoint.sh
RUN chmod a+x bin/webapp



