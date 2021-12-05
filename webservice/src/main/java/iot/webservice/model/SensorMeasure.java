package iot.webservice.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Mesure effectuée par un capteur : température, humidité ....
 * @author philippe
 *
 */
@XmlRootElement(name = "SensorMeasure")
@XmlAccessorType(XmlAccessType.FIELD)
public class SensorMeasure {
	private Long id; 			// identifiant unique
	private String measureType;	// Humidité, température, luminosité
	private String unit;		// Unité choisie Ex degré celcius
	private Date date;			// date de la mesure
	private Float value;		// valeur
	private Float targetValue;		// valeur cible (calculée)
	
	
	 public SensorMeasure() {
		super();
	}

	public SensorMeasure(Long _id, String _measureType, String _unit, Date _date, Float _value) {
	    	this.id = _id;
	        this.measureType = _measureType;
	        this.unit = _unit;
	        this.date = _date;
	        this.value = _value;
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

	public Float getTargetValue() {
		return targetValue;
	}

	public void setTargetValue(Float targetValue) {
		this.targetValue = targetValue;
	}
	
}
