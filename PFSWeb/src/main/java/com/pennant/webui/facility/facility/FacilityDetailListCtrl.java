/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */
/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : FacilityDetailListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-12-2013 * *
 * Modified Date : 04-12-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-12-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.facility.facility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Html;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.collateral.FacilityDetail;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.facility.Facility;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.service.facility.FacilityService;
import com.pennant.backend.util.FacilityConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Collateral/FacilityDetail/FacilityDetailList.zul file.
 */
public class FacilityDetailListCtrl extends GFCBaseListCtrl<FacilityDetail> {
	private static final long serialVersionUID = 1L;

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FacilityDetailList; // autowired
	protected Borderlayout borderLayout_FacilityDetailList; // autowired
	protected Listbox listBoxFacilityDetail; // autowired
	protected Listbox listBoxFinances; // autowired
	protected Tabbox tabBoxCenter;
	// List headers
	protected Button button_FacilityDetailList_NewFacilityDetail; // autowired
	private Facility facility = null;
	private Object ctrlObject = null;
	private List<FacilityDetail> facilityDetailList = new ArrayList<FacilityDetail>();
	private FacilityService facilityService;
	private boolean enqModule = false;
	private String userRole;

	Date appldate = SysParamUtil.getAppDate();

	// NEEDED for the ReUse in the SearchWindow
	/**
	 * default constructor.<br>
	 */
	public FacilityDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {

	}

