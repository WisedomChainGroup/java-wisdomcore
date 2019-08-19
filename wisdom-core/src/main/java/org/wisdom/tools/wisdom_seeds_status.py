#!/usr/bin/env python3

from urllib import request
import json

SEEDS = [
    '120.76.101.153',
    '47.74.183.249',
    '47.74.216.251',
    '47.96.67.155',
    '47.74.86.106',
    '47.56.67.236'
]

for h in SEEDS:
    try:
        resp = json.loads(
            request.urlopen('http://%s:19585/consensus/status' % h).read()
        )
        resp2 = json.loads(
            request.urlopen('http://%s:19585/version' % h).read()
        )
        print('%s\t%s version= %s' % (h, resp['currentHeight'], resp2['data']['version']))
    except Exception:
        print('cannot connect to %s' % h)