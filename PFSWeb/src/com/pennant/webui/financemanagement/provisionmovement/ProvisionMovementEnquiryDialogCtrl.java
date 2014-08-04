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
 * FileName    		:  ProvisionMovementListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.webui.financemanagement.provisionmovement;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.financemanagement.ProvisionMovement;
import com.pennant.backend.service.financemanagement.ProvisionMovementService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.financemanagement.provision.ProvisionListCtrl;
import com.pennant.webui.financemanagement.provisionmovement.model.ProvisionMovementListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Provision/ProvisionMovement/ProvisionMovementList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class ProvisionMovementEnquiryDialogCtrl extends GFCBaseListCtrl<ProvisionMovement> implements Serializable {

	private static final long serialVersionUID = -1620412127444337321L;
	private final static Logger logger = Logger.getLogger(ProvisionMovementEnquiryDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_ProvisionMovementList; 			// autowired

	protected Listbox 		listBoxProvisionMovement; 				// autowired
	protected Grid 			grid_Basicdetails;						// autowired
	
	protected Textbox 		finReference;			// autowired
	protected Textbox 		finBranch; 				// autowired
	protected Textbox 		finType; 				// autowired
	protected Longbox 		custID; 				// autowired
	protected Textbox 		lovDescCustCIF; 		// autowired
	protected Label   		custShrtName;			// autowired
	protected Checkbox 		useNFProv; 				// autowired
	protected Checkbox 		autoReleaseNFP; 		// autowired
	protected Decimalbox 	principalDue; 			// autowired
	protected Decimalbox 	profitDue; 				// autowired
	protected Decimalbox 	dueTotal; 				// autowired
	protected Decimalbox 	nonFormulaProv; 		// autowired
	protected Datebox 		dueFromDate; 			// autowired
	protected Decimalbox 	provisionedAmt; 		// autowired
	protected Datebox 		lastFullyPaidDate; 	    // autowired
	protected Decimalbox 	calProvisionedAmt; 	    // autowired

	// checkRights
	protected Button btnHelp; 		// autowired
	protected Button btnClose; 		// autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<ProvisionMovement> searchObj;
	private transient ProvisionMovementService provisionMovementService;
	private transient WorkFlowDetails workFlowDetails=null;
	private transient Provision provision = null;
	private ProvisionListCtrl provisionListCtrl;
	int listRows;
	
	/**
	 * default constructor.<br>
	 */
	public ProvisionMovementEnquiryDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected ProvisionMovement object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ProvisionMovementList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("provision")) {
			this.setProvision((Provision) args.get("provision"));
		} 
		
		if (args.containsKey("provisionListCtrl")) {
			setProvisionListCtrl((ProvisionListCtrl) args.get("provisionListCtrl"));
		} else {
			setProvisionListCtrl(null);
		}
		
		getBorderLayoutHeight();
		grid_Basicdetails.getRows().getVisibleItemCount();
		int dialogHeight =  grid_Basicdetails.getRows().getVisibleItemCount()* 20 + 100 ; 
		int listboxHeight = borderLayoutHeight-dialogHeight;
		listBoxProvisionMovement.setHeight(listboxHeight+"px");
		listRows = Math.round(listboxHeight/ 24)-1;
		
		doShowDialog(getProvision());
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aProvision
	 * @throws InterruptedException
	 */
	public void doShowDialog(Provision aProvision) throws InterruptedException {
		logger.debug("Entering");

		// if aAccountingSet == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aProvision != null) {
			setProvision(aProvision);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		this.finReference.focus();

		try {
			// fill the components with the data
			doWriteBeanToComponents(aProvision);

			// stores the initial data for comparing if they are changed
			// during user action.
			
			setDialog(this.window_ProvisionMovementList);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aProvision
	 *            Provision
	 */
	public void doWriteBeanToComponents(Provision aProvision) {
		logger.debug("Entering") ;
		this.finReference.setValue(aProvision.getFinReference());
		this.finBranch.setValue(aProvision.getFinBranch());
		this.finType.setValue(aProvision.getFinType());
		this.custID.setValue(aProvision.getCustID());
		this.lovDescCustCIF.setValue(aProvision.getLovDescCustCIF());
		this.custShrtName.setValue(aProvision.getLovDescCustShrtName());
		this.useNFProv.setChecked(aProvision.isUseNFProv());
		this.autoReleaseNFP.setChecked(aProvision.isAutoReleaseNFP());
		this.provisionedAmt.setValue(aProvision.getProvisionedAmt());
		this.principalDue.setValue(PennantAppUtil.formateAmount(aProvision.getPrincipalDue(),
				aProvision.getLovDescFinFormatter()));
		this.profitDue.setValue(PennantAppUtil.formateAmount(aProvision.getProfitDue(),
				aProvision.getLovDescFinFormatter()));
		this.dueTotal.setValue(PennantAppUtil.formateAmount(aProvision.getPrincipalDue().
				add(aProvision.getProfitDue()),aProvision.getLovDescFinFormatter()));
		this.nonFormulaProv.setValue(PennantAppUtil.formateAmount(aProvision.getNonFormulaProv().
				add(aProvision.getProfitDue()),aProvision.getLovDescFinFormatter()));
		this.dueFromDate.setValue(aProvision.getDueFromDate());
		this.lastFullyPaidDate.setValue(aProvision.getLastFullyPaidDate());
		this.calProvisionedAmt.setValue(aProvision.getProvisionAmtCal());
		
		doFilllistbox(aProvision.getProvisionMovementList());

		logger.debug("Leaving");
	}
	
	/**
	 * Method for rendering list of TransactionEntry
	 * 
	 * @param transactionEntryList
	 */
	public void doFilllistbox(List<ProvisionMovement> provisionMovements) {
		logger.debug("Entering");
		if (provisionMovements != null) {
			getPagedListWrapper().initList(provisionMovements, 
					this.listBoxProvisionMovement, new Paging());
			this.listBoxProvisionMovement.setItemRenderer(new ProvisionMovementListModelItemRenderer());
		}
		logger.debug("Leaving");
	}


	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.provision.provisionmovement.model.
	 * ProvisionMovementListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onProvisionMovementItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected ProvisionMovement object
		final Listitem item = this.listBoxProvisionMovement.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final ProvisionMovement aProvisionMovement = (ProvisionMovement) item.getAttribute("data");
			final ProvisionMovement provisionMovement = getProvisionMovementService().getProvisionMovementById(
					aProvisionMovement.getId(),aProvisionMovement.getProvMovementDate(),Long.MIN_VALUE);
			
			if(provisionMovement==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aProvisionMovement.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(
						PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond = " AND FinReference='" + provisionMovement.getFinReference()
							+ "' AND version=" + provisionMovement.getVersion() + " ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "ProvisionMovement", 
							whereCond, provisionMovement.getTaskId(), provisionMovement.getNextTaskId());
					if (userAcces){
						showDetailView(provisionMovement);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(provisionMovement);
				}
			}	
		}
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
			closeDialog(this.window_ProvisionMovementList, "ProvisionMovement");
		} catch (final WrongValuesException e) {
			logger.debug(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param ProvisionMovement (aProvisionMovement)
	 * @throws Exception
	 */
	private void showDetailView(ProvisionMovement aProvisionMovement) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aProvisionMovement.getWorkflowId()==0 && isWorkFlowEnabled()){
			aProvisionMovement.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("provisionMovement", aProvisionMovement);
		map.put("provision", getProvision());
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the ProvisionMovementListbox from the
		 * dialog when we do a delete, edit or insert a ProvisionMovement.
		 */
		map.put("movementEnquiryDialogCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/ProvisionMovement/ProvisionMovementPostingsEnquiryDialog.zul",
					null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_ProvisionMovementList);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setProvisionMovementService(ProvisionMovementService provisionMovementService) {
		this.provisionMovementService = provisionMovementService;
	}
	public ProvisionMovementService getProvisionMovementService() {
		return this.provisionMovementService;
	}

	public JdbcSearchObject<ProvisionMovement> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<ProvisionMovement> searchObj) {
		this.searchObj = searchObj;
	}

	public void setProvision(Provision provision) {
		this.provision = provision;
	}
	public Provision getProvision() {
		return provision;
	}

	public void setProvisionListCtrl(ProvisionListCtrl provisionListCtrl) {
		this.provisionListCtrl = provisionListCtrl;
	}
	public ProvisionListCtrl getProvisionListCtrl() {
		return provisionListCtrl;
	}
}