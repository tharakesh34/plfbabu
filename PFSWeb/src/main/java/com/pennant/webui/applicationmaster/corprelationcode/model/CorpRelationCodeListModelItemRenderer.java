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
 * * FileName : CorpRelationCodeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 05-05-2011 * * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.corprelationcode.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.CorpRelationCode;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class CorpRelationCodeListModelItemRenderer implements ListitemRenderer<CorpRelationCode>, Serializable {

	private static final long serialVersionUID = -4992245602767405546L;

	public CorpRelationCodeListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, CorpRelationCode corpRelationCode, int count) {

		Listcell lc;
		lc = new Listcell(corpRelationCode.getCorpRelationCode());
		lc.setParent(item);
		lc = new Listcell(corpRelationCode.getCorpRelationDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbCorpRelationIsActive = new Checkbox();
		cbCorpRelationIsActive.setDisabled(true);
		cbCorpRelationIsActive.setChecked(corpRelationCode.isCorpRelationIsActive());
		lc.appendChild(cbCorpRelationIsActive);
		lc.setParent(item);
		lc = new Listcell(corpRelationCode.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(corpRelationCode.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", corpRelationCode.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onCorpRelationCodeItemDoubleClicked");
	}
}