#!/bin/bash

cd "$( dirname "${BASH_SOURCE[0]}" )"

rm -rf data
mkdir data
cd data
touch .keep

mHdr "NGC 3372" 2.0 region.hdr

mkdir raw_j
mArchiveList 2mass j "NGC 3372" 2.8 2.8 archive_j.tbl
mCoverageCheck archive_j.tbl remote_j.tbl -header region.hdr
mArchiveExec -p raw_j remote_j.tbl
mImgtbl raw_j rimages_j.tbl

mkdir raw_h
mArchiveList 2mass h "NGC 3372" 2.8 2.8 archive_h.tbl
mCoverageCheck archive_h.tbl remote_h.tbl -header region.hdr
mArchiveExec -p raw_h remote_h.tbl
mImgtbl raw_h rimages_h.tbl

mkdir raw_k
mArchiveList 2mass k "NGC 3372" 2.8 2.8 archive_k.tbl
mCoverageCheck archive_k.tbl remote_k.tbl -header region.hdr
mArchiveExec -p raw_k remote_k.tbl
mImgtbl raw_k rimages_k.tbl
