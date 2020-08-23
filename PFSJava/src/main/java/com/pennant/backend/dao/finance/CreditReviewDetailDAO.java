package com.pennant.backend.dao.finance;

import com.pennant.backend.model.finance.ExtBreDetails;
import com.pennant.backend.model.finance.ExtCreditReviewConfig;
import com.pennant.backend.model.finance.CreditReviewData;
import com.pennant.backend.model.finance.CreditReviewDetails;
import com.pennanttech.pff.core.TableType;

public interface CreditReviewDetailDAO {

	public CreditReviewDetails getCreditReviewDetails(CreditReviewDetails creditReviewDetail);

	public CreditReviewData getCreditReviewData(String finReference, String templateName, int templateVersion);

	public void save(CreditReviewData creditReviewData);

	public void update(CreditReviewData creditReviewData);

	void delete(String finReference, TableType tableType);

	CreditReviewDetails getCreditReviewDetailsbyLoanType(CreditReviewDetails creditReviewDetail);

	public ExtCreditReviewConfig getExtCreditReviewConfigDetails(ExtCreditReviewConfig extCreditReviewDetail);

	public ExtBreDetails getExtBreDetailsByRef(String finReference);

}
