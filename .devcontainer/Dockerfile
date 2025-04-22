.# Usa uma imagem base com Java
FROM ubuntu:latest

# Instala dependências
RUN apt update && apt install -y openjdk-11-jdk wget unzip

# Instala o JMeter
RUN wget https://downloads.apache.org/jmeter/binaries/apache-jmeter-5.5.tgz \
    && tar -xzf apache-jmeter-5.5.tgz \
    && mv apache-jmeter-5.5 /opt/jmeter

# Define variáveis de ambiente
ENV JMETER_HOME=/opt/jmeter
ENV PATH="$JMETER_HOME/bin:$PATH"

# Define o diretório de trabalho
WORKDIR $JMETER_HOME