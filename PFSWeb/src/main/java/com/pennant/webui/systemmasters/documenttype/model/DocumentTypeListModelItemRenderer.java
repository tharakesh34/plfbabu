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
 * * FileName : DocumentTypeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011
 * * * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.documenttype.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class DocumentTypeListModelItemRenderer implements ListitemRenderer<DocumentType>, Serializable {

	private static final long serialVersionUID = -2962501113956666838L;

	public DocumentTypeListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, DocumentType documentType, int count) {

		Listcell lc;
		lc = new Listcell(documentType.getDocTypeCode());
		lc.setParent(item);
		lc = new Listcell(documentType.getDocTypeDesc());
		lc.setParent(item);
		lc = new Listcell(documentType.getCategoryCode());
		lc.setParent(item);
		lc = new Listcell();
		/*
		 * final Checkbox cbDocIsMandatory = new Checkbox(); cbDocIsMandatory.setDisabled(true);
		 * cbDocIsMandatory.setChecked(documentType.isDocIsMandatory()); lc.appendChild(cbDocIsMandatory);
		 */
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbDocTypeIsActive = new Checkbox();
		cbDocTypeIsActive.setDisabled(true);
		cbDocTypeIsActive.setChecked(documentType.isDocTypeIsActive());
		lc.appendChild(cbDocTypeIsActive);
		lc.setParent(item);
		lc = new Listcell(documentType.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(documentType.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", documentType.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onDocumentTypeItemDoubleClicked");
	}
}