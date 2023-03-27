import paho.mqtt.client as mqtt
import zmq

### ZMQ ###
port_zmq_kuka = "5557"
port_zmq_ur = "5558"
zmq_context = zmq.Context()
socket_zmq_kuka = zmq_context.socket(zmq.PUB)
socket_zmq_ur = zmq_context.socket(zmq.PUB)
try:
    socket_zmq_kuka.bind("tcp://*:" + port_zmq_kuka)
    socket_zmq_ur.bind("tcp://*:" + port_zmq_ur)
except zmq.error.ZMQError as exc:
    print ("socket already in use, try restarting it")
    print(exc)

def send_string_ur(msg):
    #print("Forwarding ZMQ message to UR")
    print(msg)
    socket_zmq_ur.send_string(msg)


def send_string_kuka(msg):
    #print("Forwarding ZMQ message to Kuka")
    print(msg)
    socket_zmq_kuka.send_string(msg)


mqtt_client = mqtt.Client()

def on_connect(client, userdata, flags, rc):
    print("Connected with result code "+str(rc))

    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.
    mqtt_client.subscribe("ur5e/#")
    mqtt_client.subscribe("kuka/#")

# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    print(msg.topic+" "+str(msg.payload))
    zmq_topic = msg.topic.split("/")[1]
    payload = str(msg.payload).replace("'","").replace("b","")

    if ("ur5e" in msg.topic):
        send_string_ur(zmq_topic + " " + payload)
    elif ("kuka" in msg.topic):
        send_string_kuka(zmq_topic + " " + payload)


mqtt_client.on_connect = on_connect
mqtt_client.on_message = on_message
mqtt_client.connect("192.168.1.151", 1883, 60)

mqtt_client.loop_forever()

socket_zmq_kuka.close()
socket_zmq_ur.close()
zmq_context.term()
