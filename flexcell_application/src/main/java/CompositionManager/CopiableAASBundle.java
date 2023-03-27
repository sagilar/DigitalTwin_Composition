package CompositionManager;

import org.eclipse.basyx.support.bundle.AASBundle;

public class CopiableAASBundle extends AASBundle {
	public CopiableAASBundle(AASBundle aasbundle) {
		super(aasbundle.getAAS(), aasbundle.getSubmodels());
	}
}
