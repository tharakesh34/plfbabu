package com.pennant.backend.dao.finance;

import com.pennant.backend.model.finance.CreditReviewData;
import com.pennant.backend.model.finance.CreditReviewDetails;
import com.pennant.backend.model.finance.ExtBreDetails;
import com.pennant.backend.model.finance.ExtCreditReviewConfig;
import com.pennanttech.pff.core.TableType;

public interface CreditReviewDetailDAO {

	public CreditReviewDetails getCreditReviewDetails(CreditReviewDetails creditReviewDetail);

	public CreditReviewData getCreditReviewData(long finID, String templateName);

	public void save(CreditReviewData creditReviewData);

	public void update(CreditReviewData creditReviewData);

	void delete(long finID, TableType tableType);

	CreditReviewDetails getCreditReviewDetailsbyLoanType(CreditReviewDetails creditReviewDetail);

	public ExtCreditReviewConfig getExtCreditReviewConfigDetails(ExtCreditReviewConfig extCreditReviewDetail);

	public ExtBreDetails getExtBreDetailsByRef(long finID);

}
