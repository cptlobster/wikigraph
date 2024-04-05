#!/bin/python
"""Download a wiki from its xmldump source. Source is configurable by parameters to target a different wiki, date, or
mirror.
"""
from urllib import request
import json

# Mirror URL. Make sure that the mirror supports HTTPS.
WPDUMP_MIRROR = "dumps.wikimedia.org"
# Wiki name. Check the mirror's directory tree for this
WPDUMP_WIKI = "enwiki"
# Date target, usually in format "YYYYMMDD" or "latest"
WPDUMP_DATE = "latest"
# relative path on host machine; this will default to "wp_dump" but you can change it if you have multiple xmldumps
TARGET_PATH = "wp_dump"


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


def get_metadata() -> dict:
    """Get the metadata JSON from the xmldump as a Python dict.

    :returns: A dictionary containing the xmldump metadata."""
    md_url = get_wd_url("dumpstatus.json")
    with request.urlopen(md_url) as f:
        raw_data = f.read().decode('utf-8')

    metadata = json.loads(raw_data)
    return metadata
