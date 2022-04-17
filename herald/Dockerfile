ARG JAVA=17

FROM openjdk:${JAVA}

COPY build/install/* /opt/herald

ENTRYPOINT ["/opt/herald/bin/herald"]
