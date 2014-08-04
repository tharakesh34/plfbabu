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
 * FileName    		:  MurabahaFinanceMainDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.financemain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.Interface.model.IAccounts;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PennantReferenceIDUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.InvestmentFinHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.TreasuaryFinanceService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.util.Constraint.RateValidator;
import com.pennant.webui.finance.investment.DealFinanceBaseCtrl;
import com.pennant.webui.finance.treasuaryfinance.TreasuaryFinHeaderDialogCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/FinanceMain/InvestmentDealDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class InvestmentDealDialogCtrl extends DealFinanceBaseCtrl implements Serializable {

	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(InvestmentDealDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_InvestmentDealDialogCtrl; 				// autoWired	

	private TreasuaryFinHeaderDialogCtrl treasuaryFinHeaderDialogCtrl=null;
	private transient TreasuaryFinanceService treasuaryFinanceService;
	
	protected ExtendedCombobox   	finBranch; 			// autoWired
	protected ExtendedCombobox 	    finType;             // autoWired
	
	// not auto wired variables
	private transient long oldVar_custID;
	private transient String oldVar_finBranch;
	private transient String oldVar_finType;
	private transient BigDecimal oldVar_finAmount = BigDecimal.ZERO;
	private transient BigDecimal oldVar_totalRepayAmt = BigDecimal.ZERO;;
	private transient BigDecimal oldVar_prinInvested = BigDecimal.ZERO;
	private transient BigDecimal oldVar_prinMaturity = BigDecimal.ZERO;
	private transient BigDecimal oldVar_RepayProfitRate = BigDecimal.ZERO;
	private transient boolean validationOn;
	
	private transient final String btnCtroller_ClassPrefix = "button_InvestmentDealDialog_";
	private transient ButtonStatusCtrl btnCtrl;

	private List<FinanceDetail> finDetailList;
	private String moduleType = "";

	private int ccyFormat = 0;

	/**
	 * default constructor.<br>
	 */
	public InvestmentDealDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_InvestmentDealDialogCtrl(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("investmentFinHeader")) {
			this.investmentFinHeader = (InvestmentFinHeader) args.get("investmentFinHeader");
			financeDetail = this.investmentFinHeader.getFinanceDetail();
			financeDetail.setDataFetchComplete(true);
			setFinanceDetail(financeDetail);
			
			InvestmentFinHeader befImage = new InvestmentFinHeader();
			BeanUtils.copyProperties(this.investmentFinHeader, befImage);
			this.investmentFinHeader.setBefImage(befImage);
			setInvestmentFinHeader(this.investmentFinHeader);
		}
		
		if (args.containsKey("treasuaryFinHeaderDialogCtrl")) {
			setTreasuaryFinHeaderDialogCtrl((TreasuaryFinHeaderDialogCtrl) args.get("treasuaryFinHeaderDialogCtrl"));
			setNewInvestment(true);

			if (args.containsKey("newRecord")) {
				setNewRecord(true);
			} else {
				setNewRecord(false);
			}

			setRole(getTreasuaryFinHeaderDialogCtrl().getRole());
			getUserWorkspace().alocateRoleAuthorities(getRole(), "InvestmentDealDialog");
		}
		
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		financeMain.setWorkflowId(0);
		
		doLoadWorkFlow(financeMain.isWorkflow(), financeMain.getWorkflowId(), financeMain.getNextTaskId());
		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "InvestmentDealDialog");
		}
		
		doCheckRights();
		doSetFieldProperties();
		doShowDialog(this.investmentFinHeader, this.financeDetail);
		
		logger.debug("Leaving " + event.toString());
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
		
		getUserWorkspace().alocateAuthorities("InvestmentDealDialog", getRole());

		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_InvestmentDealDialog_btnDelete"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_InvestmentDealDialog_btnEdit"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_InvestmentDealDialog_btnSave"));
		this.btnNew.setVisible(false);
		this.btnCancel.setVisible(false);
		
		logger.debug("Leaving");
	}	

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_FinContributorDetailDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSave();
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
		PTMessageUtils.showHelpWindow(event, window_InvestmentDealDialogCtrl);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		try {
			doClose();
		} catch (final WrongValueException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	public void closeWindow() throws InterruptedException {
		closeDialog2(this.window_InvestmentDealDialogCtrl, "InvestmentDealDialog");
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
	private void doClose() throws Exception {
		logger.debug("Entering");
		boolean close = true;

		if (isDataChanged()) {
			logger.debug("Data Changed(): True");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, 
					MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("Data Changed(): false");
		}
		if (close) {
			closeWindow();
		}
		logger.debug("Leaving");		
	}

	/**
	 * Deletes a FinContributorDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		
		final FinanceDetail aFinanceDetail = new FinanceDetail();
		BeanUtils.copyProperties(getFinanceDetail(), aFinanceDetail);
		final FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_value",
				new String[] {Labels.getLabel("listheader_dealTicketRef.label")})
				+ "\n\n --> " + this.investmentRef.getText();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, 
				MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aFinanceMain.getRecordType()).equals("")){
				aFinanceMain.setVersion(aFinanceMain.getVersion()+1);
				aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				aFinanceMain.setNewRecord(true);

				if (isWorkFlowEnabled()){
					aFinanceMain.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}
			
			try {
				
				aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
				
				if(isNewInvestment()){
					tranType=PennantConstants.TRAN_DEL;
					AuditHeader auditHeader =  newDealProcess(aFinanceDetail, tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_InvestmentDealDialogCtrl, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
						getTreasuaryFinHeaderDialogCtrl().doFillTicketDetails(this.finDetailList);
						// send the data back to Treasury Finance Header
						closeWindow();
					}	
				}
				
			}catch (DataAccessException e){
				logger.error(e);
				showErrorMessage(this.window_InvestmentDealDialogCtrl, e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCity
	 *            
	 */
	public void doWriteBeanToComponents(InvestmentFinHeader aFinHeader, FinanceDetail aFinanceDetail) {
		logger.debug("Entering ");
		
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		aFinanceMain.setProfitDaysBasis(aFinHeader.getProfitDaysBasis());
		aFinanceMain.setFinCcy(aFinHeader.getFinCcy());
		aFinanceMain.setLovDescFinCcyName(aFinHeader.getLovDescFinCcyName());

		// Investment Header Fields		
		this.investmentRef.setValue(aFinHeader.getInvestmentRef());
		this.totPrinAmt.setValue(PennantAppUtil.formateAmount(aFinHeader.getTotPrincipalAmt(), ccyFormat));
		this.finCcy.setValue(aFinHeader.getFinCcy());

		if(!StringUtils.trimToEmpty(aFinHeader.getFinCcy()).equals("")){
			this.lovDescfinCcyName.setValue(aFinHeader.getLovDescFinCcyName());
		}

		this.profitDaysBasis.setValue(PennantAppUtil.getlabelDesc(aFinHeader.getProfitDaysBasis(), profitDaysBasisList));
		this.invStartDate.setValue(aFinHeader.getStartDate());
		this.invMaturityDate.setValue(aFinHeader.getMaturityDate());
		this.prinInvested.setValue(PennantAppUtil.formateAmount(aFinHeader.getPrincipalInvested(), ccyFormat));
		this.prinDueToInvest.setValue(PennantAppUtil.formateAmount(aFinHeader.getPrincipalDueToInvest(), ccyFormat));
		this.prinMaturity.setValue(PennantAppUtil.formateAmount(aFinHeader.getPrincipalMaturity(), ccyFormat));
		this.avgPftRate.setValue(aFinHeader.getAvgPftRate());

		//# Investment Detail Fields
		this.custID.setValue(aFinanceMain.getCustID());
		this.lovDescCustCIF.setValue(aFinanceMain.getLovDescCustCIF());
		this.custShrtName.setValue(aFinanceMain.getLovDescCustShrtName());
		
		this.finBranch.setValue(aFinanceMain.getFinBranch());
		if(!StringUtils.trimToEmpty(aFinanceMain.getFinBranch()).equals("")){
			this.finBranch.setDescription(aFinanceMain.getLovDescFinBranchName());
		}

		this.finType.setValue(aFinanceMain.getFinType());
		if (!StringUtils.trimToEmpty(aFinanceMain.getFinType()).equals("")) {
			this.finType.setDescription(aFinanceMain.getLovDescFinTypeName());
		}

		if(StringUtils.trimToNull(aFinanceMain.getFinReference()) == null) {
			aFinanceMain.setFinReference(String.valueOf(PennantReferenceIDUtil.genNewWhatIfRef(false)));
		}

		this.dealTcktRef.setValue(aFinanceMain.getFinReference());		

		if(aFinanceMain.getFinStartDate() == null){
			this.startDate.setValue(aFinHeader.getStartDate());
		} else {
			this.startDate.setValue(aFinanceMain.getFinStartDate());
		}

		if(aFinanceMain.getMaturityDate() == null) {
			this.maturityDate.setValue(aFinHeader.getMaturityDate());
		} else {
			this.maturityDate.setValue(aFinanceMain.getMaturityDate());
		}
		
		this.finAmount.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinAmount(), ccyFormat));		
		this.finAmount_Two.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinAmount(), ccyFormat));		
		this.repayProfitRate.setValue(aFinanceMain.getRepayProfitRate());
		this.totalRepayAmt.setValue(PennantAppUtil.formateAmount(aFinanceMain.getTotalRepayAmt(), ccyFormat));

		try {
			doFillTabs(financeDetail);
		} catch (ParseException e) {
			e.printStackTrace();
			logger.error(e);
		} catch (InterruptedException e) {
			e.printStackTrace();
			logger.error(e);
		}

		logger.debug("Leaving ");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCity
	 */
	public void doWriteComponentsToBean(FinanceDetail aFinanceDetail) {
		logger.debug("Entering ");
		
		doClearMessage();
		doSetValidation();
		doSetLOVValidation();
		
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		aFinanceMain.setFinCcy(this.finCcy.getValue());
		aFinanceMain.setInvestmentRef(this.investmentRef.getValue());
		aFinanceMain.setFinReference(this.dealTcktRef.getValue());

		try {
			if (!this.btnSearchCustCIF.isDisabled()) {

				aFinanceMain.setLovDescCustCIF(this.lovDescCustCIF.getValue());
				if(StringUtils.trimToNull(this.lovDescCustCIF.getValue()) == null) {
					throw new WrongValueException(this.lovDescCustCIF, Labels.getLabel("FIELD_NO_INVALID", 
							new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_CustID.value") }));
				} else {
					aFinanceMain.setCustID(this.custID.longValue());
					aFinanceMain.setLovDescCustShrtName(this.custShrtName.getValue());
				}
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		try {
			aFinanceMain.setFinBranch(this.finBranch.getValidatedValue());
			aFinanceMain.setLovDescFinBranchName(this.finBranch.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setFinType(this.finType.getValidatedValue());
			aFinanceMain.setLovDescFinTypeName(this.finType.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {	
			aFinanceMain.setFinAmount(PennantAppUtil.unFormateAmount(this.finAmount.getValue(), ccyFormat));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		aFinanceMain.setTotalRepayAmt(PennantAppUtil.unFormateAmount(this.totalRepayAmt.getValue(), ccyFormat));

		try {
			aFinanceMain.setRepayProfitRate(this.repayProfitRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		aFinanceMain.setProfitDaysBasis(getInvestmentFinHeader().getProfitDaysBasis());

		setRepaymentAmt();
		validateDates(aFinanceMain, wve);

		doRemoveValidation();
		doRemoveLOVValidation();
		showErrorDetails(wve);

		doWriteDefaultsToBean(aFinanceDetail); 
		aFinanceMain.setRecordStatus(this.recordStatus.getValue());
		getFinanceDetail().getFinScheduleData().setFinanceMain(aFinanceMain);
		logger.debug("Leaving ");
	}

	private void validateDates(FinanceMain aFinanceMain, ArrayList<WrongValueException> wve) {
		
		try {
			if (DateUtility.compare(this.startDate.getValue(), this.maturityDate.getValue()) >= 0) {
				throw new WrongValueException(this.maturityDate, Labels.getLabel("DATE_ALLOWED_MAXDATE",
						new String[] {Labels.getLabel("label_InvestmentDealDialog_InvMaturityDate.value"),
								Labels.getLabel("label_InvestmentDealDialog_InvStartDate.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			
			if (DateUtility.compare(this.invStartDate.getValue(), this.maturityDate.getValue()) >= 0) {
				throw new WrongValueException(this.maturityDate,Labels.getLabel("DATE_ALLOWED_MINDATE",
						new String[] {Labels.getLabel("label_InvestmentDealDialog_InvMaturityDate.value"),
						Labels.getLabel("label_TreasuaryFinHeaderDialog_StartDate.value")}));
			}

			if (DateUtility.compare(this.invMaturityDate.getValue(), this.maturityDate.getValue()) < 0) {
				throw new WrongValueException(this.maturityDate, Labels.getLabel("DATE_ALLOWED_MAXDATE_EQUAL",
						new String[] {Labels.getLabel("label_InvestmentDealDialog_InvMaturityDate.value"),
								Labels.getLabel("label_TreasuaryFinHeaderDialog_MaturityDate.value") }));
			}
			
			aFinanceMain.setMaturityDate(this.maturityDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			
			if (DateUtility.compare(this.invStartDate.getValue(), this.startDate.getValue()) > 0) {
				throw new WrongValueException(this.maturityDate,Labels.getLabel("DATE_ALLOWED_MINDATE_EQUAL",
						new String[] {Labels.getLabel("label_InvestmentDealDialog_InvStartDate.value"),
						Labels.getLabel("label_TreasuaryFinHeaderDialog_StartDate.value")}));
			}
			
			if (DateUtility.compare(this.invMaturityDate.getValue(), this.startDate.getValue()) <= 0) {
				throw new WrongValueException(this.maturityDate, Labels.getLabel("DATE_ALLOWED_MAXDATE_EQUAL",
						new String[] {Labels.getLabel("label_InvestmentDealDialog_InvStartDate.value"),
						Labels.getLabel("label_TreasuaryFinHeaderDialog_MaturityDate.value") }));
			}
			
			aFinanceMain.setFinStartDate(this.startDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws InterruptedException
	 */
	public void doShowDialog(InvestmentFinHeader aInvestmentFinHeader,FinanceDetail aFinanceDetail) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.lovDescCustCIF.focus();
		} else {
			if (isNewInvestment()){
				doEdit();
			}else  if (isWorkFlowEnabled()){
				this.btnNotes.setVisible(true);
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		doSetFieldType();

		try {
			// fill the components with the data
			doWriteBeanToComponents(aInvestmentFinHeader,aFinanceDetail);
			doStoreInitValues();
			setDialog2(window_InvestmentDealDialogCtrl);

		} catch (final Exception e) {
			logger.error(e);
			e.printStackTrace();
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}


	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		doClearMessage();
		this.oldVar_custID = this.custID.getValue();
		this.oldVar_finBranch = this.finBranch.getValue();
		this.oldVar_finAmount = PennantApplicationUtil.unFormateAmount(this.finAmount.getValue(), ccyFormat);
		this.oldVar_finType = this.finType.getValue();
		this.oldVar_totalRepayAmt = PennantApplicationUtil.unFormateAmount(this.totalRepayAmt.getValue(), ccyFormat); 		
		this.oldVar_prinInvested = PennantApplicationUtil.unFormateAmount(this.prinInvested.getValue(), ccyFormat);
		this.oldVar_prinMaturity = PennantApplicationUtil.unFormateAmount(this.prinMaturity.getValue(), ccyFormat);
		this.oldVar_RepayProfitRate = this.repayProfitRate.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	public void doResetInitValues() {
		logger.debug("Entering");

		this.custID.setValue(this.oldVar_custID);
		this.finBranch.setValue(this.oldVar_finBranch);
		this.finType.setValue(this.oldVar_finType);
		this.finAmount.setValue(this.oldVar_finAmount);
		this.repayProfitRate.setValue(this.oldVar_RepayProfitRate);

		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		logger.debug("Entering");

		// To clear the Error Messages
		
		doClearMessage();
		
		if (this.oldVar_custID != this.custID.getValue()) {
			return true;
		}

		if (this.oldVar_finBranch != this.finBranch.getValue()) {
			return true;
		}

		if (this.oldVar_finAmount.compareTo(PennantApplicationUtil.unFormateAmount(this.finAmount.getValue(), ccyFormat)) != 0) {
			return true;
		}

		if (this.oldVar_finType != this.finType.getValue()) {
			return true;
		}
		
		if (this.oldVar_RepayProfitRate.compareTo(this.repayProfitRate.getValue()) != 0) {
			return true;
		}
		logger.debug("Leaving");
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.startDate.isReadonly()) {			
			this.startDate.setConstraint(setNotEmpty("label_TreasuaryFinHeaderDialog_StartDate.value"));
		}

		if (!this.maturityDate.isReadonly()) {
			this.maturityDate.setConstraint(setNotEmpty("label_TreasuaryFinHeaderDialog_MaturityDate.value"));
		}

		if (!this.finAmount.isReadonly()){
			this.finAmount.setConstraint(new AmountValidator(18, ccyFormat, Labels.getLabel("label_InvestmentDealDialog_PrincipalAmt.value"), false));
		}

		if (!this.repayProfitRate.isReadonly()){
			this.repayProfitRate.setConstraint(new RateValidator(13, 9, Labels.getLabel("label_InvestmentDealDialog_RatePerc.value"), true));
		}

		logger.debug("Leaving");
	}


	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");

		if (!this.lovDescCustCIF.isReadonly()){
			this.lovDescCustCIF.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_InvestmentDealDialog_CounterParty.value")}));
		}
		if (this.finBranch.isButtonVisible()){
			this.finBranch.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_InvestmentDealDialog_FinBranch.value")}));
		}
		if (this.finType.isButtonVisible()){
			this.finType.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_InvestmentDealDialog_FinType.value")}));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.startDate.setConstraint("");	
		this.maturityDate.setConstraint("");		
		this.finAmount.setConstraint("");		
		this.repayProfitRate.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.lovDescCustCIF.setConstraint("");
		this.finType.setConstraint("");
		this.finBranch.setConstraint("");
		logger.debug("Leaving");
	}


	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */

	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		ccyFormat = getInvestmentFinHeader().getLovDescFinFormatter();

		this.finBranch.setMaxlength(8);
        this.finBranch.setMandatoryStyle(true);
		this.finBranch.setModuleName("Branch");
		this.finBranch.setValueColumn("BranchCode");
		this.finBranch.setDescColumn("BranchDesc");
		this.finBranch.setValidateColumns(new String[] { "BranchCode" });
		
		this.finType.setMaxlength(8);
		this.finType.setMandatoryStyle(true);
		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("FinType");
		this.finType.setDescColumn("FinTypeDesc");
		this.finType.setValidateColumns(new String[] { "FinType" });
		Filter[] filtersFinType = new Filter[1];
		filtersFinType[0] = new Filter("FinDivision", PennantConstants.FIN_DIVISION_TREASURY, Filter.OP_LIKE);
		this.finType.setFilters(filtersFinType);
         
		
		this.finAmount.setMandatory(true);
		this.finAmount.setMaxlength(18);
		this.finAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.finAmount.setScale(ccyFormat);

		this.finAmount_Two.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.finAmount_Two.setScale(ccyFormat);

		this.totPrinAmt.setMaxlength(18);
		this.totPrinAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.totPrinAmt.setScale(ccyFormat);

		this.prinInvested.setMaxlength(18);
		this.prinInvested.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.prinInvested.setScale(ccyFormat);

		this.prinDueToInvest.setMaxlength(18);	
		this.prinDueToInvest.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.prinDueToInvest.setScale(ccyFormat);

		this.prinMaturity.setMaxlength(18);
		this.prinMaturity.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.prinMaturity.setScale(ccyFormat);

		this.totalRepayAmt.setMaxlength(18);
		this.totalRepayAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.totalRepayAmt.setScale(ccyFormat);
		
		this.avgPftRate.setMaxlength(13);
		this.avgPftRate.setFormat(PennantApplicationUtil.getRateFormate(9));

		this.repayProfitRate.setMaxlength(13);
		this.repayProfitRate.setFormat(PennantApplicationUtil.getRateFormate(9));
		
		this.startDate.setFormat(PennantConstants.dateFormat);
		this.maturityDate.setFormat(PennantConstants.dateFormat);
	
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		
		readOnlyComponent(true, this.invStartDate);
		readOnlyComponent(true, this.invMaturityDate);
		readOnlyComponent(true, this.profitDaysBasis);
		readOnlyComponent(true, this.avgPftRate);
		readOnlyComponent(isReadOnly("InvestmentDealDialog_FinBranch"), this.finBranch);

		this.lovDescCustCIF.setReadonly(isReadOnly("InvestmentDealDialog_CustCif"));
		this.btnSearchCustCIF.setVisible(!isReadOnly("InvestmentDealDialog_CustCif"));
		readOnlyComponent(isReadOnly("InvestmentDealDialog_CustCif"), this.custID);

		readOnlyComponent(isReadOnly("InvestmentDealDialog_FinStartDate"), this.startDate);
		readOnlyComponent(isReadOnly("InvestmentDealDialog_FinMaturityDate"), this.maturityDate);

		this.finAmount.setReadonly(isReadOnly("InvestmentDealDialog_FinAmount"));
		readOnlyComponent(isReadOnly("InvestmentDealDialog_ProfitRate"), this.repayProfitRate);
		
		if (isNewRecord()) {
			if (isNewInvestment()) {
				this.btnCancel.setVisible(false);
			}
		} else {
			this.btnCancel.setVisible(true);
			this.finType.setReadonly(true);
		}

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.investmentFinHeader.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isNewInvestment());
			}

		}else{

			if(isNewInvestment()){
				if("ENQ".equals(this.moduleType)){
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
				}else if (isNewRecord()){
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				}else{
					this.btnCtrl.setWFBtnStatus_Edit(isNewInvestment());
				}
			}else{
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}
	

	public boolean isReadOnly(String componentName){
		if (isWorkFlowEnabled() || isNewInvestment()){
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}


	private void doClearMessage() {
		logger.debug("Entering");
		this.finAmount.setErrorMessage("");
		this.repayProfitRate.setErrorMessage("");
		this.lovDescCustCIF.setErrorMessage("");
		this.finBranch.setErrorMessage("");
		this.finType.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		
		this.custID.setReadonly(true);
		readOnlyComponent(true, this.invStartDate);
		readOnlyComponent(true, this.invMaturityDate);
		readOnlyComponent(true, this.profitDaysBasis);

		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if(isWorkFlowEnabled()){
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.finAmount.setValue("");
		this.repayProfitRate.setValue("");
		this.finType.setDescription("");
		this.finBranch.setDescription("");
		this.lovDescCustCIF.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws Exception {
		logger.debug("Entering");

		final FinanceDetail aFinanceDetail = new FinanceDetail();
		BeanUtils.copyProperties(getFinanceDetail(), aFinanceDetail);
		boolean isNew = aFinanceDetail.isNew();
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		
		// fill the DocumentDetails object with the components data
		getTreasuaryFinanceService().setFinanceDetails(aFinanceDetail, "DOCUMENTS", getRole());
		getTreasuaryFinanceService().setFinanceDetails(aFinanceDetail, "AGGREMENTS", getRole());
		doWriteComponentsToBean(aFinanceDetail);
		
		//Service level validations 
		String tempRecordStatus = aFinanceDetail.getFinScheduleData().getFinanceMain().getRecordType();

		//Finance Asset Loan Details Tab
		if (childWindow != null) {
			doSave_Assets(aFinanceDetail, isNew, tempRecordStatus, true);
		}

		//Finance CheckList Details Tab
		if (checkListChildWindow != null) {
			boolean validationSuccess = doSave_CheckList(aFinanceDetail);
			if(!validationSuccess){
				return;
			}
		} else {
			aFinanceDetail.setFinanceCheckList(null);
		}

		//Document Details Saving
		if(getDocumentDetailDialogCtrl() != null){
			aFinanceDetail.setDocumentDetailsList(getDocumentDetailDialogCtrl().getDocumentDetailsList());
		}

		ErrorDetails errorDetail = null;
		errorDetail = getTreasuaryFinanceService().investmentDealValidations(aFinanceDetail, 
				getInvestmentFinHeader(), getUserWorkspace().getUserLanguage());

		if (errorDetail != null) {
			try {
				PTMessageUtils.showErrorMessage(errorDetail.getError());
				return ;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else{
			
			// Write the additional validations as per below example
			// get the selected branch object from the listBox
			// Do data level validations here
			FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
			isNew = aFinanceMain.isNew();
			String tranType = "";

			if (isWorkFlowEnabled()) {
				tranType = PennantConstants.TRAN_WF;
				if (StringUtils.trimToEmpty(aFinanceMain.getRecordType()).equals("")) {
					aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
					if (isNew) {
						aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else {
						aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						aFinanceMain.setNewRecord(true);
					}
				}
			} else {
				
				if(isNewInvestment()){
					if (isNewRecord()) {
						aFinanceMain.setVersion(1);
						aFinanceMain.setRecordType(PennantConstants.RCD_ADD);
					} else {
						tranType = PennantConstants.TRAN_UPD;
					}

					if (StringUtils.trimToEmpty(aFinanceMain.getRecordType()).equals("")) {
						aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
						aFinanceMain.setRecordType(PennantConstants.RCD_UPD);
					}

					if (aFinanceMain.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
						tranType = PennantConstants.TRAN_ADD;
					} else if (aFinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						tranType = PennantConstants.TRAN_UPD;
					}

				}else{
					aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
					if (isNew) {
						tranType = PennantConstants.TRAN_ADD;
					} else {
						tranType = PennantConstants.TRAN_UPD;
					}
				}
			}
			
			// save it to database
			try {
				if (isNewInvestment()) {
					AuditHeader auditHeader = newDealProcess(aFinanceDetail, tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_InvestmentDealDialogCtrl, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
						getTreasuaryFinHeaderDialogCtrl().doFillTicketDetails(finDetailList);
						// true;
						// send the data back to customer

						//Customer Notification for Role Identification
						if(!StringUtils.trimToEmpty(aFinanceMain.getNextTaskId()).equals("")){
							String msg = PennantApplicationUtil.getSavingStatus(aFinanceMain.getRoleCode(),aFinanceMain.getNextRoleCode(), 
									aFinanceMain.getFinReference(), Labels.getLabel("label_TreasuaryFinance_Deal"), aFinanceMain.getRecordStatus());
							Clients.showNotification(msg,  "info", null, null, -1);
						}
					}
				}
				closeWindow();
			} catch (final DataAccessException e) {
				logger.error(e);
				showErrorMessage(this.window_InvestmentDealDialogCtrl, e);
			}
		}
		logger.debug("Leaving");
	}


	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private AuditHeader newDealProcess(FinanceDetail aFinanceDetail, String tranType) {
		logger.debug("Entering");
		
		boolean recordAdded=false;
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		AuditHeader auditHeader= getAuditHeader(aFinanceMain, tranType);
		finDetailList = new ArrayList<FinanceDetail>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aFinanceMain.getLovDescCustCIF());
		valueParm[1] = aFinanceMain.getLovDescCustShrtName();

		errParm[0] = PennantJavaUtil.getLabel("") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("") + ":"+valueParm[1];

		List<FinanceDetail> invDealList  = getTreasuaryFinHeaderDialogCtrl().getInvestmentFinHeader().getFinanceDetailsList();

		if(invDealList !=null && !invDealList.isEmpty()){
			for (int i = 0; i < invDealList.size(); i++) {
				FinanceDetail financeDetail = invDealList.get(i);
				FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
				if(aFinanceMain.getFinReference().equals(financeMain.getFinReference())){
					// Both Current and Existing list Finance References Same

					if(isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,valueParm), 
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if(tranType==PennantConstants.TRAN_DEL){
						if(aFinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							finDetailList.add(aFinanceDetail);
						}else if(aFinanceMain.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aFinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							finDetailList.add(aFinanceDetail);
						}else if(aFinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getTreasuaryFinHeaderDialogCtrl().getInvestmentFinHeaderList().size(); j++) {
								FinanceDetail detail = getTreasuaryFinHeaderDialogCtrl().getInvestmentFinHeader().getFinanceDetailsList().get(i);
								if(detail.getFinScheduleData().getFinanceMain().getFinReference() == aFinanceMain.getFinReference()){
									finDetailList.add(aFinanceDetail);
								}
							}
						}
					}else{
						if(tranType!=PennantConstants.TRAN_UPD){
							finDetailList.add(financeDetail);
						}
					}
				}else{
					finDetailList.add(financeDetail);
				}
			}
		}
		if(!recordAdded){
			finDetailList.add(aFinanceDetail);
		}
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Method to invoke data filling method for eligibility tab, Scoring tab,
	 * fee charges tab, accounting tab, agreements tab and additional field
	 * details tab.
	 * 
	 * @param aFinanceDetail
	 * @throws ParseException 
	 * @throws InterruptedException 
	 * 
	 */
	public void doFillTabs(FinanceDetail aFinanceDetail) throws ParseException, InterruptedException {
		logger.debug("Entering");
		setFinanceDetail(aFinanceDetail);
		appendCheckListDetailTab(true);
		appendRecommendDetailTab();
		appendDocumentDetailTab();
		appendtAgreementsDetailTab(true);
		logger.debug("Leaving");
	}

	public BigDecimal getTotalProfitAmt(BigDecimal finAmount, BigDecimal profitRate) {
		logger.debug("Entering");
		BigDecimal profitAmt = BigDecimal.ZERO;

		if (finAmount != null && profitRate != null) {
			InvestmentFinHeader investmentFinHeader = getInvestmentFinHeader();

			Date startDate = this.startDate.getValue();
			Date maturityDate = this.maturityDate.getValue();
			String pftDaysBasis = investmentFinHeader.getProfitDaysBasis();

			profitAmt = CalculationUtil.calInterest(startDate, maturityDate, finAmount, pftDaysBasis, profitRate);
			profitAmt = profitAmt.setScale(9, RoundingMode.HALF_UP);

			return profitAmt;
		}

		logger.debug("Leaving");
		return BigDecimal.ZERO;
	}

	private void doSetFieldType() {
		if (this.repayProfitRate.isReadonly()) {
			this.space_repayProfitRate.setSclass("");
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ GUI button Components ++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	/**
	 * when clicks on button "SearchFinType"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) {
		logger.debug("Entering " + event.toString());

		Object dataObject = ExtendedSearchListBox.show(this.window_InvestmentDealDialogCtrl, "Customer");
		if (dataObject instanceof String) {
			this.custID.setValue(Long.valueOf(0));
			this.lovDescCustCIF.setValue("");
			this.custShrtName.setValue("");
			this.finBranch.setValue(dataObject.toString());
			this.finBranch.setDescription("");
		} else {
			Customer details = (Customer) dataObject;
			if (details != null) {
				this.custID.setValue(details.getCustID());
				this.lovDescCustCIF.setValue(details.getCustCIF());
				this.custShrtName.setValue(details.getCustShrtName());
				this.finBranch.setValue(details.getCustDftBranch());
				this.finBranch.setDescription(details.getLovDescCustDftBranchName());
				getFinanceDetail().getFinScheduleData().getFinanceMain().setCustID(this.custID.getValue());
				getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescCustCIF(String.valueOf(details.getCustCIF()));
				setCustomerData(this.window_InvestmentDealDialogCtrl);
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	public void onClick$viewCustInfo(Event event){
		logger.debug("Entering " + event.toString());
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("custid", this.custID.longValue());
			map.put("finReference", this.investmentRef.getValue());
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/FinCustomerDetailsEnq.zul", window_InvestmentDealDialogCtrl, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * To set the customer id from Customer filter
	 * 
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void onChange$lovDescCustCIF(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		Customer customer = (Customer)PennantAppUtil.getCustomerObject(this.lovDescCustCIF.getValue(), null);

		if (customer != null) {
			this.custID.setValue(customer.getCustID());
			this.lovDescCustCIF.setValue(String.valueOf(customer.getCustCIF()));
			this.custShrtName.setValue(customer.getCustShrtName());
			this.finBranch.setValue(customer.getCustDftBranch());
			this.finBranch.setDescription(customer.getLovDescCustDftBranchName());
			getFinanceDetail().getFinScheduleData().getFinanceMain().setCustID(this.custID.getValue());
			getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescCustCIF(String.valueOf(customer.getCustCIF()));
			setCustomerData(this.window_InvestmentDealDialogCtrl);
		} else {
			this.custID.setValue(Long.valueOf(0));
			this.lovDescCustCIF.setValue("");
			this.custShrtName.setValue("");
			this.finBranch.setValue("");
			this.finBranch.setDescription("");
			throw new WrongValueException(this.lovDescCustCIF, Labels.getLabel("FIELD_NO_INVALID", 
					new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_CustID.value") }));
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onChange$repayProfitRate(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		setRepaymentAmt();		
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$finAmount(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		setRepaymentAmt();		
		logger.debug("Leaving" + event.toString());
	}

	private void onChangeDealDates(ArrayList<WrongValueException> wve) {
		logger.debug("Entering");
		
		doSetValidation();
		doSetLOVValidation();
		FinanceMain tempFM = new FinanceMain();
		tempFM.setFinStartDate(this.startDate.getValue());
		tempFM.setMaturityDate(this.maturityDate.getValue());
		validateDates(tempFM, wve);

		doRemoveValidation();
		doRemoveLOVValidation();
		tempFM = null;
		
		logger.debug("Leaving");
	}

	public void onChange$startDate(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		onChangeDealDates(wve);
		if(!wve.isEmpty()) {
			showErrorDetails(wve);
			return;
		}

		setRepaymentAmt();
		logger.debug("Leaving" + event.toString());
	}

	public void onChange$maturityDate(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		onChangeDealDates(wve);
		if(!wve.isEmpty()) {
			showErrorDetails(wve);
			return;
		}

		setRepaymentAmt();
		logger.debug("Leaving" + event.toString());
	}

	private void setRepaymentAmt() {
		logger.debug("Entering");
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		onChangeDealDates(wve);
		if(!wve.isEmpty()) {
			showErrorDetails(wve);
			return;
		}

		BigDecimal finAmount = PennantAppUtil.unFormateAmount(this.finAmount.getValue(), ccyFormat);
		BigDecimal repayProfitRate = this.repayProfitRate.getValue();
		BigDecimal totalRepayAmt = BigDecimal.ZERO;

		BigDecimal totalProfit = getTotalProfitAmt(finAmount, repayProfitRate);		
		totalRepayAmt = finAmount.add(totalProfit);

		getFinanceDetail().getFinScheduleData().getFinanceMain().setTotalProfit(totalProfit);
		this.totalRepayAmt.setValue(PennantApplicationUtil.formateAmount(totalRepayAmt, ccyFormat));
		setHeaderFields(finAmount, totalRepayAmt);
		
		logger.debug("Leaving");
	}

	private void setHeaderFields(BigDecimal finAmount, BigDecimal totalRepayAmt) {
		logger.debug("Entering");
		
		Date startDate = this.startDate.getValue();
		Date maturityDate = this.maturityDate.getValue();

		String profitDaysBasis = getInvestmentFinHeader().getProfitDaysBasis() ;

		BigDecimal totalPrincipalAmt = PennantAppUtil.unFormateAmount(this.totPrinAmt.getValue(), ccyFormat);
		BigDecimal principalAmt = oldVar_prinInvested.add(finAmount).subtract(oldVar_finAmount);
		BigDecimal prinDueToInvest = totalPrincipalAmt.subtract(principalAmt);
		BigDecimal maturityAmount =  oldVar_prinMaturity.add(totalRepayAmt).subtract(oldVar_totalRepayAmt);

		if (principalAmt.compareTo(totalPrincipalAmt) > 0) {
			throw new WrongValueException( this.finAmount, Labels.getLabel("label_Investment_Deals_TotPrinAmount_Exceed.value"));
		}

		this.prinInvested.setValue(PennantAppUtil.formateAmount(principalAmt, ccyFormat));
		this.prinDueToInvest.setValue(PennantAppUtil.formateAmount(prinDueToInvest, ccyFormat));		
		this.prinMaturity.setValue(PennantAppUtil.formateAmount(maturityAmount, ccyFormat));

		BigDecimal avgProfitRate = CalculationUtil.calcAvgProfitRate(startDate, maturityDate, profitDaysBasis, principalAmt, maturityAmount);
		this.avgPftRate.setValue(PennantApplicationUtil.formatRate(avgProfitRate.doubleValue(), 9));

		logger.debug("Leaving");
	}

	/**
	 * When user clicks on button "SearchFinType" button
	 * @param event
	 */
	public void onFulfill$finType(Event event){
		logger.debug("Entering " + event.toString());
		
		doResetFinProcessDetail();

		Object dataObject = finType.getObject();
		if (dataObject instanceof String){
			this.finType.setValue(dataObject.toString());
			this.finType.setDescription("");
		} else{
			FinanceType financeType = (FinanceType) dataObject;
			if (financeType != null) {
				FinScheduleData finScheduleData = getFinanceDetail().getFinScheduleData();
				this.finType.setValue(financeType.getFinType());
				this.finType.setDescription(financeType.getFinTypeDesc());

				finScheduleData.setFinanceType(financeType);
				finScheduleData.getFinanceMain().setFinType(financeType.getFinType());	
				finScheduleData.getFinanceMain().setLovDescFinTypeName(financeType.getFinTypeDesc());	
				finScheduleData.getFinanceMain().setLovDescAssetCodeName(financeType.getLovDescAssetCodeName());
				finScheduleData.getFinanceMain().setScheduleMethod(financeType.getFinSchdMthd());
				finScheduleData.getFinanceMain().setRepayRateBasis(financeType.getFinRateType());

				finScheduleData.setFinanceMain(finScheduleData.getFinanceMain(), financeType);

				getFinanceDetail().getFinScheduleData().getFinanceMain().setCustID(this.custID.getValue());
				
				if (getFinanceDetail().getFinScheduleData().getFinanceMain() != null) {
					getFinanceDetail().getFinScheduleData().getFinanceMain().setFinStsReason(PennantConstants.FINSTSRSN_SYSTEM);
					getFinanceDetail().getFinScheduleData().getFinanceMain().setFinStatus(getTreasuaryFinanceService().getCustStatusByMinDueDays());
				}
				setCustomerData(this.window_InvestmentDealDialogCtrl);
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on button "btnSearchRepayAcctId"
	 * 
	 * @param event
	 * @throws InterruptedException 
	 * @throws AccountNotFoundException
	 */
	public void onClick$btnSearchRepayAcctId(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		this.lovDescCustCIF.clearErrorMessage();
		this.repayAcctId.clearErrorMessage();

		if(!StringUtils.trimToEmpty(this.lovDescCustCIF.getValue()).equals("")) {
			Object dataObject;
 
			List<IAccounts> iAccountList = new ArrayList<IAccounts>();
			IAccounts iAccount = new IAccounts();
			iAccount.setAcCcy(this.finCcy.getValue());
			iAccount.setAcType("");
			iAccount.setDivision(getFinanceDetail().getFinScheduleData().getFinanceType().getFinDivision());

			iAccount.setAcCustCIF(this.lovDescCustCIF.getValue());
			try {
				iAccountList = getAccountInterfaceService().fetchExistAccountList(iAccount);
				dataObject = ExtendedSearchListBox.show(this.window_InvestmentDealDialogCtrl, "Accounts", iAccountList);
				if (dataObject instanceof String) {
					this.repayAcctId.setValue(dataObject.toString());
				} else {
					IAccounts details = (IAccounts) dataObject;
					if (details != null) {
						this.repayAcctId.setValue(details.getAccountId());
					}
				}
			} catch (Exception e) {
				logger.error(e);
				Messagebox.show("Account Details not Found!!!", Labels.getLabel("message.Error") , 
						Messagebox.ABORT, Messagebox.ERROR);
			}
		}else {
			throw new WrongValueException(this.lovDescCustCIF,Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_CustID.value") }));
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on button "btnSearchDisbAcctId"
	 * 
	 * @param event
	 * @throws InterruptedException 
	 * @throws AccountNotFoundException
	 */
	public void onClick$btnSearchDisbAcctId(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		this.lovDescCustCIF.clearErrorMessage();
		this.disbAcctId.clearErrorMessage();
		this.repayAcctId.clearErrorMessage();

		if(!StringUtils.trimToEmpty(this.lovDescCustCIF.getValue()).equals("")) {
			Object dataObject;

			List<IAccounts> iAccountList = new ArrayList<IAccounts>();
			IAccounts iAccount = new IAccounts();
			iAccount.setAcCcy(this.finCcy.getValue());
			iAccount.setAcType("");
			iAccount.setAcCustCIF(this.lovDescCustCIF.getValue());
			iAccount.setDivision(getFinanceDetail().getFinScheduleData().getFinanceType().getFinDivision());
			
			try {
				iAccountList = getAccountInterfaceService().fetchExistAccountList(iAccount);

				dataObject = ExtendedSearchListBox.show(this.window_InvestmentDealDialogCtrl, "Accounts", iAccountList);
				if (dataObject instanceof String) {
					this.disbAcctId.setValue(dataObject.toString());
					//this.disbAcctBal.setValue(getAcBalance(""));
				} else {
					IAccounts details = (IAccounts) dataObject;

					if (details != null) {
						this.disbAcctId.setValue(details.getAccountId());
						 //this.disbAcctBal.setValue(getAcBalance(details.getAccountId())); TODO

						if(StringUtils.trimToEmpty(this.repayAcctId.getValue()).equals("")){
							this.repayAcctId.setValue(details.getAccountId());
							//this.repayAcctBal.setValue(getAcBalance(details.getAccountId())); TODO
						}
					}
				}
			} catch (Exception e) {
				logger.error(e);
				Messagebox.show("Account Details not Found!!!", Labels.getLabel("message.Error") , 
						Messagebox.ABORT, Messagebox.ERROR);
			}
		}else {
			throw new WrongValueException(this.lovDescCustCIF,Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_CustID.value") }));
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method for Fetching Account Balance
	 * @param acId
	 * @return
	 */
	private String getAcBalance(String acId){
		if (!StringUtils.trimToEmpty(acId).equals("")) {
			return PennantAppUtil.amountFormate(getAccountInterfaceService().getAccountAvailableBal(acId), 
					getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
		}else{
			return "";
		}
	}
	
	public FinanceMain getFinanceMain(){
		FinanceMain financeMain=new FinanceMain();
		financeMain.setFinReference(StringUtils.trimToEmpty(this.dealTcktRef.getValue()));
		financeMain.setCustID(this.custID.longValue());
		financeMain.setLovDescCustCIF(this.lovDescCustCIF.getValue());
		financeMain.setLovDescCustShrtName(this.custShrtName.getValue());
		financeMain.setFinCcy(this.finCcy.getValue());
		financeMain.setLovDescFinFormatter(getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
		financeMain.setFinAmount(PennantAppUtil.unFormateAmount(this.finAmount.getValue(),getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
		financeMain.setFinStartDate(this.startDate.getValue());
		return financeMain;
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ WorkFlow Components+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	private AuditHeader getAuditHeader(FinanceMain aFinanceMain, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinanceMain.getBefImage(), aFinanceMain);
		return new AuditHeader(String.valueOf(aFinanceMain.getFinReference()), null, null, null, 
				auditDetail, aFinanceMain.getUserDetails(), getOverideMap());
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public InvestmentFinHeader getInvestmentFinHeader() {
		return investmentFinHeader;
	}
	public void setInvestmentFinHeader(InvestmentFinHeader investmentFinHeader) {
		this.investmentFinHeader = investmentFinHeader;
	}

	public TreasuaryFinHeaderDialogCtrl getTreasuaryFinHeaderDialogCtrl() {
		return treasuaryFinHeaderDialogCtrl;
	}
	public void setTreasuaryFinHeaderDialogCtrl(
			TreasuaryFinHeaderDialogCtrl treasuaryFinHeaderDialogCtrl) {
		this.treasuaryFinHeaderDialogCtrl = treasuaryFinHeaderDialogCtrl;
	}

	public boolean isValidationOn() {
		return validationOn;
	}
	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public TreasuaryFinanceService getTreasuaryFinanceService() {
		return this.treasuaryFinanceService;
	}
	public void setTreasuaryFinanceService(
			TreasuaryFinanceService treasuaryFinanceService) {
		this.treasuaryFinanceService = treasuaryFinanceService;
	}

	public void setFinDetailList(List<FinanceDetail> finDetailList) {
		this.finDetailList = finDetailList;
	}
	public List<FinanceDetail> getFinDetailList() {
		return finDetailList;
	}

}