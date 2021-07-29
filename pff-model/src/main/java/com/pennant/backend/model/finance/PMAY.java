/**
 * 
 */
package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * @author nikhil.t
 *
 */
public class PMAY extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 1L;

	private String finReference;
	private String custCif;
	private String custShrtName;
	private boolean notifiedTown;
	private long townCode;
	private String townName;
	private boolean centralAssistance;
	private boolean ownedHouse;
	private BigDecimal carpetArea = BigDecimal.ZERO;
	private BigDecimal householdAnnIncome = BigDecimal.ZERO;
	private boolean balanceTransfer;
	private boolean primaryApplicant;
	private boolean prprtyOwnedByWomen;
	private String product;
	private String transactionFinType;
	private boolean waterSupply;
	private boolean drinage;
	private boolean electricity;
	private String pmayCategory;
	@XmlTransient
	private PMAY befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	private List<PmayEligibilityLog> pmayEligibilityLogList = new ArrayList<>();

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("custCif");
		excludeFields.add("custShrtName");
		excludeFields.add("townName");
		return excludeFields;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public boolean isOwnedHouse() {
		return ownedHouse;
	}

	public void setOwnedHouse(boolean ownedHouse) {
		this.ownedHouse = ownedHouse;
	}

	public boolean isCentralAssistance() {
		return centralAssistance;
	}

	public void setCentralAssistance(boolean centralAssistance) {
		this.centralAssistance = centralAssistance;
	}

	public BigDecimal getHouseholdAnnIncome() {
		return householdAnnIncome;
	}

	public void setHouseholdAnnIncome(BigDecimal householdAnnIncome) {
		this.householdAnnIncome = householdAnnIncome;
	}

	public BigDecimal getCarpetArea() {
		return carpetArea;
	}

	public void setCarpetArea(BigDecimal carpetArea) {
		this.carpetArea = carpetArea;
	}

	public PMAY getBefImage() {
		return this.befImage;
	}

	public void setBefImage(PMAY beforeImage) {
		this.befImage = beforeImage;

	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public boolean isNotifiedTown() {
		return notifiedTown;
	}

	public void setNotifiedTown(boolean notifiedTown) {
		this.notifiedTown = notifiedTown;
	}

	public boolean isBalanceTransfer() {
		return balanceTransfer;
	}

	public void setBalanceTransfer(boolean balanceTransfer) {
		this.balanceTransfer = balanceTransfer;
	}

	public boolean isPrimaryApplicant() {
		return primaryApplicant;
	}

	public void setPrimaryApplicant(boolean primaryApplicant) {
		this.primaryApplicant = primaryApplicant;
	}

	public String getTransactionFinType() {
		return transactionFinType;
	}

	public void setTransactionFinType(String transactionFinType) {
		this.transactionFinType = transactionFinType;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public boolean isPrprtyOwnedByWomen() {
		return prprtyOwnedByWomen;
	}

	public void setPrprtyOwnedByWomen(boolean prprtyOwnedByWomen) {
		this.prprtyOwnedByWomen = prprtyOwnedByWomen;
	}

	public boolean isDrinage() {
		return drinage;
	}

	public void setDrinage(boolean drinage) {
		this.drinage = drinage;
	}

	public boolean isWaterSupply() {
		return waterSupply;
	}

	public void setWaterSupply(boolean waterSupply) {
		this.waterSupply = waterSupply;
	}

	public boolean isElectricity() {
		return electricity;
	}

	public void setElectricity(boolean electricity) {
		this.electricity = electricity;
	}

	public String getPmayCategory() {
		return pmayCategory;
	}

	public void setPmayCategory(String pmayCategory) {
		this.pmayCategory = pmayCategory;
	}

	public List<PmayEligibilityLog> getPmayEligibilityLogList() {
		return pmayEligibilityLogList;
	}

	public void setPmayEligibilityLogList(List<PmayEligibilityLog> pmayEligibilityLogList) {
		this.pmayEligibilityLogList = pmayEligibilityLogList;
	}

	public String getCustCif() {
		return custCif;
	}

	public void setCustCif(String custCif) {
		this.custCif = custCif;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public long getTownCode() {
		return townCode;
	}

	public void setTownCode(long townCode) {
		this.townCode = townCode;
	}

	public String getTownName() {
		return townName;
	}

	public void setTownName(String townName) {
		this.townName = townName;
	}
}
