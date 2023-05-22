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
 * * FileName : LovFieldDetailListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 04-10-2011 * * Modified Date : 04-10-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-10-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.lovfielddetail.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class LovFieldDetailListModelItemRenderer implements ListitemRenderer<LovFieldDetail>, Serializable {

	private static final long serialVersionUID = -5660446477604159549L;

	public LovFieldDetailListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, LovFieldDetail lovFieldDetail, int count) {

		Listcell lc;
		lc = new Listcell(lovFieldDetail.getFieldCode() + "-" + lovFieldDetail.getLovDescFieldCodeName());
		lc.setParent(item);
		lc = new Listcell(lovFieldDetail.getFieldCodeValue());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbisActive = new Checkbox();
		cbisActive.setDisabled(true);
		cbisActive.setChecked(lovFieldDetail.isIsActive());
		lc.appendChild(cbisActive);
		lc.setParent(item);
		lc = new Listcell(lovFieldDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(lovFieldDetail.getRecordType()));
		lc.setParent(item);

		item.setAttribute("fieldCode", lovFieldDetail.getFieldCode());
		item.setAttribute("fieldCodeValue", lovFieldDetail.getFieldCodeValue());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onLovFieldDetailItemDoubleClicked");
	}
}