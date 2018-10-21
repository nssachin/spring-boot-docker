# Dockerfile for building a MongoDB service

# Pull base image.
FROM mongo

# Define mountable directories.
VOLUME ["/data/db"]

# Define working directory.
WORKDIR /data

# ENV variable
ENV AUTH yes

# Expose ports.
#   - 27017: process
#   - 28017: http
EXPOSE 27017

ADD run.sh /run.sh
ADD set_mongodb_password.sh /set_mongodb_password.sh

RUN chmod +x /run.sh
RUN chmod +x /set_mongodb_password.sh

CMD ["/run.sh"]
