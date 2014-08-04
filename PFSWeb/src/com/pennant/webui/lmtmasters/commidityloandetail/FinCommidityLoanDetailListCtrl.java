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
 * FileName    		:  CommidityLoanDetailDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.lmtmasters.commidityloandetail;

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
import com.pennant.backend.model.finance.commodity.CommodityBrokerDetail;
import com.pennant.backend.model.lmtmasters.CommidityLoanDetail;
import com.pennant.backend.model.lmtmasters.CommidityLoanHeader;
import com.pennant.backend.service.lmtmasters.CommidityLoanDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/CommidityLoanDetail/commidityLoanDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FinCommidityLoanDetailListCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 4157448822555239535L;
	private final static Logger logger = Logger.getLogger(FinCommidityLoanDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CommidityLoanDetailDialogList;

	protected Textbox brokerName;
	protected Textbox lovDescbrokerName;
	protected Button btnSearchbrokerName;
	protected Textbox splInstruction;
	protected Button button_CommidityLoanDetailList_NewCommidityLoanDetail;
	protected Decimalbox finAmount;
	protected Decimalbox totalProfit;

	// not auto wired vars
	private CommidityLoanDetail commidityLoanDetail; // overhanded per param
	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	// Button controller for the CRUD buttons
	// ServiceDAOs / Domain Classes

	private transient CommidityLoanDetailService commidityLoanDetailService;
	protected Listbox listBoxCommidityLoanDetail;

	// For Dynamically calling of this Controller
	private FinanceDetail financedetail;
	private Object financeMainDialogCtrl;
	private Tabpanel panel = null;
	private CommidityLoanHeader commidityLoanHeader;
	private List<CommidityLoanDetail> commidityDetailLists = new ArrayList<CommidityLoanDetail>();
	public int	                       borderLayoutHeight	= 0;
	private int ccyFormat=0;
	private transient boolean recSave = false;
	private String roleCode = "";
	BigDecimal totCost = BigDecimal.ZERO;
	private boolean isEnquiry = false;

	/**
	 * default constructor.<br>
	 */
	public FinCommidityLoanDetailListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected CommidityLoanDetail object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CommidityLoanDetailDialogList(ForwardEvent event) throws Exception {
		logger.debug("Entring" + event.toString());
		try {
			if (event.getTarget().getParent() != null) {
				panel = (Tabpanel) event.getTarget().getParent();
			}
			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);
			if (args.containsKey("financeMainDialogCtrl")) {
				setFinanceMainDialogCtrl((Object) args.get("financeMainDialogCtrl"));
				this.window_CommidityLoanDetailDialogList.setTitle("");
			}

			if (args.containsKey("roleCode")) {
				roleCode = (String) args.get("roleCode");
				getUserWorkspace().alocateRoleAuthorities((String) args.get("roleCode"), "CommidityLoanDetailDialog");
			}
			if (args.containsKey("ccyFormatter")) {
				ccyFormat=Integer.parseInt(args.get("ccyFormatter").toString());
			}
			
			if (args.containsKey("isEnquiry")) {
				isEnquiry = (Boolean) args.get("isEnquiry");
			}
			
			if (args.containsKey("financedetail")) {
				setFinancedetail((FinanceDetail) args.get("financedetail"));
				if (getFinancedetail()!=null) {
					setCommidityLoanHeader(getFinancedetail().getCommidityLoanHeader());
					setCommidityDetailLists(getFinancedetail().getCommidityLoanDetails());
					doFillCommidityLoanDEtails(commidityDetailLists);
				}
			}
			this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue() - PennantConstants.borderlayoutMainNorth;
			this.listBoxCommidityLoanDetail.setHeight(this.borderLayoutHeight-100+"px");

			doEdit();
			doSetFieldProperties();
			doShowDialog(getCommidityLoanDetail());
		} catch (Exception e) {
			createException(window_CommidityLoanDetailDialogList, e);
			logger.error(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		this.finAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.finAmount.setScale(ccyFormat);

		this.totalProfit.setMaxlength(18);
		this.totalProfit.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.totalProfit.setScale(ccyFormat);

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		this.brokerName.setDisabled(isReadOnly("CommidityLoanDetailDialog_brokerName"));
		this.btnSearchbrokerName.setDisabled(isReadOnly("CommidityLoanDetailDialog_brokerName"));
		this.splInstruction.setDisabled(isReadOnly("CommidityLoanDetailDialog_splInstruction"));
		this.button_CommidityLoanDetailList_NewCommidityLoanDetail.setDisabled(isReadOnly("CommidityLoanDetailDialog_NewCommidityLoanDetail"));
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCommidityLoanDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(CommidityLoanDetail aCommidityLoanDetail) throws InterruptedException {
		logger.debug("Entering");

		// if aCommidityLoanDetail == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aCommidityLoanDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aCommidityLoanDetail = getCommidityLoanDetailService().getNewCommidityLoanDetail();
			setCommidityLoanDetail(aCommidityLoanDetail);
		} else {
			setCommidityLoanDetail(aCommidityLoanDetail);
		}
		try {

			if(this.commidityLoanHeader != null){
				this.brokerName.setValue(commidityLoanHeader.getBrokerName());
				this.lovDescbrokerName.setValue(commidityLoanHeader.getBrokerName());
				this.splInstruction.setValue(commidityLoanHeader.getSplInstruction());

				if(!this.commidityLoanHeader.isNew()){
					this.brokerName.setReadonly(true);
					this.btnSearchbrokerName.setDisabled(true);
				}
			}else{
				this.commidityLoanHeader = new CommidityLoanHeader();
				this.commidityLoanHeader.setNewRecord(true);
			}

			FinanceMain financeMain = getFinancedetail().getFinScheduleData().getFinanceMain();
			this.finAmount.setValue(PennantAppUtil.formateAmount(financeMain.getFinAmount(), ccyFormat));
			this.totalProfit.setValue(PennantAppUtil.formateAmount(financeMain.getTotalProfit(), ccyFormat));
			doCheckEnquiry();
			// fill the components with the data
			// stores the initial data for comparing if they are changed
			// during user action.
			if (panel != null) {
				this.window_CommidityLoanDetailDialogList.setHeight(borderLayoutHeight-75+"px");
				panel.appendChild(this.window_CommidityLoanDetailDialogList);
			}

		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}


	private void doCheckEnquiry() {
		if(isEnquiry){
			this.brokerName.setDisabled(true);
			this.btnSearchbrokerName.setDisabled(true);
			this.splInstruction.setDisabled(true);
			this.button_CommidityLoanDetailList_NewCommidityLoanDetail.setVisible(false);
		}
	}
	
	/**
	 * Method for Closing Window
	 * @param event
	 */
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

		FinanceMain main=null;
		String userAction = "";
		Map<String,Object> map = new HashMap<String,Object>();
		if(event.getData() != null){
			map = (Map<String, Object>) event.getData();
		}

		if(map.containsKey("userAction")){
			userAction = (String) map.get("userAction");
		}

		if (map.containsKey("financeMain")) {
			main = (FinanceMain) map.get("financeMain");
		}
		recSave = false;
		if("Save".equalsIgnoreCase(userAction) && !map.containsKey("agreement")){
			recSave = true;
		}

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		//###090414### Start - Commented the Code
		if(!StringUtils.trim(this.brokerName.getValue()).equals("") ||( 
				this.listBoxCommidityLoanDetail.getItems()!=null && !this.listBoxCommidityLoanDetail.getItems().isEmpty())){
			try {
				if(StringUtils.trim(this.brokerName.getValue()).equals("")){
					throw new WrongValueException(this.lovDescbrokerName,Labels.getLabel("FIELD_NO_EMPTY", new String[]{
							Labels.getLabel("label_BrokerName")}));
				}
				this.commidityLoanHeader.setBrokerName(this.brokerName.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				this.commidityLoanHeader.setSplInstruction(this.splInstruction.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if(!recSave ){
					if (this.listBoxCommidityLoanDetail.getItems()==null || this.listBoxCommidityLoanDetail.getItems().isEmpty()) {
						throw new WrongValueException(this.listBoxCommidityLoanDetail,"Commodity Details Must Be Entered ");
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if(!recSave){
					if (main.getFinAmount().compareTo(totCost) != 0) {
						throw new WrongValueException(this.listBoxCommidityLoanDetail, Labels.getLabel("MUST_BE_EQUAL", new String[] { "Total Buy Amount", "Finance Amount" }));
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		showErrorDetails(wve);
		try {
			financeMainDialogCtrl.getClass().getMethod("setCommidityLoanHeader", CommidityLoanHeader.class).invoke(financeMainDialogCtrl, commidityLoanHeader);
			financeMainDialogCtrl.getClass().getMethod("setCommidityLoanDetails", List.class).invoke(financeMainDialogCtrl, commidityDetailLists);
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
			this.brokerName.setConstraint("");

			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	public void onClick$button_CommidityLoanDetailList_NewCommidityLoanDetail(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		final CommidityLoanDetail aCommidityLoanDetail = getCommidityLoanDetailService().getNewCommidityLoanDetail();
		aCommidityLoanDetail.setLoanRefNumber(financedetail.getFinScheduleData().getFinReference());
		aCommidityLoanDetail.setLovDescFinAmount(financedetail.getFinScheduleData().getFinanceMain().getFinAmount());
		aCommidityLoanDetail.setLovDescFinProfitAmt(financedetail.getFinScheduleData().getFinanceMain().getTotalProfit());
		aCommidityLoanDetail.setNewRecord(true);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("commidityLoanDetail", aCommidityLoanDetail);
		map.put("ccyFormatter", ccyFormat);
		map.put("finCommidityLoanDetailListCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", roleCode);
		map.put("financeMainDialogCtrl", 	this.financeMainDialogCtrl);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/LMTMasters/CommidityLoanDetail/CommidityLoanDetailDialogList.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onCommidityLoanDetailItemDoubleClicked(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		Listitem listitem = this.listBoxCommidityLoanDetail.getSelectedItem();
		if (listitem != null && listitem.getAttribute("data") != null) {
			final CommidityLoanDetail aCommidityLoanDetail = (CommidityLoanDetail) listitem.getAttribute("data");
			aCommidityLoanDetail.setNewRecord(false);
			aCommidityLoanDetail.setLovDescFinAmount(financedetail.getFinScheduleData().getFinanceMain().getFinAmount());
			aCommidityLoanDetail.setLovDescFinProfitAmt(financedetail.getFinScheduleData().getFinanceMain().getTotalProfit());

			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("commidityLoanDetail", aCommidityLoanDetail);
			map.put("ccyFormatter", ccyFormat);
			map.put("finCommidityLoanDetailListCtrl", this);
			map.put("financeMainDialogCtrl", 	this.financeMainDialogCtrl);
			map.put("roleCode", roleCode);
			map.put("enqModule", isEnquiry);
			
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/LMTMasters/CommidityLoanDetail/CommidityLoanDetailDialogList.zul", null, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	public void doFillCommidityLoanDEtails(List<CommidityLoanDetail> commidityLoanDetails) {
		logger.debug("Entering");

		this.listBoxCommidityLoanDetail.getItems().clear();
		if (commidityLoanDetails != null) {
			totCost = BigDecimal.ZERO;
			setCommidityDetailLists(commidityLoanDetails);
			for (CommidityLoanDetail commidityLoanDetail : commidityLoanDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(commidityLoanDetail.getLoanRefNumber());
				lc.setParent(item);
				lc = new Listcell(commidityLoanDetail.getLovDescItemDescription());
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.formateLong(commidityLoanDetail.getQuantity()));
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.formatRate(commidityLoanDetail.getUnitBuyPrice().doubleValue(),9));
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(commidityLoanDetail.getBuyAmount(),ccyFormat));
				totCost=totCost.add(commidityLoanDetail.getBuyAmount());
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.formatRate(commidityLoanDetail.getUnitSellPrice().doubleValue(),9));
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(commidityLoanDetail.getSellAmount(),ccyFormat));
				lc.setParent(item);
				lc = new Listcell(commidityLoanDetail.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(commidityLoanDetail.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", commidityLoanDetail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCommidityLoanDetailItemDoubleClicked");
				this.listBoxCommidityLoanDetail.appendChild(item);
			}
			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell();
			lc.setParent(item);
			lc = new Listcell("Total "+Labels.getLabel("listheader_Commidity_BuyAmount.label"));
			lc.setParent(item);
			lc.setStyle("font-weight:bold");
			lc.setSpan(3);
			lc = new Listcell(PennantApplicationUtil.amountFormate(totCost, ccyFormat));
			lc.setStyle("text-align:right");
			lc.setParent(item);
			lc = new Listcell();
			lc.setSpan(4);
			lc.setParent(item);
			this.listBoxCommidityLoanDetail.appendChild(item);
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchbrokerName(Event event){
		logger.debug("Entering" + event.toString());

		Object dataObject = ExtendedSearchListBox.show(this.window_CommidityLoanDetailDialogList,"CommodityBrokerDetail");
		if (dataObject instanceof String){
			this.brokerName.setValue(dataObject.toString());
			this.lovDescbrokerName.setValue("");

		}else{
			CommodityBrokerDetail details= (CommodityBrokerDetail) dataObject;
			if (details != null) {
				this.brokerName.setValue(details.getBrokerCode());
				this.lovDescbrokerName.setValue(details.getBrokerCode()+"-"+details.getBrokerCustID());
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public CommidityLoanDetail getCommidityLoanDetail() {
		return this.commidityLoanDetail;
	}
	public void setCommidityLoanDetail(CommidityLoanDetail commidityLoanDetail) {
		this.commidityLoanDetail = commidityLoanDetail;
	}

	public void setCommidityLoanDetailService(CommidityLoanDetailService commidityLoanDetailService) {
		this.commidityLoanDetailService = commidityLoanDetailService;
	}
	public CommidityLoanDetailService getCommidityLoanDetailService() {
		return this.commidityLoanDetailService;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}
	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setCommidityDetailLists(List<CommidityLoanDetail> commidityDetailLists) {
		this.commidityDetailLists = commidityDetailLists;
	}
	public List<CommidityLoanDetail> getCommidityDetailLists() {
		return commidityDetailLists;
	}

	public void setFinancedetail(FinanceDetail financedetail) {
		this.financedetail = financedetail;
	}
	public FinanceDetail getFinancedetail() {
		return financedetail;
	}

	public void setCommidityLoanHeader(CommidityLoanHeader commidityLoanHeader) {
		this.commidityLoanHeader = commidityLoanHeader;
	}
	public CommidityLoanHeader getCommidityLoanHeader() {
		return commidityLoanHeader;
	}

}
