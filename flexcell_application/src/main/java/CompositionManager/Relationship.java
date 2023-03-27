package CompositionManager;

import org.protege.owl.codegeneration.inference.CodeGenerationInference;
import org.semanticweb.owlapi.model.IRI;

public class Relationship extends SkillBasedDTOntology.impl.DefaultRelationship {

	public Relationship(CodeGenerationInference inference, IRI iri) {
		super(inference, iri);
		String name = iri.getShortForm();
		this.addHasName(name);
	}

	/***** Integration of dynamic relationships: TBD *****/
}
