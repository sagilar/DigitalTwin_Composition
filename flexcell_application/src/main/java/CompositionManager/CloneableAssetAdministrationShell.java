package CompositionManager;



import java.util.Collection;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;


public class CloneableAssetAdministrationShell extends AssetAdministrationShell implements IAssetAdministrationShell, Cloneable {
	
	public Object clone() throws CloneNotSupportedException {
		CloneableAssetAdministrationShell clone = (CloneableAssetAdministrationShell) super.clone();
		return clone;
	}
	
}
