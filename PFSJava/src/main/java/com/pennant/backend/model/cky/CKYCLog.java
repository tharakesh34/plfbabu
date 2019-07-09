package com.pennant.backend.model.cky;

import java.sql.Timestamp;
import java.util.Date;

public class CKYCLog {

	private long custId;
	private String custCif;
	private String fileName;
	private int rowNo;
	private String ckycNo;
	private String custsalutationcode;
	private String custfname;
	private String custmname;
	private String custlname;
	private String custFullName;
	private String custfatherName;
	private String custgendercode;
	private String custmaritalsts;
	private String custnationality;
	private String occupationtype;
	private Date custdob;
	private Timestamp addrLastMntOn;
	private Timestamp emailLastMntOn;
	private Timestamp phoneLastMntOn;
	private Timestamp docLastMntOn;

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public String getCustCif() {
		return custCif;
	}

	public void setCustCif(String custCif) {
		this.custCif = custCif;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getRowNo() {
		return rowNo;
	}

	public void setRowNo(int rowNo) {
		this.rowNo = rowNo;
	}

	public String getCkycNo() {
		return ckycNo;
	}

	public String getCustFullName() {
		return custFullName;
	}

	public void setCustFullName(String custFullName) {
		this.custFullName = custFullName;
	}

	public void setCkycNo(String ckycNo) {
		this.ckycNo = ckycNo;
	}

	public String getCustsalutationcode() {
		return custsalutationcode;
	}

	public void setCustsalutationcode(String custsalutationcode) {
		this.custsalutationcode = custsalutationcode;
	}

	public String getCustfname() {
		return custfname;
	}

	public void setCustfname(String custfname) {
		this.custfname = custfname;
	}

	public String getCustmname() {
		return custmname;
	}

	public void setCustmname(String custmname) {
		this.custmname = custmname;
	}

	public String getCustlname() {
		return custlname;
	}

	public void setCustlname(String custlname) {
		this.custlname = custlname;
	}

	public String getCustfatherName() {
		return custfatherName;
	}

	public void setCustfatherName(String custfatherName) {
		this.custfatherName = custfatherName;
	}

	public String getCustgendercode() {
		return custgendercode;
	}

	public void setCustgendercode(String custgendercode) {
		this.custgendercode = custgendercode;
	}

	public String getCustmaritalsts() {
		return custmaritalsts;
	}

	public void setCustmaritalsts(String custmaritalsts) {
		this.custmaritalsts = custmaritalsts;
	}

	public String getCustnationality() {
		return custnationality;
	}

	public void setCustnationality(String custnationality) {
		this.custnationality = custnationality;
	}

	public String getOccupationtype() {
		return occupationtype;
	}

	public void setOccupationtype(String occupationtype) {
		this.occupationtype = occupationtype;
	}

	public Date getCustdob() {
		return custdob;
	}

	public void setCustdob(Date custdob) {
		this.custdob = custdob;
	}

	public Timestamp getAddrLastMntOn() {
		return addrLastMntOn;
	}

	public void setAddrLastMntOn(Timestamp addrLastMntOn) {
		this.addrLastMntOn = addrLastMntOn;
	}

	public Timestamp getEmailLastMntOn() {
		return emailLastMntOn;
	}

	public void setEmailLastMntOn(Timestamp emailLastMntOn) {
		this.emailLastMntOn = emailLastMntOn;
	}

	public Timestamp getPhoneLastMntOn() {
		return phoneLastMntOn;
	}

	public void setPhoneLastMntOn(Timestamp phoneLastMntOn) {
		this.phoneLastMntOn = phoneLastMntOn;
	}

	public Timestamp getDocLastMntOn() {
		return docLastMntOn;
	}

	public void setDocLastMntOn(Timestamp docLastMntOn) {
		this.docLastMntOn = docLastMntOn;
	}

}
