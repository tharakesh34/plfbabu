package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

@XmlType(propOrder = {
		"stepNo","tenorSplitPerc","rateMargin","emiSplitPerc"
})
@XmlAccessorType(XmlAccessType.NONE)
public class FinanceStepPolicyDetail extends AbstractWorkflowEntity {

    private static final long serialVersionUID = -2343217039719002642L;
    
	private String finReference;
	@XmlElement(name="stepNumber")
	private int stepNo;
	@XmlElement
	private BigDecimal tenorSplitPerc = BigDecimal.ZERO;
	private int installments;
	@XmlElement
	private BigDecimal rateMargin = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal emiSplitPerc = BigDecimal.ZERO;
	private BigDecimal steppedEMI = BigDecimal.ZERO;
	
	private boolean newRecord=false;
	private String lovValue;
	private FinanceStepPolicyDetail befImage;
	private LoggedInUser userDetails;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public FinanceStepPolicyDetail() {
		super();
	}

	public FinanceStepPolicyDetail(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		return new HashSet<String>();
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
		
	public String getId() {
		return finReference;
	}
	public void setId (String id) {
		this.finReference = id;
	}
		
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public int getStepNo() {
		return stepNo;
	}
	public void setStepNo(int stepNo) {
		this.stepNo = stepNo;
	}

	public int getInstallments() {
		return installments;
	}
	public void setInstallments(int installments) {
		this.installments = installments;
	}

	public BigDecimal getSteppedEMI() {
		return steppedEMI;
	}
	public void setSteppedEMI(BigDecimal steppedEMI) {
		this.steppedEMI = steppedEMI;
	}

	public BigDecimal getTenorSplitPerc() {
		return tenorSplitPerc;
	}
	public void setTenorSplitPerc(BigDecimal tenorSplitPerc) {
		this.tenorSplitPerc = tenorSplitPerc;
	}
	
	public BigDecimal getRateMargin() {
		return rateMargin;
	}
	public void setRateMargin(BigDecimal rateMargin) {
		this.rateMargin = rateMargin;
	}

	public BigDecimal getEmiSplitPerc() {
		return emiSplitPerc;
	}
	public void setEmiSplitPerc(BigDecimal emiSplitPerc) {
		this.emiSplitPerc = emiSplitPerc;
	}

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	
	public String getLovValue() {
		return lovValue;
	}
	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public FinanceStepPolicyDetail getBefImage(){
		return this.befImage;
	}
	public void setBefImage(FinanceStepPolicyDetail beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
