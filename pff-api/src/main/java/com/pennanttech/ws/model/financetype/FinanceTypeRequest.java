package com.pennanttech.ws.model.financetype;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "financeType")
public class FinanceTypeRequest implements Serializable {

	private static final long serialVersionUID = 4240224916197625490L;

	public FinanceTypeRequest() {
	    super();
	}

	// Request fields
	private String finType;
	@XmlElement(name = "promotionCode")
	private String promotionType;
	private boolean basicDetailReq;
	private boolean grcDetailReq;
	private boolean repayDetailReq;
	private boolean overdueDetailReq;
	private boolean overdueProfitDetailReq;
	private boolean stepDetailReq;
	private boolean feeReq;
	private boolean partnerBankDetailReq;

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getPromotionType() {
		return promotionType;
	}

	public void setPromotionType(String promotionType) {
		this.promotionType = promotionType;
	}

	public boolean isBasicDetailReq() {
		return basicDetailReq;
	}

	public void setBasicDetailReq(boolean basicDetailReq) {
		this.basicDetailReq = basicDetailReq;
	}

	public boolean isGrcDetailReq() {
		return grcDetailReq;
	}

	public void setGrcDetailReq(boolean grcDetailReq) {
		this.grcDetailReq = grcDetailReq;
	}

	public boolean isRepayDetailReq() {
		return repayDetailReq;
	}

	public void setRepayDetailReq(boolean repayDetailReq) {
		this.repayDetailReq = repayDetailReq;
	}

	public boolean isOverdueDetailReq() {
		return overdueDetailReq;
	}

	public void setOverdueDetailReq(boolean overdueDetailReq) {
		this.overdueDetailReq = overdueDetailReq;
	}

	public boolean isOverdueProfitDetailReq() {
		return overdueProfitDetailReq;
	}

	public void setOverdueProfitDetailReq(boolean overdueProfitDetailReq) {
		this.overdueProfitDetailReq = overdueProfitDetailReq;
	}

	public boolean isStepDetailReq() {
		return stepDetailReq;
	}

	public void setStepDetailReq(boolean stepDetailReq) {
		this.stepDetailReq = stepDetailReq;
	}

	public boolean isFeeReq() {
		return feeReq;
	}

	public void setFeeReq(boolean feeReq) {
		this.feeReq = feeReq;
	}

	public boolean isPartnerBankDetailReq() {
		return partnerBankDetailReq;
	}

	public void setPartnerBankDetailReq(boolean partnerBankDetailReq) {
		this.partnerBankDetailReq = partnerBankDetailReq;
	}
}
