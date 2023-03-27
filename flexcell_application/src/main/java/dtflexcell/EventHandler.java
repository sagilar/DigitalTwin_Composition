package dtflexcell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.operation.ConnectedOperation;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;
import org.eclipse.basyx.vab.registry.proxy.VABRegistryProxy;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import CompositionManager.SBDigitalTwin;
import DTManager.DTManager;

public class EventHandler {
	private static final int AAS_PORT = 4001;
	private static final int REGISTRY_PORT = 4000;
	private static final String SERVER = "localhost";
	static AASRegistryProxy registry;
	static IConnectorFactory connectorFactory;
	static ConnectedAssetAdministrationShellManager manager;
	static ModelUrn kukaAASURN;
	static ModelUrn ur5eAASURN;
	static ModelUrn gripper2FG7AASURN;
	static ModelUrn gripperRG6AASURN;
	static ConnectedAssetAdministrationShell connectedAAS;
	static ConnectedAssetAdministrationShell connectedKukaAAS;
	static ConnectedAssetAdministrationShell connectedUR5eAAS;
	static ConnectedAssetAdministrationShell connectedGripper2FG7AAS;
	static ConnectedAssetAdministrationShell connectedGripperRG6AAS;
	static ConnectedAssetAdministrationShell connectedKukaAASExperimental;
	static ConnectedAssetAdministrationShell connectedUR5eAASExperimental;
	static ConnectedAssetAdministrationShell connectedGripper2FG7AASExperimental;
	static ConnectedAssetAdministrationShell connectedGripperRG6AASExperimental;

	static VABRegistryProxy registryProxy = new VABRegistryProxy("http://localhost:4005/dtframeworkVAB/directory/");
	// static VABConnectionManager vabConnectionManager = new
	// VABConnectionManager(registryProxy, new HTTPConnectorFactory());
	ConnectionFactory factory;
	Connection conn;
	Channel channel;
	DeliverCallback deliverCallback;
	/*static ZMQ.Socket zmqSocketUR;
	static ZMQ.Socket zmqSocketKuka;
	static ZMQ.Socket zmqSocketGripper2FG7;
	static ZMQ.Socket zmqSocketGripperRG6;
	public static ZMQ.Socket zmqSocketURWriting;
	public static ZMQ.Socket zmqSocketKukaWriting;
	public static ZMQ.Socket zmqSocketGripper2FG7Writing;
	public static ZMQ.Socket zmqSocketGripperRG6Writing;
	static ZContext context = new ZContext();*/
	public static String broker = "tcp://127.0.0.1:1883";
	public static String clientId = "BaSyx_DT_Framework_Pub";
	public static String readingClientId = "BaSyx_DT_Framework_Sub";
	public static MqttClient mqttClient;
	public static MqttAsyncClient readingMqttClient;
	public static final String ACTUALUR5ETOPIC = "dtflexcell/ur5e/actual";
	public static final String EXPERIMENTALUR5ETOPIC = "dtflexcell/ur5e/experimental";
	public static final String ACTUALKUKATOPIC = "dtflexcell/kuka/actual";
	public static final String EXPERIMENTALKUKATOPIC = "dtflexcell/kuka/experimental";
	public static final String ACTUAL2FG7TOPIC = "dtflexcell/2fg7/actual";
	public static final String EXPERIMENTAL2FG7TOPIC = "dtflexcell/2fg7/experimental";
	public static final String ACTUALRG6TOPIC = "dtflexcell/rg6/actual";
	public static final String EXPERIMENTALRG6TOPIC = "dtflexcell/rg6/experimental";
	public static final String ACTUALUR5ETOPICRECEIVE = "dtflexcell/ur5e/actual/+";
	public static final String EXPERIMENTALUR5ETOPICRECEIVE = "dtflexcell/ur5e/experimental/+";
	public static final String ACTUALKUKATOPICRECEIVE = "dtflexcell/kuka/actual/+";
	public static final String EXPERIMENTALKUKATOPICRECEIVE = "dtflexcell/kuka/experimental/+";
	public static final String ACTUAL2FG7TOPICRECEIVE = "dtflexcell/2fg7/actual/+";
	public static final String EXPERIMENTAL2FG7TOPICRECEIVE = "dtflexcell/2fg7/experimental/+";
	public static final String ACTUALRG6TOPICRECEIVE = "dtflexcell/rg6/actual/+";
	public static final String EXPERIMENTALRG6TOPICRECEIVE = "dtflexcell/rg6/experimental/+";
	public static MqttCallbackAAS callbackFunction = new MqttCallbackAAS();

	private static IModelProvider connectedKukaModel;
	private static IModelProvider connectedKukaModelExperimental;
	private static IModelProvider connectedRG6Model;
	private static IModelProvider connectedUR5eModel;
	private static IModelProvider connectedUR5eModelExperimental;
	private static IModelProvider connected2FG7Model;
	static VABConnectionManager vabConnectionManagerVABServer;
	static VABConnectionManager vabConnectionManager;

	static DTManager dtManagerEvents;

	public EventHandler() {

	}

