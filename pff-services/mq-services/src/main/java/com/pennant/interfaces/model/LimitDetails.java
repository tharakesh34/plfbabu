package com.pennant.interfaces.model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "LimitDetails")
public class LimitDetails {

	private List<Categories> categories;
	private List<ProductCodes> productCodes;
	private String securityMstId;
	private String portfolioRef;
	private String limitRef;
	private int levelValue;
	private String parentLimitRef;
	private String rev_Nrev;
	private String limitDesc;
	private List<Drawees> drawees;
	private List<Brokers> brokers;
	
	private String customerReference;
	private String branchCode;
	
	// S&D Fee details
	private BigDecimal studyFee = BigDecimal.ZERO;
	private String studyFeeCcy;
	private Date studyFeeStartDate;
	private Date studyFeeExpiryDate;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++ getter / setter +++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
	
	@XmlElement(name="Categories")
	public List<Categories> getCategories() {
		return categories;
	}
	public void setCategories(List<Categories> categories) {
		this.categories = categories;
	}

	@XmlElement(name="ProductCodes")
	public List<ProductCodes> getProductCodes() {
		return productCodes;
	}
	public void setProductCodes(List<ProductCodes> productCodes) {
		this.productCodes = productCodes;
	}

	@XmlElement(name="SecurityMstId")
	public String getSecurityMstId() {
		return securityMstId;
	}
	public void setSecurityMstId(String securityMstId) {
		this.securityMstId = securityMstId;
	}

	@XmlElement(name="PortfolioRef")
	public String getPortfolioRef() {
		return portfolioRef;
	}
	public void setPortfolioRef(String portfolioRef) {
		this.portfolioRef = portfolioRef;
	}

	@XmlElement(name="LimitRef")
	public String getLimitRef() {
		return limitRef;
	}
	public void setLimitRef(String limitRef) {
		this.limitRef = limitRef;
	}

	@XmlElement(name="Level")
	public int getLevelValue() {
		return levelValue;
	}
	public void setLevelValue(int levelValue) {
		this.levelValue = levelValue;
	}

	@XmlElement(name="ParentLimitRef")
	public String getParentLimitRef() {
		return parentLimitRef;
	}
	public void setParentLimitRef(String parentLimitRef) {
		this.parentLimitRef = parentLimitRef;
	}

	@XmlElement(name="Rev_Nrev")
	public String getRev_Nrev() {
		return rev_Nrev;
	}
	public void setRev_Nrev(String rev_Nrev) {
		this.rev_Nrev = rev_Nrev;
	}

	@XmlElement(name="LimitDesc")
	public String getLimitDesc() {
		return limitDesc;
	}
	public void setLimitDesc(String limitDesc) {
		this.limitDesc = limitDesc;
	}

	@XmlElement(name="Drawees")
	public List<Drawees> getDrawees() {
		return drawees;
	}
	public void setDrawees(List<Drawees> drawees) {
		this.drawees = drawees;
	}

	@XmlElement(name="Brokers")
	public List<Brokers> getBrokers() {
		return brokers;
	}

	public void setBrokers(List<Brokers> brokers) {
		this.brokers = brokers;
	}

	public String getCustomerReference() {
		return customerReference;
	}

	public void setCustomerReference(String customerReference) {
		this.customerReference = customerReference;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}


	@XmlElement(name = "StudyFee")
	public BigDecimal getStudyFee() {
		return studyFee;
	}

	public void setStudyFee(BigDecimal studyFee) {
		this.studyFee = studyFee;
	}

	@XmlElement(name = "StudyFeeCcy")
	public String getStudyFeeCcy() {
		return studyFeeCcy;
	}

	public void setStudyFeeCcy(String studyFeeCcy) {
		this.studyFeeCcy = studyFeeCcy;
	}

	@XmlElement(name = "StudyFeeStartDate")
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	public Date getStudyFeeStartDate() {
		return studyFeeStartDate;
	}

	public void setStudyFeeStartDate(Date studyFeeStartDate) {
		this.studyFeeStartDate = studyFeeStartDate;
	}

	@XmlElement(name = "StudyFeeExpiryDate")
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	public Date getStudyFeeExpiryDate() {
		return studyFeeExpiryDate;
	}

	public void setStudyFeeExpiryDate(Date studyFeeExpiryDate) {
		this.studyFeeExpiryDate = studyFeeExpiryDate;
	}


	private static class DateFormatterAdapter extends XmlAdapter<String, Date> {
		private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		@Override
		public Date unmarshal(final String v) throws Exception {
			return dateFormat.parse(v);
		}

		@Override
		public String marshal(final Date v) throws Exception {
			return dateFormat.format(v);
		}
	}
}
