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
 * * FileName : CurrencyListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * *
 * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.reports.reportconfiguration.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.reports.ReportConfiguration;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class ReportConfigurationListModelItemRenderer implements ListitemRenderer<ReportConfiguration>, Serializable {

	private static final long serialVersionUID = 9199981912283581234L;

	public ReportConfigurationListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, ReportConfiguration reportConfiguration, int count) {
		Listcell lc;
		lc = new Listcell(reportConfiguration.getReportName());
		lc.setParent(item);
		lc = new Listcell(reportConfiguration.getReportHeading());
		lc.setParent(item);
		lc = new Listcell(reportConfiguration.getReportJasperName());
		lc.setParent(item);
		lc = new Listcell(reportConfiguration.getMenuItemCode());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbPromptRequired = new Checkbox();
		cbPromptRequired.setDisabled(true);
		cbPromptRequired.setChecked(reportConfiguration.isPromptRequired());
		lc.appendChild(cbPromptRequired);
		lc.setParent(item);
		item.setAttribute("id", reportConfiguration.getId());
		ComponentsCtrl.applyForward(item, "onDoubleClick=onReportConfigurationItemDoubleClicked");
	}
}