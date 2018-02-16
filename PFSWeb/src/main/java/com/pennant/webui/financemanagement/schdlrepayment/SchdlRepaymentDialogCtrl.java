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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  RuleDialogCtrl.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  03-06-2011    
 *                                                                  
 * Modified Date    :  03-06-2011    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-06-2011       Pennant	                 0.1                                         * 
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
package com.pennant.webui.financemanagement.schdlrepayment;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * This is the controller class for the 
 * WEB-INF/pages/FinanceManagement/SchdlRepayment/SchdlRepaymentDialog.zul
 */
public class SchdlRepaymentDialogCtrl extends GFCBaseCtrl<FinanceScheduleDetail> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = Logger.getLogger(SchdlRepaymentDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SchdlRepaymentDialog; 		// autowired

	protected Textbox 		finReference; 				// autowired
	protected Datebox 		schdlDate;					// autowired
	protected Decimalbox 	schdlAmount;				// autowired
	protected Decimalbox 	schdlBalAmount;				// autowired
	protected Tabbox 		tabbox;
	
	protected Button btnPay; // autowire
	protected Button btnHelp;
	protected Button btnSearchFinReference;
	
	private transient PagedListService pagedListService;
	private int finformetter=2;

	/**
	 * default constructor.<br>
	 */
	public SchdlRepaymentDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Rule object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SchdlRepaymentDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SchdlRepaymentDialog);

		tabbox = (Tabbox)event.getTarget().getParent().getParent().getParent();
		// set Field Properties
		doSetFieldProperties();
		this.window_SchdlRepaymentDialog.doModal();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");		
		this.schdlAmount.setMaxlength(18);
		this.schdlAmount.setMaxlength(18);
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		MessageUtil.showHelpWindow(event, window_SchdlRepaymentDialog);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnClose(Event event) throws Exception {
		logger.debug("Entering"+event.toString());

		try {
			this.window_SchdlRepaymentDialog.onClose();
			Tab tab = (Tab) tabbox.getFellowIfAny("tab_SchdlRepayment");
			tab.close();
		} catch (final WrongValuesException e) {
			logger.error("Exception: ", e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onClick$btnSearchFinReference(Event event){
		logger.debug("Entering" + event.toString());
		
		Filter[] filters = new Filter[1] ;
		filters[0]= new Filter("finIsActive", 1, Filter.OP_EQUAL);
		Object dataObject = ExtendedSearchListBox.show(
				this.window_SchdlRepaymentDialog,"FinanceMain",filters);
		if (dataObject instanceof String){
			this.finReference.setValue(dataObject.toString());
		}else{
			FinanceMain details= (FinanceMain) dataObject;
			
			if (details != null) {
				
				finformetter=CurrencyUtil.getFormat(details.getFinCcy());
				this.finReference.setValue(details.getFinReference());
			}
		}
		
		this.schdlDate.setText("");
		this.schdlAmount.setText("");
		this.schdlBalAmount.setText("");

		if(!(this.finReference.getValue() == null || StringUtils.isEmpty(this.finReference.getValue()))){
			JdbcSearchObject<FinanceScheduleDetail> so = new JdbcSearchObject<FinanceScheduleDetail>(
					FinanceScheduleDetail.class);
			so.addTabelName("FinScheduleDetails_View");
			so.addFilter(new Filter("finReference", this.finReference.getValue(), Filter.OP_EQUAL));
			so.addFilter(new Filter("repayOnSchDate", 1, Filter.OP_EQUAL));
			so.addFilter(new Filter("schPftPaid", 0, Filter.OP_EQUAL));
			so.addFilter(new Filter("schPriPaid", 0, Filter.OP_EQUAL));
			so.addSort("schDate", false);

			List<FinanceScheduleDetail> schdlList = getPagedListService().getBySearchObject(so);
			if(schdlList.size() >0){
				FinanceScheduleDetail financeScheduleDetail =  schdlList.get(0);
				this.schdlDate.setValue(financeScheduleDetail.getSchDate());
				this.schdlAmount.setValue(PennantAppUtil.formateAmount(financeScheduleDetail.getRepayAmount(),finformetter));
				BigDecimal temp = financeScheduleDetail.getRepayAmount().subtract(financeScheduleDetail.getSchdPftPaid()).subtract(financeScheduleDetail.getSchdPriPaid());		
				this.schdlBalAmount.setValue(PennantAppUtil.formateAmount(temp,finformetter ));
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	// WorkFlow Components
	
	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

}