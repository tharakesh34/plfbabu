package com.pennant.backend.model.MMAgreement;



public class MMAgreementGenDetail {

	private String contractDate ="";
	private String custCIF ="";
	private String custName ="";
	private String 	mMADate;
	private String purchRegOffice ="";
	private String contractAmt = "";
	private String purchaddress ="";
	private String attention ="";
	private String fax ="";
	private String rate = "";
	private String titleNo ="";
	private String POBox ="";
	private String appDate="";
	private String initiatedDate ="";
	private String eIBaseRate ="";
	
	private String city ="";
	private String country = "";
	private String province ="";

	private String fOLIssueDate ="";
	private String maturityDate = "";
	private String facilityLimit ="";
	private String profitRate ="";
	private String baseRateCode ="";
	private String minRate = "";
	private String margin ="";
	private String profitPeriod ="";
	private String minAmount = "";
	private String numberOfTerms ="";
	private String latePayRate ="";
	
	private String folReference ="";
	private String avlPerDays ="";
	private String maxCapProfitRate ="";
	private String minCapRate = "";
	private String facOfferLetterDate ="";
	private String numOfContracts ="";
	private String pmaryRelOfficer = "";
	private String custAccount ="";

	private String downPaybank="";
	
	private String assetDesc="";
	private String quantity="";
	private String unitPrice="";
	private String supplier="";
	private String assetValue="";
	

	private String totalProfit;
	private String avlPeriodInWords;
	private String facLimitInWords;
	private String leasePeriodInWords;
	private String leaseTermsWords;
	private String odPri;
	private String odPft;	///(OD PRI + OD PFT) -in case of OD exists if NO OD then it is PRV INSTL PRI AMT
	private String paidPri;				//PAID PRI + PAID PFT + TOTAL INCR COST PAID+ TOTAL SUPL RENT PAID + FEES(
	private String paidPft;
	private String totalIncrCostPaid;
	private String totalSuplRentPaid;
	private String fee;
	
		
		public String geteIBaseRate() {
		return eIBaseRate;
	}
	public void seteIBaseRate(String eIBaseRate) {
		this.eIBaseRate = eIBaseRate;
	}
	public String getLeaseTermsWords() {
		return leaseTermsWords;
	}
	public void setLeaseTermsWords(String leaseTermsWords) {
		this.leaseTermsWords = leaseTermsWords;
	}
	public String getOdPri() {
		return odPri;
	}
	public void setOdPri(String odPri) {
		this.odPri = odPri;
	}
	public String getOdPft() {
		return odPft;
	}
	public void setOdPft(String odPft) {
		this.odPft = odPft;
	}
	public String getPaidPri() {
		return paidPri;
	}
	public void setPaidPri(String paidPri) {
		this.paidPri = paidPri;
	}
	public String getPaidPft() {
		return paidPft;
	}
	public void setPaidPft(String paidPft) {
		this.paidPft = paidPft;
	}
	public String getTotalIncrCostPaid() {
		return totalIncrCostPaid;
	}
	public void setTotalIncrCostPaid(String totalIncrCostPaid) {
		this.totalIncrCostPaid = totalIncrCostPaid;
	}
	public String getTotalSuplRentPaid() {
		return totalSuplRentPaid;
	}
	public void setTotalSuplRentPaid(String totalSuplRentPaid) {
		this.totalSuplRentPaid = totalSuplRentPaid;
	}
	public String getFee() {
		return fee;
	}
	public void setFee(String fee) {
		this.fee = fee;
	}
		public String getContractDate() {
			return contractDate;
		}
		public void setContractDate(String contractDate) {
			this.contractDate = contractDate;
		}
		
		public String getCustCIF() {
			return custCIF;
		}
		public void setCustCIF(String custCIF) {
			this.custCIF = custCIF;
		}
		
		public String getCustName() {
			return custName;
		}
		public void setCustName(String custName) {
			this.custName = custName;
		}
		
		public String getPurchRegOffice() {
			return purchRegOffice;
		}

		public void setPurchRegOffice(String purchRegOffice) {
			this.purchRegOffice = purchRegOffice;
		}

		
		public String getPurchaddress() {
			return purchaddress;
		}

		public void setPurchaddress(String purchaddress) {
			this.purchaddress = purchaddress;
		}

		public String getAttention() {
			return attention;
		}

		public void setAttention(String attention) {
			this.attention = attention;
		}

		public String getFax() {
			return fax;
		}

		public void setFax(String fax) {
			this.fax = fax;
		}

		public String getTitleNo() {
			return titleNo;
		}

		public void setTitleNo(String titleNo) {
			this.titleNo = titleNo;
		}

		public String getContractAmt() {
		    return contractAmt;
	    }

		public void setContractAmt(String contractAmt) {
		    this.contractAmt = contractAmt;
	    }

		public String getRate() {
		    return rate;
	    }

		public void setRate(String rate) {
		    this.rate = rate;
	    }

		public String getFacilityLimit() {
			return facilityLimit;
		}

		public void setFacilityLimit(String facilityLimit) {
			this.facilityLimit = facilityLimit;
		}

		public String getProfitRate() {
			return profitRate;
		}

		public void setProfitRate(String profitRate) {
			this.profitRate = profitRate;
		}

		public String getBaseRateCode() {
			return baseRateCode;
		}

		public void setBaseRateCode(String baseRateCode) {
			this.baseRateCode = baseRateCode;
		}

		public String getMinRate() {
			return minRate;
		}

		public void setMinRate(String minRate) {
			this.minRate = minRate;
		}

