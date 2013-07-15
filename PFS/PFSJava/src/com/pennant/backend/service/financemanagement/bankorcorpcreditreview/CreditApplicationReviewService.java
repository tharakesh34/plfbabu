package com.pennant.backend.service.financemanagement.bankorcorpcreditreview;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditHeader;
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
	public Map<String,List<FinCreditReviewSummary>> getListCreditReviewSummaryByCustId(final long id,int noOfYears,int year);
	public int isCreditSummaryExists(long custID,String auditYear);
	public List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByCategoryIdAndCalcSeq(long categoryId);

}
