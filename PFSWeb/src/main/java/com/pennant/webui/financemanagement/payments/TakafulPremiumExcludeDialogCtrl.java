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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  TakafulPremiumExcludeDialogCtrl.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  03-06-2011    
 *                                                                  
 * Modified Date    :  03-06-2011    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-06-2011       Pennant	                 0.1                                         * 
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
package com.pennant.webui.financemanagement.payments;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.FinanceSelectCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the
 * WEB-INF/pages/FinanceManagement/Payments/TakafulPremiumExcludeDialog.zul
 */
public class TakafulPremiumExcludeDialogCtrl extends GFCBaseCtrl<FinanceMain> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = Logger.getLogger(TakafulPremiumExcludeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 			window_TakafulPremiumExcludeDialog;
	protected Borderlayout		borderlayoutTakafulPremiumExclude;

	//Summary Details
	protected Textbox	 	finReference;
	protected Textbox	 	finType;
	protected Textbox	 	finBranch;
	protected Textbox	 	finCcy;
	protected Textbox	 	custID;
	protected Datebox	 	finStartDate;
	protected Datebox	 	maturityDate;

	protected Decimalbox	takafulFeeAmt;
	protected Decimalbox	paidAmount;
	protected Checkbox	    isExcludeFromReport;

	protected Listbox 		listBoxSchedule;

	private transient FinanceSelectCtrl financeSelectCtrl = null;
	private FinanceMain financeMain;
	private FeeRule feeRule;
	private FinanceDetailService financeDetailService;
	
	private int ccyFormat = 0;
	private String menuItemRightName = null;
	
	/**
	 * default constructor.<br>
	 */
	public TakafulPremiumExcludeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "TakafulPremiumExcludeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Rule object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_TakafulPremiumExcludeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_TakafulPremiumExcludeDialog);

		try{ 

			if (arguments.containsKey("feeRule")) {
				setFeeRule((FeeRule) arguments.get("feeRule"));
			}

			if (arguments.containsKey("financeMain")) {
				FinanceMain aFinanceMain = (FinanceMain) arguments.get("financeMain");
				setFinanceMain(aFinanceMain);
				
				FinanceMain befImage = new FinanceMain();
				Cloner cloner = new Cloner();
				befImage = cloner.deepClone(aFinanceMain);
				getFinanceMain().setBefImage(befImage);
			}

			if (arguments.containsKey("menuItemRightName")) {
				menuItemRightName = (String) arguments.get("menuItemRightName");
			}

			if (arguments.containsKey("financeSelectCtrl")) {
				setFinanceSelectCtrl((FinanceSelectCtrl) arguments.get("financeSelectCtrl"));
			} 

			doLoadWorkFlow(financeMain.isWorkflow(), financeMain.getWorkflowId(), financeMain.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateMenuRoleAuthorities(getRole(), "TakafulPremiumExclude", menuItemRightName);
			}else{
				this.south.setHeight("0px");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();
			
			// set Field Properties
			doSetFieldProperties();

			doEdit();
			if (!financeMain.isNewRecord()) {
				this.btnNotes.setVisible(true);
			}else{
				
			}

			doWriteBeanToComponents();

			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_TakafulPremiumExcludeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities("TakafulPremiumExclude",getRole(), menuItemRightName);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_TakafulPremiumExcludeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_TakafulPremiumExcludeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_TakafulPremiumExcludeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_TakafulPremiumExcludeDialog_btnSave"));
		this.btnCancel.setVisible(false);
		
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes

		ccyFormat = CurrencyUtil.getFormat(financeMain.getFinCcy());
		

		this.finStartDate.setFormat(DateFormat.LONG_DATE.getPattern());
		this.maturityDate.setFormat(DateFormat.LONG_DATE.getPattern());	
		
		this.takafulFeeAmt.setMaxlength(18);
		this.takafulFeeAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.takafulFeeAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.takafulFeeAmt.setScale(ccyFormat);

		this.paidAmount.setMaxlength(18);
		this.paidAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.paidAmount.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.paidAmount.setScale(ccyFormat);

		logger.debug("Leaving");
	}

	public void onClick$btnSave(Event event){
		logger.debug("Entering "+event.toString());

		doWriteComponentsToBean();
		boolean isRecordUpdated = getFinanceDetailService().updateFeeChargesByFinRefAndFeeCode(getFeeRule(), "");
		if(isRecordUpdated){
			closeDialog();
		} else {
			
		}

		logger.debug("Leaving "+event.toString());

	}
	
	
	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		
		logger.debug("Leaving");
	}

	/**
	 * Method to fill finance data.
	 * 
	 * @param isChgRpy
	 * @throws InterruptedException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private void doWriteBeanToComponents() throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		this.finReference.setValue(financeMain.getFinReference());
		this.finType.setValue(financeMain.getFinType()+(financeMain.getLovDescFinTypeName() != null ? "-"+financeMain.getLovDescFinTypeName() : ""));
		this.finBranch.setValue(financeMain.getFinBranch() + (financeMain.getLovDescFinBranchName() !=null ? "-"+ financeMain.getLovDescFinBranchName():""));
		this.finCcy.setValue(financeMain.getFinCcy());
		this.custID.setValue(financeMain.getLovDescCustCIF() + (financeMain.getLovDescCustShrtName() != null ? "-"+ financeMain.getLovDescCustShrtName() : ""));
		this.finStartDate.setValue(financeMain.getFinStartDate());
		this.maturityDate.setValue(financeMain.getMaturityDate());

		this.takafulFeeAmt.setValue(PennantAppUtil.formateAmount(getFeeRule().getFeeAmount(), ccyFormat));
		this.paidAmount.setValue(PennantAppUtil.formateAmount(getFeeRule().getPaidAmount(), ccyFormat));
		this.isExcludeFromReport.setChecked(getFeeRule().isExcludeFromRpt());
		
		this.recordStatus.setValue(financeMain.getRecordStatus());

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
		
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Close_Data_YesNo");

			if (MessageUtil.confirm(msg) == MessageUtil.YES) {
				closeDialog();
			}
		} else {
			logger.debug("isDataChanged : false");
			closeDialog();
		}

		logger.debug("Leaving" + event.toString());
	}

	public List<FinanceScheduleDetail> sortSchdDetails(
			List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtility.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;
	}

	
	
	private FeeRule doWriteComponentsToBean(){
		logger.debug("Entering");
		
		FeeRule feeRule = getFeeRule();
		
		feeRule.setFeeAmount(PennantAppUtil.unFormateAmount(this.takafulFeeAmt.getValue(), ccyFormat));
		feeRule.setPaidAmount(PennantAppUtil.unFormateAmount(this.paidAmount.getValue(), ccyFormat));
		feeRule.setExcludeFromRpt(this.isExcludeFromReport.isChecked());
		logger.debug("Leaving");
		return feeRule;
	}

	/**
	 * when user clicks on button "Notes"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.financeMain);
	}

	protected void refreshMaintainList() {
		final JdbcSearchObject<FinanceMain> soFinanceMain = getFinanceSelectCtrl().getSearchObj(true);
		getFinanceSelectCtrl().getPagingFinanceList().setActivePage(0);
		getFinanceSelectCtrl().getPagedListWrapper().setSearchObject(soFinanceMain);
		if (getFinanceSelectCtrl().getListBoxFinance() != null) {
			getFinanceSelectCtrl().getListBoxFinance().getListModel();
		}
	}
	@Override
	protected String getReference() {
		return String.valueOf(this.financeMain.getFinReference());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceMain getFinanceMain() {
		return financeMain;
	}
	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public FeeRule getFeeRule() {
		return feeRule;
	}
	public void setFeeRule(FeeRule feeRule) {
		this.feeRule = feeRule;
	}

	public FinanceSelectCtrl getFinanceSelectCtrl() {
		return financeSelectCtrl;
	}
	public void setFinanceSelectCtrl(FinanceSelectCtrl financeSelectCtrl) {
		this.financeSelectCtrl = financeSelectCtrl;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

}