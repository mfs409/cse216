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
Type 'CREATE VIEW viewMessage AS' Enter
Type 'SELECT' Enter
Type '    tblMessage.id AS id,' Enter
Type '    tblMessage.subject AS subject,' Enter
Type '    tblMessage.details AS details,' Enter
Type '    tblMessage.as_of AS as_of,' Enter
Type '    tblMessage.creatorId AS creatorId,' Enter
Type '    tblPerson.email AS email,' Enter
Type '    tblPerson.name AS name' Enter
Type 'FROM ' Enter
Type '    tblMessage INNER JOIN tblPerson' Enter
Type '        ON tblMessage.creatorId = tblPerson.id;'
Sleep 1s
Enter
Sleep 1s
Type '.schema'
Sleep 1s
Enter
Sleep 3s
