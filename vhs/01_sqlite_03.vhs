# Load default configuration, then adjust the height of the output file
Source config.tape
Set Height 450

# Launch the container.  Note that this vhs relies on the state of the
# previous one
Hide
Type './run_docker.sh' Enter
Type 'cd ~/cse216' Enter
Type 'sqlite3 db.db' Enter
Ctrl+l
Wait /sqlite>/

Show
Sleep 1s
Type 'CREATE TABLE tblMessage (' Enter
Type '    id INTEGER PRIMARY KEY, ' Enter
Type '    subject VARCHAR(50) NOT NULL, ' Enter
Type '    details VARCHAR(500) NOT NULL, ' Enter
Type '    as_of DATE NOT NULL, ' Enter
Type '    creatorId INTEGER, ' Enter
Type '    FOREIGN KEY (creatorId) REFERENCES tblPerson(id)' Enter
Type ');'
Sleep 1s
Enter
Sleep 1s
Type '.schema'
Sleep 1s
Enter
Sleep 3s
