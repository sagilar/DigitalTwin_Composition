package dtflexcell;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Function;

import javax.servlet.http.HttpServlet;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
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
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.aasx.AASXPackageManager;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.aas.executable.AASServerExecutable;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.connected.ConnectedSubmodel;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.operation.ConnectedOperation;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.support.bundle.AASBundle;
import org.eclipse.basyx.support.bundle.AASBundleHelper;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
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
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.swrlapi.exceptions.SWRLBuiltInException;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;
import org.xml.sax.SAXException;


import CompositionManager.CloneableAssetAdministrationShell;
import CompositionManager.CopiableAASBundle;
import CompositionManager.DevicePrimitive;
import CompositionManager.SBDigitalTwin;
import CompositionManager.Skill;
import CompositionManager.Task;
import DTManager.DTManager;
import DTManager.DTManager.DTSubmodelDescriptor;
import SkillBasedDTOntology.DigitalTwin;
import SkillBasedDTOntology.impl.DefaultDigitalTwin;
import dtflexcell.EventHandler;

public class DTFlexCellMain {
	private static final String SERVER = "localhost";
	private static final int AAS_PORT = 4001;
	private static final int REGISTRY_PORT = 4000;
	private static final String CONTEXT_PATH = "dtframework";
	public static SBDigitalTwin kukaDT;
	public static SBDigitalTwin ur5eDT;
	public static SBDigitalTwin gripper2FG7DT;
	public static SBDigitalTwin gripperRG6DT;
	public static SBDigitalTwin flexCell;
	public static SBDigitalTwin kukaGripperRG6DT;
	public static SBDigitalTwin ur5eGripper2FG7DT;
	public static SBDigitalTwin kukaGripper2FG7DT;
	public static SBDigitalTwin ur5eGripperRG6DT;
	
	
	
	static DTManager dtManager;

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		Set<String> loggers = new HashSet<>(Arrays.asList("org.apache.http", "groovyx.net.http"));
	    
	    for(String log:loggers) { 
		    Logger logger = Logger.getLogger(log);
		    logger.setLevel(Level.INFO);
		    logger.setAdditivity(false);
	    }
		dtManager = new DTManager();
		/********* Event Handler ********/
		// Initializing Event Handler
		EventHandler.init(dtManager);
		
		/***** creation of smaller DTs *****/
		kukaDT = dtManager.createDigitalTwin("kuka.aasx", "Kuka_LBR_iiwa7_AAS");
		ur5eDT = dtManager.createDigitalTwin("ur5e.aasx", "UR5e_AAS");
		gripper2FG7DT = dtManager.createDigitalTwin("gripper2FG7.aasx", "Gripper2FG7_AAS");
		gripperRG6DT = dtManager.createDigitalTwin("gripperRG6.aasx", "GripperRG6_AAS");
		
		
		/***** composition of larger DTs *****/
		String prefix = "http://www.semanticweb.org/ontologies/cooperativeDTs#";
		kukaGripperRG6DT = new SBDigitalTwin(dtManager.semanticNetwork.getInference(), IRI.create(prefix +"kukaGripperRG6"));
		ur5eGripper2FG7DT = new SBDigitalTwin(dtManager.semanticNetwork.getInference(), IRI.create(prefix +"ur5eGripper2FG7"));
		kukaGripper2FG7DT = new SBDigitalTwin(dtManager.semanticNetwork.getInference(), IRI.create(prefix +"kukaGripper2FG7"));
		ur5eGripperRG6DT = new SBDigitalTwin(dtManager.semanticNetwork.getInference(), IRI.create(prefix +"ur5eGripperRG6"));
		flexCell = new SBDigitalTwin(dtManager.semanticNetwork.getInference(), IRI.create(prefix +"Flexcell"));
		
		kukaGripperRG6DT.addIsComposedOf(kukaDT);
		kukaGripperRG6DT.addIsComposedOf(gripperRG6DT);
		
		ur5eGripper2FG7DT.addIsComposedOf(ur5eDT);
		ur5eGripper2FG7DT.addIsComposedOf(gripper2FG7DT);
		
		kukaGripper2FG7DT.addIsComposedOf(kukaDT);
		kukaGripper2FG7DT.addIsComposedOf(gripper2FG7DT);
		
		ur5eGripperRG6DT.addIsComposedOf(ur5eDT);
		ur5eGripperRG6DT.addIsComposedOf(gripperRG6DT);
		
		// Flex cell case (static but changeable): kukaGripperRG6DT + ur5eGripper2FG7DT
		/*flexCell.addIsComposedOf(kukaDT);
		flexCell.addIsComposedOf(ur5eDT);
		flexCell.addIsComposedOf(gripper2FG7DT);
		flexCell.addIsComposedOf(gripperRG6DT);*/
		
		flexCell.addIsComposedOf((DefaultDigitalTwin)kukaGripperRG6DT);
		flexCell.addIsComposedOf((DefaultDigitalTwin)ur5eGripper2FG7DT);
		
		/***** Ontology properties and relationships *****/
		
		kukaDT.addIsConnectedTo(gripper2FG7DT);
		kukaDT.addCooperatesWith(ur5eDT);
		
		ur5eDT.addIsConnectedTo(gripperRG6DT);
		ur5eDT.addCooperatesWith(kukaDT);
		
		
		/***** Properties and Operations *****/
		/****** Operations are not currently using native BaSyx AAS support *****/
		// Kuka
		Map<String, IOperation> kukaOperationsMap = kukaDT.getOperations();
		Map<String, IProperty> kukaOperationalPropertiesMap = kukaDT.getOperationalProperties();

		DTSubmodelDescriptor kukaDTSMDescriptor = dtManager.setKukaOperations(kukaOperationsMap,kukaDT);

		dtManager.uploadAAS2Server(kukaDT.getAAS(), kukaDT.getTechnicalDataSubmodel(),
				kukaDT.getOperationalDataSubmodel());
		dtManager.setVABInterfaces(kukaDT.getAAS(), kukaDT.getTechnicalDataSubmodel(),
				kukaDT.getOperationalDataSubmodel(), kukaDTSMDescriptor.submodelDescriptor, "KUKA");

		// UR5e
		Map<String, IOperation> ur5eOperationsMap = ur5eDT.getOperations();
		Map<String, IProperty> ur5eOperationalPropertiesMap = ur5eDT.getOperationalProperties();

		DTSubmodelDescriptor ur5eDTSMDescriptor = dtManager.setUR5eOperations(ur5eOperationsMap,ur5eDT);
		dtManager.uploadAAS2Server(ur5eDT.getAAS(), ur5eDT.getTechnicalDataSubmodel(),
				ur5eDT.getOperationalDataSubmodel());
		dtManager.setVABInterfaces(ur5eDT.getAAS(), ur5eDT.getTechnicalDataSubmodel(),
				ur5eDT.getOperationalDataSubmodel(), ur5eDTSMDescriptor.submodelDescriptor, "UR5E");


		// 2FG7
		Map<String, IOperation> gripper2FG7OperationsMap = gripper2FG7DT.getOperations();
		Map<String, IProperty> gripper2FG7OperationalPropertiesMap = gripper2FG7DT.getOperationalProperties();

		DTSubmodelDescriptor gripper2FG7DTSMDescriptor = dtManager.set2FG7Operations(gripper2FG7OperationsMap,gripper2FG7DT);
		dtManager.uploadAAS2Server(gripper2FG7DT.getAAS(), gripper2FG7DT.getTechnicalDataSubmodel(),
				gripper2FG7DT.getOperationalDataSubmodel());
		dtManager.setVABInterfaces(gripper2FG7DT.getAAS(), gripper2FG7DT.getTechnicalDataSubmodel(),
				gripper2FG7DT.getOperationalDataSubmodel(), gripper2FG7DTSMDescriptor.submodelDescriptor,
				"GRIPPER2FG7");


