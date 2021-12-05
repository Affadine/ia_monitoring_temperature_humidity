package iot.webservice.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.icu.impl.locale.StringTokenIterator;
import org.python.util.PythonInterpreter;

import com.sun.jersey.api.client.ClientResponse;

import iot.webservice.dao.SensorMeasureDao;
import iot.webservice.dao.SensorProgrammingDAO;
import iot.webservice.model.SensorMeasure;
import iot.webservice.model.SensorProgramming;




@Path("")
public class SmartEnvService {
	//final static List<String> ORDERS = new ArrayList<String>(Arrays.asList("up", "down", "on", "off", "right", "left", "stop"));
	final static Map<String, Integer> MAP_ORDERS = new HashMap<String,Integer>() {{
		put("on", 1);
	    put("off", 2);
	    put("up", 3);
	    put("down", 4);	    
	    put("right", 5);
	    put("left", 6);
	}};

	static int motorSpeed = 0;
	static List<Integer> motorSpeedHistory = new ArrayList<>();
	static String currentOrder ="";
	static SerialTest seriatTest = new SerialTest();
	// URI:
	// /contextPath/servletPath/smartenv
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public List<SensorMeasure> getSensorMeasures_JSON() {
		List<SensorMeasure> listMeasures = SensorMeasureDao.getAllSensorMeasure();
		return listMeasures;
	}

	// URI:
	// /contextPath/servletPath/smartenv/{measureId}
	@GET
	@Path("/{measureId}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public SensorMeasure getSensorMeasure(@PathParam("measureId") Long measureId) {
		return SensorMeasureDao.getSensorMeasure((measureId));
	}

	// URI:
	// /contextPath/servletPath/smartenv
	@POST
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public SensorMeasure addSensorMeasure(SensorMeasure measure) {
		return SensorMeasureDao.addSensorMeasure(measure);
	}

	// URI:
	// /contextPath/servletPath/smartenv
	@PUT
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public SensorMeasure updateSensorMeasure(SensorMeasure emp) {
		return SensorMeasureDao.updateSensorMeasure(emp);
	}

	@DELETE
	@Path("/{measureId}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public void deleteSensorMeasure(@PathParam("measureId") Long empId) {
		SensorMeasureDao.deleteSensorMeasure(empId);
	}

	// URI:
	// /contextPath/servletPath/smartenv/current_temperature
	@GET
	@Path("/current_temperature")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public SensorMeasure getCurrentTemperature() {
		return SensorMeasureDao.getCurrentTemperature(motorSpeedHistory);
	}

	@GET
	@Path("/programming")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

	public List<SensorProgramming> getAllSensorProgramming() {
		return SensorProgrammingDAO.getAllSensorProgramming();
	}

	@POST
	@Path("/programming")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public SensorProgramming addSensorProgramming(SensorProgramming sensorProgramming) {
		return SensorProgrammingDAO.addSensorProgramming(sensorProgramming);
	}
	
    @POST
    @Path("/order")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public String submitOrder(String order) {
    	this.currentOrder = order;
    	executeSubmitOrder();
        return currentOrder;
    }
    
    public void executeSubmitOrder() {
         System.out.print("currentOrder = " + currentOrder);
         if(MAP_ORDERS.containsKey(currentOrder)) {
 			int orderID = MAP_ORDERS.get(currentOrder);
 			seriatTest.send2arduino(orderID);
 			

 			// MAJ motorSpeed
 			if("on".equalsIgnoreCase(currentOrder)) {
 				motorSpeed = 4;
 			} else if ("off".equalsIgnoreCase(currentOrder)) {
 				motorSpeed = 0;
 			} else if ("up".equalsIgnoreCase(currentOrder)) {
 				motorSpeed = Math.min(16, motorSpeed+1);
 			} else if ("down".equalsIgnoreCase(currentOrder)) {
 				motorSpeed = Math.max(0, motorSpeed-1);
 			}
 			motorSpeedHistory.add(0,motorSpeed);
 			while(motorSpeedHistory.size()>10) {
 				int idx = motorSpeedHistory.size() -1;
 				motorSpeedHistory.remove(idx);
 			}
 		}
    }

