package iot.webclient;

import java.util.Date;

public class SensorProgrammingForm {
	private Long id; 			// identifiant unique
	private String measureType;	// Humidité, température, luminosité
	private String unit;		// Unité choisie Ex degré celcius
	private Date dateMin;		// Date min de la plage
	private Date dateMax;		// Date max de la plage
	private Float value;		// valeur

	 public SensorProgrammingForm() {
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

	public Date getDateMin() {
		return dateMin;
	}

	public void setDateMin(Date dateMin) {
		this.dateMin = dateMin;
	}

	public Date getDateMax() {
		return dateMax;
	}

	public void setDateMax(Date dateMax) {
		this.dateMax = dateMax;
	}

	public Float getValue() {
		return value;
	}

	public void setValue(Float value) {
		this.value = value;
	}
}
