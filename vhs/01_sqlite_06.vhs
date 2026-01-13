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
Type "INSERT INTO tblPerson (email, name) VALUES ('person@email.com', 'A. Person');" Enter
Sleep 1s
Type "INSERT INTO tblPerson (email, name) VALUES ('person2@email.com', 'B. Person');" Enter
Sleep 1s
Type 'SELECT * FROM tblPerson;' Enter
Sleep 1s
Type "UPDATE tblPerson SET name = 'My Friend' WHERE id = 2;" Enter
Sleep 1s
Type 'SELECT * FROM tblPerson;' Enter
Sleep 1s
Type 'DELETE FROM tblPerson WHERE id = 2;' Enter
Sleep 1s
Type 'SELECT * FROM tblPerson;' Enter
Sleep 1s
Sleep 3s
