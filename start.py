#!/usr/bin/env python3

# require
# pip install -U "python-dotenv[cli]"

# usage python start.py

import os
import argparse
from dotenv import load_dotenv

parser = argparse.ArgumentParser(description='start wisdom core from env file')
parser.add_argument('-e', '--env', default="local.env", type=str, help="env file path")
args = vars(parser.parse_args())
load_dotenv(dotenv_path=args['env'])

if os.name == 'nt':
    os.system(".\gradlew.bat run")
else:
    os.system("./gradlew run")





