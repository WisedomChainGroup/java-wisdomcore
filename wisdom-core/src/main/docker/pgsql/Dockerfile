FROM postgres:10
COPY ddl.sql /tmp
COPY init.sh /docker-entrypoint-initdb.d/

ENTRYPOINT ["usr/bin/env", "bash", "docker-entrypoint.sh"]
CMD ["postgres"]

EXPOSE 5432
