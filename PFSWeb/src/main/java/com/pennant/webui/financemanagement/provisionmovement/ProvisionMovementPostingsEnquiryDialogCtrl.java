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

import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.financemanagement.ProvisionMovement;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.financemanagement.provisionmovement.model.ProvisionPostingsListModelItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Provision/ProvisionMovement/ProvisionMovementList.zul file.
 */
public class ProvisionMovementPostingsEnquiryDialogCtrl extends GFCBaseCtrl<ReturnDataSet> {
	private static final long serialVersionUID = -1620412127444337321L;
	private static final Logger logger = Logger.getLogger(ProvisionMovementPostingsEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_ProvisionMovementPostingsEnquiryList; 			// autowired

	protected Listbox 		listBoxProvisionMovementPostings; 		// autowired
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
	protected Datebox 		lastFullyPaidDate; 			// autowired

	// checkRights
	protected Button btnHelp; 		// autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<ProvisionMovement> searchObj;
	private transient Provision provision = null;
	private transient ProvisionMovement provisionMovement = null;
	private ProvisionMovementEnquiryDialogCtrl movementEnquiryDialogCtrl;
	int listRows;
	
	/**
	 * default constructor.<br>
	 */
	public ProvisionMovementPostingsEnquiryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected ProvisionMovement object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ProvisionMovementPostingsEnquiryList(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ProvisionMovementPostingsEnquiryList);

		try {
			if (arguments.containsKey("provision")) {
				this.setProvision((Provision) arguments.get("provision"));
			}

			if (arguments.containsKey("provisionMovement")) {
				this.setProvisionMovement((ProvisionMovement) arguments
						.get("provisionMovement"));
			}

			if (arguments.containsKey("movementEnquiryDialogCtrl")) {
				setMovementEnquiryDialogCtrl((ProvisionMovementEnquiryDialogCtrl) arguments
						.get("movementEnquiryDialogCtrl"));
			} else {
				setMovementEnquiryDialogCtrl(null);
			}

			getBorderLayoutHeight();
			grid_Basicdetails.getRows().getVisibleItemCount();
			int dialogHeight = grid_Basicdetails.getRows()
					.getVisibleItemCount() * 20 + 100;
			int listboxHeight = borderLayoutHeight - dialogHeight;
			listBoxProvisionMovementPostings.setHeight(listboxHeight + "px");
			listRows = Math.round(listboxHeight / 24) - 1;

			doShowDialog(getProvisionMovement(), getProvision());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ProvisionMovementPostingsEnquiryList.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aProvision
	 * @throws Exception
	 */
	public void doShowDialog(ProvisionMovement movement, Provision aProvision) throws Exception {
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
			doWriteBeanToComponents(movement, aProvision);

			// stores the initial data for comparing if they are changed
			// during user action.
			
			getMovementEnquiryDialogCtrl().window_ProvisionMovementList.setVisible(false);
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ProvisionMovementPostingsEnquiryList.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aProvisionMovement
	 *            Provision
	 */
	public void doWriteBeanToComponents(ProvisionMovement aProvisionMovement,Provision provision) {
		logger.debug("Entering") ;
		
		int format = CurrencyUtil.getFormat(provision.getFinCcy());
		
		this.finReference.setValue(aProvisionMovement.getFinReference());
		this.finBranch.setValue(provision.getFinBranch());
		this.finType.setValue(provision.getFinType());
		this.custID.setValue(provision.getCustID());
		this.lovDescCustCIF.setValue(provision.getLovDescCustCIF());
		this.custShrtName.setValue(provision.getLovDescCustShrtName());
		this.useNFProv.setChecked(aProvisionMovement.isUseNFProv());
		this.autoReleaseNFP.setChecked(aProvisionMovement.isAutoReleaseNFP());
		this.provisionedAmt.setValue(aProvisionMovement.getProvisionedAmt());
		this.principalDue.setValue(PennantAppUtil.formateAmount(aProvisionMovement.getPrincipalDue(),
				format));
		this.profitDue.setValue(PennantAppUtil.formateAmount(aProvisionMovement.getProfitDue(),
				format));
		this.dueTotal.setValue(PennantAppUtil.formateAmount(aProvisionMovement.getPrincipalDue().
				add(aProvisionMovement.getProfitDue()),format));
		this.nonFormulaProv.setValue(PennantAppUtil.formateAmount(aProvisionMovement.getNonFormulaProv().
				add(aProvisionMovement.getProfitDue()),format));
		this.dueFromDate.setValue(aProvisionMovement.getDueFromDate());
		this.lastFullyPaidDate.setValue(aProvisionMovement.getLastFullyPaidDate());
		
		doFilllistbox(aProvisionMovement.getPostingsList());

		logger.debug("Leaving");
	}
	
	/**
	 * Method for rendering list of TransactionEntry
	 * 
	 * @param transactionEntryList
	 */
	public void doFilllistbox(List<ReturnDataSet> postingsList) {
		logger.debug("Entering");
		if (postingsList != null) {
			getPagedListWrapper().initList(postingsList, 
					this.listBoxProvisionMovementPostings, new Paging());
			this.listBoxProvisionMovementPostings.setItemRenderer(new ProvisionPostingsListModelItemRenderer());
		}
		logger.debug("Leaving");
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
			this.window_ProvisionMovementPostingsEnquiryList.onClose();
			getMovementEnquiryDialogCtrl().window_ProvisionMovementList.setVisible(true);
		} catch (final WrongValuesException e) {
			logger.debug(e);
			throw e;
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
		MessageUtil.showHelpWindow(event, window_ProvisionMovementPostingsEnquiryList);
		logger.debug("Leaving" + event.toString());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public void setMovementEnquiryDialogCtrl(ProvisionMovementEnquiryDialogCtrl movementEnquiryDialogCtrl) {
		this.movementEnquiryDialogCtrl = movementEnquiryDialogCtrl;
	}
	public ProvisionMovementEnquiryDialogCtrl getMovementEnquiryDialogCtrl() {
		return movementEnquiryDialogCtrl;
	}

	public void setProvisionMovement(ProvisionMovement provisionMovement) {
		this.provisionMovement = provisionMovement;
	}
	public ProvisionMovement getProvisionMovement() {
		return provisionMovement;
	}
}