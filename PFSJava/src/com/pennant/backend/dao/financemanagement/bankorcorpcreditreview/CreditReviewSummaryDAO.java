package com.pennant.backend.dao.financemanagement.bankorcorpcreditreview;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;

public interface CreditReviewSummaryDAO {
	public FinCreditReviewSummary getCreditReviewSummary();
	public FinCreditReviewSummary getNewCreditReviewSummary();
	public FinCreditReviewSummary getCreditReviewSummaryById(long summaryId,long detailId,String type);
	public void update(FinCreditReviewSummary creditReviewSummary,String type);
	public void delete(FinCreditReviewSummary creditReviewSummary,String type);
	public long save(FinCreditReviewSummary creditReviewSummary,String type);
	public List<FinCreditReviewSummary> getListCreditReviewSummaryById(final long id, String type,boolean postingProcess);
	public void initialize(FinCreditReviewSummary creditReviewSummary);
	public void refresh(FinCreditReviewSummary entity);
	public void deleteByDetailId(long detailId,String type);
	public List<FinCreditReviewSummary> getListCreditReviewSummaryByYearAndCustId(final long id, String year, String type);
	public List<FinCreditReviewSummary> getListCreditReviewSummaryByYearAndCustId2(long id, String year, String category, String type) ;
	public List<FinCreditReviewSummary> getListCreditReviewSummaryByYearAndCustId2(long id, String year, String category, int auditPeriod, boolean isCurrentYear, String type) ;
	public List<FinCreditReviewSummary> getLatestCreditReviewSummaryByYearAndCustId(long id);
    public BigDecimal getCcySpotRate(String ccyCode);
    public FinCreditReviewDetails getCreditReviewDetailsByYearAndCustId(long customerId, String year, String type);
}
