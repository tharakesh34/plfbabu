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
 * * FileName : OverdueChargeRecovery.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 11-05-2012 * *
 * Modified Date : 11-05-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 11-05-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.financemanagement.overduechargerecovery.model;

import java.io.Serializable;
import java.util.Comparator;

import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Comparator class for OverdueChargeRecovery
 */
public class OverdueChargeRecoveryComparator implements Comparator<Object>, Serializable {

	private static final long serialVersionUID = 9112640872865877333L;

	public OverdueChargeRecoveryComparator() {
	    super();
	}

	@Override
	public int compare(Object o1, Object o2) {
		OverdueChargeRecovery data = (OverdueChargeRecovery) o1;
		OverdueChargeRecovery data2 = (OverdueChargeRecovery) o2;
		return DateUtil.compare(data.getFinODSchdDate(), data2.getFinODSchdDate());
	}

}