	public static void init(DTManager dtManager) {
		dtManagerEvents = dtManager;
		// vabConnectionManagerVABServer = DTManager.vabConnectionManagerVABServer;
		// vabConnectionManager = DTManager.vabConnectionManager;

		// MQTT Client
		try {
			mqttClient = new MqttClient(broker, clientId);
			readingMqttClient = new MqttAsyncClient(broker, readingClientId);
			
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			// callback
			readingMqttClient.setCallback(callbackFunction);
			//IMqttToken conToken;
	        //conToken = mqttClient.connect(conToken);
			IMqttToken token = mqttClient.connectWithResult(connOpts);
			token.waitForCompletion();
			readingMqttClient.connect(connOpts, new IMqttActionListener() {
			    public void onSuccess(IMqttToken asyncActionToken) {
			    	
					try {
						readingMqttClient.subscribe(ACTUALUR5ETOPICRECEIVE,1);
						readingMqttClient.subscribe(EXPERIMENTALUR5ETOPICRECEIVE,1);
						readingMqttClient.subscribe(ACTUALKUKATOPICRECEIVE,1);
						readingMqttClient.subscribe(EXPERIMENTALKUKATOPICRECEIVE,1);
						readingMqttClient.subscribe(ACTUAL2FG7TOPICRECEIVE,1);
						readingMqttClient.subscribe(EXPERIMENTAL2FG7TOPICRECEIVE,1);
						readingMqttClient.subscribe(ACTUALRG6TOPICRECEIVE,1);
						readingMqttClient.subscribe(EXPERIMENTALRG6TOPICRECEIVE,1);
					} catch (MqttException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
			    }

			    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
			      exception.printStackTrace();
			    }
			});
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		

		// Kuka Actual
		// connectedKukaModel =
		// vabConnectionManagerVABServer.connectToVABElement("KUKA");

		// Kuka Experimental
		// connectedKukaModelExperimental =
		// vabConnectionManagerVABServer.connectToVABElement("KUKA_EXPERIMENTAL");

		// UR5e Actual
		// connectedUR5eModel =
		// vabConnectionManagerVABServer.connectToVABElement("UR5E");

		// UR5e Experimental
		// connectedUR5eModelExperimental =
		// vabConnectionManagerVABServer.connectToVABElement("UR5E_EXPERIMENTAL");

		// RG6
		// connectedRG6Model =
		// vabConnectionManagerVABServer.connectToVABElement("GRIPPERRG6");

		// 2FG7
		// connected2FG7Model =
		// vabConnectionManagerVABServer.connectToVABElement("GRIPPER2FG7");

		registry = new AASRegistryProxy(
				"http://" + SERVER + ":" + String.valueOf(REGISTRY_PORT) + "/registry/api/v1/registry");
		connectorFactory = new HTTPConnectorFactory();
		manager = new ConnectedAssetAdministrationShellManager(registry, connectorFactory);
		kukaAASURN = new ModelUrn("urn:dtexamples.AU-AAS.Kuka_AAS");
		connectedKukaAAS = manager.retrieveAAS(kukaAASURN);
		ur5eAASURN = new ModelUrn("urn:dtexamples.AU-AAS.UR5e_AAS");
		connectedUR5eAAS = manager.retrieveAAS(ur5eAASURN);
		gripper2FG7AASURN = new ModelUrn("urn:dtexamples.AU-AAS.Gripper2FG7_AAS");
		connectedGripper2FG7AAS = manager.retrieveAAS(gripper2FG7AASURN);
		gripperRG6AASURN = new ModelUrn("urn:dtexamples.AU-AAS.GripperRG6_AAS");
		connectedGripperRG6AAS = manager.retrieveAAS(gripperRG6AASURN);
		
		
		/******************* Not using ZMQ for now *****************/

		// Channels for reading
		/*zmqSocketUR = context.createSocket(ZMQ.SUB);
		zmqSocketUR.connect("tcp://localhost:5556");
		zmqSocketKuka = context.createSocket(ZMQ.SUB);
		zmqSocketKuka.connect("tcp://localhost:5557");
		zmqSocketGripper2FG7 = context.createSocket(ZMQ.SUB);
		zmqSocketGripper2FG7.connect("tcp://localhost:5560");
		zmqSocketGripperRG6 = context.createSocket(ZMQ.SUB);
		zmqSocketGripperRG6.connect("tcp://localhost:5561");
		String[] topicsZMQUR = { "actual_q_0", "actual_q_1", "actual_q_2", "actual_q_3", "actual_q_4", "actual_q_5" };
		for (String topic : topicsZMQUR) {
			zmqSocketUR.subscribe(topic);//
		}
		zmqSocketUR.subscribe("");//*/

		// zmqSocketKuka.subscribe("");//

		// Channels for writing
		/*zmqSocketURWriting = context.createSocket(ZMQ.PUB);
		zmqSocketURWriting.bind("tcp://localhost:5558");
		zmqSocketKukaWriting = context.createSocket(ZMQ.PUB);
		zmqSocketKukaWriting.bind("tcp://localhost:5559");
		zmqSocketGripper2FG7Writing = context.createSocket(ZMQ.PUB);
		zmqSocketGripper2FG7Writing.bind("tcp://localhost:5562");
		zmqSocketGripperRG6Writing = context.createSocket(ZMQ.PUB);
		zmqSocketGripperRG6Writing.bind("tcp://localhost:5563");*/

		// Kuka Reading Thread
		/*Thread kukaReadingThread = new Thread() {
			@Override
			public void run() {
				try {
					byte[] msg = zmqSocketKuka.recv();
					updateStatePropertyAASZMQ("kuka", msg);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		try {
			kukaReadingThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// UR5e Reading Thread
		Thread ur5eReadingThread = new Thread() {
			@Override
			public void run() {
				try {
					byte[] msg = zmqSocketUR.recv();
					System.out.println(msg);
					updateStatePropertyAASZMQ("ur5e", msg);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		try {
			ur5eReadingThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Gripper2FG7 Reading Thread
		Thread gripper2FG7ReadingThread = new Thread() {
			@Override
			public void run() {
				try {
					byte[] msg = zmqSocketGripper2FG7.recv();
					updateStatePropertyAASZMQ("gripper2FG7", msg);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		try {
			gripper2FG7ReadingThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// GripperRG6 Reading Thread
		Thread gripperRG6ReadingThread = new Thread() {
			@Override
			public void run() {
				try {
					byte[] msg = zmqSocketGripperRG6.recv();
					updateStatePropertyAASZMQ("gripperRG6", msg);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		try {
			gripperRG6ReadingThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Thread start
		kukaReadingThread.start();
		ur5eReadingThread.start();
		gripper2FG7ReadingThread.start();
		gripperRG6ReadingThread.start();
		*/
	}

	public static Object readAASVariable(String urn, String varName) {

		connectedAAS = manager.retrieveAAS(new ModelUrn(urn));
		Map<String, ISubmodel> submodels = connectedAAS.getSubmodels();
		ISubmodel connectedSM = submodels.get("OperationalData");
		ISubmodelElement seVariables = connectedSM.getSubmodelElements().get("Variables");
		Collection<ISubmodelElement> seVariablesCollection = (Collection<ISubmodelElement>) seVariables.getValue();
		Collection<IProperty> variablesCollection = new ArrayList<IProperty>();
		Map<String, IProperty> variablesMap = new HashMap<String, IProperty>();
		for (ISubmodelElement elem : seVariablesCollection) {
			variablesCollection.add((IProperty) elem);
			variablesMap.put(elem.getIdShort(), (IProperty) elem);
		}
		IProperty readingProperty = variablesMap.get(varName);
		// System.out.println(readingProperty.getSemanticId());
		Object value = 0.0;
		if (readingProperty.getValue() instanceof Double) {
			value = (Double) readingProperty.getValue();
		} else if (readingProperty.getValue() instanceof Integer) {
			value = (Integer) readingProperty.getValue();
		}
		return value;
	}
	
	public static Object readAASVariable(SBDigitalTwin dti, String varName,boolean actual) {
		Object value = 0.0;
		IAssetAdministrationShell actualDT = dti.getAAS();
		Map<String, IProperty> variablesMap = dti.getOperationalProperties();
		IProperty readingProperty = variablesMap.get(varName);
		if (readingProperty.getValue() instanceof Double) {
			value = (Double) readingProperty.getValue();
		} else if (readingProperty.getValue() instanceof Integer) {
			value = (Integer) readingProperty.getValue();
		}
		return value;
	}

	public static void sendAASCommand(String urn, String operationName, String vabElement) {
		connectedAAS = manager.retrieveAAS(new ModelUrn(urn));
		ISubmodel connectedODSubmodel = connectedAAS.getSubmodels().get("OperationalData");
		Collection<IOperation> connectedOperations = (Collection<IOperation>) (connectedODSubmodel
				.getSubmodelElement("Operations").getValue());
		Map<String, IOperation> connectedOperationsMap = new HashMap<String, IOperation>();
		for (ISubmodelElement op : connectedOperations) {
			connectedOperationsMap.put(op.getIdShort(), (IOperation) op);
		}
		ConnectedOperation connectedTmpOperation = (ConnectedOperation) connectedOperationsMap.get(operationName);

		IModelProvider connectedModel = vabConnectionManager.connectToVABElement(vabElement);
		connectedModel.invokeOperation(
				"aas/submodels/OperationalData/submodel/submodelElements/Operations/" + operationName + "/invoke");
	}

	/*public static void writeOnZmqSocket(int socketNmb, String message) {
		switch (socketNmb) {
		case 1:
			zmqSocketURWriting.send(message);
			break;
		case 2:
			zmqSocketKukaWriting.send(message);
			break;
		case 3:
			zmqSocketGripper2FG7Writing.send(message);
			break;
		case 4:
			zmqSocketGripperRG6Writing.send(message);
			break;
		}
	}*/

	public static void updatePropertyStateAAS(ConnectedAssetAdministrationShell connectedAAS, String topic,
			String pattern) {
		Map<String, ISubmodel> submodels = connectedAAS.getSubmodels();
		ISubmodel connectedSM = submodels.get("OperationalData");
		ISubmodelElement seVariables = connectedSM.getSubmodelElements().get("Variables");
		Collection<ISubmodelElement> seVariablesCollection = (Collection<ISubmodelElement>) seVariables.getValue();
		Map<String, IProperty> variablesMap = new HashMap<String, IProperty>();
		for (ISubmodelElement elem : seVariablesCollection) {
			variablesMap.put(elem.getIdShort(), (IProperty) elem);
		}
		IProperty tmpProperty;

		/*byte[] msg = zmqSocketKuka.recv(ZMQ.NOBLOCK);
		String msgStr = new String(msg, ZMQ.CHARSET);
		String[] msgSpl = msgStr.split(" ");
		String topicMsg = msgSpl[0];
		String contentMsg = msgSpl[1];
		if (topicMsg.contains(topic)) {
			String numberStr = topicMsg.replaceAll("[^0-9]", "");
			String wildCard = pattern + numberStr;
			tmpProperty = variablesMap.get(wildCard);
			tmpProperty.setValue(Double.valueOf(contentMsg));
		}*/

	}

	public static void updateStatePropertyAASZMQ(String AAS, byte[] msg) {
		// Kuka
		if (AAS.equals("kuka")) {
			connectedKukaAAS = manager.retrieveAAS(kukaAASURN);
			Map<String, ISubmodel> kukaSubmodels = connectedKukaAAS.getSubmodels();
			ISubmodel connectedKukaSM = kukaSubmodels.get("OperationalData");
			ISubmodelElement seKukaVariables = connectedKukaSM.getSubmodelElements().get("Variables");
			Collection<ISubmodelElement> seKukaVariablesCollection = (Collection<ISubmodelElement>) seKukaVariables
					.getValue();
			Map<String, IProperty> kukaVariablesMap = new HashMap<String, IProperty>();
			for (ISubmodelElement elem : seKukaVariablesCollection) {
				kukaVariablesMap.put(elem.getIdShort(), (IProperty) elem);
			}
			IProperty tmpPropertyKuka;

			try {
				String msgKukaStr = new String(msg, ZMQ.CHARSET);
				// System.out.println(msgKukaStr);
				String[] msgKukaSpl = msgKukaStr.split(" ");
				String topicKukaMsg = msgKukaSpl[0];
				String contentKukaMsg = msgKukaSpl[1];
				if (topicKukaMsg.contains("actual_q_")) {
					String numberStr = topicKukaMsg.replaceAll("[^0-9]", "");
					String wildCard = "Current_Joint_Position_" + numberStr;

					tmpPropertyKuka = kukaVariablesMap.get(wildCard);
					tmpPropertyKuka.setValue(Double.valueOf(contentKukaMsg));
				} else if (topicKukaMsg.contains("target_q_")) {
					String numberStr = topicKukaMsg.replaceAll("[^0-9]", "");
					String wildCard = "Target_Joint_Position_" + numberStr;

					tmpPropertyKuka = kukaVariablesMap.get(wildCard);
					tmpPropertyKuka.setValue(Double.valueOf(contentKukaMsg));
				} else if (topicKukaMsg.contains("measured_tq_")) {
					String numberStr = topicKukaMsg.replaceAll("[^0-9]", "");
					String wildCard = "Measured_Torque_Joint_" + numberStr;

					tmpPropertyKuka = kukaVariablesMap.get(wildCard);
					tmpPropertyKuka.setValue(Double.valueOf(contentKukaMsg));
				} else if (topicKukaMsg.contains("external_tq_")) {
					String numberStr = topicKukaMsg.replaceAll("[^0-9]", "");
					String wildCard = "External_Torque_Joint_" + numberStr;

					tmpPropertyKuka = kukaVariablesMap.get(wildCard);
					tmpPropertyKuka.setValue(Double.valueOf(contentKukaMsg));
				} else if (topicKukaMsg.contains("force_X")) {
					String wildCard = "Force_X";

					tmpPropertyKuka = kukaVariablesMap.get(wildCard);
					tmpPropertyKuka.setValue(Double.valueOf(contentKukaMsg));
				} else if (topicKukaMsg.contains("force_Y")) {
					String wildCard = "Force_Y";

					tmpPropertyKuka = kukaVariablesMap.get(wildCard);
					tmpPropertyKuka.setValue(Double.valueOf(contentKukaMsg));
				} else if (topicKukaMsg.contains("force_Z")) {
					String wildCard = "Force_Z";

					tmpPropertyKuka = kukaVariablesMap.get(wildCard);
					tmpPropertyKuka.setValue(Double.valueOf(contentKukaMsg));
				} else if (topicKukaMsg.contains("torque_X")) {
					String wildCard = "Torque_X";

					tmpPropertyKuka = kukaVariablesMap.get(wildCard);
					tmpPropertyKuka.setValue(Double.valueOf(contentKukaMsg));
				} else if (topicKukaMsg.contains("torque_Y")) {
					String wildCard = "Torque_Y";

					tmpPropertyKuka = kukaVariablesMap.get(wildCard);
					tmpPropertyKuka.setValue(Double.valueOf(contentKukaMsg));
				} else if (topicKukaMsg.contains("torque_Z")) {
					String wildCard = "Torque_Z";

					tmpPropertyKuka = kukaVariablesMap.get(wildCard);
					tmpPropertyKuka.setValue(Double.valueOf(contentKukaMsg));
				}
			} catch (Exception e) {

			}
		}
		// UR5e
		if (AAS.equals("ur5e")) {
			connectedUR5eAAS = manager.retrieveAAS(ur5eAASURN);
			Map<String, ISubmodel> ur5eSubmodels = connectedUR5eAAS.getSubmodels();
			ISubmodel connectedUR5eSM = ur5eSubmodels.get("OperationalData");
			ISubmodelElement seUR5eVariables = connectedUR5eSM.getSubmodelElements().get("Variables");
			Collection<ISubmodelElement> seUR5eVariablesCollection = (Collection<ISubmodelElement>) seUR5eVariables
					.getValue();
			Map<String, IProperty> ur5eVariablesMap = new HashMap<String, IProperty>();
			for (ISubmodelElement elem : seUR5eVariablesCollection) {
				ur5eVariablesMap.put(elem.getIdShort(), (IProperty) elem);
			}
			IProperty tmpPropertyUR;

			try {

				String msgURStr = new String(msg, ZMQ.CHARSET);
				// System.out.println(msgURStr);
				String[] msgURSpl = msgURStr.split(" ");
				String topicURMsg = msgURSpl[0];
				String contentURMsg = msgURSpl[1];
				// System.out.println(topicURMsg);
				// System.out.println(contentURMsg);

				if (topicURMsg.contains("actual_q_")) {
					String numberStr = topicURMsg.replaceAll("[^0-9]", "");
					String wildCard = "Current_Joint_Position_" + numberStr;

					tmpPropertyUR = ur5eVariablesMap.get(wildCard);
					tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
				} else if (topicURMsg.contains("target_q_")) {
					String numberStr = topicURMsg.replaceAll("[^0-9]", "");
					String wildCard = "Target_Joint_Position_" + numberStr;

					tmpPropertyUR = ur5eVariablesMap.get(wildCard);
					tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
				} else if (topicURMsg.contains("actual_qd_")) {
					String numberStr = topicURMsg.replaceAll("[^0-9]", "");
					String wildCard = "Current_Angular_Velocity_" + numberStr;

					tmpPropertyUR = ur5eVariablesMap.get(wildCard);
					tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
				} else if (topicURMsg.contains("target_qd_")) {
					String numberStr = topicURMsg.replaceAll("[^0-9]", "");
					String wildCard = "Target_Angular_Velocity_" + numberStr;

					tmpPropertyUR = ur5eVariablesMap.get(wildCard);
					tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
				} else if (topicURMsg.contains("actual_qdd_")) {
					String numberStr = topicURMsg.replaceAll("[^0-9]", "");
					String wildCard = "Current_Angular_Acceleration_" + numberStr;

					tmpPropertyUR = ur5eVariablesMap.get(wildCard);
					tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
				} else if (topicURMsg.contains("target_qdd_")) {
					String numberStr = topicURMsg.replaceAll("[^0-9]", "");
					String wildCard = "Target_Angular_Acceleration_" + numberStr;

					tmpPropertyUR = ur5eVariablesMap.get(wildCard);
					tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
				} else if (topicURMsg.contains("joint_temperatures_")) {
					String numberStr = topicURMsg.replaceAll("[^0-9]", "");
					String wildCard = "Current_Joint_Temperature_" + numberStr;

					tmpPropertyUR = ur5eVariablesMap.get(wildCard);
					tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
				} else if (topicURMsg.contains("actual_current_")) {
					String numberStr = topicURMsg.replaceAll("[^0-9]", "");
					String wildCard = "Actual_Current_" + numberStr;

					tmpPropertyUR = ur5eVariablesMap.get(wildCard);
					tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
				} else if (topicURMsg.contains("target_current_")) {
					String numberStr = topicURMsg.replaceAll("[^0-9]", "");
					String wildCard = "Target_Current_" + numberStr;

					tmpPropertyUR = ur5eVariablesMap.get(wildCard);
					tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
				} else if (topicURMsg.contains("actual_TCP_force_")) {
					String numberStr = topicURMsg.replaceAll("[^0-9]", "");
					String wildCard = "Current_TCP_Force_" + numberStr;

					tmpPropertyUR = ur5eVariablesMap.get(wildCard);
					tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
				} else if (topicURMsg.contains("target_moment_")) {
					String numberStr = topicURMsg.replaceAll("[^0-9]", "");
					String wildCard = "Target_Moment_" + numberStr;

					tmpPropertyUR = ur5eVariablesMap.get(wildCard);
					tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
				} else if (topicURMsg.contains("actual_joint_voltage_")) {
					String numberStr = topicURMsg.replaceAll("[^0-9]", "");
					String wildCard = "Current_Joint_Voltage_" + numberStr;

					tmpPropertyUR = ur5eVariablesMap.get(wildCard);
					tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
				} else if (topicURMsg.contains("actual_tool_accelerometer_")) {
					String numberStr = topicURMsg.replaceAll("[^0-9]", "");
					String wildCard = "Current_Tool_Accelerometer_" + numberStr;

					tmpPropertyUR = ur5eVariablesMap.get(wildCard);
					tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
				} else if (topicURMsg.contains("actual_TCP_pose_")) {
					String numberStr = topicURMsg.replaceAll("[^0-9]", "");
					String wildCard = "Current_TCP_Pose_" + numberStr;

					tmpPropertyUR = ur5eVariablesMap.get(wildCard);
					tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
				} else if (topicURMsg.contains("target_TCP_pose_")) {
					String numberStr = topicURMsg.replaceAll("[^0-9]", "");
					String wildCard = "Target_TCP_Pose_" + numberStr;

					tmpPropertyUR = ur5eVariablesMap.get(wildCard);
					tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
				} else if (topicURMsg.contains("actual_TCP_speed_")) {
					String numberStr = topicURMsg.replaceAll("[^0-9]", "");
					String wildCard = "Current_TCP_Speed_" + numberStr;

					tmpPropertyUR = ur5eVariablesMap.get(wildCard);
					tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
				} else if (topicURMsg.contains("target_TCP_speed_")) {
					String numberStr = topicURMsg.replaceAll("[^0-9]", "");
					String wildCard = "Target_TCP_Speed_" + numberStr;

					tmpPropertyUR = ur5eVariablesMap.get(wildCard);
					tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
				} else if (topicURMsg.contains("actual_TCP_force_")) {
					String numberStr = topicURMsg.replaceAll("[^0-9]", "");
					String wildCard = "Current_TCP_Force_" + numberStr;

					tmpPropertyUR = ur5eVariablesMap.get(wildCard);
					tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
				} else if (topicURMsg.contains("target_TCP_force_")) {
					String numberStr = topicURMsg.replaceAll("[^0-9]", "");
					String wildCard = "Target_TCP_Force_" + numberStr;

					tmpPropertyUR = ur5eVariablesMap.get(wildCard);
					tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
				} else if (topicURMsg.contains("actual_momentum")) {
					String wildCard = "Current_Momentum";

					tmpPropertyUR = ur5eVariablesMap.get(wildCard);
					tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
				} else if (topicURMsg.contains("actual_robot_current")) {
					String wildCard = "Actual_Robot_Current";

					tmpPropertyUR = ur5eVariablesMap.get(wildCard);
					tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
				} else if (topicURMsg.contains("actual_robot_voltage")) {
					String wildCard = "Actual_Robot_Voltage";

					tmpPropertyUR = ur5eVariablesMap.get(wildCard);
					tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
				} else if (topicURMsg.contains("actual_robot_voltage")) {
					String wildCard = "Actual_Robot_Voltage";

					tmpPropertyUR = ur5eVariablesMap.get(wildCard);
					tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
				}
			} catch (Exception e) {

			}
		}

		// Gripper 2FG7
		if (AAS.equals("gripper2FG7")) {
			connectedGripper2FG7AAS = manager.retrieveAAS(gripper2FG7AASURN);
			Map<String, ISubmodel> gripper2FG7Submodels = connectedGripper2FG7AAS.getSubmodels();
			ISubmodel connectedGripper2FG7SM = gripper2FG7Submodels.get("OperationalData");
			ISubmodelElement seGripper2FG7Variables = connectedGripper2FG7SM.getSubmodelElements().get("Variables");
			Collection<ISubmodelElement> seGripper2FG7VariablesCollection = (Collection<ISubmodelElement>) seGripper2FG7Variables
					.getValue();
			Map<String, IProperty> gripper2FG7VariablesMap = new HashMap<String, IProperty>();
			for (ISubmodelElement elem : seGripper2FG7VariablesCollection) {
				gripper2FG7VariablesMap.put(elem.getIdShort(), (IProperty) elem);
			}
			IProperty tmpPropertyGripper2FG7;

			try {
				String msgGripper2FG7Str = new String(msg, ZMQ.CHARSET);
				System.out.println(msgGripper2FG7Str);
				String[] msgGripper2FG7Spl = msgGripper2FG7Str.split(" ");
				String topicGripper2FG7Msg = msgGripper2FG7Spl[0];
				String contentGripper2FG7Msg = msgGripper2FG7Spl[1];
				System.out.println(topicGripper2FG7Msg);
				System.out.println(contentGripper2FG7Msg);

				if (topicGripper2FG7Msg.contains("current_force")) {
					String wildCard = "Current_Force";

					tmpPropertyGripper2FG7 = gripper2FG7VariablesMap.get(wildCard);
					tmpPropertyGripper2FG7.setValue(Double.valueOf(contentGripper2FG7Msg));
				} else if (topicGripper2FG7Msg.contains("current_diameter")) {
					String wildCard = "Current_Diameter";

					tmpPropertyGripper2FG7 = gripper2FG7VariablesMap.get(wildCard);
					tmpPropertyGripper2FG7.setValue(Double.valueOf(contentGripper2FG7Msg));
				}
			} catch (Exception e) {

			}
		}

		// Gripper RG6
		if (AAS.equals("gripperRG6")) {
			connectedGripperRG6AAS = manager.retrieveAAS(gripperRG6AASURN);
			Map<String, ISubmodel> gripperRG6Submodels = connectedGripperRG6AAS.getSubmodels();
			ISubmodel connectedGripperRG6SM = gripperRG6Submodels.get("OperationalData");
			ISubmodelElement seGripperRG6Variables = connectedGripperRG6SM.getSubmodelElements().get("Variables");
			Collection<ISubmodelElement> seGripperRG6VariablesCollection = (Collection<ISubmodelElement>) seGripperRG6Variables
					.getValue();
			Map<String, IProperty> gripperRG6VariablesMap = new HashMap<String, IProperty>();
			for (ISubmodelElement elem : seGripperRG6VariablesCollection) {
				gripperRG6VariablesMap.put(elem.getIdShort(), (IProperty) elem);
			}
			IProperty tmpPropertyGripperRG6;

			try {
				String msgGripperRG6Str = new String(msg, ZMQ.CHARSET);
				System.out.println(msgGripperRG6Str);
				String[] msgGripperRG6Spl = msgGripperRG6Str.split(" ");
				String topicGripperRG6Msg = msgGripperRG6Spl[0];
				String contentGripperRG6Msg = msgGripperRG6Spl[1];
				System.out.println(topicGripperRG6Msg);
				System.out.println(contentGripperRG6Msg);

				if (topicGripperRG6Msg.contains("current_force")) {
					String wildCard = "Current_Force";

					tmpPropertyGripperRG6 = gripperRG6VariablesMap.get(wildCard);
					tmpPropertyGripperRG6.setValue(Double.valueOf(contentGripperRG6Msg));
				} else if (topicGripperRG6Msg.contains("current_width")) {
					String wildCard = "Current_Width";

					tmpPropertyGripperRG6 = gripperRG6VariablesMap.get(wildCard);
					tmpPropertyGripperRG6.setValue(Double.valueOf(contentGripperRG6Msg));
				}
			} catch (Exception e) {

			}
		}

	}

	public static void updateStatePropertyAASMQTT(String topic, String message) {
		IProperty tmpProperty;
		String varTopic = topic.split("/")[3];
		if (topic.contains("kuka")) {
			Map<String, IProperty> kukaVariablesMap = dtManagerEvents.availableTwins.get("Kuka_LBR_iiwa7_AAS").getOperationalProperties();


			if (topic.contains("kuka/actual")) {
				if (topic.contains("actual_q_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Current_Joint_Position_" + numberStr;

					tmpProperty = kukaVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("target_q_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Target_Joint_Position_" + numberStr;

					tmpProperty = kukaVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("measured_tq_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Measured_Torque_Joint_" + numberStr;

					tmpProperty = kukaVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("external_tq_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "External_Torque_Joint_" + numberStr;

					tmpProperty = kukaVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("force_X")) {
					String wildCard = "Force_X";

					tmpProperty = kukaVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("force_Y")) {
					String wildCard = "Force_Y";

					tmpProperty = kukaVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("force_Z")) {
					String wildCard = "Force_Z";

					tmpProperty = kukaVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("torque_X")) {
					String wildCard = "Torque_X";

					tmpProperty = kukaVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("torque_Y")) {
					String wildCard = "Torque_Y";

					tmpProperty = kukaVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("torque_Z")) {
					String wildCard = "Torque_Z";

					tmpProperty = kukaVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				}

			} else if (topic.contains("experimental")) {
				if (topic.contains("actual_q_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Current_Joint_Position_" + numberStr;

					tmpProperty = kukaVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("target_q_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Target_Joint_Position_" + numberStr;

					tmpProperty = kukaVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("measured_tq_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Measured_Torque_Joint_" + numberStr;

					tmpProperty = kukaVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("external_tq_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "External_Torque_Joint_" + numberStr;

					tmpProperty = kukaVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("force_X")) {
					String wildCard = "Force_X";

					tmpProperty = kukaVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("force_Y")) {
					String wildCard = "Force_Y";

					tmpProperty = kukaVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("force_Z")) {
					String wildCard = "Force_Z";

					tmpProperty = kukaVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("torque_X")) {
					String wildCard = "Torque_X";

					tmpProperty = kukaVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("torque_Y")) {
					String wildCard = "Torque_Y";

					tmpProperty = kukaVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("torque_Z")) {
					String wildCard = "Torque_Z";

					tmpProperty = kukaVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				}
			}

		} else if (topic.contains("ur5e")) {
			Map<String, IProperty> ur5eVariablesMap = dtManagerEvents.availableTwins.get("UR5e_AAS")
					.getOperationalProperties();

			
			if (topic.contains("ur5e/actual")) {
				if (topic.contains("actual_q_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Current_Joint_Position_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("target_q_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Target_Joint_Position_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_qd_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Current_Angular_Velocity_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("target_qd_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Target_Angular_Velocity_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_qdd_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Current_Angular_Acceleration_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("target_qdd_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Target_Angular_Acceleration_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("joint_temperatures_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Current_Joint_Temperature_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_current_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Actual_Current_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("target_current_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Target_Current_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_TCP_force_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Current_TCP_Force_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("target_moment_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Target_Moment_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_joint_voltage_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Current_Joint_Voltage_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_tool_accelerometer_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Current_Tool_Accelerometer_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_TCP_pose_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Current_TCP_Pose_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("target_TCP_pose_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Target_TCP_Pose_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_TCP_speed_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Current_TCP_Speed_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("target_TCP_speed_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Target_TCP_Speed_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_TCP_force_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Current_TCP_Force_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("target_TCP_force_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Target_TCP_Force_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_momentum")) {
					String wildCard = "Current_Momentum";

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_robot_current")) {
					String wildCard = "Actual_Robot_Current";

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_robot_voltage")) {
					String wildCard = "Actual_Robot_Voltage";

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_robot_voltage")) {
					String wildCard = "Actual_Robot_Voltage";

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				}
			} else if (topic.contains("experimental")) {
				if (topic.contains("actual_q_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Current_Joint_Position_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("target_q_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Target_Joint_Position_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_qd_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Current_Angular_Velocity_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("target_qd_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Target_Angular_Velocity_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_qdd_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Current_Angular_Acceleration_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("target_qdd_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Target_Angular_Acceleration_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("joint_temperatures_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Current_Joint_Temperature_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_current_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Actual_Current_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("target_current_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Target_Current_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_TCP_force_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Current_TCP_Force_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("target_moment_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Target_Moment_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_joint_voltage_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Current_Joint_Voltage_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_tool_accelerometer_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Current_Tool_Accelerometer_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_TCP_pose_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Current_TCP_Pose_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("target_TCP_pose_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Target_TCP_Pose_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_TCP_speed_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Current_TCP_Speed_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("target_TCP_speed_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Target_TCP_Speed_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_TCP_force_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Current_TCP_Force_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("target_TCP_force_")) {
					String numberStr = varTopic.replaceAll("[^0-9]", "");
					String wildCard = "Target_TCP_Force_" + numberStr;

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_momentum")) {
					String wildCard = "Current_Momentum";

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_robot_current")) {
					String wildCard = "Actual_Robot_Current";

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_robot_voltage")) {
					String wildCard = "Actual_Robot_Voltage";

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				} else if (topic.contains("actual_robot_voltage")) {
					String wildCard = "Actual_Robot_Voltage";

					tmpProperty = ur5eVariablesMap.get(wildCard);
					tmpProperty.setValue(Double.valueOf(message));
				}
			} else if (topic.contains("2fg7")) {
				Map<String, IProperty> gripper2FG7VariablesMap = dtManagerEvents.availableTwins.get("Gripper2FG7_AAS")
						.getOperationalProperties();

				if (topic.contains("2fg7/actual")) {

				} else if (topic.contains("experimental")) {

				}
			} else if (topic.contains("rg6")) {
				Map<String, IProperty> gripper2FG7VariablesMap = dtManagerEvents.availableTwins.get("GripperRG6_AAS")
						.getOperationalProperties();

				if (topic.contains("rg6/actual")) {

				} else if (topic.contains("experimental")) {

				}
			}
		}
	}

	public static void updateStateAllAASMQTT(String topic, String message) {
		// Kuka
		Map<String, ISubmodel> kukaSubmodels = connectedKukaAAS.getSubmodels();
		ISubmodel connectedKukaSM = kukaSubmodels.get("OperationalData");
		ISubmodelElement seKukaVariables = connectedKukaSM.getSubmodelElements().get("Variables");
		Collection<ISubmodelElement> seKukaVariablesCollection = (Collection<ISubmodelElement>) seKukaVariables
				.getValue();
		Map<String, IProperty> kukaVariablesMap = new HashMap<String, IProperty>();
		for (ISubmodelElement elem : seKukaVariablesCollection) {
			kukaVariablesMap.put(elem.getIdShort(), (IProperty) elem);
		}
		IProperty tmpPropertyKuka;

		try {
			/*byte[] msgKuka = zmqSocketKuka.recv(ZMQ.NOBLOCK);
			String msgKukaStr = new String(msgKuka, ZMQ.CHARSET);
			// System.out.println(msgKukaStr);
			String[] msgKukaSpl = msgKukaStr.split(" ");
			String topicKukaMsg = msgKukaSpl[0];
			String contentKukaMsg = msgKukaSpl[1];
			if (topicKukaMsg.contains("actual_q_")) {
				String numberStr = topicKukaMsg.replaceAll("[^0-9]", "");
				String wildCard = "Current_Joint_Position_" + numberStr;

				tmpPropertyKuka = kukaVariablesMap.get(wildCard);
				tmpPropertyKuka.setValue(Double.valueOf(contentKukaMsg));
			} else if (topicKukaMsg.contains("target_q_")) {
				String numberStr = topicKukaMsg.replaceAll("[^0-9]", "");
				String wildCard = "Target_Joint_Position_" + numberStr;

				tmpPropertyKuka = kukaVariablesMap.get(wildCard);
				tmpPropertyKuka.setValue(Double.valueOf(contentKukaMsg));
			} else if (topicKukaMsg.contains("measured_tq_")) {
				String numberStr = topicKukaMsg.replaceAll("[^0-9]", "");
				String wildCard = "Measured_Torque_Joint_" + numberStr;

				tmpPropertyKuka = kukaVariablesMap.get(wildCard);
				tmpPropertyKuka.setValue(Double.valueOf(contentKukaMsg));
			} else if (topicKukaMsg.contains("external_tq_")) {
				String numberStr = topicKukaMsg.replaceAll("[^0-9]", "");
				String wildCard = "External_Torque_Joint_" + numberStr;

				tmpPropertyKuka = kukaVariablesMap.get(wildCard);
				tmpPropertyKuka.setValue(Double.valueOf(contentKukaMsg));
			} else if (topicKukaMsg.contains("force_X")) {
				String wildCard = "Force_X";

				tmpPropertyKuka = kukaVariablesMap.get(wildCard);
				tmpPropertyKuka.setValue(Double.valueOf(contentKukaMsg));
			} else if (topicKukaMsg.contains("force_Y")) {
				String wildCard = "Force_Y";

				tmpPropertyKuka = kukaVariablesMap.get(wildCard);
				tmpPropertyKuka.setValue(Double.valueOf(contentKukaMsg));
			} else if (topicKukaMsg.contains("force_Z")) {
				String wildCard = "Force_Z";

				tmpPropertyKuka = kukaVariablesMap.get(wildCard);
				tmpPropertyKuka.setValue(Double.valueOf(contentKukaMsg));
			} else if (topicKukaMsg.contains("torque_X")) {
				String wildCard = "Torque_X";

				tmpPropertyKuka = kukaVariablesMap.get(wildCard);
				tmpPropertyKuka.setValue(Double.valueOf(contentKukaMsg));
			} else if (topicKukaMsg.contains("torque_Y")) {
				String wildCard = "Torque_Y";

				tmpPropertyKuka = kukaVariablesMap.get(wildCard);
				tmpPropertyKuka.setValue(Double.valueOf(contentKukaMsg));
			} else if (topicKukaMsg.contains("torque_Z")) {
				String wildCard = "Torque_Z";

				tmpPropertyKuka = kukaVariablesMap.get(wildCard);
				tmpPropertyKuka.setValue(Double.valueOf(contentKukaMsg));
			}
		} catch (Exception e) {

		}

		// UR5e
		Map<String, ISubmodel> ur5eSubmodels = connectedUR5eAAS.getSubmodels();
		ISubmodel connectedUR5eSM = ur5eSubmodels.get("OperationalData");
		ISubmodelElement seUR5eVariables = connectedUR5eSM.getSubmodelElements().get("Variables");
		Collection<ISubmodelElement> seUR5eVariablesCollection = (Collection<ISubmodelElement>) seUR5eVariables
				.getValue();
		Map<String, IProperty> ur5eVariablesMap = new HashMap<String, IProperty>();
		for (ISubmodelElement elem : seUR5eVariablesCollection) {
			ur5eVariablesMap.put(elem.getIdShort(), (IProperty) elem);
		}
		IProperty tmpPropertyUR;

		try {
			byte[] msgUR = zmqSocketUR.recv(ZMQ.NOBLOCK);
			String msgURStr = new String(msgUR, ZMQ.CHARSET);
			// System.out.println(msgURStr);
			String[] msgURSpl = msgURStr.split(" ");
			String topicURMsg = msgURSpl[0];
			String contentURMsg = msgURSpl[1];
			// System.out.println(topicURMsg);
			// System.out.println(contentURMsg);

			if (topicURMsg.contains("actual_q_")) {
				String numberStr = topicURMsg.replaceAll("[^0-9]", "");
				String wildCard = "Current_Joint_Position_" + numberStr;

				tmpPropertyUR = ur5eVariablesMap.get(wildCard);
				tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
			} else if (topicURMsg.contains("target_q_")) {
				String numberStr = topicURMsg.replaceAll("[^0-9]", "");
				String wildCard = "Target_Joint_Position_" + numberStr;

				tmpPropertyUR = ur5eVariablesMap.get(wildCard);
				tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
			} else if (topicURMsg.contains("actual_qd_")) {
				String numberStr = topicURMsg.replaceAll("[^0-9]", "");
				String wildCard = "Current_Angular_Velocity_" + numberStr;

				tmpPropertyUR = ur5eVariablesMap.get(wildCard);
				tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
			} else if (topicURMsg.contains("target_qd_")) {
				String numberStr = topicURMsg.replaceAll("[^0-9]", "");
				String wildCard = "Target_Angular_Velocity_" + numberStr;

				tmpPropertyUR = ur5eVariablesMap.get(wildCard);
				tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
			} else if (topicURMsg.contains("actual_qdd_")) {
				String numberStr = topicURMsg.replaceAll("[^0-9]", "");
				String wildCard = "Current_Angular_Acceleration_" + numberStr;

				tmpPropertyUR = ur5eVariablesMap.get(wildCard);
				tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
			} else if (topicURMsg.contains("target_qdd_")) {
				String numberStr = topicURMsg.replaceAll("[^0-9]", "");
				String wildCard = "Target_Angular_Acceleration_" + numberStr;

				tmpPropertyUR = ur5eVariablesMap.get(wildCard);
				tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
			} else if (topicURMsg.contains("joint_temperatures_")) {
				String numberStr = topicURMsg.replaceAll("[^0-9]", "");
				String wildCard = "Current_Joint_Temperature_" + numberStr;

				tmpPropertyUR = ur5eVariablesMap.get(wildCard);
				tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
			} else if (topicURMsg.contains("actual_current_")) {
				String numberStr = topicURMsg.replaceAll("[^0-9]", "");
				String wildCard = "Actual_Current_" + numberStr;

				tmpPropertyUR = ur5eVariablesMap.get(wildCard);
				tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
			} else if (topicURMsg.contains("target_current_")) {
				String numberStr = topicURMsg.replaceAll("[^0-9]", "");
				String wildCard = "Target_Current_" + numberStr;

				tmpPropertyUR = ur5eVariablesMap.get(wildCard);
				tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
			} else if (topicURMsg.contains("actual_TCP_force_")) {
				String numberStr = topicURMsg.replaceAll("[^0-9]", "");
				String wildCard = "Current_TCP_Force_" + numberStr;

				tmpPropertyUR = ur5eVariablesMap.get(wildCard);
				tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
			} else if (topicURMsg.contains("target_moment_")) {
				String numberStr = topicURMsg.replaceAll("[^0-9]", "");
				String wildCard = "Target_Moment_" + numberStr;

				tmpPropertyUR = ur5eVariablesMap.get(wildCard);
				tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
			} else if (topicURMsg.contains("actual_joint_voltage_")) {
				String numberStr = topicURMsg.replaceAll("[^0-9]", "");
				String wildCard = "Current_Joint_Voltage_" + numberStr;

				tmpPropertyUR = ur5eVariablesMap.get(wildCard);
				tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
			} else if (topicURMsg.contains("actual_tool_accelerometer_")) {
				String numberStr = topicURMsg.replaceAll("[^0-9]", "");
				String wildCard = "Current_Tool_Accelerometer_" + numberStr;

				tmpPropertyUR = ur5eVariablesMap.get(wildCard);
				tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
			} else if (topicURMsg.contains("actual_TCP_pose_")) {
				String numberStr = topicURMsg.replaceAll("[^0-9]", "");
				String wildCard = "Current_TCP_Pose_" + numberStr;

				tmpPropertyUR = ur5eVariablesMap.get(wildCard);
				tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
			} else if (topicURMsg.contains("target_TCP_pose_")) {
				String numberStr = topicURMsg.replaceAll("[^0-9]", "");
				String wildCard = "Target_TCP_Pose_" + numberStr;

				tmpPropertyUR = ur5eVariablesMap.get(wildCard);
				tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
			} else if (topicURMsg.contains("actual_TCP_speed_")) {
				String numberStr = topicURMsg.replaceAll("[^0-9]", "");
				String wildCard = "Current_TCP_Speed_" + numberStr;

				tmpPropertyUR = ur5eVariablesMap.get(wildCard);
				tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
			} else if (topicURMsg.contains("target_TCP_speed_")) {
				String numberStr = topicURMsg.replaceAll("[^0-9]", "");
				String wildCard = "Target_TCP_Speed_" + numberStr;

				tmpPropertyUR = ur5eVariablesMap.get(wildCard);
				tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
			} else if (topicURMsg.contains("actual_TCP_force_")) {
				String numberStr = topicURMsg.replaceAll("[^0-9]", "");
				String wildCard = "Current_TCP_Force_" + numberStr;

				tmpPropertyUR = ur5eVariablesMap.get(wildCard);
				tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
			} else if (topicURMsg.contains("target_TCP_force_")) {
				String numberStr = topicURMsg.replaceAll("[^0-9]", "");
				String wildCard = "Target_TCP_Force_" + numberStr;

				tmpPropertyUR = ur5eVariablesMap.get(wildCard);
				tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
			} else if (topicURMsg.contains("actual_momentum")) {
				String wildCard = "Current_Momentum";

				tmpPropertyUR = ur5eVariablesMap.get(wildCard);
				tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
			} else if (topicURMsg.contains("actual_robot_current")) {
				String wildCard = "Actual_Robot_Current";

				tmpPropertyUR = ur5eVariablesMap.get(wildCard);
				tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
			} else if (topicURMsg.contains("actual_robot_voltage")) {
				String wildCard = "Actual_Robot_Voltage";

				tmpPropertyUR = ur5eVariablesMap.get(wildCard);
				tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
			} else if (topicURMsg.contains("actual_robot_voltage")) {
				String wildCard = "Actual_Robot_Voltage";

				tmpPropertyUR = ur5eVariablesMap.get(wildCard);
				tmpPropertyUR.setValue(Double.valueOf(contentURMsg));
			}
		} catch (Exception e) {

		}

		// Gripper 2FG7
		Map<String, ISubmodel> gripper2FG7Submodels = connectedGripper2FG7AAS.getSubmodels();
		ISubmodel connectedGripper2FG7SM = gripper2FG7Submodels.get("OperationalData");
		ISubmodelElement seGripper2FG7Variables = connectedGripper2FG7SM.getSubmodelElements().get("Variables");
		Collection<ISubmodelElement> seGripper2FG7VariablesCollection = (Collection<ISubmodelElement>) seGripper2FG7Variables
				.getValue();
		Map<String, IProperty> gripper2FG7VariablesMap = new HashMap<String, IProperty>();
		for (ISubmodelElement elem : seGripper2FG7VariablesCollection) {
			gripper2FG7VariablesMap.put(elem.getIdShort(), (IProperty) elem);
		}
		IProperty tmpPropertyGripper2FG7;

		try {
			byte[] msgGripper2FG7 = zmqSocketGripper2FG7.recv(ZMQ.NOBLOCK);
			String msgGripper2FG7Str = new String(msgGripper2FG7, ZMQ.CHARSET);
			System.out.println(msgGripper2FG7Str);
			String[] msgGripper2FG7Spl = msgGripper2FG7Str.split(" ");
			String topicGripper2FG7Msg = msgGripper2FG7Spl[0];
			String contentGripper2FG7Msg = msgGripper2FG7Spl[1];
			System.out.println(topicGripper2FG7Msg);
			System.out.println(contentGripper2FG7Msg);

			if (topicGripper2FG7Msg.contains("current_force")) {
				String wildCard = "Current_Force";

				tmpPropertyGripper2FG7 = gripper2FG7VariablesMap.get(wildCard);
				tmpPropertyGripper2FG7.setValue(Double.valueOf(contentGripper2FG7Msg));
			} else if (topicGripper2FG7Msg.contains("current_diameter")) {
				String wildCard = "Current_Diameter";

				tmpPropertyGripper2FG7 = gripper2FG7VariablesMap.get(wildCard);
				tmpPropertyGripper2FG7.setValue(Double.valueOf(contentGripper2FG7Msg));
			}
		} catch (Exception e) {

		}

		// Gripper RG6
		Map<String, ISubmodel> gripperRG6Submodels = connectedGripperRG6AAS.getSubmodels();
		ISubmodel connectedGripperRG6SM = gripperRG6Submodels.get("OperationalData");
		ISubmodelElement seGripperRG6Variables = connectedGripperRG6SM.getSubmodelElements().get("Variables");
		Collection<ISubmodelElement> seGripperRG6VariablesCollection = (Collection<ISubmodelElement>) seGripperRG6Variables
				.getValue();
		Map<String, IProperty> gripperRG6VariablesMap = new HashMap<String, IProperty>();
		for (ISubmodelElement elem : seGripperRG6VariablesCollection) {
			gripperRG6VariablesMap.put(elem.getIdShort(), (IProperty) elem);
		}
		IProperty tmpPropertyGripperRG6;

		try {
			byte[] msgGripperRG6 = zmqSocketGripperRG6.recv(ZMQ.NOBLOCK);
			String msgGripperRG6Str = new String(msgGripperRG6, ZMQ.CHARSET);
			System.out.println(msgGripperRG6Str);
			String[] msgGripperRG6Spl = msgGripperRG6Str.split(" ");
			String topicGripperRG6Msg = msgGripperRG6Spl[0];
			String contentGripperRG6Msg = msgGripperRG6Spl[1];
			System.out.println(topicGripperRG6Msg);
			System.out.println(contentGripperRG6Msg);

			if (topicGripperRG6Msg.contains("current_force")) {
				String wildCard = "Current_Force";

				tmpPropertyGripperRG6 = gripperRG6VariablesMap.get(wildCard);
				tmpPropertyGripperRG6.setValue(Double.valueOf(contentGripperRG6Msg));
			} else if (topicGripperRG6Msg.contains("current_width")) {
				String wildCard = "Current_Width";

				tmpPropertyGripperRG6 = gripperRG6VariablesMap.get(wildCard);
				tmpPropertyGripperRG6.setValue(Double.valueOf(contentGripperRG6Msg));
			}*/
		} catch (Exception e) {

		}
	}

	static class MqttCallbackAAS implements MqttCallback {
		public MqttCallbackAAS() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public void connectionLost(Throwable cause) {
			// TODO Auto-generated method stub

		}

		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception {
			String messageStr = String.valueOf(message);
			//System.out.println(topic);
			//System.out.println(messageStr);
			updateStatePropertyAASMQTT(topic, messageStr);

		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken token) {
			// TODO Auto-generated method stub

		}

	}
}
