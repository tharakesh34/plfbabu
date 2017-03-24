package com.pennant.backend.dao.payorderissue;

import com.pennant.backend.model.payorderissue.PayOrderIssueHeader;



public interface PayOrderIssueHeaderDAO {
	
	PayOrderIssueHeader getPayOrderIssueByHeaderRef(String finReference, String type);
	void update(PayOrderIssueHeader payOrderIssueHeader, String type);
	void delete(PayOrderIssueHeader payOrderIssueHeader, String type);
	void save(PayOrderIssueHeader payOrderIssueHeader, String type);
}
