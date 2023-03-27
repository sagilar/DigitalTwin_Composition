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
 * Source Class: Operation <br>
 * @version generated on Mon Mar 27 14:26:55 CEST 2023 by au698550
 */

public interface Operation extends WrappedIndividual {

    /* ***************************************************
     * Property http://www.semanticweb.org/ontologies/cooperativeDTs#isCombinationOf
     */
     
    /**
     * Gets all property values for the isCombinationOf property.<p>
     * 
     * @returns a collection of values for the isCombinationOf property.
     */
    Collection<? extends Operation> getIsCombinationOf();

    /**
     * Checks if the class has a isCombinationOf property value.<p>
     * 
     * @return true if there is a isCombinationOf property value.
     */
    boolean hasIsCombinationOf();

    /**
     * Adds a isCombinationOf property value.<p>
     * 
     * @param newIsCombinationOf the isCombinationOf property value to be added
     */
    void addIsCombinationOf(Operation newIsCombinationOf);

    /**
     * Removes a isCombinationOf property value.<p>
     * 
     * @param oldIsCombinationOf the isCombinationOf property value to be removed.
     */
    void removeIsCombinationOf(Operation oldIsCombinationOf);


    /* ***************************************************
     * Property http://www.semanticweb.org/ontologies/cooperativeDTs#hasArgument
     */
     
    /**
     * Gets all property values for the hasArgument property.<p>
     * 
     * @returns a collection of values for the hasArgument property.
     */
    Collection<? extends Object> getHasArgument();

    /**
     * Checks if the class has a hasArgument property value.<p>
     * 
     * @return true if there is a hasArgument property value.
     */
    boolean hasHasArgument();

    /**
     * Adds a hasArgument property value.<p>
     * 
     * @param newHasArgument the hasArgument property value to be added
     */
    void addHasArgument(Object newHasArgument);

    /**
     * Removes a hasArgument property value.<p>
     * 
     * @param oldHasArgument the hasArgument property value to be removed.
     */
    void removeHasArgument(Object oldHasArgument);



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
     * Common interfaces
     */

    OWLNamedIndividual getOwlIndividual();

    OWLOntology getOwlOntology();

    void delete();

}
