package iot.webclient.controller;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import iot.webclient.SensorMeasureForm;
import iot.webclient.SensorProgrammingForm;
import iot.webservice.model.*;


@RestController 
public class MainRESTController {

	static ClientConfig clientConfig = new DefaultClientConfig();
	// Create Client based on Config
	static Client client = Client.create(clientConfig);
	
	final static String SERVICE_SMARTENV = "http://localhost:8182/webservice/rest";
	
	static Calendar calendar = Calendar.getInstance();
    public static final String TYPE_TEMPERATURE = "Temperature";
    public static final String UNIT_CELSIUS =  "degree_celsius";

	public static SensorMeasure createSensorMeasure(SensorMeasureForm aSensorMeasureForm) {
		SensorMeasure sensorMeasure = new SensorMeasure();
		sensorMeasure.setId(aSensorMeasureForm.getId());
		sensorMeasure.setMeasureType(aSensorMeasureForm.getMeasureType());
		sensorMeasure.setUnit(aSensorMeasureForm.getUnit());
		sensorMeasure.setDate(aSensorMeasureForm.getDate());
		sensorMeasure.setValue(aSensorMeasureForm.getValue());
		return sensorMeasure;
    }

	public static SensorProgramming createSensorProgramming(SensorProgrammingForm aSensorProgrammingForm) {
		SensorProgramming sensorProgramming = new SensorProgramming();
		sensorProgramming.setId(aSensorProgrammingForm.getId());
		sensorProgramming.setMeasureType(aSensorProgrammingForm.getMeasureType());
		if("".equals(sensorProgramming.getMeasureType())) {
			sensorProgramming.setMeasureType(TYPE_TEMPERATURE);
		}
		sensorProgramming.setUnit(aSensorProgrammingForm.getUnit());
		if("".equals(sensorProgramming.getUnit())) {
			sensorProgramming.setUnit(UNIT_CELSIUS);
		}

		sensorProgramming.setDateMin(aSensorProgrammingForm.getDateMin());
		sensorProgramming.setDateMin(aSensorProgrammingForm.getDateMax());
		sensorProgramming.setValue(aSensorProgrammingForm.getValue());
		if(sensorProgramming.getDateMin() == null || sensorProgramming.getDateMin().getTime()==0) {
			calendar.setTime(new Date());
			sensorProgramming.setDateMin(calendar.getTime());
			calendar.add(Calendar.HOUR, 1); 
			sensorProgramming.setDateMax(calendar.getTime());
		}
		return sensorProgramming;
    }

	public static ClientResponse invokeRemoteService(String uri, String httpMethod, Object formObject) {
		return invokeRemoteService(uri, httpMethod, formObject, javax.ws.rs.core.MediaType.APPLICATION_JSON);
	}
	public static ClientResponse invokeRemoteService(String uri, String httpMethod, Object formObject, String mediaType) {
	   WebResource webResource = client.resource(uri);	   
	    Builder builder = webResource.accept(mediaType) //
	            .header("content-type", mediaType)
	            ;
	    ClientResponse response = null;
	    switch(httpMethod) {
	    	case HttpMethod.GET:
	    		response = builder.get(ClientResponse.class);
	    		break;
	    	case HttpMethod.POST:
	    	    response = builder.post(ClientResponse.class, formObject);
	    	    break;
	    	case HttpMethod.PUT:
	    	    response = builder.put(ClientResponse.class, formObject);
	    	    break;
	    	case HttpMethod.DELETE:
	    	    response = builder.delete(ClientResponse.class, formObject);
	    	    break;
	    }
	    // Status 200 is successful.
	    if(!HttpMethod.DELETE.equals(httpMethod)) {
		    if (response!=null && response.getStatus() != 200) {
		        System.out.println("Failed with HTTP Error code: " + response.getStatus());
		       String error= response.getEntity(String.class);
		       System.out.println("Error: "+error);
		        return null;
		    }
	    }
	    return response;
	}
  
  
  
