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
 * FileName    		:  ScheduleDetailDialogCtrl.java                                                   * 	  
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

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/ScheduleDetailDialog.zul file.
 */
public class FinCollateralHeaderDialogCtrl extends GFCBaseCtrl<FinCollaterals> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(FinCollateralHeaderDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_finCollateralHeaderDialog; // autoWired
	protected Borderlayout borderlayoutFinCollateralHeader; // autoWired
	// Finance Document Details Tab
	protected Label finCollateral_finType; // autoWired
	protected Label finCollateral_finReference; // autoWired
	protected Label finCollateral_finAmount; // autoWired
	protected Label finCollateral_custID; // autoWired
	protected Button btnNew_FinCollateralDetail; // autoWired
	protected Listbox listBoxFinCollateralDetails; // autoWired
	private List<FinCollaterals> finCollateralDetailsList = new ArrayList<FinCollaterals>();
	private transient FinanceDetailService financeDetailService = null;

	private Object financeMainDialogCtrl = null;
	private FinanceDetail financeDetail = null;
	private String roleCode = "";
	private BigDecimal totCost = BigDecimal.ZERO;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	protected Groupbox finBasicdetails;

	/**
	 * default constructor.<br>
	 */
	public FinCollateralHeaderDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinCollateralDetailsDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_finCollateralHeaderDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_finCollateralHeaderDialog);

		try {

			// READ OVERHANDED parameters !
			if (arguments.containsKey("financeDetail")) {
				this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
				setFinanceDetail(financeDetail);
				setFinCollateralDetailsList(financeDetail.getFinanceCollaterals());
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");
			}

			if (arguments.containsKey("roleCode")) {
				this.roleCode = (String) arguments.get("roleCode");
			}
			doCheckRights();
			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_finCollateralHeaderDialog.onClose();
		}
		logger.debug("Leaving " + event.toString());
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

			// append finance basic details
			appendFinBasicDetails();

			// fill the components with the data
			if (getFinCollateralDetailsList() != null && getFinCollateralDetailsList().size() > 0) {
				doFillCollateralDetails(getFinCollateralDetailsList());
			}

			try {
				getFinanceMainDialogCtrl().getClass().getMethod("setFinCollateralHeaderDialogCtrl", this.getClass())
						.invoke(getFinanceMainDialogCtrl(), this);
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}

			getBorderLayoutHeight();
			this.listBoxFinCollateralDetails.setHeight(this.borderLayoutHeight - 250 + "px");
			this.window_finCollateralHeaderDialog.setHeight(this.borderLayoutHeight - 80 + "px");

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_finCollateralHeaderDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Reset Finance Main data
	 * 
	 * @param fintype
	 * @param finReference
	 * @param custID
	 * @param finAmount
	 */
	public void doSetLabels(String fintype, String finReference, long custID, BigDecimal finAmount, int decPos) {
		logger.debug("Entering");
		this.finCollateral_finType.setValue(fintype);
		this.finCollateral_finReference.setValue(finReference);
		this.finCollateral_custID.setValue(String.valueOf(custID));
		this.finCollateral_finAmount.setValue(PennantAppUtil.amountFormate(finAmount, decPos));
		logger.debug("Leaving");
	}

	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("FinCollateralDetailsDialog", this.roleCode);
		this.btnNew_FinCollateralDetail.setVisible(getUserWorkspace().isAllowed(
				"button_FinCollateralDetailsDialog_btnNew"));
		logger.debug("Leaving");
	}

	// New Button & Double Click Events for Finance Collateral List

	// Finance Collateral Details Tab
	public void onClick$btnNew_FinCollateralDetail(Event event) throws InterruptedException, SecurityException,
			IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", getFinanceMainDialogCtrl());
		map.put("finCollateralHeaderDialogCtrl", this);
		FinanceMain financeMain = updateFinanceMain();
		map.put("financeMain", financeMain);

		FinCollaterals finCollateral = new FinCollaterals();
		finCollateral.setNewRecord(true);
		finCollateral.setWorkflowId(0);
		finCollateral.setFinReference(financeMain.getFinReference());
		finCollateral.setCustID(financeMain.getCustID());

		map.put("finCollateralDetail", finCollateral);
		map.put("roleCode", this.roleCode);
		map.put("newRecord", true);

		map.put("window", window_finCollateralHeaderDialog);
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinCollateralDetailDialog.zul",
				window_finCollateralHeaderDialog, map);
		logger.debug("Leaving" + event.toString());
	}

	public void doFillCollateralDetails(List<FinCollaterals> collaterals) {
		logger.debug("Entering");

		this.listBoxFinCollateralDetails.getItems().clear();
		int amtformate = SysParamUtil.getValueAsInt("APP_DFT_CURR_EDIT_FIELD");
		setFinCollateralDetailsList(collaterals);
		if (!collaterals.isEmpty()) {
			totCost = BigDecimal.ZERO;
			ArrayList<ValueLabel> collateralTypes = (ArrayList<ValueLabel>) PennantStaticListUtil.getCollateralTypes();
			for (FinCollaterals finCollateral : collaterals) {
				Listitem listitem = new Listitem();
				Listcell listcell;
				listcell = new Listcell(PennantAppUtil.getlabelDesc(finCollateral.getCollateralType(), collateralTypes));
				listitem.appendChild(listcell);
				listcell = new Listcell(finCollateral.getReference());
				listitem.appendChild(listcell);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(finCollateral.getValue(), amtformate));
				listitem.appendChild(listcell);
				listcell = new Listcell(finCollateral.getRecordType());
				listitem.appendChild(listcell);

				BigDecimal cost = finCollateral.getValue() == null ? BigDecimal.ZERO : finCollateral.getValue();
				if (!(StringUtils.equals(finCollateral.getRecordType(), PennantConstants.RECORD_TYPE_DEL) || StringUtils
						.equals(finCollateral.getRecordType(), PennantConstants.RECORD_TYPE_CAN))) {
					totCost = totCost.add(cost);
				}
				listitem.setAttribute("data", finCollateral);
				ComponentsCtrl.applyForward(listitem, "onDoubleClick=onFinCollateralItemDoubleClicked");
				this.listBoxFinCollateralDetails.appendChild(listitem);
			}

			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell(Labels.getLabel("label_FinCollateralHeaderDialog_Total_cost"));
			lc.setParent(item);
			lc.setStyle("font-weight:bold");
			lc.setSpan(2);
			lc = new Listcell(PennantApplicationUtil.amountFormate(totCost, amtformate));
			lc.setStyle("text-align:left");
			lc.setParent(item);
			lc = new Listcell();
			lc.setParent(item);
			this.listBoxFinCollateralDetails.appendChild(item);

		}
		logger.debug("Leaving");
	}

	public void onFinCollateralItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected invoiceHeader object
		final Listitem item = this.listBoxFinCollateralDetails.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			FinCollaterals finCollateral = (FinCollaterals) item.getAttribute("data");
			if (PennantConstants.RECORD_TYPE_CAN
					.equalsIgnoreCase(StringUtils.trimToEmpty(finCollateral.getRecordType()))
					|| PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(StringUtils.trimToEmpty(finCollateral
							.getRecordType()))) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("financeMainDialogCtrl", getFinanceMainDialogCtrl());
				map.put("finCollateralHeaderDialogCtrl", this);
				FinanceMain financeMain = updateFinanceMain();
				map.put("financeMain", financeMain);
				map.put("finCollateralDetail", finCollateral);
				map.put("roleCode", this.roleCode);
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinCollateralDetailDialog.zul",
							window_finCollateralHeaderDialog, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Update Finance Main Details from the Finance Main Ctrl
	 * 
	 * @return
	 */
	private FinanceMain updateFinanceMain() {
		FinanceMain main = null;
		try {
			Object object = getFinanceMainDialogCtrl().getClass().getMethod("getFinanceMain")
					.invoke(getFinanceMainDialogCtrl());
			if (object != null) {
				main = (FinanceMain) object;
				return main;
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		return null;
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

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
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

	public List<FinCollaterals> getFinCollateralDetailsList() {
		return finCollateralDetailsList;
	}

	public void setFinCollateralDetailsList(List<FinCollaterals> finCollateralDetailsList) {
		this.finCollateralDetailsList = finCollateralDetailsList;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}
}
