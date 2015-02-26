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
 * FileName    		:  FinFleetVehicleLoanDetailListCtrl.java                                                   * 	  
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
package com.pennant.webui.lmtmasters.fleetvehicleloandetail;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.lmtmasters.CarLoanDetail;
import com.pennant.backend.service.lmtmasters.CarLoanDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/FleetVehicleLoanDetail/fleetVehicleLoanDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FleetVehicleLoanDetailListCtrl extends GFCBaseCtrl implements Serializable {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(FleetVehicleLoanDetailListCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FleetVehicleLoanDetailList;
	protected Button button_FleetVehicleLoanDetailList_NewVehicleLoanDetail;
	
	protected Textbox numOfVehicle;
	protected Decimalbox totVehicleValue;
	
	// not auto wired vars
	private CarLoanDetail carLoanDetail; // overhanded per param
	private transient CarLoanDetailService carLoanDetailService;
	protected Listbox listBoxVehicleLoanDetail;
	
	// For Dynamically calling of this Controller
	private FinanceDetail financedetail;
	private Object financeMainDialogCtrl;
	private Tabpanel panel = null;
	private List<CarLoanDetail> vehicleDetailLists = new ArrayList<CarLoanDetail>();
	private List<CarLoanDetail> old_vehicleDetailLists = new ArrayList<CarLoanDetail>();
	public int borderLayoutHeight = 0;
	private int ccyFormat = 0;
	private transient boolean recSave = false;
	private BigDecimal totCost = BigDecimal.ZERO;
	private FinanceMain main = null;
	private boolean newFinance = false;
	private String roleCode = "";
	private boolean isEnquiry = false;

	/**
	 * default constructor.<br>
	 */
	public FleetVehicleLoanDetailListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected FleetVehicleLoanDetail object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FleetVehicleLoanDetailList(ForwardEvent event) throws Exception {
		logger.debug("Entring" + event.toString());
		
		doSetFieldProperties();
	
		try {
		
			if (event.getTarget().getParent() != null) {
				panel = (Tabpanel) event.getTarget().getParent();
			}
			
			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);
			if (args.containsKey("financeMainDialogCtrl")) {
				setFinanceMainDialogCtrl((Object) args.get("financeMainDialogCtrl"));
				this.window_FleetVehicleLoanDetailList.setTitle("");
				newFinance = true;
			}
			
			if (args.containsKey("roleCode")) {
				getUserWorkspace().alocateRoleAuthorities((String) args.get("roleCode"), "FleetVehicleLoanDetailDialog");
				roleCode = (String) args.get("roleCode");
			}
			
			if (args.containsKey("ccyFormatter")) {
				ccyFormat = Integer.parseInt(args.get("ccyFormatter").toString());
			}
			
			if (args.containsKey("isEnquiry")) {
				isEnquiry = (Boolean) args.get("isEnquiry");
			}
			
			if (args.containsKey("financedetail")) {
				setFinancedetail((FinanceDetail) args.get("financedetail"));
				if (getFinancedetail() != null) {
					setVehicleDetailLists(getFinancedetail().getVehicleLoanDetails());
					fillVehicleLoanDetails(vehicleDetailLists);
					main = getFinancedetail().getFinScheduleData().getFinanceMain();
				}
			}
			
			this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue() - PennantConstants.borderlayoutMainNorth;
			this.listBoxVehicleLoanDetail.setHeight(this.borderLayoutHeight - 100 + "px");
			
			doCheckRights();
			doStoreInitValues();
			doShowDialog(getCarLoanDetail());
		} catch (Exception e) {
			createException(window_FleetVehicleLoanDetailList, e);
			logger.error(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.totVehicleValue.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		logger.debug("Leaving");
	}

	
	
	private void dowriteBeanToComponents(boolean isEdit) {
		logger.debug("Entering");
		if (main != null) {
			this.totVehicleValue.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		}
		logger.debug("Entering");
	}

	private void doStoreInitValues() {
		if (getVehicleDetailLists() != null) {
			this.old_vehicleDetailLists = getVehicleDetailLists();
		}
	}

	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("FleetVehicleLoanDetailDialog",roleCode);
		this.button_FleetVehicleLoanDetailList_NewVehicleLoanDetail.setVisible(getUserWorkspace().isAllowed("button_FleetVehicleLoanDetailDialog_NewFleetVehicleLoanDetail"));
		logger.debug("leaving");
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewFinance()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}
	
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCarLoanDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(CarLoanDetail aCarLoanDetail) throws InterruptedException {
		logger.debug("Entering");
		// if aFleetVehicleLoanDetail == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aCarLoanDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aCarLoanDetail = getCarLoanDetailService().getNewCarLoanDetail();
			setCarLoanDetail(aCarLoanDetail);
		} else {
			setCarLoanDetail(aCarLoanDetail);
		}
		
		dowriteBeanToComponents(true);
		doCheckEnquiry();
		try {
			// fill the components with the data
			// stores the initial data for comparing if they are changed
			// during user action.
			if (panel != null) {
				this.window_FleetVehicleLoanDetailList.setHeight(borderLayoutHeight - 75 + "px");
				panel.appendChild(this.window_FleetVehicleLoanDetailList);
			}
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	private void doCheckEnquiry() {
		if(isEnquiry){
			this.button_FleetVehicleLoanDetailList_NewVehicleLoanDetail.setVisible(false);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onAssetValidation(Event event) {
		logger.debug("Entering" + event.toString());
		String userAction = "";
		Map<String, Object> map = new HashMap<String, Object>();
		if (event.getData() != null) {
			map = (Map<String, Object>) event.getData();
		}
		if (map.containsKey("userAction")) {
			userAction = (String) map.get("userAction");
		}
		recSave = false;
		if (("Save".equalsIgnoreCase(userAction) || "Cancel".equalsIgnoreCase(userAction))
				&& !map.containsKey("agreement")) {
			recSave = true;
		}
		doClearErrormessages();
		//if (Long.valueOf(this.sellerID.getValue()) != 0) {
		//	recSave = false;
		//}
		if (!recSave) {
			assetvalidation();
		}
		if (getFinanceMainDialogCtrl() != null) {
			try {
				financeMainDialogCtrl.getClass().getMethod("setVehicleLoanDetailList", List.class).invoke(financeMainDialogCtrl, vehicleDetailLists);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	private void assetvalidation() {
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if (this.listBoxVehicleLoanDetail.getItems() == null || this.listBoxVehicleLoanDetail.getItems().isEmpty()) {
				throw new WrongValueException(this.listBoxVehicleLoanDetail, Labels.getLabel("NOEMPTY_FleetVehicleLoanDetails"));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
	
		try {
			FinanceMain main = null;
			if (getFinanceMainDialogCtrl() != null) {
				try {
					if (financeMainDialogCtrl.getClass().getMethod("getFinanceMain") != null) {
						Object object = financeMainDialogCtrl.getClass().getMethod("getFinanceMain").invoke(financeMainDialogCtrl);
						if (object != null) {
							main = (FinanceMain) object;
						}
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}
			if (main != null && this.totVehicleValue.getValue() != null && main.getFinAmount() != null) {
				BigDecimal finAmount = main.getFinAmount();
				if (finAmount.compareTo(PennantAppUtil.unFormateAmount(this.totVehicleValue.getValue(),ccyFormat)) != 0) {
					throw new WrongValueException(this.totVehicleValue, Labels.getLabel("MUST_BE_EQUAL", new String[] { Labels.getLabel("label_FleetVehicleLoanDetailList_TotVehicleValue.value"), Labels.getLabel("label_FinanceMainDialog_FinAssetValue.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		if (wve.size() > 0) {
			if (panel != null) {
				((Tab) panel.getParent().getParent().getFellowIfAny("loanAssetTab")).setSelected(true);
			}
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
	}

	/**
	 * Event for checking whethter data has been changed before closing
	 * 
	 * @param event
	 * @return
	 * */
	public void onAssetClose(Event event) {
		logger.debug("Entering" + event.toString());
		if (getFinanceMainDialogCtrl() != null) {
			try {
				doClearErrormessages();
				financeMainDialogCtrl.getClass().getMethod("setAssetDataChanged", Boolean.class).invoke(financeMainDialogCtrl, isDataChanged());
			} catch (Exception e) {
				logger.error(e);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	private boolean isDataChanged() {
		if (vehicleDetailLists != null) {
			 if(old_vehicleDetailLists.size()>0 && vehicleDetailLists.size() > 0){
			   if (old_vehicleDetailLists != vehicleDetailLists) {
				return true;
		     	}
			 }
		}
		return false;
	}

	public void onClick$button_FleetVehicleLoanDetailList_NewVehicleLoanDetail(Event event) throws InterruptedException {
		final CarLoanDetail carLoanDetail = getCarLoanDetailService().getNewCarLoanDetail();
		final HashMap<String, Object> map = new HashMap<String, Object>();
		updateFinanceDetails();
		if (validate()) {
			carLoanDetail.setNewRecord(true);
			carLoanDetail.setLoanRefNumber(main.getFinReference());
			carLoanDetail.setItemNumber(getItemNumberId());
		
			map.put("carLoanDetail", carLoanDetail);
			map.put("ccyFormatter", ccyFormat);
			map.put("fleetVehicleLoanDetailListCtrl", this);
			map.put("newRecord", "true");
			map.put("roleCode", roleCode);
			map.put("financeMainDialogCtrl", this.financeMainDialogCtrl);

			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/LMTMasters/CarLoanDetail/CarLoanDetailDialog.zul", null, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}
		logger.debug("Leaving");
	}

	public int getItemNumberId(){
		int idNumber = 0;
		if(getVehicleDetailLists() != null && !getVehicleDetailLists().isEmpty()){
			for (CarLoanDetail carLoanDetail : getVehicleDetailLists()) {
				int tempId = Integer.valueOf(carLoanDetail.getItemNumber());
				if(tempId > idNumber){
					idNumber = tempId;
				}
			}
		}
		return idNumber+1;
	}
	
	public void onVehicleLoanDetailItemDoubleClicked(Event event) throws InterruptedException {
		Listitem listitem = this.listBoxVehicleLoanDetail.getSelectedItem();
		if (listitem != null && listitem.getAttribute("data") != null) {
			final CarLoanDetail aCarLoanDetail = (CarLoanDetail) listitem.getAttribute("data");
			aCarLoanDetail.setNewRecord(false);
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("carLoanDetail", aCarLoanDetail);
			map.put("ccyFormatter", ccyFormat);
			map.put("fleetVehicleLoanDetailListCtrl", this);
			map.put("financeMainDialogCtrl", this.financeMainDialogCtrl);
			map.put("roleCode", roleCode);
			map.put("enqModule", isEnquiry);
			
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/LMTMasters/CarLoanDetail/CarLoanDetailDialog.zul", null, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}
	}

	public void doFillVehicleLoanDetails(List<CarLoanDetail> vehicleLoanDetails) {
		fillVehicleLoanDetails(vehicleLoanDetails);
	}

	public void fillVehicleLoanDetails(List<CarLoanDetail> vehicleLoanDetails) {
		this.listBoxVehicleLoanDetail.getItems().clear();
		this.numOfVehicle.setValue(String.valueOf(0));
		this.totVehicleValue.setValue(String.valueOf(0));
		if (vehicleLoanDetails != null) {
			totCost = BigDecimal.ZERO;
			setVehicleDetailLists(vehicleLoanDetails);
			for (CarLoanDetail carLoanDetail : vehicleLoanDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(String.valueOf(carLoanDetail.getItemNumber()));
				lc.setParent(item);
				lc = new Listcell(carLoanDetail.getLovDescManufacturerName());
				lc.setParent(item);
				lc = new Listcell(carLoanDetail.getLovDescModelDesc());
				lc.setParent(item);
				lc = new Listcell(carLoanDetail.getLovDescVehicleVersionCode());
				lc.setParent(item);
				lc = new Listcell(String.valueOf(carLoanDetail.getCarMakeYear()));
				lc.setParent(item);
				lc = new Listcell(carLoanDetail.getCarColor());
				lc.setParent(item);
				lc = new Listcell(String.valueOf(carLoanDetail.getCarCapacity()));
				lc.setParent(item);
				lc = new Listcell(String.valueOf(carLoanDetail.getCarCc()));
				lc.setParent(item);
				lc = new Listcell(carLoanDetail.getCarRegNo());
				lc.setParent(item);
				lc = new Listcell(carLoanDetail.getEngineNumber());
				lc.setParent(item);
				lc = new Listcell(carLoanDetail.getCarChasisNo());
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(carLoanDetail.getVehicleValue(),ccyFormat));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(carLoanDetail.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(carLoanDetail.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", carLoanDetail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onVehicleLoanDetailItemDoubleClicked");
				this.listBoxVehicleLoanDetail.appendChild(item);
				if(carLoanDetail.getVehicleValue() != null){
					totCost = totCost.add(carLoanDetail.getVehicleValue());
				}
			}
			this.numOfVehicle.setValue(String.valueOf(vehicleLoanDetails.size()));
			this.totVehicleValue.setValue(PennantAppUtil.formateAmount(totCost,ccyFormat));
			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell(Labels.getLabel("label_FleetVehicleLoanDetailList_TotalAssetValue"));
			lc.setParent(item);
			lc.setStyle("font-weight:bold");
			lc.setSpan(11);
			lc = new Listcell(PennantAppUtil.amountFormate(totCost,ccyFormat));
			lc.setStyle("text-align:right");
			lc.setParent(item);
			lc = new Listcell();
			lc.setSpan(2);
			lc.setParent(item);
			this.listBoxVehicleLoanDetail.appendChild(item);
		}
	}


	private void updateFinanceDetails() {
		if (getFinanceMainDialogCtrl() != null) {
			try {
				if (financeMainDialogCtrl.getClass().getMethod("getFinanceMain") != null) {
					Object object = financeMainDialogCtrl.getClass().getMethod("getFinanceMain").invoke(financeMainDialogCtrl);
					if (object != null) {
						main = (FinanceMain) object;
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
		dowriteBeanToComponents(false);
	}

	private boolean validate() {
		boolean isValid = true;
		doClearErrormessages();

		return isValid;
	}

	private void doClearErrormessages() {
		this.totVehicleValue.setErrorMessage("");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	
	

	public CarLoanDetail getCarLoanDetail() {
		return carLoanDetail;
	}
	public void setCarLoanDetail(CarLoanDetail carLoanDetail) {
		this.carLoanDetail = carLoanDetail;
	}

	public CarLoanDetailService getCarLoanDetailService() {
		return carLoanDetailService;
	}
	public void setCarLoanDetailService(CarLoanDetailService carLoanDetailService) {
		this.carLoanDetailService = carLoanDetailService;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}
	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public List<CarLoanDetail> getVehicleDetailLists() {
		return vehicleDetailLists;
	}
	public void setVehicleDetailLists(List<CarLoanDetail> vehicleDetailLists) {
		this.vehicleDetailLists = vehicleDetailLists;
	}

	public void setFinancedetail(FinanceDetail financedetail) {
		this.financedetail = financedetail;
	}
	public FinanceDetail getFinancedetail() {
		return financedetail;
	}

	public boolean isNewFinance() {
		return newFinance;
	}
	public void setNewFinance(boolean newFinance) {
		this.newFinance = newFinance;
	}

}
