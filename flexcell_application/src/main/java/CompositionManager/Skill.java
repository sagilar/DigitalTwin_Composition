package CompositionManager;

import java.util.ArrayList;
import java.util.List;

import org.protege.owl.codegeneration.inference.CodeGenerationInference;
import org.semanticweb.owlapi.model.IRI;

public class Skill extends Operation implements Cloneable, SkillBasedDTOntology.Skill {
	List<DevicePrimitive> devicePrimitives = new ArrayList<DevicePrimitive>();

	public Skill(CodeGenerationInference inference, IRI iri) {
		super(inference, iri);
		String name = iri.getShortForm();
		this.addHasName(name);
	}
	
	public List getDevicePrimitives() {
		return this.devicePrimitives;
	}
	
	public void setDevicePrimitives(List<DevicePrimitive> dps) {
		this.devicePrimitives = dps;
	}
	
	public void addDevicePrimitive(DevicePrimitive dp) {
		DevicePrimitive newDp = null;
		try {
			newDp = (DevicePrimitive) dp.clone();
			this.devicePrimitives.add(newDp);
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error adding devprim");
		}
	}
	
	public void removeDevicePrimitive(int i) {
		this.devicePrimitives.remove(i);
	}
	
	public void removeDevicePrimitives() {
		this.devicePrimitives.clear();
	}
	
	public void executeSkill() {
		System.out.println("Executing skill");
		for (DevicePrimitive dp : this.devicePrimitives) {
			dp.executeSynchronousOperation(dp.params);
			waitTime(5000);
		}
	}
	
	private void waitTime(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		Object obj = super.clone();
		
		List<DevicePrimitive> newListDp = new ArrayList<DevicePrimitive>();
		Skill newSkill = (Skill) obj;
		
		for (DevicePrimitive dp : this.devicePrimitives) {
			newListDp.add(dp);
		}
		newSkill.setDevicePrimitives(newListDp);
		
		return newSkill;
	}

}
