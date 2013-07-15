package com.pennant.backend.dao.financemanagement.bankorcorpcreditreview;

import java.util.List;

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
	public List<FinCreditReviewSummary> getListCreditReviewSummaryByYearAndCustId(final long id, String year);
}
