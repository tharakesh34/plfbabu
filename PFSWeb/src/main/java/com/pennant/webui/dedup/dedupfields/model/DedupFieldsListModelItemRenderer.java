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
 * * FileName : DedupFieldsListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-08-2011
 * * * Modified Date : 23-08-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.dedup.dedupfields.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.dedup.DedupFields;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class DedupFieldsListModelItemRenderer implements ListitemRenderer<DedupFields>, Serializable {

	private static final long serialVersionUID = 1L;

	public DedupFieldsListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, DedupFields dedupFields, int count) {

		Listcell lc;
		lc = new Listcell(dedupFields.getFieldName());
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.getLabelDesc(dedupFields.getFieldControl(),
				PennantStaticListUtil.getFieldTypeList()));
		lc.setParent(item);
		lc = new Listcell(dedupFields.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(dedupFields.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", dedupFields);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onDedupFieldsItemDoubleClicked");
	}
}