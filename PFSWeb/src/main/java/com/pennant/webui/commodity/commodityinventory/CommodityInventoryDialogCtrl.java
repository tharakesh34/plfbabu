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
 * FileName    		:  CommodityInventoryDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-04-2015    														*
 *                                                                  						*
 * Modified Date    :  23-04-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-04-2015       Pennant	                 0.1                                            * 
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
package com.pennant.webui.commodity.commodityinventory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.commodity.CommodityInventory;
import com.pennant.backend.model.commodity.FinCommodityInventory;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.commodity.BrokerCommodityDetail;
import com.pennant.backend.model.finance.commodity.CommodityBrokerDetail;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.commodity.CommodityInventoryService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.ScreenCTL;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the /WEB-INF/pages/Commodity/CommodityInventory/commodityInventoryDialog.zul file.
 */
public class CommodityInventoryDialogCtrl extends GFCBaseCtrl<CommodityInventory> {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(CommodityInventoryDialogCtrl.class);

	/*	
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 				window_CommodityInventoryDialog;

	protected Row 					row0;
	protected Label 				label_BrokerCode;
	protected ExtendedCombobox 		brokerCode;

	protected Row 					row1;
	protected Space 				space_HoldCertificateNo;
	protected Uppercasebox 			holdCertificateNo;
	protected Label 				label_CommodityCode;
	protected Space 				space_CommodityCode;
	protected Combobox 				commodityCode;

	protected ExtendedCombobox 		commodityCcy; 	
	
	protected Row 					row2;
	protected Label 				label_PurchaseDate;
	protected Space 				space_PurchaseDate;
	protected Datebox 				purchaseDate;
	protected Label 				label_FinalSettlementDate;
	protected Space 				space_FinalSettlementDate;
	protected Datebox 				finalSettlementDate;

	protected Row 					row3;
	protected Label 				label_UnitPrice;
	protected Space 				space_UnitPrice;
	protected Decimalbox 			unitPrice;
	protected Label 				label_Quantity;
	protected Space 				space_Quantity;
	protected Longbox 				quantity;

	protected Row 					row4;
	protected Label 				label_Units;
	protected Space 				space_Units;
	protected Combobox 				units;
	protected Label 				label_PurchaseAmount;
	protected CurrencyBox 			purchaseAmount;

	protected Row 					row5;
	protected Label 				label_Location;
	protected ExtendedCombobox		location;
	protected Label 				label_BulkPurchase;
	protected Space 				space_BulkPurchase;
	protected Checkbox 				bulkPurchase;

	protected Row 					row6;
	protected Label 				label_TotalQuantityAllocated;
	protected Longbox 				totalQuantityAllocated;

	protected Label 				label_SoldQuantity;
	protected Longbox 				soldQuantity;

	protected Row 					row7;
	protected Label 				label_AllocatedQuantity;
	protected Longbox 				allocatedQuantity;

	protected Label 				label_CancelledQuantity;
	protected Longbox 				cancelledQuantity;


	protected Tabbox 				tabBoxIndexCenter;                     // autowired
	protected Tabs 					tabsIndexCenter;                       // autowired
	protected Tabpanels 			tabpanelsBoxIndexCenter;               // autowired

	protected Label 				label_heading;
	//protected Label recordType;
	protected Groupbox 				gb_statusDetails;
	protected Groupbox 				gb_CommodityInvEnquiry;

	// not auto wired vars
	private CommodityInventory commodityInventory; // overhanded per param
	private transient CommodityInventoryListCtrl commodityInventoryListCtrl; // overhanded per param

	
	// ServiceDAOs / Domain Classes
	private transient CommodityInventoryService commodityInventoryService;
	private transient PagedListService pagedListService;
	private List<BrokerCommodityDetail> listCommodityCode;

	private int defaultCCYDecPos;
	Date systemDate = DateUtility.getAppDate();
	Date nextThursdayDate = getNextThrusdayDate(systemDate);
	
	
	//posting
	protected Tab tabPosting;
	boolean maintaince=false;
	protected Listbox 		listBoxFinAccountings;
	private PostingsPreparationUtil postingsPreparationUtil;
	/**
	 * default constructor.<br>
	 */
	public CommodityInventoryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CommodityInventoryDialog";
		
