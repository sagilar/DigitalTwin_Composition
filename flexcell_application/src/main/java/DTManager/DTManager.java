package DTManager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

import javax.servlet.http.HttpServlet;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.aas.restapi.AASModelProvider;
import org.eclipse.basyx.aas.restapi.MultiSubmodelProvider;
import org.eclipse.basyx.components.aas.aasx.AASXPackageManager;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.operation.ConnectedOperation;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.support.bundle.AASBundle;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxHTTPServer;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;
import org.eclipse.basyx.vab.registry.api.IVABRegistryService;
import org.eclipse.basyx.vab.registry.memory.VABInMemoryRegistry;
import org.eclipse.basyx.vab.registry.proxy.VABRegistryProxy;
import org.eclipse.basyx.vab.registry.restapi.VABRegistryModelProvider;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.protege.owl.codegeneration.inference.CodeGenerationInference;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swrlapi.core.SWRLRuleEngine;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.xml.sax.SAXException;

import CompositionManager.CopiableAASBundle;
import CompositionManager.SBDigitalTwin;
import SkillBasedDTOntology.DigitalTwin;
import SkillBasedDTOntology.SkillBasedDTOntology;
import dtflexcell.DTFlexCellMain;
import dtflexcell.EventHandler;

public class DTManager {
	private static final String SERVER = "localhost";
	private static final int AAS_PORT = 4001;
	private static final int REGISTRY_PORT = 4000;
	private static final String CONTEXT_PATH = "dtframework";
	private final static String url = "jdbc:postgresql://localhost/basyx-apps";
	private final static String user = "postgres";
	private final static String password = "admin";
	private static Connection conn;
	
	IVABRegistryService directory;
	IModelProvider directoryProvider;
	HttpServlet directoryServlet;
	IVABRegistryService directoryRegistry;
	IModelProvider directoryRegistryProvider;
	HttpServlet directoryRegistryServlet;
	public static VABConnectionManager vabConnectionManager;
	public static VABConnectionManager vabConnectionManagerVABServer;
	ConnectedAssetAdministrationShellManager manager;
	String aasServerURL;
	final String REGISTRYPATH;
	AASRegistryProxy registry;
	private BaSyxContext context;
	
	//Twins
	public Map<String,SBDigitalTwin> availableTwins = new HashMap<String,SBDigitalTwin>();
	
	//Ontology
	public SkillBasedDTOntology semanticNetwork;
	CodeGenerationInference inference;
	private OWLOntology owlOntology;
	public OWLOntologyManager ontologyManager;
	public OWLDataFactory owlDataFactory;
	//SWRLAPI
    public SWRLRuleEngine swrlEngine;
    public SQWRLQueryEngine queryEngine;

