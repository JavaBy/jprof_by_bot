ARG JAVA=17

FROM openjdk:${JAVA}

COPY build/install/* /opt/herald
RUN microdnf install findutils

ENTRYPOINT ["/opt/herald/bin/herald"]
