package com.pennant.webui.customermasters.customer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class CustExternalDedupCtrl extends GFCBaseCtrl<CustomerDedup> {
	private static final long serialVersionUID = -4484270347916527133L;
	private static final Logger logger = LogManager.getLogger(CustExternalDedupCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting auto wired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_custExternalDedupDialog;

	protected Textbox customerID;
	protected Textbox uID;
	protected Textbox name;
	protected Textbox drivingLicense;
	protected Textbox gSTIN;
	protected Textbox motherName;
	protected Textbox fatherName;
	protected Textbox spouseName;
	protected Textbox registrationORCINNo;
	protected Textbox tANNo;
	protected Textbox employerName;
	protected Textbox nRegaCard;
	protected Textbox gender;
	protected Textbox bankAccountNo;
	protected Datebox dob;
	protected Textbox iFSCCode;
	protected Textbox panNo;
	protected Textbox uCIC;
	protected Textbox voterID;
	protected Textbox cSSCORE;
	protected Textbox passportNo;
	protected Textbox rank;

	private CustomerDedup customerDedup;
	private FinanceMainService financeMainService;
	protected FinanceScheduleDetailDAO financeScheduleDetailDAO;

	public CustExternalDedupCtrl() {
		super();
	}

	protected void doSetProperties() {
		super.pageRightName = "custExternalDedupDialog";
	}

	public void onCreate$window_custExternalDedupDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_custExternalDedupDialog);

		try {

			if (arguments.containsKey("customerDedup")) {
				this.customerDedup = (CustomerDedup) arguments.get("customerDedup");
				setCustomerDedup(this.customerDedup);
			} else {
				setCustomerDedup(null);
			}
			doShowDialog(getCustomerDedup());
			// set Field Properties

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_custExternalDedupDialog.onClose();
		}

		logger.debug(Literal.LEAVING);
	}

	public void doWriteBeanToComponents(CustomerDedup customerDedup) {
		logger.debug("Entering");

		this.customerID.setValue(customerDedup.getCustCIF());
		this.uID.setValue(customerDedup.getAadharNumber());
		this.name.setValue(customerDedup.getCustShrtName());
		this.motherName.setValue(customerDedup.getMotherName());
		this.fatherName.setValue(customerDedup.getFatherName());
		this.spouseName.setValue(customerDedup.getSpouseName());
		this.employerName.setValue(customerDedup.getEmployerName());
		this.gender.setValue(customerDedup.getGender());
		this.dob.setValue(customerDedup.getCustDOB());
		this.panNo.setValue(customerDedup.getCustCRCPR());
		this.voterID.setValue(customerDedup.getVoterID());
		this.passportNo.setValue(customerDedup.getCustPassportNo());
		this.drivingLicense.setValue(customerDedup.getDrivingLicenceNo());
		this.gSTIN.setValue(customerDedup.getGstin());
		this.registrationORCINNo.setValue(customerDedup.getRegistrationNo());
		this.bankAccountNo.setValue(customerDedup.getBankAccountNo());
		this.tANNo.setValue(customerDedup.getTanNo());
		this.nRegaCard.setValue(customerDedup.getNrgeaCard());
		this.iFSCCode.setValue(customerDedup.getIfscCode());
		this.uCIC.setValue(customerDedup.getCustCoreBank());
		this.cSSCORE.setValue(customerDedup.getScore());
		this.rank.setValue(customerDedup.getRank());

	}

	public void doShowDialog(CustomerDedup customerDedup) throws Exception {
		logger.debug(Literal.ENTERING);
		try {
			doWriteBeanToComponents(customerDedup);
			window_custExternalDedupDialog.doModal();
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_custExternalDedupDialog.onClose();
		} catch (Exception e) {
			throw e;
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(false);
		logger.debug(Literal.LEAVING);
	}

	public CustomerDedup getCustomerDedup() {
		return customerDedup;
	}

	public void setCustomerDedup(CustomerDedup customerDedup) {
		this.customerDedup = customerDedup;
	}

	public FinanceMainService getFinanceMainService() {
		return financeMainService;
	}

	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

}
