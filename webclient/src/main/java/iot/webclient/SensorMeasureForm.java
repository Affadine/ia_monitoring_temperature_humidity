package iot.webclient;

import java.util.Date;

public class SensorMeasureForm {
	private Long id; 			// identifiant unique
	private String measureType;	// Humidité, température, luminosité
	private String unit;		// Unité choisie Ex degré celcius
	private Date date;			// date de la mesure
	private Float value;		// valeur
	
	
	
	 public SensorMeasureForm() {
		super();
	}



	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public String getMeasureType() {
		return measureType;
	}



	public void setMeasureType(String measureType) {
		this.measureType = measureType;
	}



	public String getUnit() {
		return unit;
	}



	public void setUnit(String unit) {
		this.unit = unit;
	}



	public Date getDate() {
		return date;
	}



	public void setDate(Date date) {
		this.date = date;
	}



	public Float getValue() {
		return value;
	}



	public void setValue(Float value) {
		this.value = value;
	}
	 
	 
}
