package com.pennant.interfaces.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Drawees")
public class Drawees {
	
	private List<Drawee> drawee;

	@XmlElement(name="Drawee")
	public List<Drawee> getDrawee() {
		return drawee;
	}

	public void setDrawee(List<Drawee> drawee) {
		this.drawee = drawee;
	}
	
}
