# Load default configuration, then adjust the height of the output file
Source config.tape
Set Height 250

# This is a non-container script.  It uses 'player.sh' to run commands.  This
# reduces the .vhs file to just two very careful wait commands
Hide
# Start the script and wait .5s before starting to record.  This gives the
# script time to clear the screen and draw a fake prompt
Type './player.sh 04_dokku_08.player'
Enter
Sleep .5s
Show
# The script should end with a 'Sleep 15', so that this can do a 'Wait' and
# then a 'Sleep 5' without risking the script completely terminating.
#
# However, if we just wait for 'cse216 $', we're in trouble, so we cheat: the
# final prompt printed by the script has an extra space before the '$', which
# means we can wait on something that is guaranteed to be unique.
Wait /  \$/
# The final wait is so that the loop doesn't kick in too quickly.
Sleep 5s
