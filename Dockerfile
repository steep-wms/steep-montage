FROM geocode.igd.fraunhofer.de:4567/jobmanager/jobmanager3:5c33aec076474f184369db9ec8b96f36a38d6e94

COPY conf/rules.yaml /jobmanager/conf/rules.yaml
COPY conf/service_metadata.yaml /jobmanager/conf/service_metadata_montage.yaml
COPY src /opt/montage-helpers/

USER root

RUN cat /jobmanager/conf/service_metadata_montage.yaml >> /jobmanager/conf/service_metadata.yaml && \
    echo "deb http://http.us.debian.org/debian/ testing non-free contrib main" >> /etc/apt/sources.list && \
    apt-get update && \
    curl -sL https://deb.nodesource.com/setup_12.x | bash - && \
    apt-get install -y --no-install-recommends build-essential nodejs g++-8 && \
    cd /opt && \
    wget http://montage.ipac.caltech.edu/download/Montage_v6.0.tar.gz && \
    tar xfz Montage_v6.0.tar.gz && \
    rm Montage_v6.0.tar.gz && \
    cd Montage && \
    make && \
    cd /opt/montage-helpers && \
    make && \
    npm i && \
    apt-get purge -y build-essential && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

USER jobmanager

ENV PATH="/opt/Montage/bin:/opt/montage-helpers:${PATH}"
