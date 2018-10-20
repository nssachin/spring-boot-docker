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
- Dont forget to add the period at the end else you will get an error "docker build" requires exactly 1 argument.
```
* Verify the docker image using ```dokcer images``` command
* Time to run our **First** Docker application. Run the below command
```
docker run -p 8080:8080 <Image tag name>
```
By default docker will runn in ```attached mode``` i.e the output from the process is displayed on the terminal session. You would need to open duplicate terminal to execute other docker commands.

To make your process run in background add the -d flag to the docker run command.
```
docker run -d -p 8081:8080 <Image tag name>
```
If you want to make the docker container accessible from host machine we need to add the -p flag to the ```docker run``` command. In the above example we used the ```-p``` flag to publish ```8081```(left to colon) port from host to the container to port ```8080```(right to colon) within the container.
