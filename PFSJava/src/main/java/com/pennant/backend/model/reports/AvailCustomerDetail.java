package com.pennant.backend.model.reports;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AvailCustomerDetail {

	private String custCIF = "";
	private boolean offBSRequired = true;
	private boolean acRcvblRequired = true;
	private boolean acPayblRequired = true;
	private boolean acUnclsRequired = true;
	private boolean collateralRequired = true;

	private List<AvailAccount> offBSAcList = new ArrayList<AvailAccount>();
	private List<AvailAccount> acRcvblList = new ArrayList<AvailAccount>();
	private List<AvailAccount> acPayblList = new ArrayList<AvailAccount>();
	private List<AvailAccount> acUnclsList = new ArrayList<AvailAccount>();
	private List<AvailCollateral> colList = new ArrayList<AvailCollateral>();

	private BigDecimal custActualBal = BigDecimal.ZERO;
	private BigDecimal custBlockedBal = BigDecimal.ZERO;
	private BigDecimal custDeposit = BigDecimal.ZERO;
	private BigDecimal custBlockedDeposit = BigDecimal.ZERO;
	private BigDecimal totalCustBal = BigDecimal.ZERO;
	private BigDecimal totalCustBlockedBal = BigDecimal.ZERO;

	private AvailLimit availLimit = null;

	// Account Type List
	private List<String> accTypeList = new ArrayList<String>();

	public AvailCustomerDetail() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public boolean isOffBSRequired() {
		return offBSRequired;
	}

	public void setOffBSRequired(boolean offBSRequired) {
		this.offBSRequired = offBSRequired;
	}

	public boolean isAcRcvblRequired() {
		return acRcvblRequired;
	}

	public void setAcRcvblRequired(boolean acRcvblRequired) {
		this.acRcvblRequired = acRcvblRequired;
	}

	public boolean isAcPayblRequired() {
		return acPayblRequired;
	}

	public void setAcPayblRequired(boolean acPayblRequired) {
		this.acPayblRequired = acPayblRequired;
	}

	public boolean isAcUnclsRequired() {
		return acUnclsRequired;
	}

	public void setAcUnclsRequired(boolean acUnclsRequired) {
		this.acUnclsRequired = acUnclsRequired;
	}

	public boolean isCollateralRequired() {
		return collateralRequired;
	}

	public void setCollateralRequired(boolean collateralRequired) {
		this.collateralRequired = collateralRequired;
	}

	public void setOffBSAcList(List<AvailAccount> offBSAcList) {
		this.offBSAcList = offBSAcList;
	}

	public List<AvailAccount> getOffBSAcList() {
		return offBSAcList;
	}

	public void setAcRcvblList(List<AvailAccount> acRcvblList) {
		this.acRcvblList = acRcvblList;
	}

	public List<AvailAccount> getAcRcvblList() {
		return acRcvblList;
	}

	public void setAcPayblList(List<AvailAccount> acPayblList) {
		this.acPayblList = acPayblList;
	}

	public List<AvailAccount> getAcPayblList() {
		return acPayblList;
	}

	public void setColList(List<AvailCollateral> colList) {
		this.colList = colList;
	}

	public List<AvailCollateral> getColList() {
		return colList;
	}

	public BigDecimal getCustActualBal() {
		return custActualBal;
	}

	public void setCustActualBal(BigDecimal custActualBal) {
		this.custActualBal = custActualBal;
	}

	public BigDecimal getCustBlockedBal() {
		return custBlockedBal;
	}

	public void setCustBlockedBal(BigDecimal custBlockedBal) {
		this.custBlockedBal = custBlockedBal;
	}

	public BigDecimal getCustDeposit() {
		return custDeposit;
	}

	public void setCustDeposit(BigDecimal custDeposit) {
		this.custDeposit = custDeposit;
	}

	public BigDecimal getCustBlockedDeposit() {
		return custBlockedDeposit;
	}

	public void setCustBlockedDeposit(BigDecimal custBlockedDeposit) {
		this.custBlockedDeposit = custBlockedDeposit;
	}

	public BigDecimal getTotalCustBal() {
		return totalCustBal;
	}

	public void setTotalCustBal(BigDecimal totalCustBal) {
		this.totalCustBal = totalCustBal;
	}

	public BigDecimal getTotalCustBlockedBal() {
		return totalCustBlockedBal;
	}

	public void setTotalCustBlockedBal(BigDecimal totalCustBlockedBal) {
		this.totalCustBlockedBal = totalCustBlockedBal;
	}

	public void setAvailLimit(AvailLimit availLimit) {
		this.availLimit = availLimit;
	}

	public AvailLimit getAvailLimit() {
		return availLimit;
	}

	public List<String> getAccTypeList() {
		return accTypeList;
	}

	public void setAccTypeList(List<String> accTypeList) {
		this.accTypeList = accTypeList;
	}

	public List<AvailAccount> getAcUnclsList() {
		return acUnclsList;
	}

	public void setAcUnclsList(List<AvailAccount> acUnclsList) {
		this.acUnclsList = acUnclsList;
	}

}
