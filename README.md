# spring-boot-docker
Hello world project to deploy spring boot on Docker (Windows)

For Windows you need to install docker from Docker Toolbox. Refer to below link to install Docker Toolbox for Windows
https://docs.docker.com/toolbox/toolbox_install_windows/

Once you have installed and started your Docker verify your Host is able to ping:
```
ping <Your-Docker-IP>
```
## How to run
* Create your basic spring boot project from https://start.spring.io/ or from your preferred IDE.
* Create **Dockerfile** at the root of the project for creating a docker image from the Spring Boot Application

Here is the content of the ```Dockerfile```
```docker
# Specifies the Java 8 base Image from Openjdk
FROM openjdk:8

# Copies our spring boot jar(source) and adds them to the filesystem of the image at the path
ADD target/spring-docker.jar spring-docker.jar

# At runtime the container listens to 8080 port
EXPOSE 8080 

# Run our spring boot jar
ENTRYPOINT ["java", "-jar", "spring-docker.jar"] 
```

Docker runs instructions in a Dockerfile in order. A Dockerfile must start with a `FROM` instruction. The FROM instruction specifies the Base Image from which you are building.


Refer to below link for documentation about the Dockerfile reference

https://docs.docker.com/engine/reference/builder/
* Build an image from the ```Dockerfile``` using the ```docker build``` command 
```
docker build -f Dockerfile -t <Image tag name> .
```
-f  point to your Dockerfile in your system
-t Image tag name
```diff
- Dont forget to add the period at the end else you will get an error "docker build" requires exactly 1 argument
```
* Verify the docker image using ```dokcer images``` command
* Time to run our **First** Docker application. Run the below command
```
docker run -p 8080:8080 <Image tag name>
```
By default docker will run in ```attached mode``` i.e the output from the process is displayed on the terminal session. You would need to open duplicate terminal to execute other docker commands.

To make your process run in background add the -d flag to the docker run command.
```
docker run -d -p 8081:8080 <Image tag name>
```
If you want to make the docker container accessible from host machine we need to add the -p flag to the ```docker run``` command. In the above example we used the ```-p``` flag to publish ```8081```(left to colon) port from host to the container to port ```8080```(right to colon) within the container. 
* Once your docker service is running you can access it from your host browser using the ```http://docker_ip:host_port/```
* If you have started docker in detached(run in background) then you can stop your container using below command
```
# Find the container name to stop
docker ps -a
# Stop the container
docker stop <Container_Name>
```
# Build deploy docker image using maven
In the above approach we manually built the spring boot jar and manually deployed it to docker. We can do the same configuration using the maven build tool and build and deploy it to docker using the ```mvn clean install```.

fabric8io/docker-maven-plugin is a Maven plugin for managing Docker images and containers. I have added the fabric810 plugin to the spring boot pom.xml. Below are step by step details:
* STEP1: Add the faric plugin to your pom.xml under plugins element
```xml
<plugin>
 <groupId>io.fabric8</groupId>
 <artifactId>docker-maven-plugin</artifactId>
 <version>0.27.1</version>
```
* STEP2: Add the plugin configuration which contains a global part and a list of image-specific configuration within a <images> list, where each image is defined within a <image> tag. See below 
 ```xml
<configuration>
 <images>
 <!-- A single's image configuration -->
  <image>
   <name>springboot-dockerimage:${project.version}</name>
   <alias>springboot-dockerimage</alias>
  <!-- Configuration specifying how images are built -->
   <build>
    <dockerFileDir>${project.basedir}</dockerFileDir>
   </build>
  <!-- configuration describing how containers should be created and 
  started -->
   <run>
    <namingStrategy>alias</namingStrategy>
    <ports>
     <port>8080:8080</port>
    </ports>
   <log>
    <prefix>TC</prefix>
    <date>default</date>
    <color>cyan</color>
   </log>
 ....
</configuration>
 ```
I am buuilding the image from the ```Dockerfile``` which i had created previously. ALternatively we can define the build command within the <image> xml tag.
* STEP3: Add the execution phase which will build and deploy the Image
```xml
<executions>
 <execution>
  <id>start</id>
  <phase>pre-integration-test</phase>
  <goals>
   <goal>stop</goal>
   <goal>build</goal>
   <goal>start</goal>
  </goals>
 </execution>
</executions>
```
STEP3 is optional. If not specified you need to deploy it to docker.
* Run ```mvn clean install``` which will create the spring boot jar, build the image and deploy it to docker. Once the build is successfull you can run ```docker container ls``` command to verify if the spring boot docker image is running. You should see the docker start mesage as the output as shown below:

