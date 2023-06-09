package SkillBasedDTOntology;

import SkillBasedDTOntology.impl.*;


import java.util.Collection;

import org.protege.owl.codegeneration.CodeGenerationFactory;
import org.protege.owl.codegeneration.WrappedIndividual;
import org.protege.owl.codegeneration.impl.FactoryHelper;
import org.protege.owl.codegeneration.impl.ProtegeJavaMapping;
import org.protege.owl.codegeneration.inference.CodeGenerationInference;
import org.protege.owl.codegeneration.inference.SimpleInference;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 * A class that serves as the entry point to the generated code providing access
 * to existing individuals in the ontology and the ability to create new individuals in the ontology.<p>
 * 
 * Generated by Protege (http://protege.stanford.edu).<br>
 * Source Class: SkillBasedDTOntology<br>
 * @version generated on Mon Mar 27 14:26:55 CEST 2023 by au698550
 */
public class SkillBasedDTOntology implements CodeGenerationFactory {
    private OWLOntology ontology;
    private ProtegeJavaMapping javaMapping = new ProtegeJavaMapping();
    private FactoryHelper delegate;
    private CodeGenerationInference inference;

    public SkillBasedDTOntology(OWLOntology ontology) {
	    this(ontology, new SimpleInference(ontology));
    }
    
    public SkillBasedDTOntology(OWLOntology ontology, CodeGenerationInference inference) {
        this.ontology = ontology;
        this.inference = inference;
        javaMapping.initialize(ontology, inference);
        delegate = new FactoryHelper(ontology, inference);
    }

    public OWLOntology getOwlOntology() {
        return ontology;
    }
    
    public void saveOwlOntology() throws OWLOntologyStorageException {
        ontology.getOWLOntologyManager().saveOntology(ontology);
    }
    
    public void flushOwlReasoner() {
        delegate.flushOwlReasoner();
    }
    
    public boolean canAs(WrappedIndividual resource, Class<? extends WrappedIndividual> javaInterface) {
    	return javaMapping.canAs(resource, javaInterface);
    }
    
    public  <X extends WrappedIndividual> X as(WrappedIndividual resource, Class<? extends X> javaInterface) {
    	return javaMapping.as(resource, javaInterface);
    }
    
    public Class<?> getJavaInterfaceFromOwlClass(OWLClass cls) {
        return javaMapping.getJavaInterfaceFromOwlClass(cls);
    }
    
    public OWLClass getOwlClassFromJavaInterface(Class<?> javaInterface) {
	    return javaMapping.getOwlClassFromJavaInterface(javaInterface);
    }
    
    public CodeGenerationInference getInference() {
        return inference;
    }

    /* ***************************************************
     * Class http://www.semanticweb.org/ontologies/cooperativeDTs#Attribute
     */

    {
        javaMapping.add("http://www.semanticweb.org/ontologies/cooperativeDTs#Attribute", Attribute.class, DefaultAttribute.class);
    }

    /**
     * Creates an instance of type Attribute.  Modifies the underlying ontology.
     */
    public Attribute createAttribute(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_ATTRIBUTE, DefaultAttribute.class);
    }

    /**
     * Gets an instance of type Attribute with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public Attribute getAttribute(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_ATTRIBUTE, DefaultAttribute.class);
    }

    /**
     * Gets all instances of Attribute from the ontology.
     */
    public Collection<? extends Attribute> getAllAttributeInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_ATTRIBUTE, DefaultAttribute.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/ontologies/cooperativeDTs#Behavior
     */

    {
        javaMapping.add("http://www.semanticweb.org/ontologies/cooperativeDTs#Behavior", Behavior.class, DefaultBehavior.class);
    }

    /**
     * Creates an instance of type Behavior.  Modifies the underlying ontology.
     */
    public Behavior createBehavior(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_BEHAVIOR, DefaultBehavior.class);
    }

    /**
     * Gets an instance of type Behavior with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public Behavior getBehavior(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_BEHAVIOR, DefaultBehavior.class);
    }

    /**
     * Gets all instances of Behavior from the ontology.
     */
    public Collection<? extends Behavior> getAllBehaviorInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_BEHAVIOR, DefaultBehavior.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/ontologies/cooperativeDTs#DevicePrimitive
     */

    {
        javaMapping.add("http://www.semanticweb.org/ontologies/cooperativeDTs#DevicePrimitive", DevicePrimitive.class, DefaultDevicePrimitive.class);
    }

    /**
     * Creates an instance of type DevicePrimitive.  Modifies the underlying ontology.
     */
    public DevicePrimitive createDevicePrimitive(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_DEVICEPRIMITIVE, DefaultDevicePrimitive.class);
    }

    /**
     * Gets an instance of type DevicePrimitive with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public DevicePrimitive getDevicePrimitive(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_DEVICEPRIMITIVE, DefaultDevicePrimitive.class);
    }

    /**
     * Gets all instances of DevicePrimitive from the ontology.
     */
    public Collection<? extends DevicePrimitive> getAllDevicePrimitiveInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_DEVICEPRIMITIVE, DefaultDevicePrimitive.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/ontologies/cooperativeDTs#DigitalTwin
     */

    {
        javaMapping.add("http://www.semanticweb.org/ontologies/cooperativeDTs#DigitalTwin", DigitalTwin.class, DefaultDigitalTwin.class);
    }

    /**
     * Creates an instance of type DigitalTwin.  Modifies the underlying ontology.
     */
    public DigitalTwin createDigitalTwin(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_DIGITALTWIN, DefaultDigitalTwin.class);
    }

    /**
     * Gets an instance of type DigitalTwin with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public DigitalTwin getDigitalTwin(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_DIGITALTWIN, DefaultDigitalTwin.class);
    }

    /**
     * Gets all instances of DigitalTwin from the ontology.
     */
    public Collection<? extends DigitalTwin> getAllDigitalTwinInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_DIGITALTWIN, DefaultDigitalTwin.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/ontologies/cooperativeDTs#Operation
     */

    {
        javaMapping.add("http://www.semanticweb.org/ontologies/cooperativeDTs#Operation", Operation.class, DefaultOperation.class);
    }

    /**
     * Creates an instance of type Operation.  Modifies the underlying ontology.
     */
    public Operation createOperation(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_OPERATION, DefaultOperation.class);
    }

    /**
     * Gets an instance of type Operation with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public Operation getOperation(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_OPERATION, DefaultOperation.class);
    }

    /**
     * Gets all instances of Operation from the ontology.
     */
    public Collection<? extends Operation> getAllOperationInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_OPERATION, DefaultOperation.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/ontologies/cooperativeDTs#Relationship
     */

    {
        javaMapping.add("http://www.semanticweb.org/ontologies/cooperativeDTs#Relationship", Relationship.class, DefaultRelationship.class);
    }

    /**
     * Creates an instance of type Relationship.  Modifies the underlying ontology.
     */
    public Relationship createRelationship(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_RELATIONSHIP, DefaultRelationship.class);
    }

    /**
     * Gets an instance of type Relationship with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public Relationship getRelationship(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_RELATIONSHIP, DefaultRelationship.class);
    }

    /**
     * Gets all instances of Relationship from the ontology.
     */
    public Collection<? extends Relationship> getAllRelationshipInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_RELATIONSHIP, DefaultRelationship.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/ontologies/cooperativeDTs#Skill
     */

    {
        javaMapping.add("http://www.semanticweb.org/ontologies/cooperativeDTs#Skill", Skill.class, DefaultSkill.class);
    }

    /**
     * Creates an instance of type Skill.  Modifies the underlying ontology.
     */
    public Skill createSkill(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_SKILL, DefaultSkill.class);
    }

    /**
     * Gets an instance of type Skill with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public Skill getSkill(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_SKILL, DefaultSkill.class);
    }

    /**
     * Gets all instances of Skill from the ontology.
     */
    public Collection<? extends Skill> getAllSkillInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_SKILL, DefaultSkill.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/ontologies/cooperativeDTs#Task
     */

    {
        javaMapping.add("http://www.semanticweb.org/ontologies/cooperativeDTs#Task", Task.class, DefaultTask.class);
    }

    /**
     * Creates an instance of type Task.  Modifies the underlying ontology.
     */
    public Task createTask(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_TASK, DefaultTask.class);
    }

    /**
     * Gets an instance of type Task with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public Task getTask(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_TASK, DefaultTask.class);
    }

    /**
     * Gets all instances of Task from the ontology.
     */
    public Collection<? extends Task> getAllTaskInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_TASK, DefaultTask.class);
    }


}
