package com.pennant.backend.model.spreadsheet;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CustomerSalaried implements Serializable {

	private static final long serialVersionUID = 2835243626051472106L;

	private String custCIF;
	private BigDecimal bs = BigDecimal.ZERO;
	private BigDecimal gp = BigDecimal.ZERO;
	private BigDecimal da = BigDecimal.ZERO;
	private BigDecimal hra = BigDecimal.ZERO;
	private BigDecimal cla = BigDecimal.ZERO;
	private BigDecimal ma = BigDecimal.ZERO;
	private BigDecimal sa = BigDecimal.ZERO;
	private BigDecimal oa = BigDecimal.ZERO;
	private BigDecimal cv = BigDecimal.ZERO;
	private BigDecimal vp = BigDecimal.ZERO;
	private BigDecimal ao = BigDecimal.ZERO;
	private BigDecimal renInc = BigDecimal.ZERO;
	private BigDecimal intInc = BigDecimal.ZERO;
	//Expenses
	private BigDecimal pf = BigDecimal.ZERO;
	private BigDecimal ppf = BigDecimal.ZERO;
	private BigDecimal nps = BigDecimal.ZERO;
	private BigDecimal it = BigDecimal.ZERO;
	private BigDecimal ec = BigDecimal.ZERO;
	private BigDecimal lapf = BigDecimal.ZERO;
	private BigDecimal hlds = BigDecimal.ZERO;
	private BigDecimal plds = BigDecimal.ZERO;
	private BigDecimal alds = BigDecimal.ZERO;
	private BigDecimal odds = BigDecimal.ZERO;
	private BigDecimal olds = BigDecimal.ZERO;
	private BigDecimal ids = BigDecimal.ZERO;
	private BigDecimal od = BigDecimal.ZERO;
	private BigDecimal salAdv = BigDecimal.ZERO;
	private String finRef = "";
	//Customer Details
	private String custRelation = "";
	private String custName = "";
	private String cibilScore;
	private int riskScore;
	private String custQual = "";
	private String custDOB;
	private Date dob;
	private int workExp;
	private String custType = "";
	private String custEmpType = "";
	private int custAge;
	//Valuation amounts
	private BigDecimal docValue = BigDecimal.ZERO;
	private BigDecimal valuation1 = BigDecimal.ZERO;
	private BigDecimal valuation2 = BigDecimal.ZERO;

	public CustomerSalaried() {
		super();
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public BigDecimal getBs() {
		return bs;
	}

	public void setBs(BigDecimal bs) {
		this.bs = bs;
	}

	public BigDecimal getGp() {
		return gp;
	}

	public void setGp(BigDecimal gp) {
		this.gp = gp;
	}

	public BigDecimal getDa() {
		return da;
	}

	public void setDa(BigDecimal da) {
		this.da = da;
	}

	public BigDecimal getHra() {
		return hra;
	}

	public void setHra(BigDecimal hra) {
		this.hra = hra;
	}

	public BigDecimal getCla() {
		return cla;
	}

	public void setCla(BigDecimal cla) {
		this.cla = cla;
	}

	public BigDecimal getMa() {
		return ma;
	}

	public void setMa(BigDecimal ma) {
		this.ma = ma;
	}

	public BigDecimal getSa() {
		return sa;
	}

	public void setSa(BigDecimal sa) {
		this.sa = sa;
	}

	public BigDecimal getOa() {
		return oa;
	}

	public void setOa(BigDecimal oa) {
		this.oa = oa;
	}

	public BigDecimal getCv() {
		return cv;
	}

	public void setCv(BigDecimal cv) {
		this.cv = cv;
	}

	public BigDecimal getVp() {
		return vp;
	}

	public void setVp(BigDecimal vp) {
		this.vp = vp;
	}

	public BigDecimal getAo() {
		return ao;
	}

	public void setAo(BigDecimal ao) {
		this.ao = ao;
	}

	public BigDecimal getRenInc() {
		return renInc;
	}

	public void setRenInc(BigDecimal renInc) {
		this.renInc = renInc;
	}

	public BigDecimal getIntInc() {
		return intInc;
	}

	public void setIntInc(BigDecimal intInc) {
		this.intInc = intInc;
	}

	public BigDecimal getPf() {
		return pf;
	}

	public void setPf(BigDecimal pf) {
		this.pf = pf;
	}

	public BigDecimal getPpf() {
		return ppf;
	}

	public void setPpf(BigDecimal ppf) {
		this.ppf = ppf;
	}

	public BigDecimal getNps() {
		return nps;
	}

	public void setNps(BigDecimal nps) {
		this.nps = nps;
	}

	public BigDecimal getIt() {
		return it;
	}

	public void setIt(BigDecimal it) {
		this.it = it;
	}

	public BigDecimal getEc() {
		return ec;
	}

	public void setEc(BigDecimal ec) {
		this.ec = ec;
	}

	public BigDecimal getLapf() {
		return lapf;
	}

	public void setLapf(BigDecimal lapf) {
		this.lapf = lapf;
	}

	public BigDecimal getHlds() {
		return hlds;
	}

	public void setHlds(BigDecimal hlds) {
		this.hlds = hlds;
	}

	public BigDecimal getPlds() {
		return plds;
	}

	public void setPlds(BigDecimal plds) {
		this.plds = plds;
	}

	public BigDecimal getAlds() {
		return alds;
	}

	public void setAlds(BigDecimal alds) {
		this.alds = alds;
	}

	public BigDecimal getOdds() {
		return odds;
	}

	public void setOdds(BigDecimal odds) {
		this.odds = odds;
	}

	public BigDecimal getOlds() {
		return olds;
	}

	public void setOlds(BigDecimal olds) {
		this.olds = olds;
	}

	public BigDecimal getIds() {
		return ids;
	}

	public void setIds(BigDecimal ids) {
		this.ids = ids;
	}

	public BigDecimal getOd() {
		return od;
	}

	public void setOd(BigDecimal od) {
		this.od = od;
	}

	public BigDecimal getSalAdv() {
		return salAdv;
	}

	public void setSalAdv(BigDecimal salAdv) {
		this.salAdv = salAdv;
	}

	public String getFinRef() {
		return finRef;
	}

	public void setFinRef(String finRef) {
		this.finRef = finRef;
	}

	public String getCustRelation() {
		return custRelation;
	}

	public void setCustRelation(String custRelation) {
		this.custRelation = custRelation;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCibilScore() {
		return cibilScore;
	}

	public void setCibilScore(String cibilScore) {
		this.cibilScore = cibilScore;
	}

	public int getRiskScore() {
		return riskScore;
	}

	public void setRiskScore(int riskScore) {
		this.riskScore = riskScore;
	}

	public String getCustDOB() {
		return custDOB;
	}

	public void setCustDOB(String custDOB) {
		this.custDOB = custDOB;
	}

	public String getCustQual() {
		return custQual;
	}

	public void setCustQual(String custQual) {
		this.custQual = custQual;
	}

	public int getWorkExp() {
		return workExp;
	}

	public void setWorkExp(int workExp) {
		this.workExp = workExp;
	}

	public String getCustType() {
		return custType;
	}

	public void setCustType(String custType) {
		this.custType = custType;
	}

	public String getCustEmpType() {
		return custEmpType;
	}

	public void setCustEmpType(String custEmpType) {
		this.custEmpType = custEmpType;
	}

	public int getCustAge() {
		return custAge;
	}

	public void setCustAge(int custAge) {
		this.custAge = custAge;
	}

	public BigDecimal getDocValue() {
		return docValue;
	}

	public void setDocValue(BigDecimal docValue) {
		this.docValue = docValue;
	}

	public BigDecimal getValuation1() {
		return valuation1;
	}

	public void setValuation1(BigDecimal valuation1) {
		this.valuation1 = valuation1;
	}

	public BigDecimal getValuation2() {
		return valuation2;
	}

	public void setValuation2(BigDecimal valuation2) {
		this.valuation2 = valuation2;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

}
