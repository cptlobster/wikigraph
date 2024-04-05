#!/bin/python
"""Download a wiki from its xmldump source. Source is configurable by parameters to target a different wiki, date, or
mirror.
"""
from urllib import request
import requests
import json
import bz2

# Mirror URL. Make sure that the mirror supports HTTPS.
WPDUMP_MIRROR = "dumps.wikimedia.org"
# Wiki name. Check the mirror's directory tree for this
WPDUMP_WIKI = "enwiki"
# Date target, usually in format "YYYYMMDD" or "latest"
WPDUMP_DATE = "20240401"
# relative path on host machine; this will default to "wp_dump" but you can change it if you have multiple xmldumps
TARGET_PATH = "wp_dump"
# xmldump to download and unzip
WPDUMP_SOURCE = "articlesmultistreamdump"


def get_wd_url(path: str = "",
               mirror: str = None,
               wiki: str = None,
               date: str = None) -> str:
    """Assemble the URL for the targeted entry in the xml dump.

    :param path: The relative filepath from the xmldump root
    :param mirror: The target wikimedia xmldump mirror. Defaults to dumps.wikimedia.org
    :param wiki: The target wiki. Defaults to English Wikipedia (enwiki)
    :param date: The date string of the dump. Defaults to latest."""
    mirror = mirror or WPDUMP_MIRROR
    wiki = wiki or WPDUMP_WIKI
    date = date or WPDUMP_DATE
    return f"https://{mirror}/{wiki}/{date}/{path}"


def get_metadata(mirror: str = None,
                 wiki: str = None,
                 date: str = None) -> dict:
    """Get the metadata JSON from the xmldump as a Python dict.

    :returns: A dictionary containing the xmldump metadata."""
    mirror = mirror or WPDUMP_MIRROR
    wiki = wiki or WPDUMP_WIKI
    date = date or WPDUMP_DATE
    md_url = get_wd_url("dumpstatus.json", mirror, wiki, date)
    with request.urlopen(md_url) as f:
        raw_data = f.read().decode('utf-8')

    metadata = json.loads(raw_data)
    return metadata


def download_and_unzip(path: str,
                       mirror: str = None,
                       wiki: str = None,
                       date: str = None) -> bool:
    """Download and unzip a file automatically.

    :param path: The relative path of the file to download
    :returns: True if the file successfully downloads"""
    mirror = mirror or WPDUMP_MIRROR
    wiki = wiki or WPDUMP_WIKI
    date = date or WPDUMP_DATE
    url = get_wd_url(path, mirror, wiki, date)
    dc = bz2.BZ2Decompressor()
    with requests.get(url, stream=True) as response:
        with open(f"{TARGET_PATH}/{path.removesuffix('.bz2')}", "wb") as f:
            for chunk in response.iter_content(chunk_size=100 * 1024):
                f.write(dc.decompress(chunk))
    return True


def get_files_in_dump(metadata: dict, dump: str = None) -> list:
    """Get the list of files in a specific xmldump"""
    dump = dump or WPDUMP_SOURCE
    return metadata["jobs"][dump]["files"].keys


def download_dump(dump: str = None,
                  mirror: str = None,
                  wiki: str = None,
                  date: str = None) -> bool:
    dump = dump or WPDUMP_SOURCE
    mirror = mirror or WPDUMP_MIRROR
    wiki = wiki or WPDUMP_WIKI
    date = date or WPDUMP_DATE
    md = get_metadata()
    success = True
    for path in get_files_in_dump(md, dump):
        success = success and download_and_unzip(path, mirror, wiki, date)
    return success

if __name__ == "__main__":
    print(f"Downloading dump \"{WPDUMP_SOURCE}\" from \"https://{WPDUMP_MIRROR}/{WPDUMP_WIKI}/{WPDUMP_DATE}\"")
    print(f"Decompressing and saving files in {TARGET_PATH}")
    if download_dump():
        print("Complete.")
    else:
        print("An error occurred. See logs for details.")