		public String getMargin() {
			return margin;
		}

		public void setMargin(String margin) {
			this.margin = margin;
		}

		public String getProfitPeriod() {
			return profitPeriod;
		}

		public void setProfitPeriod(String profitPeriod) {
			this.profitPeriod = profitPeriod;
		}

		public String getMinAmount() {
			return minAmount;
		}

		public void setMinAmount(String minAmount) {
			this.minAmount = minAmount;
		}

		public String getNumberOfTerms() {
			return numberOfTerms;
		}

		public void setNumberOfTerms(String numberOfTerms) {
			this.numberOfTerms = numberOfTerms;
		}

		public String getLatePayRate() {
			return latePayRate;
		}

		public void setLatePayRate(String latePayRate) {
			this.latePayRate = latePayRate;
		}


		public String getmMADate() {
			return mMADate;
		}

		public void setmMADate(String mMADate) {
			this.mMADate = mMADate;
		}

		public String getfOLIssueDate() {
		    return fOLIssueDate;
	    }

		public void setfOLIssueDate(String fOLIssueDate) {
		    this.fOLIssueDate = fOLIssueDate;
	    }

		public String getCity() {
		    return city;
	    }

		public void setCity(String city) {
		    this.city = city;
	    }

		public String getProvince() {
		    return province;
	    }

		public void setProvince(String province) {
		    this.province = province;
	    }

		public String getCountry() {
		    return country;
	    }

		public void setCountry(String country) {
		    this.country = country;
	    }

		public String getMaturityDate() {
	        return maturityDate;
        }

		public void setMaturityDate(String maturityDate) {
	        this.maturityDate = maturityDate;
        }
		public String getFolReference() {
	        return folReference;
        }
		public void setFolReference(String folReference) {
	        this.folReference = folReference;
        }
		public String getAvlPerDays() {
	        return avlPerDays;
        }
		public void setAvlPerDays(String avlPerDays) {
	        this.avlPerDays = avlPerDays;
        }
		public String getMaxCapProfitRate() {
	        return maxCapProfitRate;
        }
		public void setMaxCapProfitRate(String maxCapProfitRate) {
	        this.maxCapProfitRate = maxCapProfitRate;
        }
		public String getMinCapRate() {
	        return minCapRate;
        }
		public void setMinCapRate(String minCapRate) {
	        this.minCapRate = minCapRate;
        }
		public String getFacOfferLetterDate() {
	        return facOfferLetterDate;
        }
		public void setFacOfferLetterDate(String facOfferLetterDate) {
	        this.facOfferLetterDate = facOfferLetterDate;
        }
		public String getNumOfContracts() {
	        return numOfContracts;
        }
		public void setNumOfContracts(String numOfContracts) {
	        this.numOfContracts = numOfContracts;
        }
		public String getPmaryRelOfficer() {
	        return pmaryRelOfficer;
        }
		public void setPmaryRelOfficer(String pmaryRelOfficer) {
	        this.pmaryRelOfficer = pmaryRelOfficer;
        }
		public String getCustAccount() {
	        return custAccount;
        }
		public void setCustAccount(String custAccount) {
	        this.custAccount = custAccount;
        }
		public String getAvlPeriodInWords() {
	        return avlPeriodInWords;
        }
		public void setAvlPeriodInWords(String avlPeriodInWords) {
	        this.avlPeriodInWords = avlPeriodInWords;
        }
		public String getFacLimitInWords() {
	        return facLimitInWords;
        }
		public void setFacLimitInWords(String facLimitInWords) {
	        this.facLimitInWords = facLimitInWords;
        }
		public String getLeasePeriodInWords() {
	        return leasePeriodInWords;
        }
		public void setLeasePeriodInWords(String leasePeriodInWords) {
	        this.leasePeriodInWords = leasePeriodInWords;
        }
		
		public String getPOBox() {
			return POBox;
		}
		public void setPOBox(String pOBox) {
			POBox = pOBox;
		}
		
		public String getAppDate() {
			return appDate;
		}
		public void setAppDate(String appDate) {
			this.appDate = appDate;
		}
		
		public String getInitiatedDate() {
			return initiatedDate;
		}
		public void setInitiatedDate(String initiatedDate) {
			this.initiatedDate = initiatedDate;
		}
		
		public String getDownPaybank() {
			return downPaybank;
		}
		public void setDownPaybank(String downPaybank) {
			this.downPaybank = downPaybank;
		}
		
		public String getAssetDesc() {
			return assetDesc;
		}
		public void setAssetDesc(String assetDesc) {
			this.assetDesc = assetDesc;
		}
		
		public String getQuantity() {
			return quantity;
		}
		public void setQuantity(String quantity) {
			this.quantity = quantity;
		}
		
		public String getUnitPrice() {
			return unitPrice;
		}
		public void setUnitPrice(String unitPrice) {
			this.unitPrice = unitPrice;
		}
		
		public String getSupplier() {
			return supplier;
		}
		public void setSupplier(String supplier) {
			this.supplier = supplier;
		}
		
		public String getAssetValue() {
			return assetValue;
		}
		public void setAssetValue(String assetValue) {
			this.assetValue = assetValue;
		}
		
		public String getTotalProfit() {
			return totalProfit;
		}
		public void setTotalProfit(String totalProfit) {
			this.totalProfit = totalProfit;
		}
		public String getEIBaseRate() {
	        return eIBaseRate;
        }
		public void setEIBaseRate(String eIBaseRate) {
	        this.eIBaseRate = eIBaseRate;
        }
	
		
}
