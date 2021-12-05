package iot.webservice.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBH2 {
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/test;FILE_LOCK=NO";
	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";

	static private Connection sqlConnection = null;
	static private Statement stmt = null;

	static private boolean logSQL = false;

	/** Constructeur privé */
	private DBH2() {
	}

	/** Instance unique pré-initialisée */
	private static DBH2 instance = new DBH2();
	static {
	}

	/** Point d'accès pour l'instance unique du singleton */
	public static DBH2 getInstance() {
		if (sqlConnection == null) {
			try {
				// STEP 1: Register JDBC driver
				Class.forName(JDBC_DRIVER);
				// STEP 2: Open a connection
				System.out.println("Connecting to database...");
				sqlConnection = DriverManager.getConnection(DB_URL, USER, PASS);
				System.out.println("Connected database successfully...");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return instance;
	}

	private void logQuery(String sql) {
		if (logSQL) {
			System.out.println("-- Requête DB H2 : --" + sql);
		}

	}

	public ResultSet executeQuery(String sql) {
		logQuery(sql);
		try {
			// STEP 3: Execute a query
			stmt = sqlConnection.createStatement();
			// String sql = "SELECT * FROM INFORMATION_SCHEMA.TABLES";
			ResultSet rs = stmt.executeQuery(sql);
			// STEP 4: Extract data from result set
			return rs;
			// STEP 5: Clean-up environment
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		}
		return null;
	}

	public long executeUpdate(String sql) {
		long newId = 0;
		logQuery(sql);
		try {
			// STEP 3: Execute a query
			stmt = sqlConnection.createStatement();
			// String sql = "SELECT * FROM INFORMATION_SCHEMA.TABLES";
			stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			// STEP 4: Extract data from result set
			// STEP 5: Clean-up environment
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs != null && rs.next()) {
				newId = rs.getLong(1);
			}
			rs.close();
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		}
		return newId;
	}

	/*
	 * public List<Map<String, Object>> DBQuery(String sql) { List<Map<String,
	 * Object>> result = new ArrayList<Map<String, Object>>(); try { // STEP 3:
	 * Execute a query stmt = sqlConnection.createStatement(); //String sql =
	 * "SELECT * FROM INFORMATION_SCHEMA.TABLES"; ResultSet rs =
	 * stmt.executeQuery(sql); // STEP 4: Extract data from result set List<String>
	 * fields = new ArrayList<String>(); for(int
	 * index=0;index<rs.getMetaData().getColumnCount();index++) { String colName =
	 * rs.getMetaData().getColumnName(1+index); fields.add(colName); }
	 * while(rs.next()) { Map<String, Object> rowContent = new HashMap<String,
	 * Object>(); for(String colName : fields) { rowContent.put(colName,
	 * rs.getObject(colName)); } result.add(rowContent); } // STEP 5: Clean-up
	 * environment rs.close(); } catch(SQLException se) { // Handle errors for JDBC
	 * se.printStackTrace(); } catch(Exception e) { // Handle errors for
	 * Class.forName e.printStackTrace(); } return result; }
	 */
}
