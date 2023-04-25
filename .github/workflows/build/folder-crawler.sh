#!/bin/bash

set -eo pipefail

# If you are changing the expected_projects_count value, you likely want to be
# changing .github/workflows.build.yml to modify which samples are built
# through GitHub actions.
expected_projects_count=32
projects=0

while IFS=' ' read -r -d $'\0'; do
  proj_dir=$(dirname "${REPLY}");
  echo "Project directory: $proj_dir"
  projects=$((projects+1))
done < <(find . -maxdepth 4 -mindepth 4 -name "build.gradle"  -print0)

echo "There are ${projects} Android projects."
echo "There are expected to be ${expected_projects_count} Android projects
configured for GitHub actions."

if [[ ${projects} != "$expected_projects_count" ]]; then
  echo "Project count does not match the expected project count. Make sure all
  samples are building through GitHub actions. See
  https://g3doc.corp.google.com/company/teams/ads-devrel/mobile-ads/enable-github-actions-on-new-android-samples.md
  for more details"
  exit 1
else
  echo "Project count matches the expected project count."
  exit 0
fi
