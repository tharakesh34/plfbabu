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
 * FileName    		:  CustomerDedupListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-07-2011    														*
 *                                                                  						*
 * Modified Date    :  26-07-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-07-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.customerDedup;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.service.customermasters.CustomerDedupService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.customermasters.customer.CustomerQDEDialogCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/Customer/CustomerDepudList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CustomerDedupListCtrl extends GFCBaseListCtrl<CustomerDedup>
		implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(CustomerDedupListCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CustomerDedupList; // autowired
	protected Panel panel_CustomerDedupList; // autowired
	protected Borderlayout borderLayout_CustomerDedupList; // autowired
	protected Paging pagingCustomerDedupList; // autowired
	protected Listbox listBoxCustomerDedup; // autowired

	// List headers
	protected Listheader listheader_CustCIF; // autowired
	protected Listheader listheader_CustCoreBank; // autowired
	protected Listheader listheader_CustFName;// autowired
	protected Listheader listheader_CustMName;// autowired
	protected Listheader listheader_CustLName;// autowired
	protected Listheader listheader_CustShrtName;// autowired
	protected Listheader listheader_CustDOB;// autowired
	protected Listheader listheader_CustPassportNo; // autowired

	protected Panel customerSeekPanel; // autowired
	protected Panel customerListPanel; // autowired

	// row count for listbox
	private int countRows;

	private CustomerDedup customerDedup;
	CustomerDedup custDedup = new CustomerDedup();
	private List<CustomerDedup> dedupArrList;
	private transient CustomerDedupService customerDedupService;
	private transient CustomerDetails aCustomerDetails;	
	private transient Customer		  aCustomer;
	private transient WorkFlowDetails workFlowDetails = null;
	
	private transient CustomerDialogCtrl customerDialogCtrl;  
	private transient CustomerQDEDialogCtrl customerQDEDialogCtrl;
	
	private transient final String btnCtroller_ClassPrefix = "button_CustomerDialog_";
    private transient ButtonStatusCtrl btnCtrl;
    protected Button btnNew; // autowire
    protected Button btnEdit; // autowire
    protected Button btnDelete; // autowire
    protected Button btnSave; // autowire
    protected Button btnCancel; // autowire
    protected Button btnClose; // autowire
    protected Button btnHelp; // autowire
    protected Button btnNotes; // autowire	

	/**
	 * default constructor.<br>
	 */
	public CustomerDedupListCtrl() {
		super();
	}

	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	public void onCreate$window_CustomerDedupList(Event event) throws Exception {
		
		
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);		
		
		if (args.containsKey("customerDedup")) {
			dedupArrList =  (List<CustomerDedup>) args.get("customerDedup");		
			
		/*	setCustomerDedup(this.customer.getCustomerDedup());		
			dedupList.add(getCustomerDedup());*/
		} else {
			//setCustomerDedup(null);
			this.dedupArrList = null;
		}	

		
		if (args.containsKey("customerDialogCtrl")) {
			// Setting the leadListCtrl Object
			this.customerDialogCtrl = ((CustomerDialogCtrl) args.get("customerDialogCtrl"));
 		} else {
			this.customerDialogCtrl = null;
		}	
		
		if (args.containsKey("customerQDEDialogCtrl")) {
			// Setting the leadListCtrl Object
			this.customerQDEDialogCtrl = ((CustomerQDEDialogCtrl) args.get("customerQDEDialogCtrl"));
 		} else {
			this.customerQDEDialogCtrl = null;
		}
		
		if(args.containsKey("customerDetails")){
			this.aCustomerDetails = ((CustomerDetails) args.get("customerDetails"));
		}else {
			this.aCustomerDetails = null;
		}
		
		if(args.containsKey("customer")){
			this.aCustomer = ((Customer) args.get("customer"));
		}else {
			this.aCustomer = null;
		}
		
	

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the listbox. Get the
		 * currentDesktopHeight from a hidden Intbox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */

		int panelHeight = 20;
		// put the logic for working with panel in the ApplicationWorkspace
		
		final boolean withPanel = true;
		if (withPanel == false) {
			this.panel_CustomerDedupList.setVisible(false);
		} else {
			this.panel_CustomerDedupList.setVisible(true);
			panelHeight = 0;
		}

		int height = ((Intbox) Path
				.getComponent("/outerIndexWindow/currentDesktopHeight"))
				.getValue().intValue();
		height = height + panelHeight;
		final int maxListBoxHeight = height - 103;
		setCountRows(Math.round(maxListBoxHeight / 24) - 1);
		this.borderLayout_CustomerDedupList.setHeight(String
				.valueOf(maxListBoxHeight) + "px");
	
		// Set the ListModel for the articles.
		getPagedListWrapper().initList(this.dedupArrList,
				this.listBoxCustomerDedup, this.pagingCustomerDedupList);
		
		/*getPagedListWrapper().initList(this.dedupList,
				this.listBoxCustomerDedup, this.pagingCustomerDedupList);*/
		
		// set the itemRenderer
		this.listBoxCustomerDedup.setItemRenderer(new DedupListItemRenderer());

		//setDialog(window_CustomerDedupList);
		this.window_CustomerDedupList.doModal();
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		getUserWorkspace().alocateAuthorities("CustomerDedupList");
		//this.btnCancel.setVisible(true);

	}
	
	
	
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
    // +++++++++++++++++OnClick Button Events++++++++++++++++//
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	/* public void onClick$btnCancel(Event event) throws ParseException {
			logger.debug("Calling onClick Event for  Cancelling the Customer Creation Process");	
			
			if(customerDialogCtrl!= null){
				 this.customerDialogCtrl.dedupCancel = true;		
				 //this.customerDialogCtrl.dedupExists = false;
				 this.customerDialogCtrl.saveForNonDedup(this.aCustomerDetails,this.customerDialogCtrl.dedupExists,this.customerDialogCtrl.dedupCancel);
			}else if(customerQDEDialogCtrl != null){
				this.customerQDEDialogCtrl.dedupCancel = true;		
				//this.customerQDEDialogCtrl.dedupExists = false;
				this.customerQDEDialogCtrl.saveCustomerQDE(this.aCustomer,this.aCustomerDetails,this.customerQDEDialogCtrl.tranType,
						 	this.customerQDEDialogCtrl.dedupExists,this.customerQDEDialogCtrl.dedupCancel);
			}
			 
			 
			 this.window_CustomerDedupList.onClose();
			logger.debug("Completed onClick Event for   Cancelling the Customer Creation Process");
  }
	 
	 public void onClick$btnSave(Event event) throws ParseException {
			logger.debug("Calling onClick Event for  Proceeding the Customer Creation Process");			
			if(customerDialogCtrl!= null){
				 this.customerDialogCtrl.dedupCancel = false;	
				 this.customerDialogCtrl.dedupExists = false;
				 this.window_CustomerDedupList.onClose();
				 this.customerDialogCtrl.saveForNonDedup(this.aCustomerDetails,this.customerDialogCtrl.dedupExists,this.customerDialogCtrl.dedupCancel);			
			}else if(customerQDEDialogCtrl != null){
				 this.customerQDEDialogCtrl.dedupCancel = false;	
				 this.customerQDEDialogCtrl.dedupExists = false;
				 this.window_CustomerDedupList.onClose();
				 this.customerQDEDialogCtrl.saveCustomerQDE(this.aCustomer,this.aCustomerDetails,this.customerQDEDialogCtrl.tranType,
						 	this.customerQDEDialogCtrl.dedupExists,this.customerQDEDialogCtrl.dedupCancel);
			}
			logger.debug("Completed onClick Event for   Proceeding the Customer Creation Process");
}
	 
	 */
	
	/**
	 * Item renderer for listitems in the listbox.
	 * 
	 */	
	final class DedupListItemRenderer implements ListitemRenderer<CustomerDedup> {
		//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
		public void render(Listitem item, CustomerDedup dedupList,int count) throws Exception {			
			//final CustomerDedup dedupList = (CustomerDedup) data;
			Listcell lc;
			//lc = new Listcell(StringUtils.leftPad(dedupList.getCustCIF().trim(), 12, '0'));
			lc = new Listcell(dedupList.getCustCIF());
			lc.setParent(item);
			/*lc = new Listcell(dedupList.getCustCoreBank());
			lc.setParent(item);*/
			lc = new Listcell(dedupList.getCustFName());
			lc.setParent(item);
			/*lc = new Listcell(dedupList.getCustMName());
			lc.setParent(item);*/
			lc = new Listcell(dedupList.getCustLName());
			lc.setParent(item);
			lc = new Listcell(dedupList.getCustShrtName());
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.formateDate(dedupList.getCustDOB(), PennantConstants.dateFormat));
			lc.setParent(item);			
			lc = new Listcell(dedupList.getCustNationality());
			lc.setParent(item);
			lc = new Listcell(dedupList.getCustPassportNo());
			lc.setParent(item);
			lc = new Listcell(dedupList.getCustVisaNum());
			lc.setParent(item);
			item.setAttribute("data", dedupList);
			
		}
	}

	public int getCountRows() {
		return this.countRows;
	}

	public void setCountRows(int countRows) {
		this.countRows = countRows;
	}

	public void setCustomerDedupService(
			CustomerDedupService customerDedupService) {
		this.customerDedupService = customerDedupService;
	}

	public CustomerDedupService getCustomerDedupService() {
		return customerDedupService;
	}

	public CustomerDedup getCustomerDedup() {
		return customerDedup;
	}

	public void setCustomerDedup(CustomerDedup customerDedup) {
		this.customerDedup = customerDedup;
	}
}