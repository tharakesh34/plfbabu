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
 * * FileName : RelationshipOfficerListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 12-09-2011 * * Modified Date : 12-09-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-09-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.relationshipofficer.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class RelationshipOfficerListModelItemRenderer implements ListitemRenderer<RelationshipOfficer>, Serializable {

	private static final long serialVersionUID = 6660192448428639124L;

	public RelationshipOfficerListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, RelationshipOfficer relationshipOfficer, int count) {
		Listcell lc;
		lc = new Listcell(relationshipOfficer.getROfficerCode());
		lc.setParent(item);
		lc = new Listcell(relationshipOfficer.getROfficerDesc());
		lc.setParent(item);
		lc = new Listcell(
				relationshipOfficer.getROfficerDeptCode() + "-" + relationshipOfficer.getLovDescROfficerDeptCodeName());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbROfficerIsActive = new Checkbox();
		cbROfficerIsActive.setDisabled(true);
		cbROfficerIsActive.setChecked(relationshipOfficer.isROfficerIsActive());
		lc.appendChild(cbROfficerIsActive);
		lc.setParent(item);
		lc = new Listcell(relationshipOfficer.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(relationshipOfficer.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", relationshipOfficer.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onRelationshipOfficerItemDoubleClicked");
	}
}