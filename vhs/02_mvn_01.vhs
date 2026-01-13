# Load default configuration, then adjust the height of the output file
Source config.tape
Set Height 600

# Launch the container.  Always clean the folder *before* entering the
# container, so that we're more confident that the path is correct.
Hide
Type 'rm -rf ~/Desktop/sweng/dev/* ~/Desktop/sweng/dev/.git' Enter
Type 'cp -R ../02_javalin/backend_04/ ~/Desktop/sweng/dev/backend/' Enter
Type 'cp profparse.db ~/Desktop/sweng/dev/db.db' Enter
Type 'mkdir ~/Desktop/sweng/dev/local' Enter
Type 'cp backend.env ~/Desktop/sweng/dev/local' Enter
Type './run_docker.sh' Enter
Type 'cd ~/cse216/backend' Enter
Type 'mvn package' Enter
Wait@60s /cse216.backend \$/
Type 'mvn clean' Enter
Type 'clear' Enter
Wait@60s /cse216.backend \$/

Show
Sleep 1s
Type 'mvn package'
Sleep 1s
Enter
Wait@60s /cse216.backend \$/

Sleep 1s
Type "source ../local/backend.env"
Sleep 1s
Enter

Sleep 1s
Type "java -jar target/backend-1.0.jar"
Sleep 1s
Enter
Sleep 2s

Sleep 5s
