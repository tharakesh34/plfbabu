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
 * FileName    		:  FinanceMainDialogCtrl.java                                                   * 	  
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.service.finance.GuarantorDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/financeMain/FinanceMainDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class JointAccountDetailDialogCtrl extends GFCBaseListCtrl<JointAccountDetail> implements Serializable {
	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(JointAccountDetailDialogCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_JointAccountDetailDialog; // autoWired not auto
	// wired variables
	private FinanceDetail financeDetail = null; // over handed per parameters
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();
	// Joint Account and Gurantors Details
	protected Listbox listBoxGurantorsDetail;
	protected Button btnAddGurantorDetails;
	protected Button btnAddJointDetails;
	protected Listbox listBoxJountAccountDetails; // autoWired
	private List<JointAccountDetail> jountAccountDetailList = new ArrayList<JointAccountDetail>();
	private List<JointAccountDetail> oldVar_JountAccountDetailList = new ArrayList<JointAccountDetail>();
	private List<GuarantorDetail> guarantorDetailList = new ArrayList<GuarantorDetail>();
	private List<GuarantorDetail> oldVar_GuarantorDetailList = new ArrayList<GuarantorDetail>();
	int ccDecimal = 0;
	private String finreference = "";
	private String custCIF = "";
	private Object financeMainDialogCtrl;
	private String ccy = "";

	private GuarantorDetailService guarantorDetailService;
	private String roleCode = "";


	/**
	 * default constructor.<br>
	 */
	public JointAccountDetailDialogCtrl() {
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
	public void onCreate$window_JointAccountDetailDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		// READ OVERHANDED parameters !
		if (args.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) args.get("financeDetail");
			finreference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
			custCIF = financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF();
			ccDecimal = financeDetail.getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
			ccy = financeDetail.getFinScheduleData().getFinanceMain().getFinCcy();
		}
		if (args.containsKey("financeMainDialogCtrl")) {
			setFinanceMainDialogCtrl(args.get("financeMainDialogCtrl"));
		}

		if (args.containsKey("roleCode")) {
			roleCode = (String) args.get("roleCode");
		}

		doCheckRights();
		doShowDialog();
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
		getUserWorkspace().alocateAuthorities("FinanceMainDialog", roleCode);
		this.btnAddGurantorDetails.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnAddGurantor"));
		this.btnAddJointDetails.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnAddJointAccount"));
		logger.debug("Leaving");
	}

	@SuppressWarnings("rawtypes")
	private void doShowDialog() {
		logger.debug("Entering");

		// Rendering Joint Account Details
		List<JointAccountDetail> jointAcctDetailList = financeDetail.getJountAccountDetailList();
		if (jointAcctDetailList != null && !jointAcctDetailList.isEmpty()) {
			doFillJointDetails(jointAcctDetailList);
		}

		// Rendering Guaranteer Details
		List<GuarantorDetail> gurantorsDetailList = financeDetail.getGurantorsDetailList();
		if (gurantorsDetailList != null && !gurantorsDetailList.isEmpty()) {
			doFillGurantorsDetails(gurantorsDetailList);
		}
		
		try {
			Class[] paramType = {this.getClass() };
			Object[] stringParameter = { this };
			financeMainDialogCtrl.getClass().getMethod("setJointAccountDetailDialogCtrl", paramType).invoke(financeMainDialogCtrl, stringParameter);
		} catch (Exception e) {
			logger.error(e);
		}
		
		getBorderLayoutHeight();
		this.window_JointAccountDetailDialog.setHeight(this.borderLayoutHeight - 100 + "px");
		this.listBoxJountAccountDetails.setHeight(((this.borderLayoutHeight - 250 - 50) / 2) + "px");// 425px
		this.listBoxGurantorsDetail.setHeight(((this.borderLayoutHeight - 250 - 50) / 2) + "px");// 425px
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	public void doSetValidation() {
		logger.debug("Entering");
		// If include JointAccount is checked then JointAccountList should not
		// be empty
		if (this.jountAccountDetailList.size() < 1) {
			try {
				PTMessageUtils.showErrorMessage("Please enter JointAccount Details in JointAccount tab");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * This method set the Guaranteer details to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 */
	public void doSave_GuarantorDetail(FinanceDetail aFinanceDetail) {
		logger.debug("Entering ");
		if (guarantorDetailList != null && !this.guarantorDetailList.isEmpty()) {
			for (GuarantorDetail details : guarantorDetailList) {
				details.setFinReference(aFinanceDetail.getFinScheduleData().getFinanceMain().getFinReference());
				details.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
				details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				details.setUserDetails(getUserWorkspace().getLoginUserDetails());
				details.setRecordStatus(aFinanceDetail.getUserAction());
			}
			aFinanceDetail.setGurantorsDetailList(guarantorDetailList);
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method set the guaranteer details to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 */
	public void doSave_JointAccountDetail(FinanceDetail aFinanceDetail) {
		logger.debug("Entering ");
		if (jountAccountDetailList != null && !this.jountAccountDetailList.isEmpty()) {
			for (JointAccountDetail details : jountAccountDetailList) {
				details.setFinReference(aFinanceDetail.getFinScheduleData().getFinanceMain().getFinReference());
				details.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
				details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				details.setUserDetails(getUserWorkspace().getLoginUserDetails());
				details.setRecordStatus(aFinanceDetail.getUserAction());
			}
			aFinanceDetail.setJountAccountDetailList(jountAccountDetailList);
		}
		logger.debug("Leaving ");
	}

	// ================Joint Account Details
	public void onClick$btnAddJointDetails(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		updateFinanceDetails();
		if (this.custCIF.equals("")) {
			PTMessageUtils.showErrorMessage("please Select The Customer");
			return;
		}
		JointAccountDetail jountAccountDetail = new JointAccountDetail();
		FinanceMain financeMain = null;
		if (getFinanceDetail() != null && getFinanceDetail().getFinScheduleData().getFinanceMain() != null) {
			financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		}
		jountAccountDetail.setNewRecord(true);
		jountAccountDetail.setWorkflowId(0);
		jountAccountDetail.setFinReference(finreference);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("jountAccountDetail", jountAccountDetail);
		map.put("finJointAccountCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", roleCode);
		map.put("financeMain", financeMain);
		map.put("primaryCustID", custCIF);
		map.put("ccy", ccy);
		map.put("filter", getjointAcFilter());
		try {
			Executions.createComponents("/WEB-INF/pages/JointAccountDetail/JointAccountDetailDialog.zul", window_JointAccountDetailDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	public void doFillJointDetails(List<JointAccountDetail> jountAccountDetails) {
		logger.debug("Entering");
		this.listBoxJountAccountDetails.getItems().clear();
		setJountAccountDetailList(jountAccountDetails);
		for (JointAccountDetail jountAccountDetail : jountAccountDetails) {
			Listitem listitem = new Listitem();
			Listcell listcell;
			listcell = new Listcell(jountAccountDetail.getCustCIF());
			listitem.appendChild(listcell);
			listcell = new Listcell(jountAccountDetail.getLovDescCIFName());
			listitem.appendChild(listcell);
			listcell = new Listcell();
			Checkbox c = new Checkbox();
			c.setDisabled(true);
			c.setChecked(jountAccountDetail.isIncludeRepay());
			c.setParent(listcell);
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantApplicationUtil.formatAccountNumber(jountAccountDetail.getRepayAccountId()));
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantApplicationUtil.amountFormate(new BigDecimal(jountAccountDetail.getPrimaryExposure() != null ? jountAccountDetail.getPrimaryExposure() : "0"), 3));
			listcell.setStyle("text-align:right");
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantApplicationUtil.amountFormate(new BigDecimal(jountAccountDetail.getSecondaryExposure() != null ? jountAccountDetail.getSecondaryExposure() : "0"), 3));
			listcell.setStyle("text-align:right");
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantApplicationUtil.amountFormate(new BigDecimal(jountAccountDetail.getGuarantorExposure() != null ? jountAccountDetail.getGuarantorExposure() : "0"), 3));
			listcell.setStyle("text-align:right");
			listitem.appendChild(listcell);
			listcell = new Listcell(jountAccountDetail.getStatus());
			listitem.appendChild(listcell);
			listcell = new Listcell(jountAccountDetail.getWorstStatus());
			listitem.appendChild(listcell);
			listitem.setAttribute("data", jountAccountDetail);
			ComponentsCtrl.applyForward(listitem, "onDoubleClick=onFinJointItemDoubleClicked");
			this.listBoxJountAccountDetails.appendChild(listitem);
		}
		logger.debug("Leaving");
	}

	public void onFinJointItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxJountAccountDetails.getSelectedItem();
		if (item != null) {
			int index = item.getIndex();
			// CAST AND STORE THE SELECTED OBJECT
			final JointAccountDetail jountAccountDetail = (JointAccountDetail) item.getAttribute("data");
			if (jountAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("jountAccountDetail", jountAccountDetail);
				map.put("finJointAccountCtrl", this);
				map.put("roleCode", roleCode);
				map.put("moduleType", "");
				map.put("index", index);
				map.put("ccDecimal", ccDecimal);
				map.put("ccy", ccy);
				map.put("filter", getjointAcFilter());
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/JointAccountDetail/JointAccountDetailDialog.zul", window_JointAccountDetailDialog, map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / " + e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	// ================Gurantors Details
	public void onClick$btnAddGurantorDetails(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		updateFinanceDetails();
		if (this.custCIF.equals("")) {
			PTMessageUtils.showErrorMessage("please Select The Customer");
			return;
		}
		GuarantorDetail guarantorDetail = new GuarantorDetail();
		guarantorDetail.setNewRecord(true);
		guarantorDetail.setWorkflowId(0);
		guarantorDetail.setFinReference(finreference);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("guarantorDetail", guarantorDetail);
		map.put("finJointAccountCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", roleCode);
		map.put("primaryCustID", custCIF);
		map.put("ccDecimal", ccDecimal);
		map.put("ccy", ccy);
		map.put("filter", getGurantorFilter());
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/GuarantorDetail/GuarantorDetailDialog.zul", window_JointAccountDetailDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	public void doFillGurantorsDetails(List<GuarantorDetail> guarantorDetailList) {
		logger.debug("Entering");
		this.listBoxGurantorsDetail.getItems().clear();
		setGuarantorDetailList(guarantorDetailList);
		for (GuarantorDetail guarantorDetail : guarantorDetailList) {
			Listitem listitem = new Listitem();
			Listcell listcell;
			if (StringUtils.isBlank(guarantorDetail.getGuarantorCIF())) {
				listcell = new Listcell(guarantorDetail.getGuarantorIDNumber());
			} else {
				listcell = new Listcell(guarantorDetail.getGuarantorCIF());
			}
			listitem.appendChild(listcell);
			listcell = new Listcell(guarantorDetail.getGuarantorCIFName());
			listitem.appendChild(listcell);
			listcell = new Listcell(guarantorDetail.getGuarantorIDTypeName());
			listitem.appendChild(listcell);
			listcell = new Listcell(guarantorDetail.getGuranteePercentage().toString());
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantApplicationUtil.amountFormate(new BigDecimal(guarantorDetail.getPrimaryExposure() != null ? guarantorDetail.getPrimaryExposure() : "0"), ccDecimal));
			listcell.setStyle("text-align:right");
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantApplicationUtil.amountFormate(new BigDecimal(guarantorDetail.getSecondaryExposure() != null ? guarantorDetail.getSecondaryExposure() : "0"), ccDecimal));
			listcell.setStyle("text-align:right");
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantApplicationUtil.amountFormate(new BigDecimal(guarantorDetail.getGuarantorExposure() != null ? guarantorDetail.getGuarantorExposure() : "0"), ccDecimal));
			listcell.setStyle("text-align:right");
			listitem.appendChild(listcell);
			listcell = new Listcell(guarantorDetail.getStatus());
			listitem.appendChild(listcell);
			listcell = new Listcell(guarantorDetail.getWorstStatus());
			listitem.appendChild(listcell);
			listcell = new Listcell(guarantorDetail.getMobileNo());
			listitem.appendChild(listcell);
			listcell = new Listcell();
			Button viewBtn = new Button("View");
			viewBtn.setStyle("font-weight:bold");
			listcell.appendChild(viewBtn);
			viewBtn.addForward("onClick", window_JointAccountDetailDialog, "onViewGurantorProofFile", listitem);
			if (StringUtils.trimToEmpty(guarantorDetail.getGuarantorProofName()).equals("")) {
				viewBtn.setVisible(false);
			}
			viewBtn.setParent(listcell);
			listitem.appendChild(listcell);
			listitem.setAttribute("data", guarantorDetail);
			ComponentsCtrl.applyForward(listitem, "onDoubleClick=onFinGurantorItemDoubleClicked");
			this.listBoxGurantorsDetail.appendChild(listitem);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Uploading Agreement Details File
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onViewGurantorProofFile(ForwardEvent event) throws Exception {
		logger.debug("Entering" + event.toString());
		String guarantorProofName = null;
		byte [] guarantorProof = null;

		Listitem listitem = (Listitem) event.getData();
		if (listitem != null) {
			GuarantorDetail detail = (GuarantorDetail) listitem.getAttribute("data");
			guarantorProofName = StringUtils.trimToNull(detail.getGuarantorProofName());
			guarantorProof = detail.getGuarantorProof();

			if(guarantorProofName != null) {
				if(guarantorProof == null) {
					guarantorProof = getGuarantorDetailService().getGuarantorProof(detail).getGuarantorProof();
				}

				detail.setGuarantorProof(guarantorProof);
				try {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("FinGurantorProofDetail", detail);
					Executions.createComponents("/WEB-INF/pages/util/ImageView.zul", null, map);
				} catch (Exception e) {
					logger.debug(e);
				}

			} else {
				PTMessageUtils.showErrorMessage("Please Upload an Proof Before View.");
			}

		}
		logger.debug("Leaving" + event.toString());
	}

	public void onFinGurantorItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxGurantorsDetail.getSelectedItem();
		if (item != null) {
			int index = item.getIndex();
			// CAST AND STORE THE SELECTED OBJECT
			final GuarantorDetail guarantorDetail = (GuarantorDetail) item.getAttribute("data");
			if (StringUtils.equalsIgnoreCase(guarantorDetail.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("guarantorDetail", guarantorDetail);
				map.put("finJointAccountCtrl", this);
				map.put("roleCode", roleCode);
				map.put("moduleType", "");
				map.put("index", index);
				map.put("ccDecimal", ccDecimal);
				map.put("ccy", ccy);
				map.put("filter", getGurantorFilter());
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/Finance/GuarantorDetail/GuarantorDetailDialog.zul", window_JointAccountDetailDialog, map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / " + e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	private void updateFinanceDetails() {
		try {
			Object object = financeMainDialogCtrl.getClass().getMethod("getFinanceMain").invoke(financeMainDialogCtrl);
			if (object != null) {
				FinanceMain main = (FinanceMain) object;
				finreference = main.getFinReference();
				custCIF = main.getLovDescCustCIF();
				ccDecimal = main.getLovDescFinFormatter();
				ccy = main.getFinCcy();
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
	private String[] getjointAcFilter(){
		if (this.guarantorDetailList!=null && guarantorDetailList.size()>0) {
			String cif[]=new String[guarantorDetailList.size()+1];
			for (int i = 0; i <  guarantorDetailList.size(); i++) {
				cif[i]=guarantorDetailList.get(i).getGuarantorCIF();
			}
			cif[cif.length-1]=custCIF;
			return cif;
		}else{
			String cif[]=new String[1];
			cif[0]=custCIF;
			return cif;
		}
	}
	private String[] getGurantorFilter(){
		if (this.jountAccountDetailList!=null && jountAccountDetailList.size()>0) {
			String cif[]=new String[jountAccountDetailList.size()+1];
			for (int i = 0; i <  jountAccountDetailList.size(); i++) {
				cif[i]=jountAccountDetailList.get(i).getCustCIF();
			}
			cif[cif.length-1]=custCIF;
			return cif;
		}else{
			String cif[]=new String[1];
			cif[0]=custCIF;
			return cif;
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public List<JointAccountDetail> getOldVar_JountAccountDetailList() {
		return oldVar_JountAccountDetailList;
	}
	public void setOldVar_JountAccountDetailList(List<JointAccountDetail> oldVarJountAccountDetailList) {
		this.oldVar_JountAccountDetailList = oldVarJountAccountDetailList;
	}

	public List<JointAccountDetail> getJountAccountDetailList() {
		return jountAccountDetailList;
	}
	public void setJountAccountDetailList(List<JointAccountDetail> jountAccountDetailList) {
		this.jountAccountDetailList = jountAccountDetailList;
	}

	public List<GuarantorDetail> getGuarantorDetailList() {
		return guarantorDetailList;
	}
	public void setGuarantorDetailList(List<GuarantorDetail> guarantorDetailList) {
		this.guarantorDetailList = guarantorDetailList;
	}

	public List<GuarantorDetail> getOldVar_GuarantorDetailList() {
		return oldVar_GuarantorDetailList;
	}
	public void setOldVar_GuarantorDetailList(List<GuarantorDetail> oldVarGuarantorDetailList) {
		this.oldVar_GuarantorDetailList = oldVarGuarantorDetailList;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}
	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setGuarantorDetailService(GuarantorDetailService guarantorDetailService) {
		this.guarantorDetailService = guarantorDetailService;
	}
	public GuarantorDetailService getGuarantorDetailService() {
		return guarantorDetailService;
	}
}
