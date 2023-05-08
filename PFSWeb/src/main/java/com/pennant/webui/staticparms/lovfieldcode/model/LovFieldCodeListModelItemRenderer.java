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
 * * FileName : LovFieldCodeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-10-2011
 * * * Modified Date : 04-10-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-10-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.staticparms.lovfieldcode.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.staticparms.LovFieldCode;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class LovFieldCodeListModelItemRenderer implements ListitemRenderer<LovFieldCode>, Serializable {

	private static final long serialVersionUID = 1L;

	public LovFieldCodeListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, LovFieldCode lovFieldCode, int count) {

		Listcell lc;
		lc = new Listcell(lovFieldCode.getFieldCode());
		lc.setParent(item);
		lc = new Listcell(lovFieldCode.getFieldCodeDesc());
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.getLabelDesc(lovFieldCode.getFieldCodeType().trim(),
				PennantStaticListUtil.getLovFieldType()));
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbFieldEdit = new Checkbox();
		cbFieldEdit.setDisabled(true);
		cbFieldEdit.setChecked(lovFieldCode.isFieldEdit());
		lc.appendChild(cbFieldEdit);
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbisActive = new Checkbox();
		cbisActive.setDisabled(true);
		cbisActive.setChecked(lovFieldCode.isIsActive());
		lc.appendChild(cbisActive);
		lc.setParent(item);
		lc = new Listcell(lovFieldCode.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(lovFieldCode.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", lovFieldCode.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onLovFieldCodeItemDoubleClicked");
	}
}