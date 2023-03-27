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
 * Source Class: DefaultBehavior <br>
 * @version generated on Mon Mar 27 14:26:55 CEST 2023 by au698550
 */
public class DefaultBehavior extends WrappedIndividualImpl implements Behavior {

    public DefaultBehavior(CodeGenerationInference inference, IRI iri) {
        super(inference, iri);
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
     * Data Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasFile
     */
     
    public Collection<? extends String> getHasFile() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASFILE, String.class);
    }

    public boolean hasHasFile() {
		return !getHasFile().isEmpty();
    }

    public void addHasFile(String newHasFile) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASFILE, newHasFile);
    }

    public void removeHasFile(String oldHasFile) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASFILE, oldHasFile);
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