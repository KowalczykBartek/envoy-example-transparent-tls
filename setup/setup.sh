echo "Configuring iptables"

#enable debug on envoy
curl -XPOST http://localhost:15000/logging?level=debug

iptables -t nat \
             -A OUTPUT \
             -p tcp \
             --dport 9099 \
             -j REDIRECT \
             --to-port 9000 \
             -m owner \
             --uid-owner 1234

iptables -t nat -A PREROUTING -p tcp --dport 9099 -i eth0 -j REDIRECT --to-port 9716

sleep 365d