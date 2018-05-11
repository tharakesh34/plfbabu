package com.pennant.webui.finance.financemain;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class ODFacilityFinanceMainDialogCtrl extends FinanceMainBaseCtrl implements Serializable {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(ODFacilityFinanceMainDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_ODFacilityFinanceMainDialog; 					
	/**
	 * default constructor.<br>
	 */
	public ODFacilityFinanceMainDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.doSetProperties();
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ODFacilityFinanceMainDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ODFacilityFinanceMainDialog);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("financeDetail")) {
			setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
			FinanceMain befImage = new FinanceMain();
			BeanUtils.copyProperties(getFinanceDetail().getFinScheduleData().getFinanceMain(), befImage);
			getFinanceDetail().getFinScheduleData().getFinanceMain().setBefImage(befImage);
			setFinanceDetail(getFinanceDetail());
			old_NextRoleCode = getFinanceDetail().getFinScheduleData().getFinanceMain().getNextRoleCode();
		}

		// READ OVERHANDED params !
		// we get the financeMainListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete financeMain here.
		if (arguments.containsKey("financeMainListCtrl")) {
			setFinanceMainListCtrl((FinanceMainListCtrl) arguments.get("financeMainListCtrl"));
		} 
		
		if (arguments.containsKey("financeSelectCtrl")) {
			setFinanceSelectCtrl((FinanceSelectCtrl) arguments.get("financeSelectCtrl"));
		} 

		if (arguments.containsKey("tabbox")) {
			listWindowTab = (Tab) arguments.get("tabbox");
		}

		if (arguments.containsKey("moduleDefiner")) {
			moduleDefiner = (String) arguments.get("moduleDefiner");
		}

		if (arguments.containsKey("eventCode")) {
			eventCode = (String) arguments.get("eventCode");
		}
		
		if (arguments.containsKey("menuItemRightName")) {
			menuItemRightName = (String) arguments.get("menuItemRightName");
		}

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		doLoadWorkFlow(financeMain);

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateMenuRoleAuthorities(getRole(), super.pageRightName, menuItemRightName);
		}else{
			this.south.setHeight("0px");
		}

		setMainWindow(window_ODFacilityFinanceMainDialog);
		setProductCode("ODFacility");
		
		if(financeMain.isAllowRepayRvw()){
			this.label_FinanceMainDialog_RepayRvwFrq.setVisible(true);
		}else{
			this.label_FinanceMainDialog_RepayRvwFrq.setVisible(false);
		}
		/* set components visible dependent of the users rights */
		doCheckRights();

		this.basicDetailTabDiv.setHeight(this.borderLayoutHeight - 100 - 52+ "px");

		// set Field Properties
		doSetFieldProperties();
		
		doShowDialog(getFinanceDetail());
		Events.echoEvent("onPostWinCreation", this.self, null);
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	public void doSetFieldProperties() {
		
		logger.debug("Entering");
		
		super.doSetFieldProperties();
		
		this.accountsOfficer.setMandatoryStyle(false);
		this.dsaCode.setMandatoryStyle(false);
		this.finAssetValue.setProperties(true, CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()));
		this.repayPftFrq.setMandatoryStyle(true);
		
		if (getFinanceDetail().getFinScheduleData().getFinanceType().isDroplineOD()) {
			this.repayFrq.setMandatoryStyle(true);
		} else {
			this.repayFrq.setMandatoryStyle(false);
		}
		
		this.firstDroplineDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.maturityDate.setReadonly(true);
		this.odMnthlyTerms.setValue(0);
		this.odYearlyTerms.setValue(0);
		
		logger.debug("Leaving");
		
	}
	
	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_ODFacilityFinanceMainDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doClose();
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		processSave();
		logger.debug(Literal.LEAVING);
	}
 
	
	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		MessageUtil.showHelpWindow(event, window_ODFacilityFinanceMainDialog);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnClose(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			logger.error("Exception: ", e);
			throw e;
		}
		logger.debug("Leaving " + event.toString());
	}

	
}
