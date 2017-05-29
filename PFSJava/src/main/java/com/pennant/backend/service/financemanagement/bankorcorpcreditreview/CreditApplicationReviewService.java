package com.pennant.backend.service.financemanagement.bankorcorpcreditreview;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevType;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;

public interface CreditApplicationReviewService {
	
	List<FinCreditRevCategory> getCreditRevCategoryByCreditRevCode(String creditRevCode);
	List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByCategoryId(long categoryId);
	FinCreditReviewDetails getNewCreditReviewDetails();
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	FinCreditReviewDetails getCreditReviewDetailsById(long id);
	FinCreditReviewDetails getApprovedCreditReviewDetailsById(long id);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	List<FinCreditReviewSummary> getListCreditReviewSummaryById(final long id, String type,boolean postingProcess);
	FinCreditRevType getFinCreditRevByRevCode(String creditRevCode);
	Map<String,List<FinCreditReviewSummary>> getListCreditReviewSummaryByCustId(final long id,int noOfYears,int year, String type);
	Map<String,List<FinCreditReviewSummary>> getListCreditReviewSummaryByCustId2(final long id,int noOfYears,int year, String category, String type);
	Map<String,List<FinCreditReviewSummary>> getListCreditReviewSummaryByCustId2(final long id,int noOfYears,int year, String category, int auditPeriod, boolean isCurrentYear, String type);
	int isCreditSummaryExists(long custID,String auditYear, int auditPeriod);
	List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByCategoryIdAndCalcSeq(long categoryId);
	List<FinCreditReviewSummary> getLatestCreditReviewSummaryByCustId(long id);
	List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByCategoryId(long categoryId, String subCategoryItemType);
	List<CustomerDocument> getCustomerDocumentsById(long custID, String type);
	int getCreditReviewAuditPeriodByAuditYear(final long customerId, final String auditYear, int auditPeriod, boolean isEnquiry, String type);
    BigDecimal getCcySpotRate(String ccyCode);
	List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByMainCategory(String category);
	FinCreditReviewDetails getCreditReviewDetailsByCustIdAndYear(long customerId, String auditYear, String type);
	Map<String, FinCreditReviewDetails> getListCreditReviewDetailsByCustId(long id,
			int noOfYears,int year);
	String getMaxAuditYearByCustomerId(long customerId, String type);
	List<FinCreditReviewDetails> getFinCreditRevDetailsByCustomerId(final long customerId, String type);
}
