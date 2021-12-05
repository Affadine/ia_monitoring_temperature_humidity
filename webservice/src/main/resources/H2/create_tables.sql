DROP TABLE IF EXISTS SENSOR_MEASURE
;
CREATE TABLE SENSOR_MEASURE(
	 ID 	INT auto_increment PRIMARY KEY
	,Type	ENUM('Temperature', 'Humidity')
	,Unit	ENUM('degree_celsius', 'degree_fahrenheit', 'g.m-3')
	,Date	TIMESTAMP
	,Value	DECIMAL(10, 3)
	)
;
INSERT INTO SENSOR_MEASURE(Type,Unit,Date,Value) VALUES
	 ('Temperature', ' degree_celsius', NOW(), 19.201)
	,('Temperature', ' degree_celsius', NOW(), 18.993)
;
	
DROP TABLE IF EXISTS SENSOR_PROGRAMMING
;
CREATE TABLE SENSOR_PROGRAMMING(
	 ID 		INT auto_increment PRIMARY KEY
	,Type		ENUM('Temperature', 'Humidity')
	,Unit		ENUM('degree_celsius', 'degree_fahrenheit', 'g.m-3')
	,DateMin	TIMESTAMP
	,DateMax	TIMESTAMP
	,Value	DECIMAL(10, 3)
	)
;
INSERT INTO SENSOR_PROGRAMMING(Type,Unit,DateMin, DateMax,Value) VALUES
	 ('Temperature', ' degree_celsius', NOW(),TIMESTAMPADD(HOUR, 1, NOW()),19.201)
;

