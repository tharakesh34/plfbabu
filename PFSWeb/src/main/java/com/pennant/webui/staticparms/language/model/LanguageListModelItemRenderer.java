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
 * * FileName : LanguageListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-06-2011 * *
 * Modified Date : 27-06-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-06-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.staticparms.language.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.staticparms.Language;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class LanguageListModelItemRenderer implements ListitemRenderer<Language>, Serializable {

	private static final long serialVersionUID = -5331036558999131544L;

	public LanguageListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Language language, int count) {

		Listcell lc;
		lc = new Listcell(language.getLngCode());
		lc.setParent(item);
		lc = new Listcell(language.getLngDesc());
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.formateInt(language.getLngNumber()));
		lc.setParent(item);
		lc = new Listcell(language.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(language.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", language.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onLanguageItemDoubleClicked");
	}
}