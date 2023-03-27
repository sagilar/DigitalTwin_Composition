## Author: Santiago Gil

from flask import Flask
from flask import request
from flask_restful import reqparse, abort, Api, Resource
from robots_flexcell import robots
import queue_server
import numpy as np
import matplotlib.pyplot as plt
from IPython import embed
import os

'''### ZMQ ###
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
    print(exc)'''





ur_robot = robots.UR5e_RL()
kuka_robot = robots.KukaLBR_RL()

app = Flask("robots_server")
api = Api(app)



position_items = []

def show_current_grid_dynamic(placed_items,shape="",invert=False,save=False):
    plt.figure(1)
    plt.clf()
    plt.title("Shape: %s" % (shape))
    x = np.arange(0, 16, 1)
    y = np.arange(0, 24, 1)
    yy, xx = np.meshgrid(y, x, sparse=False)
    plt.plot(yy, xx, marker='.', color='k', linestyle='none')
    for item in placed_items:
        item_np = np.array(item)
        if invert == False:
            plt.scatter(item_np[0],item_np[1],marker="*",color='g',s=80)
        else:
            plt.scatter(item_np[1],item_np[0],marker="*",color='g',s=80)
    if (save):
        plt.savefig("shape_" + shape + ".png")
    else:
        plt.plot()

def transmit_robot_motion(q,robot):
    joint_pos = q
    if (robot == "UR5e"):
        for j in range(len(joint_pos)):
            queue_server.send_string_ur(f"actual_q_{j} {joint_pos[j]}")
            pass
            #socket_zmq_ur.send_string(f"actual_q_{j} {joint_pos[j]}")
    elif (robot == "Kuka"):
        for j in range(len(joint_pos)):
            queue_server.send_string_kuka(f"actual_q_{j} {joint_pos[j]}")
            pass
            #socket_zmq_kuka.send_string(f"actual_q_{j} {joint_pos[j]}")

parser = reqparse.RequestParser(bundle_errors=True)
@app.get('/urComputeRotation')
def compute_ur_rotation():
    y = float(request.args.get("param1"))
    z = float(request.args.get("param2"))
    y_rot,z_rot = ur_robot.compute_yz_joint(y,z)
    return str([y_rot,z_rot])

@app.get('/urComputeXYZ')
def compute_ur_xyz():
    X = int(request.args.get("param1"))
    Y = int(request.args.get("param2"))
    Z = int(request.args.get("param3")) # 0
    comp_x,comp_y,comp_z = ur_robot.compute_xyz_flexcell(X,Y,Z=Z)
    return str([comp_x,comp_y,comp_z])

@app.get('/urComputeIK')
def compute_ur_ik():
    global position_items
    X = int(request.args.get("param1"))
    Y = int(request.args.get("param2"))
    Z = int(request.args.get("param3")) # 0
    if (len(position_items)>=24):
        position_items = []
    position_items.append([X,Y])
    items = np.array(position_items)
    show_current_grid_dynamic(items,invert=True,save=True)
    comp_x,comp_y,comp_z = ur_robot.compute_xyz_flexcell(X,Y,Z=Z)
    y_rot,z_rot = ur_robot.compute_yz_joint(comp_y,comp_z)
    validity = ur_robot.compute_ik_validity(comp_x,y_rot,z_rot)
    if (not validity):
        return "false"
    return "true"

@app.get('/urComputeQ')
def compute_ur_q():
    X = int(request.args.get("param1"))
    Y = int(request.args.get("param2"))
    Z = int(request.args.get("param3")) # 0
    comp_x,comp_y,comp_z = ur_robot.compute_xyz_flexcell(X,Y,Z=Z)
    y_rot,z_rot = ur_robot.compute_yz_joint(comp_y,comp_z)
    array_joint_pos = ur_robot.move_p(comp_x,y_rot,z_rot)[0]
    value = ""
    for obj in array_joint_pos:
        value = value + "," + str(obj)
    return value[1:]

@app.get('/urMoveP')
def move_ur_p():
    q0 = float(request.args.get("param1"))
    q1 = float(request.args.get("param2"))
    q2 = float(request.args.get("param3"))
    q3 = float(request.args.get("param4"))
    q4 = float(request.args.get("param5"))
    q5 = float(request.args.get("param6"))
    q = [q0,q1,q2,q3,q4,q5]
    transmit_robot_motion(q,"UR5e")
    return "true"

@app.get('/urGripperPick')
def urGripperPick():
    return "true"

@app.get('/urGripperPlace')
def urGripperPlace():
    return "true"

@app.get('/kukaComputeXYZ')
def compute_kuka_xyz():
    X = int(request.args.get("param1"))
    Y = int(request.args.get("param2"))
    Z = int(request.args.get("param3")) # 0
    comp_x,comp_y,comp_z = kuka_robot.compute_xyz_flexcell(X,Y,Z=Z)
    return str([comp_x,comp_y,comp_z])

@app.get('/kukaComputeIK')
def compute_kuka_ik():
    X = int(request.args.get("param1"))
    Y = int(request.args.get("param2"))
    Z = int(request.args.get("param3")) # 0
    comp_x,comp_y,comp_z = kuka_robot.compute_xyz_flexcell(X,Y,Z=Z)
    validity = kuka_robot.compute_ik_validity(comp_y,comp_x,comp_z)
    if (not validity):
        return "false"
    return "true"

@app.get('/kukaComputeQ')
def compute_kuka_q():
    X = int(request.args.get("param1"))
    Y = int(request.args.get("param2"))
    Z = int(request.args.get("param3")) # 0
    comp_x,comp_y,comp_z = kuka_robot.compute_xyz_flexcell(X,Y,Z=Z)
    array_joint_pos = kuka_robot.move_p(comp_y,comp_x,comp_z)[0]
    value = ""
    for obj in array_joint_pos:
        value = value + "," + str(obj)
    return value[1:]

@app.get('/kukaMoveP')
def move_kuka_p():
    q0 = float(request.args.get("param1"))
    q1 = float(request.args.get("param2"))
    q2 = float(request.args.get("param3"))
    q3 = float(request.args.get("param4"))
    q4 = float(request.args.get("param5"))
    q5 = float(request.args.get("param6"))
    q6 = float(request.args.get("param7"))
    q = [q0,q1,q2,q3,q4,q5,q6]
    transmit_robot_motion(q,"Kuka")
    return "true"

@app.get('/kukaGripperPick')
def kukaGripperPick():
    return "true"

@app.get('/kukaGripperPlace')
def kukaGripperPlace():
    return "true"

@app.get('/hi')
def hi():
    return "hi"

if __name__ == '__main__':
    app.run(debug=True, host="0.0.0.0")
    queue_server.close()
    '''zmq.close(socket_zmq_kuka)
    zmq.close(socket_zmq_ur)
    zmq.close(zmq_context)
    socket_zmq_kuka.close()
    socket_zmq_ur.close()
    zmq_context.term()'''
    #embed()
