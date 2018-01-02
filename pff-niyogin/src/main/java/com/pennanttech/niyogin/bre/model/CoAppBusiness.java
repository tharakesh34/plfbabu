package com.pennanttech.niyogin.bre.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "COBUSINESS")
@XmlAccessorType(XmlAccessType.FIELD)
public class CoAppBusiness {
	@XmlElement(name = "MAXWORKEXPERIENCE")
	private int maxWorkExperience;

	public int getMaxWorkExperience() {
		return maxWorkExperience;
	}

	public void setMaxWorkExperience(int maxWorkExperience) {
		this.maxWorkExperience = maxWorkExperience;
	}

}
