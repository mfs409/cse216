# Load default configuration, then adjust the height of the output file
Source config.tape
Set Height 250

# This one is a bit different: it requires manual re-writing every time, so
# that it can use the right cookie information.  It also requires the server
# to already be running in a container, and it doesn't run in a container
#
# rm -rf ~/Desktop/sweng/dev/* ~/Desktop/sweng/dev/.git
# cp -R ../02_javalin/backend_05/ ~/Desktop/sweng/dev/backend/
# cp profparse.db ~/Desktop/sweng/dev/db.db
# mkdir ~/Desktop/sweng/dev/local
# cp backend.env ~/Desktop/sweng/dev/local
# ./run_docker.sh
# cd ~/cse216/backend
# mvn package
# source ../local/backend.env
# java -jar target/backend-1.0.jar


Show

Sleep 1s
Type "curl http://localhost:3000/people \" Enter
Type "-b 'auth.gId=111044916034782695562; auth.email=mfs409@lehigh.edu; auth.name=TWljaGFlbCBTcGVhcg==; auth.id=1; auth.key=MubUx0dYdU6ahZV5u2hNx'"
Sleep 1s
Enter
Enter

Sleep 1s
Type "curl http://localhost:3000/people \" Enter
Type "-b 'auth.gId=111044916034782695562; auth.email=mfs409@lehigh.edu; auth.name=TWljaGFlbCBTcGVhcg==; auth.id=1; auth.key=MubUx0dYdU6ahZV5u2hNx' \" Enter
Type "-X PUT \" Enter
Type '-d "{\"name\": \"new name\"}"'
Sleep 1s
Enter
Enter

Sleep 1s
Type "curl http://localhost:3000/people \" Enter
Type "-b 'auth.gId=111044916034782695562; auth.email=mfs409@lehigh.edu; auth.name=TWljaGFlbCBTcGVhcg==; auth.id=1; auth.key=MubUx0dYdU6ahZV5u2hNx' \" Enter
Sleep 1s
Enter
Enter

Sleep 1s
Type "curl http://localhost:3000/people/1 \" Enter
Type "-b 'auth.gId=111044916034782695562; auth.email=mfs409@lehigh.edu; auth.name=TWljaGFlbCBTcGVhcg==; auth.id=1; auth.key=MubUx0dYdU6ahZV5u2hNx' \" Enter
Sleep 1s
Enter
Enter

Sleep 5s