		// RG6
		Map<String, IOperation> gripperRG6OperationsMap = gripperRG6DT.getOperations();
		Map<String, IProperty> gripperRG6OperationalPropertiesMap = gripperRG6DT.getOperationalProperties();
		DTSubmodelDescriptor gripperRG6DTSMDescriptor = dtManager.setRG6Operations(gripperRG6OperationsMap,gripperRG6DT);
		dtManager.uploadAAS2Server(gripperRG6DT.getAAS(), gripperRG6DT.getTechnicalDataSubmodel(),
				gripperRG6DT.getOperationalDataSubmodel());
		dtManager.setVABInterfaces(gripperRG6DT.getAAS(), gripperRG6DT.getTechnicalDataSubmodel(),
				gripperRG6DT.getOperationalDataSubmodel(), gripperRG6DTSMDescriptor.submodelDescriptor,
				"GRIPPERRG6");
		
		/***** Design of the skill-based engineering problem *****/
		// Shapes
		List<List> crossShapeList = Shape.getCrossShape();
		List<List> squareShapeList = Shape.getSquareShape();
		
		/***** Tasks *****/
		// Each task should be a combination of 2x24 skills (1 for picking up, 1 for placing)
		Task assembleSquareShape = new Task(dtManager.semanticNetwork.getInference(), IRI.create(prefix +"SquareShapeTask"));
		Task assembleCrossShape = new Task(dtManager.semanticNetwork.getInference(), IRI.create(prefix +"CrossShapeTask"));
		
		//assembleSquareShape.addHasArgument(squareShapeList);
		assembleSquareShape.setShape(squareShapeList);
		//assembleCrossShape.addHasArgument(crossShapeList);
		assembleCrossShape.setShape(crossShapeList);
		
		/***** Skills *****/
		// Flexcell Skills
		// 2 Skill per object
		Skill graspObjectUR = new Skill(dtManager.semanticNetwork.getInference(), IRI.create(prefix +"GraspObjectURSkill"));
		Skill graspObjectKuka = new Skill(dtManager.semanticNetwork.getInference(), IRI.create(prefix +"GraspObjectKukaSkill"));
		Skill placeObjectUR = new Skill(dtManager.semanticNetwork.getInference(), IRI.create(prefix +"PlaceObjectURSkill"));
		Skill placeObjectKuka = new Skill(dtManager.semanticNetwork.getInference(), IRI.create(prefix +"PlaceObjectKukaSkill"));
		
		/***** Device Primitives *****/
		// Robots and grippers
		//Kuka
		DevicePrimitive kukaMovePTPCartesian = new DevicePrimitive(dtManager.semanticNetwork.getInference(),
				IRI.create(prefix +"kukaMovePTPCartesian"));
		DevicePrimitive kukaMovePTPRadian = new DevicePrimitive(dtManager.semanticNetwork.getInference(),
				IRI.create(prefix +"kukaMovePTPRadian"));
		DevicePrimitive kukaMoveLinear = new DevicePrimitive(dtManager.semanticNetwork.getInference(),
				IRI.create(prefix +"kukaMoveLinear"));
		DevicePrimitive kukaMoveLinearRelative = new DevicePrimitive(dtManager.semanticNetwork.getInference(),
				IRI.create(prefix +"kukaMoveLinearRelative"));
		DevicePrimitive kukaMoveCircular = new DevicePrimitive(dtManager.semanticNetwork.getInference(),
				IRI.create(prefix +"kukaMoveCircular"));
		DevicePrimitive computeIKFeasibilityKuka = new DevicePrimitive(dtManager.semanticNetwork.getInference(),
				IRI.create(prefix +"ComputeIKFeasibilityKukaDevicePrimitive"));
		DevicePrimitive computeInverseKinematicsKuka = new DevicePrimitive(dtManager.semanticNetwork.getInference(),
				IRI.create(prefix +"ComputeInverseKinematicsKukaDevicePrimitive"));
		DevicePrimitive computeCartesianPositionKuka = new DevicePrimitive(dtManager.semanticNetwork.getInference(),
				IRI.create(prefix +"ComputeCartesianPositionKukaDevicePrimitive"));
		
		kukaDT.addHasDevicePrimitive(kukaMovePTPCartesian);
		kukaDT.addHasDevicePrimitive(kukaMovePTPRadian);
		kukaDT.addHasDevicePrimitive(kukaMoveLinear);
		kukaDT.addHasDevicePrimitive(kukaMoveLinearRelative);
		kukaDT.addHasDevicePrimitive(kukaMoveCircular);
		kukaDT.addHasDevicePrimitive(computeIKFeasibilityKuka);
		kukaDT.addHasDevicePrimitive(computeInverseKinematicsKuka);
		kukaDT.addHasDevicePrimitive(computeCartesianPositionKuka);
		
		//UR
		DevicePrimitive ur5eMoveP = new DevicePrimitive(dtManager.semanticNetwork.getInference(),
				IRI.create(prefix +"ur5eMoveP"));
		DevicePrimitive ur5eMoveJ = new DevicePrimitive(dtManager.semanticNetwork.getInference(),
				IRI.create(prefix +"ur5eMoveJ"));
		DevicePrimitive ur5eMoveL = new DevicePrimitive(dtManager.semanticNetwork.getInference(),
				IRI.create(prefix +"ur5eMoveL"));
		DevicePrimitive computeIKFeasibilityUR = new DevicePrimitive(dtManager.semanticNetwork.getInference(),
				IRI.create(prefix +"ComputeIKFeasibilityURDevicePrimitive"));
		DevicePrimitive computeInverseKinematicsUR = new DevicePrimitive(dtManager.semanticNetwork.getInference(),
				IRI.create(prefix +"ComputeInverseKinematicsURDevicePrimitive"));
		DevicePrimitive computeRotatedPositionUR = new DevicePrimitive(dtManager.semanticNetwork.getInference(),
				IRI.create(prefix +"ComputeRotatedPositionURDevicePrimitive"));
		DevicePrimitive computeCartesianPositionUR = new DevicePrimitive(dtManager.semanticNetwork.getInference(),
				IRI.create(prefix +"ComputeCartesianPositionURDevicePrimitive"));
		
		ur5eDT.addHasDevicePrimitive(ur5eMoveP);
		ur5eDT.addHasDevicePrimitive(ur5eMoveJ);
		ur5eDT.addHasDevicePrimitive(ur5eMoveL);
		ur5eDT.addHasDevicePrimitive(computeIKFeasibilityUR);
		ur5eDT.addHasDevicePrimitive(computeInverseKinematicsUR);
		ur5eDT.addHasDevicePrimitive(computeRotatedPositionUR);
		ur5eDT.addHasDevicePrimitive(computeCartesianPositionUR);
		
		// Gripper 2FG7
		DevicePrimitive gripper2FG7Pick = new DevicePrimitive(dtManager.semanticNetwork.getInference(),
				IRI.create(prefix +"gripper2FG7Pick"));
		DevicePrimitive gripper2FG7Place = new DevicePrimitive(dtManager.semanticNetwork.getInference(),
				IRI.create(prefix +"gripper2FG7Place"));
		
		gripper2FG7DT.addHasDevicePrimitive(gripper2FG7Pick);
		gripper2FG7DT.addHasDevicePrimitive(gripper2FG7Place);
		
		// Gripper RG6
		DevicePrimitive gripperRG6Pick = new DevicePrimitive(dtManager.semanticNetwork.getInference(),
				IRI.create(prefix +"gripperRG6Pick"));
		DevicePrimitive gripperRG6Place = new DevicePrimitive(dtManager.semanticNetwork.getInference(),
				IRI.create(prefix +"gripperRG6Place"));
		
		gripperRG6DT.addHasDevicePrimitive(gripperRG6Pick);
		gripperRG6DT.addHasDevicePrimitive(gripperRG6Place);
		
		/****** Static functionalities *****/
		List<Object> argsGripper = new ArrayList<Object>();
		argsGripper.add("urGripperPick");
		gripper2FG7Pick.setParams(argsGripper);
		
		argsGripper = new ArrayList<Object>();
		argsGripper.add("urGripperPlace");
		gripper2FG7Place.setParams(argsGripper);
		
		argsGripper = new ArrayList<Object>();
		argsGripper.add("kukaGripperPick");
		gripperRG6Pick.setParams(argsGripper);
		
