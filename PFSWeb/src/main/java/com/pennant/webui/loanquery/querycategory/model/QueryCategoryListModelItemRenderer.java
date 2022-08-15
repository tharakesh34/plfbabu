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
 * * FileName : QueryCategoryListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 08-05-2018 * * Modified Date : 08-05-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 08-05-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.loanquery.querycategory.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.loanquery.QueryCategory;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class QueryCategoryListModelItemRenderer implements ListitemRenderer<QueryCategory>, Serializable {

	private static final long serialVersionUID = 1L;

	public QueryCategoryListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, QueryCategory queryCategory, int count) {

		Listcell lc;
		lc = new Listcell(queryCategory.getCode());
		lc.setParent(item);
		lc = new Listcell(queryCategory.getDescription());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbActive = new Checkbox();
		cbActive.setDisabled(true);
		cbActive.setChecked(queryCategory.isActive());
		lc.appendChild(cbActive);
		lc.setParent(item);
		lc = new Listcell(queryCategory.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(queryCategory.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", queryCategory.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onQueryCategoryItemDoubleClicked");
	}
}