#!/bin/bash
# Script to build projects using build.sh script.
set -eo pipefail

export COMMIT_RANGE="${COMMIT_RANGE}";

for project in "${projects[@]}"
do
  export PROJ_DIR="${project}";
  . .github/workflows/build/build.sh;
done
