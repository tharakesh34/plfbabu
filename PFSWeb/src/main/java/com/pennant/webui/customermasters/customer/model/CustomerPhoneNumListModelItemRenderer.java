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
 * FileName : CustomerPhoneNumListModelItemRenderer.java
 * 
 * Author : PENNANT TECHONOLOGIES
 * 
 * Creation Date : 11-08-2011
 * 
 * Modified Date : 11-08-2011
 * 
 * Description :
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 11-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.customermasters.customer.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 */
public class CustomerPhoneNumListModelItemRenderer implements ListitemRenderer<CustomerPhoneNumber>, Serializable {

	private static final long serialVersionUID = -5669186412320406064L;

	public CustomerPhoneNumListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, CustomerPhoneNumber customerPhoneNumber, int count) {
		Listcell lc;
		lc = new Listcell(StringUtils.trimToEmpty(customerPhoneNumber.getPhoneTypeCode()));
		lc.setParent(item);
		lc = new Listcell(StringUtils.trimToEmpty(customerPhoneNumber.getPhoneCountryCode()));
		lc.setParent(item);
		lc = new Listcell(customerPhoneNumber.getPhoneAreaCode());
		lc.setParent(item);
		lc = new Listcell(customerPhoneNumber.getPhoneNumber());
		lc.setParent(item);
		lc = new Listcell(customerPhoneNumber.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(customerPhoneNumber.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", customerPhoneNumber);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerPhoneNumberItemDoubleClicked");
	}
}
