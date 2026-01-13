# Load default configuration, then adjust the height of the output file
Source config.tape
Set Height 450

# Launch the container.  Always clean the folder *before* entering the
# container, so that we're more confident that the path is correct.
Hide
Type 'rm -rf ~/Desktop/sweng/dev/*' Enter
Type 'cp -R ../02_javalin/backend_01/ ~/Desktop/sweng/dev/backend/' Enter
Type './run_docker.sh' Enter
Type 'cd ~/cse216/backend' Enter
Type 'mvn package' Enter
Wait@60s /cse216.backend \$/
Type 'mvn clean' Enter
Wait@60s /cse216.backend \$/
Type 'cd ..' Enter
Type 'clear' Enter
Wait /\$/

Show
Sleep 1s
Type 'cd backend'
Sleep 1s
Enter
Sleep 1s
Type 'mvn package'
Sleep 1s
Enter
Sleep 1s
Wait@60s /cse216.backend \$/
Sleep 1s
# Type 'DB_FILE=../db.db java -jar target/backend-1.0.jar'
Type 'PORT=3000 java -jar target/backend-1.0.jar'
Sleep 1s
Enter
Sleep 5s

Hide
Ctrl+c
