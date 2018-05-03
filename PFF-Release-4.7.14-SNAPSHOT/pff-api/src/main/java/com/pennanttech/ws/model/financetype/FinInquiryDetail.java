package com.pennanttech.ws.model.financetype;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.pennant.backend.model.finance.JointAccountDetail;
@XmlAccessorType(XmlAccessType.NONE)
public class FinInquiryDetail {
	@XmlElement
	private String						finReference;
	@XmlElement
	private String						finType;
	@XmlElement
	private String						product;
	@XmlElement
	private String						finCcy;
	@XmlElement
	private BigDecimal					finAmount;
	@XmlElement
	private BigDecimal					finAssetValue;
	@XmlElement
	private int							numberOfTerms;
	@XmlElement
	private int							loanTenor;
	@XmlElement
	private Date						maturityDate;
	@XmlElement
	private BigDecimal					firstEmiAmount;
	@XmlElement
	private BigDecimal					nextRepayAmount;
	@XmlElement
	private BigDecimal					paidTotal;
	@XmlElement
	private BigDecimal					paidPri;
	@XmlElement
	private BigDecimal					paidPft;
	@XmlElement
	private BigDecimal					outstandingTotal;
	@XmlElement
	private BigDecimal					outstandingPri;
	@XmlElement
	private BigDecimal					outstandingPft;
	@XmlElement
	private int							futureInst;
	@XmlElement
	private String						finStatus;
	@XmlElement
	private String						disbStatus;
	@XmlElementWrapper(name = "coApplicants")
	@XmlElement(name = "coApplicant")
	private List<JointAccountDetail>	jountAccountDetailList	= new ArrayList<JointAccountDetail>(1);

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public BigDecimal getFinAmount() {
		return finAmount;
	}

	public void setFinAmount(BigDecimal finAmount) {
		this.finAmount = finAmount;
	}

	public BigDecimal getFinAssetValue() {
		return finAssetValue;
	}

	public void setFinAssetValue(BigDecimal finAssetValue) {
		this.finAssetValue = finAssetValue;
	}

	public int getNumberOfTerms() {
		return numberOfTerms;
	}

	public void setNumberOfTerms(int numberOfTerms) {
		this.numberOfTerms = numberOfTerms;
	}

	public int getLoanTenor() {
		return loanTenor;
	}

	public void setLoanTenor(int loanTenor) {
		this.loanTenor = loanTenor;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public BigDecimal getFirstEmiAmount() {
		return firstEmiAmount;
	}

	public void setFirstEmiAmount(BigDecimal firstEmiAmount) {
		this.firstEmiAmount = firstEmiAmount;
	}

	public BigDecimal getNextRepayAmount() {
		return nextRepayAmount;
	}

	public void setNextRepayAmount(BigDecimal nextRepayAmount) {
		this.nextRepayAmount = nextRepayAmount;
	}

	public BigDecimal getPaidTotal() {
		return paidTotal;
	}

	public void setPaidTotal(BigDecimal paidTotal) {
		this.paidTotal = paidTotal;
	}

	public BigDecimal getPaidPri() {
		return paidPri;
	}

	public void setPaidPri(BigDecimal paidPri) {
		this.paidPri = paidPri;
	}

	public BigDecimal getPaidPft() {
		return paidPft;
	}

	public void setPaidPft(BigDecimal paidPft) {
		this.paidPft = paidPft;
	}

	public BigDecimal getOutstandingTotal() {
		return outstandingTotal;
	}

	public void setOutstandingTotal(BigDecimal outstandingTotal) {
		this.outstandingTotal = outstandingTotal;
	}

	public BigDecimal getOutstandingPri() {
		return outstandingPri;
	}

	public void setOutstandingPri(BigDecimal outstandingPri) {
		this.outstandingPri = outstandingPri;
	}

	public BigDecimal getOutstandingPft() {
		return outstandingPft;
	}

	public void setOutstandingPft(BigDecimal outstandingPft) {
		this.outstandingPft = outstandingPft;
	}

	public int getFutureInst() {
		return futureInst;
	}

	public void setFutureInst(int futureInst) {
		this.futureInst = futureInst;
	}

	public String getFinStatus() {
		return finStatus;
	}

	public void setFinStatus(String finStatus) {
		this.finStatus = finStatus;
	}

	public String getDisbStatus() {
		return disbStatus;
	}

	public void setDisbStatus(String disbStatus) {
		this.disbStatus = disbStatus;
	}

	public List<JointAccountDetail> getJountAccountDetailList() {
		return jountAccountDetailList;
	}

	public void setJountAccountDetailList(List<JointAccountDetail> jountAccountDetailList) {
		this.jountAccountDetailList = jountAccountDetailList;
	}

}