```
[INFO] --- docker-maven-plugin:0.27.1:stop (start) @ spring-docker ---
[INFO]
[INFO] --- docker-maven-plugin:0.27.1:build (start) @ spring-docker ---
[INFO] Building tar: D:\Sachin\data\appcode\Spring-Docker\spring-docker\target\docker\springboot-dockerimage\0.0.1-SNAPSHOT\tmp\docker-build.tar
[INFO] DOCKER> [springboot-dockerimage:0.0.1-SNAPSHOT] "springboot-dockerimage": Created docker-build.tar in 531 milliseconds
[INFO] DOCKER> [springboot-dockerimage:0.0.1-SNAPSHOT] "springboot-dockerimage": Built image sha256:65476
[INFO] DOCKER> [springboot-dockerimage:0.0.1-SNAPSHOT] "springboot-dockerimage": Removed old image sha256:9a42e
[INFO]
[INFO] --- docker-maven-plugin:0.27.1:start (start) @ spring-docker ---
[INFO] DOCKER> [springboot-dockerimage:0.0.1-SNAPSHOT] "springboot-dockerimage": Start container 657d75097927
[INFO]
[INFO] --- maven-install-plugin:2.5.2:install (default-install) @ spring-docker ---
[INFO] Installing D:\Sachin\data\appcode\Spring-Docker\spring-docker\target\spring-docker.jar to C:\Users\sachinseshadri_jois\.m2\repository\com\example\spring-docker\0.0.1-SNAPSHOT\spring-docker-0.0.
1-SNAPSHOT.jar
[INFO] Installing D:\Sachin\data\appcode\Spring-Docker\spring-docker\pom.xml to C:\Users\sachinseshadri_jois\.m2\repository\com\example\spring-docker\0.0.1-SNAPSHOT\spring-docker-0.0.1-SNAPSHOT.pom
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 37.617 s
[INFO] Finished at: 2018-10-21T19:38:05+01:00
[INFO] ------------------------------------------------------------------------

```

If you have not added the ```<executions>``` tag you need to start the docker manually using the mvn command ```mvn docker:start```

From your host browser using the ```http://docker_ip:host_port/```

To stop the docker run the mvn command ```mvn docker:stop```. The command will stop and remove the container as shown below from mvn output
```
D:\Sachin\data\appcode\Spring-Docker\spring-docker>mvn docker:stop
[INFO] Scanning for projects...
[INFO]
[INFO] ---------------------< com.example:spring-docker >----------------------
[INFO] Building spring-docker 0.0.1-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- docker-maven-plugin:0.27.1:stop (default-cli) @ spring-docker ---
[INFO] DOCKER> [springboot-dockerimage:0.0.1-SNAPSHOT] "springboot-dockerimage": Stop and removed container 657d75097927 after 0 ms
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 6.375 s
[INFO] Finished at: 2018-10-21T19:42:58+01:00
[INFO] ------------------------------------------------------------------------
```
Similar to fabri8o you can use spotify's docker-maven-plugin.

# Running MongoDB in docker container
```
# pull latest mongo image from docker hub
 docker pull mongo

# Run mongod server in docker container with port 27017
docker run --name <SOME_NAME> -d -p 27017:27017 mongo mongod
# bash into the container
docker exec -it <YOUR_CONTAINER_NAME> bash

# Connect to mongo
mongo

# Create user
use admin
db.createUser({user:"sachin",pwd:"password",roles:[{role:"root",db:"admin"}]})
exit from the mongo shell and container

# get your host IP. check the URL value to get the docker IP 
docker-machine ls

# now you can connect to mongo from any client(docker) or from your Host using mongo compass/ cli
mongo -u "sachin" -p "password" YOURHOSTIP --authenticationDatabase "admin"

```


# References
* Docker commands - https://docs.docker.com/engine/reference/commandline/docker/
* Install Docker on windows https://www.youtube.com/watch?v=S7NVloq0EBc
* https://devhints.io/docker
* https://github.com/docker-library/docs/tree/master/mongo
* https://dmp.fabric8.io/
* https://stackoverflow.com/questions/39126226/fabric8-springboot-full-example
* http://littlebigextra.com/build-deploy-docker-image-maven/


# Docker commands
| Command  | Description |
| ------------- | ------------- |
| docker -v  | Docker version  |
| docker ps -a  | Show all running containers |
| docker container ls  | List containers |
| docker stop <container_name>  | Stop running container specified |
| docker build -f Dockerfile -t <Image tag name> .  | Build your docker image from ```Dockerfile``` |
| docker run -d -p 8081:8080 <Image tag name>  | Run your docker container |
