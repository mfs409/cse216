# Load default configuration, then adjust the height of the output file
Source config.tape
Set Height 450

# Launch the container.  Always clean the folder *before* entering the
# container, so that we're more confident that the path is correct.
Hide
Type 'rm -rf ~/Desktop/sweng/dev/*' Enter
Type './run_docker.sh' Enter
Type 'clear' Enter
Show

Sleep 1
Type "cd ~/cse216/"
Sleep 1
Enter

Sleep 1
Type "npm create vue@latest"
Sleep 1
Enter
Sleep 1

Type "y"
Sleep 1
Enter
Sleep 1

Type "frontend"
Sleep 1
Enter
Sleep 1

Space
Sleep 1
Down
Sleep 1
Down
Sleep 1
Space
Sleep 1
Down
Sleep 1
Space
Sleep 1
Down
Sleep 1
Space
Sleep 1
Down
Sleep 1
Down
Sleep 1
Space
Sleep 1
Enter
Sleep 1
Enter
Sleep 1
Left
Sleep 1
Enter
Sleep 1

Type "cd frontend"
Sleep 1
Enter
Sleep 1

Type "tree"
Sleep 1
Enter
Sleep 1

Type "npm install"
Sleep 1
Enter
Sleep 1
Hide
Wait@60s /cse216.frontend \$/
Show
Sleep 5
