package com.pennant.backend.service.financemanagement.bankorcorpcreditreview;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevType;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;

public interface CreditApplicationReviewService {
	public List<FinCreditRevCategory> getCreditRevCategoryByCreditRevCode(String creditRevCode);
	public List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByCategoryId(long categoryId);
	FinCreditReviewDetails getCreditReviewDetails();
	FinCreditReviewDetails getNewCreditReviewDetails();
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	FinCreditReviewDetails getCreditReviewDetailsById(long id);
	FinCreditReviewDetails getApprovedCreditReviewDetailsById(long id);
	FinCreditReviewDetails refresh(FinCreditReviewDetails accountingSet);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	public List<FinCreditReviewSummary> getListCreditReviewSummaryById(final long id, String type,boolean postingProcess);
	public FinCreditRevType getFinCreditRevByRevCode(String creditRevCode);
	public Currency getCurrencyById(final String id);
	public Map<String,List<FinCreditReviewSummary>> getListCreditReviewSummaryByCustId(final long id,int noOfYears,int year, String type);
	public Map<String,List<FinCreditReviewSummary>> getListCreditReviewSummaryByCustId2(final long id,int noOfYears,int year, String category, String type);
	public Map<String,List<FinCreditReviewSummary>> getListCreditReviewSummaryByCustId2(final long id,int noOfYears,int year, String category, int auditPeriod, boolean isCurrentYear, String type);
	public int isCreditSummaryExists(long custID,String auditYear, int auditPeriod);
	public List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByCategoryIdAndCalcSeq(long categoryId);
	public List<FinCreditReviewSummary> getLatestCreditReviewSummaryByCustId(long id);
	public List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByCategoryId(long categoryId, String subCategoryItemType);
	public List<CustomerDocument> getCustomerDocumentsById(long custID, String type);
	public int getCreditReviewAuditPeriodByAuditYear(final long customerId, final String auditYear, int auditPeriod, boolean isEnquiry, String type);
    public BigDecimal getCcySpotRate(String ccyCode);
	public List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByMainCategory(String category);
	public FinCreditReviewDetails getCreditReviewDetailsByCustIdAndYear(long customerId, String auditYear, String type);
	public Map<String, FinCreditReviewDetails> getListCreditReviewDetailsByCustId(long id,
			int noOfYears,int year);
	public String getMaxAuditYearByCustomerId(long customerId, String type);
}
