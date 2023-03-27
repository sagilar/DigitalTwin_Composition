package SkillBasedDTOntology;



import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;

/**
 * Vocabulary class to provide access to the Manchester OWL API representatives for 
 * various entities in the ontology used to generate this code.<p> 
 * 
 * Generated by Protege (http://protege.stanford.edu).<br>
 * Source Class: ${javaClass}
 *
 * @version generated on Mon Mar 27 14:26:55 CEST 2023 by au698550
 */

public class Vocabulary {

	private static final OWLDataFactory factory = OWLManager.createOWLOntologyManager().getOWLDataFactory();

    /* ***************************************************
     * Class http://www.semanticweb.org/ontologies/cooperativeDTs#Attribute
     */

    /**
     * A constant to give access to the Manchester OWL api representation of the class ATTRIBUTE.<p>
     * 
     */
    public static final OWLClass CLASS_ATTRIBUTE = factory.getOWLClass(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#Attribute"));

    /* ***************************************************
     * Class http://www.semanticweb.org/ontologies/cooperativeDTs#Behavior
     */

    /**
     * A constant to give access to the Manchester OWL api representation of the class BEHAVIOR.<p>
     * 
     */
    public static final OWLClass CLASS_BEHAVIOR = factory.getOWLClass(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#Behavior"));

    /* ***************************************************
     * Class http://www.semanticweb.org/ontologies/cooperativeDTs#DevicePrimitive
     */

    /**
     * A constant to give access to the Manchester OWL api representation of the class DEVICEPRIMITIVE.<p>
     * 
     */
    public static final OWLClass CLASS_DEVICEPRIMITIVE = factory.getOWLClass(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#DevicePrimitive"));

    /* ***************************************************
     * Class http://www.semanticweb.org/ontologies/cooperativeDTs#DigitalTwin
     */

    /**
     * A constant to give access to the Manchester OWL api representation of the class DIGITALTWIN.<p>
     * 
     */
    public static final OWLClass CLASS_DIGITALTWIN = factory.getOWLClass(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#DigitalTwin"));

    /* ***************************************************
     * Class http://www.semanticweb.org/ontologies/cooperativeDTs#Operation
     */

    /**
     * A constant to give access to the Manchester OWL api representation of the class OPERATION.<p>
     * 
     */
    public static final OWLClass CLASS_OPERATION = factory.getOWLClass(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#Operation"));

    /* ***************************************************
     * Class http://www.semanticweb.org/ontologies/cooperativeDTs#Relationship
     */

    /**
     * A constant to give access to the Manchester OWL api representation of the class RELATIONSHIP.<p>
     * 
     */
    public static final OWLClass CLASS_RELATIONSHIP = factory.getOWLClass(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#Relationship"));

    /* ***************************************************
     * Class http://www.semanticweb.org/ontologies/cooperativeDTs#Skill
     */

    /**
     * A constant to give access to the Manchester OWL api representation of the class SKILL.<p>
     * 
     */
    public static final OWLClass CLASS_SKILL = factory.getOWLClass(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#Skill"));

    /* ***************************************************
     * Class http://www.semanticweb.org/ontologies/cooperativeDTs#Task
     */

    /**
     * A constant to give access to the Manchester OWL api representation of the class TASK.<p>
     * 
     */
    public static final OWLClass CLASS_TASK = factory.getOWLClass(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#Task"));

    /* ***************************************************
     * Object Property http://www.semanticweb.org/ontologies/cooperativeDTs#cooperatesWith
     */
     
    /**
     * A constant to give access to the Manchester OWL API representation of the object property COOPERATESWITH.<p>
     * 
     */
    public static final OWLObjectProperty OBJECT_PROPERTY_COOPERATESWITH = factory.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#cooperatesWith"));

    /* ***************************************************
     * Object Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasAttribute
     */
     
    /**
     * A constant to give access to the Manchester OWL API representation of the object property HASATTRIBUTE.<p>
     * 
     */
    public static final OWLObjectProperty OBJECT_PROPERTY_HASATTRIBUTE = factory.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#hasAttribute"));

    /* ***************************************************
     * Object Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasBehavior
     */
     
    /**
     * A constant to give access to the Manchester OWL API representation of the object property HASBEHAVIOR.<p>
     * 
     */
    public static final OWLObjectProperty OBJECT_PROPERTY_HASBEHAVIOR = factory.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#hasBehavior"));

    /* ***************************************************
     * Object Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasDevicePrimitive
     */
     
    /**
     * A constant to give access to the Manchester OWL API representation of the object property HASDEVICEPRIMITIVE.<p>
     * 
     */
    public static final OWLObjectProperty OBJECT_PROPERTY_HASDEVICEPRIMITIVE = factory.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#hasDevicePrimitive"));

    /* ***************************************************
     * Object Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasDigitalTwinRelationship
     */
     
    /**
     * A constant to give access to the Manchester OWL API representation of the object property HASDIGITALTWINRELATIONSHIP.<p>
     * 
     */
    public static final OWLObjectProperty OBJECT_PROPERTY_HASDIGITALTWINRELATIONSHIP = factory.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#hasDigitalTwinRelationship"));

    /* ***************************************************
     * Object Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasOperation
     */
     
    /**
     * A constant to give access to the Manchester OWL API representation of the object property HASOPERATION.<p>
     * 
     */
    public static final OWLObjectProperty OBJECT_PROPERTY_HASOPERATION = factory.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#hasOperation"));

    /* ***************************************************
     * Object Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasRelationship
     */
     
    /**
     * A constant to give access to the Manchester OWL API representation of the object property HASRELATIONSHIP.<p>
     * 
     */
    public static final OWLObjectProperty OBJECT_PROPERTY_HASRELATIONSHIP = factory.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#hasRelationship"));

    /* ***************************************************
     * Object Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasSkill
     */
     
    /**
     * A constant to give access to the Manchester OWL API representation of the object property HASSKILL.<p>
     * 
     */
    public static final OWLObjectProperty OBJECT_PROPERTY_HASSKILL = factory.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#hasSkill"));

    /* ***************************************************
     * Object Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasTask
     */
     
    /**
     * A constant to give access to the Manchester OWL API representation of the object property HASTASK.<p>
     * 
     */
    public static final OWLObjectProperty OBJECT_PROPERTY_HASTASK = factory.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#hasTask"));

    /* ***************************************************
     * Object Property http://www.semanticweb.org/ontologies/cooperativeDTs#isCombinationOf
     */
     
    /**
     * A constant to give access to the Manchester OWL API representation of the object property ISCOMBINATIONOF.<p>
     * 
     */
    public static final OWLObjectProperty OBJECT_PROPERTY_ISCOMBINATIONOF = factory.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#isCombinationOf"));

    /* ***************************************************
     * Object Property http://www.semanticweb.org/ontologies/cooperativeDTs#isComposedOf
     */
     
    /**
     * A constant to give access to the Manchester OWL API representation of the object property ISCOMPOSEDOF.<p>
     * 
     */
    public static final OWLObjectProperty OBJECT_PROPERTY_ISCOMPOSEDOF = factory.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#isComposedOf"));

    /* ***************************************************
     * Object Property http://www.semanticweb.org/ontologies/cooperativeDTs#isConnectedTo
     */
     
    /**
     * A constant to give access to the Manchester OWL API representation of the object property ISCONNECTEDTO.<p>
     * 
     */
    public static final OWLObjectProperty OBJECT_PROPERTY_ISCONNECTEDTO = factory.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#isConnectedTo"));

    /* ***************************************************
     * Object Property http://www.w3.org/2002/07/owl#topObjectProperty
     */
     
    /**
     * A constant to give access to the Manchester OWL API representation of the object property OWL:TOPOBJECTPROPERTY.<p>
     * 
     */
    //public static final OWLObjectProperty OBJECT_PROPERTY_OWL:TOPOBJECTPROPERTY = factory.getOWLObjectProperty(IRI.create("http://www.w3.org/2002/07/owl#topObjectProperty"));

    /* ***************************************************
     * Data Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasArgument
     */
     
    /**
     * A constant to give access to the Manchester OWL API representation of the data property HASARGUMENT.<p>
     * 
     */
    public static final OWLDataProperty DATA_PROPERTY_HASARGUMENT = factory.getOWLDataProperty(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#hasArgument"));

    /* ***************************************************
     * Data Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasEndpoint
     */
     
    /**
     * A constant to give access to the Manchester OWL API representation of the data property HASENDPOINT.<p>
     * 
     */
    public static final OWLDataProperty DATA_PROPERTY_HASENDPOINT = factory.getOWLDataProperty(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#hasEndpoint"));

    /* ***************************************************
     * Data Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasFile
     */
     
    /**
     * A constant to give access to the Manchester OWL API representation of the data property HASFILE.<p>
     * 
     */
    public static final OWLDataProperty DATA_PROPERTY_HASFILE = factory.getOWLDataProperty(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#hasFile"));

    /* ***************************************************
     * Data Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasName
     */
     
    /**
     * A constant to give access to the Manchester OWL API representation of the data property HASNAME.<p>
     * 
     */
    public static final OWLDataProperty DATA_PROPERTY_HASNAME = factory.getOWLDataProperty(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#hasName"));

    /* ***************************************************
     * Data Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasValue
     */
     
    /**
     * A constant to give access to the Manchester OWL API representation of the data property HASVALUE.<p>
     * 
     */
    public static final OWLDataProperty DATA_PROPERTY_HASVALUE = factory.getOWLDataProperty(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#hasValue"));

    /* ***************************************************
     * Data Property http://www.semanticweb.org/ontologies/cooperativeDTs#isComposedDigitalTwin
     */
     
    /**
     * A constant to give access to the Manchester OWL API representation of the data property ISCOMPOSEDDIGITALTWIN.<p>
     * 
     */
    public static final OWLDataProperty DATA_PROPERTY_ISCOMPOSEDDIGITALTWIN = factory.getOWLDataProperty(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#isComposedDigitalTwin"));

    /* ***************************************************
     * Data Property http://www.semanticweb.org/ontologies/cooperativeDTs#isSymmetric
     */
     
    /**
     * A constant to give access to the Manchester OWL API representation of the data property ISSYMMETRIC.<p>
     * 
     */
    public static final OWLDataProperty DATA_PROPERTY_ISSYMMETRIC = factory.getOWLDataProperty(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#isSymmetric"));

    /* ***************************************************
     * Data Property http://www.semanticweb.org/ontologies/cooperativeDTs#isTransitive
     */
     
    /**
     * A constant to give access to the Manchester OWL API representation of the data property ISTRANSITIVE.<p>
     * 
     */
    public static final OWLDataProperty DATA_PROPERTY_ISTRANSITIVE = factory.getOWLDataProperty(IRI.create("http://www.semanticweb.org/ontologies/cooperativeDTs#isTransitive"));

    /* ***************************************************
     * Data Property http://www.w3.org/2002/07/owl#topDataProperty
     */
     
    /**
     * A constant to give access to the Manchester OWL API representation of the data property OWL:TOPDATAPROPERTY.<p>
     * 
     */
    //public static final OWLDataProperty DATA_PROPERTY_OWL:TOPDATAPROPERTY = factory.getOWLDataProperty(IRI.create("http://www.w3.org/2002/07/owl#topDataProperty"));


}