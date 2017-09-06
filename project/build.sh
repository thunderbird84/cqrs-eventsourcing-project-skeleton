#!/usr/bin/env bash

set -o xtrace
set -o errexit
set -o nounset
set -o pipefail

echo "Building project using sbt"
date
sbt clean test dockerPack
date