	public DTManager() {
		
		// Ontology
		OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = null;
		try {
			ontology = ontologyManager.loadOntologyFromOntologyDocument(new File("cooperative_DTs.owl"));
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		ontologyManager.getOntologyFormat(ontology).asPrefixOWLOntologyFormat().setDefaultPrefix("http://www.semanticweb.org/ontologies/cooperativeDTs#");
		
		
        swrlEngine = SWRLAPIFactory.createSWRLRuleEngine(ontology);
        queryEngine = SWRLAPIFactory.createSQWRLQueryEngine(ontology);
		
        /**
         **** Instantiating the ontology *
         **/
        semanticNetwork = new SkillBasedDTOntology(ontology);
        this.owlOntology = ontology;
		this.inference = semanticNetwork.getInference();;
		this.ontologyManager = ontology.getOWLOntologyManager();
		this.owlDataFactory = ontologyManager.getOWLDataFactory();
		
		// AAS
		REGISTRYPATH = "http://" + SERVER + ":" + String.valueOf(REGISTRY_PORT) + "/registry/api/v1/registry";
		registry = new AASRegistryProxy(REGISTRYPATH);
		manager = new ConnectedAssetAdministrationShellManager(registry);
		aasServerURL = "http://" + SERVER + ":" + String.valueOf(AAS_PORT) + "/dtframework";
		directory = new VABInMemoryRegistry();
		directoryProvider = new VABRegistryModelProvider(directory);
		directoryServlet = new VABHTTPInterface<IModelProvider>(directoryProvider);
		directoryRegistry = new VABInMemoryRegistry();
		directoryRegistryProvider = new VABRegistryModelProvider(directoryRegistry);
		IConnectorFactory connectorFactory = new HTTPConnectorFactory();
		directoryRegistryServlet = new VABHTTPInterface<IModelProvider>(directoryRegistryProvider);
		vabConnectionManager = new VABConnectionManager(
				new VABRegistryProxy("http://localhost:4005/dtframeworkVAB/directoryRegistry/"), connectorFactory);
		// This is not yet supported by BaSyx
		vabConnectionManagerVABServer = new VABConnectionManager(
				new VABRegistryProxy("http://localhost:4005/dtframeworkVAB/directory/"), connectorFactory);

		context = settingUpContext();

		


		// Database
		Properties props = new Properties();
		props.setProperty("user", user);
		props.setProperty("password", password);
		props.setProperty("ssl", "true");
		try {
			conn = DriverManager.getConnection(url, props);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Starting BaSyx server for VAB interfaces:
		startBaSyxVABServer(context);
	}

	public SBDigitalTwin createDigitalTwin(String aasxFile, String aasIdShort) {
		SBDigitalTwin dt;
		/******** AASX Actual ********/
		AASXPackageManager aasxManager = new AASXPackageManager(aasxFile);
		Set<AASBundle> bundlesActual;
		try {
			bundlesActual = aasxManager.retrieveAASBundles();
		} catch (InvalidFormatException e) {
			bundlesActual = null;
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			bundlesActual = null;
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			bundlesActual = null;
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			bundlesActual = null;
			e.printStackTrace();
		}
		
		
		AASBundle objectAASX = findBundle(bundlesActual, aasIdShort);
		//Set<ISubmodel> submodels = objectAASX.getSubmodels();
		//Set<ISubmodel> submodelsExperimental = objectAASXExperimental.getSubmodels();
		//ISubmodel technicalDataSubmodel = getBundleSubmodel(submodels, "TechnicalData");
		//ISubmodel operationalDataSubmodel = getBundleSubmodel(submodels, "OperationalData");
		//ISubmodel technicalDataSubmodelExperimental = getBundleSubmodel(submodelsExperimental, "TechnicalData");
		//ISubmodel operationalDataSubmodelExperimental = getBundleSubmodel(submodelsExperimental, "OperationalData");

		/***** Actual *****/
		IAssetAdministrationShell objectAAS = objectAASX.getAAS();
		/***** Experimental *****/
		/***** Interface *****/
		String prefix = "http://www.semanticweb.org/ontologies/cooperativeDTs#";
		dt = new SBDigitalTwin(objectAAS, inference, IRI.create(prefix + aasIdShort));
		
		
		/***** Actual Configuration *****/
		Set<ISubmodel> submodels = objectAASX.getSubmodels();
		ISubmodel technicalDataSubmodel = getBundleSubmodel(submodels, "TechnicalData");
		ISubmodel operationalDataSubmodel = getBundleSubmodel(submodels, "OperationalData");


		dt.setSubmodels(technicalDataSubmodel, operationalDataSubmodel);
		
		availableTwins.put(aasIdShort, dt);
		return dt;
	}

	public DTSubmodelDescriptor setKukaOperations(Map<String, IOperation> kukaOperationsMap, SBDigitalTwin twin) {
		// MovePTP Cartesian
		Operation kukaMovePTPCart = (Operation) kukaOperationsMap.get("MovePTP_Cartesian_Space");

		Function<Object[], Object> movePTPCartFunction = (arguments) -> {
			// System.out.println("moveptpcart from invoke");
			if (arguments != null) {
				if (arguments.length != 0) {
					String argumentsStr = arguments[0] + "," + arguments[1] + "," + arguments[2] + "," + arguments[3]
							+ "," + arguments[4] + "," + arguments[5];
					String command = "kuka moveptpcart(" + argumentsStr + ")";
					//EventHandler.zmqSocketKukaWriting.send(command);
					MqttMessage mqttMessage = new MqttMessage(command.getBytes());
					try {
						EventHandler.mqttClient.publish(EventHandler.ACTUALKUKATOPIC, mqttMessage);
					} catch (MqttPersistenceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MqttException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					insertOperationEventRecord("moveptpcart", "Kuka_LBR_iiwa7_AAS", argumentsStr);
				}

			}

			return null;
		};
		kukaMovePTPCart.setInvokable(movePTPCartFunction);

		// Calculate Forward Kinematics
		Operation calculateForwardKinematics = (Operation) kukaOperationsMap.get("Calculate_Forward_Kinematics");

		Function<Object[], Object> calculateForwardKinematicsFunction = (arguments) -> {
			// System.out.println("calculate fk from invoke");
			String command = "kuka calculate_forward_kinematics";
			//EventHandler.zmqSocketKukaWriting.send(command);
			MqttMessage mqttMessage = new MqttMessage(command.getBytes());
			try {
				EventHandler.mqttClient.publish(EventHandler.ACTUALKUKATOPIC, mqttMessage);
			} catch (MqttPersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			insertOperationEventRecord("calculate_forward_kinematics", "Kuka_LBR_iiwa7_AAS", "");
			return null;
		};
		calculateForwardKinematics.setInvokable(calculateForwardKinematicsFunction);

		// Calculate Inverse Kinematics
		Operation calculateInverseKinematics = (Operation) kukaOperationsMap.get("Calculate_Inverse_Kinematics");

		Function<Object[], Object> calculateInverseKinematicsFunction = (arguments) -> {
			// System.out.println("calculate ik from invoke");
			String command = "kuka calculate_inverse_kinematics";
			//EventHandler.zmqSocketKukaWriting.send(command);
			MqttMessage mqttMessage = new MqttMessage(command.getBytes());
			try {
				EventHandler.mqttClient.publish(EventHandler.ACTUALKUKATOPIC, mqttMessage);
			} catch (MqttPersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			insertOperationEventRecord("calculate_inverse_kinematics", "Kuka_LBR_iiwa7_AAS", "");
			return null;
		};
		calculateInverseKinematics.setInvokable(calculateInverseKinematicsFunction);

		// Calculate Velocities
		Operation calculateVelocities = (Operation) kukaOperationsMap.get("Calculate_Velocities");

		Function<Object[], Object> calculateVelocitiesFunction = (arguments) -> {
			// System.out.println("calculate velocities from invoke");
			String command = "kuka calculate_velocities";
			//EventHandler.zmqSocketKukaWriting.send(command);
			MqttMessage mqttMessage = new MqttMessage(command.getBytes());
			try {
				EventHandler.mqttClient.publish(EventHandler.ACTUALKUKATOPIC, mqttMessage);
			} catch (MqttPersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			insertOperationEventRecord("calculate_velocities", "Kuka_LBR_iiwa7_AAS", "");
			return null;
		};
		calculateVelocities.setInvokable(calculateVelocitiesFunction);

		// Calculate Static Forces
		Operation calculateStaticForces = (Operation) kukaOperationsMap.get("Calculate_Static_Forces");

		Function<Object[], Object> calculateStaticForcesFunction = (arguments) -> {
			// System.out.println("calculate Static forces from invoke");
			String command = "kuka calculate_static_forces";
			//EventHandler.zmqSocketKukaWriting.send(command);
			MqttMessage mqttMessage = new MqttMessage(command.getBytes());
			try {
				EventHandler.mqttClient.publish(EventHandler.ACTUALKUKATOPIC, mqttMessage);
			} catch (MqttPersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			insertOperationEventRecord("calculate_static_forces", "Kuka_LBR_iiwa7_AAS", "");
			return null;
		};
		calculateStaticForces.setInvokable(calculateStaticForcesFunction);

		// MovePTP Joint
		Operation kukaMovePTPJoint = (Operation) kukaOperationsMap.get("MovePTP_Joint_Space");

		Function<Object[], Object> movePTPJointFunction = (arguments) -> {
			// System.out.println("MovePTPjoint from invoke");
			if (arguments != null) {
				if (arguments.length != 0) {
					String argumentsStr = arguments[0] + "," + arguments[1] + "," + arguments[2] + "," + arguments[3]
							+ "," + arguments[4] + "," + arguments[5] + "," + arguments[6];
					String command = "kuka moveptprad(" + argumentsStr + ")";
					//EventHandler.zmqSocketKukaWriting.send(command);
					MqttMessage mqttMessage = new MqttMessage(command.getBytes());
					try {
						EventHandler.mqttClient.publish(EventHandler.ACTUALKUKATOPIC, mqttMessage);
					} catch (MqttPersistenceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MqttException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					insertOperationEventRecord("moveptprad", "Kuka_LBR_iiwa7_AAS", argumentsStr);
				}

			}
			return null;
		};
		kukaMovePTPJoint.setInvokable(movePTPJointFunction);

		// Move Linear Relative
		Operation kukaMoveLinearRelative = (Operation) kukaOperationsMap.get("MoveLinear_Relative_Reference");

		Function<Object[], Object> kukaMoveLinearRelativeFunction = (arguments) -> {
			// System.out.println("movelinrel from invoke");
			if (arguments != null) {
				if (arguments.length == 6) {
					String argumentsStr = arguments[0] + "," + arguments[1] + "," + arguments[2] + "," + arguments[3]
							+ "," + arguments[4] + "," + arguments[5];
					String command = "kuka movelrel(" + argumentsStr + ")";
					//EventHandler.zmqSocketKukaWriting.send(command);
					insertOperationEventRecord("movelrel", "Kuka_LBR_iiwa7_AAS", argumentsStr);
				} else if (arguments.length == 7) {
					String argumentsStr = arguments[0] + "," + arguments[1] + "," + arguments[2] + "," + arguments[3]
							+ "," + arguments[4] + "," + arguments[5] + "," + arguments[6];
					String command = "kuka movelrel(" + argumentsStr + ")";
					//EventHandler.zmqSocketKukaWriting.send(command);
					MqttMessage mqttMessage = new MqttMessage(command.getBytes());
					try {
						EventHandler.mqttClient.publish(EventHandler.ACTUALKUKATOPIC, mqttMessage);
					} catch (MqttPersistenceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MqttException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					insertOperationEventRecord("movelrel", "Kuka_LBR_iiwa7_AAS", argumentsStr);
				}
			}
			return null;
		};
		kukaMoveLinearRelative.setInvokable(kukaMoveLinearRelativeFunction);

		// Move Spline
		Operation kukaMoveSpline = (Operation) kukaOperationsMap.get("MoveSpline");

		Function<Object[], Object> kukaMoveSplineFunction = (arguments) -> {
			// System.out.println("movespline from invoke");
			String command = "kuka move_spline"; // Not yet implemented
			//EventHandler.zmqSocketKukaWriting.send(command);
			MqttMessage mqttMessage = new MqttMessage(command.getBytes());
			try {
				EventHandler.mqttClient.publish(EventHandler.ACTUALKUKATOPIC, mqttMessage);
			} catch (MqttPersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			insertOperationEventRecord("move_spline", "Kuka_LBR_iiwa7_AAS", "");
			return null;
		};
		kukaMoveSpline.setInvokable(kukaMoveSplineFunction);

		// Move Linear
		Operation kukaMoveLinear = (Operation) kukaOperationsMap.get("MoveLinear");

		Function<Object[], Object> kukaMoveLinearFunction = (arguments) -> {
			// System.out.println("movelin from invoke");
			if (arguments != null) {
				if (arguments.length != 0) {
					String argumentsStr = arguments[0] + "," + arguments[1] + "," + arguments[2] + "," + arguments[3]
							+ "," + arguments[4] + "," + arguments[5];
					String command = "kuka movelin(" + argumentsStr + ")";
					//EventHandler.zmqSocketKukaWriting.send(command);
					MqttMessage mqttMessage = new MqttMessage(command.getBytes());
					try {
						EventHandler.mqttClient.publish(EventHandler.ACTUALKUKATOPIC, mqttMessage);
					} catch (MqttPersistenceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MqttException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					insertOperationEventRecord("movelin", "Kuka_LBR_iiwa7_AAS", argumentsStr);
				}

			}
			return null;
		};
		kukaMoveLinear.setInvokable(kukaMoveLinearFunction);

		// Move Circular
		Operation kukaMoveCircular = (Operation) kukaOperationsMap.get("MoveCircular");

		Function<Object[], Object> kukaMoveCircularFunction = (arguments) -> {
			// System.out.println("movecirc from invoke");
			if (arguments != null) {
				if (arguments.length != 0) {
					String argumentsStr = arguments[0] + "," + arguments[1] + "," + arguments[2] + "," + arguments[3]
							+ "," + arguments[4] + "," + arguments[5];
					String command = "kuka movecirc(" + argumentsStr + ")";
					//EventHandler.zmqSocketKukaWriting.send(command);
					MqttMessage mqttMessage = new MqttMessage(command.getBytes());
					try {
						EventHandler.mqttClient.publish(EventHandler.ACTUALKUKATOPIC, mqttMessage);
					} catch (MqttPersistenceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MqttException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					insertOperationEventRecord("movecirc", "Kuka_LBR_iiwa7_AAS", argumentsStr);
				}

			}
			return null;
		};
		kukaMoveCircular.setInvokable(kukaMoveCircularFunction);

		// Generic Command
		Operation kukaGenericCommand = (Operation) kukaOperationsMap.get("GenericCommand");

		Function<Object[], Object> kukaGenericCommandFunction = (arguments) -> {
			// System.out.println("generic command from invoke");
			if (arguments != null) {
				if (arguments.length != 0) {
					String command = "kuka genericcommand(" + arguments[0] + ")";
					//EventHandler.zmqSocketKukaWriting.send(command);
					MqttMessage mqttMessage = new MqttMessage(command.getBytes());
					try {
						EventHandler.mqttClient.publish(EventHandler.ACTUALKUKATOPIC, mqttMessage);
					} catch (MqttPersistenceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MqttException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					insertOperationEventRecord("genericcommand", "Kuka_LBR_iiwa7_AAS", String.valueOf(arguments[0]));
				}

			}
			return null;
		};
		kukaGenericCommand.setInvokable(kukaGenericCommandFunction);

		SubmodelDescriptor kukaODDescriptor = new SubmodelDescriptor(twin.getOperationalDataSubmodel(),
				"http://localhost:4005/dtframeworkVAB/KukaOperationalDataSubmodel/submodel");



		DTSubmodelDescriptor kukaDTSubmodelDescriptor = new DTSubmodelDescriptor(kukaODDescriptor);
		return kukaDTSubmodelDescriptor;
	}

	public DTSubmodelDescriptor setUR5eOperations(Map<String, IOperation> ur5eOperationsMap, SBDigitalTwin twin) {
		// MoveP
		Operation ur5eMoveP = (Operation) ur5eOperationsMap.get("MoveP");

		Function<Object[], Object> ur5eMovePFunction = (arguments) -> {
			// System.out.println("movep from invoke");
			if (arguments != null) {
				if (arguments.length != 0) {
					String argumentsStr = arguments[0] + "," + arguments[1] + "," + arguments[2] + "," + arguments[3]
							+ "," + arguments[4] + "," + arguments[5];
					String command = "ur5e movep(" + argumentsStr + ")";
					//EventHandler.zmqSocketURWriting.send(command);
					MqttMessage mqttMessage = new MqttMessage(command.getBytes());
					try {
						EventHandler.mqttClient.publish(EventHandler.ACTUALUR5ETOPIC, mqttMessage);
					} catch (MqttPersistenceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MqttException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					insertOperationEventRecord("movep", "UR5e_AAS", argumentsStr);
				}

			}
			return null;
		};
		ur5eMoveP.setInvokable(ur5eMovePFunction);

		// Calculate Forward Kinematics
		Operation ur5eCalculateForwardKinematics = (Operation) ur5eOperationsMap.get("Calculate_Forward_Kinematics");

		Function<Object[], Object> ur5eCalculateForwardKinematicsFunction = (arguments) -> {
			// System.out.println("calculate fk from invoke");
			String command = "ur5e calculate_forward_kinematics";
			//EventHandler.zmqSocketURWriting.send(command);
			MqttMessage mqttMessage = new MqttMessage(command.getBytes());
			try {
				EventHandler.mqttClient.publish(EventHandler.ACTUALUR5ETOPIC, mqttMessage);
			} catch (MqttPersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			insertOperationEventRecord("calculate_forward_kinematics", "UR5e_AAS", "");
			return null;
		};
		ur5eCalculateForwardKinematics.setInvokable(ur5eCalculateForwardKinematicsFunction);

		// Calculate Inverse Kinematics
		Operation ur5eCalculateInverseKinematics = (Operation) ur5eOperationsMap.get("Calculate_Inverse_Kinematics");

		Function<Object[], Object> ur5eCalculateInverseKinematicsFunction = (arguments) -> {
			// System.out.println("calculate ik from invoke");
			String command = "ur5e calculate_inverse_kinematics";
			//EventHandler.zmqSocketURWriting.send(command);
			MqttMessage mqttMessage = new MqttMessage(command.getBytes());
			try {
				EventHandler.mqttClient.publish(EventHandler.ACTUALUR5ETOPIC, mqttMessage);
			} catch (MqttPersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			insertOperationEventRecord("calculate_inverse_kinematics", "UR5e_AAS", "");
			return null;
		};
		ur5eCalculateInverseKinematics.setInvokable(ur5eCalculateInverseKinematicsFunction);

		// Calculate Velocities
		Operation ur5eCalculateVelocities = (Operation) ur5eOperationsMap.get("Calculate_Velocities");

		Function<Object[], Object> ur5eCalculateVelocitiesFunction = (arguments) -> {
			// System.out.println("calculate velocities from invoke");
			String command = "ur5e calculate_velocities";
			//EventHandler.zmqSocketURWriting.send(command);
			MqttMessage mqttMessage = new MqttMessage(command.getBytes());
			try {
				EventHandler.mqttClient.publish(EventHandler.ACTUALUR5ETOPIC, mqttMessage);
			} catch (MqttPersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			insertOperationEventRecord("calculate_velocities", "UR5e_AAS", "");
			return null;
		};
		ur5eCalculateVelocities.setInvokable(ur5eCalculateVelocitiesFunction);

		// Calculate Static Forces
		Operation ur5eCalculateStaticForces = (Operation) ur5eOperationsMap.get("Calculate_Static_Forces");

		Function<Object[], Object> ur5eCalculateStaticForcesFunction = (arguments) -> {
			// System.out.println("calculate Static forces from invoke");
			String command = "ur5e calculate_static_forces";
			//EventHandler.zmqSocketURWriting.send(command);
			MqttMessage mqttMessage = new MqttMessage(command.getBytes());
			try {
				EventHandler.mqttClient.publish(EventHandler.ACTUALUR5ETOPIC, mqttMessage);
			} catch (MqttPersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			insertOperationEventRecord("calculate_static_forces", "UR5e_AAS", "");
			return null;
		};
		ur5eCalculateStaticForces.setInvokable(ur5eCalculateStaticForcesFunction);

		// MoveL
		Operation ur5eMoveL = (Operation) ur5eOperationsMap.get("MoveL");

		Function<Object[], Object> ur5eMoveLFunction = (arguments) -> {
			// System.out.println("MoveL from invoke");
			if (arguments.length != 0) {
				String argumentsStr = arguments[0] + "," + arguments[1] + "," + arguments[2] + "," + arguments[3] + ","
						+ arguments[4] + "," + arguments[5];
				String command = "ur5e movel(" + argumentsStr + ")";
				//EventHandler.zmqSocketURWriting.send(command);
				MqttMessage mqttMessage = new MqttMessage(command.getBytes());
				try {
					EventHandler.mqttClient.publish(EventHandler.ACTUALUR5ETOPIC, mqttMessage);
				} catch (MqttPersistenceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				insertOperationEventRecord("movel", "UR5e_AAS", argumentsStr);
			}
			return null;
		};
		ur5eMoveL.setInvokable(ur5eMoveLFunction);

		// MoveJ
		Operation ur5eMoveJ = (Operation) ur5eOperationsMap.get("MoveJ");

		Function<Object[], Object> ur5eMoveJFunction = (arguments) -> {
			// System.out.println("MoveJ from invoke");
			if (arguments.length != 0) {
				String argumentsStr = arguments[0] + "," + arguments[1] + "," + arguments[2] + "," + arguments[3] + ","
						+ arguments[4] + "," + arguments[5];
				String command = "ur5e movej(" + argumentsStr + ")";
				//EventHandler.zmqSocketURWriting.send(command);
				MqttMessage mqttMessage = new MqttMessage(command.getBytes());
				try {
					EventHandler.mqttClient.publish(EventHandler.ACTUALUR5ETOPIC, mqttMessage);
				} catch (MqttPersistenceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				insertOperationEventRecord("movej", "UR5e_AAS", argumentsStr);
			}
			return null;
		};
		ur5eMoveJ.setInvokable(ur5eMoveJFunction);

		// Generic Command
		Operation ur5eGenericCommand = (Operation) ur5eOperationsMap.get("GenericCommand");

		Function<Object[], Object> ur5eGenericCommandFunction = (arguments) -> {
			// System.out.println("generic command from invoke");
			if (arguments != null) {
				if (arguments.length != 0) {
					String command = "ur5e genericcommand(" + arguments[0] + ")";
					//EventHandler.zmqSocketURWriting.send(command);
					MqttMessage mqttMessage = new MqttMessage(command.getBytes());
					try {
						EventHandler.mqttClient.publish(EventHandler.ACTUALUR5ETOPIC, mqttMessage);
					} catch (MqttPersistenceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MqttException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				insertOperationEventRecord("genericcommand", "UR5e_AAS", String.valueOf(arguments[0]));
			}
			return null;
		};
		ur5eGenericCommand.setInvokable(ur5eGenericCommandFunction);

		SubmodelDescriptor ur5eODDescriptor = new SubmodelDescriptor(twin.getOperationalDataSubmodel(),
				"http://localhost:4005/dtframeworkVAB/ur5eOperationalDataSubmodel/submodel");

		/**************************************************************************************************/

		DTSubmodelDescriptor ur5eDTSubmodelDescriptor = new DTSubmodelDescriptor(ur5eODDescriptor);
		return ur5eDTSubmodelDescriptor;
	}

	public DTSubmodelDescriptor set2FG7Operations(Map<String, IOperation> gripper2FG7OperationsMap, SBDigitalTwin twin) {
		// Set Force
		Operation gripper2FG7SetForce = (Operation) gripper2FG7OperationsMap.get("Set_Force");

		Function<Object[], Object> gripper2FG7SetForceFunction = (arguments) -> {
			// System.out.println("setforce from invoke");
			if (arguments.length != 0) {
				String command = "gripper2fg7 setforce(" + arguments[0] + ")";
				//EventHandler.zmqSocketGripper2FG7Writing.send(command);
				MqttMessage mqttMessage = new MqttMessage(command.getBytes());
				try {
					EventHandler.mqttClient.publish(EventHandler.ACTUAL2FG7TOPIC, mqttMessage);
				} catch (MqttPersistenceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				insertOperationEventRecord("setforce", "Gripper2FG7_AAS", String.valueOf(arguments[0]));
			}
			return null;
		};
		gripper2FG7SetForce.setInvokable(gripper2FG7SetForceFunction);

		// Set Force
		Operation gripper2FG7SetDiameter = (Operation) gripper2FG7OperationsMap.get("Set_Diameter");

		Function<Object[], Object> gripper2FG7SetDiameterFunction = (arguments) -> {
			// System.out.println("setdiameter from invoke");
			if (arguments.length != 0) {
				String command = "gripper2fg7 setdiameter(" + arguments[0] + ")";
				//EventHandler.zmqSocketGripper2FG7Writing.send(command);
				MqttMessage mqttMessage = new MqttMessage(command.getBytes());
				try {
					EventHandler.mqttClient.publish(EventHandler.ACTUAL2FG7TOPIC, mqttMessage);
				} catch (MqttPersistenceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				insertOperationEventRecord("setdiameter", "Gripper2FG7_AAS", String.valueOf(arguments[0]));
			}
			return null;
		};
		gripper2FG7SetDiameter.setInvokable(gripper2FG7SetDiameterFunction);

		// Pick
		Operation gripper2FG7Pick = (Operation) gripper2FG7OperationsMap.get("Pick");

		Function<Object[], Object> gripper2FG7PickFunction = (arguments) -> {
			// System.out.println("pick from invoke");
			String command = "gripper2fg7 pick";
			//EventHandler.zmqSocketGripper2FG7Writing.send(command);
			MqttMessage mqttMessage = new MqttMessage(command.getBytes());
			try {
				EventHandler.mqttClient.publish(EventHandler.ACTUAL2FG7TOPIC, mqttMessage);
			} catch (MqttPersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			insertOperationEventRecord("pick", "Gripper2FG7_AAS", "");
			return null;
		};
		gripper2FG7Pick.setInvokable(gripper2FG7PickFunction);

		// Place
		Operation gripper2FG7Place = (Operation) gripper2FG7OperationsMap.get("Place");

		Function<Object[], Object> gripper2FG7PlaceFunction = (arguments) -> {
			// System.out.println("place from invoke");
			String command = "gripper2fg7 place";
			//EventHandler.zmqSocketGripper2FG7Writing.send(command);
			MqttMessage mqttMessage = new MqttMessage(command.getBytes());
			try {
				EventHandler.mqttClient.publish(EventHandler.ACTUAL2FG7TOPIC, mqttMessage);
			} catch (MqttPersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			insertOperationEventRecord("place", "Gripper2FG7_AAS", "");
			return null;
		};
		gripper2FG7Place.setInvokable(gripper2FG7PlaceFunction);

		// Stop
		Operation gripper2FG7Stop = (Operation) gripper2FG7OperationsMap.get("Stop");

		Function<Object[], Object> gripper2FG7StopFunction = (arguments) -> {
			// System.out.println("stop from invoke");
			String command = "gripper2fg7 stop";
			//EventHandler.zmqSocketGripper2FG7Writing.send(command);
			MqttMessage mqttMessage = new MqttMessage(command.getBytes());
			try {
				EventHandler.mqttClient.publish(EventHandler.ACTUAL2FG7TOPIC, mqttMessage);
			} catch (MqttPersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			insertOperationEventRecord("stop", "Gripper2FG7_AAS", "");
			return null;
		};
		gripper2FG7Stop.setInvokable(gripper2FG7StopFunction);

		SubmodelDescriptor gripper2FG7ODDescriptor = new SubmodelDescriptor(
				twin.getOperationalDataSubmodel(),
				"http://localhost:4005/dtframeworkVAB/gripper2FG7OperationalDataSubmodel/submodel");

		DTSubmodelDescriptor gripper2FG7DTSubmodelDescriptor = new DTSubmodelDescriptor(gripper2FG7ODDescriptor);
		return gripper2FG7DTSubmodelDescriptor;
	}

	public DTSubmodelDescriptor setRG6Operations(Map<String, IOperation> gripperRG6OperationsMap, SBDigitalTwin twin) {
		// Set Force
		Operation gripperRG6SetForce = (Operation) gripperRG6OperationsMap.get("Set_Force");

		Function<Object[], Object> gripperRG6SetForceFunction = (arguments) -> {
			// System.out.println("setforce from invoke");
			if (arguments.length != 0) {
				String command = "gripperrg6 setforce(" + arguments[0] + ")";
				//EventHandler.zmqSocketGripperRG6Writing.send(command);
				MqttMessage mqttMessage = new MqttMessage(command.getBytes());
				try {
					EventHandler.mqttClient.publish(EventHandler.ACTUALRG6TOPIC, mqttMessage);
				} catch (MqttPersistenceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		};
		gripperRG6SetForce.setInvokable(gripperRG6SetForceFunction);

		// Set Width
		Operation gripperRG6SetWidth = (Operation) gripperRG6OperationsMap.get("Set_Width");

		Function<Object[], Object> gripperRG6SetWidthFunction = (arguments) -> {
			// System.out.println("setwidth from invoke");
			if (arguments.length != 0) {
				String command = "gripperrg6 setwidth(" + arguments[0] + ")";
				//EventHandler.zmqSocketGripperRG6Writing.send(command);
				MqttMessage mqttMessage = new MqttMessage(command.getBytes());
				try {
					EventHandler.mqttClient.publish(EventHandler.ACTUALRG6TOPIC, mqttMessage);
				} catch (MqttPersistenceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		};
		System.out.println(gripperRG6SetWidth);
		gripperRG6SetWidth.setInvokable(gripperRG6SetWidthFunction);

		// Pick
		Operation gripperRG6Pick = (Operation) gripperRG6OperationsMap.get("Pick");

		Function<Object[], Object> gripperRG6PickFunction = (arguments) -> {
			// System.out.println("pick from invoke");
			String command = "gripperrg6 pick";
			//EventHandler.zmqSocketGripperRG6Writing.send(command);
			MqttMessage mqttMessage = new MqttMessage(command.getBytes());
			try {
				EventHandler.mqttClient.publish(EventHandler.ACTUALRG6TOPIC, mqttMessage);
			} catch (MqttPersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		};
		gripperRG6Pick.setInvokable(gripperRG6PickFunction);

		// Place
		Operation gripperRG6Place = (Operation) gripperRG6OperationsMap.get("Place");

		Function<Object[], Object> gripperRG6PlaceFunction = (arguments) -> {
			// System.out.println("place from invoke");
			String command = "gripperrg6 place";
			//EventHandler.zmqSocketGripperRG6Writing.send(command);
			MqttMessage mqttMessage = new MqttMessage(command.getBytes());
			try {
				EventHandler.mqttClient.publish(EventHandler.ACTUALRG6TOPIC, mqttMessage);
			} catch (MqttPersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			insertOperationEventRecord("place", "GripperRG6_AAS", "");
			return null;
		};
		gripperRG6Place.setInvokable(gripperRG6PlaceFunction);

		// Stop
		Operation gripperRG6Stop = (Operation) gripperRG6OperationsMap.get("Stop");

		Function<Object[], Object> gripperRG6StopFunction = (arguments) -> {
			// System.out.println("stop from invoke");
			String command = "gripperrg6 stop";
			//EventHandler.zmqSocketGripperRG6Writing.send(command);
			MqttMessage mqttMessage = new MqttMessage(command.getBytes());
			try {
				EventHandler.mqttClient.publish(EventHandler.ACTUALRG6TOPIC, mqttMessage);
			} catch (MqttPersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		};
		gripperRG6Stop.setInvokable(gripperRG6StopFunction);

		SubmodelDescriptor gripperRG6ODDescriptor = new SubmodelDescriptor(
				twin.getOperationalDataSubmodel(),
				"http://localhost:4005/dtframeworkVAB/gripperRG6OperationalDataSubmodel/submodel");

		
		DTSubmodelDescriptor gripperRG6DTSubmodelDescriptor = new DTSubmodelDescriptor(gripperRG6ODDescriptor);
		return gripperRG6DTSubmodelDescriptor;
	}

	public void uploadAAS2Server(IAssetAdministrationShell iAAS, ISubmodel technicalDataSubmodel,
			ISubmodel operationalDataSubmodel) {
		// Creation/update of AASs in BaSyx server
		manager.createAAS((AssetAdministrationShell) iAAS, aasServerURL);
		manager.createSubmodel(iAAS.getIdentification(), (Submodel) technicalDataSubmodel);
		manager.createSubmodel(iAAS.getIdentification(), (Submodel) operationalDataSubmodel);
	}

	private BaSyxContext settingUpContext() {
		// setting up the context to handle VAB invokes
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration();
		contextConfig.createBaSyxContext();// loadFromDefaultSource();
		contextConfig.setHostname(SERVER);
		contextConfig.setPort(4005);
		contextConfig.setContextPath("/dtframeworkVAB");
		// BaSyxContext context = contextConfig.createBaSyxContext();
		BaSyxContext context = new BaSyxContext("/dtframeworkVAB", "", "localhost", 4005);

		return context;
	}

	public void setVABInterfaces(IAssetAdministrationShell iAAS, ISubmodel technicalDataSubmodel,
			ISubmodel operationalDataSubmodel, SubmodelDescriptor operationalDataDescriptor,
			String servletName) {
		// Creating the providers and VAB interfaces
		AASModelProvider aasProvider = new AASModelProvider((AssetAdministrationShell) iAAS);
		SubmodelProvider aasTDSMProvider = new SubmodelProvider((Submodel) technicalDataSubmodel);
		SubmodelProvider aasODSMProvider = new SubmodelProvider((Submodel) operationalDataSubmodel);
		MultiSubmodelProvider aasMSMProvider = new MultiSubmodelProvider();
		aasMSMProvider.setAssetAdministrationShell(aasProvider);
		aasMSMProvider.addSubmodel(aasTDSMProvider);
		aasMSMProvider.addSubmodel(aasODSMProvider);
		HttpServlet aasServlet = new VABHTTPInterface<IModelProvider>(aasMSMProvider);

		// Adding registry mappings to VAB and Registry Server
		context.addServletMapping("/" + servletName + "/*", aasServlet);
		directory.addMapping(servletName, "http://localhost:4005/dtframeworkVAB/" + servletName);
		context.addServletMapping("/directory/*", directoryServlet);
		context.addServletMapping("/directoryRegistry/*", directoryRegistryServlet);
		// Adding submodel descriptors to Registry
		AASRegistryProxy registryProxy = new AASRegistryProxy(
				"http://" + SERVER + ":" + String.valueOf(REGISTRY_PORT) + "/registry/api/v1/registry");
		registryProxy.register(iAAS.getIdentification(), operationalDataDescriptor);
	}

	private void startBaSyxVABServer(BaSyxContext context) {
		/********* VAB Server *********/
		// VAB Context Server
		BaSyxHTTPServer vabServer = new BaSyxHTTPServer(context);
		vabServer.start();
		System.out.println("Additional AAS server instance started on port 4005 with context path dtframework");
	}

	public static AASBundle findBundle(Set<AASBundle> bundles, String aasIdShort) {
		for (AASBundle aasBundle : bundles) {
			if (aasBundle.getAAS().getIdShort().equals(aasIdShort))
				return aasBundle;
		}
		return null;
	}

	public static ISubmodel getBundleSubmodel(Set<ISubmodel> submodels, String submodelIdShort) {
		for (ISubmodel submodel : submodels) {
			if (submodel.getIdShort().equals(submodelIdShort))
				return submodel;
		}
		return null;
	}

	public static void insertOperationEventRecord(String eventName, String aasInstance, String arguments) {
		String INSERT_ENTRY_SQL = "INSERT INTO operations_events"
				+ "  (timestamp, event_type, aas_instance, arguments) VALUES " + " (?, ?, ?, ?);";
		PreparedStatement preparedStatement;

		try {
			preparedStatement = conn.prepareStatement(INSERT_ENTRY_SQL);
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			preparedStatement.setTimestamp(1, ts);
			preparedStatement.setString(2, eventName);
			preparedStatement.setString(3, aasInstance);
			preparedStatement.setString(4, arguments);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public class DTSubmodelDescriptor {
		public SubmodelDescriptor submodelDescriptor;

		public DTSubmodelDescriptor(SubmodelDescriptor submodelDescriptor) {
			this.submodelDescriptor = submodelDescriptor;
		}
	}
}