		argsGripper = new ArrayList<Object>();
		argsGripper.add("kukaGripperPlace");
		gripperRG6Place.setParams(argsGripper);
		
		/***** Combining DPs into Skills *****/ //Perhaps can be inferred automatically
		graspObjectUR.addIsCombinationOf(ur5eMoveJ);
		graspObjectUR.addIsCombinationOf(gripper2FG7Pick);
		placeObjectUR.addIsCombinationOf(ur5eMoveJ);
		placeObjectUR.addIsCombinationOf(gripper2FG7Place);
			
		graspObjectKuka.addIsCombinationOf(kukaMovePTPRadian);
		graspObjectKuka.addIsCombinationOf(gripperRG6Pick);
		placeObjectKuka.addIsCombinationOf(kukaMovePTPRadian);
		placeObjectKuka.addIsCombinationOf(gripperRG6Place);

		/***** Combining Skills into Tasks *****/
		assembleCrossShape.addIsCombinationOf(graspObjectUR);
		assembleCrossShape.addIsCombinationOf(placeObjectUR);
		assembleCrossShape.addIsCombinationOf(graspObjectKuka);
		assembleCrossShape.addIsCombinationOf(placeObjectKuka);
		
		assembleSquareShape.addIsCombinationOf(graspObjectUR);
		assembleSquareShape.addIsCombinationOf(placeObjectUR);
		assembleSquareShape.addIsCombinationOf(graspObjectKuka);
		assembleSquareShape.addIsCombinationOf(placeObjectKuka);
		
		/***** Assigning Operations to DTs (including the composed ones) *****/
		flexCell.addHasTask(assembleCrossShape);
		flexCell.addHasTask(assembleSquareShape);
		
		kukaGripperRG6DT.addHasSkill(graspObjectKuka);
		kukaGripperRG6DT.addHasSkill(placeObjectKuka);
		
		ur5eGripper2FG7DT.addHasSkill(graspObjectUR);
		ur5eGripper2FG7DT.addHasSkill(placeObjectUR);
		
		
		/***** Setting up the SWRL rules of the ontology *****/
		setSWRLRules();
		dtManager.swrlEngine.infer();
		runSQWRLQueries();
		
