package com.pennant.backend.dao.financemanagement.bankorcorpcreditreview;

import java.util.List;
import java.util.Set;

import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevType;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;


public interface CreditApplicationReviewDAO {
	
	List<FinCreditRevCategory> getCreditRevCategoryByCreditRevCode(String creditRevCode);
	List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByCategoryId(long categoryId);
	FinCreditReviewDetails getCreditReviewDetails();
	FinCreditReviewDetails getNewCreditReviewDetails();
	FinCreditReviewDetails getCreditReviewDetailsById(long id,String type);
	void update(FinCreditReviewDetails creditReviewDetails,String type);
	void delete(FinCreditReviewDetails creditReviewDetails,String type);
	long save(FinCreditReviewDetails creditReviewDetails,String type);
	FinCreditRevType getFinCreditRevByRevCode(String creditRevCode);
	int isCreditSummaryExists(long custID,String auditYear, int auditPeriod);
	List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByCategoryIdAndCalcSeq(long categoryId);
	List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByCategoryId(long categoryId, String subCategoryItemType);
	int getCreditReviewAuditPeriodByAuditYear(final long customerId, final String auditYear, int auditPeriod, 
			boolean isEnquiry, String type);
	List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByMainCategory(String category);
	FinCreditReviewDetails getCreditReviewDetailsByCustIdAndYear(final long customerId, String auditYear, String type);
	String getMaxAuditYearByCustomerId(long customerId, String type);
	List<FinCreditReviewDetails> getFinCreditRevDetailsByCustomerId(final long customerId, String type);
	List<FinCreditReviewDetails> getAuditYearsByCustId(Set<Long> custId);
}
