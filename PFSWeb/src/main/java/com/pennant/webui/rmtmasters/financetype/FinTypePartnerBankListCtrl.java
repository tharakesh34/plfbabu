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
 * FileName    		:  FinTypePartnerBankListCtrl.java                                      * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-04-2017    														*
 *                                                                  						*
 * Modified Date    :  24-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-04-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.financetype;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * This is the controller class for the /WEB-INF/pages/com.pennant.applicationmaster/FinTypePartnerBank/FinTypePartnerBankList.zul file.
 * 
 */
public class FinTypePartnerBankListCtrl extends GFCBaseCtrl<FinTypePartnerBank> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(FinTypePartnerBankListCtrl.class);

	protected Window window_FinTypePartnerBankList;
	
	private Component parent = null;
	protected Button button_FinTypePartnerBankList_NewFinTypePartnerBank;
	
	private List<FinTypePartnerBank>			finTypePartnerBankList	= new ArrayList<FinTypePartnerBank>();
	protected Listbox listBoxFinTypePartnerBank;
	
	private String roleCode = "";
	private String finCcy = "";
	private String finType = "";
	private String finTypeDesc = "";
	protected boolean isOverdraft = false;
	private boolean isCompReadonly = false;
	private String finDivision=null;
	
	
	private Object mainController;
	
	// Search Fields
	
	/**
	 * default constructor.<br>
	 */
	public FinTypePartnerBankListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinTypePartnerBankList";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_FinTypePartnerBankList(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinTypePartnerBankList);

		try {
			if (event.getTarget().getParent() != null) {
				parent = event.getTarget().getParent();
			}

			if (arguments.containsKey("roleCode")) {
				roleCode = (String) arguments.get("roleCode");
				getUserWorkspace().allocateRoleAuthorities(roleCode, super.pageRightName);
			}

			if (arguments.containsKey("finType")) {
				finType = (String) arguments.get("finType");
			}

			if (arguments.containsKey("finTypeDesc")) {
				finTypeDesc = (String) arguments.get("finTypeDesc");
			}

			if (arguments.containsKey("finCcy")) {
				finCcy = (String) arguments.get("finCcy");
			}

			if (arguments.containsKey("mainController")) {
				this.mainController = (Object) arguments.get("mainController");
			}

			if (arguments.containsKey("isCompReadonly")) {
				this.isCompReadonly = (boolean) arguments.get("isCompReadonly");
			}

			if (arguments.containsKey("finTypePartnerBankList")) {
				this.finTypePartnerBankList = (List<FinTypePartnerBank>) arguments.get("finTypePartnerBankList");
			}
			if (arguments.containsKey("isOverdraft")) {
				this.isOverdraft =  (Boolean)arguments.get("isOverdraft");
			}
			if (arguments.containsKey("finDivision")) {
				this.finDivision =  (String)arguments.get("finDivision");
			}
			doCheckRights();
			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
			window_FinTypePartnerBankList.onClose();
		}

		logger.debug("Leaving");
	}
	
	private void doCheckRights() {
		logger.debug("Entering");
		
		//getUserWorkspace().allocateAuthorities(super.pageRightName, roleCode);
		this.button_FinTypePartnerBankList_NewFinTypePartnerBank.setVisible(!isCompReadonly);
		
		logger.debug("leaving");
	}
	
	private void doShowDialog() throws IllegalAccessException, IllegalArgumentException, NoSuchMethodException, SecurityException {
		logger.debug("Entering");
		
		doFillFinTypePartnerBanks(this.finTypePartnerBankList);
		
		if (parent != null) {
			this.window_FinTypePartnerBankList.setHeight(this.borderLayoutHeight - 90 + "px");
			this.listBoxFinTypePartnerBank.setHeight(this.borderLayoutHeight - 125 + "px");
 			parent.appendChild(this.window_FinTypePartnerBankList);
		}
		
		try {
			getMainController().getClass().getMethod("setFinTypePartnerBankListCtrl", this.getClass()).invoke(mainController, this);
		} catch (InvocationTargetException e) {
			logger.error("Exception: ", e);
		}
		
		logger.debug("leaving");
	}
	
	public void doFillFinTypePartnerBanks(List<FinTypePartnerBank> finTypePartnerBankList) {
		logger.debug("Entering");
		
		try {
			if (finTypePartnerBankList != null) {
				setFinTypePartnerBankList(finTypePartnerBankList);
				fillFinTypePartnerBank(finTypePartnerBankList);
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		
		logger.debug("Leaving");
	}
	
	private void fillFinTypePartnerBank(List<FinTypePartnerBank> finTypePartnerBankList) {
		this.listBoxFinTypePartnerBank.getItems().clear();
		
		List<ValueLabel>  purposeList = PennantStaticListUtil.getPurposeList();
		List<ValueLabel>  paymentModesList = PennantStaticListUtil.getPaymentTypes(true);
		
		for (FinTypePartnerBank finTypePartnerBank : finTypePartnerBankList) {
			Listitem item = new Listitem();
			Listcell lc;
		  
			lc = new Listcell(PennantAppUtil.getlabelDesc(finTypePartnerBank.getPurpose(), purposeList));
			lc.setParent(item);
			
		  	
			lc = new Listcell(PennantAppUtil.getlabelDesc(finTypePartnerBank.getPaymentMode(), paymentModesList));
		  	lc.setParent(item);
		   
		  	lc = new Listcell(String.valueOf(finTypePartnerBank.getPartnerBankName()));
			lc.setParent(item);
		  	
			lc = new Listcell(finTypePartnerBank.getRecordStatus());
			lc.setParent(item);
			
			lc = new Listcell(finTypePartnerBank.getRecordType());
			lc.setParent(item);

			item.setAttribute("data", finTypePartnerBank);
			
			ComponentsCtrl.applyForward(item, "onDoubleClick=onFinTypePartnerBankItemDoubleClicked");
			
			
			this.listBoxFinTypePartnerBank.appendChild(item);
		}
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_FinTypePartnerBankList_NewFinTypePartnerBank(Event event) {
		logger.debug(Literal.ENTERING);
		
		FinTypePartnerBank fintypepartnerbank = new FinTypePartnerBank();
		fintypepartnerbank.setNewRecord(true);
		fintypepartnerbank.setFinType(this.finType);
		fintypepartnerbank.setFinTypeDesc(this.finTypeDesc);
		fintypepartnerbank.setWorkflowId(getWorkFlowId());
		
		// Display the dialog page.
		doShowDialogPage(fintypepartnerbank);

		logger.debug(Literal.LEAVING);
	}


	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onFinTypePartnerBankItemDoubleClicked(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		
		Listitem item = (Listitem) event.getOrigin().getTarget();
		FinTypePartnerBank finTypePartnerBank = (FinTypePartnerBank) item.getAttribute("data");
		
		if (!StringUtils.equals(finTypePartnerBank.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
			finTypePartnerBank.setNewRecord(false);
			finTypePartnerBank.setFinTypeDesc(this.finTypeDesc);			
			doShowDialogPage(finTypePartnerBank);
		}
		
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param fintypepartnerbank
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(FinTypePartnerBank fintypepartnerbank) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("fintypepartnerbank", fintypepartnerbank);
		map.put("fintypepartnerbankListCtrl", this);
		map.put("role", roleCode);
		map.put("amountFormatter", CurrencyUtil.getFormat(this.finCcy));
		
		if(StringUtils.isNotEmpty(finDivision)){
		map.put("finDivision",finDivision);
		}
			
		
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceType/FinTypePartnerBankDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public Object getMainController() {
		return mainController;
	}

	public void setMainController(Object mainController) {
		this.mainController = mainController;
	}

	public List<FinTypePartnerBank> getFinTypePartnerBankList() {
		return finTypePartnerBankList;
	}

	public void setFinTypePartnerBankList(
			List<FinTypePartnerBank> finTypePartnerBankList) {
		this.finTypePartnerBankList = finTypePartnerBankList;
	}
}