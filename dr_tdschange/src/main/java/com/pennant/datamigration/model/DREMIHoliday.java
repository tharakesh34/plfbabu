package com.pennant.datamigration.model;

import java.math.BigDecimal;
import java.util.Date;

public class DREMIHoliday {
	private String finReference;
	private Date ehStartDate;
	private Date ehEndDate;
	private int ehInst;
	private String ehMethod;
	private boolean bounceWaiver = false;
	private Date appDate;
	private String ehStatus;
	private String ehStatusRemarks;
	private String branchCode;
	private String productType;
	private String finType;
	private int oldBucket = 0;
	private int newBucket = 0;
	private int dpd = 0;
	private BigDecimal oldEMIOS = BigDecimal.ZERO;
	private BigDecimal newEMIOS = BigDecimal.ZERO;
	private int oldBalTenure = 0;
	private int newBalTenure = 0;
	private Date oldMaturity;
	private Date newMaturity;
	private Date lastBilledDate;
	private int lastBilledInstNo;
	private BigDecimal actLoanAmount = BigDecimal.ZERO;
	private int oldTenure = 0;
	private int newTenure = 0;
	private BigDecimal oldInterest = BigDecimal.ZERO;
	private BigDecimal newInterest = BigDecimal.ZERO;
	private BigDecimal cpzInterest = BigDecimal.ZERO;
	private BigDecimal bounceWaived = BigDecimal.ZERO;
	private Date lastMntDate;
	private long linkedTranID = -1;
	private int oldMaxUnPlannedEMI = 0;
	private int newMaxUnPlannedEMI = 0;
	private int oldAvailedUnPlanEMI = 0;
	private int newAvailedUnPlanEMI = 0;
	private BigDecimal oldFinalEMI = BigDecimal.ZERO;
	private BigDecimal newFinalEMI = BigDecimal.ZERO;

	private BigDecimal instChg = BigDecimal.ZERO;
	private BigDecimal instIntChg = BigDecimal.ZERO;
	private BigDecimal instPriChg = BigDecimal.ZERO;
	private BigDecimal pastCpzChg = BigDecimal.ZERO;
	private BigDecimal pftChg = BigDecimal.ZERO;

	public int getOldMaxUnPlannedEMI() {
		return oldMaxUnPlannedEMI;
	}

	public void setOldMaxUnPlannedEMI(int oldMaxUnPlannedEMI) {
		this.oldMaxUnPlannedEMI = oldMaxUnPlannedEMI;
	}

	public int getNewMaxUnPlannedEMI() {
		return newMaxUnPlannedEMI;
	}

	public void setNewMaxUnPlannedEMI(int newMaxUnPlannedEMI) {
		this.newMaxUnPlannedEMI = newMaxUnPlannedEMI;
	}

	public int getOldAvailedUnPlanEMI() {
		return oldAvailedUnPlanEMI;
	}

	public void setOldAvailedUnPlanEMI(int oldAvailedUnPlanEMI) {
		this.oldAvailedUnPlanEMI = oldAvailedUnPlanEMI;
	}

	public int getNewAvailedUnPlanEMI() {
		return newAvailedUnPlanEMI;
	}

	public void setNewAvailedUnPlanEMI(int newAvailedUnPlanEMI) {
		this.newAvailedUnPlanEMI = newAvailedUnPlanEMI;
	}

	public BigDecimal getOldFinalEMI() {
		return oldFinalEMI;
	}

	public void setOldFinalEMI(BigDecimal oldFinalEMI) {
		this.oldFinalEMI = oldFinalEMI;
	}

	public BigDecimal getNewFinalEMI() {
		return newFinalEMI;
	}

