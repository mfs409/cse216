# Load default configuration, then adjust the height of the output file
Source config.tape
Set Height 250

# Launch the container.  Always clean the folder *before* entering the
# container, so that we're more confident that the path is correct.
Hide
Type 'rm -rf ~/Desktop/sweng/dev/*' Enter
Type './run_docker.sh' Enter
Type 'cd ~/cse216' Enter
Type 'sqlite3 db.db' Enter
Ctrl+l
Wait /sqlite>/

# Always start by going into the folder, as a reminder to the student
Show
Sleep 1s
Type 'CREATE TABLE tblPerson (' Enter
Type '    id INTEGER PRIMARY KEY, ' Enter
Type '    email VARCHAR(30) NOT NULL UNIQUE COLLATE NOCASE, ' Enter
Type '    name VARCHAR(50) NOT NULL' Enter
Type ');'
Sleep 1s
Enter
Sleep 1s
Type '.schema'
Sleep 1s
Enter
Sleep 3s
