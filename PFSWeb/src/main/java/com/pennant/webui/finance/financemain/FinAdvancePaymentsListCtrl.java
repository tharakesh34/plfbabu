/**
o * Copyright 2011 - Pennant Technologies
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
 * FileName    		:  FinAdvancePaymentsListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.webui.finance.payorderissue.DisbursementInstCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/FinAdvancePaymentsList.zul file.
 */
public class FinAdvancePaymentsListCtrl extends GFCBaseCtrl<FinAdvancePayments> {
	private static final long			serialVersionUID				= 4157448822555239535L;
	private static final Logger			logger							= Logger.getLogger(FinAdvancePaymentsListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window					window_FinAdvancePaymentsList;

	protected Button					btnNew_NewFinAdvancePayments;
	protected Label						label_AdvancePayments_Title;

	protected Listbox					listBoxAdvancePayments;

	// For Dynamically calling of this Controller
	private FinanceDetail				financedetail;
	private Object						financeMainDialogCtrl;
	private Component					parent							= null;
	private Tab							parentTab						= null;
	private transient boolean			recSave							= false;
	private String						roleCode						= "";
	private boolean						isEnquiry						= false;
	private transient boolean			newFinance;
	protected Groupbox					finBasicdetails;
	private FinBasicDetailsCtrl			finBasicDetailsCtrl;
	private String						ModuleType_Loan					= "LOAN";
	private List<FinAdvancePayments>	finAdvancePaymentsList	= new ArrayList<FinAdvancePayments>();
	private DisbursementInstCtrl		disbursementInstCtrl;
	private List<FinanceDisbursement>	financeDisbursement;
	private List<FinanceDisbursement>	approvedDisbursments;
	String moduleDefiner = "";
	
	/**
	 * default constructor.<br>
	 */
	public FinAdvancePaymentsListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinAdvancePaymentsList";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FinAdvancePayment object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_FinAdvancePaymentsList(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinAdvancePaymentsList);

		try {
			if (event.getTarget().getParent() != null) {
				parent = event.getTarget().getParent();
				//parent.setStyle("overflow:auto;");
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				setFinanceMainDialogCtrl((Object) arguments.get("financeMainDialogCtrl"));
				this.window_FinAdvancePaymentsList.setTitle("");
				setNewFinance(true);
			}

			if (arguments.containsKey("roleCode")) {
				roleCode = (String) arguments.get("roleCode");
				getUserWorkspace().allocateRoleAuthorities(roleCode, "FinAdvancePaymentsList");
			}

			if (arguments.containsKey("parentTab")) {
				parentTab = (Tab) arguments.get("parentTab");
			}

			if (arguments.containsKey("isEnquiry")) {
				isEnquiry = (Boolean) arguments.get("isEnquiry");
			}
			
			if (arguments.containsKey("approvedDisbursments")) {
				approvedDisbursments =  (List<FinanceDisbursement>) arguments.get("approvedDisbursments");
			}
			
			if (arguments.containsKey("moduleDefiner")) {
				moduleDefiner = (String) arguments.get("moduleDefiner");
			}


			if (arguments.containsKey("financeDetail")) {
				setFinancedetail((FinanceDetail) arguments.get("financeDetail"));
				if (getFinancedetail() != null) {
					if (getFinancedetail().getAdvancePaymentsList() != null) {
						setFinAdvancePaymentsList(getFinancedetail().getAdvancePaymentsList());
					}
				}
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");
				try {
					financeMainDialogCtrl.getClass().getMethod("setFinAdvancePaymentsListCtrl", this.getClass())
							.invoke(financeMainDialogCtrl, this);
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
				this.window_FinAdvancePaymentsList.setTitle("");
			}

			doEdit();
			doCheckRights();
			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	private FinanceDetail getFinancedetailsFromBase() {
		try {
			if (financeMainDialogCtrl != null) {
				return (FinanceDetail) financeMainDialogCtrl.getClass().getMethod("getFinanceDetail")
						.invoke(financeMainDialogCtrl);
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		return null;

	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}

	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("FinAdvancePaymentsList", roleCode);
		if (!StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_CANCELDISB)){			
			this.btnNew_NewFinAdvancePayments.setVisible(getUserWorkspace().isAllowed(
					"FinAdvancePaymentsList_NewFinAdvancePaymentsDetail"));
		}
		logger.debug("leaving");
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewFinance()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @throws InterruptedException
	 */
	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering");

		try {
			appendFinBasicDetails();
			doCheckEnquiry();
			FinanceMain finMain = getFinancedetail().getFinScheduleData().getFinanceMain();
			FinanceType financeType = getFinancedetail().getFinScheduleData().getFinanceType();
			disbursementInstCtrl.init(this.listBoxAdvancePayments,
					finMain.getFinCcy(), financeType.isAlwMultiPartyDisb(), roleCode);
			disbursementInstCtrl.setFinanceDisbursement(getFinancedetail().getFinScheduleData().getDisbursementDetails());
			disbursementInstCtrl.setFinanceMain(finMain);
			disbursementInstCtrl.setApprovedDisbursments(approvedDisbursments);
			
			doWriteBeanToComponents();

			this.listBoxAdvancePayments.setHeight(borderLayoutHeight - 226 + "px");
			if (parent != null) {
				this.window_FinAdvancePaymentsList.setHeight(borderLayoutHeight - 75 + "px");
				parent.appendChild(this.window_FinAdvancePaymentsList);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param commodityHeader
	 * 
	 */
	public void doWriteBeanToComponents() {
		logger.debug("Entering ");

		doFillFinAdvancePaymentsDetails(getFinAdvancePaymentsList());

		logger.debug("Leaving ");
	}

	private void doCheckEnquiry() {
		if (isEnquiry) {
			this.btnNew_NewFinAdvancePayments.setVisible(false);
		}
	}

	public boolean  onAdvancePaymentValidation(Map<String, Object> map) throws InterruptedException {
		logger.debug("Entering");

		boolean proceed=true;
		boolean isFinalStage=false;
		String userAction = "";
		FinanceDetail finDetail = null;

		if (map.containsKey("userAction")) {
			userAction = (String) map.get("userAction");
		}

		if (map.containsKey("financeDetail")) {
			finDetail = (FinanceDetail) map.get("financeDetail");
		}
		
		if (map.containsKey("isFinalStage")) {
			isFinalStage = (boolean) map.get("isFinalStage");
		}
		
		if (map.containsKey("moduleDefiner")) {
			moduleDefiner = (String) map.get("moduleDefiner");
		}

		recSave = false;
		if ("Save".equalsIgnoreCase(userAction) || "Cancel".equalsIgnoreCase(userAction)
				|| "Reject".equalsIgnoreCase(userAction) || "Resubmit".equalsIgnoreCase(userAction)) {
			recSave = true;
		}
		doClearMessage();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if (!recSave) {
				FinanceMain main = null;
				FinScheduleData schdData = null;
				if (finDetail != null) {
					schdData = finDetail.getFinScheduleData();
					main = schdData.getFinanceMain();
				}

				if (main != null && main.getFinAmount() != null) {
					disbursementInstCtrl.setFinanceDisbursement(schdData.getDisbursementDetails());
					disbursementInstCtrl.setFinanceMain(main);
					disbursementInstCtrl.markCancelIfNoDisbursmnetFound(getFinAdvancePaymentsList());
					boolean validate = getUserWorkspace().isAllowed("FinAdvancePaymentsList_NewFinAdvancePaymentsDetail") || isFinalStage;
					
					if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_CANCELDISB)) {
						validate=false;
					}
					
					List<ErrorDetail> valid = disbursementInstCtrl.validateOrgFinAdvancePayment(getFinAdvancePaymentsList(), validate);

					valid = ErrorUtil.getErrorDetails(valid, getUserWorkspace().getUserLanguage());

					if (valid != null && !valid.isEmpty()) {
						proceed=false;
						if (parentTab != null) {
							parentTab.setSelected(true);
						}
						for (ErrorDetail errorDetails : valid) {
							MessageUtil.showError(errorDetails.getError());
						}

					}
				}

			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		showErrorDetails(wve);

		if (finDetail != null && proceed) {

			Cloner cloner = new Cloner();
			List<FinAdvancePayments> finadvpaymentsList = cloner.deepClone(getFinAdvancePaymentsList());
			finDetail.setAdvancePaymentsList(finadvpaymentsList);
		}
		logger.debug("Leaving");
		return proceed;
	}

	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		logger.debug("Entering");

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	public void onClick$btnNew_NewFinAdvancePayments(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		Clients.clearWrongValue(this.btnNew_NewFinAdvancePayments);
		Clients.clearWrongValue(this.label_AdvancePayments_Title);
		FinanceDetail findetails = getFinancedetailsFromBase();
		financeDisbursement = findetails.getFinScheduleData().getDisbursementDetails();
		disbursementInstCtrl.setFinanceDisbursement(financeDisbursement);
		disbursementInstCtrl.setFinanceMain(findetails.getFinScheduleData().getFinanceMain());
		disbursementInstCtrl.setDocumentDetails(getDisbursmentDoc());
		disbursementInstCtrl.onClickNew(this, this.financeMainDialogCtrl, ModuleType_Loan, getFinAdvancePaymentsList());

		logger.debug("Leaving" + event.toString());
	}

	public void onFinAdvancePaymentsItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		Clients.clearWrongValue(this.btnNew_NewFinAdvancePayments);
		FinanceDetail findetails = getFinancedetailsFromBase();
		financeDisbursement = findetails.getFinScheduleData().getDisbursementDetails();
		disbursementInstCtrl.setFinanceDisbursement(financeDisbursement);
		disbursementInstCtrl.setFinanceMain(findetails.getFinScheduleData().getFinanceMain());
		disbursementInstCtrl.setDocumentDetails(getDisbursmentDoc());
		disbursementInstCtrl.onDoubleClick(this, this.financeMainDialogCtrl, ModuleType_Loan, isEnquiry);

		logger.debug("Leaving" + event.toString());
	}

	public void doFillFinAdvancePaymentsDetails(List<FinAdvancePayments> finAdvancePayDetails) {
		logger.debug("Entering");
		setFinAdvancePaymentsList(finAdvancePayDetails);
		disbursementInstCtrl.doFillFinAdvancePaymentsDetails(getFinAdvancePaymentsList());
		logger.debug("Leaving");
	}

	/**
	 * This method set the Advence Payment Detail to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 */
	public void doSave_AdvencePaymentDetail(FinanceDetail aFinanceDetail) {
		logger.debug("Entering ");
		if (getFinAdvancePaymentsList() != null && !this.getFinAdvancePaymentsList().isEmpty()) {
			aFinanceDetail.setAdvancePaymentsList(getFinAdvancePaymentsList());
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails() {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}

	}
	
	private DocumentDetails getDisbursmentDoc() throws Exception {
		logger.debug("Entering");

		if (financeMainDialogCtrl != null) {
			DocumentDetailDialogCtrl documentDetailDialogCtrl = (DocumentDetailDialogCtrl) financeMainDialogCtrl
					.getClass().getMethod("getDocumentDetailDialogCtrl").invoke(financeMainDialogCtrl);

			String document = SysParamUtil.getValueAsString("DISB_DOC");

			for (DocumentDetails details : documentDetailDialogCtrl.getDocumentDetailsList()) {
				if (StringUtils.equalsIgnoreCase(details.getDocCategory(), document)) {
					return details;
				}
			}
		}
		logger.debug("Leaving");
		return null;

	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public List<FinAdvancePayments> getFinAdvancePaymentsList() {
		return finAdvancePaymentsList;
	}

	public void setFinAdvancePaymentsList(List<FinAdvancePayments> finAdvancePaymentsList) {
		this.finAdvancePaymentsList = finAdvancePaymentsList;
	}

	public boolean isNewFinance() {
		return newFinance;
	}

	public void setNewFinance(boolean newFinance) {
		this.newFinance = newFinance;
	}

	public void setFinancedetail(FinanceDetail financedetail) {
		this.financedetail = financedetail;
	}

	public FinanceDetail getFinancedetail() {
		return financedetail;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}
	
	public void setDisbursementInstCtrl(DisbursementInstCtrl disbursementInstCtrl) {
		this.disbursementInstCtrl = disbursementInstCtrl;
	}

}
