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
 * * FileName : FinCreditRevSubCategoryListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 13-11-2013 * * Modified Date : 13-11-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 13-11-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.customermasters.fincreditrevsubcategory.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class FinCreditRevSubCategoryListModelItemRenderer
		implements ListitemRenderer<FinCreditRevSubCategory>, Serializable {

	private static final long serialVersionUID = 1L;

	public FinCreditRevSubCategoryListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, FinCreditRevSubCategory finCreditRevSubCategory, int count) {
		Listcell lc;
		lc = new Listcell(finCreditRevSubCategory.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(finCreditRevSubCategory.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", finCreditRevSubCategory);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onFinCreditRevSubCategoryItemDoubleClicked");

	}
}