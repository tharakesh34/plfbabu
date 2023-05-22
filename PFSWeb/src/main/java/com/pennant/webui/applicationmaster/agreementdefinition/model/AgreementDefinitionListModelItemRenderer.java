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
 * * FileName : AgreementDefinitionListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 23-11-2011 * * Modified Date : 23-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.agreementdefinition.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class AgreementDefinitionListModelItemRenderer implements ListitemRenderer<AgreementDefinition>, Serializable {

	private static final long serialVersionUID = 1277410242979825193L;

	public AgreementDefinitionListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, AgreementDefinition agreementDefinition, int count) {

		Listcell lc;
		lc = new Listcell(agreementDefinition.getAggCode());
		lc.setParent(item);
		lc = new Listcell(agreementDefinition.getAggName());
		lc.setParent(item);
		lc = new Listcell(agreementDefinition.getAggDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbAggIsActive = new Checkbox();
		cbAggIsActive.setDisabled(true);
		cbAggIsActive.setChecked(agreementDefinition.isAggIsActive());
		lc.appendChild(cbAggIsActive);
		lc.setParent(item);
		lc = new Listcell(agreementDefinition.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(agreementDefinition.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", agreementDefinition.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onAgreementDefinitionItemDoubleClicked");
	}
}