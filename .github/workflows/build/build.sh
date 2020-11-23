#!/bin/bash
# Script to build project if changes occur that contain project dir or build script.
set -eo pipefail

CHANGES="$(git --no-pager diff --name-only "${COMMIT_RANGE}")";
echo "Commit range: ${COMMIT_RANGE}; Project dir: ${PROJ_DIR};\n Changes: ${CHANGES}";
if [[ -n "$(grep -E "(${PROJ_DIR}|\.github\/workflows)" <<< "${CHANGES}")" ]]; then
  echo "Building for ${PROJ_DIR}";
  pushd "$PROJ_DIR";
  ./gradlew build;
  popd;
fi
