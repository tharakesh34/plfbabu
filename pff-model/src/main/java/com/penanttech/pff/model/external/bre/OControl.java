package com.penanttech.pff.model.external.bre;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OControl {
	@XmlElement(name = "ALIAS")
	private String alias;
	@XmlElement(name = "SIGNATURE")
	private String signature;
	@XmlElement(name = "DALOGLEVEL")
	private String daloglevel;
	@XmlElement(name = "EDITION")
	private String edition;
	@XmlElement(name = "OBJECTIVE")
	private String objective;
	@XmlElement(name = "EDITIONDATE")
	private String editiondate;
	@XmlElement(name = "ERRORCODE")
	private String errorcode;
	@XmlElement(name = "ERRORMSG")
	private String errormsg;
	@XmlElement(name = "APPLICATION_ID")
	private String application_id;

	@JsonCreator
	public OControl() {
	    super();
	}

	// Getter Methods
	public String getAlias() {
		return alias;
	}

	public String getSignature() {
		return signature;
	}

	public String getDaloglevel() {
		return daloglevel;
	}

	public String getEdition() {
		return edition;
	}

	public String getObjective() {
		return objective;
	}

	public String getEditiondate() {
		return editiondate;
	}

	public String getErrorcode() {
		return errorcode;
	}

	public String getErrormsg() {
		return errormsg;
	}

	public String getApplication_id() {
		return application_id;
	}

	// Setter Methods
	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public void setDaloglevel(String daloglevel) {
		this.daloglevel = daloglevel;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}

	public void setEditiondate(String editiondate) {
		this.editiondate = editiondate;
	}

	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}

	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

	public void setApplication_id(String application_id) {
		this.application_id = application_id;
	}

}
