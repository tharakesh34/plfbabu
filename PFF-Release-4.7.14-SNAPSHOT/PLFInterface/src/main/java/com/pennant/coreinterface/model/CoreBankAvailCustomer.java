package com.pennant.coreinterface.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class CoreBankAvailCustomer implements Serializable {
	
	private static final long serialVersionUID = 8740382994704771360L;
	
	public CoreBankAvailCustomer() {
    	super();
    }
	
	private String custMnemonic;
	private String offBSRequired;
	private String acRcvblRequired;
	private String acPayblRequired;
	private String acUnclsRequired;
	private String collateralRequired;
	
	private int offBSCount = 0;
	private int acRcvblCount = 0;
	private int acPayblCount = 0;
	private int acUnclsCount = 0;
	private int collateralCount = 0;
	
	private BigDecimal custActualBal = BigDecimal.ZERO;
	private BigDecimal custBlockedBal = BigDecimal.ZERO;
	private BigDecimal custDeposit = BigDecimal.ZERO;
	private BigDecimal custBlockedDeposit = BigDecimal.ZERO;
	private BigDecimal totalCustBal = BigDecimal.ZERO;
	private BigDecimal totalCustBlockedBal = BigDecimal.ZERO;
	private CustomerLimit customerLimit = new CustomerLimit();
	
	
	private String custRspData ;
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getCustMnemonic() {
		return custMnemonic;
	}
	public void setCustMnemonic(String custMnemonic) {
		this.custMnemonic = custMnemonic;
	}

	public String getOffBSRequired() {
		return offBSRequired;
	}
	public void setOffBSRequired(String offBSRequired) {
		this.offBSRequired = offBSRequired;
	}

	public String getAcRcvblRequired() {
		return acRcvblRequired;
	}
	public void setAcRcvblRequired(String acRcvblRequired) {
		this.acRcvblRequired = acRcvblRequired;
	}

	public String getAcPayblRequired() {
		return acPayblRequired;
	}
	public void setAcPayblRequired(String acPayblRequired) {
		this.acPayblRequired = acPayblRequired;
	}

	public String getAcUnclsRequired() {
		return acUnclsRequired;
	}
	public void setAcUnclsRequired(String acUnclsRequired) {
		this.acUnclsRequired = acUnclsRequired;
	}

	public String getCollateralRequired() {
		return collateralRequired;
	}
	public void setCollateralRequired(String collateralRequired) {
		this.collateralRequired = collateralRequired;
	}

	public int getOffBSCount() {
		return offBSCount;
	}
	public void setOffBSCount(int offBSCount) {
		this.offBSCount = offBSCount;
	}

	public int getAcRcvblCount() {
		return acRcvblCount;
	}
	public void setAcRcvblCount(int acRcvblCount) {
		this.acRcvblCount = acRcvblCount;
	}

	public int getAcPayblCount() {
		return acPayblCount;
	}
	public void setAcPayblCount(int acPayblCount) {
		this.acPayblCount = acPayblCount;
	}

	public int getAcUnclsCount() {
		return acUnclsCount;
	}
	public void setAcUnclsCount(int acUnclsCount) {
		this.acUnclsCount = acUnclsCount;
	}

	public int getCollateralCount() {
		return collateralCount;
	}
	public void setCollateralCount(int collateralCount) {
		this.collateralCount = collateralCount;
	}

	public String getCustRspData() {
		return custRspData;
	}
	public void setCustRspData(String custRspData) {
		this.custRspData = custRspData;
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
	public CustomerLimit getCustomerLimit() {
		return customerLimit;
	}
	public void setCustomerLimit(CustomerLimit customerLimit) {
		this.customerLimit = customerLimit;
	}
	

	
}
