# Load default configuration, then adjust the height of the output file
Source config.tape
Set Height 450

# Launch the container.  Always clean the folder *before* entering the
# container, so that we're more confident that the path is correct.
Hide
Type 'rm -rf ~/Desktop/sweng/dev/*' Enter
Type 'cp -R ../01_databases/admin_06/ ~/Desktop/sweng/dev/admin/' Enter
Type './run_docker.sh' Enter
Type 'cd ~/cse216/admin' Enter
Type 'DB_FILE=test.db mvn package' Enter
Wait@60s /cse216.admin \$/
Type 'mvn clean' Enter
Wait@5s /cse216.admin \$/
Type 'clear' Enter
Wait /\$/

Show
Sleep 1s
Type 'DB_FILE=test.db mvn package'
Sleep 1s
Enter
Sleep 1s
Wait@60s /cse216.admin \$/
Sleep 1s
Type 'rm -f test.db'
Sleep 1s
Enter
Sleep 5s
