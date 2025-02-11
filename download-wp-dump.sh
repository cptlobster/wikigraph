#!/bin/bash
# Mirror URL. Make sure that the mirror supports HTTPS.
WPDUMP_MIRROR="dumps.wikimedia.org"
# Wiki name. Check the mirror's directory tree for this
WPDUMP_WIKI="enwiki"
# Date target, usually in format "YYYYMMDD" or "latest"
WPDUMP_DATE="20250201"
# relative path on host machine; this will default to "wp-data" but you can change it if you have multiple xmldumps
TARGET_PATH="wp-data"
# xmldump to download and unzip
WPDUMP_SOURCE="articlesmultistreamdump"
# base url for all queries
WPDUMP_BASE_URL="https://$WPDUMP_MIRROR/$WPDUMP_WIKI/$WPDUMP_DATE"

# get metadata
FILES=($(curl "$WPDUMP_BASE_URL/dumpstatus.json" | jq -r ".jobs.$WPDUMP_SOURCE.files | keys | .[] | @sh"))

echo $FILES

cd $TARGET_PATH

# this function will be called by xargs to run two downloads at the same time
function download() {
  FILE=$1
  TRFILE=$(echo $FILE | tr -d "'")
  # if the file exists already, skip it
  if [ -f $TRFILE ]; then
    return
  fi
  # otherwise, download the file
  echo $TRFILE
  curl "$WPDUMP_BASE_URL/$TRFILE" -o "$TRFILE"
}

# get files
echo $FILE | xargs -n1 -P2 download

# get MD5 checksums and ensure all files are correct
curl "$WPDUMP_BASE_URL/$WPDUMP_WIKI-$WPDUMP_DATE-md5sums.txt" -o "sums.md5"
md5sums -c sums.md5 --ignore-missing
