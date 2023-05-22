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
 * * FileName : PresentmentHeaderListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 01-05-2017 * * Modified Date : 01-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 01-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.financemanagement.presentmentheader.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.pff.mandate.MandateUtil;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class PresentmentHeaderListModelItemRenderer implements ListitemRenderer<PresentmentHeader>, Serializable {

	private static final long serialVersionUID = 1L;

	public PresentmentHeaderListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, PresentmentHeader ph, int count) {

		Listcell lc;
		lc = new Listcell(ph.getReference());
		lc.setParent(item);

		lc = new Listcell(PennantStaticListUtil.getlabelDesc(ph.getPresentmentType(),
				PennantStaticListUtil.getPresetmentTypeList()));
		lc.setParent(item);

		lc = new Listcell(ph.getEntityCode());
		lc.setParent(item);

		lc = new Listcell(DateUtil.formatToLongDate(ph.getSchdate()));
		lc.setParent(item);

		if (StringUtils.trimToNull(ph.getBankCode()) == null) {
			lc = new Listcell();
		} else {
			lc = new Listcell(ph.getBankCode() + "-" + ph.getBankName());
		}
		lc.setParent(item);

		lc = new Listcell(ph.getPartnerBankName());
		lc.setParent(item);

		lc = new Listcell(PennantStaticListUtil.getPropertyValue(PennantStaticListUtil.getPresentmentBatchStatusList(),
				ph.getStatus()));

		lc.setParent(item);

		lc = new Listcell(
				PennantStaticListUtil.getlabelDesc(ph.getMandateType(), MandateUtil.getInstrumentTypesForBE()));
		lc.setParent(item);

		lc = new Listcell(DateUtil.formatToLongDate(ph.getPresentmentDate()));
		lc.setParent(item);

		lc = new Listcell(ph.getRecordStatus());
		lc.setParent(item);

		lc = new Listcell(PennantJavaUtil.getLabel(ph.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", ph.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onPresentmentHeaderItemDoubleClicked");
	}
}