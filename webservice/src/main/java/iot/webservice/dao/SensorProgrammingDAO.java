package iot.webservice.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import iot.webservice.model.SensorProgramming;

public class SensorProgrammingDAO {
	private static DBH2 dbH2 = DBH2.getInstance();

	static Calendar calendar = Calendar.getInstance();
	static Random random = new Random();

	public static List<SensorProgramming> dbRetrieveSensorProgramming(String filter) {
		List<SensorProgramming> result = new ArrayList<SensorProgramming>();
		try {
			// STEP 3: Execute a query
			String sql = "SELECT id, Type, Unit, DateMin, DateMax, Value FROM SENSOR_PROGRAMMING WHERE " + filter;
			ResultSet rs = dbH2.executeQuery(sql);
			// STEP 4: Extract data from result set
			while (rs.next()) {
				SensorProgramming sensorPgramming = new SensorProgramming();
				sensorPgramming.setId(rs.getLong("id"));
				sensorPgramming.setMeasureType(rs.getString("Type"));
				sensorPgramming.setUnit(rs.getString("Unit"));
				sensorPgramming.setDateMin(rs.getTimestamp("DateMin"));
				sensorPgramming.setDateMax(rs.getTimestamp("DateMax"));
				sensorPgramming.setValue(rs.getFloat("Value"));
				result.add(sensorPgramming);
				// Display values
				System.out.print(sensorPgramming.toString());
			}
			// STEP 5: Clean-up environment
			rs.close();
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		}
		return result;
	}

	public static List<SensorProgramming> getAllSensorProgramming() {
		return dbRetrieveSensorProgramming("1");
	}

	public static SensorProgramming getSensorProgramming(Long Id) {
		List<SensorProgramming> measures = dbRetrieveSensorProgramming("ID=" + Id.toString());
		if (measures.size() == 1) {
			return measures.get(0);
		}
		return null;
		// return mapSensorMeasures.get(measureId);
	}

	public static SensorProgramming addSensorProgramming(SensorProgramming sensorProgramming) {
		long timeMin = sensorProgramming.getDateMin().getTime();
		long timeMax = sensorProgramming.getDateMax().getTime();
		java.sql.Timestamp sqlDateMin = new java.sql.Timestamp(timeMin);
		java.sql.Timestamp sqlDateMax = new java.sql.Timestamp(timeMax);
		// Nettoyage si besoin
		dbH2.executeUpdate("DELETE SENSOR_PROGRAMMING WHERE" + " DateMin >= '" + sqlDateMin + "' AND DateMax <= '"
				+ sqlDateMax + "'");

		// Problèmes de chevauchement su rles plages qui précèdent
		dbH2.executeUpdate("UPDATE SENSOR_PROGRAMMING SET DateMax = '" + sqlDateMin + "' WHERE" + " DateMax >= '"
				+ sqlDateMin + "' AND DateMax < '" + sqlDateMax + "'");

		// Problèmes de chevauchement sur les plages qui suivent
		dbH2.executeUpdate("UPDATE SENSOR_PROGRAMMING SET DateMin = '" + sqlDateMax + "' WHERE" + " DateMin >= '"
				+ sqlDateMin + "' AND DateMin < '" + sqlDateMax + "'");

		StringBuffer sql = new StringBuffer(
				"INSERT INTO SENSOR_PROGRAMMING(Type, Unit, DateMin, DateMax, Value) VALUES ").append("('")
						.append(sensorProgramming.getMeasureType()).append("'").append(",'")
						.append(sensorProgramming.getUnit()).append("'").append(",'").append(sqlDateMin).append("'")
						.append(",'").append(sqlDateMax).append("'").append(",'").append(sensorProgramming.getValue())
						.append("')");
		long newId = dbH2.executeUpdate(sql.toString());
		System.out.println("new inserted id = " + newId);
		if (newId > 0) {
			return getSensorProgramming(newId);
		}
		return null;
	}
}
