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
 * * FileName : MandateListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 18-10-2016 * * Modified Date
 * : 18-10-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-10-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.mandate.mandate;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.backend.model.mandate.MandateStatus;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/Mandate/MandateList.zul file.<br>
 * ************************************************************<br>
 * 
 */
public class MandateStatusListCtrl extends GFCBaseCtrl<MandateStatus> implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(MandateStatusListCtrl.class);

	protected Window window_MandateStatusList;
	protected Borderlayout borderLayout_MandateStatusList;
	protected Listbox listBoxMandateStatus;

	protected Listheader listheader_MandateId;
	protected Listheader listheader_Status;
	protected Listheader listheader_Reason;
	protected Listheader listheader_ChangeDate;
	private long mandateId;

	/**
	 * default constructor.<br>
	 */
	public MandateStatusListCtrl() {
		super();
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_MandateStatusList(Event event) {
		// Set the page level components.
		if (arguments.containsKey("mandateId")) {
			mandateId = (Long) arguments.get("mandateId");
		}
		JdbcSearchObject<MandateStatus> jdbcSearchObject = new JdbcSearchObject<MandateStatus>(MandateStatus.class);
		jdbcSearchObject.addTabelName("MandatesStatus");
		jdbcSearchObject.addFilterEqual("mandateId", mandateId);
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		List<MandateStatus> list = pagedListService.getBySearchObject(jdbcSearchObject);
		list.sort(new Comparator<MandateStatus>() {
			@Override
			public int compare(MandateStatus obj1, MandateStatus obj2) {

				return obj1.getChangeDate().compareTo(obj2.getChangeDate());
			}
		});
		doFillListbox(list);
		this.window_MandateStatusList.doModal();

	}

	private void doFillListbox(List<MandateStatus> list) {
		this.listBoxMandateStatus.getItems().clear();
		if (list == null || list.isEmpty()) {
			return;
		}
		for (MandateStatus mandateStatus : list) {
			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell(mandateStatus.getStatus());
			lc.setParent(item);
			lc = new Listcell(mandateStatus.getReason());
			lc.setParent(item);
			lc = new Listcell(DateUtil.formatToLongDate(mandateStatus.getChangeDate()));
			lc.setParent(item);
			this.listBoxMandateStatus.appendChild(item);

		}

	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws ParseException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException, ParseException {
		logger.debug("Entering" + event.toString());
		this.window_MandateStatusList.onClose();
		logger.debug("Leaving" + event.toString());
	}

}