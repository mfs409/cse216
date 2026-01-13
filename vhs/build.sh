#!/usr/bin/bash

# This script uses the `vhs` tool to create gifs of the terminal
#
# I installed it using `go install github.com/charmbracelet/vhs@latest`
VHS=~/go/bin/vhs

# This script uses the `gifsicle` tool to optimize gifs
GIFSICLE=gifsicle
GFLAGS=-O3

# Note: The VHS files depend on a helper script called 'run_docker.sh'.  It
# is responsible for setting up a container instance, so that path names and
# user names are sanitized
#
# There is a Dockerfile you can use to set up your container.  It needs some
# care so that the uids are correct.
#
# The script for running a container can be a one-liner like this:
#   docker run -it --rm --mount type=bind,source=/home/username/tmp,target=/home/me/cse216 -p3000:5000 cse216
if [ ! -f "./run_docker.sh" ]; then
    echo "Cannot find run_docker.sh. Aborting." >&2
    exit 1
fi

# Note: In chapters 2 and 3, a database file is needed.  This will make it
# for the "Prof. Parse" user that appears in the tutorials.
cd ../01_databases/admin
rm -f test.db
DB_FILE=test.db mvn package
rm test.db
echo "C" > admin.cmd
echo "+p" >> admin.cmd
echo "mfs409@lehigh.edu" >> admin.cmd
echo "Prof. Parse" >> admin.cmd
echo "q" >> admin.cmd
rm -f ../../vhs/profparse.db
DB_FILE=../../vhs/profparse.db java -jar target/admin-1.0.jar < admin.cmd
rm admin.cmd
cd ../../vhs

# Note: In chapters 2 and 3, you'll need Google OAuth configured in the
# environent variables
if [ ! -f "./backend.env" ]; then
    echo "Cannot find backend.env. Aborting." >&2
    exit 1
fi

# File names.  Order matters, because the vhs files might make stateful
# filesystem changes.
#
# NB: .vhs files display nicely in emacs via 'conf-mode'
#
# Note: Some of these are interactive and are going to break.  Consider this
# list more of a "guide" than a "CI/CD buildscript".
VHS_FILES=(
    01_sqlite_01
    01_sqlite_02
    01_sqlite_03
    01_sqlite_04
    01_sqlite_05
    01_sqlite_06
    01_sqlite_07
    01_sqlite_08
    01_sqlite_09
    01_sqlite_10
    01_mvn_01
    01_mvn_02
    01_mvn_03
    01_mvn_04
    01_behavior_01
    01_test_01
    01_wrapup_01
    02_javalin_01
    02_javalin_02
    02_javalin_03
    02_javalin_04
    02_env_01
    02_mvn_01
    02_curl_01
    02_curl_02
    03_npm_01
    04_dokku_01
    04_dokku_02
    04_dokku_03
    04_dokku_04
    04_dokku_04a
    04_dokku_05
    04_dokku_06
    04_dokku_07
    04_dokku_08
)

# Build each image, then shrink it with gifsicle
for f in ${VHS_FILES[@]}
do
    $VHS $f.vhs -o $f.raw.gif
    $GIFSICLE $GFLAGS $f.raw.gif -o $f.gif
    rm $f.raw.gif
done
