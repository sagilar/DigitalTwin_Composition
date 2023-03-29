using System.Net.Sockets;
using System.Text;
using System.Collections.Concurrent;
using System.Threading;
using System;
using System.Globalization;
using UnityEngine;
using NetMQ;
using NetMQ.Sockets;
using Unity.Robotics.UrdfImporter.Control;
//using URController.Control;

public class data_subscriber : MonoBehaviour
{
    string[] topics = {"actual_q_0","actual_q_1", "actual_q_2", "actual_q_3", "actual_q_4", "actual_q_5"};
    private Thread _listenerWorker;
    bool done = false;
    Controller _robotarm_ctrl;

     public delegate void MessageDelegate(string message);

    private readonly MessageDelegate _messageDelegate;

    private readonly ConcurrentQueue<string[]> _messageQueue = new ConcurrentQueue<string[]>();
    public bool enable_tcp_connection = true;
    


    // Start is called before the first frame update

    void Listener(){
        try
        {
            using (var subSocket = new SubscriberSocket()){
                //Debug.Log("Starting subscriber for Topics: " + topics[0]);
                subSocket.Connect("tcp://localhost:5558");
                //subSocket.Connect("tcp://192.168.1.151:5558");
                Debug.Log("UR5e subscriber socket connecting...");
                for (int i = 0; i < topics.GetLength(0); ++i)
                    subSocket.Subscribe(topics[i]);

                while (!done)
                {
                    string[] bytes = Encoding.Default.GetString(subSocket.ReceiveFrameBytes()).Split(' ');
                    _messageQueue.Enqueue(bytes);
                }
                subSocket.Close();
            }
            
            NetMQConfig.Cleanup();
        }
        catch (SocketException e)
        {
            Debug.Log("SocketException occured source: " + e.Message);
        }
    }

    void HandleMessage(string[] message)
    {
        // Debug.Log("Handling message");
        /*Debug.Log("Message: ");
        foreach(var item in message)
        {
            Debug.Log(item.ToString());
        }
        */
        char topic_index = message[0][message[0].Length - 1];

        ArticulationBody[] articulationChain = _robotarm_ctrl.GetComponentsInChildren<ArticulationBody>();
        int joint_index = (int)Char.GetNumericValue(topic_index) + 1;
        //Debug.Log("Joint value: " + message[1]);
        float theta_val_msg = float.Parse(message[1],CultureInfo.InvariantCulture.NumberFormat);
        //Debug.Log("Joint value (after parsing): " + theta_val_msg);
        float theta_val_div = (float) (theta_val_msg / Math.PI);
        float theta_val = (float)(theta_val_div * 180.0);
        //float theta_val = (float)(float.Parse(message[1]) * 180/Math.PI);
        //_robotarm_ctrl.UpdateJointTheta(joint_index, theta_val);
        UpdateJointTheta(joint_index, theta_val);
        
        var twist = articulationChain[joint_index].jointPosition[0] * 180/Math.PI;

    }
    void Start()
    {
        Application.targetFrameRate = 30;

        if (enable_tcp_connection)
        {
            _robotarm_ctrl = gameObject.GetComponent<Controller>();
            done = false;
            _listenerWorker = new Thread(Listener);
            _listenerWorker.Start();
        }
    }


    // Update is called once per frame
    void Update()
    {
        while (!_messageQueue.IsEmpty)
        {
            string[] message;
            if (_messageQueue.TryDequeue(out message))
            {
                HandleMessage(message);
            }
            else
            {
                break;
            }
        }
            
    }

    private void OnDestroy()
    {
        done = true;
        if (enable_tcp_connection)
        {
            _listenerWorker.Join();
        }
    }
    
    void UpdateJointTheta(int jointIndex, float target_theta)
        {
            //Debug.Log(target_theta);
            ArticulationBody[] robotArticulationChain = _robotarm_ctrl.GetComponentsInChildren<ArticulationBody>();
            JointControl joint = robotArticulationChain[jointIndex].GetComponent<JointControl>();
            joint.controltype = _robotarm_ctrl.control;
            if (_robotarm_ctrl.control == ControlType.PositionControl)
            {
                ArticulationDrive drive = joint.joint.xDrive;
                drive.stiffness = _robotarm_ctrl.stiffness;
                drive.damping = _robotarm_ctrl.damping;
                drive.target = target_theta;
                joint.joint.xDrive = drive;
            }
    }

}