/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  BlackListReasonCodeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.blacklistCheck;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.webui.systemmasters.blacklistCheck.model.BlackListCustomerItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/BlackListCheck/BlackListCheck.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class BlackListCheckListCtrl extends GFCBaseListCtrl<BlackListCustomers> implements Serializable {

	private static final long serialVersionUID = -4787094221203301336L;
	private final static Logger logger = Logger .getLogger(BlackListCheckListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_BlackListCheckDialog; 		// autoWired
	protected Borderlayout 	borderLayout_BlackListCheck; 		// autoWired
	protected Paging 		pagingBlackListCheck; 		// autoWired
	protected Listbox 		listBoxBlackListCheck; 		// autoWired

	// List headers
	protected Listheader listheader_CustCIF; 		// autoWired
	protected Listheader listheader_DOB; 		// autoWired
	protected Listheader listheader_CustFName; 	// autoWired
	protected Listheader listheader_CustLName; 	// autoWired
	protected Listheader listheader_EID;		// autoWired
	protected Listheader listheader_Passport; 		// autoWired
	protected Listheader listheader_Mobile; 		// autoWired
	protected Listheader listheader_Nationality; 	// autoWired
	protected Listheader listheader_Employer; 	// autoWired
	protected Listheader listheader_WatchListRule;
	protected Listheader listheader_Override;
	
	// checkRights
	protected Button btnHelp; 															// autoWired
	protected Button button_BlackListCheck_Clean; 			// autoWired
	protected Button button_BlackListCheck_BlackListed; 	// autoWired

	protected Textbox custCIF;						// autoWired
	protected Datebox dob;						// autoWired
	protected Textbox custFName;						// autoWired
	protected Textbox custLName;						// autoWired
	protected Textbox EIDNumber;						// autoWired
	protected Textbox passport;						// autoWired
	protected Textbox mobileNum;						// autoWired
	protected Textbox nationality;						// autoWired

	
	// NEEDED for the ReUse in the SearchWindow
	protected Grid searchGrid;
	private transient Object dialogCtrl = null;
	private BlackListCustomers blackListCustomers;
	protected Customer customer;
	protected List<BlackListCustomers> blackList = null;
	
	private boolean isOverride = false;
	

	/**
	 * default constructor.<br>
	 */
	public BlackListCheckListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected BlackListReasonCode
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_BlackListCheckDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if(args.containsKey("customer")) {
			setCustomer((Customer)args.get("customer"));
		}
		if(args.containsKey("blackList")) {
			blackList = (List<BlackListCustomers>) args.get("blackList");
		}
		if(blackList != null) {
			doWriteBeanToComponents(customer);
		} else {
			//FIXME
		}
		
		
		// +++++++++++++++++++++++ Stored search object and paging ++++++++++++++++++++++ //
		
		this.borderLayout_BlackListCheck.setHeight(calculateBorderLayoutHeight()-15+"px");
		this.listBoxBlackListCheck.setHeight(getListBoxHeight(6));
		this.pagingBlackListCheck.setPageSize(getListRows());
		this.pagingBlackListCheck.setDetailed(true);
		
		paging();

		showCustomerSeekDialog();
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$button_BlackListCheck_Clean(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		for (int i = 0; i < listBoxBlackListCheck.getItems().size(); i++) {
			Listitem listitem = listBoxBlackListCheck.getItems().get(i);
			for (int j = 0; j < listitem.getChildren().size(); j++) {
				Component component = listitem.getChildren().get(j).getFirstChild();
				if (component instanceof Checkbox) {
					if (((Checkbox) component).isChecked()) {
						isOverride = true;
					} else {
						isOverride = false;
					}
				}
			}

		}
		if (!isOverride) {
			PTMessageUtils.showErrorMessage(Labels.getLabel("label_IsBlackListedCustomer"));
		} else {
			blackList = null;
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onClick$button_BlackListCheck_BlackListed(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		for (int i = 0; i < listBoxBlackListCheck.getItems().size(); i++) {
			Listitem listitem = listBoxBlackListCheck.getItems().get(i);
			for (int j = 0; j < listitem.getChildren().size(); j++) {
				Component component = listitem.getChildren().get(j);
				if (component instanceof Checkbox) {
					if (((Checkbox) component).isChecked()) {
						isOverride = true;
					} else {
						isOverride = false;
					}
				}
			}
		}
		if (!isOverride) {
			PTMessageUtils.showErrorMessage(Labels.getLabel("label_IsBlackListedCustomer"));
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_BlackListCheckDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showCustomerSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_BlackListCheckDialog.doModal();
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Render the getting list and set the pagination
	 * 
	 * @param searchObj
	 */
	private void paging() {
		logger.debug("Entering");
		this.pagingBlackListCheck.setDetailed(true);
		this.listBoxBlackListCheck.setItemRenderer(new BlackListCustomerItemRenderer());
		getPagedListWrapper().initList(blackList, listBoxBlackListCheck, pagingBlackListCheck);
		logger.debug("Leaving");
	}

	private void doWriteBeanToComponents(Customer aCustomer) {
		this.custCIF.setValue(aCustomer.getCustCIF());
		this.dob.setValue(aCustomer.getCustDOB());
		this.custFName.setValue(aCustomer.getCustFName());
		this.custLName.setValue(aCustomer.getCustLName());
		this.EIDNumber.setValue(aCustomer.getCustCRCPR());
		this.passport.setValue(aCustomer.getCustPassportNo());
		this.mobileNum.setValue(aCustomer.getPhoneNumber());
		this.nationality.setValue(aCustomer.getCustNationality());
	}
	
	public BlackListCustomers getBlackListCustomers() {
		return blackListCustomers;
	}
	
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public void setBlackListCustomers(BlackListCustomers blackListCustomers) {
		this.blackListCustomers = blackListCustomers;
	}
	
	public Object getDialogCtrl() {
		return dialogCtrl;
	}
	public void setDialogCtrl(Object dialogCtrl) {
		this.dialogCtrl = dialogCtrl;
	}
}