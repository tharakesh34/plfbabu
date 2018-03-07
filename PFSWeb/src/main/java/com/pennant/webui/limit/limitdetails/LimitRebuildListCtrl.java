package com.pennant.webui.limit.limitdetails;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.service.limit.LimitDetailService;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class LimitRebuildListCtrl extends GFCBaseCtrl<LimitHeader> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(LimitRebuildListCtrl.class);

	protected Window window_LimitRebuildList;
	protected Borderlayout borderLayout_LimitRebuildList;
	protected Tabbox tabbox;

	protected Button btnRebuild;
	protected ExtendedCombobox customer;
	protected ExtendedCombobox group;

	private transient LimitDetailService limitDetailService;

	private long custId = 0;
	private long groupId = 0;

	public LimitRebuildListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * On creating Window
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_LimitRebuildList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// Set the page level components.
		setPageComponents(window_LimitRebuildList);

		try {

			doSetFieldProperties();

			this.tabbox = (Tabbox) event.getTarget().getParent().getParent().getParent().getParent();
			this.window_LimitRebuildList.doModal();

		} catch (Exception e) {
			logger.error("Exception: ", e);
			closeDialog();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * 
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Customer
		this.customer.setModuleName("CustomerRebuild");
		this.customer.setValueColumn("CustCIF");
		this.customer.setDescColumn("CustShrtName");
		this.customer.setDisplayStyle(2);
		this.customer.setTextBoxWidth(130);
		this.customer.setValidateColumns(new String[] { "CustCIF", "CustShrtName" });
		Filter[] filters1 = new Filter[1];
		filters1[0] = new Filter("CustomerId", 0, Filter.OP_NOT_EQUAL);
		this.customer.setFilters(filters1);

		// Customer Group
		this.group.setModuleName("CustomerGrpRebuild");
		this.group.setValueColumn("CustGrpCode");
		this.group.setDescColumn("GroupName");
		this.group.setDisplayStyle(2);
		this.group.setTextBoxWidth(130);
		this.group.setValidateColumns(new String[] { "CustGrpCode", "GroupName" });
		Filter[] filters2 = new Filter[1];
		filters2[0] = new Filter("CustomerGroup", 0, Filter.OP_NOT_EQUAL);
		this.group.setFilters(filters2);

		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) {
		if (doClose(false)) {
			if (this.tabbox != null) {
				this.tabbox.getSelectedTab().close();
			}
		}
	}

	/**
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnClear(Event event) throws Exception {
		logger.debug("Entering" + event.toString()); 

		doClearMessage();
		this.customer.setValue("", "");
		this.group.setValue("", "");

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onFulfill$customer(Event event) throws Exception {
		logger.debug("Entering");

		this.customer.setConstraint("");
		Clients.clearWrongValue(this.customer);

		Object dataObject = customer.getObject();
		if (dataObject instanceof String) {
			this.customer.setValue(dataObject.toString());
			this.customer.setDescription("");
		} else {
			LimitHeader details = (LimitHeader) dataObject;
			if (details != null) {
				this.customer.setValue(details.getCustCIF());
				this.customer.setDescription(details.getCustShrtName());
				this.customer.setAttribute("CustomerId", details.getCustomerId());
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onFulfill$group(Event event) throws Exception {
		logger.debug("Entering");

		this.group.setConstraint("");
		Clients.clearWrongValue(this.group);

		Object dataObject = group.getObject();
		if (dataObject instanceof String) {
			this.group.setValue(dataObject.toString());
			this.group.setDescription("");
		} else {
			LimitHeader details = (LimitHeader) dataObject;
			if (details != null) {
				this.group.setValue(details.getCustGrpCode());
				this.group.setDescription(details.getCustGrpCode());
				this.group.setAttribute("GroupId", details.getCustomerGroup());
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param event
	 * @throws InterruptedException 
	 */
	public void onClick$btnRebuild(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		doWriteComponentsToBean();

		if (custId > 0) {

			// Customer Rebuild
			List<Object> returnList = this.limitDetailService.processCustomerRebuild(custId);

			if (!returnList.isEmpty()) {

				// If Customer Exists in EOD Processing, Not allowed to Maintenance till completion
				if ((int) returnList.get(0) > 0) {
					MessageUtil.showError(Labels.getLabel("Customer_Rebuild_EOD"));
					return;

				} else if (!(Boolean) returnList.get(1)) {
					MessageUtil.showError(Labels.getLabel("Customer_Rebuild_Failed"));
					return;
				}
			}
		}

		if (groupId > 0) {

			// Customer Group Rebuild
			List<Object> returnList = this.limitDetailService.processCustomerGroupRebuild(groupId);

			if (!returnList.isEmpty()) {

				// If Customer Exists in EOD Processing, Not allowed to Maintenance till completion
				if ((int) returnList.get(0) > 0) {
					MessageUtil.showError(Labels.getLabel("CustomerGrp_Rebuild_EOD"));
					return;

				} else if (!(Boolean) returnList.get(1)) {
					MessageUtil.showError(Labels.getLabel("CustomerGrp_Rebuild_Failed"));
					return;
				}
			}
		}

		//MessageUtil.showMessage(Labels.getLabel("LimitRebuild_Sucess"));
		//Events.sendEvent("onClick$btnClose", this.window_LimitRebuildList, "");

		Clients.showNotification(Labels.getLabel("LimitRebuild_Sucess"));
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doWriteComponentsToBean()throws InterruptedException {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		doClearMessage();

		// Customer Limit Set Up
		try {
			Object custObject = this.customer.getAttribute("CustomerId");

			if (StringUtils.isEmpty(this.customer.getValue()) || custObject == null) {
				throw new WrongValueException(this.customer, Labels.getLabel("FIELD_IS_MAND", new String[] { Labels.getLabel("label_LimitRebuildList_Customer.value") }));
			} else {
				custId = Long.valueOf(custObject.toString());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Customer Group Limit Set Up
		try {
			Object grpobject = this.group.getAttribute("GroupId");

			if (StringUtils.isEmpty(this.group.getValue()) || grpobject == null) {
				throw new WrongValueException(this.group, Labels.getLabel("FIELD_IS_MAND", new String[] { Labels.getLabel("label_LimitRebuildList_Group.value") }));
			} else {
				groupId = Long.valueOf(grpobject.toString());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if ((custId == 0 && groupId == 0) && !wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	protected void doClearMessage() {
		logger.debug("Entering");

		this.customer.setConstraint("");
		this.customer.setErrorMessage("");
		Clients.clearWrongValue(this.customer);

		this.group.setConstraint("");
		this.group.setErrorMessage("");
		Clients.clearWrongValue(this.group);

		logger.debug("Leaving");
	}

	// setters / getters

	public LimitDetailService getLimitDetailService() {
		return limitDetailService;
	}
	public void setLimitDetailService(LimitDetailService limitDetailService) {
		this.limitDetailService = limitDetailService;
	}
}