	public void setNewFinalEMI(BigDecimal newFinalEMI) {
		this.newFinalEMI = newFinalEMI;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Date getEHStartDate() {
		return ehStartDate;
	}

	public void setEHStartDate(Date ehStartDate) {
		this.ehStartDate = ehStartDate;
	}

	public int getEHInst() {
		return ehInst;
	}

	public void setEHInst(int ehInst) {
		this.ehInst = ehInst;
	}

	public String getEHMethod() {
		return ehMethod;
	}

	public void setEHMethod(String ehMethod) {
		this.ehMethod = ehMethod;
	}

	public boolean isBounceWaiver() {
		return bounceWaiver;
	}

	public void setBounceWaiver(boolean bounceWaiver) {
		this.bounceWaiver = bounceWaiver;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public String getEHStatus() {
		return ehStatus;
	}

	public void setEHStatus(String ehStatus) {
		this.ehStatus = ehStatus;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public int getOldBucket() {
		return oldBucket;
	}

	public void setOldBucket(int oldBucket) {
		this.oldBucket = oldBucket;
	}

	public int getNewBucket() {
		return newBucket;
	}

	public void setNewBucket(int newBucket) {
		this.newBucket = newBucket;
	}

	public int getDpd() {
		return dpd;
	}

	public void setDpd(int dpd) {
		this.dpd = dpd;
	}

	public BigDecimal getOldEMIOS() {
		return oldEMIOS;
	}

	public void setOldEMIOS(BigDecimal oldEMIOS) {
		this.oldEMIOS = oldEMIOS;
	}

	public BigDecimal getNewEMIOS() {
		return newEMIOS;
	}

	public void setNewEMIOS(BigDecimal newEMIOS) {
		this.newEMIOS = newEMIOS;
	}

	public int getOldBalTenure() {
		return oldBalTenure;
	}

	public void setOldBalTenure(int oldBalTenure) {
		this.oldBalTenure = oldBalTenure;
	}

	public int getNewBalTenure() {
		return newBalTenure;
	}

	public void setNewBalTenure(int newBalTenure) {
		this.newBalTenure = newBalTenure;
	}

	public Date getOldMaturity() {
		return oldMaturity;
	}

	public void setOldMaturity(Date oldMaturity) {
		this.oldMaturity = oldMaturity;
	}

	public Date getNewMaturity() {
		return newMaturity;
	}

	public void setNewMaturity(Date newMaturity) {
		this.newMaturity = newMaturity;
	}

	public Date getLastBilledDate() {
		return lastBilledDate;
	}

	public void setLastBilledDate(Date lastBilledDate) {
		this.lastBilledDate = lastBilledDate;
	}

	public int getLastBilledInstNo() {
		return lastBilledInstNo;
	}

	public void setLastBilledInstNo(int lastBilledInstNo) {
		this.lastBilledInstNo = lastBilledInstNo;
	}

	public BigDecimal getActLoanAmount() {
		return actLoanAmount;
	}

	public void setActLoanAmount(BigDecimal actLoanAmount) {
		this.actLoanAmount = actLoanAmount;
	}

	public int getOldTenure() {
		return oldTenure;
	}

	public void setOldTenure(int oldTenure) {
		this.oldTenure = oldTenure;
	}

	public int getNewTenure() {
		return newTenure;
	}

	public void setNewTenure(int newTenure) {
		this.newTenure = newTenure;
	}

	public BigDecimal getOldInterest() {
		return oldInterest;
	}

	public void setOldInterest(BigDecimal oldInterest) {
		this.oldInterest = oldInterest;
	}

	public BigDecimal getNewInterest() {
		return newInterest;
	}

	public void setNewInterest(BigDecimal newInterest) {
		this.newInterest = newInterest;
	}

	public BigDecimal getCpzInterest() {
		return cpzInterest;
	}

	public void setCpzInterest(BigDecimal cpzInterest) {
		this.cpzInterest = cpzInterest;
	}

	public BigDecimal getBounceWaived() {
		return bounceWaived;
	}

	public void setBounceWaived(BigDecimal bounceWaived) {
		this.bounceWaived = bounceWaived;
	}

	public Date getLastMntDate() {
		return lastMntDate;
	}

	public void setLastMntDate(Date lastMntDate) {
		this.lastMntDate = lastMntDate;
	}

	public long getLinkedTranID() {
		return linkedTranID;
	}

	public void setLinkedTranID(long linkedTranID) {
		this.linkedTranID = linkedTranID;
	}

	public Date getEhEndDate() {
		return ehEndDate;
	}

	public void setEhEndDate(Date ehEndDate) {
		this.ehEndDate = ehEndDate;
	}

	public String getEhStatusRemarks() {
		return ehStatusRemarks;
	}

	public void setEhStatusRemarks(String ehStatusRemarks) {
		this.ehStatusRemarks = ehStatusRemarks;
	}

	public BigDecimal getInstPriChg() {
		return instPriChg;
	}

	public void setInstPriChg(BigDecimal instPriChg) {
		this.instPriChg = instPriChg;
	}

	public BigDecimal getInstChg() {
		return instChg;
	}

	public void setInstChg(BigDecimal instChg) {
		this.instChg = instChg;
	}

	public BigDecimal getInstIntChg() {
		return instIntChg;
	}

	public void setInstIntChg(BigDecimal instIntChg) {
		this.instIntChg = instIntChg;
	}

	public BigDecimal getPastCpzChg() {
		return pastCpzChg;
	}

	public void setPastCpzChg(BigDecimal pastCpzChg) {
		this.pastCpzChg = pastCpzChg;
	}

	public BigDecimal getPftChg() {
		return pftChg;
	}

	public void setPftChg(BigDecimal pftChg) {
		this.pftChg = pftChg;
	}
}