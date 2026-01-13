# Load default configuration, then adjust the height of the output file
Source config.tape
Set Height 450

# Launch the container.  Always clean the folder *before* entering the
# container, so that we're more confident that the path is correct.
Hide
Type 'rm -rf ~/Desktop/sweng/dev/*' Enter
Type 'cp -R ../02_javalin/backend_02/ ~/Desktop/sweng/dev/backend/' Enter
Type './run_docker.sh' Enter
Type 'cd ~/cse216/backend' Enter
Type 'mvn package' Enter
Wait@120s /cse216.backend \$/
Type 'screen' Enter
Type 'clear' Enter
Ctrl+a
Ctrl+c
Sleep 1s

Show
Sleep 1s
Type 'PORT=3000 java -jar target/backend-1.0.jar'
Sleep 1s
Enter
Sleep 1s

Hide
Ctrl+a
Ctrl+a
Type "curl http://localhost:3000/people" Enter
Ctrl+a
Ctrl+a
Show
Sleep 1s

Hide
Ctrl+a
Ctrl+a
Type "curl http://localhost:3000/people -X POST -d \'{'name':'PROF. Parse'}\'" Enter
Ctrl+a
Ctrl+a
Show
Sleep 1s

Hide
Ctrl+a
Ctrl+a
Type "curl http://localhost:3000/people/1 -X PUT -d \'{'name':'Prof. Parse'}\'" Enter
Ctrl+a
Ctrl+a
Show
Sleep 1s

Hide
Ctrl+a
Ctrl+a
Type "curl http://localhost:3000/people/1 -X DELETE" Enter
Ctrl+a
Ctrl+a
Show
Sleep 1s

Hide
Ctrl+a
Ctrl+a
Type "curl http://localhost:3000/people?key=value" Enter
Ctrl+a
Ctrl+a
Show
Sleep 1s

Hide
Ctrl+a
Ctrl+a
Type 'curl "http://localhost:3000/people?format=json&colorize=true"' Enter
Ctrl+a
Ctrl+a
Show
Sleep 5s

Hide
Ctrl+d
Ctrl+d
