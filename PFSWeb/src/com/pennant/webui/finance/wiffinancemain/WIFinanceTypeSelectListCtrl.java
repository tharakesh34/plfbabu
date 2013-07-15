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
 *											    											*
 * FileName    		:  WIFinanceTypeSelectListCtrl.java                                     * 	  
 *                                                                    			    		*
 * Author      		:  PENNANT TECHONOLOGIES              				    				*
 *                                                                  			    		*
 * Creation Date    :  10-10-2011    							    						*
 *                                                                  			    		*
 * Modified Date    :  10-10-2011    							    						*
 *                                                                  			    		*
 * Description 		:                                             			    			*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-10-2011       Pennant	                 0.1                                        	* 
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

package com.pennant.webui.finance.wiffinancemain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.search.Filter;
import com.pennant.webui.finance.wiffinancemain.model.WIFinanceTypeSelectItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

public class WIFinanceTypeSelectListCtrl extends GFCBaseListCtrl<FinanceType> implements Serializable {

	private static final long serialVersionUID = 3257569537441008225L;
	private final static Logger logger = Logger.getLogger(WIFinanceTypeSelectListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FinanceTypeSelect; 				// autoWired
	protected Borderlayout borderLayout_FinanceTypeList; 	// autoWired
	protected Paging pagingFinanceTypeList; 				// autoWired
	protected Listbox listBoxFinanceType; 					// autoWired

	// List headers
	protected Listheader listheader_FinType; 				// autoWired
	protected Listheader listheader_FinTypeDesc; 			// autoWired
	protected Listheader listheader_FinCcy; 				// autoWired
	protected Listheader listheader_FinBasicType; 			// autoWired
	protected Listheader listheader_FinAcType; 				// autoWired
	protected Listheader listheader_RecordStatus; 			// autoWired
	protected Listheader listheader_RecordType;				// autoWired

	private transient FinanceTypeService financeTypeService;
	private transient PagedListService pagedListService;
	private transient WIFFinanceMainDialogCtrl wifFinanceMainDialogCtrl;
	private transient WIFFinanceMainListCtrl wIFFinanceMainListCtrl;
	private transient FinanceDetail financeDetail;
	protected JdbcSearchObject<FinanceType> searchObj;
	private String loanType = "";
	
	/**
	 * default constructor.<br>
	 */
	public WIFinanceTypeSelectListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceType object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceTypeSelect(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/**
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */

		// set the paging parameters
		this.pagingFinanceTypeList.setPageSize(15);
		this.pagingFinanceTypeList.setDetailed(true);

		this.listheader_FinType.setSortAscending(new FieldComparator("finType",
				true));
		this.listheader_FinType.setSortDescending(new FieldComparator(
				"finType", false));
		this.listheader_FinTypeDesc.setSortAscending(new FieldComparator(
				"finTypeDesc", true));
		this.listheader_FinTypeDesc.setSortDescending(new FieldComparator(
				"finTypeDesc", false));
		this.listheader_FinCcy.setSortAscending(new FieldComparator("finCcy",
				true));
		this.listheader_FinCcy.setSortDescending(new FieldComparator("finCcy",
				false));
		this.listheader_FinBasicType.setSortAscending(new FieldComparator(
				"finDaysCalType", true));
		this.listheader_FinBasicType.setSortDescending(new FieldComparator(
				"finDaysCalType", false));
		this.listheader_FinAcType.setSortAscending(new FieldComparator(
				"finAcType", true));
		this.listheader_FinAcType.setSortDescending(new FieldComparator(
				"finAcType", false));

		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("WIFFinanceMainDialogCtrl")) {
			this.wifFinanceMainDialogCtrl = (WIFFinanceMainDialogCtrl) args.get("WIFFinanceMainDialogCtrl");			
		} else {
			this.wifFinanceMainDialogCtrl = null;
		}
		
		if (args.containsKey("loanType")) {
			this.loanType = (String) args.get("loanType");
		}

		if(args.containsKey("WIFFinanceMainListCtrl")) {
			this.wIFFinanceMainListCtrl = (WIFFinanceMainListCtrl) args.get("WIFFinanceMainListCtrl");			
		} else {
			this.wIFFinanceMainListCtrl = null;
		}
		if(args.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) args.get("financeDetail");			
		} else {
			this.financeDetail = null;
		}

		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<FinanceType>(FinanceType.class,getListRows());
		this.searchObj.addSort("FinType", false);
		
		if(!StringUtils.trimToEmpty(this.loanType).equals("")){
			this.searchObj.addFilter(new Filter("lovDescProductCodeName", this.loanType, Filter.OP_EQUAL));
		}

		this.searchObj.addTabelName("RMTFinanceTypes_AView");

		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxFinanceType,this.pagingFinanceTypeList);
		
		// set the itemRenderer
		this.listBoxFinanceType.setItemRenderer(new WIFinanceTypeSelectItemRenderer());
		this.window_FinanceTypeSelect.doModal();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * This method is forwarded from the ListBoxes item renderer. <br>
	 * see: com.pennant.webui.rmtmasters.financetype.model.
	 * FinanceTypeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onFinanceTypeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// get the selected FinanceType object
		fillFinanceDetails();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * closes the dialog window
	 * @throws InterruptedException 
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering");
		this.window_FinanceTypeSelect.onClose();
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnSelect(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		fillFinanceDetails();
		logger.debug("Leaving" + event.toString());
	}    

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}

	public FinanceTypeService getFinanceTypeService() {
		return this.financeTypeService;
	}

	public JdbcSearchObject<FinanceType> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<FinanceType> searchObj) {
		this.searchObj = searchObj;
	}
	
	/**
	 * @return the financeDetail
	 */
	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	/**
	 * @param financeDetail the financeDetail to set
	 */
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	/**
	 * Method to invoke filldata method in WIFFinanceMain dialog control.
	 * 
	 * **/
	private void fillFinanceDetails() throws InterruptedException {
		logger.debug("Entering");
		final Listitem item = this.listBoxFinanceType.getSelectedItem();
		if(item==null){
			throw new WrongValueException(this.listBoxFinanceType,Labels.getLabel("STATIC_INVALID",new String[] {""}));	    
		} else if(this.wifFinanceMainDialogCtrl!=null){
			this.financeDetail = new FinanceDetail();
			this.financeDetail.getFinScheduleData().setFinanceMain(new FinanceMain(),(FinanceType)item.getAttribute("data"));
			this.financeDetail.getFinScheduleData().setFinanceType((FinanceType)item.getAttribute("data"));
			this.financeDetail.setNewRecord(true);
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("wIFFinanceMainListCtrl", this.wIFFinanceMainListCtrl);
			map.put("financeDetail",this.financeDetail);
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/Finance/WIFFinanceMain/WIFFinanceMainDialog.zul",null,map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}
		doClose();	
		logger.debug("Leaving");
	}
}
