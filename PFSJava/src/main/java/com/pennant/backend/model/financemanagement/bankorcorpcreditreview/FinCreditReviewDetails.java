package com.pennant.backend.model.financemanagement.bankorcorpcreditreview;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.aspose.pdf.Document;
import com.pennant.backend.model.Entity;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FinCreditReviewDetails extends AbstractWorkflowEntity implements Entity {

	private static final long serialVersionUID = 3557119742009775415L;
	private long detailId  = Long.MIN_VALUE;
	private String creditRevCode;
	private String custCtgCode;

	private long customerId;
	private String auditYear;
	private String bankName;
	private String auditors;
    private boolean consolidated;
	private String location;	
	private BigDecimal conversionRate=BigDecimal.ZERO;
	private Date auditedDate;
	private boolean newRecord = false;
	private String lovValue;
	private FinCreditReviewDetails befImage;
	private LoggedInUser userDetails;
	private List<FinCreditReviewSummary> lovDescCreditReviewSummaryEntries =new ArrayList<FinCreditReviewSummary>();
	private List<FinCreditRevSubCategory> lovDescFinCreditRevSubCategory =new ArrayList<FinCreditRevSubCategory>();
	private List<CustomerDocument> customerDocumentList =new ArrayList<CustomerDocument>();
	
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	private String lovDescCustCIF;
	private String lovDescCustCtgCode;
	private String lovDescCustShrtName;
	private long 	noOfShares;
	private BigDecimal marketPrice;
	
	private int auditPeriod;
	private String auditType;
	private boolean qualified;
	private String currency;
	private String lovDescMaxAuditYear;
	private String lovDescMinAuditYear;
	private String division;
	private String document;
	private List<Notes> notesList;
	private String description;
	private BigDecimal item;
	
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getItem() {
		return item;
	}

	public void setItem(BigDecimal item) {
		this.item = item;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getLovDescCustCIF() {
    	return lovDescCustCIF;
    }

	public void setLovDescCustCIF(String lovDescCustCIF) {
    	this.lovDescCustCIF = lovDescCustCIF;
    }

	public String getLovDescCustCtgCode() {
    	return lovDescCustCtgCode;
    }

	public void setLovDescCustCtgCode(String lovDescCustCtgCode) {
    	this.lovDescCustCtgCode = lovDescCustCtgCode;
    }

	public String getLovDescCustShrtName() {
    	return lovDescCustShrtName;
    }

	public void setLovDescCustShrtName(String lovDescCustShrtName) {
    	this.lovDescCustShrtName = lovDescCustShrtName;
    }

	public FinCreditReviewDetails(){
		super();
	}
	
	public boolean isNew() {
		return isNewRecord();
	}

	public long getDetailId() {
    	return detailId;
    }
	public void setDetailId(long detailId) {
    	this.detailId = detailId;
    }
	
	public long getCustomerId() {
    	return customerId;
    }
	public void setCustomerId(long customerId) {
    	this.customerId = customerId;
    }
	public String getAuditYear() {
    	return auditYear;
    }
	public void setAuditYear(String auditYear) {
    	this.auditYear = auditYear;
    }
	public String getBankName() {
    	return bankName;
    }
	public void setBankName(String bankName) {
    	this.bankName = bankName;
    }
	public String getAuditors() {
    	return auditors;
    }
	public void setAuditors(String auditors) {
    	this.auditors = auditors;
    }
	
	public String getLocation() {
    	return location;
    }
	public void setLocation(String location) {
    	this.location = location;
    }
	
	public BigDecimal getConversionRate() {
    	return conversionRate;
    }
	public void setConversionRate(BigDecimal conversionRate) {
    	this.conversionRate = conversionRate;
    }
	public Date getAuditedDate() {
    	return auditedDate;
    }
	public String getCustCtgCode() {
		return custCtgCode;
	}
	
	public void setCustCtgCode(String custCtgCode) {
		this.custCtgCode = custCtgCode;
	}
	public void setAuditedDate(Date auditedDate) {
    	this.auditedDate = auditedDate;
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

	public FinCreditReviewDetails getBefImage() {
		return this.befImage;
	}
	public void setBefImage(FinCreditReviewDetails beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setCreditRevCode(String creditRevCode) {
	    this.creditRevCode = creditRevCode;
    }
	public String getCreditRevCode() {
	    return creditRevCode;
    }
	public void setCreditReviewSummaryEntries(List<FinCreditReviewSummary> lovDescCreditReviewSummaryEntries) {
	    this.lovDescCreditReviewSummaryEntries = lovDescCreditReviewSummaryEntries;
    }
	public List<FinCreditRevSubCategory> getLovDescFinCreditRevSubCategory() {
    	return lovDescFinCreditRevSubCategory;
    }

	public void setLovDescFinCreditRevSubCategory(
            List<FinCreditRevSubCategory> lovDescFinCreditRevSubCategory) {
    	this.lovDescFinCreditRevSubCategory = lovDescFinCreditRevSubCategory;
    }

	public void setCustomerDocumentList(List<CustomerDocument> customerDocumentList) {
	    this.customerDocumentList = customerDocumentList;
    }

	public List<CustomerDocument> getCustomerDocumentList() {
	    return customerDocumentList;
    }

	public List<FinCreditReviewSummary> getCreditReviewSummaryEntries() {
	    return lovDescCreditReviewSummaryEntries;
    }

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
	    this.auditDetailMap = auditDetailMap;
    }

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
	    return auditDetailMap;
    }

	@Override
    public long getId() {
	    
	    return this.detailId;
    }

	@Override
    public void setId(long id) {
		setDetailId(id);
	    
    }

	public void setNoOfShares(long noOfShares) {
	    this.noOfShares = noOfShares;
    }

	public long getNoOfShares() {
	    return noOfShares;
    }

	public void setMarketPrice(BigDecimal marketPrice) {
	    this.marketPrice = marketPrice;
    }

	public BigDecimal getMarketPrice() {
	    return marketPrice;
    }

	public void setAuditPeriod(int auditPeriod) {
	    this.auditPeriod = auditPeriod;
    }

	public int getAuditPeriod() {
	    return auditPeriod;
    }

	public String getAuditType() {
    	return auditType;
    }

	public void setAuditType(String auditType) {
    	this.auditType = auditType;
    }

	public List<Notes> getNotesList() {
    	return notesList;
    }

	public void setNotesList(List<Notes> notesList) {
    	this.notesList = notesList;
    }
	
	public boolean isConsolidated() {
	    return consolidated;
    }

	public void setConsolidated(boolean consolidated) {
	    this.consolidated = consolidated;
    }

	public String getCurrency() {
	    return currency;
    }

	public void setCurrency(String currency) {
	    this.currency = currency;
    }

	public boolean isQualified() {
	    return qualified;
    }

	public List<FinCreditReviewSummary> getLovDescCreditReviewSummaryEntries() {
    	return lovDescCreditReviewSummaryEntries;
    }

	public void setLovDescCreditReviewSummaryEntries(
            List<FinCreditReviewSummary> lovDescCreditReviewSummaryEntries) {
    	this.lovDescCreditReviewSummaryEntries = lovDescCreditReviewSummaryEntries;
    }

	public void setQualified(boolean qualified) {
	    this.qualified = qualified;
    }

	public String getLovDescMaxAuditYear() {
    	return lovDescMaxAuditYear;
    }
	public void setLovDescMaxAuditYear(String lovDescMaxAuditYear) {
    	this.lovDescMaxAuditYear = lovDescMaxAuditYear;
    }

	public String getLovDescMinAuditYear() {
    	return lovDescMinAuditYear;
    }
	public void setLovDescMinAuditYear(String lovDescMinAuditYear) {
    	this.lovDescMinAuditYear = lovDescMinAuditYear;
    }

	public String getDivision() {
	    return division;
    }

	public void setDivision(String division) {
	    this.division = division;
    }

	public void setConversionRate(String formatAmount) {
		// TODO Auto-generated method stub
		
	}

	public void setConsolidated(Object consolidated2) {
		// TODO Auto-generated method stub
		
	}

	public void setMapList(HashMap<String, BigDecimal> excelBSmap) {
		// TODO Auto-generated method stub
		
	}

}
