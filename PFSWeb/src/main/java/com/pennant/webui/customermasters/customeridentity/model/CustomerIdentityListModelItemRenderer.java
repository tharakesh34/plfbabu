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
 * * FileName : CustomerIdentityListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 26-05-2011 * * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.customermasters.customeridentity.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.customermasters.CustomerIdentity;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CustomerIdentityListModelItemRenderer implements ListitemRenderer<CustomerIdentity>, Serializable {

	private static final long serialVersionUID = 2886661211489397173L;

	public CustomerIdentityListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, CustomerIdentity customerIdentity, int count) {

		if (item instanceof Listgroup) {
			item.appendChild(new Listcell(String.valueOf(customerIdentity.getLovDescCustCIF())));
		} else if (item instanceof Listgroupfoot) {
			Listcell cell = new Listcell("");
			cell.setSpan(7);
			item.appendChild(cell);
		} else {

			Listcell lc;
			lc = new Listcell(customerIdentity.getLovDescCustCIF());
			lc.setParent(item);
			lc = new Listcell(customerIdentity.getIdType() + "-" + customerIdentity.getLovDescIdTypeName());
			lc.setParent(item);
			lc = new Listcell(customerIdentity.getIdIssuedBy());
			lc.setParent(item);
			lc = new Listcell(customerIdentity.getIdRef());
			lc.setParent(item);
			lc = new Listcell(
					customerIdentity.getIdIssueCountry() + "-" + customerIdentity.getLovDescIdIssueCountryName());
			lc.setParent(item);
			lc = new Listcell(customerIdentity.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(customerIdentity.getRecordType()));
			lc.setParent(item);

			item.setAttribute("idCustID", customerIdentity.getId());
			item.setAttribute("idType", customerIdentity.getIdType());

			ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerIdentityItemDoubleClicked");
		}
	}
}