### Author: Santiago
import paho.mqtt.client as mqtt

import zmq

### ZMQ ###
'''port_zmq_kuka = "5557"
port_zmq_ur = "5558"
zmq_context = zmq.Context()
socket_zmq_kuka = zmq_context.socket(zmq.PUB)
socket_zmq_ur = zmq_context.socket(zmq.PUB)
try:
    socket_zmq_kuka.bind("tcp://*:" + port_zmq_kuka)
    socket_zmq_ur.bind("tcp://*:" + port_zmq_ur)
except zmq.error.ZMQError as exc:
    print ("socket already in use, try restarting it")
    print(exc)'''

mqtt_client = mqtt.Client()
mqtt_client.connect("127.0.0.1", 1883, 60)

mqtt_client.loop_start()

def send_string_ur(msg):
    #socket_zmq_ur.send_string(msg)
    msg_split = msg.split(" ")
    topic = msg_split[0]
    payload = msg_split[1]
    mqtt_client.publish("ur5e/" + topic,payload)

def send_string_kuka(msg):
    #socket_zmq_ur.send_string(msg)
    msg_split = msg.split(" ")
    topic = msg_split[0]
    payload = msg_split[1]
    mqtt_client.publish("kuka/" + topic,payload)

def close():
    mqtt_client.disconnect()
    #socket_zmq_kuka.close()
    #socket_zmq_ur.close()
    #zmq_context.term()
