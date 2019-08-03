FROM geocode.igd.fraunhofer.de:4567/jobmanager/jobmanager3:2c0b8ca1e695de1b0510f1f59ec5066cc798702a

USER root

COPY Montage-v6.0_mAdd.patch /opt/Montage-v6.0_mAdd.patch

RUN apt-get update && \
    apt-get install -y --no-install-recommends build-essential && \
    cd /opt && \
    wget http://montage.ipac.caltech.edu/download/Montage_v6.0.tar.gz && \
    tar xfz Montage_v6.0.tar.gz && \
    rm Montage_v6.0.tar.gz && \
    cd Montage && \
    patch -p 0 < ../Montage-v6.0_mAdd.patch && \
    make && \
    apt-get purge -y build-essential && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

USER jobmanager

COPY conf/plugins/* /jobmanager/conf/plugins/
COPY conf/services/montage.yaml /jobmanager/conf/services/montage.yaml

ENV PATH="/opt/Montage/bin:${PATH}"
