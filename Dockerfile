FROM steep/steep:5.0.1

USER root

COPY Montage-v6.0_mAdd.patch /opt/Montage-v6.0_mAdd.patch

RUN apt-get update && \
    apt-get install -y --no-install-recommends build-essential aria2 && \
    cd /opt && \
    wget http://montage.ipac.caltech.edu/download/Montage_v6.0.tar.gz && \
    tar xfz Montage_v6.0.tar.gz && \
    rm Montage_v6.0.tar.gz && \
    cd Montage && \
    patch -p 0 < ../Montage-v6.0_mAdd.patch && \
    make && \
    apt-get purge -y build-essential && \
    apt-get autoremove -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN mkdir /download
COPY download_data.sh /download

USER steep

COPY conf/plugins/* /steep/conf/plugins/
COPY conf/services/montage.yaml /steep/conf/services/montage.yaml

ENV JAVA_OPTS="-Xmx8192m -Xms1024m -Dvertx.disableDnsResolver=true"
ENV PATH="/opt/Montage/bin:${PATH}"
