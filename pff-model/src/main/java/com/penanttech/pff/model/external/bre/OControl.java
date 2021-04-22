package com.penanttech.pff.model.external.bre;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OControl {


	private static final long serialVersionUID = 1L;
	@JsonProperty("ALIAS")
	private String alias;
	@JsonProperty("SIGNATURE")
	private String signature;
	@JsonProperty("DALOGLEVEL")
	private String daloglevel;
	@JsonProperty("EDITION")
	private String edition;
	@JsonProperty("OBJECTIVE")
	private String objective;
	@JsonProperty("EDITIONDATE")
	private String editiondate;
	@JsonProperty("ERRORCODE")
	private String errorcode;
	@JsonProperty("ERRORMSG")
	private String errormsg;
	@JsonProperty("APPLICATION_ID")
	private String application_id;

	@JsonCreator
	public OControl() {
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
