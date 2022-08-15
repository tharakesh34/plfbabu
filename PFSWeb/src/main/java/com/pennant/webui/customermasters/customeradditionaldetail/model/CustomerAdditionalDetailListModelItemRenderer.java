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
 * * FileName : CustomerAdditionalDetailListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date
 * : 26-05-2011 * * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.customermasters.customeradditionaldetail.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.customermasters.CustomerAdditionalDetail;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CustomerAdditionalDetailListModelItemRenderer
		implements ListitemRenderer<CustomerAdditionalDetail>, Serializable {

	public CustomerAdditionalDetailListModelItemRenderer() {

	}

	private static final long serialVersionUID = -8502084612633186032L;

	@Override
	public void render(Listitem item, CustomerAdditionalDetail customerAdditionalDetail, int count) {

		Listcell lc;
		lc = new Listcell(customerAdditionalDetail.getLovDescCustCIF());
		lc.setParent(item);
		lc = new Listcell(customerAdditionalDetail.getCustAcademicLevel() + "-"
				+ customerAdditionalDetail.getLovDescCustAcademicLevelName());
		lc.setParent(item);
		lc = new Listcell(customerAdditionalDetail.getAcademicDecipline() + "-"
				+ customerAdditionalDetail.getLovDescAcademicDeciplineName());
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.formateLong(customerAdditionalDetail.getCustRefCustID()));
		lc.setParent(item);
		lc = new Listcell(customerAdditionalDetail.getCustRefStaffID());
		lc.setParent(item);
		lc = new Listcell(customerAdditionalDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(customerAdditionalDetail.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", customerAdditionalDetail.getId());
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerAdditionalDetailItemDoubleClicked");
	}
}