	public void onCreate$window_FacilityDetailList(ForwardEvent event) {
		logger.debug("Entering");
		try {
			if (arguments.containsKey("enqModule")) {
				enqModule = true;
			} else {
				enqModule = false;
			}
			if (arguments.containsKey("facility")) {
				setFacility((Facility) arguments.get("facility"));
				doFillFacilityDetail(getFacility().getFacilityDetails());
			}
			if (arguments.containsKey("userRole")) {
				userRole = (String) arguments.get("userRole");
			}
			if (arguments.containsKey("control")) {
				this.setCtrlObject((Object) arguments.get("control"));
			}
			doCheckRights();
			try {
				getCtrlObject().getClass().getMethod("setFacilityDetailListCtrl", this.getClass())
						.invoke(getCtrlObject(), this);
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
			getBorderLayoutHeight();
			if (enqModule) {
				this.button_FacilityDetailList_NewFacilityDetail.setVisible(false);
			}
			this.listBoxFinances.setHeight(this.borderLayoutHeight - 340 + "px");
			this.listBoxFacilityDetail.setHeight(this.borderLayoutHeight - 365 + "px");
			this.tabBoxCenter.setHeight(this.borderLayoutHeight - 80 + "px");
			this.borderLayout_FacilityDetailList.setHeight(this.borderLayoutHeight - 80 + "px");
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("FacilityDetailDialog", userRole);
		this.button_FacilityDetailList_NewFacilityDetail
				.setVisible(getUserWorkspace().isAllowed("button_FacilityDetailDialog_btnNew"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.collateral.facilitydetail.model. FacilityDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 */
	public void onFacilityDetailItemDoubleClicked(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		Listitem item = (Listitem) event.getOrigin().getTarget();
		FacilityDetail itemdata = (FacilityDetail) item.getAttribute("data");
		if (!StringUtils.trimToEmpty(itemdata.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
			itemdata.setNewRecord(false);
			Map<String, Object> map = getDefaultArguments();
			map.put("facilityDetail", itemdata);
			map.put("facilityDetailListCtrl", this);
			map.put("role", userRole);
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/Facility/Facility/FacilityDetailDialog.zul", null, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the FacilityDetail dialog with a new empty entry. <br>
	 */
	public void onClick$button_FacilityDetailList_NewFacilityDetail(Event event) {
		logger.debug("Entering" + event.toString());
		// create a new IncomeExpenseDetail object, We GET it from the backEnd.
		Map<String, Object> map = getDefaultArguments();
		final FacilityDetail aFacilityDetail = getFacilityService().getNewFacilityDetail();
		aFacilityDetail.setCAFReference(getFacility().getCAFReference());
		aFacilityDetail.setCustID(getFacility().getCustID());
		aFacilityDetail.setNewRecord(true);
		map.put("facility", getFacility());
		map.put("facilityDetail", aFacilityDetail);
		map.put("facilityDetailListCtrl", this);
		map.put("role", userRole);
		map.put("filter", getFacilityNewFilters());
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Facility/Facility/SelectNewFacilityDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void loadWindow(FacilityDetail aFacilityDetail, Commitment commitment) {
		Map<String, Object> map = getDefaultArguments();
		map.put("facilityDetail", aFacilityDetail);
		map.put("facilityDetailListCtrl", this);
		map.put("commitment", commitment);
		map.put("role", userRole);
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Facility/Facility/FacilityDetailDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	public void doFillFacilityDetail(List<FacilityDetail> facilityDetails) {
		logger.debug("Entering");
		if (facilityDetails != null) {
			getFacilityDetailList().clear();
			setFacilityDetailList(facilityDetails);
			fillFacilityDetails(facilityDetails);
			if (getFacilityDialogCtrl() != null) {
				getFacilityDialogCtrl().setCountryLimitAdeq(facilityDetails);
			}
		}
		logger.debug("Leaving");
	}

	private void fillFacilityDetails(List<FacilityDetail> facilityDetails) {
		logger.debug("Entering");
		this.listBoxFacilityDetail.getItems().clear();
		for (FacilityDetail facilityDetail : facilityDetails) {
			Listitem item = new Listitem();
			item.setStyle("vertical-align: text-top;");
			Listcell lc;
			lc = new Listcell(getBookingUnit());
			lc.setParent(item);
			lc = new Listcell();
			StringBuilder content = new StringBuilder();
			content.append("Type:");
			content.append(facilityDetail.getFacilityTypeDesc());
			content.append("<BR>");
			content.append("Purpose:");
			content.append(facilityDetail.getPurpose());
			content.append("<BR>");
			content.append("Repayments:");
			content.append(facilityDetail.getRepayments());
			if (StringUtils.isNotBlank(facilityDetail.getLCPeriod())) {
				content.append("<BR>");
				content.append("L/C Period:");
				content.append(facilityDetail.getLCPeriod());
			}
			if (StringUtils.isNotBlank(facilityDetail.getUsancePeriod())) {
				content.append("<BR>");
				content.append("Usance Period:");
				content.append(facilityDetail.getUsancePeriod());
			}
			content.append("<BR>");
			content.append("Security:");
			content.append(facilityDetail.getSecurityDesc());
			lc.appendChild(new Html(content.toString()));
			lc.setParent(item);
			lc = new Listcell(facilityDetail.getFacilityFor());
			lc.setParent(item);
			lc = new Listcell(facilityDetail.getRevolving());
			lc.setParent(item);
			lc = new Listcell(getTenorDesc(facilityDetail.getTenorYear(), facilityDetail.getTenorMonth()));
			lc.setParent(item);
			lc = new Listcell(facilityDetail.getPricing());
			lc.setParent(item);
			lc = new Listcell(CalculationUtil.getConvertedAmountASString(facilityDetail.getFacilityCCY(),
					AccountConstants.CURRENCY_USD, facilityDetail.getExposure()));
			lc.setParent(item);
			lc = new Listcell(CalculationUtil.getConvertedAmountASString(facilityDetail.getFacilityCCY(),
					AccountConstants.CURRENCY_USD, facilityDetail.getExistingLimit()));
			lc.setParent(item);
			lc = new Listcell(CalculationUtil.getConvertedAmountASString(facilityDetail.getFacilityCCY(),
					AccountConstants.CURRENCY_USD, facilityDetail.getNewLimit()));
			lc.setParent(item);
			lc = new Listcell(facilityDetail.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(facilityDetail.getRecordType()));
			lc.setParent(item);
			item.setAttribute("data", facilityDetail);
			if (!enqModule) {
				ComponentsCtrl.applyForward(item, "onDoubleClick=onFacilityDetailItemDoubleClicked");
			}
			this.listBoxFacilityDetail.appendChild(item);
		}
		logger.debug("Leaving");
	}

	public void onSelect$tabSummary(Event event) {
		logger.debug("Entering");
		prepareData(getFacility().getCustID());
		logger.debug("Leaving");
	}

	private void prepareData(long custID) {
		logger.debug("Entering");
		JdbcSearchObject<FinanceSummary> jdbcSearchObject = new JdbcSearchObject<FinanceSummary>(FinanceSummary.class);
		jdbcSearchObject.addTabelName("FacilityCommitmentDetail_View");
		jdbcSearchObject.addFilterEqual("CustID", custID);
		List<FinanceSummary> existFinances = getPagedListWrapper().getPagedListService()
				.getBySearchObject(jdbcSearchObject);
		if (existFinances != null && !existFinances.isEmpty()) {
			prepareRenderer(existFinances);
		}
		logger.debug("Leaving");
	}

	private void prepareRenderer(List<FinanceSummary> existFinances) {
		logger.debug("Entering");

		this.listBoxFinances.getItems().clear();
		this.listBoxFinances.setSizedByContent(true);
		List<FinanceSummary> nonCommitmentList = new ArrayList<FinanceSummary>();
		Map<String, List<FinanceSummary>> commitmentMap = new HashMap<String, List<FinanceSummary>>();
		Date oldestDate = existFinances.get(0).getFinStartDate();
		Date finalDate = existFinances.get(0).getMaturityDate();
		BigDecimal totCmtAmount = BigDecimal.ZERO;
		BigDecimal totUnUsed = BigDecimal.ZERO;
		BigDecimal cmtTotOutstanding = BigDecimal.ZERO;
		BigDecimal unCmtTotOutstanding = BigDecimal.ZERO;

		// Summary Calculation
		for (FinanceSummary financeSummary : existFinances) {
			// Check Commitment Exists.
			if (StringUtils.isNotBlank(financeSummary.getFinCommitmentRef())) {
				// Check Commitment Expired.
				if (financeSummary.getCmtExpiryDate() != null && financeSummary.getCmtExpiryDate().before(appldate)) {
					// Check Finance Matured.
					if (financeSummary.getMaturityDate().before(appldate)) {
						continue;
					}
				}
			}

			if (financeSummary.getFinStartDate().before(oldestDate)) {
				oldestDate = financeSummary.getFinStartDate();
			}
			if (financeSummary.getMaturityDate().after(finalDate)) {
				finalDate = financeSummary.getMaturityDate();
			}
			// List and map segregation for rendering
			if (StringUtils.isNotBlank(financeSummary.getFinCommitmentRef())) {
				totCmtAmount = totCmtAmount.add(CalculationUtil.getConvertedAmount(financeSummary.getFinCcy(),
						AccountConstants.CURRENCY_USD, financeSummary.getCmtAmount()));
				totUnUsed = totUnUsed.add(CalculationUtil.getConvertedAmount(financeSummary.getFinCcy(),
						AccountConstants.CURRENCY_USD, financeSummary.getCmtAvailable()));
				cmtTotOutstanding = cmtTotOutstanding.add(financeSummary.getTotalOutStanding());

				if (commitmentMap.containsKey(financeSummary.getFinCommitmentRef())) {
					commitmentMap.get(financeSummary.getFinCommitmentRef()).add(financeSummary);
				} else {
					List<FinanceSummary> list = new ArrayList<FinanceSummary>();
					list.add(financeSummary);
					commitmentMap.put(financeSummary.getFinCommitmentRef(), list);
				}
			} else {
				unCmtTotOutstanding = unCmtTotOutstanding
						.add(CalculationUtil.getConvertedAmount(financeSummary.getFinCcy(),
								AccountConstants.CURRENCY_USD, financeSummary.getTotalOutStanding()));
				nonCommitmentList.add(financeSummary);
			}
		}

		// List box rendering
		Listitem item;
		Listcell cell;
		Listgroup group = null;
		// First Lisr Row For Summary
		item = new Listitem();
		item.setStyle("background:#ADD8E6");
		cell = new Listcell("Total");
		cell.setStyle("font-weight:bold;cursor:default");
		cell.setParent(item);
		cell = new Listcell("");
		cell.setParent(item);
		cell = new Listcell(formatdDate(oldestDate));
		cell.setParent(item);
		cell = new Listcell(formatdDate(finalDate));
		cell.setParent(item);
		cell = new Listcell(formatdAmount(totCmtAmount, AccountConstants.CURRENCY_USD_FORMATTER));
		cell.setStyle("text-align:right;");
		cell.setParent(item);
		cell = new Listcell(formatdAmount(totUnUsed, AccountConstants.CURRENCY_USD_FORMATTER));
		cell.setStyle("text-align:right;");
		cell.setParent(item);
		cell = new Listcell(
				formatdAmount(cmtTotOutstanding.add(unCmtTotOutstanding), AccountConstants.CURRENCY_USD_FORMATTER));
		cell.setStyle("text-align:right;");
		cell.setParent(item);
		cell = new Listcell();
		cell.setParent(item);
		cell = new Listcell();
		cell.setParent(item);
		this.listBoxFinances.appendChild(item);
		// List Group And Child's With Commitments
		doFillCommitmentGroups(item, cell, group, commitmentMap);
		// Finance With Out Commitment
		doFillUnCommitedGroup(item, cell, group, nonCommitmentList, unCmtTotOutstanding);
		logger.debug("Leaving");
	}

	private void doFillCommitmentGroups(Listitem item, Listcell cell, Listgroup group,
			Map<String, List<FinanceSummary>> commitmentMap) {
		for (String key : commitmentMap.keySet()) {

			List<FinanceSummary> list = commitmentMap.get(key);
			if (list != null && !list.isEmpty()) {
				FinanceSummary finsum = list.get(0);

				String keyOldStartDt = finsum.getFinCommitmentRef() + "cmtOldestStartDt";
				String keyCmtTotOutStd = finsum.getFinCommitmentRef() + "cmtTotOutStanding";
				String keyOutStdPerc = finsum.getFinCommitmentRef() + "cmtOutStdPerc";

				BigDecimal cmtTotOutStanding = BigDecimal.ZERO;
				Date cmtOldestStartDt = finsum.getFinStartDate();

				group = new Listgroup();
				cell = new Listcell(finsum.getFinCommitmentRef());
				cell.setParent(group);
				cell = new Listcell();
				cell.setParent(group);
				cell = new Listcell();
				cell.setId(keyOldStartDt);
				cell.setParent(group);
				cell = new Listcell(formatdDate(finsum.getCmtExpiryDate()));
				cell.setParent(group);
				cell = new Listcell(CalculationUtil.getConvertedAmountASString(finsum.getFinCcy(),
						AccountConstants.CURRENCY_USD, finsum.getCmtAmount()));
				cell.setStyle("text-align:right;");
				cell.setParent(group);
				cell = new Listcell(CalculationUtil.getConvertedAmountASString(finsum.getFinCcy(),
						AccountConstants.CURRENCY_USD, finsum.getCmtAvailable()));
				cell.setStyle("text-align:right;");
				cell.setParent(group);
				cell = new Listcell();
				cell.setStyle("text-align:right;");
				cell.setId(keyCmtTotOutStd);
				cell.setParent(group);
				cell = new Listcell();
				cell.setStyle("text-align:right;");
				cell.setId(keyOutStdPerc);
				cell.setParent(group);
				cell = new Listcell();
				cell.setParent(group);
				this.listBoxFinances.appendChild(group);
				for (FinanceSummary financeSummary : list) {
					item = new Listitem();
					cell = new Listcell(financeSummary.getFinType());
					cell.setParent(item);
					cell = new Listcell(financeSummary.getFinReference());
					cell.setParent(item);
					cell = new Listcell(formatdDate(financeSummary.getFinStartDate()));
					cell.setParent(item);
					cell = new Listcell(formatdDate(financeSummary.getMaturityDate()));
					cell.setParent(item);
					cell = new Listcell(CalculationUtil.getConvertedAmountASString(financeSummary.getFinCcy(),
							AccountConstants.CURRENCY_USD, financeSummary.getTotalOriginal()));
					cell.setStyle("text-align:right;");
					cell.setParent(item);
					cell = new Listcell(CalculationUtil.getConvertedAmountASString(financeSummary.getFinCcy(),
							AccountConstants.CURRENCY_USD, financeSummary.getTotalPaid()));
					cell.setStyle("text-align:right;");
					cell.setParent(item);
					cell = new Listcell(CalculationUtil.getConvertedAmountASString(financeSummary.getFinCcy(),
							AccountConstants.CURRENCY_USD, financeSummary.getTotalOutStanding()));
					cell.setStyle("text-align:right;");
					cell.setParent(item);
					cell = new Listcell();
					cell.setParent(item);
					cell = new Listcell(financeSummary.getFinStatus());
					cell.setParent(item);
					this.listBoxFinances.appendChild(item);

					cmtTotOutStanding = cmtTotOutStanding
							.add(CalculationUtil.getConvertedAmount(financeSummary.getFinCcy(),
									AccountConstants.CURRENCY_USD, financeSummary.getTotalOutStanding()));

					if (financeSummary.getFinStartDate().before(cmtOldestStartDt)) {
						cmtOldestStartDt = financeSummary.getFinStartDate();
					}
				}
				Listcell lcCmtOldestStartDt = (Listcell) this.listBoxFinances.getFellowIfAny(keyOldStartDt);
				lcCmtOldestStartDt.setLabel(formatdDate(cmtOldestStartDt));
				Listcell lccmtTotOutStanding = (Listcell) this.listBoxFinances.getFellowIfAny(keyCmtTotOutStd);
				lccmtTotOutStanding.setLabel(formatdAmount(cmtTotOutStanding, AccountConstants.CURRENCY_USD_FORMATTER));

				BigDecimal outStandPerc = BigDecimal.ZERO;
				if (cmtTotOutStanding.compareTo(new BigDecimal(0)) != 0
						&& finsum.getCmtAmount().compareTo(new BigDecimal(0)) != 0) {
					outStandPerc = cmtTotOutStanding.divide(finsum.getCmtAmount(), 2, RoundingMode.HALF_DOWN)
							.multiply(new BigDecimal(100));
				}
				Listcell lcCmtOutStdPerc = (Listcell) this.listBoxFinances.getFellowIfAny(keyOutStdPerc);
				lcCmtOutStdPerc.setLabel(String.valueOf(outStandPerc));
			}

		}

	}

	private void doFillUnCommitedGroup(Listitem item, Listcell cell, Listgroup group,
			List<FinanceSummary> nonCommitmentList, BigDecimal unCmtTotOutstanding) {

		if (!nonCommitmentList.isEmpty()) {
			Date unCmtfinalDate = nonCommitmentList.get(0).getMaturityDate();
			group = new Listgroup();
			cell = new Listcell("UnCommited Facilities");
			cell.setParent(group);
			cell = new Listcell();
			cell.setParent(group);
			cell = new Listcell();
			cell.setParent(group);
			cell = new Listcell();
			cell.setId("unCmtfinalDate");
			cell.setParent(group);
			cell = new Listcell(formatdAmount(BigDecimal.ZERO, AccountConstants.CURRENCY_USD_FORMATTER));
			cell.setStyle("text-align:right;");
			cell.setParent(group);
			cell = new Listcell(formatdAmount(BigDecimal.ZERO, AccountConstants.CURRENCY_USD_FORMATTER));
			cell.setStyle("text-align:right;");
			cell.setParent(group);
			cell = new Listcell(formatdAmount(unCmtTotOutstanding, AccountConstants.CURRENCY_USD_FORMATTER));
			cell.setStyle("text-align:right;");
			cell.setParent(group);
			cell = new Listcell();
			cell.setParent(group);
			cell = new Listcell();
			cell.setParent(group);
			this.listBoxFinances.appendChild(group);
			for (FinanceSummary financeSummary : nonCommitmentList) {
				item = new Listitem();
				cell = new Listcell(financeSummary.getFinType());
				cell.setParent(item);
				cell = new Listcell(financeSummary.getFinReference());
				cell.setParent(item);
				cell = new Listcell(formatdDate(financeSummary.getFinStartDate()));
				cell.setParent(item);
				cell = new Listcell(formatdDate(financeSummary.getMaturityDate()));
				cell.setParent(item);
				cell = new Listcell(CalculationUtil.getConvertedAmountASString(financeSummary.getFinCcy(),
						AccountConstants.CURRENCY_USD, financeSummary.getTotalOriginal()));
				cell.setStyle("text-align:right;");
				cell.setParent(item);
				cell = new Listcell(CalculationUtil.getConvertedAmountASString(financeSummary.getFinCcy(),
						AccountConstants.CURRENCY_USD, financeSummary.getTotalPaid()));
				cell.setStyle("text-align:right;");
				cell.setParent(item);
				cell = new Listcell(CalculationUtil.getConvertedAmountASString(financeSummary.getFinCcy(),
						AccountConstants.CURRENCY_USD, financeSummary.getTotalOutStanding()));
				cell.setStyle("text-align:right;");
				cell.setParent(item);
				cell = new Listcell();
				cell.setParent(item);
				cell = new Listcell(financeSummary.getFinStatus());
				cell.setParent(item);
				if (StringUtils.isBlank(financeSummary.getFinCommitmentRef())) {
					if (financeSummary.getMaturityDate().after(unCmtfinalDate)) {
						unCmtfinalDate = financeSummary.getMaturityDate();
					}
				}
				this.listBoxFinances.appendChild(item);
			}
			Listcell listcell = (Listcell) this.listBoxFinances.getFellowIfAny("unCmtfinalDate");
			listcell.setLabel(formatdDate(unCmtfinalDate));
		}
	}

	private String[] getFacilityNewFilters() {
		logger.debug("Entering");
		JdbcSearchObject<FacilityDetail> jdbcSearchObject = new JdbcSearchObject<FacilityDetail>(FacilityDetail.class);
		jdbcSearchObject.addTabelName("FacilityDetails_View");
		jdbcSearchObject.addFilterEqual("CustID", getFacility().getCustID());
		List<FacilityDetail> facilities = getPagedListWrapper().getPagedListService()
				.getBySearchObject(jdbcSearchObject);
		List<String> strings = new ArrayList<String>();
		if (facilities != null && !facilities.isEmpty()) {
			for (FacilityDetail facilityDetail : facilities) {
				if (StringUtils.isNotBlank(facilityDetail.getTermSheetRef())) {
					strings.add(facilityDetail.getTermSheetRef());
				}
			}
		}
		if (getFacilityDetailList() != null && !getFacilityDetailList().isEmpty()) {
			for (FacilityDetail facilityDetail : getFacilityDetailList()) {
				if (StringUtils.isNotBlank(facilityDetail.getTermSheetRef())
						&& !strings.contains(facilityDetail.getTermSheetRef())) {
					strings.add(facilityDetail.getTermSheetRef());
				}
			}
		}
		logger.debug("Leaving");
		return strings.toArray(new String[strings.size()]);
	}

	private String formatdDate(Date date) {
		return DateUtil.formatToLongDate(date);
	}

	private String formatdAmount(BigDecimal amount, int finFormatter) {
		return PennantApplicationUtil.amountFormate(amount, finFormatter);
	}

	public void setCtrlObject(Object ctrlObject) {
		this.ctrlObject = ctrlObject;
	}

	public Object getCtrlObject() {
		return ctrlObject;
	}

	public void setFacility(Facility facility) {
		this.facility = facility;
	}

	public Facility getFacility() {
		return facility;
	}

	public void setFacilityDetailList(List<FacilityDetail> facilityDetailList) {
		this.facilityDetailList = facilityDetailList;
	}

	public List<FacilityDetail> getFacilityDetailList() {
		return facilityDetailList;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public FacilityDialogCtrl getFacilityDialogCtrl() {
		if (this.ctrlObject instanceof FacilityDialogCtrl) {
			return (FacilityDialogCtrl) ctrlObject;
		}
		return null;
	}

	public void setFacilityDialogCtrl(FacilityDialogCtrl facilityDialogCtrl) {

	}

	private String getBookingUnit() {
		if (getFacility().getFacilityType().equals(FacilityConstants.FACILITY_COMMERCIAL)) {
			return FacilityConstants.FACILITY_BOOKING_COMM_UNIT;
		} else if (getFacility().getFacilityType().equals(FacilityConstants.FACILITY_CORPORATE)) {
			return FacilityConstants.FACILITY_BOOKING_CORP_UNIT;
		}
		return "";
	}

	@SuppressWarnings("unused")
	private String getTenor(Date startDate, Date maturityDate) {
		int months = DateUtil.getMonthsBetween(startDate, maturityDate);
		int years = months / 12;
		int remaining = months % 12;
		return getTenorDesc(years, remaining);
	}

	private String getTenorDesc(int years, int months) {
		String tenor = "";
		if (getFacility().getFacilityType().equals(FacilityConstants.FACILITY_COMMERCIAL)) {
			tenor = String.valueOf(new BigDecimal(years + "." + months));
		} else if (getFacility().getFacilityType().equals(FacilityConstants.FACILITY_CORPORATE)) {
			if (years > 0 && months > 0) {
				tenor = years + " Years " + months + " Months";
			} else if (years > 0) {
				tenor = years + " Years";
			} else if (months > 0) {
				tenor = months + " Months";
			}

		}
		return tenor;
	}

}