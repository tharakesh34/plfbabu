package com.pennant.webui.finance.financemain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.CheckList;
import com.pennant.backend.model.customermasters.CustEmployeeDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.ScoringGroup;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.solutionfactory.DeviationParam;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.finance.FinanceDeviationsService;
import com.pennant.backend.util.DeviationConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.fusioncharts.ChartUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.dashboard.DashboardCreate;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pennapps.core.feature.ModuleUtil;

public class FinanceDeviationsDialogCtrl extends GFCBaseCtrl<FinanceDeviations> {
	private static final long			serialVersionUID	= 2290501784830847866L;
	private static final Logger			logger				= Logger.getLogger(FinanceDeviationsDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window					window_FinanceDeviationsDialog;											// autoWired
	protected Borderlayout				borderlayoutDeviationDetail;												// autoWired
	protected Div						divFianncedetails;
	protected Tab						tabDeviation;
	private int							ccyFormatter		= 0;
	// Tab 1
	protected Textbox					finCustSysref;
	protected Textbox					finCustBranch;
	protected Textbox					custCIF;
	protected Label						custName;
	protected Textbox					finCustIDTypeNo;
	protected Textbox					finCustEmployer;
	protected Textbox					finCustCYOE;
	protected Textbox					finCustAccNo;
	protected Textbox					finCustNationality;
	protected Textbox					finCustOccupation;
	protected Textbox					finCustEducation;
	protected Textbox					finCustPrvEmployer;
	protected Textbox					finCustPrvYOE;
	protected Textbox					finCustFDOB;
	protected Textbox					finCustFAge;
	protected Textbox					finCustSDOB;
	protected Textbox					finCustSAge;
	protected Textbox					finCustSector;
	protected Textbox					finCustSubSector;
	protected Textbox					finCustPhone;
	protected Textbox					finCustFax;
	protected Textbox					finCustMail;
	protected Textbox					finCustCPR;
	// Finance Details
	protected Textbox					finType;
	protected Textbox					finCcy;
	protected Label						finCcyDesc;
	protected Textbox					finDivison;
	protected Decimalbox				finAmount;
	protected Decimalbox				finDownPayBank;
	protected Decimalbox				finDownPaySupp;
	protected Decimalbox				finProfitRate;
	protected Intbox					numberOfterms;
	protected Textbox					finPurpose;

	protected Listbox					listBoxCustomerFinExposure;
	protected Listbox					listBoxFinElgRef;
	protected Listbox					listBoxRetailScoRef;
	protected Listbox					listBox_CheckList;
	// Tab 2
	protected Groupbox					finBasicdetails;
	protected Listbox					listBoxDeviationDetails;
	protected Listbox					listBoxApprovedDeviationDetails;

	private static final String			bold				= " font-weight: bold;";
	private static final String			boldAndRed			= "font-weight:bold;color:red;";

	private FinanceDetail				financeDetail		= null;

	List<DeviationParam>				eligibilitiesList	= PennantAppUtil.getDeviationParams();
	private FinanceDeviationsService	deviationDetailsService;
	private FinanceDeviationsListCtrl	financeDeviationsListCtrl;

	/**
	 * default constructor.<br>
	 */
	public FinanceDeviationsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "DeviationsDetailDialog";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceDeviationsDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinanceDeviationsDialog);

