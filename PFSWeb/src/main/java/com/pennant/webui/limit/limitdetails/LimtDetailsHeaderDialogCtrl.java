package com.pennant.webui.limit.limitdetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerGroup;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;

public class LimtDetailsHeaderDialogCtrl extends GFCBaseCtrl<LimitHeader> implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(LimtDetailsHeaderDialogCtrl.class);

	/*
	 * ************************************************************************
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ************************************************************************
	 */
	protected Window window_LimtDetailsHeaderDialog;
	protected Borderlayout borderLayout_LimtDetailsHeaderDialog;

	protected Button btnClose;
	protected Button btnProceed;

	protected ExtendedCombobox group;
	protected ExtendedCombobox customer;
	protected Grid searchGrid;

	private LimitHeader limitHeader; // overhanded per param
	private transient LimitDetailListCtrl limitDetailListCtrl; // overhanded per param

	
	/**
	 * default constructor.<br>
	 */
	public LimtDetailsHeaderDialogCtrl() {
		super();
	}
	
	@Override
	protected void doSetProperties() {
		super.pageRightName = "LimitHeaderDialog";
		super.enqiryModule = (Boolean) arguments.get("enqiryModule");
	}

	// ***************************************************//
	// *************** Component Events ******************//
	// ***************************************************//

	public void onCreate$window_LimtDetailsHeaderDialog(Event event) throws Exception {
		logger.debug("Entering");
		
		setPageComponents(window_LimtDetailsHeaderDialog);

		limitHeader=(LimitHeader) arguments.get("limitHeader");
		limitHeader.setNewRecord(true);

		// READ OVERHANDED params !
		// we get the limitHeaderListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete limitHeader here.
		if (arguments.containsKey("limitDetailListCtrl")) {
			setLimitDetailListCtrl((LimitDetailListCtrl) arguments.get("limitDetailListCtrl"));
		} else {
			setLimitDetailListCtrl(null);
		}
		
		/*doLoadWorkFlow(this.limitHeader.isWorkflow(),
				this.limitHeader.getWorkflowId(),
				this.limitHeader.getNextTaskId());
*/
		// set Field Properties
		doSetFieldProperties();

		logger.debug("Leaving");
	}

	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Group
		this.group.getTextbox().setReadonly(false);
		this.group.setModuleName("CustomerGroup");
		this.group.setValueColumn("CustGrpID");
		this.group.setValidateColumns(new String[] { "CustGrpID", "CustGrpCode", "CustGrpDesc" });

		List<String> existingGroups = PennantAppUtil.getLimitHeaderCustomer(false,false);
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("CustGrpID", existingGroups, Filter.OP_NOT_IN);
		if (existingGroups != null && existingGroups.size() > 0) {
			this.group.setFilters(filters);
		}
		// Customer
		this.customer.getTextbox().setReadonly(false);
		this.customer.setModuleName("Customer");
		this.customer.setValueColumn("CustCIF");
		this.customer.setValidateColumns(new String[] { "CustCIF", "CustShrtName", "CustCtgCode", "CustFName", "CustLName" });
		List<String> existingcustomers = PennantAppUtil.getLimitHeaderCustomer(true,false);
		Filter[] filters2 = new Filter[1];
		filters2[0] = new Filter("CustID", existingcustomers, Filter.OP_NOT_IN);
		if (existingcustomers != null && existingcustomers.size() > 0) {
			this.customer.setFilters(filters2);
		}
		
		//Rule
		logger.debug("Leaving");
	}

	public void onFulfill$customer(Event event) throws Exception {
		logger.debug("Entering");
		btnProceed.setDisabled(false);
		Object dataObject = customer.getObject();
		if (dataObject instanceof String) {
			this.customer.setValue(dataObject.toString());
			this.customer.setDescription("");
		} else {
			Customer details = (Customer) dataObject;
			if (details != null) {
				this.customer.setValue(details.getCustCIF());
				//this.customer.setDescription(details.getCustShrtName());
				getLimitHeader().setCustomerId(details.getCustID());
				getLimitHeader().setCustCIF(details.getCustCIF());
				getLimitHeader().setCustCoreBank(details.getCustCoreBank());
				getLimitHeader().setCustSalutationCode(details.getCustSalutationCode());
				getLimitHeader().setCustShrtName(details.getCustShrtName());
				getLimitHeader().setCustFullName(PennantApplicationUtil.getFullName(details.getCustFName(), details.getCustMName(), details.getCustShrtName()));
				getLimitHeader().setCustDftBranch(details.getCustDftBranch());
				getLimitHeader().setResponsibleBranchName(details.getLovDescCustDftBranchName());
				getLimitHeader().setCustomerGroup(0);
				this.group.setValue("");
				this.group.setDescription("");
			}
		}
		logger.debug("Leaving");
	}

	public void onFulfill$group(Event event) throws Exception {
		logger.debug("Entering");
		btnProceed.setDisabled(false);
		Object dataObject = group.getObject();
		if (dataObject instanceof String) {
			this.group.setValue(dataObject.toString());
			this.group.setDescription("");
		} else {
			CustomerGroup details = (CustomerGroup) dataObject;
			if (details != null) {
				this.group.setValue(details.getCustGrpCode());
				//this.group.setDescription(details.getCustGrpDesc());
				getLimitHeader().setCustomerId(0);
				getLimitHeader().setCustGrpCode(details.getCustGrpCode());
				getLimitHeader().setCustomerGroup(details.getCustGrpID());
				getLimitHeader().setGroupName(details.getCustGrpDesc());
				this.customer.setValue("");
				this.customer.setDescription("");
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * When the limitDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		this.window_LimtDetailsHeaderDialog.onClose();
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnProceed(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		if (getLimitHeader().getCustomerId() != 0 || getLimitHeader().getCustomerGroup() != 0) {
			getLimitDetailListCtrl().doShowDialogPage(getLimitHeader());
			window_LimtDetailsHeaderDialog.onClose();
		} else {
			try {
				if (getLimitHeader().getCustomerId() == 0) {
					throw new WrongValueException(group,  "  Please Select " +Labels.getLabel("label_LimtDetailsHeaderDialog_Group.value"));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (getLimitHeader().getCustomerGroup() == 0) {
					throw new WrongValueException(customer, " Please Select "+ Labels.getLabel("label_LimtDetailsHeaderDialog_Customer.value") );
					}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving" + event.toString());
	}

	// ******************************************************//
	// ****************** getter / setter ******************//
	// ******************************************************//
	public LimitHeader getLimitHeader() {
		return limitHeader;
	}

	public void setLimitHeader(LimitHeader limitHeader) {
		this.limitHeader = limitHeader;
	}

	public LimitDetailListCtrl getLimitDetailListCtrl() {
		return limitDetailListCtrl;
	}

	public void setLimitDetailListCtrl(LimitDetailListCtrl limitDetailListCtrl) {
		this.limitDetailListCtrl = limitDetailListCtrl;
	}
}