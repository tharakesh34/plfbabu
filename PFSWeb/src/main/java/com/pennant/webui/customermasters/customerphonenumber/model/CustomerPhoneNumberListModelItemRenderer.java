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
 * * FileName : CustomerPhoneNumberListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 26-05-2011 * * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.customermasters.customerphonenumber.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CustomerPhoneNumberListModelItemRenderer implements ListitemRenderer<CustomerPhoneNumber>, Serializable {

	private static final long serialVersionUID = 2940801004411140146L;

	public CustomerPhoneNumberListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, CustomerPhoneNumber customerPhoneNumber, int count) {
		if (item instanceof Listgroup) {
			item.appendChild(new Listcell(customerPhoneNumber.getLovDescCustCIF()));
		} else if (item instanceof Listgroupfoot) {
			Listcell cell = new Listcell("");
			cell.setSpan(7);
			item.appendChild(cell);
		} else {

			Listcell lc;
			lc = new Listcell(String.valueOf(customerPhoneNumber.getPhoneCustID()));
			lc.setParent(item);
			lc = new Listcell(
					customerPhoneNumber.getPhoneTypeCode() + "-" + customerPhoneNumber.getLovDescPhoneTypeCodeName());
			lc.setParent(item);
			lc = new Listcell(customerPhoneNumber.getPhoneCountryCode());
			lc.setParent(item);
			lc = new Listcell(customerPhoneNumber.getPhoneAreaCode());
			lc.setParent(item);
			lc = new Listcell(customerPhoneNumber.getPhoneNumber());
			lc.setParent(item);
			lc = new Listcell(customerPhoneNumber.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(customerPhoneNumber.getRecordType()));
			lc.setParent(item);

			item.setAttribute("id", customerPhoneNumber.getPhoneCustID());
			item.setAttribute("phoneTypeCode", customerPhoneNumber.getPhoneTypeCode());

			ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerPhoneNumberItemDoubleClicked");
		}
	}
}