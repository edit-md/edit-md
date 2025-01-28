#!/bin/bash

mkdir -p helm/files/db

# copy the db init scripts to the helm chart
cp -r ./dev/db ./helm/files