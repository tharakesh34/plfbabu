/**
Copyright 2011 - Pennant Technologies
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
 * FileName    		:  FinTypeInsuranceListCtrl.java                                        * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-03-2017    														*
 *                                                                  						*
 * Modified Date    :  21-03-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-03-2017       Pennant	                 0.1                                            * 
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.FinTypeInsurances;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class FinTypeInsuranceListCtrl  extends GFCBaseCtrl<FinTypeInsurances> {
	
	private static final long serialVersionUID = 4521079241535245640L;

	private static final Logger logger = Logger.getLogger(FinTypeInsuranceListCtrl.class);

	protected Window window_FinTypeInsuranceList;
	
	private Component parent = null;
	//private Tab parentTab = null;
	
	protected Button						btnNew_insuranceType;
	private List<FinTypeInsurances>			finTypeInsuranceList	= new ArrayList<FinTypeInsurances>();
	private Listbox							listBoxInsuranceDetails;
	
	private String roleCode = "";
	private String finCcy = "";
	private String finType = "";
	private String finTypeDesc = "";
	protected boolean isOverdraft = false;
	protected int moduleId;
	private boolean isCompReadonly = false;
	
	
	private Object mainController;
	
	/**
	 * default constructor.<br>
	 */
	public FinTypeInsuranceListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinTypeInsuranceList";
	}
	
	@SuppressWarnings("unchecked")
	public void onCreate$window_FinTypeInsuranceList(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinTypeInsuranceList);

		try {
			if (event.getTarget().getParent() != null) {
				parent = event.getTarget().getParent();
			}

			// if (arguments.containsKey("parentTab")) {
			// parentTab = (Tab) arguments.get("parentTab");
			// }

			if (arguments.containsKey("roleCode")) {
				roleCode = (String) arguments.get("roleCode");
				getUserWorkspace().allocateRoleAuthorities(roleCode, super.pageRightName);
			}

			if (arguments.containsKey("finType")) {
				finType = (String) arguments.get("finType");
			}

			if (arguments.containsKey("moduleId")) {
				moduleId = (int) arguments.get("moduleId");
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

			if (arguments.containsKey("finTypeInsuranceList")) {
				this.finTypeInsuranceList = (List<FinTypeInsurances>) arguments.get("finTypeInsuranceList");
			}
			if (arguments.containsKey("isOverdraft")) {
				this.isOverdraft =  (Boolean)arguments.get("isOverdraft");
			}
			doEdit();
			doCheckRights();
			doSetFieldProperties();
			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
			window_FinTypeInsuranceList.onClose();
		}

		logger.debug("Leaving");
	}
	
	private void doEdit() {
		
	}

	private void doSetFieldProperties() {
		
	}

	private void doCheckRights() {
		logger.debug("Entering");
		
		//getUserWorkspace().allocateAuthorities(super.pageRightName, roleCode);
		this.btnNew_insuranceType.setVisible(!isCompReadonly);
		
		logger.debug("leaving");
	}

	private void doShowDialog() throws IllegalAccessException, IllegalArgumentException, NoSuchMethodException, SecurityException {
		logger.debug("Entering");
		
		doFillFinInsuranceTypes(this.finTypeInsuranceList);
		
		if (parent != null) {
			this.window_FinTypeInsuranceList.setHeight(borderLayoutHeight-75+"px");
			parent.appendChild(this.window_FinTypeInsuranceList);
		}
		
		try {
			getMainController().getClass().getMethod("setFinTypeInsuranceListCtrl", this.getClass()).invoke(mainController, this);
		} catch (InvocationTargetException e) {
			logger.error("Exception: ", e);
		}
		
		logger.debug("leaving");
	}
	
	public void doFillFinInsuranceTypes(List<FinTypeInsurances> finTypeInsuranceList) {
		logger.debug("Entering");
		
		try {
			if (finTypeInsuranceList != null) {
				setFinTypeInsuranceList(finTypeInsuranceList);
				fillFinTypeInsuranecs(finTypeInsuranceList);
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		
		logger.debug("Leaving");
	}
	
	private void fillFinTypeInsuranecs(List<FinTypeInsurances> finTypeInsuranceList) {
		this.listBoxInsuranceDetails.getItems().clear();
		
		List<ValueLabel>  insurancePaymentTypeList = PennantStaticListUtil.getInsurancePaymentType();
		List<ValueLabel>  insuranceCalTypeList = PennantStaticListUtil.getInsuranceCalType();
		
		for (FinTypeInsurances finTypeInsurance : finTypeInsuranceList) {
			Listitem item = new Listitem();
			Listcell lc;

			/*lc = new Listcell(finTypeInsurance.getPolicyType() + "-" + finTypeInsurance.getPolicyDesc());
			lc.setParent(item);*/

			lc = new Listcell(finTypeInsurance.getInsuranceType());
			lc.setParent(item);
			
			lc = new Listcell(finTypeInsurance.getInsuranceTypeDesc());
			lc.setParent(item);
			
			lc = new Listcell(PennantAppUtil.getlabelDesc(String.valueOf(finTypeInsurance.getDftPayType()), insurancePaymentTypeList));
			lc.setParent(item);

			lc = new Listcell(PennantAppUtil.getlabelDesc(String.valueOf(finTypeInsurance.getCalType()), insuranceCalTypeList));
			lc.setParent(item);

			lc = new Listcell();
			Checkbox checkbox = new Checkbox();
			checkbox.setChecked(finTypeInsurance.isMandatory());
			checkbox.setDisabled(true);
			checkbox.setParent(lc);
			lc.setParent(item);

			lc = new Listcell(finTypeInsurance.getRecordStatus());
			lc.setParent(item);

			lc = new Listcell(finTypeInsurance.getRecordType());
			lc.setParent(item);

			item.setAttribute("data", finTypeInsurance);
			
			ComponentsCtrl.applyForward(item, "onDoubleClick=onFinTypeInsuranceItemDoubleClicked");
			
			this.listBoxInsuranceDetails.appendChild(item);
		}
	}

	public void onClick$btnNew_insuranceType(Event event) throws InterruptedException {
		logger.debug("Entering");
		
		FinTypeInsurances finTypeInsurances = new FinTypeInsurances();
		finTypeInsurances.setNewRecord(true);
		finTypeInsurances.setFinType(this.finType);
		finTypeInsurances.setFinTypeDesc(this.finTypeDesc);
		finTypeInsurances.setWorkflowId(getWorkFlowId());
		finTypeInsurances.setModuleId(moduleId);

		doshowInsuranceDialog(finTypeInsurances);

		logger.debug("Leaving");
	}

	public void onFinTypeInsuranceItemDoubleClicked(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		
		Listitem item = (Listitem) event.getOrigin().getTarget();
		FinTypeInsurances finTypeInsurances = (FinTypeInsurances) item.getAttribute("data");
		
		if (!StringUtils.equals(finTypeInsurances.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
			finTypeInsurances.setNewRecord(false);
			finTypeInsurances.setFinTypeDesc(this.finTypeDesc);			
			doshowInsuranceDialog(finTypeInsurances);
		}
		
		logger.debug("Leaving" + event.toString());
	}
	
	private void doshowInsuranceDialog(FinTypeInsurances finTypeInsurances) throws InterruptedException {
		logger.debug("Entering");
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finTypeInsurances", finTypeInsurances);
		map.put("finTypeInsuranceListCtrl", this);
		map.put("role", roleCode);
		map.put("amountFormatter", CurrencyUtil.getFormat(this.finCcy));
		
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceType/FinTypeInsuranceDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		
		logger.debug("Leaving");
	}

	public List<FinTypeInsurances> getFinTypeInsuranceList() {
		return finTypeInsuranceList;
	}

	public void setFinTypeInsuranceList(List<FinTypeInsurances> finTypeInsuranceList) {
		this.finTypeInsuranceList = finTypeInsuranceList;
	}
	
	public Object getMainController() {
		return mainController;
	}

	public void setMainController(Object mainController) {
		this.mainController = mainController;
	}
}
