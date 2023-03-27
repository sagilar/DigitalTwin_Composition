package CompositionManager;

import java.util.ArrayList;
import java.util.List;

import org.protege.owl.codegeneration.inference.CodeGenerationInference;
import org.semanticweb.owlapi.model.IRI;

public class DevicePrimitive extends Operation implements Cloneable, SkillBasedDTOntology.DevicePrimitive {
	List<Object> params = new ArrayList<Object>();

	public DevicePrimitive(CodeGenerationInference inference, IRI iri) {
		super(inference, iri);
		String name = iri.getShortForm();
		this.addHasName(name);
	}
	
	public void setParams(List<Object> args) {
		this.params = args;
	}
	
	public List<Object> getParams(){
		return this.params;
	}
	
	public Object getParam(int i) {
		return this.params.get(i);
	}
	
	public void clearParams() {
		this.params.clear();
	}
	
	public void removeParam(int i) {
		this.params.remove(i);
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		Object obj = super.clone();
		
		List<Object> newParams = new ArrayList<Object>();
		DevicePrimitive newDP = (DevicePrimitive) obj;
		
		for (Object p : this.params) {
			newParams.add(p);
		}
		newDP.setParams(newParams);
		return newDP;
	}
	
}