	@POST
	@Path("/audio_message")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_XML })
	public String sendAudioMessage(byte[] content) {
		System.out.println("SmartEnvService.sendAudioMessage " + content.length);
		String fileSep = System.getProperty("file.separator");
		//String dir = ".." + fileSep +  "sampling" + fileSep;
		String dir =  "audio_message" + fileSep;
		long number = (new Date()).getTime();
		// up, down, on, off, right, left,stop
        System.out.print("currentOrder = " + currentOrder);
		String fileName = "audio_" + number + ".wav";
		if(currentOrder.length()>0) {
			fileName = currentOrder + "_" + number + ".wav";
		}
		try {
			FileOutputStream fos = new FileOutputStream(dir + fileName);
			fos.write(content);
			// fos.close // no need, try-with-resources auto close
		} catch (Exception e) {
			e.printStackTrace();
		}
		String dir2 = ".." + fileSep + "Reconnaissance_Vocale" + fileSep + "";
		try {
			FileOutputStream fos = new FileOutputStream(dir2 + fileName);
			fos.write(content);
			// fos.close // no need, try-with-resources auto close
		} catch (Exception e) {
			e.printStackTrace();
		}

		// TODO : appeller tensor-flow
		String order = callTensorFlow(fileName, dir2);
		return order;
	}



	public String callTensorFlow(String fileName, String fileDir) {
		// Préparer le script
		String fileSep = System.getProperty("file.separator");
		try  {
			File fout = new File("cmd.bat");
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			bw.write("cd  ..\\Reconnaissance_Vocale\\prediction");
			bw.newLine();
			bw.write("py prediction.py " + fileName);
			bw.close();
			// fos.close // no need, try-with-resources auto close
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			 Process p = Runtime.getRuntime().exec("cmd /c cmd.bat >> foo.log");
			 System.out.println("Waiting for batch file ...");
			 p.waitFor();
		     System.out.println("Batch file done.");
			//Runtime.getRuntime().exec("cmd /c start \"\" build.bat");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Récupérer la sortie du fichier produit
		String output="";
		String score = "";
		File file=new File(fileDir + fileSep + fileName + ".out");
		//creates a buffer reader input stream
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
		    String line;
		    if ((line = br.readLine()) != null) {
		    	StringTokenIterator stOuput = new StringTokenIterator(line, ":");
		    	output = stOuput.first();
		    	score = stOuput.next();
		    }
		} catch(Exception e ) {
			e.printStackTrace();
		}
		float fScore=0;
		try {
			fScore = (new Float(score)).floatValue();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(fScore>=35) {
			if(MAP_ORDERS.containsKey(output)) {
				currentOrder = output;
				executeSubmitOrder();
				return output;
			}
		} else {
			seriatTest.send2arduino(9);
		}
		return "?"+output;
		/*
		PythonInterpreter interpreter = new PythonInterpreter();
		Properties props = new Properties();
		props.put("python.path",  dir1);
		interpreter.initialize(props, props, null);
		interpreter.exec("import sys");
		interpreter.exec("import tensorflow as tf");
		interpreter.exec("import prediction.py");
		interpreter.exec("sys.path.append('" + "../Reconnaissance_Vocale/prediction" + "')");
		//interpreter.exec("import ../Reconnaissance_Vocale/prediction/prediction.py");
		// execute a function that takes a string and returns a string
		//PyObject someFunc = interpreter.get("prediction");
		PyObject str = interpreter.eval("prediction(" +  fileName+ ")");
		//PyObject result = someFunc.__call__(new PyString(fileName));
		//String realResult = (String) result.__tojava__(String.class);
		return str.toString();
		*/
	}
}
