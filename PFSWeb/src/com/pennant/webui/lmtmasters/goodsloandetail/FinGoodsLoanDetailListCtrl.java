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
 * FileName    		:  FinGoodsLoanDetailListCtrl.java                                                   * 	  
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

import org.apache.commons.lang.StringUtils;
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
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.lmtmasters.GoodsLoanDetail;
import com.pennant.backend.service.lmtmasters.GoodsLoanDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/GoodsLoanDetail/goodsLoanDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FinGoodsLoanDetailListCtrl extends GFCBaseCtrl implements Serializable {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(FinGoodsLoanDetailListCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_GoodsLoanDetailDialogList;
	protected ExtendedCombobox sellerID;
	protected Button button_GoodsLoanDetailList_NewGoodsLoanDetail;
	protected Space space_SellerID;
	protected Label label_SellerID;
	protected Hlayout hlayout_SellerID;
	protected Textbox sellerPhone;
	protected Textbox sellerFax;
	protected Decimalbox salePrice;
	protected Decimalbox downPayment;
	protected Textbox purchaseOrder; 
	protected Datebox purchaseDate; 
	protected Textbox quotationNo;
	protected Datebox quotationDate;
	
	// not auto wired vars
	private GoodsLoanDetail goodsLoanDetail; // overhanded per param
	private transient GoodsLoanDetailService goodsLoanDetailService;
	protected Listbox listBoxGoodsLoanDetail;
	
	// For Dynamically calling of this Controller
	private FinanceDetail financedetail;
	private Object financeMainDialogCtrl;
	private Tabpanel panel = null;
	private List<GoodsLoanDetail> goodsDetailLists = new ArrayList<GoodsLoanDetail>();
	private List<GoodsLoanDetail> old_goodsDetailLists = new ArrayList<GoodsLoanDetail>();
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
	public FinGoodsLoanDetailListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected GoodsLoanDetail object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_GoodsLoanDetailDialogList(ForwardEvent event) throws Exception {
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
				this.window_GoodsLoanDetailDialogList.setTitle("");
				newFinance = true;
			}
			
			if (args.containsKey("roleCode")) {
				getUserWorkspace().alocateRoleAuthorities((String) args.get("roleCode"), "GoodsLoanDetailDialog");
				getUserWorkspace().alocateAuthorities("GoodsLoanDetailDialog",(String) args.get("roleCode"));
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
					setGoodsDetailLists(getFinancedetail().getGoodsLoanDetails());
					fillGoodLoanDEtails(goodsDetailLists);
					main = getFinancedetail().getFinScheduleData().getFinanceMain();
				}
			}
			
			this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue() - PennantConstants.borderlayoutMainNorth;
			this.listBoxGoodsLoanDetail.setHeight(this.borderLayoutHeight - 100 + "px");
			
			doCheckRights();
			doStoreInitValues();
			doShowDialog(getGoodsLoanDetail());
		} catch (Exception e) {
			createException(window_GoodsLoanDetailDialogList, e);
			logger.error(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.sellerID.setInputAllowed(false);
		this.sellerID.setDisplayStyle(3);
		this.sellerID.setModuleName("VehicleDealer");
		this.sellerID.setValueColumn("DealerId");
		this.sellerID.setDescColumn("DealerName");
		this.sellerID.setValidateColumns(new String[] { "DealerId" });
		Filter filter[] = new Filter[1];
		filter[0] = new Filter("DealerType", "S", Filter.OP_EQUAL);
		this.sellerID.setFilters(filter);
		this.sellerID.setMandatoryStyle(true);
		this.purchaseOrder.setMaxlength(100);
		this.quotationNo.setMaxlength(100);
        this.purchaseDate.setFormat(PennantConstants.dateFormat);
        this.quotationDate.setFormat(PennantConstants.dateFormat);
		this.salePrice.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
        this.downPayment.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		logger.debug("Leaving");
	}

	
	
	private void dowriteBeanToComponents(boolean isEdit) {
		if (main != null) {
			
			this.salePrice.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
			this.downPayment.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
			
			this.salePrice.setValue(PennantAppUtil.formateAmount(main.getFinAmount(), ccyFormat));
			this.downPayment.setValue(PennantAppUtil.formateAmount(main.getDownPayment(), ccyFormat));
		}
		
		if (isEdit && getGoodsDetailLists() != null && getGoodsDetailLists().size() > 0) {
			GoodsLoanDetail goodsLoanDetail = getGoodsDetailLists().get(0);
			this.sellerID.setValue(String.valueOf(goodsLoanDetail.getSellerID()));
			this.sellerID.setDescription(goodsLoanDetail.getLovDescSellerID());
			this.sellerFax.setValue(goodsLoanDetail.getLovDescSellerPhone());
			this.sellerPhone.setValue(goodsLoanDetail.getLovDescSellerFax());
			this.purchaseOrder.setValue(goodsLoanDetail.getAddtional1()); 
			this.purchaseDate.setValue(goodsLoanDetail.getAddtional5()); 
			this.quotationNo.setValue(goodsLoanDetail.getAddtional2()); 
			this.quotationDate.setValue(goodsLoanDetail.getAddtional6()); 
		}
	}

	private void doStoreInitValues() {
		if (getGoodsDetailLists() != null) {
			this.old_goodsDetailLists.addAll(getGoodsDetailLists());
		}
	}

	private void doCheckRights() {
		
		this.button_GoodsLoanDetailList_NewGoodsLoanDetail.setVisible(getUserWorkspace().isAllowed("button_GoodsLoanDetailDialog_NewGoodsLoanDetail"));
		this.sellerID.setReadonly(isReadOnly("GoodsLoanDetailDialog_sellerId"));
		this.purchaseOrder.setReadonly(true);//isReadOnly("GoodsLoanDetailDialog_purchaseOrder")
		this.quotationNo.setReadonly(isReadOnly("GoodsLoanDetailDialog_quotationNo"));
		this.purchaseDate.setDisabled(isReadOnly("GoodsLoanDetailDialog_purchaseDate"));
		this.quotationDate.setDisabled(isReadOnly("GoodsLoanDetailDialog_quotationDate"));
		
		if (getGoodsDetailLists() != null && getGoodsDetailLists().size() > 0) {
			this.sellerID.setReadonly(true);
		}
		
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
	 * @param aGoodsLoanDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(GoodsLoanDetail aGoodsLoanDetail) throws InterruptedException {
		logger.debug("Entering");
		// if aGoodsLoanDetail == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aGoodsLoanDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aGoodsLoanDetail = getGoodsLoanDetailService().getNewGoodsLoanDetail();
			setGoodsLoanDetail(aGoodsLoanDetail);
		} else {
			setGoodsLoanDetail(aGoodsLoanDetail);
		}
		
		dowriteBeanToComponents(true);
		doCheckEnquiry();
		try {
			// fill the components with the data
			// stores the initial data for comparing if they are changed
			// during user action.
			if (panel != null) {
				this.window_GoodsLoanDetailDialogList.setHeight(borderLayoutHeight - 75 + "px");
				panel.appendChild(this.window_GoodsLoanDetailDialogList);
			}
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	private void doCheckEnquiry() {
		if(isEnquiry){
			this.button_GoodsLoanDetailList_NewGoodsLoanDetail.setVisible(false);
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
				
				String purchaseOrderNum = "";
				if(!StringUtils.trimToEmpty(main.getLovDescCustCIF()).equals("")){
					purchaseOrderNum = main.getLovDescCustCIF() +"-";
				}
				if(!StringUtils.trimToEmpty(main.getFinBranch()).equals("")){
					purchaseOrderNum = purchaseOrderNum + main.getFinBranch() + "-";
				}
				if(!StringUtils.trimToEmpty(main.getLovDescCustCIF()).equals("")){
					purchaseOrderNum = purchaseOrderNum + main.getFinReference();
				}
				this.purchaseOrder.setValue(purchaseOrderNum);
					
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
		if (Long.valueOf(this.sellerID.getValue()) != 0) {
			recSave = false;
		}
		if (!recSave) {
			assetvalidation();
		}
		if (getFinanceMainDialogCtrl() != null) {
			try {
				if (goodsDetailLists != null && goodsDetailLists.size() > 0) {
					for (GoodsLoanDetail detail : goodsDetailLists) {
						detail.setAddtional1(this.purchaseOrder.getValue());
						detail.setAddtional2(this.quotationNo.getValue());
						detail.setAddtional5(this.purchaseDate.getValue());
						detail.setAddtional6(this.quotationDate.getValue());
					}
				}
				financeMainDialogCtrl.getClass().getMethod("setGoodsLoanDetailList", List.class).invoke(financeMainDialogCtrl, goodsDetailLists);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	private void assetvalidation() {
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if (this.listBoxGoodsLoanDetail.getItems() == null || this.listBoxGoodsLoanDetail.getItems().isEmpty()) {
				throw new WrongValueException(this.listBoxGoodsLoanDetail, "Good Details Must Be Entered ");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (Long.valueOf(this.sellerID.getValue()) == 0) {
				throw new WrongValueException(this.sellerID, Labels.getLabel("FIELD_IS_MAND", new String[] { Labels.getLabel("label_GoodsLoanDetailDialog_SellerID.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.purchaseOrder.getValue() == null || this.purchaseOrder.getValue().equals("")) {
				throw new WrongValueException(this.purchaseOrder, Labels.getLabel("FIELD_IS_MAND", new String[] { Labels.getLabel("label_GoodsLoanDetailDialog_PurchaseOrder.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.quotationNo.getValue() == null || this.quotationNo.getValue().equals("")) {
				throw new WrongValueException(this.quotationNo, Labels.getLabel("FIELD_IS_MAND", new String[] { Labels.getLabel("label_GoodsLoanDetailDialog_QuotationNo.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.purchaseDate.getValue() == null) {
				throw new WrongValueException(this.purchaseDate, Labels.getLabel("FIELD_IS_MAND", new String[] { Labels.getLabel("label_GoodsLoanDetailDialog_PurchaseDate.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.quotationDate.getValue() == null) {
				throw new WrongValueException(this.quotationDate, Labels.getLabel("FIELD_IS_MAND", new String[] { Labels.getLabel("label_GoodsLoanDetailDialog_QuotationDate.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.quotationDate.getValue() != null && this.purchaseDate.getValue() != null) {
				if (this.purchaseDate.getValue().compareTo(this.quotationDate.getValue()) < 0) {
					throw new WrongValueException(this.purchaseDate, Labels.getLabel("DATE_ALLOWED_AFTER", new String[] { Labels.getLabel("label_GoodsLoanDetailDialog_PurchaseDate.value"), Labels.getLabel("label_GoodsLoanDetailDialog_QuotationDate.value") }));
				}
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
			if (main != null && this.purchaseDate.getValue() != null && main.getFinStartDate() != null) {
				if (main.getFinStartDate().compareTo(this.purchaseDate.getValue()) < 0) {
					throw new WrongValueException(this.purchaseDate, Labels.getLabel("DATE_ALLOWED_BEFORE", new String[] { Labels.getLabel("label_GoodsLoanDetailDialog_PurchaseDate.value"), Labels.getLabel("label_FinStartDate") }));
				}
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
					throw new WrongValueException(this.salePrice, Labels.getLabel("MUST_BE_EQUAL", new String[] { Labels.getLabel("label_GoodsLoanDetailDialog_Total_cost"), Labels.getLabel("label_GoodsLoanDetailDialog_SalePrice.value") }));
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
		if (goodsDetailLists != null) {
			 if(old_goodsDetailLists.size()>0 && goodsDetailLists.size() > 0){
			   if (old_goodsDetailLists != goodsDetailLists) {
				return true;
		     	}
			 }
		}
		return false;
	}

	public void onClick$button_GoodsLoanDetailList_NewGoodsLoanDetail(Event event) throws InterruptedException {
		final GoodsLoanDetail aGoodsLoanDetail = getGoodsLoanDetailService().getNewGoodsLoanDetail();
		final HashMap<String, Object> map = new HashMap<String, Object>();
		updateFinanceDetails();
		if (validate()) {
			aGoodsLoanDetail.setLoanRefNumber(main.getFinReference());
			this.sellerID.getValidatedValue();
			aGoodsLoanDetail.setSellerID(Long.valueOf(this.sellerID.getValue()));
			aGoodsLoanDetail.setLovDescSellerID(this.sellerID.getDescription());
			aGoodsLoanDetail.setLovDescSellerFax(this.sellerFax.getValue());
			aGoodsLoanDetail.setLovDescSellerPhone(this.sellerPhone.getValue());
			aGoodsLoanDetail.setItemNumber(String.valueOf(getItemNumberId()));
																																										// Summary
			aGoodsLoanDetail.setNewRecord(true);
			map.put("goodsLoanDetail", aGoodsLoanDetail);
			map.put("ccyFormatter", ccyFormat);
			map.put("finGoodsLoanDetailListCtrl", this);
			map.put("newRecord", "true");
			map.put("roleCode", roleCode);
			map.put("financeMainDialogCtrl", this.financeMainDialogCtrl);

			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/LMTMasters/GoodsLoanDetail/GoodsLoanDetailDialogList.zul", null, map);
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
			for (GoodsLoanDetail goodsLoanDetail : getGoodsDetailLists()) {
				int tempId = Integer.valueOf(goodsLoanDetail.getItemNumber());
				if(tempId > idNumber){
					idNumber = tempId;
				}
			}
		}
		return idNumber+1;
	}
	
	public void onGoodsLoanDetailItemDoubleClicked(Event event) throws InterruptedException {
		Listitem listitem = this.listBoxGoodsLoanDetail.getSelectedItem();
		if (listitem != null && listitem.getAttribute("data") != null) {
			final GoodsLoanDetail aGoodsLoanDetail = (GoodsLoanDetail) listitem.getAttribute("data");
			aGoodsLoanDetail.setNewRecord(false);
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("goodsLoanDetail", aGoodsLoanDetail);
			map.put("ccyFormatter", ccyFormat);
			map.put("finGoodsLoanDetailListCtrl", this);
			map.put("financeMainDialogCtrl", this.financeMainDialogCtrl);
			map.put("roleCode", roleCode);
			map.put("enqModule", isEnquiry);
			
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/LMTMasters/GoodsLoanDetail/GoodsLoanDetailDialogList.zul", null, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}
	}

	public void doFillGoodLoanDetails(List<GoodsLoanDetail> goodsLoanDetails) {
		fillGoodLoanDEtails(goodsLoanDetails);
	}

	public void fillGoodLoanDEtails(List<GoodsLoanDetail> goodsLoanDetails) {
		this.listBoxGoodsLoanDetail.getItems().clear();
		if (goodsLoanDetails != null) {
			totCost = BigDecimal.ZERO;
			setGoodsDetailLists(goodsLoanDetails);
			for (GoodsLoanDetail goodsLoanDetail : goodsLoanDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(goodsLoanDetail.getLoanRefNumber());
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
				lc = new Listcell(PennantApplicationUtil.amountFormate(cost, ccyFormat));
				lc.setParent(item);
				lc = new Listcell(goodsLoanDetail.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(goodsLoanDetail.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", goodsLoanDetail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onGoodsLoanDetailItemDoubleClicked");
				this.listBoxGoodsLoanDetail.appendChild(item);
			}
			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell();
			lc.setParent(item);
			lc = new Listcell(Labels.getLabel("label_GoodsLoanDetailDialog_Total_cost"));
			lc.setParent(item);
			lc.setStyle("font-weight:bold");
			lc.setSpan(4);
			lc = new Listcell(PennantApplicationUtil.amountFormate(totCost, ccyFormat));
			lc.setStyle("text-align:right");
			lc.setParent(item);
			lc = new Listcell();
			lc.setSpan(2);
			lc.setParent(item);
			this.listBoxGoodsLoanDetail.appendChild(item);
		}
		if (goodsLoanDetails != null && goodsLoanDetails.size() > 0) {
			this.sellerID.setReadonly(true);
		}else{
			this.sellerID.setReadonly(getUserWorkspace().isReadOnly("GoodsLoanDetailDialog_sellerId"));
		}
	}

	public void onFulfill$sellerID(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = sellerID.getObject();
		if(dataObject instanceof String){
			this.sellerPhone.setValue("");
			this.sellerFax.setValue("");
		}else{
			VehicleDealer details = (VehicleDealer)dataObject;
			if (details != null) {
				this.sellerPhone.setValue(details.getDealerTelephone());
				this.sellerFax.setValue(details.getDealerFax());
				getGoodsLoanDetail().setLovDescSellerPhone(details.getDealerTelephone());
				getGoodsLoanDetail().setLovDescSellerFax(details.getDealerFax());
			}
		}
		updateFinanceDetails();
		logger.debug("Entering" + event.toString());
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

		if (StringUtils.trimToEmpty(this.sellerID.getValue()).equals("")) {
			isValid = false;
			throw new WrongValueException(this.sellerID, Labels.getLabel("CHECK_NO_EMPTY", new String[] { Labels.getLabel("label_GoodsLoanDetailDialog_SellerID.value") }));
		}
		if (this.salePrice.getValue().compareTo(BigDecimal.ZERO) == 0) {
			isValid = false;
			throw new WrongValueException(this.salePrice, Labels.getLabel("FIELD_IS_GREATER", new String[] { Labels.getLabel("label_GoodsLoanDetailDialog_SalePrice.value"), PennantAppUtil.amountFormate(BigDecimal.ZERO, 2) }));
		}
		return isValid;
	}

	private void doClearErrormessages() {
		this.salePrice.setErrorMessage("");
		this.salePrice.clearErrorMessage();
		this.purchaseOrder.setErrorMessage("");
		this.purchaseOrder.clearErrorMessage();
		this.quotationNo.setErrorMessage("");
		this.quotationNo.clearErrorMessage();
		this.purchaseDate.setErrorMessage("");
		this.purchaseDate.clearErrorMessage();
		this.quotationDate.setErrorMessage("");
		this.quotationDate.clearErrorMessage();
		this.sellerID.setErrorMessage("");
		this.sellerID.clearErrorMessage();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public GoodsLoanDetail getGoodsLoanDetail() {
		return this.goodsLoanDetail;
	}
	public void setGoodsLoanDetail(GoodsLoanDetail goodsLoanDetail) {
		this.goodsLoanDetail = goodsLoanDetail;
	}

	public void setGoodsLoanDetailService(GoodsLoanDetailService goodsLoanDetailService) {
		this.goodsLoanDetailService = goodsLoanDetailService;
	}
	public GoodsLoanDetailService getGoodsLoanDetailService() {
		return this.goodsLoanDetailService;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}
	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setGoodsDetailLists(List<GoodsLoanDetail> goodsDetailLists) {
		this.goodsDetailLists = goodsDetailLists;
	}
	public List<GoodsLoanDetail> getGoodsDetailLists() {
		return goodsDetailLists;
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
