FROM amazon/aws-lambda-java:latest

COPY build/libs/jprof_by_bot-eval-evaluator-all.jar ${LAMBDA_TASK_ROOT}/lib/

RUN yum -y install unzip tar gzip xz && \
    yum -y clean all  && \
    rm -rf /var/cache

RUN curl -L https://github.com/JetBrains/kotlin/releases/download/v1.5.31/kotlin-compiler-1.5.31.zip --output /tmp/kotlin.zip --silent && \
    unzip /tmp/kotlin.zip -d /tmp && \
	mv /tmp/kotlinc /opt/kotlin && \
    rm /tmp/kotlin.zip

RUN curl -L https://download.java.net/java/GA/jdk17/0d483333a00540d886896bac774ff48b/35/GPL/openjdk-17_linux-x64_bin.tar.gz --output /tmp/java.tar.gz --silent && \
    mkdir /opt/java && \
    tar -xf /tmp/java.tar.gz -C /opt/java --strip-components 1 && \
    rm /tmp/java.tar.gz

RUN curl -L https://nodejs.org/dist/v16.11.0/node-v16.11.0-linux-x64.tar.xz --output /tmp/node.tar.xz --silent && \
    mkdir /opt/node && \
    tar -xf /tmp/node.tar.xz -C /opt/node --strip-components 1 && \
    rm /tmp/node.tar.xz

ENV PATH="/opt/node/bin:${PATH}"
RUN npm install -g typescript@4.4.3

RUN curl -L https://www.python.org/ftp/python/3.10.0/Python-3.10.0.tar.xz --output /tmp/python.tar.xz --silent && \
    mkdir /opt/python && \
    tar -xf /tmp/python.tar.xz -C /opt/python --strip-components 1 && \
    rm /tmp/python.tar.xz

RUN curl -L https://golang.org/dl/go1.17.2.linux-amd64.tar.gz --output /tmp/go.tar.gz --silent && \
    mkdir /opt/go && \
    tar -xf /tmp/go.tar.gz -C /opt/go --strip-components 1 && \
    rm /tmp/go.tar.gz

CMD [ "by.jprof.telegram.bot.eval.evaluator.Evaluator::handleRequest" ]
