# Load default configuration, then adjust the height of the output file
Source config.tape
Set Height 350

# Launch the container.  This doesn't one clean first!
Hide
Type './run_docker.sh' Enter
Type 'cd ~/cse216' Enter
Type 'clear' Enter
Wait /cse216 \$/

# Always start by going into the folder, as a reminder to the student
Show
Sleep 1s
Type 'tree'
Sleep 1s
Enter
Sleep 5s