		super.enqiryModule = (Boolean) arguments.get("enqiryModule");
	}


	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected CommodityInventory
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CommodityInventoryDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CommodityInventoryDialog);

		try {
			// READ OVERHANDED params !
			if (arguments.containsKey("commodityInventory")) {
				this.commodityInventory = (CommodityInventory) arguments.get("commodityInventory");
				CommodityInventory befImage = new CommodityInventory();
				BeanUtils.copyProperties(this.commodityInventory, befImage);
				this.commodityInventory.setBefImage(befImage);

				setCommodityInventory(this.commodityInventory);
			} else {
				setCommodityInventory(null);
			}
			
			doLoadWorkFlow(this.commodityInventory.isWorkflow(), this.commodityInventory.getWorkflowId(),this.commodityInventory.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), super.pageRightName);
			}
			
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the commodityInventoryListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete commodityInventory here.
			if (arguments.containsKey("commodityInventoryListCtrl")) {
				setCommodityInventoryListCtrl((CommodityInventoryListCtrl) arguments.get("commodityInventoryListCtrl"));
			} else {
				setCommodityInventoryListCtrl(null);
			}
			
			if (!StringUtils.isBlank(getCommodityInventory().getRecordStatus()) && 
					StringUtils.isBlank(getCommodityInventory().getRecordType())) {
				maintaince=true;
			}
			
			// fetch total number of finances which are used commodities 
			if(!enqiryModule) {
				if(maintaince) {
					int count = getCommodityInventoryService().getCommodityFinances(getCommodityInventory().getBrokerCode(), 
							getCommodityInventory().getHoldCertificateNo(), PennantConstants.COMMODITY_CANCELLED); 
					if(count > 0) {
						enqiryModule = true;
						setWorkFlowEnabled(false);
					}
				}
			}

			//set Tabs
			if(enqiryModule) {
				this.label_heading.setValue(Labels.getLabel("panel_commodityInventoryEnquiryList.title"));
				this.label_heading.setSclass("label-heading");
				this.tabBoxIndexCenter.setVisible(true);
				this.row6.setVisible(true);
				this.row7.setVisible(true);

				this.space_HoldCertificateNo.setSclass("");
				this.space_CommodityCode.setSclass("");
				this.space_PurchaseDate.setSclass("");
				this.space_FinalSettlementDate.setSclass("");
				this.space_UnitPrice.setSclass("");
				this.space_Units.setSclass("");
				this.space_Quantity.setSclass("");
			}

			if(isWorkFlowEnabled() && enqiryModule) {
				this.tabBoxIndexCenter.setVisible(true);
				this.row6.setVisible(true);
				this.row7.setVisible(true);
			}
			getBorderLayoutHeight();
			this.listBoxFinAccountings.setHeight((this.borderLayoutHeight- 220) +"px");
			// set Field Properties
			this.defaultCCYDecPos = CurrencyUtil.getFormat(getCommodityInventory().getCommodityCcy());
			doSetFieldProperties();
			doShowDialog(getCommodityInventory());
		} catch (Exception e) {
			createException(window_CommodityInventoryDialog, e);
			logger.error("Exception: ", e);
		}

		logger.debug("Leaving" + event.toString());
	}

	protected Listbox stockDetailsSold;
	protected Listbox stockDetailsAllotted;
	protected Listbox stockDetailsCancel;
	protected Listbox stockDetailsFees;
	/**
	 * This method for setting the list of the tabs.<br>
	 * @throws Exception
	 */
	public void setTabs() {
		logger.debug("Entering");
		
		this.stockDetailsSold.setHeight(getListBoxHeight(5));
		this.stockDetailsAllotted.setHeight(getListBoxHeight(5));
		this.stockDetailsCancel.setHeight(getListBoxHeight(5));
		this.stockDetailsFees.setHeight(getListBoxHeight(5));
		
		List<FinCommodityInventory> appList = getInventoryDetails();
		
		if (appList!=null) {
			renderStockDetails(appList,getFinanceDetailsbyInventory(appList));
			renderStockFees(appList);
		}
		logger.debug("Leaving");
	}
	
	public List<FinCommodityInventory> getInventoryDetails(){
		JdbcSearchObject<FinCommodityInventory> jdbcSearchObject=new JdbcSearchObject<FinCommodityInventory>(FinCommodityInventory.class);
		jdbcSearchObject.addTabelName("FinCommodityInventory");
		jdbcSearchObject.addFilterEqual("BrokerCode", getCommodityInventory().getBrokerCode());
		jdbcSearchObject.addFilterEqual("HoldCertificateNo", getCommodityInventory().getHoldCertificateNo());
		pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		return pagedListService.getBySearchObject(jdbcSearchObject);
	}
	
	
	public List<FinanceMain> getFinanceDetailsbyInventory(List<FinCommodityInventory> appList){
		logger.debug(" Entering ");
		if (appList==null) {
			return null;
		}
		
		List<String> finreference=new ArrayList<String>();
		for (FinCommodityInventory commodityInventory : appList) {
			finreference.add(commodityInventory.getFinreference());
		}
		
		JdbcSearchObject<FinanceMain> jdbcSearchObject=new JdbcSearchObject<FinanceMain>(FinanceMain.class);
		jdbcSearchObject.addTabelName("FinCommInventoryEnq_View");
		jdbcSearchObject.addFilterIn("FinReference", finreference,false);
		logger.debug(" Leaving ");
		return pagedListService.getBySearchObject(jdbcSearchObject);

	}

	
	/**
	 * render the Stock Details Enquiry List
	 * @param commInventory
	 * @param setListToTab
	 */
	private void renderStockFees(List<FinCommodityInventory> list) {
		logger.debug(" Entering ");
		this.stockDetailsFees.getItems().clear();
		
		Date purDate = this.purchaseDate.getValue();
		Date settleDate = this.finalSettlementDate.getValue();
		
		/* since fee on un slod for one million AED  we will calculate fee for 1 AED item;*/  
		BigDecimal fee= getCommodityInventory().getFeeOnUnsold();
		fee=PennantApplicationUtil.formateAmount(fee, CurrencyUtil.getFormat(SysParamUtil.getAppCurrency()));
		fee = fee.divide(new BigDecimal(1000000));
		fee = CalculationUtil.getConvertedAmount(SysParamUtil.getAppCurrency(), this.commodityCcy.getValue(), fee);
		
		Long quatity = this.quantity.longValue();
		BigDecimal untiPrice = this.unitPrice.getValue()!=null?this.unitPrice.getValue():BigDecimal.ZERO;
		Date appDate=DateUtility.getAppDate();
		SortedMap<Date, BigDecimal> hashMap=new TreeMap<Date, BigDecimal>();
		hashMap.put(purDate, BigDecimal.valueOf(getQuantityByDate(purDate,list)));
		Calendar tempcAL = Calendar.getInstance();
		Date tempDate=purDate;
		
		for (;;) {
			long datelong=tempDate.getTime();
			long oneday=1000*60*60*24;
			datelong=datelong+oneday;
			tempcAL.setTimeInMillis(datelong);
			tempDate = tempcAL.getTime();
	
			hashMap.put(tempDate, BigDecimal.valueOf(getQuantityByDate(tempDate,list)));
			if (tempDate.compareTo(appDate)>=0) {
				break;
			}
			if (tempDate.compareTo(appDate)>=0) {
				break;
			}
			
			if (tempDate.compareTo(settleDate)>=0) {
				break;
			}
		}
		
		BigDecimal rem=new BigDecimal(quatity);
		for (Date date : hashMap.keySet()) {
			if (rem.compareTo(BigDecimal.ZERO)==0) {
				break;
			}
			Listitem item = new Listitem();
			Listcell lc;
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(date);
			lc = new Listcell(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH));
			lc.setParent(item);
			lc = new Listcell(String.valueOf(date));
			lc = new Listcell(DateUtility.formatToLongDate(date));
			lc.setParent(item);
			rem=new BigDecimal(quatity).subtract(hashMap.get(date));
			lc = new Listcell(String.valueOf(rem));
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.formatAmount(rem.multiply(untiPrice).multiply(fee), defaultCCYDecPos, false));
			lc.setParent(item);
			this.stockDetailsFees.appendChild(item);
		}
		
		logger.debug(" Leaving ");
		
	}
	
	/**
	 * @param purDate
	 * @param commInventory
	 * @return
	 */
	private long getQuantityByDate(Date purDate, List<FinCommodityInventory> commInventory) {
		logger.debug(" Entering ");
		long quantity = 0L;
		for (FinCommodityInventory commodityInventory : commInventory) {
			String status = commodityInventory.getCommodityStatus();
			if (!status.equals(PennantConstants.COMMODITY_CANCELLED) && commodityInventory.getDateOfSelling() != null) {
				if (commodityInventory.getDateOfSelling().compareTo(purDate) == 0) {
					quantity = quantity + commodityInventory.getSaleQuantity();
				}
			}

		}
		logger.debug(" Entering ");
		return quantity;
	}


	/**
	 * render the Stock Details Inquiry List
	 * @param commInventory
	 * @param list 
	 * @param setListToTab
	 */
	private void renderStockDetails(List<FinCommodityInventory> commInventory, List<FinanceMain> list) {
		logger.debug(" Entering ");
		
		this.stockDetailsSold.getItems().clear();
		this.stockDetailsCancel.getItems().clear();
		this.stockDetailsFees.getItems().clear();

		long soldandAllocated = 0L;
		long sold = 0L;
		long allocated = 0L;
		long cancelled = 0L;
		
		for (FinCommodityInventory commodityInventory : commInventory) {
			String status = StringUtils.trimToEmpty(commodityInventory.getCommodityStatus());
			FinanceMain financeMain = new FinanceMain();
			for (FinanceMain finMain : list) {
				if (finMain.getFinReference().equals(commodityInventory.getFinreference())) {
					financeMain = finMain;
					break;
				}
			}
			
			int format = CurrencyUtil.getFormat(financeMain.getFinCcy());

			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell(commodityInventory.getFinreference());
			lc.setParent(item);
			lc = new Listcell(financeMain.getLovDescCustCIF());
			lc.setParent(item);
			lc = new Listcell(financeMain.getLovDescCustShrtName());
			lc.setParent(item);
			lc = new Listcell(String.valueOf(commodityInventory.getSaleQuantity()));
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.formatAmount(commodityInventory.getSalePrice(), format, false));
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.formatAmount(commodityInventory.getUnitSalePrice(), format, false));
			lc.setParent(item);
			
			String date="";
			
			switch (status) {
			
			case PennantConstants.COMMODITY_ALLOCATED:
				
				soldandAllocated=soldandAllocated + commodityInventory.getSaleQuantity();
				allocated=allocated + commodityInventory.getSaleQuantity();
				
				date=DateUtility.formatToShortDate(commodityInventory.getDateOfAllocation());
				
				item.setParent(this.stockDetailsAllotted);
				break;
			case PennantConstants.COMMODITY_SOLD:
				
				soldandAllocated=soldandAllocated + commodityInventory.getSaleQuantity();
				sold=sold + commodityInventory.getSaleQuantity();
				
				date=DateUtility.formatToShortDate(commodityInventory.getDateOfSelling());
				item.setParent(this.stockDetailsSold);
				
				break;
			case PennantConstants.COMMODITY_CANCELLED:
				cancelled=cancelled + commodityInventory.getSaleQuantity();
				date=DateUtility.formatToShortDate(commodityInventory.getDateCancelled());
				item.setParent(this.stockDetailsCancel);
				
				break;
			default:
				break;
			}
			
			lc = new Listcell(date);
			lc.setParent(item);
		}
		
		this.totalQuantityAllocated.setValue(soldandAllocated);
		this.soldQuantity.setValue(sold);
		this.allocatedQuantity.setValue(allocated);
		this.cancelledQuantity.setValue(cancelled);
		logger.debug(" Leaving ");
	}
	
	/**
	 * Set Purchase price 
	 */
	private void setPurchasePrice(){
		logger.debug("Entering");
		BigDecimal purAmount=this.purchaseAmount.getActualValue()==null?BigDecimal.ZERO:this.purchaseAmount.getActualValue();
		Long quantity=this.quantity.longValue();
		if (purAmount.compareTo(BigDecimal.ZERO)!=0 && quantity !=0) {
			BigDecimal untPrice=purAmount.divide(new BigDecimal(quantity),defaultCCYDecPos,RoundingMode.HALF_DOWN);
			this.unitPrice.setValue(untPrice);
			
		}
		
		logger.debug("Leaving");
	}
	
	
	/**
	 * when change the unit price  <br>
	 * 
	 * @param event
	 */
	public void onFulfill$purchaseAmount(Event event) {
		logger.debug("Entering" + event.toString());
		setPurchasePrice();
		logger.debug("Leaving" + event.toString());
	}
	
	
	/**
	 * when change the quantity  <br>
	 * 
	 * @param event
	 */
	public void onChange$quantity(Event event) {
		logger.debug("Entering" + event.toString());
		setPurchasePrice();
		logger.debug("Leaving" + event.toString());
	}
	
	
	

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());

		this.brokerCode.setMandatoryStyle(true);
		doEdit();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		doDelete();

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		doSave();

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());

		doCancel();

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		MessageUtil.showHelpWindow(event, window_CommodityInventoryDialog);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		try {

			ScreenCTL.displayNotes(getNotes("CommodityInventory", String.valueOf(getCommodityInventory().getCommodityInvId()),
					getCommodityInventory().getVersion()), this);

		} catch (Exception e) {
			logger.error("Exception: Opening window", e);

			MessageUtil.showErrorMessage(e.toString());
		}

		logger.debug("Leaving" + event.toString());

	}

	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCommodityInventory
	 * @throws InterruptedException
	 */
	public void doShowDialog(CommodityInventory aCommodityInventory) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aCommodityInventory.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.brokerCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.brokerCode.focus();
				if (StringUtils.isNotBlank(aCommodityInventory.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}

			if(enqiryModule) {
				this.btnCtrl.setBtnStatus_Enquiry();
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aCommodityInventory);
			
			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.commodityInventory.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getCommodityInventory().isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		this.brokerCode.setReadonly(isReadOnly("CommodityInventoryDialog_BrokerCode"));
		this.holdCertificateNo.setReadonly(isReadOnly("CommodityInventoryDialog_HoldCertificateNo"));
		this.commodityCode.setDisabled(isReadOnly("CommodityInventoryDialog_CommodityCode"));
		this.commodityCcy.setReadonly(isReadOnly("CommodityInventoryDialog_CommodityCode"));//TODO
		this.purchaseDate.setDisabled(isReadOnly("CommodityInventoryDialog_PurchaseDate"));
		this.finalSettlementDate.setDisabled(isReadOnly("CommodityInventoryDialog_FinalSettlementDate"));
		this.purchaseAmount.setReadonly(isReadOnly("CommodityInventoryDialog_PurchaseAmount")); 
		this.unitPrice.setReadonly(true); //Set to true as the component should not be Editable even if the right is given 
		this.units.setDisabled(true);
		this.quantity.setReadonly(isReadOnly("CommodityInventoryDialog_Quantity"));
		this.location.setReadonly(isReadOnly("CommodityInventoryDialog_Location"));
		this.bulkPurchase.setDisabled(isReadOnly("CommodityInventoryDialog_BulkPurchage"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.commodityInventory.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
				
		logger.debug("Leaving ");
	}
	
	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.brokerCode.setReadonly(true);
		this.holdCertificateNo.setReadonly(true);
		this.commodityCcy.setReadonly(true);
		this.commodityCode.setDisabled(true);
		this.purchaseDate.setDisabled(true);
		this.finalSettlementDate.setDisabled(true);
		this.purchaseAmount.setReadonly(true);
		this.unitPrice.setReadonly(true);
		this.units.setDisabled(true);
		this.quantity.setReadonly(true);
		this.location.setReadonly(true);
		this.bulkPurchase.setDisabled(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		
		logger.debug("Leaving");
	}

	// Helpers

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
		
		getUserWorkspace().allocateAuthorities(super.pageRightName);
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CommodityInventoryDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CommodityInventoryDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CommodityInventoryDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CommodityInventoryDialog_btnSave"));

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		// Empty sent any required attributes
		this.holdCertificateNo.setMaxlength(50);
		this.purchaseDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.finalSettlementDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.purchaseAmount.setMandatory(true);
		this.purchaseAmount.setFormat(PennantApplicationUtil.getAmountFormate(defaultCCYDecPos));
		this.purchaseAmount.setScale(defaultCCYDecPos);
		this.purchaseAmount.setTextBoxWidth(195);
		
		
		this.unitPrice.setRoundingMode(BigDecimal.ROUND_DOWN);
		setAmountFormat();
		
		this.commodityCcy.setMaxlength(LengthConstants.LEN_CURRENCY);
		this.commodityCcy.setMandatoryStyle(true);
		this.commodityCcy.setModuleName("Currency");
		this.commodityCcy.setValueColumn("CcyCode");
		this.commodityCcy.setDescColumn("CcyDesc");
		this.commodityCcy.setValidateColumns(new String[] { "CcyCode" });
		
//		this.location.setInputAllowed(false);
//		this.location.setDisplayStyle(3);
		this.location.setMandatoryStyle(true);
		this.location.setModuleName("ComLocation");
		this.location.setValueColumn("FieldCodeValue");
		this.location.setDescColumn("ValueDesc");
		this.location.setValidateColumns(new String[] { "FieldCodeValue" });
		this.location.setTextBoxWidth(120);
		
		this.quantity.setMaxlength(9);
		this.brokerCode.setMaxlength(8);
		this.brokerCode.setMandatoryStyle(true);
		this.brokerCode.setModuleName("CommodityBrokerDetail");
		this.brokerCode.setValueColumn("BrokerCode");
		this.brokerCode.setDescColumn("LovDescBrokerShortName");
		this.brokerCode.setValidateColumns(new String[] {"BrokerCode"});

		setStatusDetails();
		
		logger.debug("Leaving");
	}

	public void setAmountFormat(){
		this.unitPrice.setFormat(PennantApplicationUtil.getAmountFormate(defaultCCYDecPos));
		this.unitPrice.setScale(defaultCCYDecPos);
		this.purchaseAmount.setFormat(PennantApplicationUtil.getAmountFormate(defaultCCYDecPos));
		this.purchaseAmount.setScale(defaultCCYDecPos);
	}

	public void onFulfill$brokerCode(Event event) {
		logger.debug("Entering");

		Object dataObject = brokerCode.getObject();
		if (dataObject instanceof String) {
			this.brokerCode.setValue(dataObject.toString());
			this.brokerCode.setDescription("");
			listCommodityCode.clear();
			fillComboBox(this.commodityCode, "", fetchBrokerUnitList(listCommodityCode,false),"");
			fillComboBox(this.units, "", fetchBrokerUnitList(listCommodityCode,true),"");
		} else {
			CommodityBrokerDetail details = (CommodityBrokerDetail) dataObject;
			if (details != null) {
				this.brokerCode.setValue(details.getBrokerCode());
				this.brokerCode.setDescription(details.getLovDescBrokerShortName());
				listCommodityCode = PennantAppUtil.getBrokerCommodityCodes(brokerCode.getValue());
				fillComboBox(this.commodityCode, "", fetchBrokerUnitList(listCommodityCode,false),"");
			} else {
				if(listCommodityCode != null){
					listCommodityCode.clear();
				}
				fillComboBox(this.commodityCode, "", fetchBrokerUnitList(listCommodityCode,false),"");
			}
		}

		logger.debug("Leaving");
	}
	
	
	private List<ValueLabel> fetchBrokerUnitList(List<BrokerCommodityDetail>  brokerCommList,boolean isUnits){
		logger.debug("Entering");
		List<ValueLabel> finalList = new ArrayList<ValueLabel>();
		if(brokerCommList != null && !brokerCommList.isEmpty()){
			ValueLabel valLab = null;
			for (BrokerCommodityDetail brokerCommodityDetail : brokerCommList) {
				valLab = new ValueLabel();
				if(isUnits){
					valLab.setValue(brokerCommodityDetail.getCommodityUnitCode());
					valLab.setLabel(brokerCommodityDetail.getCommodityUnitName());
				}else{
					valLab.setValue(brokerCommodityDetail.getCommodityCode());
					valLab.setLabel(brokerCommodityDetail.getLovDescCommodityDesc());
				}
				
				finalList.add(valLab);
			}
		}
		logger.debug("Leaving");
		return finalList;
		
	}
	
	
	public void onSelect$commodityCode(Event event) {
		logger.debug("Entering" + event.toString());
		
		String unitValue = "";
		if(this.commodityCode.getSelectedIndex() > 0){
			for (BrokerCommodityDetail brokerCommodityDetail : listCommodityCode) {
				if(StringUtils.equals(brokerCommodityDetail.getCommodityCode(),
						this.commodityCode.getSelectedItem().getValue().toString())){
					unitValue = brokerCommodityDetail.getCommodityUnitCode();
					break;
				}
			}
		}
		fillComboBox(this.units, unitValue, fetchBrokerUnitList(listCommodityCode,true),"");

		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$commodityCcy(Event event) {
		logger.debug(" Entering ");
		
		Object object = this.commodityCcy.getObject();
		if(object instanceof String){
			defaultCCYDecPos = 0;
		}else{
			if (object != null) {
				Currency currency = (Currency) object;
				defaultCCYDecPos = currency.getCcyEditField();
			} else {
				defaultCCYDecPos = 0;
			}
		}
		setAmountFormat();
		
		logger.debug(" Leaving ");
	}
	
	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCommodityInventory
	 *            CommodityInventory
	 */
	public void doWriteBeanToComponents(CommodityInventory aCommodityInventory) {
		logger.debug("Entering");

		this.brokerCode.setValue(aCommodityInventory.getBrokerCode());
		this.holdCertificateNo.setValue(aCommodityInventory.getHoldCertificateNo());
		this.commodityCcy.setValue(aCommodityInventory.getCommodityCcy(),CurrencyUtil.getCcyDesc(getCommodityInventory().getCommodityCcy()));
		
		if(aCommodityInventory.getBrokerCode()!=null){
			listCommodityCode = PennantAppUtil.getBrokerCommodityCodes(aCommodityInventory.getBrokerCode());
			fillComboBox(this.commodityCode, aCommodityInventory.getCommodityCode(), fetchBrokerUnitList(listCommodityCode,false),"");
			fillComboBox(this.units, aCommodityInventory.getUnits(), fetchBrokerUnitList(listCommodityCode,true),"");
		}else{
			fillComboBox(this.units, aCommodityInventory.getUnits(), fetchBrokerUnitList(listCommodityCode,true),"");
			fillComboBox(this.commodityCode, aCommodityInventory.getCommodityCode(), fetchBrokerUnitList(listCommodityCode,false),"");
		}
		if(aCommodityInventory.isNewRecord()) {
			this.brokerCode.setDescription("");
			this.purchaseDate.setValue(systemDate);
			this.finalSettlementDate.setValue(nextThursdayDate);
		} else {
			this.brokerCode.setDescription(aCommodityInventory.getBrokerShrtName());
			this.purchaseDate.setValue(aCommodityInventory.getPurchaseDate());
			this.finalSettlementDate.setValue(aCommodityInventory.getFinalSettlementDate());
		}
		this.purchaseAmount.setValue(PennantApplicationUtil.formateAmount(aCommodityInventory.getPurchaseAmount(),CurrencyUtil.getFormat(getCommodityInventory().getCommodityCcy())));
		this.unitPrice.setValue(aCommodityInventory.getUnitPrice());

		this.quantity.setValue(aCommodityInventory.getQuantity());
		this.location.setObject(aCommodityInventory.getLocation());
		this.location.setValue(aCommodityInventory.getLocationCode(),aCommodityInventory.getLocationDesc());
		
		this.bulkPurchase.setChecked(aCommodityInventory.isBulkPurchase());

		this.recordStatus.setValue(aCommodityInventory.getRecordStatus());
		//this.recordType.setValue(PennantJavaUtil.getLabel(aCommodityInventory.getRecordType()));

		//Show Accounting Tab Details Based upon Role Condition using Work flow
		if(!enqiryModule && "Accounting".equals(getTaskTabs(getTaskId(getRole())))){
			//Accounting Details Tab Addition
			showAccounting(aCommodityInventory,false);
		}
		if (enqiryModule) {
			setTabs();
			showAccounting(aCommodityInventory,true);
		}
		
	
		logger.debug("Leaving");
	}

	private void showAccounting(CommodityInventory aCommodityInventory, boolean enquiry) {
		try {

			this.tabPosting.setVisible(true);

			if (enquiry) {
				List<ReturnDataSet> returnDataSetList =getPostings(aCommodityInventory);
				doFillAccounting(returnDataSetList);
			} else {
				
				CommodityInventory prvCommodityInventory = getCommodityInventoryService().getCommodityDetails(commodityInventory.getHoldCertificateNo(), commodityInventory.getBrokerCode());
				
				if (aCommodityInventory.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
					
					List<ReturnDataSet> returnDataSetList = getPostingsPreparationUtil().prepareAccountingDataSet(aCommodityInventory, AccountEventConstants.ACCEVENT_CMTINV_DEL, "N");
					
					doFillAccounting(returnDataSetList);
				
				}else{
					
					if (prvCommodityInventory==null) {
						List<ReturnDataSet> returnDataSetList = getPostingsPreparationUtil().prepareAccountingDataSet(aCommodityInventory, AccountEventConstants.ACCEVENT_CMTINV_NEW, "N");
						doFillAccounting(returnDataSetList);
					} else {
						List<ReturnDataSet> returnDataSetList = getPostingsPreparationUtil().prepareAccountingDataSet(aCommodityInventory, AccountEventConstants.ACCEVENT_CMTINV_MAT, "N");
						doFillAccounting(returnDataSetList);
					}
					
				}
				
				
	
			}

		} catch (Exception e) {
			logger.debug(e);
		}

	}


	private List<ReturnDataSet> getPostings(CommodityInventory aCommodityInventory) {
		
		List<ReturnDataSet> postingAccount = new ArrayList<ReturnDataSet>();
		JdbcSearchObject<ReturnDataSet> searchObject=new JdbcSearchObject<ReturnDataSet>(ReturnDataSet.class);
		searchObject.addTabelName("Postings_view");
		searchObject.addFilterEqual("finreference", aCommodityInventory.getBrokerCode()+aCommodityInventory.getHoldCertificateNo());
		List<ReturnDataSet>  postings = pagedListService.getBySearchObject(searchObject);
		if(postings!=null && !postings.isEmpty()){
			return postings;
		}
		
		logger.debug("Leaving");
		return postingAccount ;
	}


	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCommodityInventory
	 */
	public void doWriteComponentsToBean(CommodityInventory aCommodityInventory) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Broker Code
		try {
			aCommodityInventory.setBrokerShrtName(this.brokerCode.getDescription());
			aCommodityInventory.setBrokerCode(this.brokerCode.getValidatedValue());	
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Holding Number
		try {
			aCommodityInventory.setHoldCertificateNo(this.holdCertificateNo.getValue().trim());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Commodity Code
		try {
			if("#".equals(getComboboxValue(this.commodityCode))){
				throw new WrongValueException(this.commodityCode, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_CommodityInventoryDialog_CommodityCode.value") }));
			}
			String strCommodityCode = null;
			if (this.commodityCode.getSelectedItem() != null) {
				strCommodityCode = this.commodityCode.getSelectedItem().getValue().toString();
			}
			if (strCommodityCode != null && !PennantConstants.List_Select.equals(strCommodityCode)) {
				aCommodityInventory.setCommodityCode(strCommodityCode);
			} else {
				aCommodityInventory.setCommodityCode(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		// Currency
		try {
			aCommodityInventory.setCommodityCcy(this.commodityCcy.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		// Purchase Date
		try {
			aCommodityInventory.setPurchaseDate(this.purchaseDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Final Settlement Date
		try {
			aCommodityInventory.setFinalSettlementDate(this.finalSettlementDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Price
		try {
			if (this.purchaseAmount.getValidateValue() != null) {
				aCommodityInventory.setPurchaseAmount(PennantApplicationUtil.unFormateAmount(this.purchaseAmount.getActualValue(),
						defaultCCYDecPos));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if(this.units.getSelectedIndex() <= 0) {
			aCommodityInventory.setUnits("");
		}else{
			aCommodityInventory.setUnits(this.units.getSelectedItem().getValue().toString());
		}

		// Unit Price
		try {
			BigDecimal maxUnitValue = new BigDecimal(999999999999.999999999);
			if (this.unitPrice.getValue() != null && this.unitPrice.getValue().compareTo(maxUnitValue) > 0) {
				throw new WrongValueException(this.unitPrice, Labels.getLabel("label_UnitPrice_Validate.value",
						new String[] { Labels.getLabel("label_CommodityInventoryDialog_UnitPrice.value"),
						Labels.getLabel("label_CommodityInventoryDialog_PurchaseAmount.value"),
						Labels.getLabel("label_CommodityInventoryDialog_Quantity.value") }));
			}
			aCommodityInventory.setUnitPrice(this.unitPrice.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Quantity
		try {
			aCommodityInventory.setQuantity(this.quantity.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Location
		try {
			this.location.getValidatedValue();

			String valu = null;
			Object object = this.location.getObject();
			if (object != null) {
				if (object instanceof LovFieldDetail) {
					LovFieldDetail detail = (LovFieldDetail) object;
					valu = String.valueOf(detail.getFieldCodeId());
				} else if (object instanceof String) {
					valu=(String) object;
				}
			}

			aCommodityInventory.setLocation(valu);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Bulk Purchage
		try {
			aCommodityInventory.setBulkPurchase(this.bulkPurchase.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		//setValidationOn(true);
		doClearMessage();
		// Broker Code
		if (!this.brokerCode.isReadonly()) {
			this.brokerCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityInventoryDialog_BrokerCode.value"), null, true,true));
		}
		
		// Currency
		if (!this.commodityCcy.isReadonly()) {
			this.commodityCcy.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityInventoryDialog_CommodityCcy.value"), null, true,true));
		}


		// Holding Number
		if (!this.holdCertificateNo.isReadonly()) {
			this.holdCertificateNo.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CommodityInventoryDialog_HoldCertificateNo.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_UNDERSCORE_SPACE, true));
		}
	
		if (!this.location.isReadonly()) {
			this.location.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityInventoryDialog_Location.value"), null, true,true));
		}


		// Purchase Date
		if (!this.purchaseDate.isReadonly()) {
			this.purchaseDate.setConstraint(new PTDateValidator(
					Labels.getLabel("label_CommodityInventoryDialog_PurchaseDate.value"), true));
		}

		// Final Settlement Date
		if (!this.finalSettlementDate.isReadonly()) {
			
			if (this.finalSettlementDate.getValue() == null) {
				this.finalSettlementDate.setConstraint(new PTDateValidator(
						Labels.getLabel("label_CommodityInventoryDialog_FinalSettlementDate.value"), true));
			}else if(finalSettlementDate.getValue().compareTo(this.purchaseDate.getValue()) < 0) {
				throw new WrongValueException(this.finalSettlementDate, Labels.getLabel("DATE_ALLOWED_ON_AFTER", new String[] { 
						Labels.getLabel("label_CommodityInventoryDialog_FinalSettlementDate.value"), Labels.getLabel("label_CommodityInventoryDialog_PurchaseDate.value") }));
			}
			
		}

		// Purchase amount
		if (!this.purchaseAmount.isReadonly()) {
			this.purchaseAmount.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_CommodityInventoryDialog_PurchaseAmount.value"),
					defaultCCYDecPos, true, false));
		}
		
		/*if (this.unitPrice.isReadonly() && this.unitPrice.getValue()!=null) {
         this.unitPrice.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CommodityInventoryDialog_UnitPrice.value"),
        		 PennantRegularExpressions.REGEX_NUMERIC_MAXLENGTH,9, false, false));
		}*/

		// Quantity
		if (!this.quantity.isReadonly()) {
			this.quantity.setConstraint(new PTNumberValidator(Labels.getLabel("label_CommodityInventoryDialog_Quantity.value"), true, false));
		}


		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");

		this.brokerCode.setConstraint("");
		this.commodityCcy.setConstraint("");
		this.holdCertificateNo.setConstraint("");
		this.commodityCode.setConstraint("");
		this.purchaseDate.setConstraint("");
		this.finalSettlementDate.setConstraint("");
		this.purchaseAmount.setConstraint("");
		this.unitPrice.setConstraint("");
		this.units.setConstraint("");
		this.quantity.setConstraint("");
		this.location.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.brokerCode.setErrorMessage("");
		this.commodityCcy.setErrorMessage("");
		this.holdCertificateNo.setErrorMessage("");
		this.commodityCode.setErrorMessage("");
		this.purchaseDate.setErrorMessage("");
		this.finalSettlementDate.setErrorMessage("");
		this.purchaseAmount.setErrorMessage("");
		this.unitPrice.setErrorMessage("");
		this.units.setErrorMessage("");
		this.quantity.setErrorMessage("");
		this.location.setErrorMessage("");

		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getCommodityInventoryListCtrl().search();
	}

	/**
	 * Deletes a CommodityInventory object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final CommodityInventory aCommodityInventory = new CommodityInventory();
		BeanUtils.copyProperties(getCommodityInventory(), aCommodityInventory);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")+ "\n\n --> " + 
				Labels.getLabel("label_CommodityInventoryDialog_BrokerCode.value")+" : "+aCommodityInventory.getBrokerCode()+","+
				Labels.getLabel("label_CommodityInventoryDialog_HoldCertificateNo.value")+" : "+aCommodityInventory.getHoldCertificateNo();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true);

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.isBlank(aCommodityInventory.getRecordType())) {
				aCommodityInventory.setVersion(aCommodityInventory.getVersion() + 1);
				aCommodityInventory.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aCommodityInventory.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aCommodityInventory.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(),	aCommodityInventory.getNextTaskId(),
							aCommodityInventory);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aCommodityInventory, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				logger.error("Exception: ", e);
				MessageUtil.showErrorMessage(e.getMessage());
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.brokerCode.setValue("");
		this.brokerCode.setDescription("");
		this.holdCertificateNo.setValue("");
		this.commodityCode.setSelectedIndex(0);
		this.purchaseDate.setText("");
		this.finalSettlementDate.setText("");
		this.purchaseAmount.setValue("");
		this.unitPrice.setValue("");
		this.units.setSelectedIndex(0);
		this.quantity.setText("");
		this.location.setValue("");
		this.commodityCcy.setValue("");
		this.bulkPurchase.setChecked(false);

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final CommodityInventory aCommodityInventory = new CommodityInventory();
		BeanUtils.copyProperties(getCommodityInventory(), aCommodityInventory);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aCommodityInventory.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(),
					aCommodityInventory.getNextTaskId(), aCommodityInventory);
		}

		// force validation, if on, than execute by component.getValue()
		if (!PennantConstants.RCD_STATUS_CANCELLED.equals(userAction.getSelectedItem().getValue().toString()) &&
				!PennantConstants.RECORD_TYPE_DEL.equals(aCommodityInventory.getRecordType()) && isValidation()) {

			doSetValidation();

			// fill the CommodityInventory object with the components data
			doWriteComponentsToBean(aCommodityInventory);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aCommodityInventory.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCommodityInventory.getRecordType())) {
				aCommodityInventory.setVersion(aCommodityInventory.getVersion() + 1);
				if (isNew) {
					aCommodityInventory.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCommodityInventory.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCommodityInventory.setNewRecord(true);
				}
			}
		} else {
			aCommodityInventory.setVersion(aCommodityInventory.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aCommodityInventory, tranType)) {
				// doWriteBeanToComponents(aCommodityInventory);
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showErrorMessage(this.window_CommodityInventoryDialog, e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */

	private boolean doProcess(CommodityInventory aCommodityInventory, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		aCommodityInventory.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aCommodityInventory.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCommodityInventory.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (auditingReq) {
					try {
						if (!notesEntered) {
							MessageUtil.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						logger.error("Exception: ", e);
					}
				}
			}

			aCommodityInventory.setTaskId(getTaskId());
			aCommodityInventory.setNextTaskId(getNextTaskId());
			aCommodityInventory.setRoleCode(getRole());
			aCommodityInventory.setNextRoleCode(getNextRoleCode());

			if (StringUtils.isBlank(getOperationRefs())) {
				processCompleted = doSaveProcess(getAuditHeader(aCommodityInventory, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader = getAuditHeader(aCommodityInventory, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(getAuditHeader(aCommodityInventory, tranType), null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");

		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader
	 *            auditHeader
	 * @param method
	 *            (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		CommodityInventory aCommodityInventory = (CommodityInventory) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
						auditHeader = getCommodityInventoryService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getCommodityInventoryService().saveOrUpdate(auditHeader);
					}

				} else {
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getCommodityInventoryService().doApprove(auditHeader);

						if (PennantConstants.RECORD_TYPE_DEL.equals(aCommodityInventory.getRecordType())) {
							deleteNotes = true;
						}

					} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getCommodityInventoryService().doReject(auditHeader);
						if (PennantConstants.RECORD_TYPE_NEW.equals(aCommodityInventory.getRecordType())) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, 
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CommodityInventoryDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(
						this.window_CommodityInventoryDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes("CommodityInventory",String.valueOf(aCommodityInventory.getCommodityInvId()),
								aCommodityInventory.getVersion()), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * This method for building the listbox with dynamic headers.<br>
	 * 
	 */	
	public Listbox setListToTab(String divId, String tabId,Tabpanel tabPanel,CommodityInventory commInventory){
		logger.debug("Entering");
		Div div = new Div();
		div.setId(divId);
		div.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px"))) - 100 - 40-20-81 + "px");
		Listbox listbox = new Listbox();
		listbox.setId("listbox"+divId);
		listbox.setVflex(true);
		listbox.setSpan(true);
		listbox.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px"))) - 100 - 40-20-85 + "px");


		Listhead listHead = new Listhead();
		listHead.setId("listHead_"+commInventory.getId());
		listHead.setStyle("background:#447294;color:white;");
		listHead.setSizable(true);

		final Listhead listhead = new Listhead();
		listhead.setParent(listbox);
		String headerList = null;
		if(StringUtils.equals(tabId, "tab_Fee Payable")) {
			headerList = Labels.getLabel("listHeader_CommInventoryFeePayableEnquiry_label");
		} else {
			headerList = Labels.getLabel("listHeader_CommInventoryEnquiry_label");
		}
		String[] listHeaders = headerList.split(",");
		for (int i = 0; i < listHeaders.length; i++) {
			final Listheader listheader = new Listheader();
			listheader.setLabel(getLabel(listHeaders[i]));
			listheader.setParent(listhead);
			if(getLabel(listHeaders[i]).contains("Price")) {
				listheader.setAlign("right");
			}
		}

		listbox.setParent(div);
		div.setParent(tabPanel);
		logger.debug("Leaving");
		return listbox;

	}

	private String getLabel(String value) {
		String label = Labels.getLabel(value + "_label");
		if (StringUtils.isBlank(label)) {
			return value;
		}
		return label;
	}


	// WorkFlow Components

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(CommodityInventory aCommodityInventory, String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCommodityInventory.getBefImage(), aCommodityInventory);

		return new AuditHeader(String.valueOf(aCommodityInventory.getCommodityInvId()), null, null, null, auditDetail,
				aCommodityInventory.getUserDetails(), getOverideMap());
	}

	/**
	 *  Calculate Next Thursday Date from Current Purchase date
	 *  
	 * @param bussinessDate
	 * @return
	 */
	private Date getNextThrusdayDate(Date bussinessDate) {
		logger.debug("Entering");

		if (bussinessDate != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(bussinessDate);
			int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

			if (dayOfWeek != Calendar.THURSDAY) {
				int days = (Calendar.SATURDAY - dayOfWeek + 5) % 7;
				cal.add(Calendar.DAY_OF_YEAR, days);
			}
			return cal.getTime();
		}

		logger.debug("Entering");
		return bussinessDate;

	}
	
	//postings
	
	
	/**
	 * Method to fill list box in Accounting Tab <br>
	 *  
	 * @param accountingSetEntries (List)
	 * 
	 */
	public void doFillAccounting(List<?> accountingSetEntries) {
		logger.debug("Entering");
		
//		setDisbCrSum(BigDecimal.ZERO);
//		setDisbDrSum(BigDecimal.ZERO);
		
		int formatter = defaultCCYDecPos;

		this.listBoxFinAccountings.getItems().clear();
		this.listBoxFinAccountings.setSizedByContent(true);
		if (accountingSetEntries != null && !accountingSetEntries.isEmpty()) {
			for (int i = 0; i < accountingSetEntries.size(); i++) {

				Listitem item = new Listitem();
				Listcell lc;
				if (accountingSetEntries.get(i) instanceof TransactionEntry) {
					TransactionEntry entry = (TransactionEntry) accountingSetEntries.get(i);

					//Adding List Group to ListBox
					/*if(i == 0){
						Listgroup listgroup = new Listgroup(entry.getLovDescEventCodeName()+"-"+entry.getLovDescEventCodeDesc());
						this.listBoxFinAccountings.appendChild(listgroup);
					}*/

					lc = new Listcell(PennantAppUtil.getlabelDesc(
							entry.getDebitcredit(), PennantStaticListUtil.getTranType()));
					lc.setParent(item);
					lc = new Listcell(entry.getTransDesc());
					lc.setParent(item);
					lc = new Listcell(entry.getTranscationCode());
					lc.setParent(item);
					lc = new Listcell(entry.getRvsTransactionCode());
					lc.setParent(item);
					lc = new Listcell(entry.getAccount());
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
				} else if (accountingSetEntries.get(i) instanceof ReturnDataSet) {
					ReturnDataSet entry = (ReturnDataSet) accountingSetEntries.get(i);
					
					//Highlighting Failed Posting Details 
					String sClassStyle = "";
					if(StringUtils.isNotBlank(entry.getErrorId()) && !"0000".equals(StringUtils.trimToEmpty(entry.getErrorId()))){
						sClassStyle = "color:#FF0000;";
					}

					//Adding List Group to ListBox
					/*if(i == 0){
						Listgroup listgroup = new Listgroup(entry.getFinEvent() +"-"+ entry.getLovDescEventCodeName());
						this.listBoxFinAccountings.appendChild(listgroup);
					}*/

					Hbox hbox = new Hbox();
					Label label = new Label(PennantAppUtil.getlabelDesc(
							entry.getDrOrCr(), PennantStaticListUtil.getTranType()));
					label.setStyle(sClassStyle);
					hbox.appendChild(label);
					if (StringUtils.isNotBlank(entry.getPostStatus())) {
						Label la = new Label("*");
						la.setStyle("color:red;");
						hbox.appendChild(la);
					}
					lc = new Listcell();
					lc.setStyle(sClassStyle);
					lc.appendChild(hbox);
					lc.setParent(item);
					lc = new Listcell(entry.getTranDesc());
					lc.setStyle(sClassStyle);
					lc.setParent(item);
					if(entry.isShadowPosting()){
						lc = new Listcell("Shadow");
						lc.setStyle(sClassStyle);
						lc.setParent(item);
						lc = new Listcell("Shadow");
						lc.setStyle(sClassStyle);
						lc.setParent(item);
					}else{
						lc = new Listcell(entry.getTranCode());
						lc.setStyle(sClassStyle);
						lc.setParent(item);
						lc = new Listcell(entry.getRevTranCode());
						lc.setStyle(sClassStyle);
						lc.setParent(item);
					}
					lc = new Listcell(entry.getAccountType());
					lc.setStyle(sClassStyle);
					lc.setParent(item);
					lc = new Listcell(PennantApplicationUtil.formatAccountNumber(entry.getAccount()));
					lc.setStyle("font-weight:bold;");
						lc.setStyle(sClassStyle);
					lc.setParent(item);	
					
					lc = new Listcell(entry.getAcCcy());
					lc.setParent(item);

					BigDecimal amt = entry.getPostAmount()!=null?entry.getPostAmount(): BigDecimal.ZERO;
					lc = new Listcell(PennantApplicationUtil.amountFormate(amt,formatter));
					
					lc.setStyle("font-weight:bold;text-align:right;");
					lc.setStyle(sClassStyle+"font-weight:bold;text-align:right;");
					lc.setParent(item);
					lc = new Listcell("0000".equals(StringUtils.trimToEmpty(entry.getErrorId())) ? "" : StringUtils.trimToEmpty(entry.getErrorId()));
					lc.setStyle("font-weight:bold;color:red;");
					lc.setTooltiptext(entry.getErrorMsg());
					lc.setParent(item);
				}
				this.listBoxFinAccountings.appendChild(item);
			}

//			this.getLabel_AccountingDisbCrVal().setValue(PennantApplicationUtil.amountFormate(getDisbCrSum(), formatter));
//			this.getLabel_AccountingDisbDrVal().setValue(PennantAppUtil.amountFormate(getDisbDrSum(), formatter));
		}
		logger.debug("Leaving");
	}
	

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public CommodityInventory getCommodityInventory() {
		return this.commodityInventory;
	}

	public void setCommodityInventory(CommodityInventory commodityInventory) {
		this.commodityInventory = commodityInventory;
	}

	public void setCommodityInventoryService(CommodityInventoryService commodityInventoryService) {
		this.commodityInventoryService = commodityInventoryService;
	}

	public CommodityInventoryService getCommodityInventoryService() {
		return this.commodityInventoryService;
	}

	public void setCommodityInventoryListCtrl(CommodityInventoryListCtrl commodityInventoryListCtrl) {
		this.commodityInventoryListCtrl = commodityInventoryListCtrl;
	}

	public CommodityInventoryListCtrl getCommodityInventoryListCtrl() {
		return this.commodityInventoryListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}
}