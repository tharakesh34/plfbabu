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
 * * FileName : PhoneTypeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-05-2011 *
 * * Modified Date : 06-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.phonetype.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.PhoneType;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class PhoneTypeListModelItemRenderer implements ListitemRenderer<PhoneType>, Serializable {

	private static final long serialVersionUID = 478211645293947995L;

	public PhoneTypeListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, PhoneType phoneType, int count) {

		Listcell lc;
		lc = new Listcell(phoneType.getPhoneTypeCode());
		lc.setParent(item);
		lc = new Listcell(phoneType.getPhoneTypeDesc());
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.formateInt(phoneType.getPhoneTypePriority()));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbPhoneTypeIsActive = new Checkbox();
		cbPhoneTypeIsActive.setDisabled(true);
		cbPhoneTypeIsActive.setChecked(phoneType.isPhoneTypeIsActive());
		lc.appendChild(cbPhoneTypeIsActive);
		lc.setParent(item);
		lc = new Listcell(phoneType.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(phoneType.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", phoneType.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onPhoneTypeItemDoubleClicked");
	}
}