		// Enable to overwrite the owl file with the new instances and properties
		/*try {
			dtManager.semanticNetwork.saveOwlOntology();
		} catch (OWLOntologyStorageException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		
		/***** Defining the execution of the Cross Shape task *****/
		boolean flag = false;
		for (List li : assembleCrossShape.getShape()) {
			int index = assembleCrossShape.getShape().indexOf(li);
			
			
			List<Object> flexCellPoint = new ArrayList<Object>();
			flexCellPoint.add("urComputeIK");
			flexCellPoint.add(li.get(0));
			flexCellPoint.add(li.get(1));
			flexCellPoint.add(0);
			boolean computationResultUR = Boolean.valueOf((String)computeIKFeasibilityUR.executeSynchronousOperation(flexCellPoint));
			flexCellPoint = new ArrayList<Object>();
			flexCellPoint.add("kukaComputeIK");
			flexCellPoint.add(li.get(0));
			flexCellPoint.add(li.get(1));
			flexCellPoint.add(0);
			boolean computationResultKuka = Boolean.valueOf((String)computeIKFeasibilityKuka.executeSynchronousOperation(flexCellPoint));
				
			
			if (computationResultUR && computationResultKuka) {
				if (!flag) {
					// UR executes the action
					// Picking
					List<Object> argsOp = new ArrayList<Object>();
					argsOp.add("urComputeQ");
					argsOp.add(0);
					argsOp.add(23);
					argsOp.add(2); // Z = 2
					computeInverseKinematicsUR.setParams(argsOp);
					//graspObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
					String jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
					List<String> jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					argsOp = new ArrayList<Object>();
					argsOp.add("urMoveP");
					argsOp.addAll(jointPositionsList);
					ur5eMoveJ.setParams(argsOp);
					graspObjectUR.addDevicePrimitive(ur5eMoveJ);
					
					argsOp = new ArrayList<Object>();
					argsOp.add("urComputeQ");
					argsOp.add(0);
					argsOp.add(23);
					argsOp.add(0); // Z = 0
					computeInverseKinematicsUR.setParams(argsOp);
					jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
					jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					//graspObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
					argsOp = new ArrayList<Object>();
					argsOp.add("urMoveP");
					argsOp.addAll(jointPositionsList);
					ur5eMoveJ.setParams(argsOp);
					graspObjectUR.addDevicePrimitive(ur5eMoveJ);
					graspObjectUR.addDevicePrimitive(gripper2FG7Pick);
					
					argsOp = new ArrayList<Object>();
					argsOp.add("urComputeQ");
					argsOp.add(0);
					argsOp.add(23);
					argsOp.add(2); // Z = 2
					computeInverseKinematicsUR.setParams(argsOp);
					jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
					jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					//graspObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
					argsOp = new ArrayList<Object>();
					argsOp.add("urMoveP");
					argsOp.addAll(jointPositionsList);
					ur5eMoveJ.setParams(argsOp);
					graspObjectUR.addDevicePrimitive(ur5eMoveJ);
					
					argsOp = new ArrayList<Object>();
					argsOp.add("urComputeQ");
					argsOp.add(li.get(0));
					argsOp.add(li.get(1));
					argsOp.add(2); // Z = 2
					computeInverseKinematicsUR.setParams(argsOp);
					//placeObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
					jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
					jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					//graspObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
					argsOp = new ArrayList<Object>();
					argsOp.add("urMoveP");					
					argsOp.addAll(jointPositionsList);
					ur5eMoveJ.setParams(argsOp);
					graspObjectUR.addDevicePrimitive(ur5eMoveJ);
					
					// Placing
					argsOp = new ArrayList<Object>();
					argsOp.add("urComputeQ");
					argsOp.add(li.get(0));
					argsOp.add(li.get(1));
					argsOp.add(0); // Z = 0
					jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
					jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					//placeObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
					argsOp = new ArrayList<Object>();
					argsOp.add("urMoveP");
					argsOp.addAll(jointPositionsList);
					ur5eMoveJ.setParams(argsOp);
					placeObjectUR.addDevicePrimitive(ur5eMoveJ);
					placeObjectUR.addDevicePrimitive(gripper2FG7Place);
					
					argsOp = new ArrayList<Object>();
					argsOp.add("urComputeQ");
					argsOp.add(li.get(0));
					argsOp.add(li.get(1));
					argsOp.add(2); // Z = 2
					jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
					jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					//placeObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
					argsOp = new ArrayList<Object>();
					argsOp.add("urMoveP");					
					argsOp.addAll(jointPositionsList);
					ur5eMoveJ.setParams(argsOp);
					placeObjectUR.addDevicePrimitive(ur5eMoveJ);
					
					argsOp = new ArrayList<Object>();
					argsOp.add("urComputeQ");
					argsOp.add(0);
					argsOp.add(23);
					argsOp.add(3); // Z = 3
					jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
					jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					//placeObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
					argsOp = new ArrayList<Object>();
					argsOp.add("urMoveP");					
					argsOp.addAll(jointPositionsList);
					ur5eMoveJ.setParams(argsOp);
					placeObjectUR.addDevicePrimitive(ur5eMoveJ);
					
					// Adding skill to the task
					assembleCrossShape.addSkill(graspObjectUR);
					assembleCrossShape.addSkill(placeObjectUR);
					
					graspObjectUR.removeDevicePrimitives();
					placeObjectUR.removeDevicePrimitives();
					flag = !flag;
				}else {
					// Kuka executes the action
					// Picking
					List<Object> argsOp = new ArrayList<Object>();
					argsOp.add("kukaComputeQ");
					argsOp.add(0);
					argsOp.add(0);
					argsOp.add(2); // Z = 2
					computeInverseKinematicsKuka.setParams(argsOp);
					//graspObjectKuka.addDevicePrimitive(computeInverseKinematicsKuka);
					String jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
					List<String> jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					argsOp = new ArrayList<Object>();
					argsOp.add("kukaMoveP");
					argsOp.addAll(jointPositionsList);
					kukaMovePTPRadian.setParams(argsOp);
					graspObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
					
					argsOp = new ArrayList<Object>();
					argsOp.add("kukaComputeQ");
					argsOp.add(0);
					argsOp.add(0);
					argsOp.add(0); // Z = 0
					computeInverseKinematicsKuka.setParams(argsOp);
					jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
					jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					argsOp = new ArrayList<Object>();
					argsOp.add("kukaMoveP");
					argsOp.addAll(jointPositionsList);
					kukaMovePTPRadian.setParams(argsOp);
					graspObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
					
					graspObjectKuka.addDevicePrimitive(gripperRG6Pick);
					
					argsOp = new ArrayList<Object>();
					argsOp.add("kukaComputeQ");
					argsOp.add(0);
					argsOp.add(0);
					argsOp.add(2); // Z = 2
					computeInverseKinematicsKuka.setParams(argsOp);
					jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
					jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					argsOp = new ArrayList<Object>();
					argsOp.add("kukaMoveP");					
					argsOp.addAll(jointPositionsList);
					kukaMovePTPRadian.setParams(argsOp);
					graspObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
					
					argsOp = new ArrayList<Object>();
					argsOp.add("kukaComputeQ");
					argsOp.add(li.get(0));
					argsOp.add(li.get(1));
					argsOp.add(2); // Z = 2
					computeInverseKinematicsKuka.setParams(argsOp);
					//placeObjectKuka.addDevicePrimitive(computeInverseKinematicsKuka);
					jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
					jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					argsOp = new ArrayList<Object>();
					argsOp.add("kukaMoveP");					
					argsOp.addAll(jointPositionsList);
					kukaMovePTPRadian.setParams(argsOp);
					graspObjectKuka.addDevicePrimitive(kukaMovePTPRadian);					
					
					// Placing

					argsOp = new ArrayList<Object>();
					argsOp.add("kukaComputeQ");
					argsOp.add(li.get(0));
					argsOp.add(li.get(1));
					argsOp.add(0); // Z = 0
					computeInverseKinematicsKuka.setParams(argsOp);
					jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
					jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					argsOp = new ArrayList<Object>();
					argsOp.add("kukaMoveP");					
					argsOp.addAll(jointPositionsList);
					kukaMovePTPRadian.setParams(argsOp);
					placeObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
					placeObjectKuka.addDevicePrimitive(gripperRG6Place);
					
					argsOp = new ArrayList<Object>();
					argsOp.add("kukaComputeQ");
					argsOp.add(li.get(0));
					argsOp.add(li.get(1));
					argsOp.add(2); // Z = 2
					computeInverseKinematicsKuka.setParams(argsOp);
					jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
					jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					argsOp = new ArrayList<Object>();
					argsOp.add("kukaMoveP");					
					argsOp.addAll(jointPositionsList);
					kukaMovePTPRadian.setParams(argsOp);
					placeObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
					
					argsOp = new ArrayList<Object>();
					argsOp.add("kukaComputeQ");
					argsOp.add(0);
					argsOp.add(0);
					argsOp.add(3); // Z = 3
					computeInverseKinematicsKuka.setParams(argsOp);
					jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
					jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					argsOp = new ArrayList<Object>();
					argsOp.add("kukaMoveP");					
					argsOp.addAll(jointPositionsList);
					kukaMovePTPRadian.setParams(argsOp);
					placeObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
					
	
					// Adding skill to the task
					
					assembleCrossShape.addSkill(graspObjectKuka);
					assembleCrossShape.addSkill(placeObjectKuka);
					graspObjectKuka.removeDevicePrimitives();
					placeObjectKuka.removeDevicePrimitives();
					flag = !flag;
				}
			} else if(computationResultUR && !computationResultKuka) {
				// UR executes the action
				// Picking
				List<Object> argsOp = new ArrayList<Object>();
				argsOp.add("urComputeQ");
				argsOp.add(0);
				argsOp.add(23);
				argsOp.add(2); // Z = 2
				computeInverseKinematicsUR.setParams(argsOp);
				//graspObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
				String jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
				List<String> jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				argsOp = new ArrayList<Object>();
				argsOp.add("urMoveP");
				argsOp.addAll(jointPositionsList);
				ur5eMoveJ.setParams(argsOp);
				graspObjectUR.addDevicePrimitive(ur5eMoveJ);
				
				argsOp = new ArrayList<Object>();
				argsOp.add("urComputeQ");
				argsOp.add(0);
				argsOp.add(23);
				argsOp.add(0); // Z = 0
				computeInverseKinematicsUR.setParams(argsOp);
				jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
				jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				//graspObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
				argsOp = new ArrayList<Object>();
				argsOp.add("urMoveP");
				argsOp.addAll(jointPositionsList);
				ur5eMoveJ.setParams(argsOp);
				graspObjectUR.addDevicePrimitive(ur5eMoveJ);
				graspObjectUR.addDevicePrimitive(gripper2FG7Pick);
				
				argsOp = new ArrayList<Object>();
				argsOp.add("urComputeQ");
				argsOp.add(0);
				argsOp.add(23);
				argsOp.add(2); // Z = 2
				computeInverseKinematicsUR.setParams(argsOp);
				jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
				jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				//graspObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
				argsOp = new ArrayList<Object>();
				argsOp.add("urMoveP");
				argsOp.addAll(jointPositionsList);
				ur5eMoveJ.setParams(argsOp);
				graspObjectUR.addDevicePrimitive(ur5eMoveJ);
				
				argsOp = new ArrayList<Object>();
				argsOp.add("urComputeQ");
				argsOp.add(li.get(0));
				argsOp.add(li.get(1));
				argsOp.add(2); // Z = 2
				computeInverseKinematicsUR.setParams(argsOp);
				//placeObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
				jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
				jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				//graspObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
				argsOp = new ArrayList<Object>();
				argsOp.add("urMoveP");					
				argsOp.addAll(jointPositionsList);
				ur5eMoveJ.setParams(argsOp);
				graspObjectUR.addDevicePrimitive(ur5eMoveJ);
				
				// Placing
				argsOp = new ArrayList<Object>();
				argsOp.add("urComputeQ");
				argsOp.add(li.get(0));
				argsOp.add(li.get(1));
				argsOp.add(0); // Z = 0
				jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
				jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				//placeObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
				argsOp = new ArrayList<Object>();
				argsOp.add("urMoveP");
				argsOp.addAll(jointPositionsList);
				ur5eMoveJ.setParams(argsOp);
				placeObjectUR.addDevicePrimitive(ur5eMoveJ);
				placeObjectUR.addDevicePrimitive(gripper2FG7Place);
				
				argsOp = new ArrayList<Object>();
				argsOp.add("urComputeQ");
				argsOp.add(li.get(0));
				argsOp.add(li.get(1));
				argsOp.add(2); // Z = 2
				jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
				jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				//placeObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
				argsOp = new ArrayList<Object>();
				argsOp.add("urMoveP");					
				argsOp.addAll(jointPositionsList);
				ur5eMoveJ.setParams(argsOp);
				placeObjectUR.addDevicePrimitive(ur5eMoveJ);
				
				argsOp = new ArrayList<Object>();
				argsOp.add("urComputeQ");
				argsOp.add(0);
				argsOp.add(23);
				argsOp.add(3); // Z = 3
				jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
				jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				//placeObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
				argsOp = new ArrayList<Object>();
				argsOp.add("urMoveP");					
				argsOp.addAll(jointPositionsList);
				ur5eMoveJ.setParams(argsOp);
				placeObjectUR.addDevicePrimitive(ur5eMoveJ);
				
				// Adding skill to the task
				assembleCrossShape.addSkill(graspObjectUR);
				assembleCrossShape.addSkill(placeObjectUR);
				
				graspObjectUR.removeDevicePrimitives();
				placeObjectUR.removeDevicePrimitives();
			} else if(!computationResultUR && computationResultKuka) {
				// Kuka executes the action
				// Picking
				List<Object> argsOp = new ArrayList<Object>();
				argsOp.add("kukaComputeQ");
				argsOp.add(0);
				argsOp.add(0);
				argsOp.add(2); // Z = 2
				computeInverseKinematicsKuka.setParams(argsOp);
				//graspObjectKuka.addDevicePrimitive(computeInverseKinematicsKuka);
				String jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
				List<String> jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				argsOp = new ArrayList<Object>();
				argsOp.add("kukaMoveP");
				argsOp.addAll(jointPositionsList);
				kukaMovePTPRadian.setParams(argsOp);
				graspObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
				
				argsOp = new ArrayList<Object>();
				argsOp.add("kukaComputeQ");
				argsOp.add(0);
				argsOp.add(0);
				argsOp.add(0); // Z = 0
				computeInverseKinematicsKuka.setParams(argsOp);
				jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
				jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				argsOp = new ArrayList<Object>();
				argsOp.add("kukaMoveP");
				argsOp.addAll(jointPositionsList);
				kukaMovePTPRadian.setParams(argsOp);
				graspObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
				
				graspObjectKuka.addDevicePrimitive(gripperRG6Pick);
				
				argsOp = new ArrayList<Object>();
				argsOp.add("kukaComputeQ");
				argsOp.add(0);
				argsOp.add(0);
				argsOp.add(2); // Z = 2
				computeInverseKinematicsKuka.setParams(argsOp);
				jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
				jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				argsOp = new ArrayList<Object>();
				argsOp.add("kukaMoveP");					
				argsOp.addAll(jointPositionsList);
				kukaMovePTPRadian.setParams(argsOp);
				graspObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
				
				argsOp = new ArrayList<Object>();
				argsOp.add("kukaComputeQ");
				argsOp.add(li.get(0));
				argsOp.add(li.get(1));
				argsOp.add(2); // Z = 2
				computeInverseKinematicsKuka.setParams(argsOp);
				//placeObjectKuka.addDevicePrimitive(computeInverseKinematicsKuka);
				jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
				jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				argsOp = new ArrayList<Object>();
				argsOp.add("kukaMoveP");					
				argsOp.addAll(jointPositionsList);
				kukaMovePTPRadian.setParams(argsOp);
				graspObjectKuka.addDevicePrimitive(kukaMovePTPRadian);					
				
				// Placing

				argsOp = new ArrayList<Object>();
				argsOp.add("kukaComputeQ");
				argsOp.add(li.get(0));
				argsOp.add(li.get(1));
				argsOp.add(0); // Z = 0
				computeInverseKinematicsKuka.setParams(argsOp);
				jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
				jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				argsOp = new ArrayList<Object>();
				argsOp.add("kukaMoveP");					
				argsOp.addAll(jointPositionsList);
				kukaMovePTPRadian.setParams(argsOp);
				placeObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
				placeObjectKuka.addDevicePrimitive(gripperRG6Place);
				
				argsOp = new ArrayList<Object>();
				argsOp.add("kukaComputeQ");
				argsOp.add(li.get(0));
				argsOp.add(li.get(1));
				argsOp.add(2); // Z = 2
				computeInverseKinematicsKuka.setParams(argsOp);
				jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
				jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				argsOp = new ArrayList<Object>();
				argsOp.add("kukaMoveP");					
				argsOp.addAll(jointPositionsList);
				kukaMovePTPRadian.setParams(argsOp);
				placeObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
				
				argsOp = new ArrayList<Object>();
				argsOp.add("kukaComputeQ");
				argsOp.add(0);
				argsOp.add(0);
				argsOp.add(3); // Z = 3
				computeInverseKinematicsKuka.setParams(argsOp);
				jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
				jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				argsOp = new ArrayList<Object>();
				argsOp.add("kukaMoveP");					
				argsOp.addAll(jointPositionsList);
				kukaMovePTPRadian.setParams(argsOp);
				placeObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
				// Adding skill to the task
				
				assembleCrossShape.addSkill(graspObjectKuka);
				assembleCrossShape.addSkill(placeObjectKuka);
				graspObjectKuka.removeDevicePrimitives();
				placeObjectKuka.removeDevicePrimitives();

			} else {
				System.out.println("None of the robots can compute inverse kinematics for the position");
			}
			
			System.out.println("Task " + assembleCrossShape.getOwlIndividual() + " for object " + index + " is Combination Of:" );
			for (Object o : assembleCrossShape.getIsCombinationOf()) {
				System.out.println(o.toString());
				//assembleCrossShape.removeIsCombinationOf((SkillBasedDTOntology.Operation) o);
			}
			
			
		}
		
		
		
