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
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : DedupParm.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-08-2011 * * Modified Date :
 * 23-08-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.dedup;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>DedupParm table</b>.<br>
 *
 */
public class DedupParm extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -5474091857496782851L;

	private long queryId = Long.MIN_VALUE;
	private String queryCode;
	private String queryDesc;
	private String queryModule;
	private String querySubCode;
	private String sQLQuery;
	private String actualBlock;
	private String lovValue;
	private DedupParm befImage;
	private LoggedInUser userDetails;

	public DedupParm() {
		super();
	}

	public DedupParm(long id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return queryId;
	}

	public void setId(long id) {
		this.queryId = id;
	}

	public long getQueryId() {
		return queryId;
	}

	public void setQueryId(long queryId) {
		this.queryId = queryId;
	}

	public String getQueryCode() {
		return queryCode;
	}

	public void setQueryCode(String queryCode) {
		this.queryCode = queryCode;
	}

	public String getQueryDesc() {
		return queryDesc;
	}

	public void setQueryDesc(String queryDesc) {
		this.queryDesc = queryDesc;
	}

	public String getQueryModule() {
		return queryModule;
	}

	public void setQueryModule(String queryModule) {
		this.queryModule = queryModule;
	}

	public void setQuerySubCode(String querySubCode) {
		this.querySubCode = querySubCode;
	}

	public String getQuerySubCode() {
		return querySubCode;
	}

	public String getSQLQuery() {
		return sQLQuery;
	}

	public void setSQLQuery(String sQLQuery) {
		this.sQLQuery = sQLQuery;
	}

	public String getActualBlock() {
		return actualBlock;
	}

	public void setActualBlock(String actualBlock) {
		this.actualBlock = actualBlock;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public DedupParm getBefImage() {
		return this.befImage;
	}

	public void setBefImage(DedupParm beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
