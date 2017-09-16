FROM jeanblanchard/java:jdk-8u131


EXPOSE 80

RUN mkdir  /opt/app
WORKDIR /opt/app/

ENTRYPOINT ["bin/entrypoint.sh"]


COPY repo/ /opt/app/repo/
COPY bin/ /opt/app/bin/
RUN chmod a+x bin/entrypoint.sh
RUN ln -s /opt/jdk/bin/java bin/usr-compounder-java
COPY libs/ /opt/app/repo/
