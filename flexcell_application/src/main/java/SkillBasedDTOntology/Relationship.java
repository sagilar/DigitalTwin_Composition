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
 * Source Class: Relationship <br>
 * @version generated on Mon Mar 27 14:26:55 CEST 2023 by au698550
 */

public interface Relationship extends WrappedIndividual {

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
     * Property http://www.semanticweb.org/ontologies/cooperativeDTs#isSymmetric
     */
     
    /**
     * Gets all property values for the isSymmetric property.<p>
     * 
     * @returns a collection of values for the isSymmetric property.
     */
    Collection<? extends Boolean> getIsSymmetric();

    /**
     * Checks if the class has a isSymmetric property value.<p>
     * 
     * @return true if there is a isSymmetric property value.
     */
    boolean hasIsSymmetric();

    /**
     * Adds a isSymmetric property value.<p>
     * 
     * @param newIsSymmetric the isSymmetric property value to be added
     */
    void addIsSymmetric(Boolean newIsSymmetric);

    /**
     * Removes a isSymmetric property value.<p>
     * 
     * @param oldIsSymmetric the isSymmetric property value to be removed.
     */
    void removeIsSymmetric(Boolean oldIsSymmetric);



    /* ***************************************************
     * Property http://www.semanticweb.org/ontologies/cooperativeDTs#isTransitive
     */
     
    /**
     * Gets all property values for the isTransitive property.<p>
     * 
     * @returns a collection of values for the isTransitive property.
     */
    Collection<? extends Boolean> getIsTransitive();

    /**
     * Checks if the class has a isTransitive property value.<p>
     * 
     * @return true if there is a isTransitive property value.
     */
    boolean hasIsTransitive();

    /**
     * Adds a isTransitive property value.<p>
     * 
     * @param newIsTransitive the isTransitive property value to be added
     */
    void addIsTransitive(Boolean newIsTransitive);

    /**
     * Removes a isTransitive property value.<p>
     * 
     * @param oldIsTransitive the isTransitive property value to be removed.
     */
    void removeIsTransitive(Boolean oldIsTransitive);



    /* ***************************************************
     * Common interfaces
     */

    OWLNamedIndividual getOwlIndividual();

    OWLOntology getOwlOntology();

    void delete();

}
