package com.pennant.backend.dao.financemanagement.bankorcorpcreditreview;

import java.util.List;

import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevType;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;


public interface CreditApplicationReviewDAO {
	public List<FinCreditRevCategory> getCreditRevCategoryByCreditRevCode(String creditRevCode);
	public List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByCategoryId(long categoryId);
	public FinCreditReviewDetails getCreditReviewDetails();
	public FinCreditReviewDetails getNewCreditReviewDetails();
	public FinCreditReviewDetails getCreditReviewDetailsById(long id,String type);
	public void update(FinCreditReviewDetails creditReviewDetails,String type);
	public void delete(FinCreditReviewDetails creditReviewDetails,String type);
	public long save(FinCreditReviewDetails creditReviewDetails,String type);
	public void initialize(FinCreditReviewDetails creditReviewDetails);
	public void refresh(FinCreditReviewDetails entity);
	public FinCreditRevType getFinCreditRevByRevCode(String creditRevCode);
	public int isCreditSummaryExists(long custID,String auditYear, int auditPeriod);
	public List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByCategoryIdAndCalcSeq(long categoryId);
	public List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByCategoryId(long categoryId, String subCategoryItemType);
	public int getCreditReviewAuditPeriodByAuditYear(final long customerId, final String auditYear, int auditPeriod, 
			boolean isEnquiry, String type);
	public List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByMainCategory(String category);
	public FinCreditReviewDetails getCreditReviewDetailsByCustIdAndYear(final long customerId, String auditYear, String type);
	public String getMaxAuditYearByCustomerId(long customerId, String type);
}
