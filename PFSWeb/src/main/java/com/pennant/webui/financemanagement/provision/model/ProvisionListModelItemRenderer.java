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
 * * FileName : ProvisionListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-05-2012 *
 * * Modified Date : 31-05-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-05-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.financemanagement.provision.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class ProvisionListModelItemRenderer implements ListitemRenderer<Provision>, Serializable {

	private static final long serialVersionUID = -4554647022945989420L;

	public ProvisionListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Provision provision, int count) {

		int format = CurrencyUtil.getFormat(provision.getFinCcy());

		Listcell lc;
		lc = new Listcell(provision.getFinReference());
		lc.setParent(item);
		lc = new Listcell(provision.getCustCIF());
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.formateDate(provision.getProvisionDate(), PennantConstants.dateFormat));
		lc.setParent(item);
		lc = new Listcell(CurrencyUtil.format(provision.getProvisionedAmt(), format));
		lc.setStyle("text-align:right");
		lc.setParent(item);
		lc = new Listcell(CurrencyUtil.format(provision.getProvisionedAmt(), format));
		lc.setStyle("text-align:right");
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbUseNFProv = new Checkbox();
		cbUseNFProv.setDisabled(true);
		// cbUseNFProv.setChecked(provision.isUseNFProv());
		lc.appendChild(cbUseNFProv);
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.formateDate(provision.getDueFromDate(), PennantConstants.dateFormat));
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.formateDate(provision.getLastFullyPaidDate(), PennantConstants.dateFormat));
		lc.setParent(item);
		lc = new Listcell(provision.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(provision.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", provision);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onProvisionItemDoubleClicked");
	}
}