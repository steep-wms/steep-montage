FROM steep/steep:6.0.0

USER root

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        build-essential gcc-9 g++-9 aria2 git && \
    update-alternatives --install /usr/bin/gcc gcc /usr/bin/gcc-9 90 \
        --slave /usr/bin/g++ g++ /usr/bin/g++-9 \
        --slave /usr/bin/gcov gcov /usr/bin/gcov-9 && \
    cd /opt && \
    git clone https://github.com/Caltech-IPAC/Montage.git && \
    cd Montage && \
    git reset --hard dffcaf683961e92c10c4912f1ef35babb209a113 && \
    make && \
    apt-get purge -y build-essential gcc-9 g++-9 git && \
    apt-get autoremove -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN mkdir /download
COPY scripts /download/scripts

USER steep

COPY conf/plugins/* /steep/conf/plugins/
COPY conf/services/montage.yaml /steep/conf/services/montage.yaml

ENV JAVA_OPTS="-Xmx8192m -Xms1024m -Dvertx.disableDnsResolver=true"
ENV PATH="/opt/Montage/bin:${PATH}"
ENV STEEP_LOGS_PROCESSCHAINS_ENABLED=true
