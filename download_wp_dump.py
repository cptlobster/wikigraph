#!/bin/python
from urllib import request
import json

WPDUMP_MIRROR = "dumps.wikimedia.org"
WPDUMP_WIKI = "enwiki"
WPDUMP_DATE = "20240401"
TARGET_PATH = "wp_dump"


def get_wd_url(path: str = "",
               mirror: str = None,
               wiki: str = None,
               date: str = None) -> str:
    mirror = mirror or WPDUMP_MIRROR
    wiki = wiki or WPDUMP_WIKI
    date = date or WPDUMP_DATE
    return f"https://{mirror}/{wiki}/{date}/{path}"


def get_metadata() -> dict:
    md_url = get_wd_url("dumpstatus.json")
    with request.urlopen(md_url) as f:
        raw_data = f.read().decode('utf-8')

    metadata = json.loads(raw_data)
    return metadata
