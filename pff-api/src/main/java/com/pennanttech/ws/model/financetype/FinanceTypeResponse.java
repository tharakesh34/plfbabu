package com.pennanttech.ws.model.financetype;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rulefactory.FeeRule;

@XmlType(propOrder = { "finType", "finTypeDesc", "basicDetail", "graceDetail", "repayDetail", "overdueDetail",
		"overdueProfitDetail", "insurance", "feeRule","finTypeFeesList", "stepDetail", "promotions", "returnStatus" })
@XmlAccessorType(XmlAccessType.FIELD)
public class FinanceTypeResponse implements Serializable {

	private static final long serialVersionUID = 4240224916197625490L;

	public FinanceTypeResponse() {

	}

	// Response fields
	private String finType;
	private String finTypeDesc;
	private BasicDetail basicDetail;
	private GraceDetail graceDetail;
	private RepayDetail repayDetail;
	private OverdueDetail overdueDetail;
	private OverdueProfitDetail overdueProfitDetail;
	private Insurance insurance;
	private FeeRule feeRule;
	private StepDetail stepDetail;
	
	@XmlElement(name="promotionDetail")
	private List<PromotionType> promotions;
	
	@XmlElementWrapper(name="fees")
	@XmlElement(name="fee")
	private List<FinTypeFees> finTypeFeesList = new ArrayList<FinTypeFees>();

	// Return status
	private WSReturnStatus returnStatus;

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public BasicDetail getBasicDetail() {
		return basicDetail;
	}

	public void setBasicDetail(BasicDetail basicDetail) {
		this.basicDetail = basicDetail;
	}

	public GraceDetail getGraceDetail() {
		return graceDetail;
	}

	public void setGraceDetail(GraceDetail graceDetail) {
		this.graceDetail = graceDetail;
	}

	public RepayDetail getRepayDetail() {
		return repayDetail;
	}

	public void setRepayDetail(RepayDetail repayDetail) {
		this.repayDetail = repayDetail;
	}

	public OverdueDetail getOverdueDetail() {
		return overdueDetail;
	}

	public void setOverdueDetail(OverdueDetail overdueDetail) {
		this.overdueDetail = overdueDetail;
	}

	public OverdueProfitDetail getOverdueProfitDetail() {
		return overdueProfitDetail;
	}

	public void setOverdueProfitDetail(OverdueProfitDetail overdueProfitDetail) {
		this.overdueProfitDetail = overdueProfitDetail;
	}

	public Insurance getInsurance() {
		return insurance;
	}

	public void setInsurance(Insurance insurance) {
		this.insurance = insurance;
	}

	public FeeRule getFeeRule() {
		return feeRule;
	}

	public void setFeeRule(FeeRule feeRule) {
		this.feeRule = feeRule;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public StepDetail getStepDetail() {
		return stepDetail;
	}

	public void setStepDetail(StepDetail stepDetail) {
		this.stepDetail = stepDetail;
	}
	
	public List<PromotionType> getPromotions() {
		return promotions;
	}

	public void setPromotions(List<PromotionType> promotions) {
		this.promotions = promotions;
	}
	
	public List<FinTypeFees> getFinTypeFeesList() {
		return finTypeFeesList;
	}

	public void setFinTypeFeesList(List<FinTypeFees> finTypeFeesList) {
		this.finTypeFeesList = finTypeFeesList;
	}
}
