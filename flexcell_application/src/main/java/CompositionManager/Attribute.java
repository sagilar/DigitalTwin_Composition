package CompositionManager;

import java.util.Collection;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.protege.owl.codegeneration.WrappedIndividual;
import org.protege.owl.codegeneration.inference.CodeGenerationInference;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

public class Attribute extends SkillBasedDTOntology.impl.DefaultAttribute {
	private IProperty iProperty = null;

	public Attribute(CodeGenerationInference inference, IRI iri) {
		super(inference, iri);
		String name = iri.getShortForm();
		this.addHasName(name);
	}
	
	public Attribute(IProperty iProperty, CodeGenerationInference inference, IRI iri) {
		super(inference, iri);
		this.iProperty = iProperty;
		String name = iri.getShortForm();
		this.addHasName(name);
	}
	
	public void setAttributeValue(Object value) {
		this.iProperty.setValue(value);
	}
	
	public Object getAttributeValue() {
		return this.iProperty.getValue();
	}



}
