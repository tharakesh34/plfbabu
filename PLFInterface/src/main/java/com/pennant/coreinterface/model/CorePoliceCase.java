package com.pennant.coreinterface.model;
import java.io.Serializable;
import java.util.Date;

public class CorePoliceCase  implements Serializable {

	private static final long serialVersionUID = 384180539764860246L;

	public CorePoliceCase() {
		super();
	}

	private String 		custCIF; 						
	private String 		custFName;					
	private String 		custLName;					
	private String 		custMobileNumber;				
	private String 		custNationality;				
	private String 		custCRCPR;					
	private String 		custPassPort;	
	private Date 		custDOB;
	private String 		custProduct;
	private String 		policeCaseRule;
	private boolean		override;


	public String getCustCIF() {
		return custCIF;
	}
	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustFName() {
		return custFName;
	}
	public void setCustFName(String custFName) {
		this.custFName = custFName;
	}

	public String getCustLName() {
		return custLName;
	}
	public void setCustLName(String custLName) {
		this.custLName = custLName;
	}

	public String getCustMobileNumber() {
		return custMobileNumber;
	}
	public void setCustMobileNumber(String custMobileNumber) {
		this.custMobileNumber = custMobileNumber;
	}

	public String getCustNationality() {
		return custNationality;
	}
	public void setCustNationality(String custNationality) {
		this.custNationality = custNationality;
	}

	public String getCustCRCPR() {
		return custCRCPR;
	}
	public void setCustCRCPR(String custCRCPR) {
		this.custCRCPR = custCRCPR;
	}

	public String getCustPassPort() {
		return custPassPort;
	}
	public void setCustPassPort(String custPassPort) {
		this.custPassPort = custPassPort;
	}

	public Date getCustDOB() {
		return custDOB;
	}
	public void setCustDOB(Date custDOB) {
		this.custDOB = custDOB;
	}

	public String getCustProduct() {
		return custProduct;
	}
	public void setCustProduct(String custProduct) {
		this.custProduct = custProduct;
	}

	public String getPoliceCaseRule() {
		return policeCaseRule;
	}
	public void setPoliceCaseRule(String policeCaseRule) {
		this.policeCaseRule = policeCaseRule;
	}


	public boolean getOverride() {
		return override;
	}

	public boolean isOverride() {
		return override;
	}
	public void setOverride(boolean override) {
		this.override = override;
	}

}
