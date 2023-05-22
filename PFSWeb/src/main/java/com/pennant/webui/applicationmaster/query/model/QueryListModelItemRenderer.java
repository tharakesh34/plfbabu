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
 * * FileName : QueryListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-07-2013 * *
 * Modified Date : 04-07-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-07-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.query.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.Query;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class QueryListModelItemRenderer implements ListitemRenderer<Query>, Serializable {

	private static final long serialVersionUID = 1L;

	public QueryListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Query query, int count) {

		Listcell lc;
		lc = new Listcell(query.getQueryCode());
		lc.setParent(item);
		lc = new Listcell(query.getQueryModule());
		lc.setParent(item);
		lc = new Listcell(query.getQueryDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbSubQuery = new Checkbox();
		cbSubQuery.setDisabled(true);
		cbSubQuery.setChecked(query.isSubQuery());
		lc.appendChild(cbSubQuery);
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbActive = new Checkbox();
		cbActive.setDisabled(true);
		cbActive.setChecked(query.isActive());
		lc.appendChild(cbActive);
		lc.setParent(item);
		lc = new Listcell(query.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(query.getRecordType()));
		lc.setParent(item);

		item.setAttribute("queryCode", query.getQueryCode());
		item.setAttribute("queryModule", query.getQueryModule());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onQueryItemDoubleClicked");
	}
}