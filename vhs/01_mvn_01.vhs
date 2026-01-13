# Load default configuration, then adjust the height of the output file
Source config.tape
Set Height 450

# Launch the container.  Always clean the folder *before* entering the
# container, so that we're more confident that the path is correct.
Hide
Type 'rm -rf ~/Desktop/sweng/dev/*' Enter
Type './run_docker.sh' Enter
Type 'clear' Enter
Wait /\$/

# Always start by going into the folder, as a reminder to the student
Show
Sleep 1s
Type 'cd ~/cse216' Enter
Sleep 1s
Type 'mvn archetype:generate \' Enter
Type '    -DarchetypeGroupId=org.apache.maven.archetypes \' Enter
Type '    -DarchetypeArtifactId=maven-archetype-quickstart \' Enter
Type '    -DarchetypeVersion=1.5 \' Enter
Type '    -DinteractiveMode=false \' Enter
Type '    -DartifactId=admin \' Enter
Type '    -DgroupId=quickstart.admin \' Enter
Type '    -Dpackage=quickstart.admin'
Sleep 1s
Enter

# Wait for a shell prompt, so we know the command finished
Wait@60s /cse216 \$/
Sleep 5s
