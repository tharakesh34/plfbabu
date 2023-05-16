/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 *********************************************************************************************
 * FILE HEADER *
 *********************************************************************************************
 *
 * FileName : PostingsDAO.java
 * 
 * Author : PENNANT TECHONOLOGIES
 * 
 * Creation Date : 07-02-2012
 * 
 * Modified Date : 07-02-2012
 * 
 * Description :
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 07-02-2012 PENNANT TECHONOLOGIES 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.rulefactory;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.rulefactory.ReturnDataSet;

public interface PostingsDAO {

	void saveBatch(List<ReturnDataSet> dataSetList);

	void updateStatusByLinkedTranId(long linkedTranId, String postStatus);

	void updateStatusByPostRef(String postRef, String postStatus);

	void updateStatusByFinRef(String postRef, String postStatus);

	long getLinkedTransId();

	long getPostingId();

	List<ReturnDataSet> getPostingsByPostRef(String postref);

	List<ReturnDataSet> getPostingsByLinkTransId(long linkedTranId);

	List<ReturnDataSet> getPostingsByPostRef(long postingId);

	List<ReturnDataSet> getPostingsByFinRefAndEvent(String reference, String finEvent, boolean showZeroBal,
			String postingGroupBy, String type);

	List<ReturnDataSet> getPostingsbyFinanceBranch(String branchCode);

	List<ReturnDataSet> getPostingsByVasref(String vasReference, String[] finEvent);

	List<ReturnDataSet> getPostingsByFinRef(String reference, boolean reqReversals);

	List<ReturnDataSet> getPostingsByTransIdList(List<Long> tranIdList);

	void updatePostCtg();

	List<ReturnDataSet> getPostings(String postRef, String finEvent);

	List<ReturnDataSet> getDisbursementPostings(long FinReference);

	List<ReturnDataSet> getInstDatePostings(String finReference, Date schdDate);

	List<Long> getAMZPostings(String finReference, Date postDate);

	List<ReturnDataSet> getPostingsByEnquiry(String reference, String finEvent, boolean showZeroBal,
			String postingGroupBy);

}
