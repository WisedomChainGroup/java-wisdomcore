#!/usr/bin/env python3

# require
# pip3 install -U "python-dotenv[cli]"

import os

os.system("dotenv -f ./rest.env run ./gradlew run")




