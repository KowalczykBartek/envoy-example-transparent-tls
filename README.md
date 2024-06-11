## what it is ?
Just an example of envoy as transparent proxy with iptables and so_original_dst used (something like istio).
This sandbox is highly inspired by this blog-post https://venilnoronha.medium.com/introduction-to-original-destination-in-envoy-d8a8aa184bb6 

## how to run
build an image(and jar which is a ping-pong app written in Java):
```commandline
create-image.sh 
```
and run 
```commandline
docker-compose up
```
you can exec into one of the *-setup containers:
```commandline
docker exec -u 0 -it envoy-example-transparent-tls-first-setup-1 bash
```
and examine traffic :
```commandline
tcpdump -A -i eth0 port 8099
```
is unmodified, so plaintext, and:
```commandline
tcpdump -A -i eth0 port 9099
```
is encrypted, that is because 
```commandline
iptables -t nat \
             -A OUTPUT \
             -p tcp \
             --dport 9099 \
             -j REDIRECT \
             --to-port 9000 \
             -m owner \
             --uid-owner 1234

iptables -t nat -A PREROUTING -p tcp --dport 9099 -i eth0 -j REDIRECT --to-port 9716
```
routes all packets to envoy-proxy.

## usefully links
https://venilnoronha.medium.com/introduction-to-original-destination-in-envoy-d8a8aa184bb6

https://www.cyberciti.biz/faq/linux-iptables-delete-prerouting-rule-command/

https://github.com/istio/istio/wiki/Understanding-IPTables-snapshot (points why its necessary to have --uid-owner on OUTPUT rule: basically, to avoid redirection back to the proxy on the output chain)