		/***** Execution of the Square Shape task *****/
		
		boolean flagSquare = false;
		for (List li : assembleSquareShape.getShape()) {
			int index = assembleSquareShape.getShape().indexOf(li);
			
			List<Object> flexCellPoint = new ArrayList<Object>();
			flexCellPoint.add("urComputeIK");
			flexCellPoint.add(li.get(0));
			flexCellPoint.add(li.get(1));
			flexCellPoint.add(0);
			boolean computationResultUR = Boolean.valueOf((String)computeIKFeasibilityUR.executeSynchronousOperation(flexCellPoint));
			flexCellPoint = new ArrayList<Object>();
			flexCellPoint.add("kukaComputeIK");
			flexCellPoint.add(li.get(0));
			flexCellPoint.add(li.get(1));
			flexCellPoint.add(0);
			boolean computationResultKuka = Boolean.valueOf((String)computeIKFeasibilityKuka.executeSynchronousOperation(flexCellPoint));
				
			
			if (computationResultUR && computationResultKuka) {
				if (!flagSquare) {
					// UR executes the action
					// Picking
					List<Object> argsOp = new ArrayList<Object>();
					argsOp.add("urComputeQ");
					argsOp.add(0);
					argsOp.add(23);
					argsOp.add(2); // Z = 2
					computeInverseKinematicsUR.setParams(argsOp);
					//graspObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
					String jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
					List<String> jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					argsOp = new ArrayList<Object>();
					argsOp.add("urMoveP");
					argsOp.addAll(jointPositionsList);
					ur5eMoveJ.setParams(argsOp);
					graspObjectUR.addDevicePrimitive(ur5eMoveJ);
					
					argsOp = new ArrayList<Object>();
					argsOp.add("urComputeQ");
					argsOp.add(0);
					argsOp.add(23);
					argsOp.add(0); // Z = 0
					computeInverseKinematicsUR.setParams(argsOp);
					jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
					jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					//graspObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
					argsOp = new ArrayList<Object>();
					argsOp.add("urMoveP");
					argsOp.addAll(jointPositionsList);
					ur5eMoveJ.setParams(argsOp);
					graspObjectUR.addDevicePrimitive(ur5eMoveJ);					
					graspObjectUR.addDevicePrimitive(gripper2FG7Pick);
					
					argsOp = new ArrayList<Object>();
					argsOp.add("urComputeQ");
					argsOp.add(0);
					argsOp.add(23);
					argsOp.add(2); // Z = 2
					computeInverseKinematicsUR.setParams(argsOp);
					jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
					jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					//graspObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
					argsOp = new ArrayList<Object>();
					argsOp.add("urMoveP");
					argsOp.addAll(jointPositionsList);
					ur5eMoveJ.setParams(argsOp);
					graspObjectUR.addDevicePrimitive(ur5eMoveJ);
					
					argsOp = new ArrayList<Object>();
					argsOp.add("urComputeQ");
					argsOp.add(li.get(0));
					argsOp.add(li.get(1));
					argsOp.add(2); // Z = 2
					computeInverseKinematicsUR.setParams(argsOp);
					//placeObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
					jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
					jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					//graspObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
					argsOp = new ArrayList<Object>();
					argsOp.add("urMoveP");					
					argsOp.addAll(jointPositionsList);
					ur5eMoveJ.setParams(argsOp);
					graspObjectUR.addDevicePrimitive(ur5eMoveJ);
					
					// Placing
					argsOp = new ArrayList<Object>();
					argsOp.add("urComputeQ");
					argsOp.add(li.get(0));
					argsOp.add(li.get(1));
					argsOp.add(0); // Z = 0
					computeInverseKinematicsUR.setParams(argsOp);
					jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
					jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					//graspObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
					argsOp = new ArrayList<Object>();
					argsOp.add("urMoveP");
					argsOp.addAll(jointPositionsList);
					ur5eMoveJ.setParams(argsOp);
					placeObjectUR.addDevicePrimitive(ur5eMoveJ);					
					placeObjectUR.addDevicePrimitive(gripper2FG7Place);
					
					argsOp = new ArrayList<Object>();
					argsOp.add("urComputeQ");
					argsOp.add(li.get(0));
					argsOp.add(li.get(1));
					argsOp.add(2); // Z = 2
					computeInverseKinematicsUR.setParams(argsOp);
					jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
					jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					//graspObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
					argsOp = new ArrayList<Object>();
					argsOp.add("urMoveP");					
					argsOp.addAll(jointPositionsList);
					ur5eMoveJ.setParams(argsOp);
					placeObjectUR.addDevicePrimitive(ur5eMoveJ);
					
					argsOp = new ArrayList<Object>();
					argsOp.add("urComputeQ");
					argsOp.add(0);
					argsOp.add(23);
					argsOp.add(3); // Z = 3
					jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
					jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					//placeObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
					argsOp = new ArrayList<Object>();
					argsOp.add("urMoveP");					
					argsOp.addAll(jointPositionsList);
					ur5eMoveJ.setParams(argsOp);
					placeObjectUR.addDevicePrimitive(ur5eMoveJ);
					
					// Adding skill to the task
					assembleSquareShape.addSkill(graspObjectUR);
					assembleSquareShape.addSkill(placeObjectUR);
					
					graspObjectUR.removeDevicePrimitives();
					placeObjectUR.removeDevicePrimitives();
					flagSquare = !flagSquare;
				}else {
					// Kuka executes the action
					// Picking
					List<Object> argsOp = new ArrayList<Object>();
					argsOp.add("kukaComputeQ");
					argsOp.add(0);
					argsOp.add(0);
					argsOp.add(2); // Z = 2
					computeInverseKinematicsKuka.setParams(argsOp);
					//graspObjectKuka.addDevicePrimitive(computeInverseKinematicsKuka);
					String jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
					List<String> jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					argsOp = new ArrayList<Object>();
					argsOp.add("kukaMoveP");
					argsOp.addAll(jointPositionsList);
					kukaMovePTPRadian.setParams(argsOp);
					graspObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
					
					argsOp = new ArrayList<Object>();
					argsOp.add("kukaComputeQ");
					argsOp.add(0);
					argsOp.add(0);
					argsOp.add(0); // Z = 0
					computeInverseKinematicsKuka.setParams(argsOp);
					jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
					jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					argsOp = new ArrayList<Object>();
					argsOp.add("kukaMoveP");
					argsOp.addAll(jointPositionsList);
					kukaMovePTPRadian.setParams(argsOp);
					graspObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
					
					graspObjectKuka.addDevicePrimitive(gripperRG6Pick);
					
					argsOp = new ArrayList<Object>();
					argsOp.add("kukaComputeQ");
					argsOp.add(0);
					argsOp.add(0);
					argsOp.add(2); // Z = 2
					computeInverseKinematicsKuka.setParams(argsOp);
					jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
					jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					argsOp = new ArrayList<Object>();
					argsOp.add("kukaMoveP");					
					argsOp.addAll(jointPositionsList);
					kukaMovePTPRadian.setParams(argsOp);
					graspObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
					
					argsOp = new ArrayList<Object>();
					argsOp.add("kukaComputeQ");
					argsOp.add(li.get(0));
					argsOp.add(li.get(1));
					argsOp.add(2); // Z = 2
					computeInverseKinematicsKuka.setParams(argsOp);
					//placeObjectKuka.addDevicePrimitive(computeInverseKinematicsKuka);
					jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
					jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					argsOp = new ArrayList<Object>();
					argsOp.add("kukaMoveP");					
					argsOp.addAll(jointPositionsList);
					kukaMovePTPRadian.setParams(argsOp);
					graspObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
					
					// Placing
					argsOp = new ArrayList<Object>();
					argsOp.add("kukaComputeQ");
					argsOp.add(li.get(0));
					argsOp.add(li.get(1));
					argsOp.add(0); // Z = 0
					computeInverseKinematicsKuka.setParams(argsOp);
					jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
					jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					argsOp = new ArrayList<Object>();
					argsOp.add("kukaMoveP");					
					argsOp.addAll(jointPositionsList);
					kukaMovePTPRadian.setParams(argsOp);
					placeObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
					placeObjectKuka.addDevicePrimitive(gripperRG6Place);
					
					argsOp = new ArrayList<Object>();
					argsOp.add("kukaComputeQ");
					argsOp.add(li.get(0));
					argsOp.add(li.get(1));
					argsOp.add(2); // Z = 2
					computeInverseKinematicsKuka.setParams(argsOp);
					jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
					jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					argsOp = new ArrayList<Object>();
					argsOp.add("kukaMoveP");					
					argsOp.addAll(jointPositionsList);
					kukaMovePTPRadian.setParams(argsOp);
					placeObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
					
					argsOp = new ArrayList<Object>();
					argsOp.add("kukaComputeQ");
					argsOp.add(0);
					argsOp.add(0);
					argsOp.add(3); // Z = 3
					computeInverseKinematicsKuka.setParams(argsOp);
					jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
					jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
					argsOp = new ArrayList<Object>();
					argsOp.add("kukaMoveP");					
					argsOp.addAll(jointPositionsList);
					kukaMovePTPRadian.setParams(argsOp);
					placeObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
					// Adding skill to the task
					
					assembleSquareShape.addSkill(graspObjectKuka);
					assembleSquareShape.addSkill(placeObjectKuka);
					graspObjectKuka.removeDevicePrimitives();
					placeObjectKuka.removeDevicePrimitives();
					flagSquare = !flagSquare;
				}
				
			} else if(computationResultUR && !computationResultKuka) {
				// UR executes the action
				// Picking
				List<Object> argsOp = new ArrayList<Object>();
				argsOp.add("urComputeQ");
				argsOp.add(0);
				argsOp.add(23);
				argsOp.add(2); // Z = 2
				computeInverseKinematicsUR.setParams(argsOp);
				//graspObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
				String jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
				List<String> jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				argsOp = new ArrayList<Object>();
				argsOp.add("urMoveP");
				argsOp.addAll(jointPositionsList);
				ur5eMoveJ.setParams(argsOp);
				graspObjectUR.addDevicePrimitive(ur5eMoveJ);
				
				argsOp = new ArrayList<Object>();
				argsOp.add("urComputeQ");
				argsOp.add(0);
				argsOp.add(23);
				argsOp.add(0); // Z = 0
				computeInverseKinematicsUR.setParams(argsOp);
				jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
				jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				//graspObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
				argsOp = new ArrayList<Object>();
				argsOp.add("urMoveP");
				argsOp.addAll(jointPositionsList);
				ur5eMoveJ.setParams(argsOp);
				graspObjectUR.addDevicePrimitive(ur5eMoveJ);					
				graspObjectUR.addDevicePrimitive(gripper2FG7Pick);
				
				argsOp = new ArrayList<Object>();
				argsOp.add("urComputeQ");
				argsOp.add(0);
				argsOp.add(23);
				argsOp.add(2); // Z = 2
				computeInverseKinematicsUR.setParams(argsOp);
				jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
				jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				//graspObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
				argsOp = new ArrayList<Object>();
				argsOp.add("urMoveP");
				argsOp.addAll(jointPositionsList);
				ur5eMoveJ.setParams(argsOp);
				graspObjectUR.addDevicePrimitive(ur5eMoveJ);
				
				argsOp = new ArrayList<Object>();
				argsOp.add("urComputeQ");
				argsOp.add(li.get(0));
				argsOp.add(li.get(1));
				argsOp.add(2); // Z = 2
				computeInverseKinematicsUR.setParams(argsOp);
				//placeObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
				jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
				jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				//graspObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
				argsOp = new ArrayList<Object>();
				argsOp.add("urMoveP");					
				argsOp.addAll(jointPositionsList);
				ur5eMoveJ.setParams(argsOp);
				graspObjectUR.addDevicePrimitive(ur5eMoveJ);
				
				// Placing
				argsOp = new ArrayList<Object>();
				argsOp.add("urComputeQ");
				argsOp.add(li.get(0));
				argsOp.add(li.get(1));
				argsOp.add(0); // Z = 0
				computeInverseKinematicsUR.setParams(argsOp);
				jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
				jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				//graspObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
				argsOp = new ArrayList<Object>();
				argsOp.add("urMoveP");
				argsOp.addAll(jointPositionsList);
				ur5eMoveJ.setParams(argsOp);
				placeObjectUR.addDevicePrimitive(ur5eMoveJ);					
				placeObjectUR.addDevicePrimitive(gripper2FG7Place);
				
				argsOp = new ArrayList<Object>();
				argsOp.add("urComputeQ");
				argsOp.add(li.get(0));
				argsOp.add(li.get(1));
				argsOp.add(2); // Z = 2
				computeInverseKinematicsUR.setParams(argsOp);
				jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
				jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				//graspObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
				argsOp = new ArrayList<Object>();
				argsOp.add("urMoveP");					
				argsOp.addAll(jointPositionsList);
				ur5eMoveJ.setParams(argsOp);
				placeObjectUR.addDevicePrimitive(ur5eMoveJ);
				
				argsOp = new ArrayList<Object>();
				argsOp.add("urComputeQ");
				argsOp.add(0);
				argsOp.add(23);
				argsOp.add(3); // Z = 3
				jointPositionsObject = String.valueOf(computeInverseKinematicsUR.executeSynchronousOperation(argsOp));
				jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				//placeObjectUR.addDevicePrimitive(computeInverseKinematicsUR);
				argsOp = new ArrayList<Object>();
				argsOp.add("urMoveP");					
				argsOp.addAll(jointPositionsList);
				ur5eMoveJ.setParams(argsOp);
				placeObjectUR.addDevicePrimitive(ur5eMoveJ);
				
				// Adding skill to the task
				assembleSquareShape.addSkill(graspObjectUR);
				assembleSquareShape.addSkill(placeObjectUR);
				
				graspObjectUR.removeDevicePrimitives();
				placeObjectUR.removeDevicePrimitives();
			} else if(!computationResultUR && computationResultKuka) {
				// Kuka executes the action
				// Kuka executes the action
				// Picking
				List<Object> argsOp = new ArrayList<Object>();
				argsOp.add("kukaComputeQ");
				argsOp.add(0);
				argsOp.add(0);
				argsOp.add(2); // Z = 2
				computeInverseKinematicsKuka.setParams(argsOp);
				//graspObjectKuka.addDevicePrimitive(computeInverseKinematicsKuka);
				String jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
				List<String> jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				argsOp = new ArrayList<Object>();
				argsOp.add("kukaMoveP");
				argsOp.addAll(jointPositionsList);
				kukaMovePTPRadian.setParams(argsOp);
				graspObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
				
				argsOp = new ArrayList<Object>();
				argsOp.add("kukaComputeQ");
				argsOp.add(0);
				argsOp.add(0);
				argsOp.add(0); // Z = 0
				computeInverseKinematicsKuka.setParams(argsOp);
				jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
				jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				argsOp = new ArrayList<Object>();
				argsOp.add("kukaMoveP");
				argsOp.addAll(jointPositionsList);
				kukaMovePTPRadian.setParams(argsOp);
				graspObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
				
				graspObjectKuka.addDevicePrimitive(gripperRG6Pick);
				
				argsOp = new ArrayList<Object>();
				argsOp.add("kukaComputeQ");
				argsOp.add(0);
				argsOp.add(0);
				argsOp.add(2); // Z = 2
				computeInverseKinematicsKuka.setParams(argsOp);
				jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
				jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				argsOp = new ArrayList<Object>();
				argsOp.add("kukaMoveP");					
				argsOp.addAll(jointPositionsList);
				kukaMovePTPRadian.setParams(argsOp);
				graspObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
				
				argsOp = new ArrayList<Object>();
				argsOp.add("kukaComputeQ");
				argsOp.add(li.get(0));
				argsOp.add(li.get(1));
				argsOp.add(2); // Z = 2
				computeInverseKinematicsKuka.setParams(argsOp);
				//placeObjectKuka.addDevicePrimitive(computeInverseKinematicsKuka);
				jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
				jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				argsOp = new ArrayList<Object>();
				argsOp.add("kukaMoveP");					
				argsOp.addAll(jointPositionsList);
				kukaMovePTPRadian.setParams(argsOp);
				graspObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
				
				// Placing
				argsOp = new ArrayList<Object>();
				argsOp.add("kukaComputeQ");
				argsOp.add(li.get(0));
				argsOp.add(li.get(1));
				argsOp.add(0); // Z = 0
				computeInverseKinematicsKuka.setParams(argsOp);
				jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
				jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				argsOp = new ArrayList<Object>();
				argsOp.add("kukaMoveP");					
				argsOp.addAll(jointPositionsList);
				kukaMovePTPRadian.setParams(argsOp);
				placeObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
				placeObjectKuka.addDevicePrimitive(gripperRG6Place);
				
				argsOp = new ArrayList<Object>();
				argsOp.add("kukaComputeQ");
				argsOp.add(li.get(0));
				argsOp.add(li.get(1));
				argsOp.add(2); // Z = 2
				computeInverseKinematicsKuka.setParams(argsOp);
				jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
				jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				argsOp = new ArrayList<Object>();
				argsOp.add("kukaMoveP");					
				argsOp.addAll(jointPositionsList);
				kukaMovePTPRadian.setParams(argsOp);
				placeObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
				
				argsOp = new ArrayList<Object>();
				argsOp.add("kukaComputeQ");
				argsOp.add(0);
				argsOp.add(0);
				argsOp.add(3); // Z = 3
				computeInverseKinematicsKuka.setParams(argsOp);
				jointPositionsObject = String.valueOf(computeInverseKinematicsKuka.executeSynchronousOperation(argsOp));
				jointPositionsList = new ArrayList<String>(Arrays.asList(jointPositionsObject.split(",")));
				argsOp = new ArrayList<Object>();
				argsOp.add("kukaMoveP");					
				argsOp.addAll(jointPositionsList);
				kukaMovePTPRadian.setParams(argsOp);
				placeObjectKuka.addDevicePrimitive(kukaMovePTPRadian);
				// Adding skill to the task
				
				assembleSquareShape.addSkill(graspObjectKuka);
				assembleSquareShape.addSkill(placeObjectKuka);
				graspObjectKuka.removeDevicePrimitives();
				placeObjectKuka.removeDevicePrimitives();
			} else {
				System.out.println("None of the robots can compute inverse kinematics for the position");
			}
			
			System.out.println("Task " + assembleSquareShape.getOwlIndividual() + " for object " + index + " is Combination Of:" );
			for (Object o : assembleSquareShape.getIsCombinationOf()) {
				System.out.println(o.toString());
				//assembleSquareShape.removeIsCombinationOf((SkillBasedDTOntology.Operation) o);
			}
		}
		
		
		/***** Getting data from the ontology *****/
		
