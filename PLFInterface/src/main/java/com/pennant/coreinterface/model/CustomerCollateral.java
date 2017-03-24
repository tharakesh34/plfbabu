package com.pennant.coreinterface.model;

import java.io.Serializable;

public class CustomerCollateral implements Serializable {
	
	private static final long serialVersionUID = -5775138323274602299L;

	public CustomerCollateral() {
		super();
	}
	
	private String custCIF;
	private String collReference;
	private String collType;
	private String collTypeDesc;
	private String collComplete;
	private String collCcy;
	private Object collExpDate;
	private Object colllastRvwDate;
	private Object collValue;
	private Object collBankVal;
	private Object collBankValMar;
	private String colllocation;
	private String colllocationDesc;

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCollReference() {
		return collReference;
	}

	public void setCollReference(String dsRspCLR) {
		this.collReference = dsRspCLR;
	}

	public String getCollType() {
		return collType;
	}

	public void setCollType(String dsRspCLP) {
		this.collType = dsRspCLP;
	}

	public String getCollTypeDesc() {
		return collTypeDesc;
	}

	public void setCollTypeDesc(String dsRspCPD) {
		this.collTypeDesc = dsRspCPD;
	}

	public String getCollComplete() {
		return collComplete;
	}

	public void setCollComplete(String dsRspCCM) {
		this.collComplete = dsRspCCM;
	}

	public String getCollCcy() {
		return collCcy;
	}

	public void setCollCcy(String dsRspCCY) {
		this.collCcy = dsRspCCY;
	}

	public Object getCollExpDate() {
		return collExpDate;
	}

	public void setCollExpDate(Object dsRspCXD) {
		this.collExpDate = dsRspCXD;
	}

	public Object getColllastRvwDate() {
		return colllastRvwDate;
	}

	public void setColllastRvwDate(Object dsRspLRD) {
		this.colllastRvwDate = dsRspLRD;
	}

	public Object getCollValue() {
		return collValue;
	}

	public void setCollValue(Object dsRspCLV) {
		this.collValue = dsRspCLV;
	}

	public Object getCollBankVal() {
		return collBankVal;
	}

	public void setCollBankVal(Object dsRspBKV) {
		this.collBankVal = dsRspBKV;
	}

	public Object getCollBankValMar() {
		return collBankValMar;
	}

	public void setCollBankValMar(Object dsRspBVM) {
		this.collBankValMar = dsRspBVM;
	}

	public String getColllocation() {
		return colllocation;
	}

	public void setColllocation(String dsRspCLO) {
		this.colllocation = dsRspCLO;
	}

	public String getColllocationDesc() {
		return colllocationDesc;
	}

	public void setColllocationDesc(String dsRspCLS) {
		this.colllocationDesc = dsRspCLS;
	}

}
