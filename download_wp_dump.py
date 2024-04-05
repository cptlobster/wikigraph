#!/bin/python
"""Download a wiki from its xmldump source. Source is configurable by parameters to target a different wiki, date, or
mirror.
"""
import requests as req
import json

from urllib import request
from bz2 import BZ2Decompressor
from rich.progress import Progress
from timeit import default_timer as timer
from datetime import timedelta

# Mirror URL. Make sure that the mirror supports HTTPS.
WPDUMP_MIRROR = "dumps.wikimedia.org"
# Wiki name. Check the mirror's directory tree for this
WPDUMP_WIKI = "enwiki"
# Date target, usually in format "YYYYMMDD" or "latest"
WPDUMP_DATE = "20240401"
# relative path on host machine; this will default to "wp-data" but you can change it if you have multiple xmldumps
TARGET_PATH = "wp-data"
# xmldump to download and unzip
WPDUMP_SOURCE = "articlesmultistreamdump"
# Default chunk size (bytes)
CHUNK_SIZE = 100 * 1024


def sizeof_fmt(num, suffix="B"):
    for unit in ("", "Ki", "Mi", "Gi", "Ti", "Pi", "Ei", "Zi"):
        if abs(num) < 1024.0:
            return f"{num:3.1f}{unit}{suffix}"
        num /= 1024.0
    return f"{num:.1f}Yi{suffix}"


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
                       date: str = None,
                       progress: Progress = None) -> (int, int):
    """Download and unzip a file automatically.

    :param path: The relative path of the file to download
    :returns: True if the file successfully downloads"""
    mirror = mirror or WPDUMP_MIRROR
    wiki = wiki or WPDUMP_WIKI
    date = date or WPDUMP_DATE
    url = get_wd_url(path, mirror, wiki, date)
    dc = BZ2Decompressor()
    leftover = b""
    dc_length = 0
    with req.get(url, stream=True) as response:
        length = int(response.headers['Content-Length'])
        if progress:
            current_dl = progress.add_task(f"Downloading {sizeof_fmt(length)}...", total=length)
        with open(f"{TARGET_PATH}/{path.removesuffix('.bz2')}", "wb") as f:
            for chunk in response.iter_content(chunk_size=CHUNK_SIZE):
                decompressed = dc.decompress(leftover + chunk)
                f.write(decompressed)
                dc_length += len(decompressed)
                leftover = b""
                if progress:
                    progress.update(current_dl, advance=CHUNK_SIZE)
                if dc.eof:
                    leftover = dc.unused_data
                    # you should see that 'leftover' is the start of a new stream
                    # we have to start a new decompressor
                    dc = BZ2Decompressor()
    progress.console.print(f"Downloaded {sizeof_fmt(length)} ({sizeof_fmt(dc_length)} uncompressed)")
    return (length, dc_length)


def get_files_in_dump(metadata: dict, dump: str = None) -> list:
    """Get the list of files in a specific xmldump"""
    dump = dump or WPDUMP_SOURCE
    return metadata["jobs"][dump]["files"].keys()


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
    with Progress(transient=True) as progress:
        md = get_metadata()
        files = get_files_in_dump(md)
        overall_task = progress.add_task(f"Downloading {len(files)} files...", total=len(files))
        overall_bytes = 0
        overall_dc_bytes = 0
        start = timer()
        for n, path in enumerate(files, start=1):
            progress.console.print(f"({n} / {len(files)}) File {path}")
            nb, ndb = download_and_unzip(path, progress=progress)
            overall_bytes += nb
            overall_dc_bytes += ndb
            progress.update(overall_task, advance=1)
        end = timer()
        progress.console.print(f"Download completed.\n\n"
                               f"DOWNLOAD STATISTICS\n"
                               f"Size (compressed): {sizeof_fmt(overall_bytes)}\n"
                               f"Size (decompressed): {sizeof_fmt(overall_dc_bytes)}"
                               f"Average file size (compressed): {sizeof_fmt(overall_bytes / len(files))}\n"
                               f"Average file size (decompressed): {sizeof_fmt(overall_dc_bytes / len(files))}\n"
                               f"Total time elapsed: {str(timedelta(seconds=end - start))}\n"
                               f"Average time per file: {str(timedelta(seconds=(end - start) / len(files)))}")
