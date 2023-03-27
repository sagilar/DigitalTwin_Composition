package CompositionManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.protege.owl.codegeneration.inference.CodeGenerationInference;
import org.semanticweb.owlapi.model.IRI;

import SkillBasedDTOntology.DigitalTwin;
import SkillBasedDTOntology.impl.DefaultDigitalTwin;

public class SBDigitalTwin extends DefaultDigitalTwin implements DigitalTwin {
	private IAssetAdministrationShell aas;
	private ISubmodel technicalDataSubmodel;
	private ISubmodel operationalDataSubmodel;
	
	public SBDigitalTwin(IAssetAdministrationShell aas, CodeGenerationInference inference, IRI iri) {
		super(inference, iri);
		this.aas = aas;
		String name = iri.getShortForm();
		this.addHasName(name);
	}
	
	public SBDigitalTwin(CodeGenerationInference inference, IRI iri) {
		// TODO Auto-generated constructor stub
		super(inference, iri);
		String name = iri.getShortForm();
		this.addHasName(name);
	}
	
	public IAssetAdministrationShell getAAS() {
		return this.aas;
	}
	
	
	public void setSubmodels(ISubmodel technicalDataSubmodel,ISubmodel operationalDataSubmodel) {
		this.technicalDataSubmodel = technicalDataSubmodel;
		this.operationalDataSubmodel = operationalDataSubmodel;
	}
	
	public Set<ISubmodel> getSubmodels() {
		Set<ISubmodel> set = new HashSet<ISubmodel>();
		set.add(this.technicalDataSubmodel);
		set.add(this.operationalDataSubmodel);
		return set;
	}
	
	public ISubmodel getTechnicalDataSubmodel() {
		return this.technicalDataSubmodel;
	}
	
	public ISubmodel getOperationalDataSubmodel() {
		return this.operationalDataSubmodel;
	}
	

	public Map<String, IOperation> getOperations(){
		ISubmodelElement seOperations = this.getOperationalDataSubmodel().getSubmodelElement("Operations");
		Collection<ISubmodelElement> seOperationsCollection = (Collection<ISubmodelElement>) seOperations.getValue();
		Map<String, IOperation> operationsMap = new HashMap<String, IOperation>();
		for (ISubmodelElement op : seOperationsCollection) {
			operationsMap.put(op.getIdShort(), (IOperation) op);
		}
		return operationsMap;
	}
	

	public Map<String, IProperty> getOperationalProperties(){
		ISubmodelElement seVariables = this.getOperationalDataSubmodel().getSubmodelElement("Variables");
		Collection<ISubmodelElement> seVariablesCollection = (Collection<ISubmodelElement>) seVariables.getValue();
		Map<String, IProperty> variablesMap = new HashMap<String, IProperty>();
		for (ISubmodelElement op : seVariablesCollection) {
			variablesMap.put(op.getIdShort(), (IProperty) op);
		}
		return variablesMap;
	}
}
