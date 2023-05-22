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
 * * FileName : MailTemplateListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-10-2012
 * * * Modified Date : 04-10-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-10-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.mail.mailtemplate.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class MailTemplateListModelItemRenderer implements ListitemRenderer<MailTemplate>, Serializable {

	public MailTemplateListModelItemRenderer() {
	    super();
	}

	private static final long serialVersionUID = -1086618969673027251L;

	@Override
	public void render(Listitem item, MailTemplate mailTemplate, int count) {

		Listcell lc;
		Checkbox cb;
		lc = new Listcell(mailTemplate.getTemplateCode());
		lc.setParent(item);

		lc = new Listcell();
		cb = new Checkbox();
		cb.setDisabled(true);
		cb.setChecked(mailTemplate.isSmsTemplate());
		lc.appendChild(cb);
		lc.setParent(item);

		lc = new Listcell();
		cb = new Checkbox();
		cb.setDisabled(true);
		cb.setChecked(mailTemplate.isEmailTemplate());
		lc.appendChild(cb);
		lc.setParent(item);

		lc = new Listcell();
		cb = new Checkbox();
		cb.setDisabled(true);
		cb.setChecked(mailTemplate.isActive());
		lc.appendChild(cb);
		lc.setParent(item);

		lc = new Listcell(mailTemplate.getRecordStatus());
		lc.setParent(item);

		lc = new Listcell(PennantJavaUtil.getLabel(mailTemplate.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", mailTemplate.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onMailTemplateItemDoubleClicked");
	}
}