FROM jeanblanchard/java:jdk-8u131


EXPOSE 80

WORKDIR /opt/app/

ENTRYPOINT ["bin/entrypoint.sh"]


COPY repo/ /opt/app/repo/
COPY bin/ /opt/app/bin/
RUN chmod a+x bin/entrypoint.sh
RUN ln -s /opt/jdk/bin/java bin/usr-service-query-java
COPY libs/ /opt/app/repo/
