FROM geocode.igd.fraunhofer.de:4567/jobmanager/jobmanager3:0f0c69da30c4d2cb56321f9db219291fcd9a943a

USER root

RUN apt-get update && \
    apt-get install -y --no-install-recommends build-essential && \
    cd /opt && \
    wget http://montage.ipac.caltech.edu/download/Montage_v6.0.tar.gz && \
    tar xfz Montage_v6.0.tar.gz && \
    rm Montage_v6.0.tar.gz && \
    cd Montage && \
    make && \
    apt-get purge -y build-essential && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

USER jobmanager

COPY conf/plugins/* /jobmanager/conf/plugins/
COPY conf/services/montage.yaml /jobmanager/conf/services/montage.yaml

ENV PATH="/opt/Montage/bin:${PATH}"
