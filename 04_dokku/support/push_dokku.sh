#!/bin/bash

# Build everything and send it to Dokku
# - This should be run from the root folder of the developer git checkout
# - DIR_DOKKU should be the path to the Dokku-hosted repository for pushing code
# - ENV_BACKEND should be a file that can be sourced before running mvn (e.g. to provide env vars for unit tests)
# - Be sure that the Google Cloud Console has https://quickstart.dokku.cse.lehigh.edu as an Authorized JavaScript Origin
# - Be sure that the Google Cloud Console has https://quickstart.dokku.cse.lehigh.edu/auth/google/callback as an Authorized Redirect URI

# set -x  # un comment this line to get verbose script output
START_DIR="$PWD" && echo "dokku deploy script running from ${START_DIR}"

DIR_DOKKU='../dokku-tutorial'
ENV_BACKEND='./local/backend.env'

[ ! -d backend ] && echo "backend directory does not exist" && exit 1
[ ! -d frontend ] && echo "frontend directory does not exist" && exit 1
[ ! -d "${DIR_DOKKU}" ] && echo "Dokku deployment directory does not exist: '${DIR_DOKKU}'" && exit 1
[ ! -d "${DIR_DOKKU}/.git" ] && echo "Missing expected .git repo in Dokku deployment directory: '${DIR_DOKKU}'/.git" && exit 1
[ ! -f "${ENV_BACKEND}" ] && echo "Missing expected .env file for backend: '${ENV_BACKEND}'" && exit 1

# Build the frontend, copy it into the backend/resources folder
echo "Press <enter> to build frontend, or <ctrl-c> to quit"
read -r x
cd frontend || exit 1
npm run build
npm run deploy
cd ..

echo "Press <enter> to build backend, as a check that the code compiles, or <ctrl-c> to quit"
read -r x
source "${ENV_BACKEND}"
cd backend || exit 1
mvn package
cd ..

echo "Press <enter> to clear deploy folder, or <ctrl-c> to quit"
read -r x
rm -rf "${DIR_DOKKU}/src"
rm -f "${DIR_DOKKU}/pom.xml"
rm -f "${DIR_DOKKU}/Procfile"
rm -f "${DIR_DOKKU}/system.properties"

echo "Press <enter> to copy to deploy folder, or <ctrl-c> to quit"
read -r x
cp -R backend/src "${DIR_DOKKU}"
cp backend/pom.xml "${DIR_DOKKU}"
cp backend/Procfile "${DIR_DOKKU}"
cp backend/system.properties "${DIR_DOKKU}"

echo "Press <enter> to commit deploy folder, or <ctrl-c> to quit"
read -r x
cd "${DIR_DOKKU}" || exit 1
git add .
git commit -m "latest version"

echo "Press <enter> to push to Dokku, or <ctrl-c> to quit"
read -r x
git push
cd "${START_DIR}" || exit 1