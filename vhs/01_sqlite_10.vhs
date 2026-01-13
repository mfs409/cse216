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
Type 'DROP VIEW viewMessage;'
Sleep 1s
Enter
Sleep 1s
Type 'DROP TABLE tblMessage;'
Sleep 1s
Enter
Sleep 1s
Type 'DROP TABLE tblPerson;'
Sleep 1s
Enter
Sleep 1s
Type '.schema'
Sleep 1s
Enter
Sleep 3s