    // URL:
    // http://localhost:8080/SomeContextPath/smartenv
    // http://localhost:8080/SomeContextPath/smartenv.xml
    // http://localhost:8080/SomeContextPath/smartenv.json
    @RequestMapping(value = "/smartenv", //
            method = RequestMethod.GET, //
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    /*
    @RequestMapping(value = { "/history" }, method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    */
    @ResponseBody
    public List<SensorMeasure> getSensorMeasures() {
    	ClientResponse response = invokeRemoteService(SERVICE_SMARTENV, HttpMethod.GET, null);
 	    GenericType<List<SensorMeasure>> generic = new GenericType<List<SensorMeasure>>() {
 	        // No thing
 	    };
 	    List<SensorMeasure> list = new ArrayList<SensorMeasure>();
 	    if(response!=null) {
 	    	list = response.getEntity(generic);
 	    }
        return list;
    }
  
    // URL:
    // http://localhost:8080/SomeContextPath/smartenv/{measureId}
    // http://localhost:8080/SomeContextPath/smartenv/{measureId}.xml
    // http://localhost:8080/SomeContextPath/smartenv/{measureId}.json
    @RequestMapping(value = "/smartenv/{measureId}", //
            method = RequestMethod.GET, //
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @ResponseBody
    public SensorMeasure getSensorMeasure(@PathVariable("measureId") Long measureId) {
    	ClientResponse response = invokeRemoteService(SERVICE_SMARTENV + "/" + measureId.toString(), HttpMethod.GET, null);
    	SensorMeasure sensorMeasure = (SensorMeasure) response.getEntity(SensorMeasure.class);
        return sensorMeasure;
    }
  
    // URL:
    // http://localhost:8080/SomeContextPath/smartenv
    // http://localhost:8080/SomeContextPath/smartenv.xml
    // http://localhost:8080/SomeContextPath/smartenv.json
  
    @RequestMapping(value = "/smartenv", //
            method = RequestMethod.POST, //
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @ResponseBody
    public SensorMeasure addSensorMeasure(@RequestBody SensorMeasureForm measureForm) {
        ClientResponse response = invokeRemoteService(SERVICE_SMARTENV , HttpMethod.POST, createSensorMeasure(measureForm));
        System.out.println("(Service Side) Creating SensorMeasure with id: " + measureForm.getId());
        SensorMeasure sensorMeasure = (SensorMeasure) response.getEntity(SensorMeasure.class);
        return sensorMeasure;
    }
  
    // URL:
    // http://localhost:8080/SomeContextPath/smartenv
    // http://localhost:8080/SomeContextPath/smartenv.xml
    // http://localhost:8080/SomeContextPath/smartenv.json
    @RequestMapping(value = "/smartenv", //
            method = RequestMethod.PUT, //
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @ResponseBody
    public SensorMeasure updateSensorMeasure(@RequestBody SensorMeasureForm measureForm) {
        ClientResponse response = invokeRemoteService(SERVICE_SMARTENV , HttpMethod.PUT, createSensorMeasure(measureForm));
        System.out.println("(Service Side) Editing sensorMeasure with Id: " + measureForm.getId());
        SensorMeasure sensorMeasure = (SensorMeasure) response.getEntity(SensorMeasure.class);
        return sensorMeasure;
    }


    // URL:
    // http://localhost:8080/SomeContextPath/smartenv/{measureId}
    @RequestMapping(value = "/smartenv/{measureId}", //
            method = RequestMethod.DELETE, //
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @ResponseBody
    public void deleteSensorMeasure(@PathVariable("measureId") Long measureId) {
    	ClientResponse response2 = invokeRemoteService(SERVICE_SMARTENV + "/" + measureId.toString() , HttpMethod.DELETE, null);
		System.out.println("(Service Side) Deleting sensorMeasure with Id: " + measureId);
    }
    

    @RequestMapping(value = "/smartenv/current_temperature", //
            method = RequestMethod.GET, //
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @ResponseBody
    public SensorMeasure getCurrentTemperature() {
    	ClientResponse response = invokeRemoteService(SERVICE_SMARTENV + "/current_temperature", HttpMethod.GET, null);
    	SensorMeasure sensorMeasure = (SensorMeasure) response.getEntity(SensorMeasure.class);
        return sensorMeasure;
    }

    @RequestMapping(value = "/smartenv/programming", //
            method = RequestMethod.POST, //
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @ResponseBody
    public SensorProgramming addSensorProgramming(@RequestBody SensorProgrammingForm sensorProgrammingForm) {
		SensorProgramming sensorProgramming = createSensorProgramming(sensorProgrammingForm);
		ClientResponse response = invokeRemoteService(SERVICE_SMARTENV + "/programming", HttpMethod.POST, sensorProgramming);
		SensorProgramming sensorProgramming2 = (SensorProgramming) response.getEntity(SensorProgramming.class);
		return sensorProgramming2;
    }

    @RequestMapping(value = "/smartenv/audio_message", //
            method = RequestMethod.POST, //
            produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.APPLICATION_XML_VALUE })
    @ResponseBody
    public String sendAudioMessage(@RequestBody byte[]  content) {
        ClientResponse response =invokeRemoteService(SERVICE_SMARTENV + "/audio_message", HttpMethod.POST, content, javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM);
        System.out.println("sendAudioMessage " +  content.length);
        String returnedOrder = (String) response.getEntity(String.class);
        return returnedOrder;
    }

    @RequestMapping(value = "/smartenv/order", //
            method = RequestMethod.POST, //
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @ResponseBody
    public String submitOrder(@RequestBody String order) {
        ClientResponse response = invokeRemoteService(SERVICE_SMARTENV + "/order" , HttpMethod.POST, order);
        System.out.println("(Service Side) submitOrder " + order);
        String returnedOrder = (String) response.getEntity(String.class);
        return returnedOrder;
    }
}