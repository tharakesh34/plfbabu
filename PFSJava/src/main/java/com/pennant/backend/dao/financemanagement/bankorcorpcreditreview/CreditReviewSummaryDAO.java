package com.pennant.backend.dao.financemanagement.bankorcorpcreditreview;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;

public interface CreditReviewSummaryDAO {
	
	FinCreditReviewSummary getCreditReviewSummary();
	FinCreditReviewSummary getNewCreditReviewSummary();
	FinCreditReviewSummary getCreditReviewSummaryById(long summaryId,long detailId,String type);
	void update(FinCreditReviewSummary creditReviewSummary,String type);
	void delete(FinCreditReviewSummary creditReviewSummary,String type);
	long save(FinCreditReviewSummary creditReviewSummary,String type);
	List<FinCreditReviewSummary> getListCreditReviewSummaryById(final long id, String type,boolean postingProcess);
	void deleteByDetailId(long detailId,String type);
	List<FinCreditReviewSummary> getListCreditReviewSummaryByYearAndCustId(final long id, String year, String type);
	List<FinCreditReviewSummary> getListCreditReviewSummaryByYearAndCustId2(long id, String year, String category, String type) ;
	List<FinCreditReviewSummary> getListCreditReviewSummaryByYearAndCustId2(long id, String year, String category, int auditPeriod, boolean isCurrentYear, String type) ;
	List<FinCreditReviewSummary> getLatestCreditReviewSummaryByYearAndCustId(long id);
    BigDecimal getCcySpotRate(String ccyCode);
    FinCreditReviewDetails getCreditReviewDetailsByYearAndCustId(long customerId, String year, String type);
}
