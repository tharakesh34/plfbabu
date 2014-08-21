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
 * FileName    		:  GenGoodsLoanDetailDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.lmtmasters.goodsloandetail;

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
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.lmtmasters.GenGoodsLoanDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.lmtmasters.GenGoodsLoanDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/GenGoodsLoanDetail/goodsLoanDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FinGenGoodsLoanDetailListCtrl extends GFCBaseCtrl implements Serializable {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(FinGenGoodsLoanDetailListCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_GenGoodsLoanDetailDialogList;
	protected Button button_GenGoodsLoanDetailList_NewGenGoodsLoanDetail;
	protected Space space_SellerID;
	protected Label label_SellerID;
	protected Hlayout hlayout_SellerID;
	protected Decimalbox salePrice;
	protected Decimalbox downPayment;
	// not auto wired vars
	private GenGoodsLoanDetail goodsLoanDetail; // overhanded per param
	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	// Button controller for the CRUD buttons
	// ServiceDAOs / Domain Classes
	private transient GenGoodsLoanDetailService goodsLoanDetailService;
	private transient PagedListService pagedListService;
	protected Listbox listBoxGenGoodsLoanDetail;
	// For Dynamically calling of this Controller
	private FinanceDetail financedetail;
	private Object financeMainDialogCtrl;
	private Tabpanel panel = null;
	private List<GenGoodsLoanDetail> goodsDetailLists = new ArrayList<GenGoodsLoanDetail>();
	private List<GenGoodsLoanDetail> old_goodsDetailLists = new ArrayList<GenGoodsLoanDetail>();
	public int borderLayoutHeight = 0;
	private int ccyFormat = 0;
	private transient boolean recSave = false;
	BigDecimal totCost = new BigDecimal(0);
	private FinanceMain main = null;
	private String roleCode = "";
	private boolean isEnquiry = false;

	/**
	 * default constructor.<br>
	 */
	public FinGenGoodsLoanDetailListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected GenGoodsLoanDetail object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_GenGoodsLoanDetailDialogList(ForwardEvent event) throws Exception {
		logger.debug("Entring" + event.toString());
		try {
			if (event.getTarget().getParent() != null) {
				panel = (Tabpanel) event.getTarget().getParent();
			}
			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);
			if (args.containsKey("financeMainDialogCtrl")) {
				setFinanceMainDialogCtrl((Object) args.get("financeMainDialogCtrl"));
				this.window_GenGoodsLoanDetailDialogList.setTitle("");
			}
			if (args.containsKey("roleCode")) {
				roleCode = (String) args.get("roleCode");
				getUserWorkspace().alocateRoleAuthorities((String) args.get("roleCode"), "GenGoodsLoanDetailDialog");
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
					setGoodsDetailLists(getFinancedetail().getGenGoodsLoanDetails());
					doFillGoodLoanDetails(goodsDetailLists);
				}
			}
			if (getFinancedetail() != null) {
				main = getFinancedetail().getFinScheduleData().getFinanceMain();
			}
			dowriteBeanToComponents();
			this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue() - PennantConstants.borderlayoutMainNorth;
			this.listBoxGenGoodsLoanDetail.setHeight(this.borderLayoutHeight - 100 + "px");
			doCheckRights();
			doStoreInitValues();
			doShowDialog(getGenGoodsLoanDetail());
		} catch (Exception e) {
			createException(window_GenGoodsLoanDetailDialogList, e);
			logger.error(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	private void dowriteBeanToComponents() {
		if (main != null) {
			
			this.salePrice.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
			this.downPayment.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
			
			this.salePrice.setValue(PennantAppUtil.formateAmount(main.getFinAmount(), ccyFormat));
			this.downPayment.setValue(PennantAppUtil.formateAmount(main.getDownPayment(), ccyFormat));
		}
	}

	private void doStoreInitValues() {
		if (getGoodsDetailLists() != null) {
			this.old_goodsDetailLists.addAll(getGoodsDetailLists());
		}
	}

	private void doCheckRights() {
		this.button_GenGoodsLoanDetailList_NewGenGoodsLoanDetail.setVisible(!getUserWorkspace().isReadOnly("GenGoodsLoanDetailDialog_UnitPrice"));
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aGenGoodsLoanDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(GenGoodsLoanDetail aGenGoodsLoanDetail) throws InterruptedException {
		logger.debug("Entering");
		// if aGenGoodsLoanDetail == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aGenGoodsLoanDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aGenGoodsLoanDetail = getGenGoodsLoanDetailService().getNewGenGoodsLoanDetail();
			setGenGoodsLoanDetail(aGenGoodsLoanDetail);
		} else {
			setGenGoodsLoanDetail(aGenGoodsLoanDetail);
		}
		doCheckEnquiry();
		try {
			// fill the components with the data
			// stores the initial data for comparing if they are changed
			// during user action.
			if (panel != null) {
				this.window_GenGoodsLoanDetailDialogList.setHeight(borderLayoutHeight - 75 + "px");
				panel.appendChild(this.window_GenGoodsLoanDetailDialogList);
			}
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	private void doCheckEnquiry() {
		if(isEnquiry){
			this.button_GenGoodsLoanDetailList_NewGenGoodsLoanDetail.setVisible(false);
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
		if (map.containsKey("financeMain")) {
			FinanceMain main = (FinanceMain) map.get("financeMain");
			if (main != null) {
				this.salePrice.setValue(PennantAppUtil.formateAmount(main.getFinAmount(), ccyFormat));
				this.downPayment.setValue(PennantAppUtil.formateAmount(main.getDownPayment(), ccyFormat));
			}
		}
		recSave = false;
		if (("Save".equalsIgnoreCase(userAction) || "Cancel".equalsIgnoreCase(userAction))
				&& !map.containsKey("agreement")) {
			recSave = true;
		}
		doClearErrormessages();
		if (!recSave) {
			assetvalidation();
		}
		if (getFinanceMainDialogCtrl() != null) {
			try {
			//	Class[] paramType = { Class.forName("java.util.List") };
			//	Object[] stringParameter = { goodsDetailLists };
			//	if (financeMainDialogCtrl.getClass().getMethod("setGenGoodsLoanDetailList", List.class) != null) {
					financeMainDialogCtrl.getClass().getMethod("setGenGoodsLoanDetailList", List.class).invoke(financeMainDialogCtrl, goodsDetailLists);
			//	}
			} catch (Exception e) {
				logger.error(e);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	private void assetvalidation() {
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if (this.listBoxGenGoodsLoanDetail.getItems() == null || this.listBoxGenGoodsLoanDetail.getItems().isEmpty()) {
				throw new WrongValueException(this.listBoxGenGoodsLoanDetail, "Good Details Must Be Entered ");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (wve.size() == 0) {
				if (this.salePrice.getValue() == null) {
					this.salePrice.setValue(BigDecimal.ZERO);
				}
				if (this.salePrice.getValue().compareTo(PennantAppUtil.formateAmount(totCost, ccyFormat)) != 0) {
					throw new WrongValueException(this.salePrice, Labels.getLabel("MUST_BE_EQUAL", new String[] { Labels.getLabel("label_GenGoodsLoanDetailDialog_Total_cost"), Labels.getLabel("label_GenGoodsLoanDetailDialog_SalePrice.value") }));
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
			//	Class[] paramType = { Class.forName("java.lang.Boolean") };
			//	Object[] stringParameter = { isDataChanged() };
			//	if (financeMainDialogCtrl.getClass().getMethod("setAssetDataChanged", Boolean.class) != null) {
					financeMainDialogCtrl.getClass().getMethod("setAssetDataChanged", Boolean.class).invoke(financeMainDialogCtrl, isDataChanged());
		  //	   }
			} catch (Exception e) {
				logger.error(e);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	private boolean isDataChanged() {
		if (goodsDetailLists != null) {
			if (old_goodsDetailLists != goodsDetailLists) {
				return true;
			}
		}
		return false;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public GenGoodsLoanDetail getGenGoodsLoanDetail() {
		return this.goodsLoanDetail;
	}

	public void setGenGoodsLoanDetail(GenGoodsLoanDetail goodsLoanDetail) {
		this.goodsLoanDetail = goodsLoanDetail;
	}

	public void setGenGoodsLoanDetailService(GenGoodsLoanDetailService goodsLoanDetailService) {
		this.goodsLoanDetailService = goodsLoanDetailService;
	}

	public GenGoodsLoanDetailService getGenGoodsLoanDetailService() {
		return this.goodsLoanDetailService;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setGoodsDetailLists(List<GenGoodsLoanDetail> goodsDetailLists) {
		this.goodsDetailLists = goodsDetailLists;
	}

	public List<GenGoodsLoanDetail> getGoodsDetailLists() {
		return goodsDetailLists;
	}

	public void onClick$button_GenGoodsLoanDetailList_NewGenGoodsLoanDetail(Event event) throws InterruptedException {
		final GenGoodsLoanDetail aGenGoodsLoanDetail = getGenGoodsLoanDetailService().getNewGenGoodsLoanDetail();
		final HashMap<String, Object> map = new HashMap<String, Object>();
		updateFinanceDetails();
		if (validate()) {
			aGenGoodsLoanDetail.setLoanRefNumber(main.getFinReference());
			aGenGoodsLoanDetail.setItemNumber(String.valueOf(getItemNumberId()));
			aGenGoodsLoanDetail.setNewRecord(true);
			map.put("goodsLoanDetail", aGenGoodsLoanDetail);
			map.put("ccyFormatter", ccyFormat);
			map.put("finGenGoodsLoanDetailListCtrl", this);
			map.put("newRecord", "true");
			map.put("financeMainDialogCtrl", this.financeMainDialogCtrl);
			map.put("roleCode", roleCode);
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/LMTMasters/GenGoodsLoanDetail/GenGoodsLoanDetailDialogList.zul", null, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}
		logger.debug("Leaving");
	}

	public int getItemNumberId(){
		int idNumber = 0;
		if(getGoodsDetailLists() != null && !getGoodsDetailLists().isEmpty()){
			for (GenGoodsLoanDetail genGoodsLoanDetail : getGoodsDetailLists()) {
				int tempId = new Integer(genGoodsLoanDetail.getItemNumber());
				if(tempId > idNumber){
					idNumber = tempId;
				}
			}
		}
		return idNumber+1;
	}
	
	public void onGenGoodsLoanDetailItemDoubleClicked(Event event) throws InterruptedException {
		Listitem listitem = this.listBoxGenGoodsLoanDetail.getSelectedItem();
		if (listitem != null && listitem.getAttribute("data") != null) {
			final GenGoodsLoanDetail aGenGoodsLoanDetail = (GenGoodsLoanDetail) listitem.getAttribute("data");
			aGenGoodsLoanDetail.setNewRecord(false);
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("goodsLoanDetail", aGenGoodsLoanDetail);
			map.put("ccyFormatter", ccyFormat);
			map.put("finGenGoodsLoanDetailListCtrl", this);
			map.put("financeMainDialogCtrl", this.financeMainDialogCtrl);
			map.put("roleCode", roleCode);
			map.put("enqModule", isEnquiry);
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/LMTMasters/GenGoodsLoanDetail/GenGoodsLoanDetailDialogList.zul", null, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}
	}

	public void doFillGoodLoanDetails(List<GenGoodsLoanDetail> goodsLoanDetails) {
		HashMap<String, List<GenGoodsLoanDetail>> selerMap=new HashMap<String, List<GenGoodsLoanDetail>>();
		if (goodsLoanDetails != null && goodsLoanDetails.size() > 0) {
			for (GenGoodsLoanDetail genGoodsLoanDetail : goodsLoanDetails) {
				if (selerMap.containsKey(genGoodsLoanDetail.getLovDescSellerID())) {
					selerMap.get(genGoodsLoanDetail.getLovDescSellerID()).add(genGoodsLoanDetail);
				} else {
					ArrayList<GenGoodsLoanDetail> list = new ArrayList<GenGoodsLoanDetail>();
					list.add(genGoodsLoanDetail);
					selerMap.put(genGoodsLoanDetail.getLovDescSellerID(), list);
				}
			}
		}
		setGoodsDetailLists(goodsLoanDetails);
		fillGoodLoanDEtails(selerMap);
	}

	public void fillGoodLoanDEtails(HashMap<String, List<GenGoodsLoanDetail>> goodsmap) {
		this.listBoxGenGoodsLoanDetail.getItems().clear();
		this.listBoxGenGoodsLoanDetail.setSizedByContent(true);
		Listitem item;
		Listcell lc;
		Listgroup group;
		if (goodsmap != null && goodsmap.size()>0) {
			totCost = new BigDecimal(0);
			BigDecimal subtot=new BigDecimal(0);
			for (String category : goodsmap.keySet()) {
				List<GenGoodsLoanDetail> list = goodsmap.get(category);
				if (list!=null && list.size()>0) {
					group = new Listgroup();
					lc = new Listcell(category);
					lc.setParent(group);
					this.listBoxGenGoodsLoanDetail.appendChild(group);
					subtot=new BigDecimal(0);
					for (GenGoodsLoanDetail goodsLoanDetail : list) {
						item = new Listitem();
						lc = new Listcell("");//goodsLoanDetail.getLovDescSellerID()
						lc.setParent(item);
						lc = new Listcell(goodsLoanDetail.getItemNumber());
						lc.setParent(item);
						lc = new Listcell(goodsLoanDetail.getItemDescription());
						lc.setParent(item);
						lc = new Listcell(PennantApplicationUtil.formateInt(goodsLoanDetail.getQuantity()));
						lc.setParent(item);
						lc = new Listcell(PennantApplicationUtil.amountFormate(goodsLoanDetail.getUnitPrice(), ccyFormat));
						lc.setParent(item);
						BigDecimal cost = new BigDecimal(goodsLoanDetail.getQuantity()).multiply(goodsLoanDetail.getUnitPrice());
						totCost = totCost.add(cost);
						subtot=subtot.add(cost);
						lc = new Listcell(PennantApplicationUtil.amountFormate(cost, ccyFormat));
						lc.setParent(item);
						lc = new Listcell(goodsLoanDetail.getRecordStatus());
						lc.setParent(item);
						lc = new Listcell(PennantJavaUtil.getLabel(goodsLoanDetail.getRecordType()));
						lc.setParent(item);
						item.setAttribute("data", goodsLoanDetail);
						ComponentsCtrl.applyForward(item, "onDoubleClick=onGenGoodsLoanDetailItemDoubleClicked");
						this.listBoxGenGoodsLoanDetail.appendChild(item);
					}
					item = new Listitem();
					lc = new Listcell(Labels.getLabel("label_GenGoodsLoanDetailDialog_Supp_Total_cost"));
					lc.setParent(item);
					lc.setStyle("font-weight:bold");
					lc.setSpan(5);
					lc = new Listcell(PennantApplicationUtil.amountFormate(subtot, ccyFormat));
					lc.setStyle("text-align:right");
					lc.setParent(item);
					lc = new Listcell();
					lc.setSpan(2);
					lc.setParent(item);
					this.listBoxGenGoodsLoanDetail.appendChild(item);
				}

			}
			
			item = new Listitem();
			lc = new Listcell(Labels.getLabel("label_GenGoodsLoanDetailDialog_Total_cost"));
			lc.setParent(item);
			lc.setStyle("font-weight:bold");
			lc.setSpan(5);
			lc = new Listcell(PennantApplicationUtil.amountFormate(totCost, ccyFormat));
			lc.setStyle("text-align:right");
			lc.setParent(item);
			lc = new Listcell();
			lc.setSpan(2);
			lc.setParent(item);
			this.listBoxGenGoodsLoanDetail.appendChild(item);
		}
	}

	public void setFinancedetail(FinanceDetail financedetail) {
		this.financedetail = financedetail;
	}

	public FinanceDetail getFinancedetail() {
		return financedetail;
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
		dowriteBeanToComponents();
	}

	private boolean validate() {
		boolean isValid = true;
		doClearErrormessages();
		// if (main != null) {
		// if (StringUtils.trimToEmpty(main.getFinReference()).equals("")) {
		// try {
		// isValid = false;
		// PTMessageUtils.showErrorMessage(Labels.getLabel("label_GenGoodsLoanDetailDialog_LoanRefNumber.value")
		// + " is Mandatory.");
		// } catch (InterruptedException e) {
		// logger.debug(e);
		// }
		// }
		// }
		//TODO Need to move below validation to Service class(businessValidation)  
		if (this.salePrice.getValue().compareTo(BigDecimal.ZERO) == 0) {
			isValid = false;
			throw new WrongValueException(this.salePrice, Labels.getLabel("FIELD_IS_GREATER", new String[] { Labels.getLabel("label_GenGoodsLoanDetailDialog_SalePrice.value"), PennantAppUtil.amountFormate(BigDecimal.ZERO, 2) }));
		}
		return isValid;
	}

	private void doClearErrormessages() {
		this.salePrice.setErrorMessage("");
		this.salePrice.clearErrorMessage();
	}
}
