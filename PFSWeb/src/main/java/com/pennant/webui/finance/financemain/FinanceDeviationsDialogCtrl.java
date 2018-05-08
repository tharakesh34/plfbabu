package com.pennant.webui.finance.financemain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.delegationdeviation.DeviationHelper;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.FinanceDeviationsService;
import com.pennant.backend.util.DeviationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.delegationdeviation.DeviationRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class FinanceDeviationsDialogCtrl extends GFCBaseCtrl<FinanceDeviations> {
	private static final long			serialVersionUID	= 2290501784830847866L;
	private static final Logger			logger				= Logger.getLogger(FinanceDeviationsDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window					window_FinanceDeviationsDialog;												// autoWired
	protected Borderlayout				borderlayoutDeviationDetail;												// autoWired
	protected Tab						tabDeviation;
	// Tab 1
	protected Tabpanel					creditApprovalTabpanel;
	// Tab 2
	protected Groupbox					finBasicdetails;
	protected Listbox					listBoxDeviationDetails;
	protected Listbox					listBoxManualDeviations;

	private FinanceDetail				financeDetail		= null;

	private FinanceDeviationsService	deviationDetailsService;
	private FinanceDeviationsListCtrl	financeDeviationsListCtrl;
	@Autowired
	private DeviationRenderer			deviationRenderer;
	@Autowired
	private DeviationHelper deviationHelper;
	int									ccyformat			= 0;
	List<ValueLabel> delegators = new ArrayList<>();

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
				ccyformat = CurrencyUtil.getFormat(financeDetail.getFinScheduleData().getFinanceMain().getFinCcy());
				deviationHelper.getRoleAndDesc(financeDetail.getFinScheduleData().getFinanceMain().getWorkflowId());
			}
			deviationRenderer.init(getUserWorkspace(), ccyformat, true, false, delegators);

			if (arguments.containsKey("financeDeviationsListCtrl")) {
				this.setFinanceDeviationsListCtrl(
						(FinanceDeviationsListCtrl) arguments.get("financeDeviationsListCtrl"));
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
	 * @param arrayList
	 */
	private void appendCreditApprovalTab(FinanceDetail financeDetail) {
		logger.debug(" Entering ");
		try {

			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeDetail", financeDetail);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/CreditApprovalDialog.zul",
					this.creditApprovalTabpanel, map);
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
			appendCreditApprovalTab(getFinanceDetail());

			// fill the components with the data

			List<FinanceDeviations> autoDeviations = getFinanceDetail().getFinanceDeviations();
			List<FinanceDeviations> approvedAutoDeviations = getFinanceDetail().getApprovedFinanceDeviations();
			deviationRenderer.renderAutoDeviations(autoDeviations, approvedAutoDeviations,
					this.listBoxDeviationDetails);

			List<FinanceDeviations> manualDeviations = getFinanceDetail().getManualDeviations();
			List<FinanceDeviations> approvedManualDeviations = getFinanceDetail().getApprovedManualDeviations();
			deviationRenderer.setDescriptions(manualDeviations);
			deviationRenderer.setDescriptions(approvedManualDeviations);
			deviationRenderer.renderManualDeviations(manualDeviations, approvedManualDeviations,
					listBoxManualDeviations);

			int height = (borderLayoutHeight - 150) / 2;

			this.listBoxDeviationDetails.setHeight(height + "px");
			this.listBoxManualDeviations.setHeight(height + "px");

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
			map.put("enqModule", true);
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

	public void onClick$btnSave() {
		logger.debug(" Entering ");
		doSave();

		logger.debug(" Leaving ");

	}

	private void doSave() {
		logger.debug(" Entering ");

		FinanceMain fianncemain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		List<FinanceDeviations> list = doWriteComponentsToBean();
		getDeviationDetailsService().processApproval(list, getAuditHeader(fianncemain.getFinReference()),
				fianncemain.getFinReference());
		//get the update finance main
		FinanceMain finmain = getDeviationDetailsService().getFinanceMain(fianncemain.getFinReference());
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
		List<Listitem> list = new ArrayList<Listitem>();
		List<Listitem> autoList = this.listBoxDeviationDetails.getItems();
		if (autoList != null && !autoList.isEmpty()) {
			list.addAll(autoList);
		}
		List<Listitem> manualList = this.listBoxManualDeviations.getItems();
		if (manualList != null && !manualList.isEmpty()) {
			list.addAll(manualList);
		}
		for (Listitem listitem : list) {
			if (listitem instanceof Listgroup) {
				continue;
			}
			boolean process = false;
			FinanceDeviations deviationDetail = (FinanceDeviations) listitem.getAttribute("data");
			if (deviationDetail.isApproved()) {
				continue;
			}
			Component component = listitem.getFellowIfAny("combo_" + deviationDetail.getDeviationId());
			if (component != null && component instanceof Combobox) {
				Combobox combobox = (Combobox) component;
				if (combobox.getSelectedItem() != null
						&& !combobox.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)) {
					deviationDetail.setApprovalStatus(combobox.getSelectedItem().getValue().toString());
					long userId = getUserWorkspace().getLoggedInUser().getUserId();
					deviationDetail.setDelegatedUserId(String.valueOf(userId));
					process = true;
				}
			}

			if (process) {
				financeDeviations.add(deviationDetail);
			}

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

	public void setDeviationHelper(DeviationHelper deviationHelper) {
		this.deviationHelper = deviationHelper;
	}

}
