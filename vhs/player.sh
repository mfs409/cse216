#!/usr/bin/bash

# The core data structure is an array of statements
#
# The statement format is <how> <space> <command>, where `how` indicates how
# `command` ought to be executed
#
# `how` is always a single character.  It can be one of the following:
#  - L -- print text in a single statement and wait 1s
#  - E -- use "echo" to print a line along with a newline
#  - e -- print a newline (enter)
#  - C -- print line one character at a time
#  - S -- execute a command without showing its output
#  - X -- execute a command and print its output, line by line, like 'L'
#  - c -- clear the screen
#  - # -- a comment

# Execute a command and print its output, line by line, with .1s delay
# between lines
function cmd_execute {
    # Run the command and dump its output to a temp file
    fname=$(mktemp)
    bash -c "$1" > $fname
    # Print the lines of the file
    while IFS= read -r line
    do
        echo "$line"
        sleep 0.1
    done < "$fname"
    rm $fname
}

# Execute a command and suppress its output
function cmd_silent {
    $1 > /dev/null
}

# Print a string, one character at a time, with a .05s delay between chars
function cmd_chars {
    txt=$1
    for ((i=0; i<${#txt}; i++)); do
        printf "${txt:i:1}"
        sleep .05
    done
}

# Print a string with a newline at the end, and then wait 1s
function cmd_line {
    printf "$1"
    sleep 1
}

# Given a statement, figure out what to do
function process_statement {
    cmd=$1
    first="${cmd:0:1}"
    rest="${cmd:2}"
    if [[ "$first" == "L" ]]
    then
        cmd_line "$rest"
    elif [[ "$first" == "C" ]]
    then
        cmd_chars "$rest"
    elif [[ "$first" == "X" ]]
    then
        cmd_execute "$rest"
    elif [[ "$first" == "S" ]]
    then
        cmd_silent "$rest"
    elif [[ "$first" == "c" ]]
    then
        clear
    elif [[ "$first" == "e" ]]
    then
        printf "\n"
    elif [[ "$first" == "#" ]]
    then
        printf ""
    elif [[ "$first" == "E" ]]
    then
        echo "$rest"
    else
        echo "Invalid first character $first"
        exit
    fi
}

# Fail unless arg count is 1
if [ "$#" -ne 1 ]
then
  echo "Usage: $0 <filename>"
  exit 1
fi

# Get the input file
input=()
while IFS= read -r line
do
    input+=("$line")
done < "$1"

for line in "${input[@]}"
do
    process_statement "$line"
done

