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
 * * FileName : NotificationsListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 23-05-2011 * * Modified Date : 23-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.mail.notifications.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.rulefactory.Notifications;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;

/**
 * Item renderer for listItems in the listBox.
 */
public class NotificationsListModelItemRenderer implements ListitemRenderer<Notifications>, Serializable {

	private static final long serialVersionUID = 3736186724610414895L;

	public NotificationsListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Notifications notifications, int count) {

		Listcell lc;
		lc = new Listcell(notifications.getRuleCode());
		lc.setParent(item);
		lc = new Listcell(notifications.getRuleCodeDesc());
		lc.setParent(item);
		lc = new Listcell(PennantStaticListUtil.getlabelDesc(StringUtils.trimToEmpty(notifications.getRuleModule()),
				PennantStaticListUtil.getMailModulesList()));
		lc.setParent(item);
		lc = new Listcell(notifications.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(notifications.getRecordType()));
		lc.setParent(item);

		item.setAttribute("ruleCode", notifications.getRuleCode());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onNotificationsItemDoubleClicked");
	}
}