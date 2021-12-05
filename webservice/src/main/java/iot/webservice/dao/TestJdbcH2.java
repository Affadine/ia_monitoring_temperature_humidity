package iot.webservice.dao;

import java.util.Date;

import iot.webservice.model.SensorMeasure;  

public class TestJdbcH2 { 
   
   public static void main(String[] args) {	   
      SensorMeasureDao.dbRetrieveSenorMeasures("1");
      SensorMeasure aSensorMeasure = new SensorMeasure(-1L, SensorMeasureDao.TYPE_TEMPERATURE  ,SensorMeasureDao.UNIT_CELSIUS, new Date(),18.701F);
      SensorMeasureDao.addSensorMeasure(aSensorMeasure);
   } 
}