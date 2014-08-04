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
 * FileName    		:  FeeTierDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-09-2011    														*
 *                                                                  						*
 * Modified Date    :  16-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rulefactory.rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.codemirror.Codemirror;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.rulefactory.BMTRBFldDetails;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Fee/RuleResult/feeTierDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class RuleResultDialogCtrl extends GFCBaseListCtrl<Rule> implements Serializable {

	private static final long serialVersionUID = -2393925908398735705L;
	private final static Logger logger = Logger.getLogger(RuleResultDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_RuleResultDialog; 	// autoWired
	public Codemirror 	formula; 					// autoWired

	protected Listbox listboxAmountCodes;			// autoWired
	protected Listbox listboxFeeOperators;			// autoWired
	protected Tabbox tabbox_AmtCodes;				// autoWired
	protected Tabbox tabbox_optValues;				// autoWired
	Textbox textbox ;
	
	protected Column amtCodeColumn;				// autoWired
	protected Column coremirrorColumn;			// autoWired
	protected Column optValuesColumn;			// autoWired

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialize.
	private transient String 		oldVar_formula;

	// Button controller for the CRUD buttons
	protected Button btnSave; 			// autoWired
	protected Button btnCancel; 		// autoWired
	protected Button btnClose; 			// autoWired
	protected Button btnHelp; 			// autoWired

	// ServiceDAOs / Domain Classes
	private transient PagedListService pagedListService;
	private List<BMTRBFldDetails> ruleCodesList;
	private List<String> amountCodes = new ArrayList<String>();
	String returnValue = "";
	
	private List<ValueLabel> listRuleOperators = PennantStaticListUtil.getRuleOperator(); // autoWired
	protected JdbcSearchObject<BMTRBFldDetails> searchObj;

	/**
	 * default constructor.<br>
	 */
	public RuleResultDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected RuleResult object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_RuleResultDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		this.searchObj = new JdbcSearchObject<BMTRBFldDetails>(BMTRBFldDetails.class);
		this.searchObj.addSort("rbModule", false);
		
		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		this.searchObj.addFilterEqual("rbForCalFlds",1);
		
		if(args.containsKey("returnValue")){
			returnValue =  (String) args.get("returnValue");
		}
		if(args.containsKey("textbox")){
			textbox =  (Textbox) args.get("textbox");
		}
		if(args.containsKey("rbModule")){
			this.searchObj.addFilterEqual("rbModule",(String) args.get("rbModule"));
		}
		if(args.containsKey("rbEvent")){
			this.searchObj.addFilterEqual("rbEvent",(String) args.get("rbEvent"));
		}

		// READ OVERHANDED parameters !
		// we get the feeTierListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete RuleResult here.

		getBorderLayoutHeight();
		int groupboxHeight = borderLayoutHeight-150;
		tabbox_AmtCodes.setHeight(groupboxHeight+"px");
		tabbox_optValues.setHeight(groupboxHeight+"px");
		listboxAmountCodes.setHeight((groupboxHeight-35)+"px");
		listboxFeeOperators.setHeight((groupboxHeight-35)+"px");
		formula.setHeight(groupboxHeight+"px");

		// ++ create the searchObject and initialize sorting ++//
		this.searchObj.addTabelName("BMTRBFldDetails");

		Listitem item;
		Listcell cell;

		ruleCodesList = this.pagedListService.getBySearchObject(searchObj);

		if (ruleCodesList != null) {
			for (int i = 0; i < ruleCodesList.size(); i++) {
				item = new Listitem();
				cell = new Listcell(ruleCodesList.get(i).getRbFldName());
				amountCodes.add(ruleCodesList.get(i).getRbFldName());
				cell.setStyle("text-align:left;");
				cell.setParent(item);
				cell = new Listcell(ruleCodesList.get(i).getRbFldDesc());
				cell.setStyle("text-align:left;");
				cell.setParent(item);
				item.setParent(listboxAmountCodes);
			}
		}

		if (listRuleOperators != null) {

			for (int i = 0; i < listRuleOperators.size(); i++) {
				item = new Listitem();
				cell = new Listcell(listRuleOperators.get(i).getValue());
				cell.setStyle("text-align:left;");
				cell.setParent(item);
				cell = new Listcell(listRuleOperators.get(i).getLabel());
				cell.setStyle("text-align:left;");
				cell.setParent(item);
				item.setParent(listboxFeeOperators);
			}
		}

		doEdit();

		try {
			// fill the components with the data
			this.formula.setFocus(true);
			this.formula.setValue(returnValue);
			this.oldVar_formula = this.formula.getValue();
			this.window_RuleResultDialog.doModal();

		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_RuleResultDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		try {
			doClose();
		} catch (final WrongValueException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Enterring");
		
		boolean close = true;
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("isDataChanged : false");
		}

		if (close) {
			window_RuleResultDialog.onClose();
		}

		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		
		if (this.oldVar_formula != this.formula.getValue()) {
			return true;
		}
		return false;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Enterring");

		btnCancel.setVisible(false);
		this.formula.setReadonly(false);
		
		this.amtCodeColumn.setWidth("45%");
		this.coremirrorColumn.setWidth("40%");
		this.optValuesColumn.setWidth("15%");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Enterring");
		
		//Clients.evalJavaScript(javaScript)
		if(this.formula.getValue()==null || StringUtils.trimToEmpty(this.formula.getValue()).equals("")){
			if (!this.formula.isReadonly()) {
				throw new WrongValueException(formula,"Please enter code");
			}
		}

		try {
			textbox.setValue(this.formula.getValue());
		} catch (WrongValueException e) {
			this.window_RuleResultDialog.onClose();
			throw e;
		}
		this.window_RuleResultDialog.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Method for creating Simulated window with Existing Fields
	 * @param event
	 * @throws InterruptedException
	 */
	private void createSimulationWindow(String Values) throws InterruptedException {
		logger.debug("Entering");

		String[] Variables = Values.split(",");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("Variables", Variables);
		map.put("ruleResultDialogCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/RulesFactory/Rule/RuleResultView.zul",null, map);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}
	
	/**	 
	 * This Method/Event is called from the java script function Validate/Simulate.
	 * It will open a new window to execute the rule 
	 * 
	 * @param event
	 */
	public void onUser$btnValidate(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		if(StringUtils.trimToEmpty(this.formula.getValue()).equals("")){
			PTMessageUtils.showErrorMessage(Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_Formula")}));
		}else{
			while (event.getData() == null) {
				event = ((ForwardEvent) event).getOrigin();
			}
			Object[] data = (Object[]) event.getData();
			// Check clicking button is for Validation  or Simulation
			boolean isSimulated = (Boolean) data[0];
			JSONArray errors = (JSONArray) data[1];
			JSONArray codeVariables = (JSONArray) data[2];
			boolean isSaveRecord = (Boolean) data[3];

			int conf;
			if(isSimulated){
				String values ="";
				for (int i = 0; i < codeVariables.size(); i++) {

					JSONObject jsonObject = (JSONObject) codeVariables.get(i);
					if(!jsonObject.get("name").equals("Result")){
						values = values + jsonObject.get("name")+",";
					}
				}
				createSimulationWindow(values);
			}else{

				if(errors.size() != 0){
					conf =  (MultiLineMessageBox.show(errors.size()+ 
							PennantJavaUtil.getLabel("message_ErrorCount_CodeMirror"),
							PennantJavaUtil.getLabel("Validate_Title"),
							MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

					if (conf==MultiLineMessageBox.YES){
						Events.postEvent("onUser$errors", window_RuleResultDialog,errors);
					}else{
						//do Nothing
					}
				}else{
					if(isSaveRecord){
						doSave();
					}else{
						conf =  MultiLineMessageBox.show(PennantJavaUtil.getLabel("message_NoError_CodeMirror"),
								" Error Details",MultiLineMessageBox.OK, Messagebox.INFORMATION, true);
					}
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for showing Error Details
	 * @param event
	 * @throws InterruptedException
	 */
	public void onUser$errors(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		
		if (formula.getValue().equalsIgnoreCase("")) {
			PTMessageUtils.showErrorMessage(Labels.getLabel("Code_NotEmpty"));
		} else {
			JSONArray message = (JSONArray) event.getData();

			for (int i = 0; i < message.size(); i++) {

				JSONObject jsonObject = (JSONObject) message.get(i);

				if(jsonObject != null){
					
					String errorMsg =  (String) jsonObject.get("reason") ;
					String title = " Error : Line-"+jsonObject.get("line") + ",Character-" + 
											jsonObject.get("character");
					
					int conf;
					if(message.size()-1 != i+1){
						errorMsg = errorMsg +"\n\n"+
									PennantJavaUtil.getLabel("message_ErrorProcess_Conformation");

						conf = MultiLineMessageBox.show(errorMsg,title,
								MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.ERROR, true);
					}else{
						conf = MultiLineMessageBox.show(errorMsg,title,
								MultiLineMessageBox.OK, Messagebox.ERROR, true);
					}

					if (conf==MultiLineMessageBox.NO || conf==MultiLineMessageBox.OK){
						break;
					}else{
						//do Nothing
					}			
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
	
	public void setAmountCodesList(List<BMTRBFldDetails> amountCodesList) {
		this.ruleCodesList = amountCodesList;
	}
	public List<BMTRBFldDetails> getAmountCodesList() {
		return ruleCodesList;
	}

	
}
