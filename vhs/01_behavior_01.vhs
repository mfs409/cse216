# Load default configuration, then adjust the height of the output file
Source config.tape
Set Height 450

# Launch the container.  Always clean the folder *before* entering the
# container, so that we're more confident that the path is correct.
Hide
Type 'rm -rf ~/Desktop/sweng/dev/*' Enter
Type 'cp -R ../01_databases/admin_05/ ~/Desktop/sweng/dev/admin/' Enter
Type './run_docker.sh' Enter
Type 'cd ~/cse216/admin' Enter
Type 'mvn package' Enter
Wait@60s /cse216.admin \$/
Type 'mvn clean' Enter
Wait@60s /cse216.admin \$/
Type 'clear' Enter
Wait /\$/

Show
Sleep 1s
Type 'mvn package'
Sleep 1s
Enter
Sleep 1s
Wait@60s /cse216.admin \$/
Sleep 1s
Type 'rm -f db.db'
Sleep 1s
Enter
Type 'DB_FILE=db.db java -jar target/admin-1.0.jar'
Sleep 1s
Enter
Sleep 1s
Type 'C'
Sleep 1s
Enter
Sleep 1s
Type 'D'
Sleep 1s
Enter
Sleep 1s
Type 'C'
Sleep 1s
Enter
Sleep 1s
Type '1p'
Sleep 1s
Enter
Sleep 1s
Type '1'
Sleep 1s
Enter
Sleep 1s
Type '+p'
Sleep 1s
Enter
Sleep 1s
Type 'parse@lehigh.edu'
Sleep 1s
Enter
Sleep 1s
Type 'Prof. Parse'
Sleep 1s
Enter
Sleep 1s
Type '1p'
Sleep 1s
Enter
Sleep 1s
Type '1'
Sleep 1s
Enter
Sleep 1s
Type '*p'
Sleep 1s
Enter
Sleep 1s
Type '-p'
Sleep 1s
Enter
Sleep 1s
Type '2'
Sleep 1s
Enter
Sleep 1s
Type '*p'
Sleep 1s
Enter
Sleep 1s
Type '-p'
Sleep 1s
Enter
Sleep 1s
Type '1'
Sleep 1s
Enter
Sleep 1s
Type '*p'
Sleep 1s
Enter
Sleep 1s
Type 'q'
Sleep 1s
Enter
Sleep 5s