		dtManager.swrlEngine.infer();
		runSQWRLQueries();
		
		
		/***** Executing the Tasks *****/
		/*** THIS DOES NOT WORK BECAUSE OF TRYING TO EXECUTE OPERATION FROM THE ONTOLOGY CLASS
		Collection<Task> tasksFlexcell = (Collection<Task>) flexCell.getHasTask();
		for (Task task : tasksFlexcell) {
			task.executeTask();
		}***/
				
		assembleCrossShape.executeTask();
		assembleSquareShape.executeTask();
		
		
		
		
		

		/************ Threads ***********/
		// Event Thread

		/*Thread eventThread = new Thread(() -> {
			new Timer().scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					try {
						//Object value = EventHandler.readAASVariable("urn:dtexamples.AU-AAS.UR5e_AAS",
						//		"Current_Joint_Position_5");
						Object value = EventHandler.readAASVariable(ur5eDT,"Current_Joint_Position_5",false);
						System.out.println("UR5e Current_Joint_Position_5 (Experimental): " + value);
						value = EventHandler.readAASVariable(ur5eDT,"Current_Joint_Position_5",true);
						System.out.println("UR5e Current_Joint_Position_5 (Actual): " + value);
						value = EventHandler.readAASVariable(kukaDT,"Current_Joint_Position_5",false);
						System.out.println("Kuka Current_Joint_Position_5 (Experimental): " + value);
						value = EventHandler.readAASVariable(kukaDT,"Current_Joint_Position_5",true);
						System.out.println("Kuka Current_Joint_Position_5 (Actual): " + value);
						//EventHandler.updateStatePropertyAASZMQ();
						
						// Relationships
						Collection<DigitalTwin> objectsOfFlexcell = (Collection<DigitalTwin>) flexCell.getIsComposedOf();
						for (DigitalTwin dt : objectsOfFlexcell) {
							System.out.println(dt.getOwlIndividual() + " composes Flexcell");
						}
						
						Collection<DigitalTwin> kukaCollaborators = (Collection<DigitalTwin>) kukaDT.getCooperatesWith();
						for (DigitalTwin dt : kukaCollaborators) {
							System.out.println(dt.getOwlIndividual() + " collaborates with Kuka");
						}
						Collection<DigitalTwin> urCollaborators = (Collection<DigitalTwin>) ur5eDT.getCooperatesWith();
						for (DigitalTwin dt : urCollaborators) {
							System.out.println(dt.getOwlIndividual() + " collaborates with UR5e");
						}
						


					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}, 5000, 5000);

		});

		try {
			eventThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		eventThread.start();*/
	}
	
	private static void waitTime(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void setSWRLRules() {
		try {
			dtManager.swrlEngine.createSWRLRule("inherited_device_primitives",
					"DigitalTwin(?cdt) ^ DigitalTwin(?sdt) ^ isComposedOf(?cdt,?sdt) ^ hasOperation(?sdt,?op)"
					+ " ^ DevicePrimitive(?op) -> hasOperation(?cdt,?op)");
			dtManager.swrlEngine.createSWRLRule("inherited_skills",
					"DigitalTwin(?cdt) ^ DigitalTwin(?sdt) ^ isComposedOf(?cdt,?sdt) ^ hasOperation(?sdt,?op)"
					+ " ^ Skill(?op) -> hasOperation(?cdt,?op)");
			dtManager.swrlEngine.createSWRLRule("inherited_skills_from_definition",
					"Task(?tk) ^ isCombinationOf(?tk,?sk) ^ Skill(?sk) ^ isCombinationOf(?sk,?dp)"
					+ " ^ DigitalTwin(?dt) ^ hasOperation(?dt,?tk) -> hasOperation(?dt,?sk) ^ hasOperation(?dt,?dp)");
			dtManager.swrlEngine.createSWRLRule("composed_digital_twin",
					"DigitalTwin(?dt) ^ isComposedOf(?dt,?sdt) ^ DigitalTwin(?sdt)  -> isComposedDigitalTwin(?dt,true)");
			
		} catch (SWRLParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SWRLBuiltInException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void runSQWRLQueries() {
		System.out.println("Executing queries");
		 SQWRLResult qResult;
		 String result = "";
		 try {
			qResult = dtManager.queryEngine.runSQWRLQuery("QueryOfSkillsByDT",
					"DigitalTwin(?dt) ^ hasOperation(?dt,?sk) ^ Skill(?sk) -> sqwrl:selectDistinct(?dt,?sk)");
			result = "Skills per Digital Twin:\n";
	        while (qResult.next()) {
	        	result += "DigitalTwin: " + qResult.getValue("dt") + " has Skill: " + qResult.getValue("sk") + "\n";
	        }
	        System.out.println(result);
	         
	        qResult = dtManager.queryEngine.runSQWRLQuery("QueryOfDevicePrimitivesByDT",
						"DigitalTwin(?dt) ^ hasOperation(?dt,?dp) ^ DevicePrimitive(?dp) -> sqwrl:selectDistinct(?dt,?dp)");
			result = "Skills per Digital Twin:\n";
		    while (qResult.next()) {
		    	result += "DigitalTwin: " + qResult.getValue("dt") + " has Device Primitive: " + qResult.getValue("dp") + "\n";
		    }
		    System.out.println(result);
		    
		    qResult = dtManager.queryEngine.runSQWRLQuery("QueryOfDTs",
					"DigitalTwin(?dt) -> sqwrl:selectDistinct(?dt)");
			result = "Digital Twin query:\n";
		    while (qResult.next()) {
		    	result += "DigitalTwin: " + qResult.getValue("dt")+ "\n";
		    }
		    System.out.println(result);
		    
		    qResult = dtManager.queryEngine.runSQWRLQuery("QueryComposedDT",
					"DigitalTwin(?cdt) ^ DigitalTwin(?sdt) ^ isComposedOf(?cdt,?sdt) -> sqwrl:selectDistinct(?cdt)");
			result = "Composed Digital Twin query:\n";
		    while (qResult.next()) {
		    	result += "Composed DigitalTwin: " + qResult.getValue("cdt")+ "\n";
		    }
		    System.out.println(result);
		    
		    qResult = dtManager.queryEngine.runSQWRLQuery("QueryOfDTsThatCanPickMoveAndPick",
					"DigitalTwin(?dt) ^ hasOperation(?dt,?dp1) ^ DevicePrimitive(?dp1) "
					+ "^ hasOperation(?dt,?dp2) ^ DevicePrimitive(?dp2) ^ hasName(?dp1,?n1) ^ hasName(?dp2,?n2)"
					+ " ^ swrlb:containsIgnoreCase(?n1,\"pick\") ^ swrlb:containsIgnoreCase(?n2,\"move\")  -> sqwrl:selectDistinct(?dt)");
			result = "Digital Twin that can perform move and pick operations:\n";
		    while (qResult.next()) {
		    	result += "DigitalTwin with move and pick capabilities: " + qResult.getValue("dt") + "\n";
		    }
		    System.out.println(result);
			 
			/*SQWRLResult resultEx = dtManager.queryEngine.runSQWRLQuery("q1", "swrlb:add(?x, 2, 4) -> sqwrl:select(?x)");
			while (resultEx.next()) {
		        result += "Sum: " + resultEx.getValue("x") + "\n";
		    }
			System.out.println(result);
			 
			qResult = dtManager.queryEngine.runSQWRLQuery("QueryOfSkillsByDT");
			result = "Skills per Digital Twin:\n";
		    while (qResult.next()) {
		        result += "DigitalTwin: " + qResult.getValue("dt") + " has Skill: " + qResult.getValue("sk") + "\n";
		    }
		    System.out.println(result);
		    
		    qResult = dtManager.queryEngine.runSQWRLQuery("QueryOfDevicePrimitivesByDT");
			result = "Device Primitives per Digital Twin:\n";
		    while (qResult.next()) {
		    	result += "DigitalTwin: " + qResult.getValue("dt") + " has Device Primitive: " + qResult.getValue("dp") + "\n";
		    }
		    System.out.println(result);
		    
		    qResult = dtManager.queryEngine.runSQWRLQuery("QueryOfDTs");
			result = "Digital Twin query:\n";
		    while (qResult.next()) {
		    	result += "DigitalTwin: " + qResult.getValue("dt")+ "\n";
		    }
		    System.out.println(result);
		    
		    qResult = dtManager.queryEngine.runSQWRLQuery("QueryComposedDT");
			result = "Composed Digital Twin query:\n";
		    while (qResult.next()) {
		    	result += "Composed DigitalTwin: " + qResult.getValue("cdt")+ "\n";
		    }
		    System.out.println(result);*/
	         
		} catch (SQWRLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SWRLParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

         
	}

}
