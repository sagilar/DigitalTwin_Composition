package CompositionManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.protege.owl.codegeneration.inference.CodeGenerationInference;
import org.semanticweb.owlapi.model.IRI;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;

public class Task extends Operation implements Cloneable, SkillBasedDTOntology.Task {
	List<List> shape = new ArrayList<List>();
	List<Skill> skills = new ArrayList<Skill>();
    

	public Task(CodeGenerationInference inference, IRI iri) {
		super(inference, iri);
		String name = iri.getShortForm();
		this.addHasName(name);
	}

	public List getSkills() {
		return this.skills;
	}
	
	public void addSkill(Skill sk) {
		Skill newSk = null;
		try {
			newSk = (Skill) sk.clone();
			this.skills.add(newSk);
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error adding skill");
		}
	}
	
	public void removeSkill(int i) {
		this.skills.remove(i);
	}
	
	public void removeSkills() {
		this.skills.clear();
	}
	
	public void executeTask() {
		System.out.println("Executing task");
		for (Skill skill : this.skills) {
			DevicePrimitive dp1 = (DevicePrimitive) skill.getDevicePrimitives().get(0);
			skill.executeSkill();
		}
	}
	
	public void setShape(List<List> shape) {
		this.shape = shape;
	}
	
	public List<List> getShape(){
		return this.shape;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
