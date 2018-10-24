package com.pennant.backend.model.applicationmaster;

import javax.xml.bind.annotation.XmlElement;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class LoanPendingData   extends AbstractWorkflowEntity implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7058074481411678596L;
	
	@XmlElement
	private String finReference;
	@XmlElement
	private String custCIF;
	@XmlElement
	private String custShrtName;
	
	
	public String getFinReference() {
		return finReference;
	}
	
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	
	public String getCustCIF() {
		return custCIF;
	}
	
	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}
	
	public String getCustShrtName() {
		return custShrtName;
	}
	
	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}
	
}
