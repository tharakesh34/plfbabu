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
 * * FileName : CourseListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 29-09-2011 * *
 * Modified Date : 29-09-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 29-09-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.amtmasters.course.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.amtmasters.Course;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class CourseListModelItemRenderer implements ListitemRenderer<Course>, Serializable {

	private static final long serialVersionUID = 1L;

	public CourseListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Course course, int count) {

		Listcell lc;
		lc = new Listcell(course.getCourseName());
		lc.setParent(item);
		lc = new Listcell(course.getCourseDesc());
		lc.setParent(item);
		lc = new Listcell(course.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(course.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", course.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onCourseItemDoubleClicked");
	}
}