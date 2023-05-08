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
 * * FileName : CustomerPRelationListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 26-05-2011 * * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.customermasters.customerprelation.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.customermasters.CustomerPRelation;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CustomerPRelationListModelItemRenderer implements ListitemRenderer<CustomerPRelation>, Serializable {

	private static final long serialVersionUID = -4384335745555359611L;

	public CustomerPRelationListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, CustomerPRelation customerPRelation, int count) {

		if (item instanceof Listgroup) {
			item.appendChild(new Listcell(String.valueOf(customerPRelation.getPRCustID())));
		} else if (item instanceof Listgroupfoot) {
			Listcell cell = new Listcell("");
			cell.setSpan(8);
			item.appendChild(cell);
		} else {

			Listcell lc;
			lc = new Listcell(String.valueOf(customerPRelation.getPRCustID()));
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.formateInt(customerPRelation.getPRCustPRSNo()));
			lc.setParent(item);
			lc = new Listcell(
					customerPRelation.getPRRelationCode() + "-" + customerPRelation.getLovDescPRRelationCodeName());
			lc.setParent(item);
			lc = new Listcell(customerPRelation.getPRRelationCustID());
			lc.setParent(item);
			lc = new Listcell();
			final Checkbox cbPRisGuardian = new Checkbox();
			cbPRisGuardian.setDisabled(true);
			cbPRisGuardian.setChecked(customerPRelation.isPRisGuardian());
			lc.appendChild(cbPRisGuardian);
			lc.setParent(item);
			lc = new Listcell(customerPRelation.getPRSName());
			lc.setParent(item);
			lc = new Listcell(customerPRelation.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(customerPRelation.getRecordType()));
			lc.setParent(item);

			item.setAttribute("id", customerPRelation.getPRCustID());
			item.setAttribute("PRCustPRSNo", customerPRelation.getPRCustPRSNo());

			ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerPRelationItemDoubleClicked");
		}
	}
}