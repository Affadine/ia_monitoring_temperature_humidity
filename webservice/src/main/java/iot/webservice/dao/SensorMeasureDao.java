package iot.webservice.dao;

	 
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import iot.webservice.model.SensorMeasure;
	 

	 
public class SensorMeasureDao  {
    private static DBH2 dbH2 = DBH2.getInstance();

	static Calendar calendar = Calendar.getInstance();
	static Random random = new Random();
	// JDBC driver name and database URL 
	
    public static final String TYPE_TEMPERATURE = "Temperature";
    public static final String UNIT_CELSIUS =  "degree_celsius";
    public static DecimalFormat NUM_3 = new DecimalFormat("0.000");

   
 
 
    public static List<SensorMeasure> dbRetrieveSenorMeasures(String filter) {
		List<SensorMeasure> result = new ArrayList<SensorMeasure>();
		String sql = "SELECT SMeasure.id, SMeasure.Type, SMeasure.Unit, SMeasure.Date, SMeasure.Value, "
				+ " (SELECT IFNULL(MAX(SPgm.Value), -999)"
				+ "		FROM SENSOR_PROGRAMMING AS SPgm WHERE"
				+ "			 SPgm.DateMin <= SMeasure.Date "
				+ "				AND SPgm.DateMax > SMeasure.Date "
				+ "				AND SPgm.Type = SMeasure.Type"
				+ "		) AS TargetValue"
				+ " FROM SENSOR_MEASURE AS SMeasure"
				+ " WHERE " + filter;
		try { 
			 // STEP 3: Execute a query 
			 ResultSet rs = dbH2.executeQuery(sql); 
			 // STEP 4: Extract data from result set 
			 while(rs.next()) { 
				SensorMeasure sensorMeasure = new SensorMeasure();
				sensorMeasure.setId(rs.getLong("id")); 
				sensorMeasure.setMeasureType(rs.getString("Type")); 
				sensorMeasure.setUnit(rs.getString("Unit")); 
				sensorMeasure.setDate(rs.getTimestamp("Date"));  
				sensorMeasure.setValue(rs.getFloat("Value"));
				sensorMeasure.setTargetValue(rs.getFloat("TargetValue"));
				result.add(sensorMeasure);
				// Display values 
			    System.out.print(sensorMeasure.toString());
			 } 
			 // STEP 5: Clean-up environment 
			 rs.close(); 
		  } catch(SQLException se) { 
		     // Handle errors for JDBC 
		     se.printStackTrace(); 
		  } catch(Exception e) { 
		     // Handle errors for Class.forName 
		     e.printStackTrace(); 
		  }
		  return result;
    }

 
    
    
    
    
    private static float round3(float value) {
    	return  (float) Math.round(1000*value)/1000;
    }

    private static void initData() {
    	//  public SensorMeasure(Long _id, String _measureType, String _unit, Date _date, Float _value) {
    	//Date currentDate = new Date();
/*
    	calendar.add(Calendar.MINUTE, -60);
    	float temperature = 18.7F;
    	for(int i=0; i<10; i++) {
    		float value = temperature + 2*random.nextFloat();
    		addSensorMeasure(TYPE_TEMPERATURE,UNIT_CELSIUS, calendar.getTime(),round3(value));
    		calendar.add(Calendar.MINUTE, 1);
		}
*/
    }

    public static SensorMeasure getSensorMeasure(Long measureId) {
		List<SensorMeasure> measures = dbRetrieveSenorMeasures("ID=" + measureId.toString());
		if(measures.size()==1) {
			return measures.get(0);
		}
		return null;
		//return mapSensorMeasures.get(measureId);
    }
 
    /*
    private static SensorMeasure addSensorMeasure(String _measureType, String _unit, Date _date, Float _value) {
    	SensorMeasure sensorMeasure = new SensorMeasure(0L, _measureType,_unit, _date, _value);
    	return addSensorMeasure(sensorMeasure);
    }*/

    public static SensorMeasure addSensorMeasure(SensorMeasure sensorMeasure) {
		long time = sensorMeasure.getDate().getTime();
		java.sql.Timestamp sqlDate = new java.sql.Timestamp(time); 
		StringBuffer sql = new StringBuffer("INSERT INTO SENSOR_MEASURE(Type, Unit, Date, Value) VALUES ")
				.append("('").append(sensorMeasure.getMeasureType()).append("'")
				.append(",'").append(sensorMeasure.getUnit()).append("'")
				.append(",'").append(sqlDate).append("'")
				.append(",'").append(sensorMeasure.getValue()).append("')");
		long newId = dbH2.executeUpdate(sql.toString());
		System.out.println("new inserted id = " + newId);
		if(newId > 0) {
			return  getSensorMeasure(newId);
		}
		return null;
    }
 
    public static SensorMeasure updateSensorMeasure(SensorMeasure sensorMeasure) {
		long time = sensorMeasure.getDate().getTime();
		java.sql.Timestamp sqlDate = new java.sql.Timestamp(time); 
		StringBuffer sql = new StringBuffer("UPDATE SENSOR_MEASURE SET ")
				.append("Type='").append(sensorMeasure.getMeasureType()).append("'")
				.append(",Unit='").append(sensorMeasure.getUnit()).append("'")
				.append(",Date='").append(sqlDate).append("'")
				.append(",Value='").append(sensorMeasure.getValue()).append("'")
				.append(" WHERE SENSOR_MEASURE.ID = ").append(sensorMeasure.getId());
		long newId = dbH2.executeUpdate(sql.toString());
		if(newId > 0) {
			return  getSensorMeasure(newId);
		}
        return null;
    }
 
    public static void deleteSensorMeasure(Long measureId) {
		StringBuffer sql = new StringBuffer("DELETE FROM SENSOR_MEASURE WHERE ID = ").append(measureId);
		dbH2.executeUpdate(sql.toString());
    }
 
    public static List<SensorMeasure> getAllSensorMeasure() {
		return dbRetrieveSenorMeasures("1");
    }
     
   public static SensorMeasure getCurrentTemperature(List<Integer> motorSpeedHistory) {
	   float temperature = 22.7F+2*random.nextFloat();
	   float timeFactor = 1;
	   for(Integer motorSpeed : motorSpeedHistory) {
		   if(motorSpeed>0) {
			   float tempdelta = timeFactor * motorSpeed *  5/16 * timeFactor*random.nextFloat();
			   temperature = temperature -  tempdelta;
			   timeFactor = Math.max(0.0F,timeFactor- 0.1F );
		   }
	   }
	    SensorMeasure sensorMeasure = new SensorMeasure(0L, TYPE_TEMPERATURE,UNIT_CELSIUS, new Date(), round3(temperature));
   		return addSensorMeasure(sensorMeasure);
   }
    
    
    List<SensorMeasure> list;
	 
}
