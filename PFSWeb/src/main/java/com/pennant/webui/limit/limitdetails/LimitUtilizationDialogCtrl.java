package com.pennant.webui.limit.limitdetails;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.A;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.limit.LimitDetails;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.limit.LimitReferenceMapping;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.limit.LimitDetailService;
import com.pennant.backend.service.limitservice.impl.LimitManagement;
import com.pennant.backend.util.LimitConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.ScreenCTL;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class LimitUtilizationDialogCtrl extends GFCBaseCtrl<LimitHeader> implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(LimitUtilizationDialogCtrl.class);

	/*
	 * ************************************************************************ All the components that are defined here
	 * and have a corresponding component with the same 'id' in the zul-file are getting by our 'extends GFCBaseCtrl'
	 * GenericForwardComposer. ************************************************************************
	 */

	protected Window window_LimitUtilizationDialog;

	protected Listbox listBoxLimitDetail;
	protected Row row1;

	protected Label customerGroup;
	protected Label customerGroupName;
	protected Label label_CustomerId;

	protected Label customerId;
	protected Label custCoreBank;

	protected Label custFullName;
	protected Label custSalutationCode;
	protected Label limiDialogRule;

	protected Label custDftBranchCode;
	protected Label custDftBranchName;
	protected Label label_date;
	protected Label label_ccyDesc;
	protected Label label_currency;
	private Label limiDialogRuleValue;

	protected Div gb_CustomerDetails;
	protected Div gb_GroupDetails;
	protected Div gb_RuleBased;
	protected Div amountInDiv;
	protected Label amountInLabel;

	// not auto wired vars
	private LimitHeader limitHeader;
	private transient LimitDetailListCtrl limitDetailListCtrl;
	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.

	// ServiceDAOs / Domain Classes
	private transient LimitManagement limitManagement;
	private transient LimitDetailService limitDetailService;
	private transient PagedListService pagedListService;

	private BigDecimal THOUSANDS = new BigDecimal(1000);
	private BigDecimal LAKHS = new BigDecimal(100000);
	private BigDecimal MILLIONS = new BigDecimal(1000000);
	private BigDecimal CRORES = new BigDecimal(10000000);
	private BigDecimal BILLIONS = new BigDecimal(1000000000);
	private int ccyFormat = 0;

	/**
	 * default constructor.<br>
	 */
	public LimitUtilizationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "LimitHeaderDialog";
	}

	// ************************************************* //
	// *************** Component Events **************** //
	// ************************************************* //

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected LimitHeader object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_LimitUtilizationDialog(Event event) {
		logger.debug("Entring" + event.toString());
		try {

			setPageComponents(window_LimitUtilizationDialog);
			// READ OVERHANDED params !

			// READ OVERHANDED params !
			if (arguments.containsKey("limitHeader")) {
				this.limitHeader = (LimitHeader) arguments.get("limitHeader");
				LimitHeader befImage = new LimitHeader();
				BeanUtils.copyProperties(this.limitHeader, befImage);
				this.limitHeader.setBefImage(befImage);
				setLimitHeader(this.limitHeader);
			} else {
				setLimitHeader(null);
			}

			// READ OVERHANDED params !
			// we get the limitHeaderListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete limitHeader here.
			if (arguments.containsKey("limitDetailListCtrl")) {
				setCustomerLimitDetailListCtrl((LimitDetailListCtrl) arguments.get("limitDetailListCtrl"));
			} else {
				setCustomerLimitDetailListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getLimitHeader());
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws DatatypeConfigurationException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException, DatatypeConfigurationException {
		logger.debug("Entering" + event.toString());
		// doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws DatatypeConfigurationException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException, DatatypeConfigurationException {
		logger.debug("Entering" + event.toString());
		// doSave();
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
		MessageUtil.showHelpWindow(event, window_LimitUtilizationDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug("Entering" + event.toString());
		try {

			ScreenCTL.displayNotes(getNotes("LimitHeader", String.valueOf(getLimitHeader().getHeaderId()),
					getLimitHeader().getVersion()), this);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());

	}

	public void onItemClick(ForwardEvent event) {
		logger.debug("Entering" + event.toString());

		try {
			LimitDetails details = (LimitDetails) event.getData();
			List<LimitReferenceMapping> limitReferences = getLimitDetailService().getLimitReferences(details);
			if (limitReferences != null && limitReferences.size() > 0) {
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("limitDetails", details);
				map.put("limitReference", limitReferences);
				Executions.createComponents("/WEB-INF/pages/Limit/LimitDetails/LimitCustomerReferencesDialog.zul", null,
						map);
			} else {
				MessageUtil.showMessage("Limit reference details is not available");
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	public void render(List<LimitDetails> limitDetailslits) {

		this.listBoxLimitDetail.getItems().clear();

		for (LimitDetails limitDetails : limitDetailslits) {

			Listitem item = new Listitem();
			if (limitDetails.getDisplayStyle() != null) {
				item.setStyle(PennantStaticListUtil.getLimitDetailStyle(limitDetails.getDisplayStyle()));
			}
			Listcell lc;

			A itemLink = new A();
			itemLink.addForward("onClick", self, "onItemClick", limitDetails);
			itemLink.setStyle("text-decoration:none");
			lc = new Listcell();
			itemLink.setParent(lc);

			StringBuilder indent = new StringBuilder();
			for (int i = 0; i < limitDetails.getItemLevel(); i++) {
				if (indent.length() == 0) {
					indent.append("|___");
				} else {
					indent.append("___");
				}
			}
			if (limitDetails.getGroupCode() == null) {
				if (StringUtils.equals(LimitConstants.LIMIT_ITEM_UNCLSFD, limitDetails.getLimitLine())) {
					itemLink.setLabel(indent.toString() + "Unclassified");
				} else {
					itemLink.setLabel(indent.toString() + limitDetails.getLimitLineDesc());
				}
			} else {
				if (StringUtils.equals(LimitConstants.LIMIT_ITEM_TOTAL, limitDetails.getGroupCode())) {
					itemLink.setLabel(indent.toString() + "Total");
				} else
					itemLink.setLabel(indent.toString() + limitDetails.getGroupName());
			}

			lc.setParent(item);

			BigDecimal sactioned = BigDecimal.ZERO;
			BigDecimal reserved = BigDecimal.ZERO;
			BigDecimal utilisied = BigDecimal.ZERO;
			BigDecimal avialable = BigDecimal.ZERO;
			BigDecimal osPriBal = BigDecimal.ZERO;

			if (limitDetails.getLimitSanctioned() != null) {
				sactioned = setAmountIn(limitDetails.getLimitSanctioned());
			}

			if (limitDetails.getReservedLimit() != null) {
				reserved = setAmountIn(limitDetails.getReservedLimit());
			}

			if (limitDetails.getUtilisedLimit() != null) {
				utilisied = setAmountIn(limitDetails.getUtilisedLimit());
			}

			if (limitDetails.getOsPriBal() != null) {
				osPriBal = setAmountIn(limitDetails.getOsPriBal());
			}

			if (StringUtils.equals(LimitConstants.LIMIT_CHECK_RESERVED, limitDetails.getLimitChkMethod())) {
				avialable = sactioned.subtract(utilisied).subtract(reserved);
			} else if (StringUtils.equals(LimitConstants.LIMIT_CHECK_ACTUAL, limitDetails.getLimitChkMethod())) {
				avialable = sactioned.subtract(utilisied);
			}

			lc = new Listcell(CurrencyUtil.format(sactioned, ccyFormat));
			lc.setParent(item);

			lc = new Listcell(CurrencyUtil.format(reserved, ccyFormat));
			lc.setParent(item);

			lc = new Listcell(CurrencyUtil.format(utilisied, ccyFormat));
			lc.setParent(item);

			lc = new Listcell(CurrencyUtil.format(osPriBal, ccyFormat));
			lc.setParent(item);

			lc = new Listcell(CurrencyUtil.format(avialable, ccyFormat));
			lc.setParent(item);

			lc = new Listcell(DateUtil.formatToShortDate(limitDetails.getExpiryDate()));
			lc.setParent(item);
			limitDetails.setCurrency(getLimitHeader().getLimitCcy());
			limitDetails.setCustomerGroup(getLimitHeader().getCustomerGroup());
			limitDetails.setCustomerId(getLimitHeader().getCustomerId());

			this.listBoxLimitDetail.appendChild(item);

		}

	}

	private BigDecimal setAmountIn(BigDecimal amount) {

		if (amount == null) {
			amount = BigDecimal.ZERO;
		}

		String showIn = StringUtils.trimToEmpty(getLimitHeader().getShowLimitsIn());

		if (StringUtils.equals(LimitConstants.CCY_UNITS_THOUSANDS, showIn)) {
			amount = amount.divide(THOUSANDS);
		} else if (StringUtils.equals(LimitConstants.CCY_UNITS_LAKHS, showIn)) {
			amount = amount.divide(LAKHS);
		} else if (StringUtils.equals(LimitConstants.CCY_UNITS_MILLIONS, showIn)) {
			amount = amount.divide(MILLIONS);
		} else if (StringUtils.equals(LimitConstants.CCY_UNITS_CRORES, showIn)) {
			amount = amount.divide(CRORES);
		} else if (StringUtils.equals(LimitConstants.CCY_UNITS_BILLIONS, showIn)) {
			amount = amount.divide(BILLIONS);
		}

		return amount;
	}

	// ****************************************************************+
	// ************************ GUI operations ************************+
	// ****************************************************************+

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aLimitHeader
	 * @throws InterruptedException
	 */
	public void doShowDialog(LimitHeader aLimitHeader) throws InterruptedException {
		logger.debug("Entering");

		try {
			// fill the components with the data
			doWriteBeanToComponents(aLimitHeader);

			// stores the initial data for comparing if they are changed
			// during user action.
			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	// ****************************************************************+
	// ****************************++ helpers ************************++
	// ****************************************************************+

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.listBoxLimitDetail.setHeight(getListBoxHeight(4));
		if (getLimitHeader().getRuleCode() != null) {
			gb_CustomerDetails.setVisible(false);
			gb_GroupDetails.setVisible(false);
			gb_RuleBased.setVisible(true);
		} else {
			if (getLimitHeader().getCustomerId() != 0) {
				gb_CustomerDetails.setVisible(true);
				gb_GroupDetails.setVisible(false);
				gb_RuleBased.setVisible(false);
			} else {
				gb_CustomerDetails.setVisible(false);
				gb_GroupDetails.setVisible(true);
				gb_RuleBased.setVisible(false);
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aLimitHeader LimitHeader
	 */
	public void doWriteBeanToComponents(LimitHeader aLimitHeader) {
		logger.debug("Entering");

		this.customerGroup.setValue(aLimitHeader.getCustGrpCode());
		this.customerGroupName.setValue(aLimitHeader.getGroupName());
		this.customerId.setValue(aLimitHeader.getCustCIF());
		this.custCoreBank.setValue(aLimitHeader.getCustCoreBank());
		this.custFullName.setValue(aLimitHeader.getCustShrtName());
		this.custSalutationCode.setValue(aLimitHeader.getCustSalutationCode());
		this.custDftBranchCode.setValue(aLimitHeader.getCustDftBranch());
		this.custDftBranchName.setValue(aLimitHeader.getResponsibleBranchName());
		this.label_currency.setValue(aLimitHeader.getLimitCcy());
		if (aLimitHeader.getLimitExpiryDate() != null) {
			this.label_date.setValue(DateUtil.formatToShortDate(aLimitHeader.getLimitExpiryDate()));
		}
		if (aLimitHeader.getCcyDesc() != null) {
			this.label_ccyDesc.setValue(aLimitHeader.getCcyDesc());
		}
		this.recordStatus.setValue(aLimitHeader.getRecordStatus());
		this.limiDialogRule.setValue(aLimitHeader.getRuleCode());
		this.limiDialogRuleValue.setValue(aLimitHeader.getRuleValue());
		if (!StringUtils.equals(LimitConstants.CCY_UNITS_DEFAULT, aLimitHeader.getShowLimitsIn())) {
			amountInDiv.setVisible(true);
			amountInLabel.setValue(Labels.getLabel("ccy_unit_" + aLimitHeader.getShowLimitsIn()));
		} else {
			amountInDiv.setVisible(false);
		}
		ccyFormat = CurrencyUtil.getFormat(aLimitHeader.getLimitCcy());
		render(aLimitHeader.getCustomerLimitDetailsList());
		logger.debug("Leaving");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public LimitHeader getLimitHeader() {
		return this.limitHeader;
	}

	public void setLimitHeader(LimitHeader limitHeader) {
		this.limitHeader = limitHeader;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public LimitDetailListCtrl getCustomerLimitDetailListCtrl() {
		return limitDetailListCtrl;
	}

	public void setCustomerLimitDetailListCtrl(LimitDetailListCtrl customerLimitDetailListCtrl) {
		this.limitDetailListCtrl = customerLimitDetailListCtrl;
	}

	public LimitDetailService getLimitDetailService() {
		return limitDetailService;
	}

	public void setLimitDetailService(LimitDetailService limitDetailService) {
		this.limitDetailService = limitDetailService;
	}

	public LimitManagement getLimitManagement() {
		return limitManagement;
	}

	public void setLimitManagement(LimitManagement limitManagement) {
		this.limitManagement = limitManagement;
	}

}