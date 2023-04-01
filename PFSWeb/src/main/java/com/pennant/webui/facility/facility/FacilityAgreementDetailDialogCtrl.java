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
 * * FileName : FinanceMainDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.facility.facility;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Html;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.aspose.words.SaveFormat;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.collateral.Collateral;
import com.pennant.backend.model.collateral.FacilityDetail;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.model.facility.Facility;
import com.pennant.backend.model.finance.FacilityAgreementDetail;
import com.pennant.backend.model.finance.FacilityAgreementDetail.CheckListAnsDetails;
import com.pennant.backend.model.finance.FacilityAgreementDetail.CheckListDetails;
import com.pennant.backend.model.finance.FacilityAgreementDetail.CustomerCreditReview;
import com.pennant.backend.model.finance.FacilityAgreementDetail.CustomerCreditReviewDetails;
import com.pennant.backend.model.finance.FacilityAgreementDetail.ExceptionList;
import com.pennant.backend.model.finance.FacilityAgreementDetail.FacilityCollateral;
import com.pennant.backend.model.finance.FacilityAgreementDetail.GroupRecommendation;
import com.pennant.backend.model.finance.FacilityAgreementDetail.ProposedFacility;
import com.pennant.backend.model.finance.FacilityAgreementDetail.Recommendation;
import com.pennant.backend.model.finance.FacilityAgreementDetail.ScoringDetails;
import com.pennant.backend.model.finance.FacilityAgreementDetail.ScoringHeader;
import com.pennant.backend.model.finance.FacilityAgreementDetail.Shareholder;
import com.pennant.backend.model.finance.FinAgreementDetail;
import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;
import com.pennant.backend.model.lmtmasters.FacilityReferenceDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.service.NotesService;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;
import com.pennant.backend.util.FacilityConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.coreinterface.model.CustomerCollateral;
import com.pennant.util.AgreementEngine;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/FinanceMainDialog.zul file.
 */
