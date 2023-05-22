/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */
/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : ScheduleDetailDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.service.configuration.VASConfigurationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/FinVasRecordingDialog.zul file.
 */
public class FinVasRecordingDialogCtrl extends GFCBaseCtrl<VASRecording> {

	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(FinVasRecordingDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	public Window window_FinVasRecordingDialog;
	protected Borderlayout borderlayoutFinVasRecordingDialog;

	protected Button btnNew_VasRecording;
	protected Div vasRecordingDiv;
	protected Listbox listBoxVasRecording;
	protected Groupbox finBasicdetails;
	private Component parent = null;

	private FinanceMainBaseCtrl financeMainDialogCtrl = null;
	private String roleCode = "";
	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private List<VASRecording> vasRecordings = null;
	private String finType = "";
	private VASConfigurationService vasConfigurationService;
	private FinanceDetail financeDetail;
	private List<JointAccountDetail> jointAccountDetails;
	List<VASRecording> cumulativeVasRecordsList = new ArrayList<>();

	/**
	 * default constructor.<br>
	 */
	public FinVasRecordingDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinVasRecordingDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_FinVasRecordingDialog(ForwardEvent event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinVasRecordingDialog);

		try {

			if (event.getTarget().getParent() != null) {
				parent = event.getTarget().getParent();
			}

			// READ OVERHANDED parameters !
			if (arguments.containsKey("vasRecordingList")) {

				for (VASRecording vasRecording : (List<VASRecording>) arguments.get("vasRecordingList")) {
					cumulativeVasRecordsList.add(vasRecording);
				}
			}

			if (arguments.containsKey("ChildVasRecordingList")) {
				for (VASRecording vasRecording : (List<VASRecording>) arguments.get("ChildVasRecordingList")) {
					cumulativeVasRecordsList.add(vasRecording);
				}
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				this.financeMainDialogCtrl = (FinanceMainBaseCtrl) arguments.get("financeMainDialogCtrl");
			}

			if (arguments.containsKey("roleCode")) {
				this.roleCode = (String) arguments.get("roleCode");
			}

			if (arguments.containsKey("finType")) {
				this.finType = (String) arguments.get("finType");
			}
			if (arguments.containsKey("financeDetail")) {
				this.setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
			}

			doCheckRights();
			doShowDialog();

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_FinVasRecordingDialog.onClose();
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 */
	public void doShowDialog() {
		logger.debug("Entering");

		try {
			// append finance basic details
			appendFinBasicDetails();

			// fill the components with the data
			doFillVasRecordings(cumulativeVasRecordsList);

			// Setting Controller to the Parent Controller
			try {
				getFinanceMainDialogCtrl().getClass().getMethod("setFinVasRecordingDialogCtrl", this.getClass())
						.invoke(getFinanceMainDialogCtrl(), this);
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}

			getBorderLayoutHeight();
			if (parent != null) {
				int borderHeight = (this.borderLayoutHeight - 200);
				this.window_FinVasRecordingDialog.setHeight(this.borderLayoutHeight - 75 + "px");
				this.listBoxVasRecording.setHeight(borderHeight + "px");
				parent.appendChild(this.window_FinVasRecordingDialog);
			} else {
				this.listBoxVasRecording.setHeight(150 + "px");
				this.window_FinVasRecordingDialog.setHeight(this.borderLayoutHeight - 80 + "px");
			}

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_FinVasRecordingDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Checking Rights for the Collateral Dialog
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities(this.pageRightName, this.roleCode);
		this.btnNew_VasRecording.setVisible(getUserWorkspace().isAllowed("button_FinVasRecordingDialog_btnNew"));

		logger.debug("Leaving");
	}

	/**
	 * New Button & Double Click Events for adding Vas Details
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void onClick$btnNew_VasRecording(Event event) throws InterruptedException, SecurityException,
			IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering" + event.toString());

		VASRecording recording = new VASRecording();
		recording.setNewRecord(true);

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("finVasRecordingDialogCtrl", this);
		map.put("vASRecording", recording);
		map.put("newRecord", true);
		map.put("waivedFlag", true);
		map.put("financeDetail", getFinanceDetail());
		map.put("jointAccountDetails", getJointAccountDetails());

		List<String> roles = new ArrayList<>();
		roles.add(roleCode);
		map.put("role", roles);
		map.put("finType", finType);

		Executions.createComponents("/WEB-INF/pages/VASRecording/SelectVASConfigurationDialog.zul",
				window_FinVasRecordingDialog, map);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Filling List box with the list rendering for Assignments
	 * 
	 * @param vasRecordings
	 */
	public void doFillVasRecordings(List<VASRecording> vasRecordings) {
		logger.debug("Entering");

		this.listBoxVasRecording.getItems().clear();
		setVasRecordings(vasRecordings);

		if (vasRecordings != null && !vasRecordings.isEmpty()) {

			for (VASRecording vasRec : vasRecordings) {

				Listitem listitem = new Listitem();
				Listcell listcell;

				listcell = new Listcell(vasRec.getVasReference());
				listitem.appendChild(listcell);

				listcell = new Listcell(vasRec.getProductCode());
				listitem.appendChild(listcell);

				listcell = new Listcell(vasRec.getProductCtg());
				listitem.appendChild(listcell);

				listcell = new Listcell(vasRec.getProductType());
				listitem.appendChild(listcell);

				listcell = new Listcell(vasRec.getManufacturerDesc());
				listitem.appendChild(listcell);

				listcell = new Listcell(DateUtil.formatToLongDate(vasRec.getValueDate()));
				listitem.appendChild(listcell);

				listcell = new Listcell(DateUtil.formatToLongDate(vasRec.getAccrualTillDate()));
				listitem.appendChild(listcell);

				listcell = new Listcell(vasRec.getRecordStatus());
				listitem.appendChild(listcell);

				listcell = new Listcell(PennantJavaUtil.getLabel(vasRec.getRecordType()));
				listitem.appendChild(listcell);

				listitem.setAttribute("data", vasRec);
				ComponentsCtrl.applyForward(listitem, "onDoubleClick=onVasRecordingDoubleClicked");
				this.listBoxVasRecording.appendChild(listitem);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Editing the Collateral Assignment Details on Double Click
	 * 
	 * @param event
	 */
	public void onVasRecordingDoubleClicked(Event event) {
		logger.debug("Entering" + event.toString());

		// get the selected invoiceHeader object
		final Listitem item = this.listBoxVasRecording.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			VASRecording recording = (VASRecording) item.getAttribute("data");
			if (StringUtils.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN,
					StringUtils.trimToEmpty(recording.getRecordType()))
					|| StringUtils.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL,
							StringUtils.trimToEmpty(recording.getRecordType()))) {

				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));

			} else {

				// Checking VAS Configuration Exists in the Bean or not and retrieving, if not available
				VASConfiguration vasConfiguration = recording.getVasConfiguration();
				if (vasConfiguration == null) {
					vasConfiguration = getVasConfigurationService()
							.getApprovedVASConfigurationByCode(recording.getProductCode(), true);
					recording.setVasConfiguration(vasConfiguration);
				}

				final Map<String, Object> map = new HashMap<String, Object>();

				if (StringUtils.isEmpty(recording.getVasReference())) {
					map.put("feeEditable", true);
				}
				map.put("waivedFlag", true);
				map.put("finVasRecordingDialogCtrl", this);
				map.put("roleCode", this.roleCode);
				map.put("vASRecording", recording);
				map.put("financeDetail", getFinanceDetail());
				map.put("jointAccountDetails", getJointAccountDetails());

				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/VASRecording/VASRecordingDialog.zul", null, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails() {
		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}

	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceMainBaseCtrl getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(FinanceMainBaseCtrl financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public List<VASRecording> getVasRecordings() {
		return vasRecordings;
	}

	public void setVasRecordings(List<VASRecording> vasRecordings) {
		this.vasRecordings = vasRecordings;
	}

	public VASConfigurationService getVasConfigurationService() {
		return vasConfigurationService;
	}

	public void setVasConfigurationService(VASConfigurationService vasConfigurationService) {
		this.vasConfigurationService = vasConfigurationService;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public void addCoApplicants(List<JointAccountDetail> jointAccountDetails) {
		this.jointAccountDetails = jointAccountDetails;
	}

	public List<JointAccountDetail> getJointAccountDetails() {
		return jointAccountDetails;
	}
}
