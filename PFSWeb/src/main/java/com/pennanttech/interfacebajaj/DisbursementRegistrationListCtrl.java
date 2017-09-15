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
 * FileName    		:  DisbursementRegistrationListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-04-2017    														*
 *                                                                  						*
 * Modified Date    :  19-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-04-2017       Pennant	                 0.1                                            * 
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

package com.pennanttech.interfacebajaj;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Center;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.services.DisbursementRequestService;
import com.pennanttech.pff.core.util.DateUtil;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/Mandate/MandateRegistration.zul file.<br>
 * ************************************************************<br>
 * 
 */
public class DisbursementRegistrationListCtrl extends GFCBaseListCtrl<FinAdvancePayments> implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(DisbursementRegistrationListCtrl.class);

	protected Window window_DisbursementRegistrationList;
	protected Borderlayout borderLayout_DisbursementList;
	protected Paging pagingDisbursementList;
	protected Listbox listBoxDisbursementRegistration;

	protected Listheader listheader_Disbursement_DisbTypes;
	protected Listheader listheader_Disbursement_FinRef;
	protected Listheader listheader_Disbursement_FinType;
	protected Listheader listheader_Disbursement_Custname;
	protected Listheader listheader_Disbursement_BenName;
	protected Listheader listheader_Disbursement_BenAcctno;
	protected Listheader listheader_Disbursement_Branch;
	protected Listheader listheader_Disbursement_Channel;

	protected Combobox disbTypes;
	protected ExtendedCombobox partnerBank;
	protected Datebox fromDate;
	protected Datebox toDate;
	protected Uppercasebox finType;
	protected Space space_finType;
	protected ExtendedCombobox branch;
	protected Checkbox qdp;
	protected Combobox channelTypes;
	protected Textbox  finRef;

	protected Listbox sortOperator_DisbType;
	protected Listbox sortOperator_PartnerBank;
	protected Listbox sortOperator_FromDate;
	protected Listbox sortOperator_ToDate;
	protected Listbox sortOperator_FinType;
	protected Listbox sortOperator_Branch;
	protected Listbox sortOperator_Channel;
	protected Listbox sortOperator_FinRef;

	protected Listheader listHeader_CheckBox_Name;
	protected Listcell listCell_Checkbox;
	protected Listitem listItem_Checkbox;
	protected Checkbox listHeader_CheckBox_Comp;
	protected Checkbox list_CheckBox;

	protected Button button_Search;
	protected Button btnDownload;
	protected Button btnbranchDetails;
	protected int oldVar_sortOperator_finType;

	private Map<Long, FinAdvancePayments> disbursementMap = new HashMap<Long, FinAdvancePayments>();
	private ArrayList<ValueLabel> channelTypesList =  PennantStaticListUtil.getChannelTypes();
	private int futureDays =  0;;

	@Autowired
	private DisbursementRequestService disbursementRequestService;

	/**
	 * default constructor.<br>
	 */
	public DisbursementRegistrationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "DisbursementRegistration";
		super.pageRightName = "DisbursementRegistration";
		super.tableName = "INT_DISBURSEMENT_REQUEST_VIEW";
		super.queueTableName = "INT_DISBURSEMENT_REQUEST_VIEW";
		futureDays = Integer.valueOf((SysParamUtil.getValue("NO_FUTURE_DAYS_DISB_DOWNLOAD").toString()));
		
		// Future day's including current date
		futureDays = futureDays - 1;
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		Filter[] filter = new Filter[1];
		java.util.Date date = DateUtility.getAppDate();
		date = DateUtil.addDays(date,futureDays);
		filter[0] = new Filter("LLDATE", PennantAppUtil.formateDate(date, PennantConstants.DBDateFormat), Filter.OP_LESS_OR_EQUAL);
		this.searchObject.addFilters(filter);

		if (fromDate.getValue() != null) {
			String fromDate = PennantAppUtil.formateDate(this.fromDate.getValue(), PennantConstants.DBDateFormat);
			Filter[] filters = new Filter[1];
			filters[0] = new Filter("LLDATE", fromDate, Filter.OP_GREATER_OR_EQUAL);
			this.searchObject.addFilters(filters);
		}
		if (toDate.getValue() != null) {
			String toDate = PennantAppUtil.formateDate(this.toDate.getValue(), PennantConstants.DBDateFormat);
			Filter[] filters = new Filter[1];
			filters[0] = new Filter("LLDATE", toDate, Filter.OP_LESS_OR_EQUAL);
			this.searchObject.addFilters(filters);
		}
	}

	
	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_DisbursementRegistrationList(Event event) {
		// Set the page level components.
		setPageComponents(window_DisbursementRegistrationList, borderLayout_DisbursementList,
				listBoxDisbursementRegistration, pagingDisbursementList);
		setItemRender(new DisbursementListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_Search);
		registerField("paymentId");
		registerField("partnerBankID");
		registerField("branchDesc");
		registerField("alwFileDownload");

		registerField("partnerbankCode", partnerBank, SortOrder.NONE, sortOperator_PartnerBank, Operators.STRING);
		registerField("paymentType", listheader_Disbursement_DisbTypes, SortOrder.NONE, disbTypes,
				sortOperator_DisbType, Operators.STRING);
		registerField("finReference", listheader_Disbursement_FinRef, SortOrder.NONE, finRef, sortOperator_FinRef,
				Operators.STRING);
		registerField("finType", listheader_Disbursement_FinType, SortOrder.NONE, finType, sortOperator_FinType,
				Operators.MULTISELECT);
		registerField("custShrtName", listheader_Disbursement_Custname, SortOrder.NONE);
		registerField("beneficiaryName", listheader_Disbursement_BenName, SortOrder.NONE);
		registerField("beneficiaryAccNo", listheader_Disbursement_BenAcctno, SortOrder.NONE);
		registerField("branchCode", listheader_Disbursement_Branch, SortOrder.NONE, branch, sortOperator_Branch,
				Operators.STRING);
		registerField("AMTTOBERELEASED");
		registerField("channel", listheader_Disbursement_Channel, SortOrder.NONE, channelTypes, sortOperator_Channel,
				Operators.STRING);
		// Render the page and display the data.
		doRenderPage();
		this.disbursementMap.clear();
		doSetFieldProperties();

		if (listBoxDisbursementRegistration.getItems().size() > 0) {
			listHeader_CheckBox_Comp.setDisabled(false);
		} else {
			listHeader_CheckBox_Comp.setDisabled(true);
		}
	}
	
	
	/*public void onChange$toDate(Event event) {
		
		
		Date date = DateUtility.addDays(DateUtility.getAppDate(),futureDays);

		
		if (DateUtility.compare(this.toDate.getValue(), date) > 0) {
			this.toDate.setConstraint(new PTDateValidator("To Date",
					true, null, date, false));
		} 
		//doRemoveValidation();
		this.toDate.getValue();
	}*/
	
	
	public void onSelect$sortOperator_FinType(Event event) {
		this.oldVar_sortOperator_finType = doChangeStringOperator(sortOperator_FinType, oldVar_sortOperator_finType,
				this.finType);
	}

	/**
	 * On Change Search Operators resetting Data entered by User
	 * 
	 * @param listbox
	 * @param oldOperator
	 * @param textbox
	 * @return
	 */
	private int doChangeStringOperator(Listbox listbox, int oldOperator, Textbox textbox) {

		final Listitem item = listbox.getSelectedItem();
		final int searchOpId = Integer.parseInt(((ValueLabel) item.getAttribute("data")).getValue());

		if (oldOperator == Filter.OP_IN || oldOperator == Filter.OP_NOT_IN) {
			if (!(searchOpId == Filter.OP_IN || searchOpId == Filter.OP_NOT_IN)) {
				textbox.setValue("");
			}
		} else {
			if (searchOpId == Filter.OP_IN || searchOpId == Filter.OP_NOT_IN) {
				textbox.setValue("");
			}
		}
		return searchOpId;

	}
	
	public void onClick$btnbranchDetails(Event event) {
		logger.debug("Entering  " + event.toString());
		setSearchValue(sortOperator_FinType, this.finType, "FinanceType");
		logger.debug("Leaving" + event.toString());
		
	}
	

	private void doSetFieldProperties() {
		listItem_Checkbox = new Listitem();
		listCell_Checkbox = new Listcell();
		listHeader_CheckBox_Comp = new Checkbox();
		listCell_Checkbox.appendChild(listHeader_CheckBox_Comp);
		listHeader_CheckBox_Comp.addForward("onClick", self, "onClick_listHeaderCheckBox");
		listItem_Checkbox.appendChild(listCell_Checkbox);

		if (listHeader_CheckBox_Name.getChildren() != null) {
			listHeader_CheckBox_Name.getChildren().clear();
		}
		listHeader_CheckBox_Name.appendChild(listHeader_CheckBox_Comp);

		this.space_finType.setSclass(PennantConstants.mandateSclass);

		fillComboBox(this.disbTypes, "", PennantStaticListUtil.getPaymentTypes(false), "");
		fillComboBox(this.channelTypes, "",channelTypesList, "");

		this.partnerBank.setModuleName("PartnerBank");
		this.partnerBank.setDisplayStyle(2);
		this.partnerBank.setValueColumn("PartnerBankCode");
		this.partnerBank.setDescColumn("PartnerBankName");
		this.partnerBank.setValidateColumns(new String[] { "PartnerBankCode" });
		this.partnerBank.setMandatoryStyle(true);

		this.branch.setModuleName("BankBranch");
		this.branch.setDisplayStyle(2);
		this.branch.setValueColumn("BranchCode");
		this.branch.setDescColumn("BranchDesc");
		this.branch.setValidateColumns(new String[] { "BranchCode" });
	}

	/**
	 * Filling the MandateIdMap details and based on checked and unchecked events of listCellCheckBox.
	 */
	public void onClick_listHeaderCheckBox(ForwardEvent event) throws Exception {
		logger.debug(Literal.ENTERING);

		for (int i = 0; i < listBoxDisbursementRegistration.getItems().size(); i++) {
			Listitem listitem = listBoxDisbursementRegistration.getItems().get(i);
			Checkbox cb = (Checkbox) listitem.getChildren().get(0).getChildren().get(0);
			cb.setChecked(listHeader_CheckBox_Comp.isChecked());
		}

		if (listHeader_CheckBox_Comp.isChecked() && listBoxDisbursementRegistration.getItems().size() > 0) {
			disbursementMap = getDisbursementDetails();
		} else {
			disbursementMap.clear();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Filling the MandateIdMap details based on checked and unchecked events of listCellCheckBox.
	 */
	public void onClick_listCellCheckBox(ForwardEvent event) throws Exception {
		logger.debug(Literal.ENTERING);
		
		Checkbox checkBox = (Checkbox) event.getOrigin().getTarget();

		FinAdvancePayments advancePayments = (FinAdvancePayments) checkBox.getAttribute("finAdvancePayments");

		if (checkBox.isChecked()) {
			disbursementMap.put(advancePayments.getPaymentId(), advancePayments);
		} else {
			disbursementMap.remove(advancePayments.getPaymentId());
		}

		if (disbursementMap.size() == this.pagingDisbursementList.getTotalSize()) {
			listHeader_CheckBox_Comp.setChecked(true);
		} else {
			listHeader_CheckBox_Comp.setChecked(false);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Item renderer for listItems in the listBox.
	 */
	private class DisbursementListModelItemRenderer implements ListitemRenderer<FinAdvancePayments>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, FinAdvancePayments payments, int count) throws Exception {

			Listcell lc;

			lc = new Listcell();
			list_CheckBox = new Checkbox();
			list_CheckBox.setAttribute("finAdvancePayments", payments);
			list_CheckBox.addForward("onClick", self, "onClick_listCellCheckBox");
			lc.appendChild(list_CheckBox);
			if (listHeader_CheckBox_Comp.isChecked()) {
				list_CheckBox.setChecked(true);
			} else {
				list_CheckBox.setChecked(disbursementMap.containsKey(payments.getPaymentId()));
			}
			lc.setParent(item);

			lc = new Listcell(payments.getPaymentType());
			lc.setParent(item);

			lc = new Listcell(payments.getFinReference());
			lc.setParent(item);

			lc = new Listcell(payments.getFinType());
			lc.setParent(item);
			
			lc = new Listcell(PennantApplicationUtil.amountFormate(payments.getAmtToBeReleased().multiply(new BigDecimal(100)),
					CurrencyUtil.getFormat(payments.getDisbCCy())));
			lc.setParent(item);

			lc = new Listcell(payments.getCustShrtName());
			lc.setParent(item);

			lc = new Listcell(payments.getBeneficiaryName());
			lc.setParent(item);

			lc = new Listcell(payments.getBeneficiaryAccNo());
			lc.setParent(item);

			lc = new Listcell(payments.getBranchDesc());
			lc.setParent(item);
			
			lc = new Listcell(PennantStaticListUtil.getlabelDesc(payments.getChannel(), channelTypesList));
			 
			lc.setParent(item);

			item.setAttribute("finAdvancePayments", payments);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onDisbursementDoubleClicked");
		}
	}

	/**
	 * Getting the Disbursement List using JdbcSearchObject with search criteria..
	 */
	private Map<Long, FinAdvancePayments> getDisbursementDetails() {
		JdbcSearchObject<FinAdvancePayments> searchObject = new JdbcSearchObject<FinAdvancePayments>(FinAdvancePayments.class);
		
		searchObject.addField("paymentid");
		searchObject.addField("PaymentType");
		searchObject.addField("PartnerBankId");
		searchObject.addField("FinType");
		searchObject.addField("BranchCode");
		searchObject.addField("BranchDesc");
		searchObject.addField("PARTNERBANKCODE");
		searchObject.addField("alwFileDownload");
		searchObject.addField("finReference");
		searchObject.addTabelName(this.tableName);
		
		Filter[] filter = new Filter[1];
		java.util.Date date = DateUtility.getAppDate();
		date = DateUtil.addDays(date,futureDays);
		filter[0] = new Filter("LLDATE", PennantAppUtil.formateDate(date, PennantConstants.DBDateFormat), Filter.OP_LESS_OR_EQUAL);
		searchObject.addFilters(filter);
		
		

		if (fromDate.getValue() != null) {
			String fromDate = PennantAppUtil.formateDate(this.fromDate.getValue(), PennantConstants.DBDateFormat);
			Filter[] filters = new Filter[1];
			filters[0] = new Filter("LLDATE", fromDate, Filter.OP_GREATER_OR_EQUAL);
			searchObject.addFilters(filters);
		}
		if (toDate.getValue() != null) {
			String toDate = PennantAppUtil.formateDate(this.toDate.getValue(), PennantConstants.DBDateFormat);
			Filter[] filters = new Filter[1];
			filters[0] = new Filter("LLDATE", toDate, Filter.OP_LESS_OR_EQUAL);
			searchObject.addFilters(filters);
		}
		
		for (SearchFilterControl searchControl : searchControls) {
			Filter filters = searchControl.getFilter();
			if (filters != null) {
				searchObject.addFilter(filters);
			}
		}

		List<FinAdvancePayments> list = getPagedListWrapper().getPagedListService().getBySearchObject(searchObject);

		Map<Long, FinAdvancePayments> disbMap = new HashMap<Long, FinAdvancePayments>();
		
		if(list == null || list.isEmpty()) {
			return disbMap;
		}

		for (FinAdvancePayments disbursement : list) {
			disbMap.put(disbursement.getPaymentId(), disbursement);
		}
		
		return disbMap;
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_Search(Event event) {
		this.disbursementMap.clear();
		this.listHeader_CheckBox_Comp.setChecked(false);
		doSetValidations();
		// searchObject.clear();
		search();

		if (listBoxDisbursementRegistration.getItems().size() > 0) {
			listHeader_CheckBox_Comp.setDisabled(false);
		} else {
			listBoxDisbursementRegistration.setEmptyMessage(Labels.getLabel("listEmptyMessage.title"));
			listHeader_CheckBox_Comp.setDisabled(true);
		}
	}

	private void doSetValidations() {
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (!this.partnerBank.isReadonly())
				this.partnerBank.setConstraint(
						new PTStringValidator(Labels.getLabel("label_DisbursementList_PartnerBank.value"),
								PennantRegularExpressions.REGEX_DESCRIPTION, true));
			this.partnerBank.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.finType.isReadonly())
				this.finType
						.setConstraint(new PTStringValidator(Labels.getLabel("label_DisbursementList_LoanType.value"),
								PennantRegularExpressions.REGEX_DESCRIPTION, true));
			this.finType.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			Date date = DateUtility.addDays(DateUtility.getAppDate(), futureDays);
			if (DateUtility.compare(this.toDate.getValue(), date) > 0) {
				throw new WrongValueException(this.toDate, Labels.getLabel("DATE_ALLOWED_MAXDATE_EQUAL",
						new String[] { "To Date", DateUtility.formatToShortDate(date) }));
			}
			
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		if (this.fromDate.getValue() != null && this.toDate.getValue() != null) {
			if (this.fromDate.getValue().compareTo(this.toDate.getValue()) == 1) {
				throw new WrongValueException(this.toDate, "To date should be greater than or equal to From date.");
			}
		}
	}

	private void doRemoveValidation() {
		logger.debug("Entering ");
		this.partnerBank.setConstraint("");
		this.finType.setConstraint("");
		this.toDate.setConstraint("");

		logger.debug("Leaving ");
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		searchObject.clearFilters();
		this.fromDate.setValue(null);
		this.toDate.setValue(null);
		this.disbursementMap.clear();
		this.listHeader_CheckBox_Comp.setChecked(false);
		this.listheader_Disbursement_DisbTypes.setSort("none");
		this.listheader_Disbursement_FinRef.setSort("none");
		this.listheader_Disbursement_FinType.setSort("none");
		this.listheader_Disbursement_Custname.setSort("none");
		this.listheader_Disbursement_BenName.setSort("none");
		this.listheader_Disbursement_BenAcctno.setSort("none");
		this.listheader_Disbursement_Branch.setSort("none");
		this.listBoxDisbursementRegistration.getItems().clear();

		if (listBoxDisbursementRegistration.getItems().size() > 0) {
			listHeader_CheckBox_Comp.setDisabled(false);
		} else {
			listHeader_CheckBox_Comp.setDisabled(true);
		}
		this.pagingDisbursementList.setTotalSize(0);
	}

	/**
	 * when the "Upload" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onClick$btnDownload(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		List<FinAdvancePayments> disbushmentList;

		if (listHeader_CheckBox_Comp.isChecked()) {
			disbursementMap.clear();
			disbursementMap = getDisbursementDetails();
			disbushmentList = new ArrayList<FinAdvancePayments>(disbursementMap.values());
		} else {
			disbushmentList = new ArrayList<FinAdvancePayments>(disbursementMap.values());
		}

		if (listBoxDisbursementRegistration.getItems().size() > 0) {
			listHeader_CheckBox_Comp.setDisabled(false);
		} else {
			listHeader_CheckBox_Comp.setDisabled(true);
		}

		if (disbushmentList.isEmpty()) {
			MessageUtil.showError(Labels.getLabel("MandateDataList_NoEmpty"));
			return;
		}

		// Show a confirm box

		String msg = "You have selected " + this.disbursementMap.size() + " Disbursement(s) out of "
				+ this.pagingDisbursementList.getTotalSize() + ".\n Do you want to continue?";
		int conf = MessageUtil.confirm(msg);
		if (conf == MessageUtil.NO) {
			return;
		}
		try {
			btnDownload.setDisabled(true);
			button_Search.setDisabled(true);
			disbursementRequestService.sendReqest(this.finType.getValue(), disbushmentList, getUserWorkspace().getLoggedInUser().getLoginUsrID(), ((PartnerBank)partnerBank.getObject()).getFileName());
			
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("module", "DISBURSEMENT");
			
			MessageUtil.showMessage("File download process initiated.");
			createNewPage("/WEB-INF/pages/InterfaceBajaj/DisbursementFileDownloadList.zul", "menu_Item_DisbursementFileDownlaods", args);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}  finally {
			this.disbursementMap.clear();
			this.listHeader_CheckBox_Comp.setChecked(false);
			search();
			btnDownload.setDisabled(false);
			button_Search.setDisabled(false);
			logger.debug("Leaving");
		}
	}

	protected void createNewPage(String uri, String tabName, Map<String, Object> args) {
		final Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		final Center center = bl.getCenter();
		final Tabs tabs = (Tabs) center.getFellow("divCenter").getFellow("tabBoxIndexCenter")
				.getFellow("tabsIndexCenter");

		Tab tab = null;
		if (tabs.getFellowIfAny(tabName.trim().replace("menu_Item_", "tab_")) != null) {
			tab = (Tab) tabs.getFellow(tabName.trim().replace("menu_Item_", "tab_"));
			if (tab != null) {
				tab.close();
			}
		}
		tab = new Tab();
		tab.setId(tabName.trim().replace("menu_Item_", "tab_"));
		tab.setLabel(Labels.getLabel(tabName));
		tab.setClosable(true);
		tab.setParent(tabs);
		tab.setLabel("Disbursement File Control");

		final Tabpanels tabpanels = (Tabpanels) tabs.getFellow("tabpanelsBoxIndexCenter");
		final Tabpanel tabpanel = new Tabpanel();
		tabpanel.setHeight("100%");
		tabpanel.setStyle("padding: 0px;");
		tabpanel.setParent(tabpanels);

		Executions.createComponents(uri, tabpanel, args);
		tab.setSelected(true);
	}
}