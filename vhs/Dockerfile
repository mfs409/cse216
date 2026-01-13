FROM alpine:latest

# Update everything
RUN apk update --no-cache
RUN apk upgrade --no-cache

# Set up all required programs and libraries for *doing* the tutorials:
RUN apk add --no-cache openjdk21-jdk bash git maven nodejs-lts npm sqlite curl postgresql

# Set up required programs for *building* the tutorials
RUN apk add --no-cache screen openssh ca-certificates

# Create a user named 'me' using the User Private Group scheme
#
# NB: 1000 as the UID and GID is specific to my personal machine, and ensures
# that the created user has the right permissions on mount points in my
# filesystem
RUN addgroup -g 1000 -S me
RUN adduser -u 1000 -S -G me -s /bin/sh -D me
RUN mkdir -p /home/me
RUN chown -R me:me /home/me

# Make a cleaner prompt for generating screenshots:
RUN echo PS1=\'\\w $ \' >> /home/me/.bashrc
RUN chown -R me:me /home/me/.bashrc

# The container will run as `me` in their home folder
USER me
WORKDIR /home/me
CMD ["bash"]

# Build this container like this:
#   docker build -t cse216 .
