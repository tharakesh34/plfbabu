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
 * * FileName : QuestionListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-11-2011 * *
 * Modified Date : 21-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.bmtmasters.question.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.bmtmasters.Question;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class QuestionListModelItemRenderer implements ListitemRenderer<Question>, Serializable {

	private static final long serialVersionUID = 1L;

	public QuestionListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Question question, int count) {

		Listcell lc;
		lc = new Listcell(question.getQuestionDesc());
		lc.setParent(item);
		lc = new Listcell(question.getAnswerA());
		lc.setParent(item);
		lc = new Listcell(question.getAnswerB());
		lc.setParent(item);
		lc = new Listcell(question.getAnswerC());
		lc.setParent(item);
		lc = new Listcell(question.getAnswerD());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbQuestionIsActive = new Checkbox();
		cbQuestionIsActive.setDisabled(true);
		cbQuestionIsActive.setChecked(question.isQuestionIsActive());
		lc.appendChild(cbQuestionIsActive);
		lc.setParent(item);
		lc = new Listcell(question.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(question.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", question);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onQuestionItemDoubleClicked");
	}
}