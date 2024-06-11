FROM ubuntu:latest
RUN apt-get update && \
    apt-get install -y curl dnsutils iptables tcpdump

COPY ./setup/setup.sh setup.sh

CMD ["bash", "setup.sh"]