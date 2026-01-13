# Load default configuration, then adjust the height of the output file
Source config.tape
Set Height 250

# Launch the container.  Always clean the folder *before* entering the
# container, so that we're more confident that the path is correct.
Hide
Type 'rm -rf ~/Desktop/sweng/dev/*' Enter
Type 'rm -rf ~/Desktop/sweng/dev/.git' Enter
Type 'cp -R ../02_javalin/backend_03/ ~/Desktop/sweng/dev/backend/' Enter
Sleep 3s
Type './run_docker.sh' Enter
Sleep 3s
Type 'cd ~/cse216/' Enter
Type 'git config --global init.defaultBranch main' Enter
Type 'git config --global user.email "mfs409@lehigh.edu"' Enter
Type 'git config --global user.name "Prof. Parse"' Enter
Sleep 3s
Type 'git init' Enter
Type 'git add backend' Enter
Type 'git commit -m "Latest back-end code"' Enter
Type 'clear' Enter
Type '# Note: this is running in the project root (cse216), not cse216/backend!' Enter

Show
Sleep 1s
Type 'mkdir local'
Sleep 1s
Enter
Sleep 1s
Type 'echo local/ >> .gitignore'
Sleep 1s
Enter
Sleep 1s
Type "echo '*.env' >> .gitignore"
Sleep 1s
Enter
Sleep 1s
Type 'touch local/backend.env'
Sleep 1s
Enter
Sleep 1s
Type 'git status'
Sleep 1s
Enter
Sleep 5s
