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
 * * FileName : CustomerEmploymentDetailListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date
 * : 26-05-2011 * * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.customermasters.customeremploymentdetail.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CustomerEmploymentDetailListModelItemRenderer
		implements ListitemRenderer<CustomerEmploymentDetail>, Serializable {

	private static final long serialVersionUID = -6978886004363491169L;

	public CustomerEmploymentDetailListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, CustomerEmploymentDetail customerEmploymentDetail, int count) {

		Listcell lc;
		lc = new Listcell(customerEmploymentDetail.getLovDescCustCIF());
		lc.setParent(item);
		lc = new Listcell(customerEmploymentDetail.getLovDesccustEmpName());
		lc.setParent(item);
		lc = new Listcell(customerEmploymentDetail.getLovDescCustEmpDesgName());
		lc.setParent(item);
		lc = new Listcell(customerEmploymentDetail.getLovDescCustEmpDeptName());
		lc.setParent(item);
		lc = new Listcell(customerEmploymentDetail.getLovDescCustEmpTypeName());
		lc.setParent(item);
		lc = new Listcell(customerEmploymentDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(customerEmploymentDetail.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", customerEmploymentDetail.getCustID());
		item.setAttribute("empName", customerEmploymentDetail.getCustEmpName());

		item.setAttribute("custEmpId", customerEmploymentDetail.getCustEmpId());
		item.setAttribute("data", customerEmploymentDetail);

		ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerEmploymentDetailItemDoubleClicked");
	}
}