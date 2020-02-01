#!/bin/bash

cd "$( dirname "${BASH_SOURCE[0]}" )"

set -ex

mkdir -p data
rm -rf data/*
cd data
touch .keep

mHdr "NGC 3372" 2.0 region.hdr

mkdir raw_j
mArchiveList 2mass j "NGC 3372" 2.8 2.8 archive_j.tbl
mCoverageCheck archive_j.tbl remote_j.tbl -header region.hdr
# mArchiveExec -p raw_j remote_j.tbl
cat remote_j.tbl | grep 'http://' | awk '{print $22"\n  dir=raw_j\n  out="$23}' > remote_j.txt
aria2c -i remote_j.txt
mImgtbl raw_j rimages_j.tbl

mkdir raw_h
mArchiveList 2mass h "NGC 3372" 2.8 2.8 archive_h.tbl
mCoverageCheck archive_h.tbl remote_h.tbl -header region.hdr
# mArchiveExec -p raw_h remote_h.tbl
cat remote_h.tbl | grep 'http://' | awk '{print $22"\n  dir=raw_h\n  out="$23}' > remote_h.txt
aria2c -i remote_h.txt
mImgtbl raw_h rimages_h.tbl

mkdir raw_k
mArchiveList 2mass k "NGC 3372" 2.8 2.8 archive_k.tbl
mCoverageCheck archive_k.tbl remote_k.tbl -header region.hdr
# mArchiveExec -p raw_k remote_k.tbl
cat remote_k.tbl | grep 'http://' | awk '{print $22"\n  dir=raw_k\n  out="$23}' > remote_k.txt
aria2c -i remote_k.txt
mImgtbl raw_k rimages_k.tbl
