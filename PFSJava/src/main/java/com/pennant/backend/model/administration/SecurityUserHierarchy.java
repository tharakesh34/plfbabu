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
 * * FileName : SecurityUserHierarchy.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * *
 * Modified Date : 30-07-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 30-07-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.administration;

import java.util.ArrayList;
import java.util.List;

public class SecurityUserHierarchy extends ReportingManager {
	private static final long serialVersionUID = 1L;
	private int depth;
	private String userName;

	private List<SecurityUserHierarchy> userHierarchys = new ArrayList<>();

	public SecurityUserHierarchy() {
		super();
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public List<SecurityUserHierarchy> getUserHierarchys() {
		return userHierarchys;
	}

	public void setUserHierarchys(List<SecurityUserHierarchy> userHierarchys) {
		this.userHierarchys = userHierarchys;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