		try {

		// READ OVERHANDED parameters !
			if (arguments.containsKey("financeDetail")) {
				this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
				setFinanceDetail(financeDetail);
			}

			if (arguments.containsKey("financeDeviationsListCtrl")) {
				this.setFinanceDeviationsListCtrl((FinanceDeviationsListCtrl) arguments.get("financeDeviationsListCtrl"));
			}
			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_FinanceDeviationsDialog.onClose();
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * @param arrayList
	 */
	private void appendFinBasicDetails(ArrayList<Object> arrayList) {
		logger.debug(" Entering ");
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			if (arrayList != null) {
				map.put("finHeaderList", arrayList);
			}
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug(" Leaving ");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws Exception
	 */
	public void doShowDialog() throws Exception {
		logger.debug("Entering");
		try {

			appendFinBasicDetails(getFinBasicDetails(getFinanceDetail()));
			doWriteBeantoComponentsFiannceData(getFinanceDetail());

			// fill the components with the data
			doFillDeviationDetails(getFinanceDetail().getFinanceDeviations(), false, this.listBoxDeviationDetails);
			doFillDeviationDetails(getFinanceDetail().getApprovedFinanceDeviations(), true,
					this.listBoxApprovedDeviationDetails);

			int height = (borderLayoutHeight - 150) / 2;

			this.listBoxDeviationDetails.setHeight(height + "px");
			this.listBoxApprovedDeviationDetails.setHeight(height + "px");
			this.divFianncedetails.setHeight((borderLayoutHeight - 25) + "px");

			this.tabDeviation.setSelected(true);
			setDialog(DialogType.EMBEDDED);

		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private ArrayList<Object> getFinBasicDetails(FinanceDetail financeDetail) {
		logger.debug(" Entering ");

		FinanceMain main = financeDetail.getFinScheduleData().getFinanceMain();
		ArrayList<Object> arrayList = new ArrayList<Object>();
		arrayList.add(0, main.getFinType());
		arrayList.add(1, main.getFinCcy());
		arrayList.add(2, main.getScheduleMethod());
		arrayList.add(3, main.getFinReference());
		arrayList.add(4, main.getProfitDaysBasis());
		arrayList.add(5, main.getGrcPeriodEndDate());
		arrayList.add(6, main.isAllowGrcPeriod());
		if (StringUtils.isNotEmpty(getFinanceDetail().getFinScheduleData().getFinanceType().getProduct())) {
			arrayList.add(7, true);
		} else {
			arrayList.add(7, false);
		}
		arrayList.add(8, getFinanceDetail().getFinScheduleData().getFinanceType().getFinCategory());
		arrayList.add(9, getFinanceDetail().getCustomerDetails().getCustomer().getCustShrtName());
		arrayList.add(10, getFinanceDetail().getFinScheduleData().getFinanceMain().isNewRecord());
		arrayList.add(11, "");
		logger.debug(" Leaving ");
		return arrayList;
	}

	public void doFillDeviationDetails(List<FinanceDeviations> financeDeviations, boolean approved, Listbox listbox) {
		logger.debug("Entering");

		listbox.getItems().clear();
		if (financeDeviations == null || financeDeviations.isEmpty()) {
			return;
		}
		Collections.sort(financeDeviations, new CompareDeviation());

		String module = "";

		for (FinanceDeviations deviationDetail : financeDeviations) {
			//to show other deviation which in pending queue but should not be editable
			boolean pending = false;

			if (DeviationConstants.MULTIPLE_APPROVAL && !approved) {

				if (!getUserWorkspace().getUserRoles().contains(deviationDetail.getDelegationRole())) {
					pending = true;
				}
			}

			boolean deviationNotallowed = false;
			Listcell listcell;
			if (!module.equals(deviationDetail.getModule())) {
				module = deviationDetail.getModule();
				Listgroup listgroup = new Listgroup();
				listcell = new Listcell(Labels.getLabel("listGroup_" + deviationDetail.getModule()));
				listcell.setStyle(bold);
				listgroup.appendChild(listcell);
				listbox.appendChild(listgroup);
			}
			if (StringUtils.isEmpty(deviationDetail.getDelegationRole())) {
				deviationNotallowed = true;
			}

			Listitem listitem = new Listitem();

			String deviationCodedesc = getDeviationDesc(deviationDetail);
			listcell = getNewListCell(deviationCodedesc, deviationNotallowed);
			listitem.appendChild(listcell);
			listcell = getNewListCell(deviationDetail.getDeviationType(), deviationNotallowed);
			listitem.appendChild(listcell);
			listcell = getNewListCell(getDeviationValue(deviationDetail), deviationNotallowed);
			listitem.appendChild(listcell);
			listcell = getNewListCell(deviationDetail.getUserRole(), deviationNotallowed);
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantStaticListUtil.getlabelDesc(deviationDetail.getDelegationRole(),
					PennantAppUtil.getSecRolesList(null)));
			listitem.appendChild(listcell);
			listcell = getNewListCell(DateUtility.formatToShortDate(deviationDetail.getDeviationDate()),
					deviationNotallowed);
			listitem.appendChild(listcell);

			if (approved) {
				listcell = getNewListCell(
						PennantStaticListUtil.getlabelDesc(deviationDetail.getApprovalStatus(),
								PennantStaticListUtil.getApproveStatus()), deviationNotallowed);
			} else {
				listcell = getNewListCell("", deviationNotallowed);
				Combobox combobox = new Combobox();
				combobox.setReadonly(true);
				combobox.setWidth("100px");
				combobox.setId("combo_" + deviationDetail.getDeviationId());
				fillComboBox(combobox, deviationDetail.getApprovalStatus(), PennantStaticListUtil.getApproveStatus());
				combobox.setDisabled(pending);
				listcell.appendChild(combobox);
			}
			listitem.appendChild(listcell);
			listcell = getNewListCell("", deviationNotallowed);
			Button button = new Button();
			String lable = "";
			if (approved) {
				lable = "view";
				button.addForward("onClick", "", "onClickViewNotes", deviationDetail);
			} else {
				lable = "add";
				button.addForward("onClick", "", "onClickAddNotes", deviationDetail);
			}
			button.setLabel(lable);
			button.setDisabled(pending);
			listcell.appendChild(button);
			listitem.appendChild(listcell);

			listcell = getNewListCell(deviationDetail.getDeviationUserId(), deviationNotallowed);
			listitem.appendChild(listcell);
			listcell = getNewListCell(deviationDetail.getDelegatedUserId(), deviationNotallowed);
			listitem.appendChild(listcell);
			listitem.setAttribute("data", deviationDetail);
			listbox.appendChild(listitem);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to fill the combobox with given list of values
	 * 
	 * @param combobox
	 * @param value
	 * @param list
	 */
	public void fillComboBox(Combobox combobox, String value, List<ValueLabel> list) {
		logger.debug("Entering fillComboBox()");
		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.pending"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		combobox.setReadonly(true);
		for (ValueLabel valueLabel : list) {
			comboitem = new Comboitem();
			comboitem.setValue(valueLabel.getValue());
			comboitem.setLabel(valueLabel.getLabel());
			combobox.appendChild(comboitem);

			if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(valueLabel.getValue()))) {
				combobox.setSelectedItem(comboitem);
			}
		}
		logger.debug("Leaving fillComboBox()");
	}

	public void onClickAddNotes(ForwardEvent event) throws Exception {
		logger.debug(" Entering ");

		FinanceDeviations deviationDetail = (FinanceDeviations) event.getData();
		showNotes(deviationDetail, false);

		logger.debug(" Leaving ");

	}

	public void onClickViewNotes(ForwardEvent event) throws Exception {
		logger.debug(" Entering ");

		FinanceDeviations deviationDetail = (FinanceDeviations) event.getData();
		showNotes(deviationDetail, true);

		logger.debug(" Leaving ");
	}

	private void showNotes(FinanceDeviations deviationDetail, boolean enquiry) throws InterruptedException {
		logger.debug("Entering ");

		Notes notes = new Notes();
		notes.setModuleName(DeviationConstants.NOTES_MODULE);
		notes.setReference(getReference(deviationDetail));
		notes.setVersion(0);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", notes);
		map.put("control", this);
		if (enquiry) {
			map.put("enquiry", true);
		}

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving ");
	}

	private String getReference(FinanceDeviations deviations) {
		return deviations.getFinReference() + "_" + deviations.getDeviationId();
	}

	public String getDeviationDesc(FinanceDeviations deviationDetail) {
		logger.debug(" Entering ");

		String devCode = deviationDetail.getDeviationCode();

		if (DeviationConstants.TY_PRODUCT.equals(deviationDetail.getModule())) {

			logger.debug(" Leaving ");
			return getProlabelDesc(devCode, eligibilitiesList);

		} else if (DeviationConstants.TY_ELIGIBILITY.equals(deviationDetail.getModule())) {

			logger.debug(" Leaving ");
			return getRuleDesc(devCode, RuleConstants.MODULE_ELGRULE, null);

		} else if (DeviationConstants.TY_CHECKLIST.equals(deviationDetail.getModule())) {

			String temp = getChklabelDesc(devCode.substring(0, devCode.indexOf("_")));
			String cskDevType = devCode.substring(devCode.indexOf("_"));

			logger.debug(" Leaving ");
			return temp
					+ Labels.getLabel(
							"deviation_checklist",
							new String[] { PennantStaticListUtil.getlabelDesc(cskDevType,
									PennantStaticListUtil.getCheckListDeviationType()) });

		} else if (DeviationConstants.TY_FEE.equals(deviationDetail.getModule())) {

			logger.debug(" Leaving ");
			return getRuleDesc(null, RuleConstants.MODULE_FEES, devCode);

		} else if (DeviationConstants.TY_SCORE.equals(deviationDetail.getModule())) {

			logger.debug(" Leaving ");
			return getScoreinglabelDesc(devCode);
		}

		logger.debug(" Leaving ");
		return "";
	}

	public String getDeviationValue(FinanceDeviations deviationDetail) {
		logger.debug(" Entering ");

		String devType = deviationDetail.getDeviationType();
		String devValue = deviationDetail.getDeviationValue();
		
		int format = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		if (DeviationConstants.DT_BOOLEAN.equals(devType)) {

			logger.debug(" Leaving ");
			return devValue;

		} else if (DeviationConstants.DT_PERCENTAGE.equals(devType)) {

			logger.debug(" Leaving ");
			return devValue + " % ";

		} else if (DeviationConstants.DT_DECIMAL.equals(devType)) {

			BigDecimal amount = new BigDecimal(devValue);
			logger.debug(" Leaving ");
			return PennantAppUtil.amountFormate(amount, format);

		} else if (DeviationConstants.DT_INTEGER.equals(devType)) {

			BigDecimal amount = new BigDecimal(devValue);
			logger.debug(" Leaving ");
			return Integer.toString(amount.intValue());
		}

		logger.debug(" Leaving ");
		return "";
	}

	private Listcell getNewListCell(String val, boolean colrRed) {
		Listcell listcell = new Listcell(val);
		if (colrRed) {
			listcell.setStyle(boldAndRed);
		}
		return listcell;
	}

	class CompareDeviation implements Comparator<FinanceDeviations> {

		public CompareDeviation() {

		}

		@Override
		public int compare(FinanceDeviations o1, FinanceDeviations o2) {
			return o1.getModule().compareTo(o2.getModule());
		}

	}

	/**
	 * @param value
	 * @param deviationParamsList
	 * @return
	 */
	private String getProlabelDesc(String value, List<DeviationParam> deviationParamsList) {

		if (deviationParamsList != null && !deviationParamsList.isEmpty()) {
			for (DeviationParam param : deviationParamsList) {
				if (param.getCode().equals(value)) {
					return param.getDescription();
				}

			}
		}
		return "";
	}

	/**
	 * @param ruleid
	 * @param ruleModule
	 * @param rulecode
	 * @return
	 */
	private String getRuleDesc(String ruleid, String ruleModule, String rulecode) {

		logger.debug(" Entering ");

		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<Rule> searchObject = new JdbcSearchObject<Rule>(Rule.class);
		searchObject.addTabelName("Rules");

		if (!StringUtils.isEmpty(ruleid)) {
			searchObject.addFilterEqual("RuleId", ruleid);
		}
		if (!StringUtils.isEmpty(ruleModule)) {
			searchObject.addFilterEqual("RuleModule", ruleModule);
		}
		if (!StringUtils.isEmpty(rulecode)) {
			searchObject.addFilterEqual("RuleCode", rulecode);
		}

		List<Rule> list = pagedListService.getBySearchObject(searchObject);
		if (list != null && !list.isEmpty()) {

			logger.debug(" Leaving ");
			return list.get(0).getRuleCodeDesc();
		}

		logger.debug(" Leaving ");
		return "";
	}

	/**
	 * @param value
	 * @return
	 */
	private String getChklabelDesc(String value) {

		logger.debug(" Entering ");

		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<CheckList> searchObject = new JdbcSearchObject<CheckList>(CheckList.class);
		searchObject.addTabelName("BMTCheckList");
		searchObject.addFilterIn("CheckListId", value);

		List<CheckList> list = pagedListService.getBySearchObject(searchObject);
		if (list != null && !list.isEmpty()) {

			logger.debug(" Leaving ");
			return list.get(0).getCheckListDesc();
		}

		logger.debug(" Leaving ");
		return "";
	}

	/**
	 * @param value
	 * @return
	 */
	private String getScoreinglabelDesc(String value) {

		logger.debug(" Entering ");

		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<ScoringGroup> searchObject = new JdbcSearchObject<ScoringGroup>(ScoringGroup.class);
		searchObject.addTabelName("RMTScoringGroup");
		searchObject.addFilterIn("ScoreGroupId", value);

		List<ScoringGroup> list = pagedListService.getBySearchObject(searchObject);
		if (list != null && !list.isEmpty()) {

			logger.debug(" Leaving ");
			return list.get(0).getScoreGroupName();
		}

		logger.debug(" Leaving ");
		return "";
	}

	public void onClick$btnSave() {
		logger.debug(" Entering ");
		doSave();

		logger.debug(" Leaving ");

	}

	private void doSave() {
		logger.debug(" Entering ");

		List<FinanceDeviations> list = doWriteComponentsToBean();
		getDeviationDetailsService().processApproval(list, getAuditHeader(this.finCustSysref.getValue()),
				this.finCustSysref.getValue());
		//get the update finance main
		FinanceMain finmain = getDeviationDetailsService().getFinanceMain(this.finCustSysref.getValue());
		//Show status after moved from delegation approval queue. 
		if (finmain != null && !finmain.isDeviationApproval()) {
			String msg = PennantApplicationUtil.getSavingStatus(finmain.getRoleCode(), finmain.getNextRoleCode(),
					finmain.getFinReference(), " Finance ", finmain.getRecordStatus(), finmain.getNextUserId());
			Clients.showNotification(msg, "info", null, null, -1);
		}
		refreshList();
		closeDialog();
		logger.debug(" Leaving ");

	}

	private void refreshList() {
		getFinanceDeviationsListCtrl().search();
	}

	private List<FinanceDeviations> doWriteComponentsToBean() {
		logger.debug(" Entering ");

		List<FinanceDeviations> financeDeviations = new ArrayList<FinanceDeviations>();
		List<Listitem> list = this.listBoxDeviationDetails.getItems();
		for (Listitem listitem : list) {
			if (listitem instanceof Listgroup) {
				continue;
			}
			FinanceDeviations deviationDetail = (FinanceDeviations) listitem.getAttribute("data");
			Component component = listitem.getFellowIfAny("combo_" + deviationDetail.getDeviationId());
			if (component != null && component instanceof Combobox) {
				Combobox combobox = (Combobox) component;
				if (combobox.getSelectedItem() != null
						&& !combobox.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)) {
					deviationDetail.setApprovalStatus(combobox.getSelectedItem().getValue().toString());
					deviationDetail.setDelegatedUserId(String.valueOf(getUserWorkspace().getLoggedInUser()
							.getUserId()));
				}
			}

			financeDeviations.add(deviationDetail);

		}

		logger.debug(" Leaving ");
		return financeDeviations;

	}

	public void onClick$btnClose(Event event) {
		logger.debug(" Entering ");

		closeDialog();

		logger.debug(" Leaving ");
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	public AuditHeader getAuditHeader(String finreference) {
		LoggedInUser userDetails = getUserWorkspace().getLoggedInUser();
		AuditHeader auditHeader = new AuditHeader();
		auditHeader.setAuditModule(ModuleUtil.getTableName(FinanceDeviations.class.getSimpleName()));
		auditHeader.setAuditReference(finreference);
		auditHeader.setAuditUsrId(userDetails.getUserId());
		auditHeader.setAuditBranchCode(userDetails.getBranchCode());
		auditHeader.setAuditDeptCode(userDetails.getDepartmentCode());
		auditHeader.setAuditSystemIP(userDetails.getIpAddress());
		auditHeader.setAuditSessionID(userDetails.getSessionId());
		auditHeader.setUsrLanguage(userDetails.getLanguage());
		return auditHeader;
	}

	/**
	 * @param financeDetail
	 */
	private void doWriteBeantoComponentsFiannceData(FinanceDetail financeDetail) {
		logger.debug(" Entering ");

		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinanceType type = financeDetail.getFinScheduleData().getFinanceType();
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		Customer customer = customerDetails.getCustomer();
		Date appldate = DateUtility.getAppDate();
		ccyFormatter = CurrencyUtil.getFormat(finMain.getFinCcy());

		this.finCustSysref.setValue(finMain.getFinReference());
		this.finCustBranch.setValue(customer.getCustDftBranch() + "-" + customer.getLovDescCustDftBranchName());
		this.custCIF.setValue(customer.getCustCIF() + "-" + customer.getCustShrtName());
		// this.custName.setValue(customer.getCustShrtName());
		this.finCustIDTypeNo.setValue("Passport" + "-" + customer.getCustPassportNo());// Passport-03
		this.finCustAccNo.setValue(finMain.getDownPayAccount());
		this.finCustNationality
				.setValue(customer.getCustNationality() + "-" + customer.getLovDescCustNationalityName());
		this.finCustOccupation.setValue(customer.getLovDescCustEmpStsName());

		this.finCustCPR.setValue(customer.getCustCRCPR());
		if (customer.getCustDOB() != null) {
			this.finCustFDOB.setValue(DateUtility.formatToShortDate(customer.getCustDOB()));
			this.finCustFAge.setValue(String.valueOf(DateUtility.getYearsBetween(customer.getCustDOB(), appldate)));
		}
		// Customer Employment
		CustEmployeeDetail empdet = customerDetails.getCustEmployeeDetail();
		if(empdet != null){
			this.finCustEmployer.setValue(empdet.getLovDescEmpName());
			int custYearOfExp = DateUtility.getYearsBetween(empdet.getEmpFrom(), appldate);
			this.finCustCYOE.setValue(Integer.toString(custYearOfExp));
		}

		// Customer Email
		List<CustomerEMail> emailList = customerDetails.getCustomerEMailList();

		if (emailList != null) {
			for (CustomerEMail customerEMail : emailList) {
				this.finCustMail.setValue(customerEMail.getCustEMail());
			}
		}

		// Customer Phone Number
		List<CustomerPhoneNumber> phoneList = customerDetails.getCustomerPhoneNumList();

		if (phoneList != null && !phoneList.isEmpty()) {
			CustomerPhoneNumber customerPhoneNumber = phoneList.get(0);
			this.finCustPhone.setValue(PennantApplicationUtil.formatPhoneNumber(
					customerPhoneNumber.getPhoneCountryCode(), customerPhoneNumber.getPhoneAreaCode(),
					customerPhoneNumber.getPhoneNumber()));

		}
		this.finCustEducation.setValue("");
		// Finance Details

		this.finType.setValue(type.getFinType() + "-" + type.getFinTypeDesc());
		this.finCcy.setValue(finMain.getFinCcy());
		// this.finCcyDesc.setValue(main.getLovDescFinCcyName());
		this.finDivison.setValue(type.getFinDivision() + " - " + type.getLovDescFinDivisionName());
		this.finAmount.setValue(PennantAppUtil.formateAmount(finMain.getFinAmount(), ccyFormatter));
		this.finDownPayBank.setValue(PennantAppUtil.formateAmount(finMain.getDownPayBank(), ccyFormatter));
		this.finDownPaySupp.setValue(PennantAppUtil.formateAmount(finMain.getDownPaySupl(), ccyFormatter));
		this.finProfitRate.setValue(finMain.getRepayProfitRate());
		this.numberOfterms.setValue(finMain.getNumberOfTerms());
		this.finPurpose.setValue(finMain.getFinPurpose());

		doFillCustFinanceExposureDetails(customerDetails.getCustFinanceExposureList());

		// Set Eligibility based on deviations and rule result
		List<FinanceEligibilityDetail> eligibilityRuleList = financeDetail.getElgRuleList();
		for (FinanceEligibilityDetail financeEligibilityDetail : eligibilityRuleList) {
			setStatusByDevaition(financeEligibilityDetail);
		}

		doFillFInEligibilityDetails(eligibilityRuleList);

		doFillCheckListdetails(financeDetail);

		doFillScoringdetails(financeDetail.getFinScoreHeaderList(), financeDetail.getScoreDetailListMap());
		
		createDashboards();

		logger.debug(" Entering ");
	}

	/**
	 * @param custFinanceExposureDetails
	 */
	private void doFillCustFinanceExposureDetails(List<FinanceEnquiry> custFinanceExposureDetails) {

		logger.debug(" Entering ");

		this.listBoxCustomerFinExposure.getItems().clear();
		if (custFinanceExposureDetails != null) {
			for (FinanceEnquiry finEnquiry : custFinanceExposureDetails) {
				Listitem item = new Listitem();
				Listcell lc = new Listcell(DateUtility.formatToLongDate(finEnquiry.getFinStartDate()));
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getFinType());
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getFinReference());
				lc.setParent(item);
				BigDecimal totAmt = finEnquiry.getFinAmount().subtract(finEnquiry.getDownPayment().add(finEnquiry.getFeeChargeAmt().add(finEnquiry.getInsuranceAmt())));
				lc = new Listcell(PennantAppUtil.amountFormate(totAmt,
						CurrencyUtil.getFormat(finEnquiry.getFinCcy())));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				BigDecimal instAmt = BigDecimal.ZERO;
				if(finEnquiry.getNumberOfTerms() > 0){
					instAmt = totAmt.divide(
							new BigDecimal(finEnquiry.getNumberOfTerms()), 0, RoundingMode.HALF_DOWN);
				}
				lc = new Listcell(PennantApplicationUtil.amountFormate(instAmt, CurrencyUtil.getFormat(finEnquiry.getFinCcy())));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(totAmt.subtract(finEnquiry.getFinRepaymentAmount()),
						CurrencyUtil.getFormat(finEnquiry.getFinCcy())));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getFinStatus());
				lc.setParent(item);
				this.listBoxCustomerFinExposure.appendChild(item);
			}
		}

