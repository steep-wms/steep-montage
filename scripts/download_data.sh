#!/bin/bash

DIRNAME="$( dirname "${BASH_SOURCE[0]}" )"
cd "$DIRNAME/.."

set -ex

mkdir -p data
rm -rf data/*
cd data
touch .keep

SURVEY=2mass
LOCATION="NGC 3372"
HDRWIDTH=2.0
WIDTH=2.2
HEIGHT=2.2

mHdr "$LOCATION" $HDRWIDTH region.hdr

$DIRNAME/download_band.sh $SURVEY j "$LOCATION" $WIDTH $HEIGHT region.hdr raw_j
$DIRNAME/download_band.sh $SURVEY h "$LOCATION" $WIDTH $HEIGHT region.hdr raw_h
$DIRNAME/download_band.sh $SURVEY k "$LOCATION" $WIDTH $HEIGHT region.hdr raw_k
