# Load default configuration, then adjust the height of the output file
Source config.tape
Set Height 200

# Launch the container.  Always clean the folder *before* entering the
# container, so that we're more confident that the path is correct.
Hide
Type 'rm -rf ~/Desktop/sweng/dev/*' Enter
Sleep 3s
Type 'cp -R ../02_javalin/backend_03/ ~/Desktop/sweng/dev/backend/' Enter
Sleep 3s
Type 'cp -R ../01_databases/admin/ ~/Desktop/sweng/dev/admin/' Enter
Sleep 3s
Type './run_docker.sh' Enter
Sleep 3s
Type 'cd ~/cse216/admin' Enter
Sleep 3s
Type 'DB_FILE=test.db mvn package' Enter
Sleep 3s
Wait@40s /cse216.admin \$/
Sleep 3s
Type 'DB_FILE=../db.db java -jar target/admin-1.0.jar' Enter
Sleep 5s
Type 'C' Enter
Sleep 1s
Type '+p' Enter
Sleep 1s
Type 'mfs409@lehigh.edu' Enter
Sleep 1s
Type 'Prof. Parse' Enter
Sleep 1s
Type 'q' Enter
Sleep 1s
Type 'clear' Enter
Sleep 1s
Type 'cd ~/cse216/backend' Enter
Sleep 1s
Type 'mvn package' Enter
Sleep 1s
Wait@60s /cse216.backend \$/
Type 'screen' Enter
Sleep 3s
Type 'PORT=3000 DB_FILE=../db.db java -jar target/backend-1.0.jar'
Sleep 1s
Enter
Sleep 5s
Ctrl+a
Ctrl+c
Type "cd ~/" Enter
Sleep 1s
Type "clear" Enter
Sleep 1s

Show
Sleep 1s
Type "curl -s http://localhost:3000/people"
Sleep 1s
Enter
Sleep 5s

Hide
Ctrl+d
Ctrl+c
Ctrl+d
