#!/bin/bash

usage() {
  echo "download_band.sh <SURVEY> <BAND> <OBJECT|LOCATION> <WIDTH> <HEIGHT> <region.hdr> <OUTPUT DIRECTORY>"
}

if [ $# -lt 7 ]; then
  usage
  exit 1
fi

SURVEY=$1
BAND=$2
LOCATION=$3
WIDTH=$4
HEIGHT=$5
REGIONHDR=$6
OUTDIR=$7

REGIONPATH="$(cd "$(dirname "$6")"; pwd -P)"

[ "$(ls -A "$OUTDIR")" ] && echo "Output directory is not empty. Skipping download." && exit

set -ex

mkdir -p $OUTDIR
mArchiveList $SURVEY $BAND "$LOCATION" $WIDTH $HEIGHT $REGIONPATH/archive_$BAND.tbl
mCoverageCheck $REGIONPATH/archive_$BAND.tbl $REGIONPATH/remote_$BAND.tbl -header $REGIONHDR
# mArchiveExec -p $OUTDIR $REGIONPATH/remote_$BAND.tbl
cat $REGIONPATH/remote_$BAND.tbl | grep 'http://' | awk "{print \$22\"\n  dir=$OUTDIR\n  out=\"\$23}" > $REGIONPATH/remote_$BAND.txt
aria2c -i $REGIONPATH/remote_$BAND.txt
mImgtbl $OUTDIR $REGIONPATH/rimages_$BAND.tbl