		logger.debug(" Leaving ");
	}

	/**
	 * Method for Rendering Executed Eligibility Details
	 * 
	 * @param eligibilityDetails
	 */
	private void doFillFInEligibilityDetails(List<FinanceEligibilityDetail> eligibilityDetails) {
		logger.debug("Entering");

		this.listBoxFinElgRef.getItems().clear();

		if (eligibilityDetails != null && !eligibilityDetails.isEmpty()) {
			for (FinanceEligibilityDetail detail : eligibilityDetails) {
				Listitem item = new Listitem();
				Listcell lc;

				// Rule Code
				lc = new Listcell(detail.getLovDescElgRuleCode());
				lc.setParent(item);

				// Rule Code Desc
				lc = new Listcell(detail.getLovDescElgRuleCodeDesc());
				lc.setParent(item);

				// If Rule Not Executed

				if (StringUtils.isEmpty(detail.getRuleResult())) {

					lc = new Listcell("");
					lc.setParent(item);

				} else {

					String labelCode = "";
					String StyleCode = "";

					if (detail.isEligible()) {
						if (detail.isEligibleWithDevaition()) {
							labelCode = Labels.getLabel("common.Eligible_Deviation");
						} else {
							labelCode = Labels.getLabel("common.Eligible");
						}
						StyleCode = "font-weight:bold;color:green;";

					} else {
						labelCode = Labels.getLabel("common.Ineligible");
						StyleCode = "font-weight:bold;color:red;";
					}

					if (RuleConstants.RETURNTYPE_DECIMAL.equals(detail.getRuleResultType())) {

						if (RuleConstants.ELGRULE_DSRCAL.equals(detail.getLovDescElgRuleCode())
								|| RuleConstants.ELGRULE_PDDSRCAL.equals(detail.getLovDescElgRuleCode())) {
							BigDecimal val = new BigDecimal(detail.getRuleResult());
							val = val.setScale(2, RoundingMode.HALF_DOWN);
							labelCode = String.valueOf(val) + "%";
						} else {
							labelCode = PennantAppUtil.amountFormate(new BigDecimal(detail.getRuleResult()),
									ccyFormatter);
						}
						StyleCode = "text-align:right;";
					}

					lc = new Listcell(labelCode);
					lc.setStyle(StyleCode);
					lc.setParent(item);

				}

				listBoxFinElgRef.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * @param finElgDet
	 */
	private void setStatusByDevaition(FinanceEligibilityDetail finElgDet) {
		logger.debug(" Entering ");

		FinanceDeviations deviation = null;

		List<FinanceDeviations> list = getFinanceDetail().getFinanceDeviations();
		if (list != null && !list.isEmpty()) {
			for (FinanceDeviations financeDeviations : list) {
				if (financeDeviations.getDeviationCode().equals(String.valueOf(finElgDet.getElgRuleCode()))) {
					deviation = financeDeviations;
					break;
				}
			}
		}
		if (deviation == null) {
			List<FinanceDeviations> approvedList = getFinanceDetail().getApprovedFinanceDeviations();
			if (approvedList != null && !approvedList.isEmpty()) {
				for (FinanceDeviations financeDeviations : approvedList) {
					if (financeDeviations.getDeviationCode().equals(String.valueOf(finElgDet.getElgRuleCode()))) {
						deviation = financeDeviations;
						break;
					}
				}
			}
		}

		if (deviation != null) {
			if ("".equals(deviation.getDelegationRole())) {
				finElgDet.setEligible(false);
			} else {
				finElgDet.setEligible(true);
				finElgDet.setEligibleWithDevaition(true);
			}

		} else {
			String ruleResult = StringUtils.trimToEmpty(finElgDet.getRuleResult());
			if ("0.0".equals(ruleResult) || "0.00".equals(ruleResult)) {
				finElgDet.setEligible(false);
			} else {
				finElgDet.setEligible(true);
			}

		}

		logger.debug(" Leaving ");
	}

	class CompareCheckList implements Comparator<FinanceCheckListReference> {

		public CompareCheckList() {

		}

		@Override
		public int compare(FinanceCheckListReference o1, FinanceCheckListReference o2) {
			return String.valueOf(o1.getQuestionId()).compareTo(String.valueOf(o2.getQuestionId()));
		}
	}

	/**
	 * @param financeDetail
	 */
	private void doFillCheckListdetails(FinanceDetail financeDetail) {
		logger.debug(" Entering ");

		List<FinanceCheckListReference> list = financeDetail.getFinanceCheckList();

		Collections.sort(list, new CompareCheckList());

		long Questionid = 0;
		for (FinanceCheckListReference finRefDetail : list) {
			if (Questionid != finRefDetail.getQuestionId()) {
				Questionid = finRefDetail.getQuestionId();
				Listgroup listgroup = new Listgroup();
				Listcell listcell;
				listcell = new Listcell(finRefDetail.getLovDescQuesDesc());
				listcell.setStyle(bold);
				listcell.setParent(listgroup);
				// Deviation Combobox
				FinanceDeviations devation = getDeviationValuesifnay(finRefDetail);

				if (devation != null) {
					String devCode = devation.getDeviationValue();
					int index = devCode.indexOf("_");
					String comboVal = devCode.substring(index);
					Listcell listCell = new Listcell();
					Combobox combobox = new Combobox();
					combobox.setWidth("100px");
					fillComboBox(combobox, StringUtils.trimToEmpty(comboVal),
							PennantStaticListUtil.getCheckListDeviationType(), "");
					combobox.setDisabled(true);
					listCell.appendChild(combobox);
					listCell.appendChild(new Space());
					if (!DeviationConstants.CL_WAIVED.equals(comboVal)) {
						Intbox intbox = new Intbox();
						intbox.setValue(Integer.parseInt(devation.getDeviationValue()));
						intbox.setWidth("50px");
						intbox.setDisabled(true);
						listCell.appendChild(intbox);
					}

					listgroup.appendChild(listCell);
				} else {
					listgroup.appendChild(new Listcell(""));
				}

				listgroup.appendChild(new Listcell(""));

				this.listBox_CheckList.appendChild(listgroup);

				Listitem listitem = new Listitem();
				listcell = new Listcell(finRefDetail.getLovDescAnswerDesc());
				listcell.setParent(listitem);
				listitem.appendChild(new Listcell(""));
				listitem.appendChild(new Listcell(""));
				this.listBox_CheckList.appendChild(listitem);
			}
		}

		logger.debug(" Leaving ");
	}

	/**
	 * @param list
	 * @param map
	 */
	private void doFillScoringdetails(List<FinanceScoreHeader> list, Map<Long, List<FinanceScoreDetail>> map) {
		logger.debug(" Entering ");

		this.listBoxRetailScoRef.getItems().clear();

		for (FinanceScoreHeader financeScoreHeader : list) {
			Listgroup listGroup = new Listgroup();
			Listcell listcell;

			listcell = new Listcell(financeScoreHeader.getGroupCode() + "-" + financeScoreHeader.getGroupCodeDesc());
			listcell.setStyle(bold);
			listcell.setParent(listGroup);

			listcell = new Listcell(PennantJavaUtil.concat(Labels.getLabel("label_MinScore"), " :",
					String.valueOf(financeScoreHeader.getMinScore())));
			listcell.setStyle(bold);
			listcell.setParent(listGroup);

			listcell = new Listcell("");
			listcell.setParent(listGroup);

			listcell = new Listcell("");
			listcell.setParent(listGroup);
			this.listBoxRetailScoRef.appendChild(listGroup);

			List<FinanceScoreDetail> dtlist = map.get(financeScoreHeader.getHeaderId());

			BigDecimal totScore = new BigDecimal(0);
			BigDecimal totExeScore = new BigDecimal(0);

			for (FinanceScoreDetail financeScoreDetail : dtlist) {
				Listitem listitem = new Listitem();

				listcell = new Listcell(financeScoreDetail.getRuleCode());
				listcell.setParent(listitem);

				listcell = new Listcell(financeScoreDetail.getRuleCodeDesc());
				listcell.setParent(listitem);

				totScore = totScore.add(financeScoreDetail.getMaxScore());
				listcell = new Listcell(PennantAppUtil.formatAmount(financeScoreDetail.getMaxScore(), 0, false));
				listcell.setParent(listitem);

				totExeScore = totExeScore.add(financeScoreDetail.getExecScore());
				listcell = new Listcell(PennantAppUtil.formatAmount(financeScoreDetail.getExecScore(), 0, false));
				listcell.setParent(listitem);
				this.listBoxRetailScoRef.appendChild(listitem);
			}
			// Total's
			Listitem listfoot = new Listitem();

			listcell = new Listcell();
			listcell.setParent(listfoot);

			listcell = new Listcell(Labels.getLabel("label_Credit_Worth") + " : " + financeScoreHeader.getCreditWorth());
			listcell.setStyle(bold);
			listcell.setParent(listfoot);

			listcell = new Listcell(PennantAppUtil.formatAmount(totScore, 0, false));
			listcell.setStyle(bold);
			listcell.setParent(listfoot);

			listcell = new Listcell(PennantAppUtil.formatAmount(totExeScore, 0, false));
			listcell.setStyle(bold);
			listcell.setParent(listfoot);
			this.listBoxRetailScoRef.appendChild(listfoot);
		}

		logger.debug(" Leaving ");

	}

	/**
	 * @param finRefDetail
	 * @return
	 */
	private FinanceDeviations getDeviationValuesifnay(FinanceCheckListReference finRefDetail) {
		logger.debug(" Entering ");

		List<FinanceDeviations> list = new ArrayList<>();
		list.addAll(getFinanceDetail().getFinanceDeviations());
		list.addAll(getFinanceDetail().getApprovedFinanceDeviations());

		if (!list.isEmpty()) {
			for (FinanceDeviations financeDeviations : list) {
				if (!financeDeviations.getModule().equals(DeviationConstants.TY_CHECKLIST)) {
					continue;
				}

				String devCode = financeDeviations.getDeviationCode();
				int index = devCode.indexOf("_");
				long devRef = Long.parseLong(devCode.substring(0, index));

				if (devRef == finRefDetail.getQuestionId()) {

					logger.debug(" Leaving ");
					return financeDeviations;
				}

			}
		}

		logger.debug(" Leaving ");
		return null;

	}

	private ChartUtil		chartUtil;
	public DashboardCreate	dashboardCreate;
	public Cell col_html;

	/**
	 * Create DashBoards
	 * 
	 * @param panelchildren
	 * @param info
	 */
	public void createDashboards() {
		chartUtil = new ChartUtil();
		JdbcSearchObject<DashboardConfiguration> jdbcSearchObject = new JdbcSearchObject<DashboardConfiguration>(DashboardConfiguration.class);
		PagedListService pagedListService=(PagedListService) SpringUtil.getBean("pagedListService");
		jdbcSearchObject.addFilterEqual("DashboardCode", "CCBYFINAMT");
		List<DashboardConfiguration> list = pagedListService.getBySearchObject(jdbcSearchObject);
		if (list!=null && !list.isEmpty()) {
			jdbcSearchObject.addFilterEqual("DashboardCode", list.get(0));
			ChartDetail chartDetail = dashboardCreate.getChartDetail(list.get(0));
			chartDetail.setChartId(list.get(0).getDashboardCode());
			chartDetail.setChartHeight("75%");
			chartDetail.setChartWidth("100%");
			chartDetail.setiFrameHeight("100%");
			chartDetail.setiFrameWidth("100%");
			dashboardCreate.setChartDetail(chartDetail);
			//Get HTML Content which renders fusion chart by calling chartUtil.getHtmlContent(chartDetail)
			col_html.appendChild(chartUtil.getHtmlContent(chartDetail));
		}

	}
	
	public ChartUtil getChartUtil() {
		return chartUtil;
	}

	public void setChartUtil(ChartUtil chartUtil) {
		this.chartUtil = chartUtil;
	}
	public DashboardCreate getDashboardCreate() {
		return dashboardCreate;
	}

	public void setDashboardCreate(DashboardCreate dashboardCreate) {
		this.dashboardCreate = dashboardCreate;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public FinanceDeviationsService getDeviationDetailsService() {
		return deviationDetailsService;
	}

	public void setDeviationDetailsService(FinanceDeviationsService deviationDetailsService) {
		this.deviationDetailsService = deviationDetailsService;
	}

	public FinanceDeviationsListCtrl getFinanceDeviationsListCtrl() {
		return financeDeviationsListCtrl;
	}

	public void setFinanceDeviationsListCtrl(FinanceDeviationsListCtrl financeDeviationsListCtrl) {
		this.financeDeviationsListCtrl = financeDeviationsListCtrl;
	}

}
