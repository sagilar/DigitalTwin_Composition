package SkillBasedDTOntology;

import java.net.URI;
import java.util.Collection;
import javax.xml.datatype.XMLGregorianCalendar;

import org.protege.owl.codegeneration.WrappedIndividual;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * 
 * <p>
 * Generated by Protege (http://protege.stanford.edu). <br>
 * Source Class: DigitalTwin <br>
 * @version generated on Mon Mar 27 14:26:55 CEST 2023 by au698550
 */

public interface DigitalTwin extends WrappedIndividual {

    /* ***************************************************
     * Property http://www.semanticweb.org/ontologies/cooperativeDTs#cooperatesWith
     */
     
    /**
     * Gets all property values for the cooperatesWith property.<p>
     * 
     * @returns a collection of values for the cooperatesWith property.
     */
    Collection<? extends DigitalTwin> getCooperatesWith();

    /**
     * Checks if the class has a cooperatesWith property value.<p>
     * 
     * @return true if there is a cooperatesWith property value.
     */
    boolean hasCooperatesWith();

    /**
     * Adds a cooperatesWith property value.<p>
     * 
     * @param newCooperatesWith the cooperatesWith property value to be added
     */
    void addCooperatesWith(DigitalTwin newCooperatesWith);

    /**
     * Removes a cooperatesWith property value.<p>
     * 
     * @param oldCooperatesWith the cooperatesWith property value to be removed.
     */
    void removeCooperatesWith(DigitalTwin oldCooperatesWith);


    /* ***************************************************
     * Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasAttribute
     */
     
    /**
     * Gets all property values for the hasAttribute property.<p>
     * 
     * @returns a collection of values for the hasAttribute property.
     */
    Collection<? extends Attribute> getHasAttribute();

    /**
     * Checks if the class has a hasAttribute property value.<p>
     * 
     * @return true if there is a hasAttribute property value.
     */
    boolean hasHasAttribute();

    /**
     * Adds a hasAttribute property value.<p>
     * 
     * @param newHasAttribute the hasAttribute property value to be added
     */
    void addHasAttribute(Attribute newHasAttribute);

    /**
     * Removes a hasAttribute property value.<p>
     * 
     * @param oldHasAttribute the hasAttribute property value to be removed.
     */
    void removeHasAttribute(Attribute oldHasAttribute);


    /* ***************************************************
     * Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasBehavior
     */
     
    /**
     * Gets all property values for the hasBehavior property.<p>
     * 
     * @returns a collection of values for the hasBehavior property.
     */
    Collection<? extends Behavior> getHasBehavior();

    /**
     * Checks if the class has a hasBehavior property value.<p>
     * 
     * @return true if there is a hasBehavior property value.
     */
    boolean hasHasBehavior();

    /**
     * Adds a hasBehavior property value.<p>
     * 
     * @param newHasBehavior the hasBehavior property value to be added
     */
    void addHasBehavior(Behavior newHasBehavior);

    /**
     * Removes a hasBehavior property value.<p>
     * 
     * @param oldHasBehavior the hasBehavior property value to be removed.
     */
    void removeHasBehavior(Behavior oldHasBehavior);


    /* ***************************************************
     * Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasDevicePrimitive
     */
     
    /**
     * Gets all property values for the hasDevicePrimitive property.<p>
     * 
     * @returns a collection of values for the hasDevicePrimitive property.
     */
    Collection<? extends DevicePrimitive> getHasDevicePrimitive();

    /**
     * Checks if the class has a hasDevicePrimitive property value.<p>
     * 
     * @return true if there is a hasDevicePrimitive property value.
     */
    boolean hasHasDevicePrimitive();

    /**
     * Adds a hasDevicePrimitive property value.<p>
     * 
     * @param newHasDevicePrimitive the hasDevicePrimitive property value to be added
     */
    void addHasDevicePrimitive(DevicePrimitive newHasDevicePrimitive);

    /**
     * Removes a hasDevicePrimitive property value.<p>
     * 
     * @param oldHasDevicePrimitive the hasDevicePrimitive property value to be removed.
     */
    void removeHasDevicePrimitive(DevicePrimitive oldHasDevicePrimitive);


    /* ***************************************************
     * Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasDigitalTwinRelationship
     */
     
    /**
     * Gets all property values for the hasDigitalTwinRelationship property.<p>
     * 
     * @returns a collection of values for the hasDigitalTwinRelationship property.
     */
    Collection<? extends DigitalTwin> getHasDigitalTwinRelationship();

    /**
     * Checks if the class has a hasDigitalTwinRelationship property value.<p>
     * 
     * @return true if there is a hasDigitalTwinRelationship property value.
     */
    boolean hasHasDigitalTwinRelationship();

    /**
     * Adds a hasDigitalTwinRelationship property value.<p>
     * 
     * @param newHasDigitalTwinRelationship the hasDigitalTwinRelationship property value to be added
     */
    void addHasDigitalTwinRelationship(DigitalTwin newHasDigitalTwinRelationship);

    /**
     * Removes a hasDigitalTwinRelationship property value.<p>
     * 
     * @param oldHasDigitalTwinRelationship the hasDigitalTwinRelationship property value to be removed.
     */
    void removeHasDigitalTwinRelationship(DigitalTwin oldHasDigitalTwinRelationship);


    /* ***************************************************
     * Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasOperation
     */
     
    /**
     * Gets all property values for the hasOperation property.<p>
     * 
     * @returns a collection of values for the hasOperation property.
     */
    Collection<? extends Operation> getHasOperation();

    /**
     * Checks if the class has a hasOperation property value.<p>
     * 
     * @return true if there is a hasOperation property value.
     */
    boolean hasHasOperation();

    /**
     * Adds a hasOperation property value.<p>
     * 
     * @param newHasOperation the hasOperation property value to be added
     */
    void addHasOperation(Operation newHasOperation);

    /**
     * Removes a hasOperation property value.<p>
     * 
     * @param oldHasOperation the hasOperation property value to be removed.
     */
    void removeHasOperation(Operation oldHasOperation);


    /* ***************************************************
     * Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasRelationship
     */
     
    /**
     * Gets all property values for the hasRelationship property.<p>
     * 
     * @returns a collection of values for the hasRelationship property.
     */
    Collection<? extends Relationship> getHasRelationship();

    /**
     * Checks if the class has a hasRelationship property value.<p>
     * 
     * @return true if there is a hasRelationship property value.
     */
    boolean hasHasRelationship();

    /**
     * Adds a hasRelationship property value.<p>
     * 
     * @param newHasRelationship the hasRelationship property value to be added
     */
    void addHasRelationship(Relationship newHasRelationship);

    /**
     * Removes a hasRelationship property value.<p>
     * 
     * @param oldHasRelationship the hasRelationship property value to be removed.
     */
    void removeHasRelationship(Relationship oldHasRelationship);


    /* ***************************************************
     * Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasSkill
     */
     
    /**
     * Gets all property values for the hasSkill property.<p>
     * 
     * @returns a collection of values for the hasSkill property.
     */
    Collection<? extends Skill> getHasSkill();

    /**
     * Checks if the class has a hasSkill property value.<p>
     * 
     * @return true if there is a hasSkill property value.
     */
    boolean hasHasSkill();

    /**
     * Adds a hasSkill property value.<p>
     * 
     * @param newHasSkill the hasSkill property value to be added
     */
    void addHasSkill(Skill newHasSkill);

    /**
     * Removes a hasSkill property value.<p>
     * 
     * @param oldHasSkill the hasSkill property value to be removed.
     */
    void removeHasSkill(Skill oldHasSkill);


    /* ***************************************************
     * Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasTask
     */
     
    /**
     * Gets all property values for the hasTask property.<p>
     * 
     * @returns a collection of values for the hasTask property.
     */
    Collection<? extends Task> getHasTask();

    /**
     * Checks if the class has a hasTask property value.<p>
     * 
     * @return true if there is a hasTask property value.
     */
    boolean hasHasTask();

    /**
     * Adds a hasTask property value.<p>
     * 
     * @param newHasTask the hasTask property value to be added
     */
    void addHasTask(Task newHasTask);

    /**
     * Removes a hasTask property value.<p>
     * 
     * @param oldHasTask the hasTask property value to be removed.
     */
    void removeHasTask(Task oldHasTask);


    /* ***************************************************
     * Property http://www.semanticweb.org/ontologies/cooperativeDTs#isComposedOf
     */
     
    /**
     * Gets all property values for the isComposedOf property.<p>
     * 
     * @returns a collection of values for the isComposedOf property.
     */
    Collection<? extends DigitalTwin> getIsComposedOf();

    /**
     * Checks if the class has a isComposedOf property value.<p>
     * 
     * @return true if there is a isComposedOf property value.
     */
    boolean hasIsComposedOf();

    /**
     * Adds a isComposedOf property value.<p>
     * 
     * @param newIsComposedOf the isComposedOf property value to be added
     */
    void addIsComposedOf(DigitalTwin newIsComposedOf);

    /**
     * Removes a isComposedOf property value.<p>
     * 
     * @param oldIsComposedOf the isComposedOf property value to be removed.
     */
    void removeIsComposedOf(DigitalTwin oldIsComposedOf);


    /* ***************************************************
     * Property http://www.semanticweb.org/ontologies/cooperativeDTs#isConnectedTo
     */
     
    /**
     * Gets all property values for the isConnectedTo property.<p>
     * 
     * @returns a collection of values for the isConnectedTo property.
     */
    Collection<? extends DigitalTwin> getIsConnectedTo();

    /**
     * Checks if the class has a isConnectedTo property value.<p>
     * 
     * @return true if there is a isConnectedTo property value.
     */
    boolean hasIsConnectedTo();

    /**
     * Adds a isConnectedTo property value.<p>
     * 
     * @param newIsConnectedTo the isConnectedTo property value to be added
     */
    void addIsConnectedTo(DigitalTwin newIsConnectedTo);

    /**
     * Removes a isConnectedTo property value.<p>
     * 
     * @param oldIsConnectedTo the isConnectedTo property value to be removed.
     */
    void removeIsConnectedTo(DigitalTwin oldIsConnectedTo);


    /* ***************************************************
     * Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasEndpoint
     */
     
    /**
     * Gets all property values for the hasEndpoint property.<p>
     * 
     * @returns a collection of values for the hasEndpoint property.
     */
    Collection<? extends String> getHasEndpoint();

    /**
     * Checks if the class has a hasEndpoint property value.<p>
     * 
     * @return true if there is a hasEndpoint property value.
     */
    boolean hasHasEndpoint();

    /**
     * Adds a hasEndpoint property value.<p>
     * 
     * @param newHasEndpoint the hasEndpoint property value to be added
     */
    void addHasEndpoint(String newHasEndpoint);

    /**
     * Removes a hasEndpoint property value.<p>
     * 
     * @param oldHasEndpoint the hasEndpoint property value to be removed.
     */
    void removeHasEndpoint(String oldHasEndpoint);



    /* ***************************************************
     * Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasName
     */
     
    /**
     * Gets all property values for the hasName property.<p>
     * 
     * @returns a collection of values for the hasName property.
     */
    Collection<? extends String> getHasName();

    /**
     * Checks if the class has a hasName property value.<p>
     * 
     * @return true if there is a hasName property value.
     */
    boolean hasHasName();

    /**
     * Adds a hasName property value.<p>
     * 
     * @param newHasName the hasName property value to be added
     */
    void addHasName(String newHasName);

    /**
     * Removes a hasName property value.<p>
     * 
     * @param oldHasName the hasName property value to be removed.
     */
    void removeHasName(String oldHasName);



    /* ***************************************************
     * Property http://www.semanticweb.org/ontologies/cooperativeDTs#isComposedDigitalTwin
     */
     
    /**
     * Gets all property values for the isComposedDigitalTwin property.<p>
     * 
     * @returns a collection of values for the isComposedDigitalTwin property.
     */
    Collection<? extends Boolean> getIsComposedDigitalTwin();

    /**
     * Checks if the class has a isComposedDigitalTwin property value.<p>
     * 
     * @return true if there is a isComposedDigitalTwin property value.
     */
    boolean hasIsComposedDigitalTwin();

    /**
     * Adds a isComposedDigitalTwin property value.<p>
     * 
     * @param newIsComposedDigitalTwin the isComposedDigitalTwin property value to be added
     */
    void addIsComposedDigitalTwin(Boolean newIsComposedDigitalTwin);

    /**
     * Removes a isComposedDigitalTwin property value.<p>
     * 
     * @param oldIsComposedDigitalTwin the isComposedDigitalTwin property value to be removed.
     */
    void removeIsComposedDigitalTwin(Boolean oldIsComposedDigitalTwin);



    /* ***************************************************
     * Common interfaces
     */

    OWLNamedIndividual getOwlIndividual();

    OWLOntology getOwlOntology();

    void delete();

}