package SkillBasedDTOntology.impl;

import SkillBasedDTOntology.*;


import java.net.URI;
import java.util.Collection;
import javax.xml.datatype.XMLGregorianCalendar;

import org.protege.owl.codegeneration.WrappedIndividual;
import org.protege.owl.codegeneration.impl.WrappedIndividualImpl;

import org.protege.owl.codegeneration.inference.CodeGenerationInference;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Generated by Protege (http://protege.stanford.edu).<br>
 * Source Class: DefaultTask <br>
 * @version generated on Mon Mar 27 14:26:55 CEST 2023 by au698550
 */
public class DefaultTask extends WrappedIndividualImpl implements Task {

    public DefaultTask(CodeGenerationInference inference, IRI iri) {
        super(inference, iri);
    }





    /* ***************************************************
     * Object Property http://www.semanticweb.org/ontologies/cooperativeDTs#isCombinationOf
     */
     
    public Collection<? extends Operation> getIsCombinationOf() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_ISCOMBINATIONOF,
                                               DefaultOperation.class);
    }

    public boolean hasIsCombinationOf() {
	   return !getIsCombinationOf().isEmpty();
    }

    public void addIsCombinationOf(Operation newIsCombinationOf) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_ISCOMBINATIONOF,
                                       newIsCombinationOf);
    }

    public void removeIsCombinationOf(Operation oldIsCombinationOf) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_ISCOMBINATIONOF,
                                          oldIsCombinationOf);
    }


    /* ***************************************************
     * Data Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasArgument
     */
     
    public Collection<? extends Object> getHasArgument() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASARGUMENT, Object.class);
    }

    public boolean hasHasArgument() {
		return !getHasArgument().isEmpty();
    }

    public void addHasArgument(Object newHasArgument) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASARGUMENT, newHasArgument);
    }

    public void removeHasArgument(Object oldHasArgument) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASARGUMENT, oldHasArgument);
    }


    /* ***************************************************
     * Data Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasEndpoint
     */
     
    public Collection<? extends String> getHasEndpoint() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASENDPOINT, String.class);
    }

    public boolean hasHasEndpoint() {
		return !getHasEndpoint().isEmpty();
    }

    public void addHasEndpoint(String newHasEndpoint) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASENDPOINT, newHasEndpoint);
    }

    public void removeHasEndpoint(String oldHasEndpoint) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASENDPOINT, oldHasEndpoint);
    }


    /* ***************************************************
     * Data Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasName
     */
     
    public Collection<? extends String> getHasName() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASNAME, String.class);
    }

    public boolean hasHasName() {
		return !getHasName().isEmpty();
    }

    public void addHasName(String newHasName) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASNAME, newHasName);
    }

    public void removeHasName(String oldHasName) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASNAME, oldHasName);
    }


}
