FROM postgres:10
COPY ddl.sql /tmp
COPY init.sh /docker-entrypoint-initdb.d/

ENTRYPOINT ["bash", "docker-entrypoint.sh"]
CMD ["postgres"]

EXPOSE 5432
