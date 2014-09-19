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
 * FileName    		:  SharesDetailDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.lmtmasters.sharesdetail;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.lmtmasters.SharesDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.lmtmasters.SharesDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/SharesDetail/SharesDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FinSharesDetailListCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 4157448822555239535L;
	private final static Logger logger = Logger.getLogger(FinSharesDetailListCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FinSharesDetailDialogList;
	
	protected Textbox companyName;
	//protected Textbox splInstruction;
	protected Button button_SharesDetailList_NewSharesDetail;
	
	// not auto wired vars
	private SharesDetail sharesDetail; // overhanded per param
	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	// Button controller for the CRUD buttons
	// ServiceDAOs / Domain Classes
	
	private transient SharesDetailService sharesDetailService;
	private transient PagedListService pagedListService;
	protected Listbox listBoxSharesDetail;
	protected Caption caption_sharesLoan;

	
	// For Dynamically calling of this Controller
	private FinanceDetail financedetail;
	private Object financeMainDialogCtrl;
	private Tabpanel panel = null;
	private List<SharesDetail> sharesDetailLists = new ArrayList<SharesDetail>();
	public int	                       borderLayoutHeight	= 0;
	private int ccyFormat=0;
	private transient boolean recSave = false;
	private String roleCode = "";
	BigDecimal totface=BigDecimal.ZERO;
	BigDecimal totMarket=BigDecimal.ZERO;
	
	/**
	 * default constructor.<br>
	 */
	public FinSharesDetailListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected SharesDetail object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinSharesDetailDialogList(ForwardEvent event) throws Exception {
		logger.debug("Entring" + event.toString());
		try {
			if (event.getTarget().getParent() != null) {
				panel = (Tabpanel) event.getTarget().getParent();
			}
			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);
			if (args.containsKey("financeMainDialogCtrl")) {
				setFinanceMainDialogCtrl((Object) args.get("financeMainDialogCtrl"));
				this.window_FinSharesDetailDialogList.setTitle("");
				//this.caption_sharesLoan.setVisible(true);
			}
	
			if (args.containsKey("roleCode")) {
				roleCode = (String) args.get("roleCode");
				getUserWorkspace().alocateRoleAuthorities((String) args.get("roleCode"), "SharesDetailDialog");
			}
			if (args.containsKey("ccyFormatter")) {
				ccyFormat = Integer.parseInt(args.get("ccyFormatter").toString());
			}
			if (args.containsKey("financedetail")) {
				setFinancedetail((FinanceDetail) args.get("financedetail"));
				if (getFinancedetail().getSharesDetails()!=null) {
					setSharesDetailLists(getFinancedetail().getSharesDetails());
					doFillSharesDetails(sharesDetailLists);
				}
			}
			this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue() - PennantConstants.borderlayoutMainNorth;
			this.listBoxSharesDetail.setHeight(this.borderLayoutHeight-100+"px");
			
			doEdit();
			doShowDialog(getSharesDetail());
		} catch (Exception e) {
			createException(window_FinSharesDetailDialogList, e);
			logger.error(e);
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		//this.companyName.setDisabled(isReadOnly("SharesDetailDialog_brokerName"));

		this.button_SharesDetailList_NewSharesDetail.setDisabled(isReadOnly("SharesDetailDialog_NewSharesDetail"));
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aSharesDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(SharesDetail aSharesDetail) throws InterruptedException {
		logger.debug("Entering");
		// if aSharesDetail == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aSharesDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aSharesDetail = getSharesDetailService().getNewSharesDetail();
			setSharesDetail(aSharesDetail);
		} else {
			setSharesDetail(aSharesDetail);
		}
		try {
			// fill the components with the data
			// stores the initial data for comparing if they are changed
			// during user action.
			if (panel != null) {
				this.window_FinSharesDetailDialogList.setHeight(borderLayoutHeight-75+"px");
				panel.appendChild(this.window_FinSharesDetailDialogList);
			}
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}
	public void onAssetClose(Event event) {
		logger.debug("Entering" + event.toString());
			try {
					financeMainDialogCtrl.getClass().getMethod("setAssetDataChanged", Boolean.class).invoke(financeMainDialogCtrl, false);
			} catch (Exception e) {
				logger.error(e);
		}
		logger.debug("Leaving" + event.toString());
	}
	@SuppressWarnings("unchecked")
	public void onAssetValidation(Event event){
		logger.debug("Entering" + event.toString());
		String userAction = "";
		Map<String,Object> map = new HashMap<String,Object>();
		if(event.getData() != null){
			map = (Map<String, Object>) event.getData();
		}

		if(map.containsKey("userAction")){
			userAction = (String) map.get("userAction");
		}
		
		recSave = false;
		if(("Save".equalsIgnoreCase(userAction) || "Cancel".equalsIgnoreCase(userAction))
				&& !map.containsKey("agreement")){
			recSave = true;
		}
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		
		try {
			if(!recSave){

				if (this.listBoxSharesDetail.getItems()==null || this.listBoxSharesDetail.getItems().isEmpty()) {
					throw new WrongValueException(this.listBoxSharesDetail,"Shares Details Must Be Entered ");
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		showErrorDetails(wve);
			try {
				financeMainDialogCtrl.getClass().getMethod("setSharesDetails", List.class).invoke(financeMainDialogCtrl, this.sharesDetailLists);
			} catch (Exception e) {
				logger.error(e);
			}
		logger.debug("Leaving" + event.toString());
	}
	
	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		logger.debug("Entering");

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			
			if(panel != null){
				((Tab)panel.getParent().getParent().getFellowIfAny("loanAssetTab")).setSelected(true);
			}
	    	//	this.companyName.setConstraint(""); FIXME
			// groupBox.set
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public SharesDetail getSharesDetail() {
		return this.sharesDetail;
	}

	public void setSharesDetail(SharesDetail sharesDetail) {
		this.sharesDetail = sharesDetail;
	}

	public void setSharesDetailService(SharesDetailService sharesDetailService) {
		this.sharesDetailService = sharesDetailService;
	}

	public SharesDetailService getSharesDetailService() {
		return this.sharesDetailService;
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

	public void setSharesDetailLists(List<SharesDetail> sharesDetailLists) {
		this.sharesDetailLists = sharesDetailLists;
	}

	public List<SharesDetail> getSharesDetailLists() {
		return sharesDetailLists;
	}

	public void onClick$button_SharesDetailList_NewSharesDetail(Event event) throws InterruptedException {
		final SharesDetail aSharesDetail = getSharesDetailService().getNewSharesDetail();
		final HashMap<String, Object> map = new HashMap<String, Object>();
		aSharesDetail.setLoanRefNumber(financedetail.getFinScheduleData().getFinReference());
		aSharesDetail.setLovDescFinProfitAmt(financedetail.getFinScheduleData().getFinanceMain().getTotalProfit());
		aSharesDetail.setNewRecord(true);
		map.put("sharesDetail", aSharesDetail);
		map.put("ccyFormatter", ccyFormat);
		map.put("finSharesDetailListCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", roleCode);
		map.put("financeMainDialogCtrl", this.financeMainDialogCtrl);
	
		
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/LMTMasters/SharesDetail/SharesDetailDialogList.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	public void onSharesDetailItemDoubleClicked(Event event) throws InterruptedException {
		Listitem listitem = this.listBoxSharesDetail.getSelectedItem();
		if (listitem != null && listitem.getAttribute("data") != null) {
			final SharesDetail aSharesDetail = (SharesDetail) listitem.getAttribute("data");
			aSharesDetail.setNewRecord(false);
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("sharesDetail", aSharesDetail);
			map.put("ccyFormatter", ccyFormat);
			map.put("finSharesDetailListCtrl", this);
			map.put("financeMainDialogCtrl", 	this.financeMainDialogCtrl);
			map.put("roleCode", roleCode);
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/LMTMasters/SharesDetail/SharesDetailDialogList.zul", null, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}
	}

	public void doFillSharesDetails(List<SharesDetail> sharesDetails) {
		this.listBoxSharesDetail.getItems().clear();
		if (sharesDetails != null) {
			totface = BigDecimal.ZERO;
			totMarket = BigDecimal.ZERO;
			setSharesDetailLists(sharesDetails);
			for (SharesDetail SharesDetail : sharesDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(SharesDetail.getCompanyName());
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.formateLong(SharesDetail.getQuantity()));
				lc.setParent(item);
			  	lc = new Listcell(PennantApplicationUtil.amountFormate(SharesDetail.getFaceValue(),ccyFormat));
			  	lc.setParent(item);
			  	lc = new Listcell(PennantApplicationUtil.amountFormate(SharesDetail.getMarketValue(),ccyFormat));
			  	lc.setParent(item);
			  	lc = new Listcell(PennantApplicationUtil.amountFormate(SharesDetail.getTotalFaceValue(), ccyFormat));
			  	lc.setStyle("text-align:right");
				totface =totface.add(SharesDetail.getTotalFaceValue());
			  	lc.setParent(item);
			  	lc = new Listcell(PennantApplicationUtil.amountFormate(SharesDetail.getTotalMarketValue(),ccyFormat));
			  	lc.setStyle("text-align:right");
			  	totMarket = totMarket.add(SharesDetail.getTotalMarketValue());
			  	lc.setParent(item);
				lc = new Listcell(SharesDetail.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(SharesDetail.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", SharesDetail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onSharesDetailItemDoubleClicked");
				this.listBoxSharesDetail.appendChild(item);
			}
			
			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell("Total ");
			lc.setParent(item);
			lc.setStyle("font-weight:bold");
			lc.setSpan(4);
			lc = new Listcell(PennantApplicationUtil.amountFormate(totface, ccyFormat));
			lc.setStyle("text-align:right");
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.amountFormate(totMarket, ccyFormat));
			lc.setStyle("text-align:right");
			lc.setParent(item);
			lc = new Listcell();
			lc.setSpan(3);
			lc.setParent(item);
	
			this.listBoxSharesDetail.appendChild(item);
		}
	}

	public void setFinancedetail(FinanceDetail financedetail) {
		this.financedetail = financedetail;
	}

	public FinanceDetail getFinancedetail() {
		return financedetail;
	}

}