public class FacilityAgreementDetailDialogCtrl extends GFCBaseCtrl<FinAgreementDetail> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(FacilityAgreementDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_AgreementDetailDialog; // autoWired
	// Agreements Details Tab
	protected Listbox listBox_Agreements; // autoWired
	// Main Tab Details
	private Facility facility = null;
	private Object ctrlObject = null;
	private PagedListService pagedListService;
	private NotesService notesService;
	private CreditApplicationReviewService creditApplicationReviewService;
	long custid = 0;
	String jointcustCif = "";
	String userRole = "";
	private Set<String> bankDisplayfiled;
	private Set<String> corpDisplayfiled;
	private static final String RISKRATING_AIB = "AIB";
	private static final String RISKRATING_CI = "CI";
	private static final String RISKRATING_FITCH = "FITCH";
	private static final String RISKRATING_MOODY = "MOODY";
	private static final String RISKRATING_SNP = "S&P";
	private String AGGREEMENT_CODE_BANKCAF = "BANKCAF";

	public Set<String> getBankDisplayfiled() {
		if (bankDisplayfiled == null) {
			bankDisplayfiled = new HashSet<String>();
			bankDisplayfiled.add("TOTCURASST");
			bankDisplayfiled.add("TOTASST");
			bankDisplayfiled.add("TOTCURLIAB");
			bankDisplayfiled.add("TOTLTDEBT2");
			bankDisplayfiled.add("TOTLIAB");
			bankDisplayfiled.add("RTNOAVGEQUITY");
			bankDisplayfiled.add("CURRATIO2");
			bankDisplayfiled.add("RTNOAVGASS");
			bankDisplayfiled.add("RTNOAVGEQUITY");
			bankDisplayfiled.add("LEVERAGERTO");
		}
		return bankDisplayfiled;
	}

	public Set<String> getCorpDisplayfiled() {
		if (corpDisplayfiled == null) {
			corpDisplayfiled = new HashSet<String>();
			corpDisplayfiled.add("TOTCURAST");
			corpDisplayfiled.add("TOTAST");
			corpDisplayfiled.add("TOTCURLBL");
			corpDisplayfiled.add("TOTLTDEBT");
			corpDisplayfiled.add("TOTLBL");
			corpDisplayfiled.add("TOTEQV");
			corpDisplayfiled.add("GROSSPFT");
			corpDisplayfiled.add("CURRATIO");
			corpDisplayfiled.add("RTNAVGEQT");
			corpDisplayfiled.add("RTNAVGAST");
			corpDisplayfiled.add("LVGRAT");
			corpDisplayfiled.add("EBITDA4");
		}
		return corpDisplayfiled;
	}

	/**
	 * default constructor.<br>
	 */
	public FacilityAgreementDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_AgreementDetailDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_AgreementDetailDialog);

		if (arguments.containsKey("facility")) {
			this.facility = (Facility) arguments.get("facility");
		}
		if (arguments.containsKey("control")) {
			setCtrlObject(arguments.get("control"));
		}
		if (arguments.containsKey("userRole")) {
			userRole = (String) arguments.get("userRole");
		}
		doShowDialog();
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void doShowDialog() {
		logger.debug(Literal.ENTERING);
		doFillListbox(getFacility().getAggrementList());
		getBorderLayoutHeight();
		this.listBox_Agreements.setHeight(this.borderLayoutHeight - 80 - 35 + "px");// 210px
		this.window_AgreementDetailDialog.setHeight(this.borderLayoutHeight - 80 + "px");
		try {
			getCtrlObject().getClass().getMethod("setFacilityAgreementDetailDialogCtrl", this.getClass())
					.invoke(getCtrlObject(), this);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void doFillListbox(List<FacilityReferenceDetail> aggrementList) {
		logger.debug(Literal.ENTERING);
		this.listBox_Agreements.getItems().clear();
		if (aggrementList != null && !aggrementList.isEmpty()) {
			for (FacilityReferenceDetail financeReferenceDetail : aggrementList) {
				if (isAllowedToShow(financeReferenceDetail, userRole)) {
					if (AGGREEMENT_CODE_BANKCAF.equals(financeReferenceDetail.getLovDescCodelov())
							&& !getFacility().getCustCtgCode().equals(PennantConstants.PFF_CUSTCTG_SME)) {
						continue;
					}
					doFillAgreementsList(this.listBox_Agreements, financeReferenceDetail);
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public boolean isAllowedToShow(FacilityReferenceDetail financeReferenceDetail, String userRole) {
		logger.debug(Literal.ENTERING);
		String showinStage = StringUtils.trimToEmpty(financeReferenceDetail.getShowInStage());
		if (showinStage.contains(",")) {
			String[] roles = showinStage.split(",");
			for (String string : roles) {
				if (userRole.equals(string)) {
					logger.debug(Literal.LEAVING);
					return true;
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return false;
	}

	/**
	 * Method to fill Agreements tab.
	 * 
	 * @param listbox
	 * @param financeReferenceDetail
	 * @param userRole
	 */
	private void doFillAgreementsList(Listbox listbox, FacilityReferenceDetail financeReferenceDetail) {
		logger.debug(Literal.ENTERING);

		Listitem item = new Listitem(); // To Create List item
		Listcell listCell;
		listCell = new Listcell();
		listCell.setLabel(financeReferenceDetail.getLovDescNamelov());
		listCell.setParent(item);
		listCell = new Listcell();
		Html ageementLink = new Html();
		ageementLink.setContent("<a href='javascript:;' style = 'font-weight:bold'>"
				+ financeReferenceDetail.getLovDescAggReportName() + "</a> ");
		listCell.appendChild(ageementLink);
		listCell.setParent(item);
		listbox.appendChild(item);
		item.setAttribute("data", financeReferenceDetail);
		ageementLink.addForward("onClick", window_AgreementDetailDialog, "onGenerateReportClicked",
				financeReferenceDetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Generating Template replaced to Finance Details
	 * 
	 * @param event
	 */
	public void onGenerateReportClicked(ForwardEvent event) {
		logger.debug(Literal.ENTERING + event.toString());
		FacilityReferenceDetail data = (FacilityReferenceDetail) event.getData();

		try {
			Object object = getCtrlObject().getClass().getMethod("getAgrFacilitty").invoke(ctrlObject);
			if (object != null) {
				setFacility((Facility) object);
			}

			if (getFacility() != null) {
				custid = getFacility().getCustID();
				AgreementEngine engine = new AgreementEngine();
				engine.setTemplate(data.getLovDescAggReportName());
				engine.loadTemplate();
				String aggName = StringUtils.trimToEmpty(data.getLovDescNamelov());
				String reportName = "";
				engine.mergeFields(getAggrementData(getFacility(), data.getLovDescAggImage()));
				if (StringUtils.equals(data.getAggType(), PennantConstants.DOC_TYPE_PDF)) {
					reportName = (getFacility().getCAFReference().replace("/", "")) + "_" + aggName
							+ PennantConstants.DOC_TYPE_PDF_EXT;
					byte[] docData = engine.getDocumentInByteArray(SaveFormat.PDF);
					showDocument(docData, this.window_AgreementDetailDialog, reportName, SaveFormat.DOCX);
				} else {
					reportName = (getFacility().getCAFReference().replace("/", "")) + "_" + "_" + aggName
							+ PennantConstants.DOC_TYPE_WORD_EXT;
					byte[] docData = engine.getDocumentInByteArray(SaveFormat.PDF);
					showDocument(docData, this.window_AgreementDetailDialog, reportName, SaveFormat.PDF);
				}
				engine.close();
				engine = null;
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	public Facility getFacility() {
		return facility;
	}

	public void setFacility(Facility facility) {
		this.facility = facility;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public void setCtrlObject(Object object) {
		this.ctrlObject = object;
	}

	public Object getCtrlObject() {
		return ctrlObject;
	}

	public CreditApplicationReviewService getCreditApplicationReviewService() {
		return creditApplicationReviewService;
	}

	public void setCreditApplicationReviewService(CreditApplicationReviewService creditApplicationReviewService) {
		this.creditApplicationReviewService = creditApplicationReviewService;
	}

	public void setNotesService(NotesService notesService) {
		this.notesService = notesService;
	}

	public NotesService getNotesService() {
		return notesService;
	}

	// =============== Agreement Generation============
	/**
	 * To prepare Agreement Data
	 * 
	 * @param detail
	 * @return
	 */
	private FacilityAgreementDetail getAggrementData(Facility detail, String aggModuleDetails) {
		logger.debug(Literal.ENTERING);
		// Create New Object For The Agreement Detail
		FacilityAgreementDetail agreement = new FacilityAgreementDetail();
		try {
			if (custid != 0) {
				// ------------------ Basic details
				if (aggModuleDetails.contains(PennantConstants.AGG_BASICDE)) {
					agreement = setBasicDetails(detail, agreement);
				}
				// -----------------Customer Credit Review Details
				String custCtg = getFacility().getCustCtgCode();
				if (aggModuleDetails.contains(PennantConstants.AGG_CRDTRVW)) {
					agreement = setCreditReviewDetails(agreement, custCtg);
				}
				// -----------------Scoring Detail
				if (aggModuleDetails.contains(PennantConstants.AGG_SCOREDE)) {
					if (custCtg.equals(PennantConstants.PFF_CUSTCTG_CORP)
							|| custCtg.equals(PennantConstants.PFF_CUSTCTG_SME)) {
						agreement = setScoringDetails(agreement, detail);
					}
				}
				// -----------------Check List Details
				if (aggModuleDetails.contains(PennantConstants.AGG_CHKLSTD)) {
					if (detail != null) {
						agreement = setCheckListDetails(agreement, detail);
					}
				}
				// -------------------Recommendations
				if (aggModuleDetails.contains(PennantConstants.AGG_RECOMMD)) {
					if (detail != null) {
						agreement = setRecommendations(agreement, detail.getCAFReference());
					}
				}
				// -------------------Exception List
				if (aggModuleDetails.contains(PennantConstants.AGG_EXCEPTN)) {
					agreement = setExceptions(agreement, detail);
				}
				//
				agreement = setFacilityDetails(agreement, detail);
				// Collateral
				agreement = setCollateralDetails(agreement, detail);
				// Share Holder
				agreement = setShareHolderDetails(agreement, detail);
				// Risk Rating
				agreement = setRiskRating(agreement, detail);
				logger.debug(Literal.LEAVING);
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug(Literal.LEAVING);
		return agreement;
	}

	private FacilityAgreementDetail setRiskRating(FacilityAgreementDetail agreement, Facility detail) {
		try {
			List<CustomerRating> list = detail.getCustomerRatings();
			if (list != null && !list.isEmpty()) {
				for (CustomerRating customerRating : list) {
					String rating = StringUtils.trimToEmpty(customerRating.getCustRatingType());
					if (RISKRATING_AIB.equals(rating)) {
						agreement.setAIBCountry(customerRating.getCustRatingCode());
						agreement.setAIBCountryDesc(
								StringUtils.trimToEmpty(customerRating.getLovDesccustRatingCodeDesc()));
						agreement.setAIBObligator(customerRating.getCustRating());
						agreement.setAIBObligatorDesc(
								StringUtils.trimToEmpty(customerRating.getLovDescCustRatingName()));
					} else if (RISKRATING_CI.equals(rating)) {
						agreement.setCICountry(StringUtils.trimToEmpty(customerRating.getLovDesccustRatingCodeDesc()));
						agreement.setCIObligator(StringUtils.trimToEmpty(customerRating.getLovDescCustRatingName()));
					} else if (RISKRATING_FITCH.equals(rating)) {
						agreement.setFITCHCountry(
								StringUtils.trimToEmpty(customerRating.getLovDesccustRatingCodeDesc()));
						agreement.setFITCHObligator(StringUtils.trimToEmpty(customerRating.getLovDescCustRatingName()));
					} else if (RISKRATING_MOODY.equals(rating)) {
						agreement.setMOODYCountry(
								StringUtils.trimToEmpty(customerRating.getLovDesccustRatingCodeDesc()));
						agreement.setMOODYObligator(StringUtils.trimToEmpty(customerRating.getLovDescCustRatingName()));
					} else if (RISKRATING_SNP.equals(rating)) {
						agreement.setSNPCountry(StringUtils.trimToEmpty(customerRating.getLovDesccustRatingCodeDesc()));
						agreement.setSNPObligator(StringUtils.trimToEmpty(customerRating.getLovDescCustRatingName()));
					}
				}
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		return agreement;
	}

	private FacilityAgreementDetail setShareHolderDetails(FacilityAgreementDetail agreement, Facility detail) {
		try {
			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
			JdbcSearchObject<DirectorDetail> jdbcSearchObject = new JdbcSearchObject<DirectorDetail>(
					DirectorDetail.class);
			jdbcSearchObject.addTabelName("CustomerDirectorDetail_AView");
			jdbcSearchObject.addFilterEqual("CustID", detail.getCustID());
			List<DirectorDetail> directorList = pagedListService.getBySearchObject(jdbcSearchObject);
			BigDecimal totSharePerc = BigDecimal.ZERO;
			agreement.setShareholders(new ArrayList<FacilityAgreementDetail.Shareholder>());
			if (directorList != null && !directorList.isEmpty()) {
				for (DirectorDetail directorDetail : directorList) {
					Shareholder shareholder = agreement.new Shareholder();
					String name = "";
					if (StringUtils.isNotBlank(directorDetail.getShortName())) {
						name = directorDetail.getShortName();
					} else {
						name = directorDetail.getFirstName() + "  " + directorDetail.getLastName();
					}
					shareholder.setShareholderName(name);
					shareholder.setShareholderPercentage(String.valueOf(directorDetail.getSharePerc()));
					totSharePerc = totSharePerc.add(directorDetail.getSharePerc());
					agreement.getShareholders().add(shareholder);
				}
			}
			agreement.setTotSharePerc(String.valueOf(totSharePerc));
			if (agreement.getShareholders().isEmpty()) {
				agreement.getShareholders().add(agreement.new Shareholder());
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		return agreement;
	}

	@SuppressWarnings("unchecked")
	private FacilityAgreementDetail setCollateralDetails(FacilityAgreementDetail agreement, Facility detail) {
		agreement.setCollaterals(new ArrayList<FacilityAgreementDetail.FacilityCollateral>());
		try {
			BigDecimal totValue = BigDecimal.ZERO;
			BigDecimal totCover = BigDecimal.ZERO;
			try {
				List<CustomerCollateral> collateralsFromEquation = null;
				Object object = getCtrlObject().getClass().getMethod("getCollateralsFromEquation").invoke(ctrlObject);
				if (object != null) {
					collateralsFromEquation = (List<CustomerCollateral>) object;
				}
				if (collateralsFromEquation != null && !collateralsFromEquation.isEmpty()) {
					for (CustomerCollateral collateral : collateralsFromEquation) {
						FacilityCollateral coll = agreement.new FacilityCollateral();
						coll.setSecurityType(collateral.getCollTypeDesc());
						coll.setMarketValue(PennantApplicationUtil.amountFormate(
								new BigDecimal(collateral.getCollValue().toString()),
								SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT)));
						coll.setBankValue(PennantApplicationUtil.amountFormate(
								new BigDecimal(collateral.getCollBankVal().toString()),
								SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT)));
						coll.setMargin(String.valueOf(collateral.getCollBankValMar()));
						coll.setCover(String.valueOf(collateral.getCollBankValMar()));
						agreement.getCollaterals().add(coll);
						totValue = totValue.add(new BigDecimal(collateral.getCollValue().toString()));
					}
				}
			} catch (Exception e) {
				logger.debug(e);
			}
			if (detail.getCollaterals() != null && !detail.getCollaterals().isEmpty()) {
				for (Collateral collateral : detail.getCollaterals()) {
					FacilityCollateral coll = agreement.new FacilityCollateral();
					coll.setSecurityType(collateral.getDescription());
					BigDecimal marketValue = CalculationUtil.getConvertedAmount(collateral.getCurrency(),
							SysParamUtil.getAppCurrency(), collateral.getValue());
					BigDecimal bankValue = CalculationUtil.getConvertedAmount(collateral.getCurrency(),
							SysParamUtil.getAppCurrency(), collateral.getBankvaluation());
					coll.setMarketValue(PennantApplicationUtil.amountFormate(marketValue,
							SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT)));
					coll.setBankValue(PennantApplicationUtil.amountFormate(bankValue,
							SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT)));
					coll.setMargin(String.valueOf(collateral.getBankmargin()));
					coll.setCover(String.valueOf(collateral.getActualCoverage()));
					agreement.getCollaterals().add(coll);
					totValue = totValue.add(marketValue);
					totCover = totCover.add(collateral.getActualCoverage());
				}
			}
			agreement.setTotValue(PennantApplicationUtil.amountFormate(totValue,
					SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT)));
			agreement.setTotCover(String.valueOf(totCover));
			if (agreement.getCollaterals().isEmpty()) {
				agreement.getCollaterals().add(agreement.new FacilityCollateral());
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		return agreement;
	}

	private FacilityAgreementDetail setFacilityDetails(FacilityAgreementDetail agreement, Facility detail) {
		try {
			Date finalMaturity = null;
			BigDecimal maturity = BigDecimal.ZERO;
			BigDecimal amountBD = BigDecimal.ZERO;
			BigDecimal amountUSD = BigDecimal.ZERO;
			BigDecimal totExposure = BigDecimal.ZERO;
			BigDecimal totExsisting = BigDecimal.ZERO;

			agreement.setProposedFacilities(new ArrayList<FacilityAgreementDetail.ProposedFacility>());

			for (FacilityDetail facilityDetail : detail.getFacilityDetails()) {

				ProposedFacility proposedFacility = agreement.new ProposedFacility();

				proposedFacility = setFacilityDetailsData(proposedFacility, facilityDetail, detail);

				amountBD = amountBD.add(CalculationUtil.getConvertedAmount(facilityDetail.getFacilityCCY(),
						SysParamUtil.getAppCurrency(), facilityDetail.getNewLimit()));
				amountUSD = amountUSD.add(CalculationUtil.getConvertedAmount(facilityDetail.getFacilityCCY(),
						AccountConstants.CURRENCY_USD, facilityDetail.getNewLimit()));
				totExposure = totExposure.add(CalculationUtil.getConvertedAmount(facilityDetail.getFacilityCCY(),
						AccountConstants.CURRENCY_USD, facilityDetail.getExposure()));
				totExsisting = totExsisting.add(CalculationUtil.getConvertedAmount(facilityDetail.getFacilityCCY(),
						AccountConstants.CURRENCY_USD, facilityDetail.getExistingLimit()));

				int years = facilityDetail.getTenorYear();
				int months = facilityDetail.getTenorMonth();

				if (new BigDecimal(years + "." + months).compareTo(maturity) > 0) {
					maturity = new BigDecimal(years + "." + months);
				}
				agreement.getProposedFacilities().add(proposedFacility);

				if (finalMaturity == null) {
					finalMaturity = facilityDetail.getMaturityDate();
				}
				if (finalMaturity != null) {
					if (finalMaturity.compareTo(facilityDetail.getMaturityDate()) > 0) {
						finalMaturity = facilityDetail.getMaturityDate();
					}
				}
				setFacilityFor(agreement, facilityDetail);
			}
			agreement.setTotalTenor(String.valueOf(maturity));
			if (finalMaturity != null) {
				agreement.setFinalMaturityDate(DateUtil.formatToLongDate(finalMaturity));
			}
			agreement.setTotFacilityAmt(CurrencyUtil.format(amountBD, 3));
			agreement.setTotFacilityAmtinUSD(CurrencyUtil.format(amountUSD, 2));
			agreement.setTotExposure(CurrencyUtil.format(totExposure, 2));
			agreement.setTotExsisting(CurrencyUtil.format(totExsisting, 2));
			if (agreement.getProposedFacilities().isEmpty()) {
				agreement.getProposedFacilities().add(agreement.new ProposedFacility());
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		return agreement;
	}

	public ProposedFacility setFacilityDetailsData(ProposedFacility proposedFacility, FacilityDetail facilityDetail,
			Facility detail) {

		// == NONC
		proposedFacility.setDate(DateUtil.formatToLongDate(detail.getStartDate()));
		proposedFacility.setCafRef(detail.getCAFReference());
		proposedFacility.setCustomerType(detail.getCustTypeDesc());
		proposedFacility.setCustNumber(detail.getCustCIF());
		proposedFacility.setCustName(detail.getCustShrtName());
		if (detail.getCustDOB() != null) {
			proposedFacility.setRelationshipSince(DateUtil.formatToLongDate(detail.getCustDOB()));
		} else {
			proposedFacility.setRelationshipSince("NEW");
		}

		proposedFacility.setNatureOfBusinessCode(detail.getNatureOfBusiness());
		proposedFacility.setNatureOfBusiness(detail.getNatureOfBusinessName());
		proposedFacility.setNextReviewDate(DateUtil.formatToLongDate(detail.getNextReviewDate()));
		proposedFacility.setCountryOfRisk(detail.getCountryOfRiskName());
		BigDecimal totScore = BigDecimal.ZERO;
		try {
			if (detail.getScoreDetailListMap().containsKey(detail.getFinScoreHeaderList().get(0).getHeaderId())) {
				List<FinanceScoreDetail> scoreDetailList = detail.getScoreDetailListMap()
						.get(detail.getFinScoreHeaderList().get(0).getHeaderId());
				for (FinanceScoreDetail curScoreDetail : scoreDetailList) {
					totScore = totScore.add(curScoreDetail.getExecScore());
				}
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		proposedFacility.setTotalScoring(String.valueOf(totScore));
		// == NONC

		proposedFacility.setBookingUnit(getBookingUnit(detail));
		StringBuilder content = new StringBuilder();
		content.append("Type:");
		content.append(facilityDetail.getFacilityTypeDesc());
		content.append("\n");
		content.append("Purpose:");
		content.append(facilityDetail.getPurpose());
		content.append("\n");
		content.append("Repayments:");
		content.append(facilityDetail.getRepayments());
		if (StringUtils.isNotBlank(facilityDetail.getLCPeriod())) {
			content.append("\n");
			content.append("L/C Period:");
			content.append(facilityDetail.getLCPeriod());
		}
		if (StringUtils.isNotBlank(facilityDetail.getUsancePeriod())) {
			content.append("\n");
			content.append("Usance Period:");
			content.append(facilityDetail.getUsancePeriod());
		}
		content.append("\n");
		content.append("Security:");
		content.append(facilityDetail.getSecurityDesc());
		proposedFacility.setFacilityDesc(content.toString());

		proposedFacility.setFacilityPurpose(facilityDetail.getPurpose());
		proposedFacility.setFacilityType(facilityDetail.getFacilityTypeDesc());
		proposedFacility.setProfitRate(facilityDetail.getPricing());
		proposedFacility.setRepaymentSchedule(facilityDetail.getRepayments());
		proposedFacility.setDocumentsRequired(facilityDetail.getDocumentsRequired());

		if (facilityDetail.isSecurityClean()) {
			proposedFacility.setSecurityDescription("Clean");
		} else {
			proposedFacility.setSecurityDescription(facilityDetail.getSecurityDesc());
		}
		proposedFacility.setCommission(facilityDetail.getCommission());
		proposedFacility.setGuarantee(facilityDetail.getGuarantee());
		proposedFacility.setCovenants(facilityDetail.getCovenants());
		proposedFacility.setPricing(facilityDetail.getPricing());
		if (StringUtils.trimToEmpty(facilityDetail.getRevolving()).equalsIgnoreCase(PennantConstants.YES)) {
			proposedFacility.setRevolving("Yes");
		} else if (StringUtils.trimToEmpty(facilityDetail.getRevolving()).equalsIgnoreCase(PennantConstants.NO)) {
			proposedFacility.setRevolving("No");
		}

		proposedFacility.setTransactionType(PennantStaticListUtil.getlabelDesc(facilityDetail.getTransactionType(),
				PennantStaticListUtil.getTransactionTypesList()));
		proposedFacility.setAgentBank(facilityDetail.getAgentBank());

		proposedFacility.setTotalFacilityCcy(facilityDetail.getTotalFacilityCcy());
		proposedFacility.setUnderWritingCcy(facilityDetail.getUnderWritingCcy());
		proposedFacility.setPropFinalTakeCcy(facilityDetail.getPropFinalTakeCcy());

		if (facilityDetail.getTotalFacility() != null
				&& facilityDetail.getTotalFacility().compareTo(BigDecimal.ZERO) != 0) {
			proposedFacility.setTotalFacilityAmount(CurrencyUtil.format(facilityDetail.getTotalFacility(),
					CurrencyUtil.getFormat(facilityDetail.getTotalFacilityCcy())));
			proposedFacility.setTotalFacilityAmountUSD(
					CalculationUtil.getConvertedAmountASString(facilityDetail.getTotalFacilityCcy(),
							AccountConstants.CURRENCY_USD, facilityDetail.getTotalFacility()));
		}

		if (facilityDetail.getUnderWriting() != null
				&& facilityDetail.getUnderWriting().compareTo(BigDecimal.ZERO) != 0) {
			proposedFacility.setUnderWritingAmount(CurrencyUtil.format(facilityDetail.getUnderWriting(),
					CurrencyUtil.getFormat(facilityDetail.getUnderWritingCcy())));
			proposedFacility.setUnderWritingAmountUSD(
					CalculationUtil.getConvertedAmountASString(facilityDetail.getUnderWritingCcy(),
							AccountConstants.CURRENCY_USD, facilityDetail.getUnderWriting()));
		}

		if (facilityDetail.getPropFinalTake() != null
				&& facilityDetail.getPropFinalTake().compareTo(BigDecimal.ZERO) != 0) {
			proposedFacility.setPropFinalTakeAmount(CurrencyUtil.format(facilityDetail.getPropFinalTake(),
					CurrencyUtil.getFormat(facilityDetail.getPropFinalTakeCcy())));
			proposedFacility.setPropFinalTakeAmountUSD(
					CalculationUtil.getConvertedAmountASString(facilityDetail.getPropFinalTakeCcy(),
							AccountConstants.CURRENCY_USD, facilityDetail.getPropFinalTake()));
		}

		proposedFacility.setExposure(CalculationUtil.getConvertedAmountASString(facilityDetail.getFacilityCCY(),
				AccountConstants.CURRENCY_USD, facilityDetail.getExposure()));
		proposedFacility.setLimitExisting(CalculationUtil.getConvertedAmountASString(facilityDetail.getFacilityCCY(),
				AccountConstants.CURRENCY_USD, facilityDetail.getExistingLimit()));
		proposedFacility.setLimitNew(CalculationUtil.getConvertedAmountASString(facilityDetail.getFacilityCCY(),
				AccountConstants.CURRENCY_USD, facilityDetail.getNewLimit()));

		int years = facilityDetail.getTenorYear();
		int months = facilityDetail.getTenorMonth();

		if (detail.getFacilityType().equals(FacilityConstants.FACILITY_COMMERCIAL)) {
			proposedFacility.setTenor(String.valueOf(new BigDecimal(years + "." + months)));
		} else if (detail.getFacilityType().equals(FacilityConstants.FACILITY_CORPORATE)) {
			if (years > 0 && months > 0) {
				proposedFacility.setTenor(years + " Years " + months + " Months");
			} else if (years > 0) {
				proposedFacility.setTenor(years + " Years");
			} else if (months > 0) {
				proposedFacility.setTenor(months + " Months");
			}
		}
		return proposedFacility;
	}

	private void setFacilityFor(FacilityAgreementDetail agreement, FacilityDetail facilityDetail) {
		String facilityfor = facilityDetail.getFacilityFor();
		if (facilityfor.equals(FacilityConstants.FACILITY_NEW)) {
			agreement.setAvailmentNew("X");
		} else if (facilityfor.equals(FacilityConstants.FACILITY_REVIEW)) {
			agreement.setAnnualReview("X");
		} else if (facilityfor.equals(FacilityConstants.FACILITY_AMENDMENT)) {
			agreement.setAmendment("X");
		}
	}

	private String getBookingUnit(Facility detail) {
		if (detail.getFacilityType().equals(FacilityConstants.FACILITY_COMMERCIAL)) {
			return FacilityConstants.FACILITY_BOOKING_COMM_UNIT;
		} else if (detail.getFacilityType().equals(FacilityConstants.FACILITY_CORPORATE)) {
			return FacilityConstants.FACILITY_BOOKING_CORP_UNIT;
		}
		return "";
	}

	private FacilityAgreementDetail setBasicDetails(Facility detail, FacilityAgreementDetail agreement) {
		try {
			// Application Date
			Date appldate = SysParamUtil.getAppDate();
			String appDate = DateUtil.formatToLongDate(appldate);
			agreement.setApplicationDate(appDate);
			agreement.setDate(DateUtil.formatToLongDate(detail.getStartDate()));
			agreement.setCafRef(detail.getCAFReference());
			agreement.setCountryOfDomicile(detail.getCountryOfDomicileName());
			agreement.setCountryOfRisk(detail.getCountryOfRiskName());
			agreement.setCustNumber(detail.getCustCIF());
			agreement.setCustName(detail.getCustShrtName());
			agreement.setEstablishedDate(DateUtil.formatToLongDate(detail.getEstablishedDate()));
			agreement.setNatureOfBusinessCode(detail.getNatureOfBusiness());
			agreement.setNatureOfBusiness(detail.getNatureOfBusinessName());
			agreement.setSicCode(detail.getSICCodeName());
			agreement.setCustomerRiskType(detail.getCustomerRiskTypeName());
			agreement.setDeadline(DateUtil.formatToLongDate(detail.getDeadLine()));
			agreement.setNextReviewDate(DateUtil.formatToLongDate(detail.getNextReviewDate()));
			agreement.setRelationshipManager(detail.getRelationshipManager());
			agreement.setCountryManager(detail.getCountryManagerName());
			agreement.setLevelofApprovalRequired(PennantStaticListUtil.getlabelDesc(detail.getLevelOfApproval(),
					PennantStaticListUtil.getLevelOfApprovalList()));
			agreement.setCountryLimitsAdequacy(detail.getCountryLimitAdeq());
			agreement.setReviewCenter(detail.getReviewCenter());
			agreement.setCustGroupCode(detail.getCustGrpCodeName());
			agreement.setCustGroupName(detail.getCustomerGroupName());
			agreement.setRiskLimit(CurrencyUtil.format(detail.getCountryLimit(), 0));
			agreement.setRiskExposure(CurrencyUtil.format(detail.getCountryExposure(), 0));

			if (StringUtils.trimToEmpty(detail.getCustRelation()).equals(FacilityConstants.CUSTRELATION_CONNECTED)) {
				agreement.setConnectedCustomer("Yes");
			} else {
				agreement.setConnectedCustomer("No");
			}
			if (StringUtils.trimToEmpty(detail.getCustRelation()).equals(FacilityConstants.CUSTRELATION_RELATED)) {
				agreement.setRelatedCustomer("Yes");
			} else {
				agreement.setRelatedCustomer("No");
			}
			if (detail.getCustDOB() != null) {
				agreement.setRelationshipSince(DateUtil.formatToLongDate(detail.getCustDOB()));
			} else {
				agreement.setRelationshipSince("NEW");
			}
			agreement.setCustomerBackGround(detail.getCustomerBackGround());
			agreement.setStrength(detail.getStrength());
			agreement.setWeaknesses(detail.getWeaknesses());
			agreement.setSourceOfRepayment(detail.getSourceOfRepayment());
			agreement.setAdequacyOfCashFlows(detail.getAdequacyOfCashFlows());
			agreement.setTypesOfSecurities(detail.getTypesOfSecurities());
			agreement.setGuaranteeDescription(detail.getGuaranteeDescription());
			agreement.setFinancialSummary(detail.getFinancialSummary());
			agreement.setMitigants(detail.getMitigants());
			agreement.setInterim(DateUtil.formatToLongDate(detail.getInterim()));
			agreement.setAntiMoneyLaunderClear(detail.getAntiMoneyLaunderClear());
			agreement.setPurpose(detail.getPurpose());
			agreement.setAccountRelation(detail.getAccountRelation());
			agreement.setAntiMoneyLaunderSection(detail.getAntiMoneyLaunderSection());
			agreement.setLimitAndAncillary(detail.getLimitAndAncillary());
			agreement.setCustomerType(detail.getCustTypeDesc());
		} catch (Exception e) {
			logger.debug(e);
		}
		return agreement;
	}

	/**
	 * To get Scoring Detail List
	 * 
	 * @param agreementDetail
	 * @return
	 */
	private FacilityAgreementDetail setScoringDetails(FacilityAgreementDetail agreementDetail, Facility detail) {
		logger.debug(Literal.ENTERING);
		try {
			BigDecimal totScore = BigDecimal.ZERO;
			String creditWorth = "";
			List<ScoringHeader> finScoringHeaderList = new ArrayList<FacilityAgreementDetail.ScoringHeader>();
			List<ScoringHeader> nonFinScoringHeaderList = new ArrayList<FacilityAgreementDetail.ScoringHeader>();
			List<FinanceScoreHeader> finScoreHeaderList = detail.getFinScoreHeaderList();
			if (finScoreHeaderList != null && !finScoreHeaderList.isEmpty()) {
				creditWorth = detail.getFinScoreHeaderList().get(0).getCreditWorth();
				ScoringHeader header = null;
				long prvGrpId = 0;
				if (detail.getScoreDetailListMap().containsKey(detail.getFinScoreHeaderList().get(0).getHeaderId())) {
					List<FinanceScoreDetail> scoreDetailList = detail.getScoreDetailListMap()
							.get(detail.getFinScoreHeaderList().get(0).getHeaderId());
					for (FinanceScoreDetail curScoreDetail : scoreDetailList) {
						// Adding List Group
						if ((prvGrpId == 0) || (prvGrpId != curScoreDetail.getSubGroupId())) {
							header = agreementDetail.new ScoringHeader();
							header.setScoringGroup(curScoreDetail.getSubGrpCodeDesc());
							header.setScoringDetails(new ArrayList<FacilityAgreementDetail.ScoringDetails>());
							if ("F".equals(curScoreDetail.getCategoryType())) {
								finScoringHeaderList.add(header);
							} else {
								nonFinScoringHeaderList.add(header);
							}
						}
						totScore = totScore.add(curScoreDetail.getExecScore());
						ScoringDetails details = agreementDetail.new ScoringDetails();
						details.setScoringMetric(curScoreDetail.getRuleCode());
						details.setScoringDesc(curScoreDetail.getRuleCodeDesc());
						details.setMetricMaxScore(String.valueOf(curScoreDetail.getMaxScore()));
						details.setCalcScore(String.valueOf(curScoreDetail.getExecScore()));
						header.getScoringDetails().add(details);
						prvGrpId = curScoreDetail.getSubGroupId();
					}
				}
			}
			if (finScoringHeaderList.isEmpty()) {
				ScoringHeader header = agreementDetail.new ScoringHeader();
				header.setScoringDetails(new ArrayList<FacilityAgreementDetail.ScoringDetails>());
				header.getScoringDetails().add(agreementDetail.new ScoringDetails());
				finScoringHeaderList.add(header);
			}
			agreementDetail.setFinScoringHeaderDetails(finScoringHeaderList);
			if (StringUtils.isNotEmpty(creditWorth) && creditWorth.contains("-")) {
				String creditGradeWorth[] = creditWorth.split("-");
				if (creditGradeWorth.length == 2) {
					agreementDetail.setProposedGrade(creditGradeWorth[0]);
					agreementDetail.setProposedDesc(creditGradeWorth[1]);
				}
			}
			agreementDetail.setTotalScoring(String.valueOf(totScore));
			if (nonFinScoringHeaderList.isEmpty()) {
				ScoringHeader header = agreementDetail.new ScoringHeader();
				header.setScoringDetails(new ArrayList<FacilityAgreementDetail.ScoringDetails>());
				header.getScoringDetails().add(agreementDetail.new ScoringDetails());
				nonFinScoringHeaderList.add(header);
			}
			agreementDetail.setNonFinScoringHeaderDetails(nonFinScoringHeaderList);
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug(Literal.LEAVING);
		return agreementDetail;
	}

	/**
	 * To get the check List details
	 * 
	 * @param agreement
	 * @param detail
	 * @return
	 */
	private FacilityAgreementDetail setCheckListDetails(FacilityAgreementDetail agreement, Facility detail) {
		logger.debug(Literal.ENTERING);
		try {
			// Add the Check List Data To the Agreement object
			List<FacilityReferenceDetail> finRefDetailsList = detail.getFinRefDetailsList();
			if (finRefDetailsList != null && !finRefDetailsList.isEmpty()) {
				agreement.setCheckListDetails(new ArrayList<FacilityAgreementDetail.CheckListDetails>());
				for (FacilityReferenceDetail checkListReference : finRefDetailsList) {
					CheckListDetails checkListDetails = agreement.new CheckListDetails();
					checkListDetails.setQuestionId(checkListReference.getFinRefId());
					checkListDetails.setQuestion(checkListReference.getLovDescRefDesc());
					checkListDetails.setListquestionAns(new ArrayList<FacilityAgreementDetail.CheckListAnsDetails>());
					for (CheckListDetail checkListDetail : checkListReference.getLovDescCheckListAnsDetails()) {
						CheckListAnsDetails ansDetails = agreement.new CheckListAnsDetails();
						ansDetails.setQuestionId(checkListReference.getFinRefId());
						ansDetails.setQuestionAns(checkListDetail.getAnsDesc());
						if (detail.getFinanceCheckList() != null && !detail.getFinanceCheckList().isEmpty()) {
							for (FinanceCheckListReference financeCheckList : detail.getFinanceCheckList()) {
								if (financeCheckList.getQuestionId() == checkListReference.getFinRefId()
										&& financeCheckList.getAnswer() == checkListDetail.getAnsSeqNo()) {
									ansDetails.setQuestionRem("YES");
									break;
								} else {
									ansDetails.setQuestionRem("");
								}
							}
						}
						checkListDetails.getListquestionAns().add(ansDetails);
					}
					agreement.getCheckListDetails().add(checkListDetails);
				}
			}
			if (agreement.getCheckListDetails() == null) {
				agreement.setCheckListDetails(new ArrayList<FacilityAgreementDetail.CheckListDetails>());
				CheckListDetails checkListDetails = agreement.new CheckListDetails();
				checkListDetails.setListquestionAns(new ArrayList<FacilityAgreementDetail.CheckListAnsDetails>());
				checkListDetails.getListquestionAns().add(agreement.new CheckListAnsDetails());
				agreement.getCheckListDetails().add(checkListDetails);
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug(Literal.LEAVING);
		return agreement;
	}

	/**
	 * TO get Recommendation form notes table
	 * 
	 * @param agreement
	 * @param finreference
	 * @return
	 */
	private FacilityAgreementDetail setRecommendations(FacilityAgreementDetail agreement, String finreference) {
		logger.debug(Literal.ENTERING);
		try {
			Notes note = new Notes();
			note.setModuleName(PennantConstants.NOTES_MODULE_FACILITY);
			note.setReference(finreference);
			List<Notes> list = getNotesService().getNotesListByRole(note, false, null);
			if (list != null && !list.isEmpty()) {
				agreement.setGroupRecommendations(new ArrayList<FacilityAgreementDetail.GroupRecommendation>());
				// prepare Grouping
				Map<String, ArrayList<Notes>> hashMap = new HashMap<String, ArrayList<Notes>>();
				for (Notes notes : list) {
					if (StringUtils.isNotEmpty(notes.getRoleCode())) {
						if (hashMap.containsKey(notes.getRoleCode())) {
							hashMap.get(notes.getRoleCode()).add(notes);
						} else {
							ArrayList<Notes> arrayList = new ArrayList<Notes>();
							arrayList.add(notes);
							hashMap.put(notes.getRoleCode(), arrayList);
						}
					}
				}
				for (String roleCode : hashMap.keySet()) {
					GroupRecommendation groupRecommendation = agreement.new GroupRecommendation();
					ArrayList<Notes> templist = hashMap.get(roleCode);
					groupRecommendation.setRecommendations(new ArrayList<FacilityAgreementDetail.Recommendation>());
					for (Notes notes : templist) {
						groupRecommendation.setUserRole(notes.getRoleDesc());
						Recommendation recommendation = agreement.new Recommendation();
						String noteType = "";
						if (notes.getRemarkType().equals(PennantConstants.NOTES_TYPE_COMMENT)) {
							noteType = Labels.getLabel("common.noteType.Comment");
						} else if (notes.getRemarkType().equals(PennantConstants.NOTES_TYPE_RECOMMEND)) {
							noteType = Labels.getLabel("common.noteType.Recommend");
						}
						recommendation.setNoteType(noteType);
						recommendation.setNoteDesc(notes.getRemarks());
						recommendation.setCommentedDate(
								DateUtil.format(notes.getInputDate(), PennantConstants.dateTimeAMPMFormat));
						recommendation.setUserName(notes.getUsrLogin());
						recommendation.setUserRole(notes.getRoleDesc());
						groupRecommendation.getRecommendations().add(recommendation);
					}
					agreement.getGroupRecommendations().add(groupRecommendation);
				}
			} else {
				agreement.setGroupRecommendations(new ArrayList<FacilityAgreementDetail.GroupRecommendation>());
				GroupRecommendation groupRecommendation = agreement.new GroupRecommendation();
				groupRecommendation.setRecommendations(new ArrayList<FacilityAgreementDetail.Recommendation>());
				Recommendation recommendation = agreement.new Recommendation();
				groupRecommendation.getRecommendations().add(recommendation);
				agreement.getGroupRecommendations().add(groupRecommendation);
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug(Literal.LEAVING);
		return agreement;
	}

	/**
	 * To get the overridden item in scoring and eligibility as exception List
	 * 
	 * @param agreement
	 * @param detail
	 * @return
	 */
	private FacilityAgreementDetail setExceptions(FacilityAgreementDetail agreement, Facility detail) {
		logger.debug(Literal.ENTERING);
		try {
			if (detail != null) {
				List<FinanceScoreHeader> finscoreheader = detail.getFinScoreHeaderList();
				if (finscoreheader != null && !finscoreheader.isEmpty()) {
					agreement.setExceptionLists(
							new ArrayList<FacilityAgreementDetail.ExceptionList>(finscoreheader.size()));
					for (FinanceScoreHeader financeScoreHeader : finscoreheader) {
						if (financeScoreHeader.isOverride()) {
							ExceptionList exceptionList = agreement.new ExceptionList();
							exceptionList.setExceptionItem("Scoring");
							exceptionList.setExceptionDesc(financeScoreHeader.getGroupCodeDesc());
							agreement.getExceptionLists().add(exceptionList);
						}
					}
				}
			}
			if (agreement.getExceptionLists() == null) {
				agreement.setExceptionLists(new ArrayList<FacilityAgreementDetail.ExceptionList>(1));
				agreement.getExceptionLists().add(agreement.new ExceptionList());
			} else if (agreement.getExceptionLists().isEmpty()) {
				agreement.getExceptionLists().add(agreement.new ExceptionList());
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug(Literal.LEAVING);
		return agreement;
	}

	/**
	 * To get the customer Credit Review Details
	 * 
	 * @param agreement
	 * @return
	 */
	private FacilityAgreementDetail setCreditReviewDetails(FacilityAgreementDetail agreement, String category) {
		logger.debug(Literal.ENTERING);
		try {
			// -----------------Customer Credit Review Details
			// 1 Balance Sheet
			// 2 Income Statement
			// 3 Cash Flow Information
			int noOfYears = 3;
			Map<String, List<FinCreditReviewSummary>> detailedMap = this.creditApplicationReviewService
					.getListCreditReviewSummaryByCustId2(custid, noOfYears,
							DateUtil.getYear(DateUtil.getSysDate()), category, "");
			if (detailedMap.size() > 0) {
				agreement.setAdtYear1(null);
				agreement.setAdtYear2(null);
				agreement.setAdtYear3(null);
				List<FinCreditReviewSummary> listCreditReviewSummaries1 = null;
				List<FinCreditReviewSummary> listCreditReviewSummaries2 = null;
				List<FinCreditReviewSummary> listCreditReviewSummaries3 = null;
				for (Map.Entry<String, List<FinCreditReviewSummary>> entry : detailedMap.entrySet()) {
					if (StringUtils.isEmpty("agreement.getAdtYear1()")) {
						agreement.setAdtYear1(entry.getKey());
						listCreditReviewSummaries1 = entry.getValue();
					} else if (StringUtils.isEmpty(agreement.getAdtYear2())) {
						agreement.setAdtYear2(entry.getKey());
						listCreditReviewSummaries2 = entry.getValue();
					} else if (StringUtils.isEmpty(agreement.getAdtYear3())) {
						agreement.setAdtYear3(entry.getKey());
						listCreditReviewSummaries3 = entry.getValue();
					}
				}
				if (listCreditReviewSummaries1 != null && !listCreditReviewSummaries1.isEmpty()) {
					agreement.setCreditReviewsBalance(new ArrayList<FacilityAgreementDetail.CustomerCreditReview>());
					CustomerCreditReview crediReview = agreement.new CustomerCreditReview();
					FinCreditReviewSummary finCreditReviewSummary = null;
					for (int i = 0; i < listCreditReviewSummaries1.size(); i++) {
						finCreditReviewSummary = listCreditReviewSummaries1.get(i);
						if (category.equals(PennantConstants.PFF_CUSTCTG_CORP)) {
							if (!getCorpDisplayfiled().contains(finCreditReviewSummary.getSubCategoryCode())) {
								continue;
							}
						} else if (category.equals(PennantConstants.PFF_CUSTCTG_SME)) {
							if (!getBankDisplayfiled().contains(finCreditReviewSummary.getSubCategoryCode())) {
								continue;
							}
						}
						CustomerCreditReviewDetails customerCreditReviewDetails = agreement.new CustomerCreditReviewDetails();
						customerCreditReviewDetails
								.setSubCategoryName(finCreditReviewSummary.getLovDescSubCategoryDesc());
						customerCreditReviewDetails.setYear1(formatdAmount(finCreditReviewSummary.getItemValue()));

						if (listCreditReviewSummaries2 != null) {
							customerCreditReviewDetails
									.setYear2(formatdAmount(listCreditReviewSummaries2.get(i).getItemValue()));
						}
						if (listCreditReviewSummaries3 != null) {
							customerCreditReviewDetails
									.setYear3(formatdAmount(listCreditReviewSummaries3.get(i).getItemValue()));
						}
						crediReview.getCustomerCreditReviewDetails().add(customerCreditReviewDetails);
					}
					agreement.getCreditReviewsBalance().add(crediReview);
				}
			}
			if (agreement.getCreditReviewsBalance() == null || agreement.getCreditReviewsBalance().isEmpty()) {
				agreement.setCreditReviewsBalance(new ArrayList<FacilityAgreementDetail.CustomerCreditReview>());
				CustomerCreditReview creditReview = agreement.new CustomerCreditReview();
				creditReview.setCustomerCreditReviewDetails(
						new ArrayList<FacilityAgreementDetail.CustomerCreditReviewDetails>());
				creditReview.getCustomerCreditReviewDetails().add(agreement.new CustomerCreditReviewDetails());
				agreement.getCreditReviewsBalance().add(creditReview);
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug(Literal.LEAVING);
		return agreement;
	}

	private String formatdAmount(BigDecimal amount) {
		// in credit review Value Taken As BHD
		return PennantApplicationUtil.amountFormate(amount, 3);
	}
}