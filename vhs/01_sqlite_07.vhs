# Load default configuration, then adjust the height of the output file
Source config.tape
Set Height 250

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
Type "INSERT INTO tblMessage (subject, details, as_of, creatorId) VALUES ('test', 'this is a test message', 1757603196045, 1);" Enter
Sleep 1s
Type 'SELECT * FROM tblMessage;' Enter
Sleep 1s
Type 'SELECT * from viewMessage;' Enter
Sleep 1s
Sleep 3s
