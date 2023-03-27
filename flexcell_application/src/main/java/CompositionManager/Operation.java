package CompositionManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IEmbeddedDataSpecification;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IConstraint;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IAsyncInvocation;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperationVariable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.protege.owl.codegeneration.WrappedIndividual;
import org.protege.owl.codegeneration.inference.CodeGenerationInference;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

import dtflexcell.EventHandler;

public class Operation extends SkillBasedDTOntology.impl.DefaultOperation implements SkillBasedDTOntology.Operation {
	private IOperation iOperation = null;
	
	
	public Operation(CodeGenerationInference inference, IRI iri) {
		super(inference, iri); 
		String name = iri.getShortForm();
		this.addHasName(name);
	}
	
	public Operation(IOperation iOperation, CodeGenerationInference inference, IRI iri) {
		super(inference, iri);
		this.iOperation = iOperation;
		String name = iri.getShortForm();
		this.addHasName(name);
	}
	
	public void executeAsynchronousOperation(List<?> arguments) {
		String command = String.valueOf(arguments.get(0));//"kuka calculate_forward_kinematics";
		String topic = String.valueOf(arguments.get(1)); // EventHandler.ACTUALKUKATOPIC
		if (arguments.size() >= 3) {
			List<String> argsList = (List<String>) arguments.get(2);
			if(argsList.size() == 6) {
				String argsStr = argsList.get(0) + "," + argsList.get(1) + "," + argsList.get(2) + "," + argsList.get(3)
				+ "," + argsList.get(4) + "," + argsList.get(5);
			}else {
				String argsStr = argsList.get(0) + "," + argsList.get(1) + "," + argsList.get(2) + "," + argsList.get(3)
				+ "," + argsList.get(4) + "," + argsList.get(5) + "," + argsList.get(6);
			}
			
		}
		
		MqttMessage mqttMessage = new MqttMessage(command.getBytes());
		try {
			EventHandler.mqttClient.publish(topic, mqttMessage);
		} catch (MqttPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Object executeSynchronousOperation(List<?> arguments) {
		String url = "http://127.0.0.1:5000/" + arguments.get(0) + "?";
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		
		List<NameValuePair> urlParameters = new ArrayList<>();
		
		URIBuilder builder = new URIBuilder();
		builder.setScheme("http").setHost("127.0.0.1").setPort(5000).setPath("/" + arguments.get(0));
		int index = 0;
		for (Object o : arguments) {
			// HTTP Request
			
			if (index == 0) {
				index++;
				continue;
			}
			builder.setParameter("param" + (index), String.valueOf(o));
			index++;
		}
		
		try {
			URI uri = builder.build();
			httpGet = new HttpGet(uri);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			CloseableHttpResponse response = httpclient.execute(httpGet);
			
			HttpEntity entity = response.getEntity();
            if (entity != null) {
                // return it as a String
                String result = EntityUtils.toString(entity);
                return result;
            }
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return null;
	}

}
