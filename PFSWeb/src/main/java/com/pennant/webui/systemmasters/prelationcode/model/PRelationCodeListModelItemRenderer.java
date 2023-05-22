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
 * * FileName : PRelationCodeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 05-05-2011 * * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.prelationcode.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.PRelationCode;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class PRelationCodeListModelItemRenderer implements ListitemRenderer<PRelationCode>, Serializable {

	private static final long serialVersionUID = 4872966285218448333L;

	public PRelationCodeListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, PRelationCode pRelationCode, int count) {

		Listcell lc;
		lc = new Listcell(pRelationCode.getPRelationCode());
		lc.setParent(item);
		lc = new Listcell(pRelationCode.getPRelationDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbRelationCodeIsActive = new Checkbox();
		cbRelationCodeIsActive.setDisabled(true);
		cbRelationCodeIsActive.setChecked(pRelationCode.isRelationCodeIsActive());
		lc.appendChild(cbRelationCodeIsActive);
		lc.setParent(item);
		lc = new Listcell(pRelationCode.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(pRelationCode.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", pRelationCode.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onPRelationCodeItemDoubleClicked");
	}
}