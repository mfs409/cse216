# Load default configuration, then adjust the height of the output file
Source config.tape
Set Height 250

# Launch the container.  Note that this vhs relies on the state of the
# previous one
Hide
Type './run_docker.sh' Enter
Type 'cd ~/cse216' Enter
Type 'sqlite3 db.db' Enter
Type 'PRAGMA foreign_keys = ON;' Enter
Ctrl+l
Wait /sqlite>/

Show
Sleep 1s
Type 'DELETE FROM tblPerson WHERE id=1;' Enter
Sleep 1s
Sleep 3s
