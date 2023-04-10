package com.pennanttech.external.api.casavalidation.model;

public class CasaAccountValidationReq {

	private String Extsysname;

	private String Idtxn;

	private String Iduser;

	private CustAcctDetails custacctdetails;

	public class CustAcctDetails {

		private String accountNo;
		private String idcust;

		public String getAccountNo() {
			return accountNo;
		}

		public void setAccountNo(String accountNo) {
			this.accountNo = accountNo;
		}

		public String getIdcust() {
			return idcust;
		}

		public void setIdcust(String idcust) {
			this.idcust = idcust;
		}
	}

	public String getExtsysname() {
		return Extsysname;
	}

	public void setExtsysname(String extsysname) {
		Extsysname = extsysname;
	}

	public String getIdtxn() {
		return Idtxn;
	}

	public void setIdtxn(String idtxn) {
		Idtxn = idtxn;
	}

	public String getIduser() {
		return Iduser;
	}

	public void setIduser(String iduser) {
		Iduser = iduser;
	}

	public CustAcctDetails getCustacctdetails() {
		return custacctdetails;
	}

	public void setCustacctdetails(CustAcctDetails custacctdetails) {
		this.custacctdetails = custacctdetails;
	}

	@Override
	public String toString() {
		return "<faml><extsysname>" + Extsysname + "</extsysname><idtxn>" + Idtxn + "</idtxn>" + "<iduser>" + Iduser
				+ "</iduser><custacctdetails>" + "<accountNo>" + custacctdetails.getAccountNo()
				+ "</accountNo><idcust/></custacctdetails>" + "</faml>";
	}

}
