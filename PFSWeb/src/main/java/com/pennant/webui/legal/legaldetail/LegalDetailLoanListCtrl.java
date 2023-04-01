/**
 * o * Copyright 2011 - Pennant Technologies
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
 * * FileName : LegalDetailListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 20-08-2018 * * Modified
 * Date : 20-08-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 20-08-2018 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.legal.legaldetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class LegalDetailLoanListCtrl extends GFCBaseCtrl<LegalDetail> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(LegalDetailLoanListCtrl.class);

	protected Window window_LegalDetailLoanList;
	protected Listbox listBoxLegalDetail;

	private FinanceDetail financedetail;
	private Object financeMainDialogCtrl;
	private Component parent = null;
	private String roleCode = "";
	protected Groupbox finBasicdetails;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;

	private List<LegalDetail> legalDetailsList;

	/**
	 * default constructor.<br>
	 */
	public LegalDetailLoanListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "LegalDetail";
		super.pageRightName = "LegalDetailList";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FinAdvancePayment object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_LegalDetailLoanList(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_LegalDetailLoanList);

		try {
			if (event.getTarget().getParent() != null) {
				parent = event.getTarget().getParent();
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				setFinanceMainDialogCtrl((Object) arguments.get("financeMainDialogCtrl"));
				this.window_LegalDetailLoanList.setTitle("");
			}

			if (arguments.containsKey("roleCode")) {
				roleCode = (String) arguments.get("roleCode");
			}

			if (arguments.containsKey("financeDetail")) {
				setFinancedetail((FinanceDetail) arguments.get("financeDetail"));
				if (getFinancedetail() != null) {
					setLegalDetailsList(getFinancedetail().getLegalDetailsList());
				}
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");
				try {
					financeMainDialogCtrl.getClass().getMethod("setLegalDetailLoanListCtrl", this.getClass())
							.invoke(financeMainDialogCtrl, this);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}

			if (arguments.containsKey("finHeaderList")) {
				appendFinBasicDetails((ArrayList<Object>) arguments.get("finHeaderList"));
			} else {
				appendFinBasicDetails(null);
			}

			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @throws InterruptedException
	 */
	public void doShowDialog() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		try {
			doWriteBeanToComponents();
			this.listBoxLegalDetail.setHeight(borderLayoutHeight - 226 + "px");
			if (parent != null) {
				this.window_LegalDetailLoanList.setHeight(borderLayoutHeight - 75 + "px");
				parent.appendChild(this.window_LegalDetailLoanList);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 * 
	 * @param arrayList
	 */
	private void appendFinBasicDetails(ArrayList<Object> finHeaderList) {
		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", finHeaderList);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}

	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * 
	 */
	public void doWriteBeanToComponents() {
		logger.debug(Literal.ENTERING);
		doFillLegalDetailDetails(getLegalDetailsList());
		logger.debug(Literal.LEAVING);
	}

	public void doFillLegalDetailDetails(List<LegalDetail> legalDetailsList) {
		logger.debug(Literal.ENTERING);

		this.listBoxLegalDetail.getItems().clear();
		setLegalDetailsList(legalDetailsList);

		getFinancedetail().setLegalDetailsList(legalDetailsList);

		if (CollectionUtils.isNotEmpty(legalDetailsList)) {
			for (LegalDetail legalDetail : legalDetailsList) {

				Listitem item = new Listitem();
				Listcell lc;

				lc = new Listcell(legalDetail.getLoanReference());
				lc.setParent(item);

				lc = new Listcell(legalDetail.getCollateralReference());
				lc.setParent(item);

				lc = new Listcell(legalDetail.getLegalReference());
				lc.setParent(item);

				lc = new Listcell(legalDetail.getBranchDesc());
				lc.setParent(item);

				lc = new Listcell(DateUtil.format(legalDetail.getLegalDate(), PennantConstants.dateFormat));
				lc.setParent(item);

				if (legalDetail.isActive()) {
					lc = new Listcell("Active");
					lc.setStyle("font-weight:bold;color:#00F566;");
					lc.setParent(item);
				} else {
					lc = new Listcell("Inactive");
					lc.setStyle("font-weight:bold;color:#E37114;");
					lc.setParent(item);
				}

				lc = new Listcell(legalDetail.getRecordStatus());
				lc.setParent(item);

				lc = new Listcell(PennantJavaUtil.getLabel(legalDetail.getRecordType()));
				lc.setParent(item);
				item.setAttribute("object", legalDetail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onLegalDetailItemDoubleClicked");
				this.listBoxLegalDetail.appendChild(item);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onLegalDetailItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		// Get the selected record.
		Listitem selectedItem = this.listBoxLegalDetail.getSelectedItem();
		LegalDetail legalDetail = (LegalDetail) selectedItem.getAttribute("object");

		if (legalDetail == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		if (!legalDetail.isActive()) {
			final String msg = Labels.getLabel("message.Question.Are_you_sure_to_proceed_this_record_is_inactive");
			if (MessageUtil.confirm(msg) == MessageUtil.NO) {
				return;
			}
		}
		doShowDialogPage(legalDetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param legaldetail The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(LegalDetail legalDetail) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = new HashMap<>();
		arg.put("legalDetail", legalDetail);
		arg.put("legalDetailListCtrl", this);
		arg.put("fromLoan", true);
		arg.put("roleCode", this.roleCode);
		arg.put("dialogCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Legal/LegalDetail/LegalDetailDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
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

	public List<LegalDetail> getLegalDetailsList() {
		return legalDetailsList;
	}

	public void setLegalDetailsList(List<LegalDetail> legalDetailsList) {
		this.legalDetailsList = legalDetailsList;
	}

}
