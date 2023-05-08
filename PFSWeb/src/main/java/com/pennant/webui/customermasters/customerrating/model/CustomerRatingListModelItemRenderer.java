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
 * * FileName : CustomerRatingListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 26-05-2011 * * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.customermasters.customerrating.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CustomerRatingListModelItemRenderer implements ListitemRenderer<CustomerRating>, Serializable {

	private static final long serialVersionUID = 1255070721055120232L;

	public CustomerRatingListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, CustomerRating customerRating, int count) {

		Listcell lc;
		lc = new Listcell(customerRating.getLovDescCustCIF());
		lc.setParent(item);
		lc = new Listcell(customerRating.getCustRatingType());
		lc.setParent(item);
		lc = new Listcell(customerRating.getCustRatingCode());
		lc.setParent(item);
		lc = new Listcell(customerRating.getCustRating());
		lc.setParent(item);
		lc = new Listcell(customerRating.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(customerRating.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", customerRating.getId());
		item.setAttribute("custRatingType", customerRating.getCustRatingType());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerRatingItemDoubleClicked");

	}
}