#!/bin/bash
# Script to build projects within java and kotlin subdirectories matching $REGEX variable,
#  using build.sh script.

set -eo pipefail

export COMMIT_RANGE="${COMMIT_RANGE}";

export projects=()

while IFS=' ' read -r -d $'\0'; do
  proj_dir=$(dirname "${REPLY}");
  (echo "${proj_dir}" | grep -E "(kotlin|java)" | grep -E "${REGEX}" )  \
   && projects+=("${proj_dir}") && echo "Building ${proj_dir}"
done < <(find . -maxdepth 4 -mindepth 4 -name "build.gradle"  -print0)

. .github/workflows/build/build_all.sh;
