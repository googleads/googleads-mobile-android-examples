#!/bin/bash
# Script to build projects within java and kotlin subdirectories matching
# $REGEX variable.  Assumes all projects are under java/ or kotlin/ and
# two subdirectories deep from there (eg admob/BannerExample).
# Checks $COMMIT_RANGE to ensure changes are relevant to each project.

set -eo pipefail

projects=()

while IFS=' ' read -r -d $'\0'; do
  proj_dir=$(dirname "${REPLY}");
  (echo "${proj_dir}" | grep -E "(kotlin|java)" | grep -E "${REGEX}" )  \
   && projects+=("${proj_dir#./}")
done < <(find . -maxdepth 4 -mindepth 4 -name "build.gradle"  -print0)

CHANGES="$(git --no-pager diff --name-only "${COMMIT_RANGE}")";
echo "Commit range: ${COMMIT_RANGE}; \n Changes: ${CHANGES}";

for PROJ_DIR in "${projects[@]}"
do
  echo "Checking ${PROJ_DIR}"
  if [[ -n "$(grep -E "(${PROJ_DIR}|\.github\/workflows)" <<< "${CHANGES}")" ]]; then
    echo "Building for ${PROJ_DIR}";
    pushd "$PROJ_DIR";
    ./gradlew build --no-daemon;
    popd;
  fi
done
