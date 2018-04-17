package com.pennant.webui.finance.financemain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.North;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.solutionfactory.DeviationParam;
import com.pennant.backend.delegationdeviation.DeviationHelper;
import com.pennant.backend.util.DeviationConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.delegationdeviation.DeviationRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class DeviationDetailDialogCtrl extends GFCBaseCtrl<FinanceDeviations> {
	private static final long	serialVersionUID		= 2290501784830847866L;
	private static final Logger	logger					= Logger.getLogger(DeviationDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window			window_deviationDetailDialog;												// autoWired
	protected Borderlayout		borderlayoutDeviationDetail;												// autoWired
	protected North				northdeviationDetailDialog;
	protected Button			btnProceed;

	protected Listbox			listBoxAutoDeviations;														// autoWired
	protected Listbox			listBoxManualDeviations;													// autoWired

	private FinBasicDetailsCtrl	finBasicDetailsCtrl;
	protected Groupbox			finBasicdetails;

	private Object				financeMainDialogCtrl	= null;
	private FinanceMain			financeMain;
	private FinanceDetail		financeDetail			= null;

	List<DeviationParam>		eligibilitiesList		= PennantAppUtil.getDeviationParams();
	boolean						enquiry					= false;
	boolean						approvalEnquiry			= false;
	int							ccyformat				= 0;
	private String				roleCode				= null;

	Tab							parenttab				= null;
	private Tabpanel			tabPanel_dialogWindow;

	List<FinanceDeviations>		approvalEnqList			= null;
	//Manual deviations
	List<FinanceDeviations>		manualDeviationList		= null;
	private Button				btnNew_ManualDeviation;

	@Autowired
	private DeviationHelper		deviationHelper;
	@Autowired
	private DeviationRenderer	deviationRenderer;

	/**
	 * default constructor.<br>
	 */
	public DeviationDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_deviationDetailDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_deviationDetailDialog);

		try {

			// READ OVERHANDED parameters !
			if (arguments.containsKey("financeDetail")) {
				this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
				setFinanceDetail(financeDetail);
				financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
				ccyformat = CurrencyUtil.getFormat(financeMain.getFinCcy());
			}

			deviationRenderer.init(getUserWorkspace(), ccyformat, false);
			if (arguments.containsKey("financeMainDialogCtrl")) {
				this.financeMainDialogCtrl = arguments.get("financeMainDialogCtrl");
			}

			if (arguments.containsKey("enquiry")) {
				enquiry = true;
			}

			if (arguments.containsKey("approvalEnquiry")) {
				approvalEnquiry = true;
			}

			if (arguments.containsKey("approvalEnqList")) {
				approvalEnqList = (List<FinanceDeviations>) arguments.get("approvalEnqList");
			}
			if (arguments.containsKey("ccyformat")) {
				ccyformat = Integer.parseInt(arguments.get("ccyformat").toString());
			}

			if (arguments.containsKey("tab")) {
				parenttab = (Tab) arguments.get("tab");
			}

			if (arguments.containsKey("roleCode")) {
				roleCode = (String) arguments.get("roleCode");
			} else {
				roleCode = null;
			}

			if (arguments.containsKey("tabPaneldialogWindow")) {
				tabPanel_dialogWindow = (Tabpanel) arguments.get("tabPaneldialogWindow");
			}

			if (arguments.containsKey("finHeaderList")) {
				appendFinBasicDetails((ArrayList<Object>) arguments.get("finHeaderList"));
			} else {
				appendFinBasicDetails(null);
			}
			doCheckRight();
			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_deviationDetailDialog.onClose();
		}
		logger.debug("Leaving " + event.toString());
	}

	private void doCheckRight() {
		boolean alloowd = deviationHelper.checkInputAllowed(financeMain.getFinType(), roleCode);
		this.btnNew_ManualDeviation.setVisible(alloowd);

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

			int height = (borderLayoutHeight - 240) / 2;
			this.listBoxAutoDeviations.setHeight(height + "px");
			this.listBoxManualDeviations.setHeight(height + "px");

			if (approvalEnquiry) {
				this.finBasicdetails.setVisible(false);
				this.northdeviationDetailDialog.setVisible(false);
				this.window_deviationDetailDialog.setHeight(borderLayoutHeight - 150 + "px");
				this.tabPanel_dialogWindow.appendChild(this.window_deviationDetailDialog);

				deviationRenderer.renderAutoDeviations(approvalEnqList, null, this.listBoxAutoDeviations);
				//Auto deviation
				List<FinanceDeviations> listAuto = deviationHelper.getDeviationDetais(approvalEnqList, false);
				deviationRenderer.renderAutoDeviations(null, listAuto, this.listBoxAutoDeviations);
				//manual Deviation
				List<FinanceDeviations> listManual = deviationHelper.getDeviationDetais(approvalEnqList, true);
				deviationRenderer.setDescriptions(listManual);
				deviationRenderer.renderManualDeviations(null, listManual, this.listBoxManualDeviations);
				return;
			} else {

				//Auto deviation
				doFillAutoDeviationDetails(getFinanceDetail().getFinanceDeviations());
				//manual Deviation
				deviationRenderer.setDescriptions(getFinanceDetail().getManualDeviations());
				deviationRenderer.setDescriptions(getFinanceDetail().getApprovedManualDeviations());
				doFillManualDeviations(getFinanceDetail().getManualDeviations());
			}

			// fill the components with the data
			if (enquiry) {
				this.window_deviationDetailDialog.setHeight("75%");
				this.window_deviationDetailDialog.setWidth("90%");
				this.btnNew_ManualDeviation.setVisible(false);
				this.window_deviationDetailDialog.doModal();

			} else {
				this.window_deviationDetailDialog.setHeight(borderLayoutHeight - 75 + "px");
				try {
					getFinanceMainDialogCtrl().getClass().getMethod("setDeviationDetailDialogCtrl", this.getClass())
							.invoke(getFinanceMainDialogCtrl(), this);
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
				this.northdeviationDetailDialog.setVisible(false);
			}

		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
	}

	public void doFillAutoDeviationDetails(List<FinanceDeviations> financeDeviations) {
		deviationRenderer.renderAutoDeviations(financeDeviations, getFinanceDetail().getApprovedFinanceDeviations(),
				this.listBoxAutoDeviations);
	}

	public void onClick$btnProceed() throws InterruptedException {

		Executions.getCurrent().setAttribute("devationConfirm", true);
		List<FinanceDeviations> list = getFinanceDetail().getFinanceDeviations();
		boolean valid = true;
		for (FinanceDeviations financeDeviations : list) {
			if (StringUtils.isEmpty(financeDeviations.getDelegationRole())) {
				valid = false;
				break;
			}
		}

		if (!valid) {
			MessageUtil.showError("There are some deviation with out deligation.");
			return;
		}

		Executions.notify(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinReference());
		this.window_deviationDetailDialog.onClose();
	}

	public void onClick$btnCancel() {
		Executions.notify(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinReference());
		this.window_deviationDetailDialog.onClose();
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 * 
	 * @param arrayList
	 */
	private void appendFinBasicDetails(ArrayList<Object> arrayList) {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			map.put("displayLog", false);
			if (arrayList != null) {
				map.put("finHeaderList", arrayList);
			}
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	//Manual Deviation
	public void doFillManualDeviations(List<FinanceDeviations> financeDeviations) {
		setManualDeviationList(financeDeviations);
		deviationRenderer.renderManualDeviations(getManualDeviationList(),
				getFinanceDetail().getApprovedManualDeviations(), this.listBoxManualDeviations);

	}

	public void onClick$btnNew_ManualDeviation(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final FinanceDeviations aFinanceDeviations = new FinanceDeviations();
		aFinanceDeviations.setFinReference(financeMain.getFinReference());
		aFinanceDeviations.setNewRecord(true);
		aFinanceDeviations.setManualDeviation(true);
		aFinanceDeviations.setDeviationType(DeviationConstants.DT_STRING);
		aFinanceDeviations.setDeviationValue("");
		aFinanceDeviations.setUserRole(roleCode);
		long userId = getUserWorkspace().getLoggedInUser().getUserId();
		aFinanceDeviations.setDeviationUserId(String.valueOf(userId));
		aFinanceDeviations.setWorkflowId(0);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeDeviations", aFinanceDeviations);
		map.put("newRecord", "true");
		doshowDialog(map, false);

		logger.debug("Leaving");

		logger.debug("Leaving" + event.toString());
	}

	public void onManualDeviationItemDoubleClicked(Event event) throws Exception {
		Listitem listitem = this.listBoxManualDeviations.getSelectedItem();
		if (listitem != null && listitem.getAttribute("data") != null) {
			final FinanceDeviations aFinDeviations = (FinanceDeviations) listitem.getAttribute("data");
			if (!DeviationHelper.isApproved(aFinDeviations)
					&& !StringUtils.equals(aFinDeviations.getRecordType(), PennantConstants.RCD_DEL)) {
				aFinDeviations.setNewRecord(false);
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("financeDeviations", aFinDeviations);
				doshowDialog(map, false);
			} else {
				MessageUtil.showMessage(Labels.getLabel("common_NoMaintainance"));
			}

		}

	}

	private void doshowDialog(HashMap<String, Object> map, boolean isEnquiry) throws InterruptedException {

		map.put("enqModule", isEnquiry);
		map.put("roleCode", roleCode);
		map.put("financeMain", financeMain);
		map.put("DeviationDetailDialogCtrl", this);
		map.put("financeMainDialogCtrl", this.financeMainDialogCtrl);
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ManualDeviationTriggerDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

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

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public List<FinanceDeviations> getManualDeviationList() {
		return manualDeviationList;
	}

	public void setManualDeviationList(List<FinanceDeviations> manualDeviationList) {
		this.manualDeviationList = manualDeviationList;
	}

}
