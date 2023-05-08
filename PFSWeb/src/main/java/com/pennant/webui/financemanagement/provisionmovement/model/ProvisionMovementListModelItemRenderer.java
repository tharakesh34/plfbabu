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
 * * FileName : ProvisionMovementListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 31-05-2012 * * Modified Date : 31-05-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-05-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.financemanagement.provisionmovement.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.financemanagement.ProvisionMovement;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class ProvisionMovementListModelItemRenderer implements ListitemRenderer<ProvisionMovement>, Serializable {

	private static final long serialVersionUID = -4343497695244309847L;

	public ProvisionMovementListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, ProvisionMovement provisionMovement, int count) {

		Listcell lc;
		lc = new Listcell(DateUtil.formatToLongDate(provisionMovement.getProvMovementDate()));
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.formateInt(provisionMovement.getProvMovementSeq()));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(CurrencyUtil.format(provisionMovement.getNonFormulaProv(), 3));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbUseNFProv = new Checkbox();
		cbUseNFProv.setDisabled(true);
		cbUseNFProv.setChecked(provisionMovement.isUseNFProv());
		lc.appendChild(cbUseNFProv);
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbAutoReleaseNFP = new Checkbox();
		cbAutoReleaseNFP.setDisabled(true);
		cbAutoReleaseNFP.setChecked(provisionMovement.isAutoReleaseNFP());
		lc.appendChild(cbAutoReleaseNFP);
		lc.setParent(item);
		lc = new Listcell(CurrencyUtil.format(provisionMovement.getPrincipalDue(), 3));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(CurrencyUtil.format(provisionMovement.getProfitDue(), 3));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(provisionMovement.getDueFromDate()));
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(provisionMovement.getLastFullyPaidDate()));
		lc.setParent(item);
		item.setAttribute("data", provisionMovement);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onProvisionMovementItemDoubleClicked");
	}
}