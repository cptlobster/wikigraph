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
FILES=$(curl "$WPDUMP_BASE_URL/dumpstatus.json" | jq -r ".jobs.$WPDUMP_SOURCE.files | keys | .[]")

cd $TARGET_PATH

# get files
for FILE in "${FILES[@]}"; do
  curl -o "$FILE" "$WPDUMP_BASE_URL/$FILE"

# get MD5 checksums and ensure all files are correct
curl -o "sums.md5" "$WPDUMP_BASE_URL/$WPDUMP_WIKI-$WPDUMP_DATE-md5sums.txt"
md5sums -c sums.md5 --ignore-missing