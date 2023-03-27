package CompositionManager;

import org.protege.owl.codegeneration.inference.CodeGenerationInference;
import org.semanticweb.owlapi.model.IRI;

public class Behavior extends SkillBasedDTOntology.impl.DefaultBehavior{

	public Behavior(CodeGenerationInference inference, IRI iri) {
		super(inference, iri);
		String name = iri.getShortForm();
		this.addHasName(name);
	}
	
	/***** Integrated behaviors: TBD *****/

}
