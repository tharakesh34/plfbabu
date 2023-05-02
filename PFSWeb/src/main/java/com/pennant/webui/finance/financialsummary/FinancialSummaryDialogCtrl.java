/**
 * O * Copyright 2011 - Pennant Technologies
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
 * * FileName : FinancialSummaryDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 20-06-2018 * *
 * Modified Date : 20-06-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 20-06-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financialsummary;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zhtml.Textarea;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.NotesDAO;
import com.pennant.backend.dao.finance.financialSummary.DueDiligenceDetailsDAO;
import com.pennant.backend.dao.finance.financialSummary.RecommendationNotesDetailsDAO;
import com.pennant.backend.dao.finance.financialSummary.RisksAndMitigantsDAO;
import com.pennant.backend.dao.loanquery.QueryDetailDAO;
import com.pennant.backend.delegationdeviation.DeviationHelper;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.Property;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.financialsummary.DealRecommendationMerits;
import com.pennant.backend.model.finance.financialsummary.DueDiligenceCheckList;
import com.pennant.backend.model.finance.financialsummary.DueDiligenceDetails;
import com.pennant.backend.model.finance.financialsummary.RecommendationNotes;
import com.pennant.backend.model.finance.financialsummary.RecommendationNotesConfiguration;
import com.pennant.backend.model.finance.financialsummary.RisksAndMitigants;
import com.pennant.backend.model.finance.financialsummary.SanctionConditions;
import com.pennant.backend.model.finance.financialsummary.SynopsisDetails;
import com.pennant.backend.model.finance.psl.PSLDetail;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennant.backend.model.solutionfactory.DeviationParam;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.customermasters.impl.CustomerDataService;
import com.pennant.backend.service.finance.GuarantorDetailService;
import com.pennant.backend.util.DeviationConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.PTCKeditor;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.external.eligibility.CustomerEligibiltyService;

/**
 * This is the controller class for the /WEB-INF/pages/AMTMasters/PSLDetail/pSLDetailDialog.zul file. <br>
 */
public class FinancialSummaryDialogCtrl extends GFCBaseCtrl<FinanceMain> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(FinancialSummaryDialogCtrl.class);
	protected Window window_financialSummaryDialog;
	protected Groupbox basicGb;

	private Textbox custCif;
	private Textbox lanNo;
	private Datebox businessDate;
	private Textbox customerType;
	private Textbox loanBranch;
	private Textbox product;
	private Textbox leadId;
	private Intbox proposedTenor;
	private Textbox loanReference;
	private Intbox actualTenor;
	private Textbox source;
	private CurrencyBox emi;
	protected CurrencyBox proposedLoanAmount;
	private Textbox overallLTV;
	protected Decimalbox roi;
	private Textbox schemepromotions;
	private Textbox financier;
	private Textbox mob;
	private Textbox loanAmount;
	private Textbox collateralAvailable;
	private Textbox pos;
	private Textbox endUseOfFunds;
	private Textbox btTenure;
	private Textbox btdetailsEmi;
	protected PTCKeditor customerBackground;
	protected PTCKeditor detailedBusinessProfile;
	protected PTCKeditor detailsofGroupCompaniesIfAny;
	protected PTCKeditor pdDetails;
	protected PTCKeditor majorProduct;
	protected PTCKeditor otherRemarks;
	private Textbox purposeOfLoan;
	private Textbox employeerName;
	protected PTCKeditor cmtOnCollateralDtls;
	protected PTCKeditor endUse;

	protected Listbox listBoxCustomerDetails;
	protected Listbox listBoxReferencesDetails;
	protected Listbox listBoxDeviationsDetails;
	protected Listbox listBoxSanctionConditionsDetails;
	protected Listbox listBoxInterfacesDetails;
	protected Listbox listBoxRecommendationsDetails;
	protected Listbox listBoxScoringDetails;
	protected Listbox listBoxEligibilityDetails;
	protected Listbox listBoxQueriesDetails;
	protected Listbox listBoxConvenantsDetails;
	protected Listbox listBoxDocumentCheckListDetails;
	protected Listbox listBoxDealRecommendationMeritsDetails;
	protected Listbox listBoxDueDiligenceDetail;
	protected Listbox listBoxRecommendationNoteDetails;

	List<RisksAndMitigants> risksAndMitigantsDetials = new ArrayList<RisksAndMitigants>();
	List<DeviationParam> deviationParams = PennantAppUtil.getDeviationParams();
	ArrayList<ValueLabel> documentTypes = PennantAppUtil.getDocumentTypes();

	private FinanceDetail financeDetail = null;
	private boolean newFinance = false;
	Tab parenttab = null;
	private Component parent = null;
	private boolean isFinanceProcess = false;
	private Object financeMainDialogCtrl;

	protected Listbox listBoxRisksAndMitigantsDetails;
	protected Button btnNew_NewRisksAndMitigants;
	protected Button btnNew_NewSanctionConditions;
	protected Button btnNew_NewDealRecommendationMerits;

	private List<RisksAndMitigants> risksAndMitigantsDetailList = new ArrayList<RisksAndMitigants>();
	private List<SanctionConditions> sanctionConditionsDetailList = new ArrayList<SanctionConditions>();
	private List<DealRecommendationMerits> dealRecommendationMeritsDetailList = new ArrayList<DealRecommendationMerits>();
	private List<DueDiligenceDetails> dueDiligenceDetailsList = new ArrayList<DueDiligenceDetails>();
	private List<DueDiligenceCheckList> dueDiligenceCheckListDetails = new ArrayList<DueDiligenceCheckList>();
	private List<RecommendationNotesConfiguration> recommendationNotesConfigDetails = new ArrayList<RecommendationNotesConfiguration>();
	private List<RecommendationNotes> recommendationNotesDetailsList = new ArrayList<RecommendationNotes>();
	ArrayList<ValueLabel> secRolesList = PennantAppUtil.getSecRolesList(null);
	List<ValueLabel> approveStatus = PennantStaticListUtil.getApproveStatus();
	private RisksAndMitigantsDAO risksAndMitigantsDAO;
	private DueDiligenceDetailsDAO dueDiligenceDetailsDAO;
	private QueryDetailDAO queryDetailDAO;
	private RecommendationNotesDetailsDAO recommendationNotesDetailsDAO;
	private SynopsisDetails synopsisDetails = new SynopsisDetails();

	long idCount = 0;
	private Image imgBasicDetails;
	private Image imgBtDetails;
	private Image imgCustomerDetails;
	private Image imgDueDiligence;
	private Image imgReferences;
	private Image imgSynopsisandpddetails;
	private Image imgDeviations;
	private Image imgDealRecommendationMerits;
	private Image imgSanctionConditions;
	private Image imgRisksMigigants;
	private Image imgInterfaces;
	private Image imgScoring;
	private Image imgEligibility;
	private Image imgRecommendations;
	private Image imgQueries;
	private Image imgConvents;
	private Image imgDocumentCheckList;
	private Image imgCollateralDetails;
	private Image imgAssetDetails;
	private Image imgOtherDetails;
	private Image imgRecommendationNote;

	private Groupbox gb_basicDetails;
	private Groupbox gb_btDetails;
	private Groupbox gb_customerDetails;
	private Groupbox gb_references;
	private Groupbox gb_collateralDetails;
	private Groupbox gb_assetDetails;
	private Groupbox gb_synopsisAndPdDetails;
	private Groupbox gb_deviations;
	private Groupbox gb_sanctionConditionsDetails;
	private Groupbox gb_risksAndMitigants;
	private Groupbox gb_interfacesDetails;
	private Groupbox gb_eligibilityDetails;
	private Groupbox gb_scoringDetails;
	private Groupbox gb_dueDiligenceDetail;
	private Groupbox gb_dealRecommendationMeritsDetails;
	private Groupbox gb_recommendationsDetails;
	private Groupbox gb_queriesDetails;
	private Groupbox gb_convenantsDetails;
	private Groupbox gb_documentCheckListDetails;
	private Groupbox gb_otherDetails;
	private Groupbox gb_recommendationNoteDetails;

	private CustomerDataService customerDataService;
	private GuarantorDetailService guarantorDetailService;

	List<Property> severities = PennantStaticListUtil.getManualDeviationSeverities();
	protected Tabpanel otherDetailsFieldTabPanel;
	private ExtendedFieldCtrl extendedFieldCtrl = null;

	private DueDiligenceDetails dueDiligenceDetails;
	private NotesDAO notesDAO;
	@Autowired
	private DeviationHelper deviationHelper;

	private boolean isbasicDetailsVisible = false;
	private boolean isbtDetailsVisible = false;
	private boolean iscustomerDetailsVisible = false;
	private boolean isdueDiligenceDetailsVisible = false;
	private boolean isreferencesVisible = false;
	private boolean iscollateralDetailsVisible = false;
	private boolean isassetDetailsVisible = false;
	private boolean issynopsisAndPdDetailsVisible = false;
	private boolean isdeviationsDetailsVisible = false;
	private boolean isrecommendationNoteDetailsVisible = false;
	private boolean isdealRecommendationMeritsDetailsVisible = false;
	private boolean issanctionConditionsDetailsVisible = false;
	private boolean isrisksAndMitigantsDetailsVisible = false;
	private boolean isinterfacesDetailsVisible = false;
	private boolean isscoringDetailsVisible = false;
	private boolean iseligibilityDetailsVisible = false;
	private boolean isrecommendationsDetailsVisible = false;
	private boolean isqueriesDetailsVisible = false;
	private boolean isconvenantsDetailsVisible = false;
	private boolean isdocumentCheckListDetailsVisible = false;
	private boolean isotherDetailsVisible = false;
	@Autowired(required = false)
	private CustomerEligibiltyService customerEligibiltyService;

	private Button button_FinancialSummaryDailog_DelphiCheck;

	private CustomerDialogCtrl customerDialogCtrl;

	/**
	 * default constructor.<br>
	 */
	public FinancialSummaryDialogCtrl() {
		super();
	}

	protected void doSetProperties() {
		super.pageRightName = "FinancialSummaryDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_financialSummaryDialog(Event event) {
		logger.debug(Literal.LEAVING);

		// Set the page level components.
		setPageComponents(window_financialSummaryDialog);

		try {
			if (event.getTarget().getParent() != null) {
				parent = event.getTarget().getParent();
			}

			if (arguments.containsKey("financeDetail")) {
				isFinanceProcess = true;
				financeDetail = (FinanceDetail) arguments.get("financeDetail");
				newFinance = true;
			}
			if (arguments.containsKey("tab")) {
				parenttab = (Tab) arguments.get("tab");
			}

			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}
			if (arguments.containsKey("financeMainDialogCtrl")) {

				this.financeMainDialogCtrl = arguments.get("financeMainDialogCtrl");

				if (financeMainDialogCtrl instanceof FinanceMainBaseCtrl) {
					((FinanceMainBaseCtrl) financeMainDialogCtrl).setFinancialSummaryDialogCtrl(this);
				}
				newFinance = true;

			}

			if (arguments.containsKey("isEnquiry")) {
				this.enqiryModule = (boolean) arguments.get("isEnquiry");
			}

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateRoleAuthorities(getRole(), pageRightName);
			}

			if (customerEligibiltyService != null) {
				this.button_FinancialSummaryDailog_DelphiCheck.setVisible(true);
			}

			/*
			 * int divKycHeight = this.borderLayoutHeight - 80; int semiBorderlayoutHeights = divKycHeight / 2;
			 * this.listBoxCustomerDetails.setHeight(semiBorderlayoutHeights - 108 + "px");
			 * this.listBoxReferencesDetails.setHeight(semiBorderlayoutHeights - 108 + "px");
			 * this.listBoxDeviationsDetails.setHeight(semiBorderlayoutHeights - 108 + "px");
			 * this.listBoxSanctionConditionsDetails.setHeight( semiBorderlayoutHeights - 108 + "px");
			 * this.listBoxRisksAndMitigantsDetails.setHeight( semiBorderlayoutHeights - 108 + "px");
			 * this.listBoxInterfacesDetails.setHeight(semiBorderlayoutHeights - 108 + "px");
			 * this.listBoxRecommendationsDetails.setHeight( semiBorderlayoutHeights - 108 + "px");
			 * this.listBoxScoringDetails.setHeight(semiBorderlayoutHeights - 108 + "px");
			 * this.listBoxEligibilityDetails.setHeight(semiBorderlayoutHeights - 108 + "px");
			 * this.listBoxQueriesDetails.setHeight(semiBorderlayoutHeights - 108 + "px");
			 * this.listBoxConvenantsDetails.setHeight(semiBorderlayoutHeights - 108 + "px");
			 * this.listBoxDocumentCheckListDetails.setHeight( semiBorderlayoutHeights - 108 + "px");
			 * this.listBoxDealRecommendationMeritsDetails.setHeight( semiBorderlayoutHeights - 108 + "px");
			 * this.listBoxDueDiligenceDetail.setHeight(semiBorderlayoutHeights + "px");
			 * this.listBoxRecommendationNoteDetails.setHeight( semiBorderlayoutHeights - 108 + "px");
			 */
			doShowDialog(this.financeDetail);
			doCheckRights();
		} catch (Exception e) {
			logger.error(e);

		}
	}

	public void doShowDialog(FinanceDetail financeDetail) {
		logger.debug(Literal.LEAVING);

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String cetGroupBoxesVisibility = "CET_" + financeMain.getFinCategory();
		doShowCetGroupBoxes(SysParamUtil.getValueAsString(cetGroupBoxesVisibility));

		doWriteBeanToComponents(financeDetail);
		doReadOnly();

		if (this.enqiryModule) {
			this.basicGb.setVisible(false);
		}
		this.window.setHeight(this.borderLayoutHeight - 120 + "px");

		try {
			if (getFinanceMainDialogCtrl() != null) {
				getFinanceMainDialogCtrl().getClass().getMethod("setFinancialSummaryDialogCtrl", this.getClass())
						.invoke(getFinanceMainDialogCtrl(), this);
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void doShowCetGroupBoxes(String cetGroupBoxesVisibility) {

		logger.debug(Literal.LEAVING);
		cetGroupBoxesVisibility = StringUtils.trimToEmpty(cetGroupBoxesVisibility);

		if (cetGroupBoxesVisibility.contains("gb_basicDetails")) {
			isbasicDetailsVisible = true;
			imgBasicDetails.setStyle("display:block");
			gb_basicDetails.setVisible(true);
		}
		if (cetGroupBoxesVisibility.contains("gb_btDetails")) {
			isbtDetailsVisible = true;
			imgBtDetails.setStyle("display:block");
			gb_btDetails.setVisible(true);

		}
		if (cetGroupBoxesVisibility.contains("gb_customerDetails")) {
			iscustomerDetailsVisible = true;
			imgCustomerDetails.setStyle("display:block");
			gb_customerDetails.setVisible(true);
		}
		if (cetGroupBoxesVisibility.contains("gb_dueDiligenceDetail")) {
			isdueDiligenceDetailsVisible = true;
			imgDueDiligence.setStyle("display:block");
			gb_dueDiligenceDetail.setVisible(true);
		}
		if (cetGroupBoxesVisibility.contains("gb_references")) {
			isreferencesVisible = true;
			imgReferences.setStyle("display:block");
			gb_references.setVisible(true);
		}
		if (cetGroupBoxesVisibility.contains("gb_collateralDetails")) {
			iscollateralDetailsVisible = true;
			gb_collateralDetails.setVisible(true);
		}
		if (cetGroupBoxesVisibility.contains("gb_assetDetails")) {
			isassetDetailsVisible = true;
			gb_assetDetails.setVisible(true);
		}
		if (cetGroupBoxesVisibility.contains("gb_synopsisAndPdDetails")) {
			issynopsisAndPdDetailsVisible = true;
			imgSynopsisandpddetails.setStyle("display:block");
			gb_synopsisAndPdDetails.setVisible(true);
		}
		if (cetGroupBoxesVisibility.contains("gb_deviations")) {
			isdeviationsDetailsVisible = true;
			imgDeviations.setStyle("display:block");
			gb_deviations.setVisible(true);
		}
		if (cetGroupBoxesVisibility.contains("gb_recommendationNoteDetails")) {
			isrecommendationNoteDetailsVisible = true;
			imgRecommendationNote.setStyle("display:block");
			gb_recommendationNoteDetails.setVisible(true);
		}
		if (cetGroupBoxesVisibility.contains("gb_dealRecommendationMeritsDetails")) {
			isdealRecommendationMeritsDetailsVisible = true;
			imgDealRecommendationMerits.setStyle("display:block");
			gb_dealRecommendationMeritsDetails.setVisible(true);
		}
		if (cetGroupBoxesVisibility.contains("gb_sanctionConditionsDetails")) {
			issanctionConditionsDetailsVisible = true;
			if (!this.enqiryModule) {
				imgSanctionConditions.setStyle("display:block");
			}
			gb_sanctionConditionsDetails.setVisible(true);
		}
		if (cetGroupBoxesVisibility.contains("gb_risksAndMitigants")) {
			isrisksAndMitigantsDetailsVisible = true;
			imgRisksMigigants.setStyle("display:block");
			gb_risksAndMitigants.setVisible(true);
		}
		if (cetGroupBoxesVisibility.contains("gb_interfacesDetails")) {
			isinterfacesDetailsVisible = true;
			imgInterfaces.setStyle("display:block");
			gb_interfacesDetails.setVisible(true);
		}
		if (cetGroupBoxesVisibility.contains("gb_scoringDetails")) {
			isscoringDetailsVisible = true;
			imgScoring.setStyle("display:block");
			gb_scoringDetails.setVisible(true);

		}
		if (cetGroupBoxesVisibility.contains("gb_eligibilityDetails")) {
			iseligibilityDetailsVisible = true;
			imgEligibility.setStyle("display:block");
			gb_eligibilityDetails.setVisible(true);
		}
		if (cetGroupBoxesVisibility.contains("gb_recommendationsDetails")) {
			isrecommendationsDetailsVisible = true;
			imgRecommendations.setStyle("display:block");
			gb_recommendationsDetails.setVisible(true);
		}
		if (cetGroupBoxesVisibility.contains("gb_queriesDetails")) {
			isqueriesDetailsVisible = true;
			imgQueries.setStyle("display:block");
			gb_queriesDetails.setVisible(true);
		}
		if (cetGroupBoxesVisibility.contains("gb_convenantsDetails")) {
			isconvenantsDetailsVisible = true;
			imgConvents.setStyle("display:block");
			gb_convenantsDetails.setVisible(true);
		}
		if (cetGroupBoxesVisibility.contains("gb_documentCheckListDetails")) {
			isdocumentCheckListDetailsVisible = true;
			imgDocumentCheckList.setStyle("display:block");
			gb_documentCheckListDetails.setVisible(true);
		}
		if (cetGroupBoxesVisibility.contains("gb_otherDetails")) {
			isotherDetailsVisible = true;
			imgOtherDetails.setStyle("display:block");
			gb_otherDetails.setVisible(true);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param pSLDetail
	 * 
	 */
	public void doWriteBeanToComponents(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		List<FinanceScheduleDetail> fsdList = financeDetail.getFinScheduleData().getFinanceScheduleDetails();

		this.custCif.setValue(customerDetails.getCustomer().getCustCIF());
		this.lanNo.setValue(financeMain.getFinReference());
		this.businessDate.setValue(financeMain.getFinStartDate());
		this.customerType.setValue(customerDetails.getCustomer().getCustCtgCode());
		this.loanBranch.setValue(financeMain.getLovDescFinBranchName());
		// this.product.setValue(financeMain.getFinType() + "-" +
		// financeMain.getLovDescFinTypeName());
		this.product.setValue(financeMain.getLovDescFinTypeName());
		this.loanReference.setValue(financeMain.getFinReference());
		this.source.setValue(financeMain.getLovDescSourceCity());
		if (financeDetail.getSynopsisDetails() != null) {
			try {
				if (financeDetail.getSynopsisDetails().getCustomerBackGround() != null) {
					// We are using UTF-8 character set to provide the compatibility of the existing data
					this.customerBackground
							.setValue(new String(financeDetail.getSynopsisDetails().getCustomerBackGround(),
									NotificationConstants.UTF_EIGHT_CHARSET));
				}
				if (financeDetail.getSynopsisDetails().getDetailedBusinessProfile() != null) {
					this.detailedBusinessProfile
							.setValue(new String(financeDetail.getSynopsisDetails().getDetailedBusinessProfile(),
									NotificationConstants.UTF_EIGHT_CHARSET));
				}
				if (financeDetail.getSynopsisDetails().getDetailsofGroupCompaniesIfAny() != null) {
					this.detailsofGroupCompaniesIfAny
							.setValue(new String(financeDetail.getSynopsisDetails().getDetailsofGroupCompaniesIfAny(),
									NotificationConstants.UTF_EIGHT_CHARSET));
				}
				if (financeDetail.getSynopsisDetails().getPdDetails() != null) {
					this.pdDetails.setValue(new String(financeDetail.getSynopsisDetails().getPdDetails(),
							NotificationConstants.UTF_EIGHT_CHARSET));
				}
				if (financeDetail.getSynopsisDetails().getMajorProduct() != null) {
					this.majorProduct.setValue(new String(financeDetail.getSynopsisDetails().getMajorProduct(),
							NotificationConstants.UTF_EIGHT_CHARSET));
				}
				if (financeDetail.getSynopsisDetails().getOtherRemarks() != null) {
					this.otherRemarks.setValue(new String(financeDetail.getSynopsisDetails().getOtherRemarks(),
							NotificationConstants.UTF_EIGHT_CHARSET));
				}
				if (financeDetail.getSynopsisDetails().getCmtOnCollateralDtls() != null) {
					// new requirement
					this.cmtOnCollateralDtls
							.setValue(new String(financeDetail.getSynopsisDetails().getCmtOnCollateralDtls(),
									NotificationConstants.DEFAULT_CHARSET));
				}
				if (financeDetail.getSynopsisDetails().getEndUse() != null) {
					this.endUse.setValue(new String(financeDetail.getSynopsisDetails().getEndUse(),
							NotificationConstants.DEFAULT_CHARSET));
				}
			} catch (UnsupportedEncodingException e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
		if (isbasicDetailsVisible) {
			doFillBasicDetails(financeMain, fsdList);
		}
		if (iscustomerDetailsVisible) {
			Date maturityDate = financeDetail.getFinScheduleData().getFinanceMain().getMaturityDate();
			renderCustomerDetails(customerDetails.getCustomer().getCustID(),
					customerDetails.getCustomer().getCustShrtName(), null, customerDetails.getCustomer().getCustDOB(),
					"Primary", maturityDate);
			if (!financeDetail.getJointAccountDetailList().isEmpty()) {
				List<JointAccountDetail> jointAccountDetailList = financeDetail.getJointAccountDetailList();
				for (JointAccountDetail jointAccountDetail : jointAccountDetailList) {
					renderCustomerDetails(jointAccountDetail.getCustID(), jointAccountDetail.getLovDescCIFName(),
							jointAccountDetail.getCatOfcoApplicant(), jointAccountDetail.getLovCustDob(),
							"Co-Applicant", maturityDate);

				}
			}
			if (!financeDetail.getGurantorsDetailList().isEmpty()) {
				List<GuarantorDetail> guarantorDetailList = financeDetail.getGurantorsDetailList();
				for (GuarantorDetail guarantorDetail : guarantorDetailList) {
					renderCustomerDetails(guarantorDetail.getGuarantorId(), guarantorDetail.getGuarantorCIFName(), null,
							guarantorDetail.getLovCustDob(), "Guarantor", maturityDate);

				}
			}
		}

		if (isreferencesVisible) {
		}

		if (isdeviationsDetailsVisible) {
			List<FinanceDeviations> financeDeviations = financeDetail.getFinanceDeviations();
			List<FinanceDeviations> approvedFinanceDeviations = financeDetail.getApprovedFinanceDeviations();
			List<FinanceDeviations> manualDeviations = financeDetail.getManualDeviations();
			List<FinanceDeviations> approvedManualDeviations = financeDetail.getApprovedManualDeviations();

			List<FinanceDeviations> totalDevaitons = Stream
					.of(financeDeviations, approvedFinanceDeviations, manualDeviations, approvedManualDeviations)
					.flatMap(x -> x.stream()).collect(Collectors.toList());
			doFillDeviationsDetails(totalDevaitons);
		}

		if (issanctionConditionsDetailsVisible) {
			doFillSanctionConditionsDetails(financeDetail.getSanctionDetailsList());
		}
		if (isrisksAndMitigantsDetailsVisible) {
			doFillRisksAndMitigants(financeDetail.getRisksAndMitigantsList());
		}

		if (isinterfacesDetailsVisible) {
			List<InterfaceLogDetail> interfaceLogDetail = getRisksAndMitigantsDAO()
					.getInterfaceLogDetails(financeMain.getFinReference());

			doFillInterfacesDetails(interfaceLogDetail);

		}
		if (isrecommendationsDetailsVisible) {
			Notes notes = new Notes();
			notes.setModuleName("FinanceMain");
			notes.setReference(financeMain.getFinReference());
			List<Notes> notesList = notesDAO.getNotesList(notes, false);
			doFillRecommendationsDetails(notesList);
		}
		if (iscollateralDetailsVisible) {
			doFillScoringDetails();
		}
		if (iseligibilityDetailsVisible) {
			doFillEligibilityDetails();
		}
		if (isqueriesDetailsVisible) {
			List<QueryDetail> queryDetails = queryDetailDAO.getQueryMgmtListForAgreements(financeMain.getFinReference(),
					"_View");
			doFillQueriesDetails(queryDetails);
		}
		if (isconvenantsDetailsVisible) {
			doFillConvenantsDetails(financeDetail.getCovenantTypeList());
		}
		if (isdocumentCheckListDetailsVisible) {
			doFillDocumentCheckListDetails(financeDetail.getDocumentDetailsList());
		}

		if (isdealRecommendationMeritsDetailsVisible) {
			doFillDealRecommendationMeritsDetails(financeDetail.getDealRecommendationMeritsDetailsList());
		}
		if (isotherDetailsVisible) {
			doFillOtherDetails(financeDetail);
		}

		if (isdueDiligenceDetailsVisible) {
			dueDiligenceCheckListDetails = getRisksAndMitigantsDAO().getDueDiligenceCheckListDetails();
			String pslCategory;
			String pslDetails = null;

			if (CollectionUtils.isNotEmpty(dueDiligenceCheckListDetails)) {
				// After saving
				if (CollectionUtils.isNotEmpty(financeDetail.getDueDiligenceDetailsList())) {
					doFillDueDiligenceDetails(financeDetail.getDueDiligenceDetailsList(), null);
				} else {
					// Before Saving
					// PSL Details should auto populate in Due Diligence Details
					// if available
					PSLDetail pslDetail = financeDetail.getPslDetail();
					pslCategory = pslDetail.getCategoryCode();

					if (StringUtils.equals("NPSL", pslCategory)) {
						pslDetails = null;
					} else if (StringUtils.equals("AGRI", pslCategory)) {
						pslDetails = pslCategory.concat(",")
								.concat(StringUtils.trimToEmpty(pslDetail.getWeakerSectionName())).concat(",")
								.concat(StringUtils.trimToEmpty(pslDetail.getPurposeName())).concat(",")
								.concat(StringUtils.trimToEmpty(pslDetail.getEndUseName()));
					} else if (StringUtils.equals("MSME", pslCategory)) {
						pslDetails = pslCategory.concat(",")
								.concat(StringUtils.trimToEmpty(pslDetail.getWeakerSectionName())).concat(",")
								.concat(StringUtils.trimToEmpty(pslDetail.getPurposeName()));
					} else if (StringUtils.equals("HF", pslCategory)) {
						pslDetails = pslCategory.concat(",")
								.concat(StringUtils.trimToEmpty(pslDetail.getWeakerSectionName())).concat(",")
								.concat(StringUtils.trimToEmpty(pslDetail.getPurposeName()));
					} else if (StringUtils.equals("GNL", pslCategory)) {
						pslDetails = pslCategory.concat(",")
								.concat(StringUtils.trimToEmpty(pslDetail.getWeakerSectionName())).concat(",")
								.concat(StringUtils.trimToEmpty(pslDetail.getPurposeName()));
					}

					renderDueDiligenceDetails(dueDiligenceCheckListDetails, pslDetails);
				}
			} else {
				imgDueDiligence.setVisible(false);
				gb_dueDiligenceDetail.setVisible(false);
			}
		}

		if (isrecommendationNoteDetailsVisible) {
			recommendationNotesConfigDetails = getRecommendationNotesDetailsDAO()
					.getRecommendationNotesConfigurationDetails();
			if (CollectionUtils.isNotEmpty(recommendationNotesConfigDetails)) {
				if (CollectionUtils.isNotEmpty(financeDetail.getRecommendationNoteList())) {
					doFillRecommendationNotesDetails(financeDetail.getRecommendationNoteList());
				} else {
					renderRecommendationNotesDetails(recommendationNotesConfigDetails);
				}
			} else {
				imgRecommendationNote.setVisible(false);
				gb_recommendationNoteDetails.setVisible(false);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void doFillBasicDetails(FinanceMain financeMainDetails, List<FinanceScheduleDetail> fsdList) {
		logger.debug("Entering");
		int finFormatter = CurrencyUtil.getFormat(financeMainDetails.getFinCcy());

		this.proposedLoanAmount.setProperties(false, finFormatter);
		this.emi.setProperties(false, finFormatter);
		int format = CurrencyUtil.getFormat(financeMainDetails.getFinCcy());
		if (financeMainDetails != null) {
			this.leadId.setValue(financeMainDetails.getApplicationNo());
			this.proposedTenor.setValue(financeMainDetails.getNumberOfTerms());
			this.actualTenor.setValue(financeMainDetails.getNumberOfTerms());
			this.source.setValue(financeMainDetails.getDsaName());
			this.proposedLoanAmount
					.setValue(PennantApplicationUtil.formateAmount(financeMainDetails.getFinAssetValue(), format));
			this.overallLTV.setValue("");
			this.roi.setValue(financeMainDetails.getRepayProfitRate());
			this.schemepromotions.setValue(financeMainDetails.getLovDescEligibilityMethod());
			for (FinanceScheduleDetail schd : fsdList) {
				if (schd.isRepayOnSchDate()) {
					this.emi.setValue(schd.getRepayAmount());
					if (this.emi.getValidateValue().compareTo(BigDecimal.ZERO) > 0) {
						break;
					}
				}
			}
			this.emi.setValue(PennantApplicationUtil.formateAmount(this.emi.getValidateValue(), format));
			this.purposeOfLoan.setValue(financeMainDetails.getLovDescFinPurposeName());
			this.employeerName.setValue(financeMainDetails.getEmployeeNameDesc());

		}
		logger.debug("Leaving");
	}

	public void renderCustomerDetails(long custID, String custName, String relationShipWithapplicant, Date custDob,
			String customerType, Date maturityDate) {
		logger.debug("Entering");
		Listitem item = new Listitem();
		Listcell lc;
		lc = new Listcell(customerType);
		lc.setParent(item);
		lc = new Listcell(custName);
		lc.setParent(item);
		if (StringUtils.equals(customerType, "Co-Applicant")) {
			lc = new Listcell(relationShipWithapplicant);
			lc.setParent(item);
		} else {
			lc = new Listcell("NA");
			lc.setParent(item);
		}

		lc = new Listcell("NA");
		lc.setParent(item);
		if (!StringUtils.equals(customerType, "Guarantor")) {
			lc = new Listcell(String.valueOf(DateUtil.getYearsBetween(maturityDate, custDob)));
			lc.setParent(item);
		} else {
			lc = new Listcell("NA");
			lc.setParent(item);
		}

		lc = new Listcell("");
		lc.setParent(item);

		lc = new Listcell();
		Button detailsButton;
		detailsButton = new Button();
		detailsButton.setParent(lc);
		if (StringUtils.equals(customerType, "Guarantor")) {
			detailsButton.addForward("onClick", self, "onClickGuarantorId", custID);
		} else {
			detailsButton.addForward("onClick", self, "onClickCustomerId", custID);
		}
		detailsButton.setLabel("View Details");
		lc.setParent(item);

		this.listBoxCustomerDetails.appendChild(item);
		logger.debug("Leaving");
	}

	public void doFillReferencesDetails(CustomerDetails customerDetails) {
		logger.debug("Entering");
		this.listBoxReferencesDetails.getItems().clear();
		if (customerDetails != null) {
			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell(customerDetails.getCustomer().getCustShrtName());
			lc.setParent(item);
			lc = new Listcell("Business Partner");
			lc.setParent(item);
			lc = new Listcell("9550305503");
			lc.setParent(item);
			lc = new Listcell("10");
			lc.setParent(item);
			lc = new Listcell("tested");
			lc.setParent(item);
			this.listBoxReferencesDetails.appendChild(item);
		}
		logger.debug("Leaving");
	}

	public void doFillSynopsisDetails(FinanceMain fm) {
		logger.debug("Entering");

		if (financeDetail.getSynopsisDetails() != null) {
			synopsisDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			synopsisDetails.setNewRecord(false);
		} else {
			synopsisDetails.setRecordStatus(PennantConstants.RECORD_TYPE_NEW);
			synopsisDetails.setNewRecord(true);
		}
		try {
			synopsisDetails.setCustomerBackGround(
					this.customerBackground.getValue().getBytes(NotificationConstants.UTF_EIGHT_CHARSET));
			synopsisDetails.setDetailedBusinessProfile(
					this.detailedBusinessProfile.getValue().getBytes(NotificationConstants.UTF_EIGHT_CHARSET));
			synopsisDetails.setDetailsofGroupCompaniesIfAny(
					this.detailsofGroupCompaniesIfAny.getValue().getBytes(NotificationConstants.UTF_EIGHT_CHARSET));
			synopsisDetails.setPdDetails(this.pdDetails.getValue().getBytes(NotificationConstants.UTF_EIGHT_CHARSET));
			synopsisDetails
					.setMajorProduct(this.majorProduct.getValue().getBytes(NotificationConstants.UTF_EIGHT_CHARSET));
			synopsisDetails
					.setOtherRemarks(this.otherRemarks.getValue().getBytes(NotificationConstants.UTF_EIGHT_CHARSET));
			synopsisDetails.setCmtOnCollateralDtls(
					this.cmtOnCollateralDtls.getValue().getBytes(NotificationConstants.DEFAULT_CHARSET));
			synopsisDetails.setEndUse(this.endUse.getValue().getBytes(NotificationConstants.DEFAULT_CHARSET));
		} catch (UnsupportedEncodingException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		synopsisDetails.setFinID(fm.getFinID());
		synopsisDetails.setFinReference(fm.getFinReference());

		setSynopsisDetails(synopsisDetails);

		logger.debug("Leaving");
	}

	public void doFillDeviationsDetails(List<FinanceDeviations> totalDevaitons) {
		logger.debug("Entering");
		this.listBoxDeviationsDetails.getItems().clear();
		int devSerialNo = 0;
		if (totalDevaitons != null) {
			if (CollectionUtils.isNotEmpty(totalDevaitons)) {
				for (FinanceDeviations financeDeviations : totalDevaitons) {
					Listitem item = new Listitem();
					Listcell lc;
					lc = new Listcell(String.valueOf(devSerialNo + 1));
					lc.setParent(item);

					if (StringUtils.equals(DeviationConstants.CAT_AUTO, financeDeviations.getDeviationCategory())) {

						String deviationCodedesc = deviationHelper.getDeviationDesc(financeDeviations, deviationParams);
						lc = new Listcell(deviationCodedesc);
					} else {
						lc = new Listcell(financeDeviations.getDeviationCodeName() + " - "
								+ financeDeviations.getDeviationCodeDesc());
					}

					lc.setParent(item);
					lc = new Listcell(
							PennantStaticListUtil.getlabelDesc(financeDeviations.getDelegationRole(), secRolesList));
					lc.setParent(item);

					lc = new Listcell(StringUtils.isEmpty(
							PennantStaticListUtil.getlabelDesc(financeDeviations.getApprovalStatus(), approveStatus))
									? "Pending"
									: PennantStaticListUtil.getlabelDesc(financeDeviations.getApprovalStatus(),
											approveStatus));
					lc.setParent(item);
					if (StringUtils.equals(financeDeviations.getDeviationCategory(), "A")) {
						Notes notes = new Notes();
						notes.setModuleName("FinanceDevaitions");
						notes.setReference(
								financeDeviations.getFinReference() + "_" + financeDeviations.getDeviationId());
						notes.setRemarkType("N");
						List<Notes> notesList = notesDAO.getNotesList(notes, true);

						for (Notes notesRemarks : notesList) {
							lc = new Listcell(notesRemarks.getRemarks());

						}
					} else {
						lc = new Listcell(financeDeviations.getRemarks());
					}
					lc.setParent(item);
					this.listBoxDeviationsDetails.appendChild(item);
					devSerialNo++;
				}
			}
		}
		logger.debug("Leaving");
	}

	public void doFillSanctionConditionsDetails(List<SanctionConditions> sanctionConditionsDetails) {
		logger.debug("Entering");
		this.listBoxSanctionConditionsDetails.getItems().clear();
		if (CollectionUtils.isNotEmpty(sanctionConditionsDetails)) {
			Collections.sort(sanctionConditionsDetails, new Comparator<SanctionConditions>() {

				@Override
				public int compare(SanctionConditions detail1, SanctionConditions detail2) {
					return Long.compare(detail1.getSeqNo(), detail2.getSeqNo());
				}
			});

			for (SanctionConditions sanctionConditions : sanctionConditionsDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(String.valueOf(sanctionConditions.getSeqNo()));
				lc.setParent(item);
				lc = new Listcell(sanctionConditions.getSanctionCondition());
				lc.setParent(item);
				lc = new Listcell(sanctionConditions.getStatus());
				lc.setParent(item);
				lc = new Listcell(sanctionConditions.getRemarks());
				lc.setParent(item);
				item.setAttribute("data", sanctionConditions);
				ComponentsCtrl.applyForward(item,
						"onDoubleClick=onFinancialSummarySanctionConditionsItemDoubleClicked");
				this.listBoxSanctionConditionsDetails.appendChild(item);
			}
			setSanctionConditionsDetailList(sanctionConditionsDetails);
		}
		if (CollectionUtils.isEmpty(sanctionConditionsDetails)) {
			setSanctionConditionsDetailList(new ArrayList<SanctionConditions>());
		}
		logger.debug("Leaving");
	}

	public void doFillRisksAndMitigants(List<RisksAndMitigants> risksAndMitigantsDetails) {
		logger.debug("Entering");
		this.listBoxRisksAndMitigantsDetails.getItems().clear();
		if (CollectionUtils.isNotEmpty(risksAndMitigantsDetails)) {
			Collections.sort(risksAndMitigantsDetails, new Comparator<RisksAndMitigants>() {

				@Override
				public int compare(RisksAndMitigants detail1, RisksAndMitigants detail2) {
					return Long.compare(detail1.getSeqNo(), detail2.getSeqNo());
				}
			});

			for (RisksAndMitigants risksAndMitigants : risksAndMitigantsDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(String.valueOf(risksAndMitigants.getSeqNo()));
				lc.setParent(item);
				lc = new Listcell(risksAndMitigants.getRisk());
				lc.setParent(item);
				lc = new Listcell(risksAndMitigants.getMitigants());
				lc.setParent(item);
				item.setAttribute("data", risksAndMitigants);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onFinancialSummaryRisksAndMitigantsItemDoubleClicked");
				this.listBoxRisksAndMitigantsDetails.appendChild(item);
			}
			setRisksAndMitigantsDetailList(risksAndMitigantsDetails);
		}
		if (CollectionUtils.isEmpty(risksAndMitigantsDetails)) {
			setRisksAndMitigantsDetailList(new ArrayList<RisksAndMitigants>());
		}
		logger.debug("Leaving");
	}

	public void doFillInterfacesDetails(List<InterfaceLogDetail> interfaceLogDetailList) {
		logger.debug("Entering");
		int interfaceSerialNo = 0;
		this.listBoxInterfacesDetails.getItems().clear();
		for (InterfaceLogDetail interfaceLogDetail : interfaceLogDetailList) {
			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell(String.valueOf(interfaceSerialNo + 1));
			lc.setParent(item);
			lc = new Listcell(interfaceLogDetail.getServiceName());
			lc.setParent(item);
			lc = new Listcell(interfaceLogDetail.getStatus());
			lc.setParent(item);
			lc = new Listcell(interfaceLogDetail.getErrorDesc());
			lc.setParent(item);
			this.listBoxInterfacesDetails.appendChild(item);
			interfaceSerialNo++;
		}
		logger.debug("Leaving");
	}

	public void doFillRecommendationsDetails(List<Notes> notesList) {
		logger.debug("Entering");
		int notesSerialNo = 0;
		this.listBoxRecommendationsDetails.getItems().clear();
		for (Notes notes : notesList) {
			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell(String.valueOf(notesSerialNo + 1));
			lc.setParent(item);
			lc = new Listcell(notes.getFullName(notes.getUsrFName(), notes.getUsrMName(), notes.getUsrLName()));
			lc.setParent(item);
			lc = new Listcell(notes.getRemarks());
			lc.setParent(item);
			this.listBoxRecommendationsDetails.appendChild(item);
			notesSerialNo++;
		}
		logger.debug("Leaving");
	}

	public void doFillScoringDetails() {
		logger.debug("Entering");
		this.listBoxScoringDetails.getItems().clear();
		/* for (FinanceDeviations financeDeviations : financeDetails) { */
		Listitem item = new Listitem();
		Listcell lc;
		lc = new Listcell("");
		lc.setParent(item);
		lc = new Listcell("");
		lc.setParent(item);
		lc = new Listcell("");
		lc.setParent(item);
		this.listBoxScoringDetails.appendChild(item);
		/* } */
		logger.debug("Leaving");
	}

	public void doFillEligibilityDetails() {
		logger.debug("Entering");
		this.listBoxEligibilityDetails.getItems().clear();
		/* for (FinanceDeviations financeDeviations : financeDetails) { */
		Listitem item = new Listitem();
		Listcell lc;
		lc = new Listcell("");
		lc.setParent(item);
		lc = new Listcell("");
		lc.setParent(item);
		lc = new Listcell("");
		lc.setParent(item);
		this.listBoxEligibilityDetails.appendChild(item);
		/* } */
		logger.debug("Leaving");
	}

	public void doFillQueriesDetails(List<QueryDetail> queryDetailList) {
		logger.debug("Entering");
		this.listBoxQueriesDetails.getItems().clear();
		for (QueryDetail queryDetail : queryDetailList) {
			Listitem item = new Listitem();
			Listcell lc;

			lc = new Listcell(queryDetail.getFinReference());
			lc.setParent(item);
			lc = new Listcell(String.valueOf(queryDetail.getRaisedBy() + " - " + queryDetail.getUsrLogin()));
			lc.setParent(item);
			lc = new Listcell(DateUtil.format(queryDetail.getRaisedOn(), DateFormat.LONG_DATE_TIME));
			lc.setParent(item);
			lc = new Listcell(
					String.valueOf(queryDetail.getCategoryCode() + " - " + queryDetail.getCategoryDescription()));
			lc.setParent(item);
			lc = new Listcell(queryDetail.getQryNotes());
			lc.setParent(item);
			lc = new Listcell(String.valueOf(queryDetail.getStatus()));
			lc.setParent(item);
			this.listBoxQueriesDetails.appendChild(item);
		}
		logger.debug("Leaving");
	}

	public void doFillConvenantsDetails(List<FinCovenantType> covenantTypeList) {
		logger.debug("Entering");

		logger.debug("Entering");
		this.listBoxConvenantsDetails.getItems().clear();
		int covenantSerialNo = 0;
		if (covenantTypeList != null) {
			if (CollectionUtils.isNotEmpty(covenantTypeList)) {
				for (FinCovenantType covenantTypes : covenantTypeList) {
					Listitem item = new Listitem();
					Listcell lc;
					lc = new Listcell(String.valueOf(covenantSerialNo + 1));
					lc.setParent(item);
					lc = new Listcell(covenantTypes.getCovenantTypeDesc());
					lc.setParent(item);
					lc = new Listcell(covenantTypes.getMandRoleDesc());
					lc.setParent(item);
					lc = new Listcell(String.valueOf(covenantTypes.isAlwWaiver() ? "Yes" : "No"));
					lc.setParent(item);
					lc = new Listcell(String.valueOf(covenantTypes.isPddFlag() ? "Yes" : "No"));
					lc.setParent(item);
					lc = new Listcell(String.valueOf(covenantTypes.isAlwOtc() ? "Yes" : "No"));
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					this.listBoxConvenantsDetails.appendChild(item);
					covenantSerialNo++;
				}
			}
		}
		logger.debug("Leaving");
	}

	public void doFillDocumentCheckListDetails(List<DocumentDetails> documentDetailsList) {
		logger.debug("Entering");
		this.listBoxDocumentCheckListDetails.getItems().clear();
		int checkListSerialNo = 0;

		for (DocumentDetails documentDetails : documentDetailsList) {
			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell(String.valueOf(checkListSerialNo + 1));
			lc.setParent(item);
			String docdesc = getlabelDesc(documentDetails.getDocCategory(), documentTypes);
			documentDetails.setLovDescDocCategoryName(docdesc);
			lc = new Listcell(documentDetails.getDocCategory() + " - " + documentDetails.getLovDescDocCategoryName());
			lc.setParent(item);
			lc = new Listcell(documentDetails.getDocName());
			lc.setParent(item);
			lc = new Listcell(String
					.valueOf(documentDetails.getDocReceivedDate() != null ? documentDetails.getDocReceivedDate() : ""));
			lc.setParent(item);
			lc = new Listcell(documentDetails.getCustDocTitle() != null ? documentDetails.getCustDocTitle() : "");
			lc.setParent(item);
			lc = new Listcell("");
			lc.setParent(item);
			this.listBoxDocumentCheckListDetails.appendChild(item);
			checkListSerialNo++;
		}
		/* } */
		logger.debug("Leaving");
	}

	public void doFillDealRecommendationMeritsDetails(List<DealRecommendationMerits> dealRecommendationMeritsDetails) {
		logger.debug("Entering");
		this.listBoxDealRecommendationMeritsDetails.getItems().clear();
		if (CollectionUtils.isNotEmpty(dealRecommendationMeritsDetails)) {
			Collections.sort(dealRecommendationMeritsDetails, new Comparator<DealRecommendationMerits>() {

				@Override
				public int compare(DealRecommendationMerits detail1, DealRecommendationMerits detail2) {
					return Long.compare(detail1.getSeqNo(), detail2.getSeqNo());
				}
			});

			for (DealRecommendationMerits dealRecommendationMerits : dealRecommendationMeritsDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(String.valueOf(dealRecommendationMerits.getSeqNo()));
				lc.setParent(item);
				lc = new Listcell(dealRecommendationMerits.getDealMerits());
				lc.setParent(item);
				item.setAttribute("data", dealRecommendationMerits);
				ComponentsCtrl.applyForward(item,
						"onDoubleClick=onFinancialSummaryDealRecommendationMeritsItemDoubleClicked");
				this.listBoxDealRecommendationMeritsDetails.appendChild(item);
			}
			setDealRecommendationMeritsDetailList(dealRecommendationMeritsDetails);
		}
		if (CollectionUtils.isEmpty(dealRecommendationMeritsDetails)) {
			setDealRecommendationMeritsDetailList(new ArrayList<DealRecommendationMerits>());
		}
		logger.debug("Leaving");
	}

	public void onFinancialSummaryRisksAndMitigantsItemDoubleClicked(Event event) {
		logger.debug("Entering" + event.toString());
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxRisksAndMitigantsDetails.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final RisksAndMitigants risksAndMitigants = (RisksAndMitigants) item.getAttribute("data");
			if (isDeleteRecord(risksAndMitigants.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();

				map.put("risksAndMitigants", risksAndMitigants);
				map.put("financialSummaryDialogCtrl", this);
				map.put("isFinanceProcess", isFinanceProcess);
				map.put("roleCode", getRole());
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/RisksAndMitigantsDialog.zul", null,
							map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving");
	}

	public void onFinancialSummarySanctionConditionsItemDoubleClicked(Event event) {
		logger.debug("Entering" + event.toString());
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxSanctionConditionsDetails.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final SanctionConditions sanctionConditions = (SanctionConditions) item.getAttribute("data");
			if (isDeleteRecord(sanctionConditions.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();

				map.put("sanctionConditions", sanctionConditions);
				map.put("financialSummaryDialogCtrl", this);
				map.put("isFinanceProcess", isFinanceProcess);
				map.put("roleCode", getRole());
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/SanctionConditionsDialog.zul", null,
							map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving");
	}

	public void onFinancialSummaryDealRecommendationMeritsItemDoubleClicked(Event event) {
		logger.debug("Entering" + event.toString());
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxDealRecommendationMeritsDetails.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final DealRecommendationMerits dealRecommendationMerits = (DealRecommendationMerits) item
					.getAttribute("data");
			if (isDeleteRecord(dealRecommendationMerits.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();

				map.put("dealRecommendationMerits", dealRecommendationMerits);
				map.put("financialSummaryDialogCtrl", this);
				map.put("isFinanceProcess", isFinanceProcess);
				map.put("roleCode", getRole());
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DealRecommendationMeritsDialog.zul",
							null, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		this.custCif.setReadonly(true);
		this.lanNo.setReadonly(true);
		this.businessDate.setReadonly(true);
		this.customerType.setReadonly(true);
		this.loanBranch.setReadonly(true);
		this.product.setReadonly(true);
		this.leadId.setReadonly(true);
		this.proposedTenor.setReadonly(true);
		this.loanReference.setReadonly(true);
		this.actualTenor.setReadonly(true);
		this.source.setReadonly(true);
		this.emi.setReadonly(true);
		this.purposeOfLoan.setReadonly(true);
		this.employeerName.setReadonly(true);
		this.proposedLoanAmount.setReadonly(true);
		this.overallLTV.setReadonly(true);
		this.roi.setReadonly(true);
		this.schemepromotions.setReadonly(true);
		this.financier.setReadonly(true);
		this.mob.setReadonly(true);
		this.loanAmount.setReadonly(true);
		this.collateralAvailable.setReadonly(true);
		this.pos.setReadonly(true);
		this.endUseOfFunds.setReadonly(true);
		this.btTenure.setReadonly(true);
		this.btdetailsEmi.setReadonly(true);
		this.btnNew_NewRisksAndMitigants.setVisible(true);
		this.btnNew_NewSanctionConditions.setVisible(true);
		this.btnNew_NewDealRecommendationMerits.setVisible(true);

		this.leadId.setVisible(true);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities(super.pageRightName, getRole());
		if (this.enqiryModule) {
			this.btnNew_NewSanctionConditions.setVisible(false);
		}

		// Customer related List Buttons
		/*
		 * this.btnNew_NewRisksAndMitigants .setVisible(getUserWorkspace().isAllowed(
		 * "button_FinancialSummaryDailog_NewRisksAndMitigants"));
		 */
		/*
		 * this.btnNew_NewSanctionConditions .setVisible(getUserWorkspace().isAllowed(
		 * "button_FinancialSummaryDailog_NewSanctionConditions"));
		 */

		logger.debug("Leaving");
	}

	public void onClick$btnNew_NewRisksAndMitigants(Event event) {
		logger.debug("Entering");
		RisksAndMitigants risksAndMitigants = new RisksAndMitigants();
		risksAndMitigants.setNewRecord(true);
		risksAndMitigants.setWorkflowId(0);
		if (risksAndMitigantsDetailList != null && risksAndMitigantsDetailList.size() > 0) {
			idCount = risksAndMitigantsDetailList.size() + 1;
			risksAndMitigants.setSeqNo(idCount);
		} else {
			risksAndMitigants.setSeqNo(1);
		}
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("risksAndMitigants", risksAndMitigants);
		map.put("financialSummaryDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("isFinanceProcess", isFinanceProcess);
		map.put("roleCode", getRole());

		if (getFinanceMainDialogCtrl() != null) {
			map.put("financeMainDialogCtrl", getFinanceMainDialogCtrl());
		}
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/RisksAndMitigantsDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	public void onClick$btnNew_NewSanctionConditions(Event event) {
		logger.debug("Entering");
		SanctionConditions sanctionConditions = new SanctionConditions();
		sanctionConditions.setNewRecord(true);
		sanctionConditions.setWorkflowId(0);
		if (CollectionUtils.isNotEmpty(sanctionConditionsDetailList)) {
			Collections.sort(sanctionConditionsDetailList,
					(sanction1, sanction2) -> sanction1.getSeqNo() > sanction2.getSeqNo() ? -1
							: sanction1.getSeqNo() < sanction2.getSeqNo() ? 1 : 0);
			idCount = sanctionConditionsDetailList.get(0).getSeqNo() + 1;
			sanctionConditions.setSeqNo(idCount);
		} else {
			sanctionConditions.setSeqNo(1);
		}
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("sanctionConditions", sanctionConditions);
		map.put("financialSummaryDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("isFinanceProcess", isFinanceProcess);
		map.put("roleCode", getRole());

		if (getFinanceMainDialogCtrl() != null) {
			map.put("financeMainDialogCtrl", getFinanceMainDialogCtrl());
		}
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/SanctionConditionsDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	public void onClick$btnNew_NewDealRecommendationMerits(Event event) {
		logger.debug("Entering");
		DealRecommendationMerits dealRecommendationMerits = new DealRecommendationMerits();
		dealRecommendationMerits.setNewRecord(true);
		dealRecommendationMerits.setWorkflowId(0);
		if (dealRecommendationMeritsDetailList != null && dealRecommendationMeritsDetailList.size() > 0) {
			idCount = dealRecommendationMeritsDetailList.size() + 1;
			dealRecommendationMerits.setSeqNo(idCount);
		} else {
			dealRecommendationMerits.setSeqNo(1);
		}
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("dealRecommendationMerits", dealRecommendationMerits);
		map.put("financialSummaryDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("isFinanceProcess", isFinanceProcess);
		map.put("roleCode", getRole());

		if (getFinanceMainDialogCtrl() != null) {
			map.put("financeMainDialogCtrl", getFinanceMainDialogCtrl());
		}
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DealRecommendationMeritsDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * View The Customer Details
	 */
	public void onClickCustomerId(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		long custID = (long) event.getData();

		CustomerDetails customerDetails = null;
		if (financeDetail.getCustomerDetails().getCustomer().getCustCIF().equals(custCif)) {
			customerDetails = financeDetail.getCustomerDetails();
		} else {
			customerDetails = customerDataService.getCustomerDetailsbyID(custID, true, "_AView");

		}
		String pageName = PennantAppUtil.getCustomerPageName();
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("customerDetails", customerDetails);
		map.put("newRecord", false);
		map.put("isEnqProcess", true);
		map.put("CustomerEnq", true);
		map.put("enqiryModule", true);
		map.put("enqModule", true);
		map.put("moduleType", PennantConstants.MODULETYPE_ENQ);
		Executions.createComponents(pageName, null, map);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * View The Customer Details
	 */
	public void onClickGuarantorId(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		long guarantorID = (long) event.getData();

		GuarantorDetail guarantorDetail = null;
		guarantorDetail = guarantorDetailService.getGuarantorDetailById(guarantorID);

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("guarantorDetail", guarantorDetail);
		map.put("enqModule", true);
		map.put("moduleType", "ENQ");
		map.put("finsumryGurnatorEnq", true);

		map.put("moduleType", PennantConstants.MODULETYPE_ENQ);
		Executions.createComponents("/WEB-INF/pages/Finance/GuarantorDetail/GuarantorDetailDialog.zul", null, map);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is for append verification extended field details
	 */
	private void doFillOtherDetails(FinanceDetail aFinanceDetail) {
		logger.debug(Literal.ENTERING);
		try {
			FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
			if (aFinanceMain == null) {
				return;
			}
			extendedFieldCtrl = new ExtendedFieldCtrl();
			ExtendedFieldHeader extendedFieldHeader = this.extendedFieldCtrl
					.getExtendedFieldHeader(ExtendedFieldConstants.MODULE_LOAN, aFinanceMain.getFinCategory());
			if (extendedFieldHeader == null) {
				return;
			}
			List<ExtendedFieldDetail> detailsList = extendedFieldHeader.getExtendedFieldDetails();
			int fieldSize = 0;
			if (detailsList != null && !detailsList.isEmpty()) {
				fieldSize = detailsList.size();
				if (fieldSize != 0) {
					fieldSize = fieldSize / 2;
					fieldSize = fieldSize + 1;
				}
			}
			ExtendedFieldRender extendedFieldRender = extendedFieldCtrl
					.getExtendedFieldRender(aFinanceMain.getFinReference());
			extendedFieldCtrl.setTabpanel(otherDetailsFieldTabPanel);
			aFinanceDetail.setExtendedFieldHeader(extendedFieldHeader);
			aFinanceDetail.setExtendedFieldRender(extendedFieldRender);

			if (aFinanceDetail.getBefImage() != null) {
				aFinanceDetail.getBefImage().setExtendedFieldHeader(extendedFieldHeader);
				aFinanceDetail.getBefImage().setExtendedFieldRender(extendedFieldRender);
			}

			extendedFieldCtrl.setCcyFormat(CurrencyUtil.getFormat(aFinanceMain.getFinCcy()));
			extendedFieldCtrl.setReadOnly(/* isReadOnly("CustomerDialog_custFirstName") */false);
			extendedFieldCtrl.setWindow(window_financialSummaryDialog);
			extendedFieldCtrl.setReadOnly(true);
			extendedFieldCtrl.render();
			this.otherDetailsFieldTabPanel.setHeight((fieldSize * 37) + "px");
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void renderDueDiligenceDetails(List<DueDiligenceCheckList> dueDiligenceCheckListDetails, String pslDetails) {
		this.listBoxDueDiligenceDetail.getItems().clear();

		List<DueDiligenceDetails> dueDiligenceDetails = new ArrayList<DueDiligenceDetails>();

		for (DueDiligenceCheckList dueDiligenceCheck : dueDiligenceCheckListDetails) {
			DueDiligenceDetails dueDiligenceDts = new DueDiligenceDetails();
			dueDiligenceDts.setParticularId(dueDiligenceCheck.getId());
			dueDiligenceDts.setParticulars(dueDiligenceCheck.getParticulars());
			dueDiligenceDts.setStatus(dueDiligenceCheck.getStatus());
			dueDiligenceDts.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			dueDiligenceDts.setNewRecord(true);
			dueDiligenceDetails.add(dueDiligenceDts);
		}
		doFillDueDiligenceDetails(dueDiligenceDetails, pslDetails);
	}

	public void doFillDueDiligenceDetails(List<DueDiligenceDetails> dueDiligenceDetailsList, String pslDetails) {
		this.listBoxDueDiligenceDetail.getItems().clear();
		long dueDiligenceCount = 0;
		if (CollectionUtils.isNotEmpty(dueDiligenceDetailsList)) {
			for (DueDiligenceDetails dueDiligenceDetails : dueDiligenceDetailsList) {
				Listitem item = new Listitem();
				Listcell lc;
				// ID
				lc = new Listcell(String.valueOf(dueDiligenceCount + 1));
				lc.setParent(item);

				// PARTICULARS
				lc = new Listcell(String.valueOf(dueDiligenceDetails.getParticulars()));
				lc.setParent(item);

				// STATUS
				lc = new Listcell();
				Combobox status = new Combobox();
				status.setParent(lc);
				status.setReadonly(false);
				status.setAttribute("dueDiligenceDetails", dueDiligenceDetails);
				status.addForward("onChange", self, "onChangeStatus", dueDiligenceDetails);
				String statusValues = dueDiligenceDetailsDAO.getStatus(dueDiligenceDetails.getParticularId());
				String statusComboBoxValues = statusValues;
				List<String> statusComboBoxList = Arrays.asList(statusComboBoxValues.split(","));
				List<ValueLabel> statusList = new ArrayList<>();
				for (String statusDetail : statusComboBoxList) {
					ValueLabel deta = new ValueLabel();
					deta.setLabel(statusDetail);
					deta.setValue(statusDetail);
					statusList.add(deta);
				}

				fillComboBox(status, dueDiligenceDetails.getStatus(), statusList, "");
				if (StringUtils.equals(PennantConstants.List_Select, (getComboboxValue(status)))) {
					dueDiligenceDetails.setStatus("#");
				}

				if (StringUtils.isNotBlank(pslDetails)
						&& StringUtils.equals("Priority Sector Lending", dueDiligenceDetails.getParticulars())) {
					fillComboBox(status, "Applicable", statusList, "");

				}
				lc.setParent(item);
				// Reference
				lc = new Listcell();
				Textarea reference = new Textarea();
				reference.setParent(lc);
				reference.setReadonly(false);

				reference.setAttribute("dueDiligenceDetails", dueDiligenceDetails);

				reference.addForward("onChange", self, "onChangeRemarks", dueDiligenceDetails);
				reference.setStyle("width:400px;height:70px");
				reference.setValue(dueDiligenceDetails.getRemarks());
				if (StringUtils.isNotBlank(pslDetails)
						&& StringUtils.equals("Priority Sector Lending", dueDiligenceDetails.getParticulars())) {
					reference.setValue(pslDetails);
					dueDiligenceDetails = (DueDiligenceDetails) status.getAttribute("dueDiligenceDetails");
					dueDiligenceDetails.setStatus(status.getSelectedItem().getValue());
					dueDiligenceDetails = (DueDiligenceDetails) reference.getAttribute("dueDiligenceDetails");
					dueDiligenceDetails.setRemarks(reference.getValue());
				}
				lc.setParent(item);

				item.setAttribute("data", dueDiligenceDetailsList);
				this.listBoxDueDiligenceDetail.appendChild(item);

				if (!dueDiligenceDetails.isNewRecord()) {
					dueDiligenceDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					dueDiligenceDetails.setNewRecord(false);
					dueDiligenceDetails.setParticularId(dueDiligenceDetails.getParticularId());
				}
				dueDiligenceCount++;
			}
			setDueDiligenceDetailsList(dueDiligenceDetailsList);
		}
	}

	public void renderRecommendationNotesDetails(List<RecommendationNotesConfiguration> recommendationNoteDetailsList) {
		this.listBoxRecommendationNoteDetails.getItems().clear();

		List<RecommendationNotes> recommendationNotesList = new ArrayList<RecommendationNotes>();

		for (RecommendationNotesConfiguration recommendationNotes : recommendationNoteDetailsList) {
			RecommendationNotes recommendationNotesDetails = new RecommendationNotes();
			recommendationNotesDetails.setParticularId(recommendationNotes.getId());
			recommendationNotesDetails.setParticulars(recommendationNotes.getParticulars());
			recommendationNotesDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			recommendationNotesDetails.setNewRecord(true);
			recommendationNotesList.add(recommendationNotesDetails);
		}
		doFillRecommendationNotesDetails(recommendationNotesList);
	}

	public void doFillRecommendationNotesDetails(List<RecommendationNotes> recommendationNotesList) {
		this.listBoxRecommendationNoteDetails.getItems().clear();
		long recommendationNotesCount = 0;
		if (CollectionUtils.isNotEmpty(recommendationNotesList)) {
			for (RecommendationNotes recommendationNotesDetails : recommendationNotesList) {
				Listitem item = new Listitem();
				Listcell lc;
				// IDo
				lc = new Listcell(String.valueOf(recommendationNotesCount + 1));
				lc.setParent(item);

				// PARTICULARS
				lc = new Listcell(String.valueOf(recommendationNotesDetails.getParticulars()));
				lc.setParent(item);

				// Reference

				lc = new Listcell();
				Textarea comments = new Textarea();
				comments.setParent(lc);
				comments.setReadonly(false);

				comments.setAttribute("recommendationNotesDetails", recommendationNotesDetails);

				comments.addForward("onChange", self, "onChangeComments", recommendationNotesDetails);
				comments.setValue(recommendationNotesDetails.getRemarks());
				comments.setStyle("width:450px;height:70px");

				lc.setParent(item);

				item.setAttribute("data", recommendationNotesList);
				this.listBoxRecommendationNoteDetails.appendChild(item);

				if (!recommendationNotesDetails.isNewRecord()) {
					recommendationNotesDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					recommendationNotesDetails.setNewRecord(false);
					recommendationNotesDetails.setParticularId(recommendationNotesDetails.getParticularId());
				}
				recommendationNotesCount++;
			}
			setRecommendationNotesDetailsList(recommendationNotesList);
		}
	}

	public void onChangeStatus(ForwardEvent event) {
		logger.debug("Entering");

		Combobox statusCombo = (Combobox) event.getOrigin().getTarget();
		DueDiligenceDetails dueDiligenceDetails = (DueDiligenceDetails) statusCombo.getAttribute("dueDiligenceDetails");
		dueDiligenceDetails.setStatus(statusCombo.getSelectedItem().getValue());

		logger.debug("Leaving");
	}

	public void onChangeRemarks(ForwardEvent event) {
		logger.debug("Entering");

		Textarea remarksTextArea = (Textarea) event.getOrigin().getTarget();
		DueDiligenceDetails dueDiligenceDetails = (DueDiligenceDetails) remarksTextArea
				.getAttribute("dueDiligenceDetails");
		dueDiligenceDetails.setRemarks(remarksTextArea.getValue());

		logger.debug("Leaving");
	}

	public void onChangeComments(ForwardEvent event) {
		logger.debug("Entering");

		Textarea commentsTextArea = (Textarea) event.getOrigin().getTarget();
		RecommendationNotes recommendationNotesDetails = (RecommendationNotes) commentsTextArea
				.getAttribute("recommendationNotesDetails");
		recommendationNotesDetails.setRemarks(commentsTextArea.getValue());

		logger.debug("Leaving");
	}

	public void onClick$imgBasicDetails(Event event) {
		logger.debug("Entering");
		gb_basicDetails.focus();
		logger.debug("Leaving");
	}

	public void onClick$imgBtDetails(Event event) {
		logger.debug("Entering");
		gb_btDetails.focus();
		logger.debug("Leaving");
	}

	public void onClick$imgCustomerDetails(Event event) {
		logger.debug("Entering");
		gb_customerDetails.focus();
		logger.debug("Leaving");
	}

	public void onClick$imgDueDiligence(Event event) {
		logger.debug("Entering");
		gb_dueDiligenceDetail.focus();
		logger.debug("Leaving");
	}

	public void onClick$imgReferences(Event event) {
		logger.debug("Entering");
		gb_references.focus();
		logger.debug("Leaving");
	}

	public void onClick$imgSynopsisandpddetails(Event event) {
		logger.debug("Entering");
		gb_synopsisAndPdDetails.focus();
		logger.debug("Leaving");
	}

	public void onClick$imgDeviations(Event event) {
		logger.debug("Entering");
		gb_deviations.focus();
		logger.debug("Leaving");
	}

	public void onClick$imgDealRecommendationMerits(Event event) {
		logger.debug("Entering");
		gb_dealRecommendationMeritsDetails.focus();
		logger.debug("Leaving");
	}

	public void onClick$imgSanctionConditions(Event event) {
		logger.debug("Entering");
		gb_sanctionConditionsDetails.focus();
		logger.debug("Leaving");
	}

	public void onClick$imgRisksMigigants(Event event) {
		logger.debug("Entering");
		gb_risksAndMitigants.focus();
		logger.debug("Leaving");
	}

	public void onClick$imgInterfaces(Event event) {
		logger.debug("Entering");
		gb_interfacesDetails.focus();
		logger.debug("Leaving");
	}

	public void onClick$imgScoring(Event event) {
		logger.debug("Entering");
		gb_scoringDetails.focus();
		logger.debug("Leaving");
	}

	public void onClick$imgEligibility(Event event) {
		logger.debug("Entering");
		gb_eligibilityDetails.focus();
		logger.debug("Leaving");
	}

	public void onClick$imgRecommendations(Event event) {
		logger.debug("Entering");
		gb_recommendationsDetails.focus();
		logger.debug("Leaving");
	}

	public void onClick$imgQueries(Event event) {
		logger.debug("Entering");
		gb_queriesDetails.focus();
		logger.debug("Leaving");
	}

	public void onClick$imgConvents(Event event) {
		logger.debug("Entering");
		gb_convenantsDetails.focus();
		logger.debug("Leaving");
	}

	public void onClick$imgDocumentCheckList(Event event) {
		logger.debug("Entering");
		gb_documentCheckListDetails.focus();
		logger.debug("Leaving");
	}

	public void onClick$imgCollateralDetails(Event event) {
		logger.debug("Entering");
		gb_collateralDetails.focus();
		logger.debug("Leaving");
	}

	public void onClick$imgAssetDetails(Event event) {
		logger.debug("Entering");
		gb_assetDetails.focus();
		logger.debug("Leaving");
	}

	public void onClick$imgOtherDetails(Event event) {
		logger.debug("Entering");
		gb_otherDetails.focus();
		logger.debug("Leaving");
	}

	public void onClick$imgRecommendationNote(Event event) {
		logger.debug("Entering");
		gb_recommendationNoteDetails.focus();
		logger.debug("Leaving");
	}

	public void onClick$button_FinancialSummaryDailog_DelphiCheck(Event event)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		customerEligibiltyService.checkCustomerEligility(financeDetail);
		setCustomerDialogCtrl(fetchCustomerDialogCtrl());
		if (getCustomerDialogCtrl() != null) {
			getCustomerDialogCtrl().doFillCustomerExtLiabilityDetails(
					financeDetail.getCustomerDetails().getCustomerExtLiabilityList());
		}
		logger.debug("Leaving");
	}

	public CustomerDialogCtrl fetchCustomerDialogCtrl()
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		CustomerDialogCtrl customerDialogCtrl = null;
		if (getFinanceMainDialogCtrl().getClass().getMethod("getCustomerDialogCtrl") != null) {
			customerDialogCtrl = (CustomerDialogCtrl) getFinanceMainDialogCtrl().getClass()
					.getMethod("getCustomerDialogCtrl").invoke(getFinanceMainDialogCtrl());
		}
		logger.debug("Leaving");
		return customerDialogCtrl;
	}

	public static String getlabelDesc(String value, List<ValueLabel> list) {
		for (ValueLabel valueLabel : list) {
			if (valueLabel.getValue().equalsIgnoreCase(value)) {
				return valueLabel.getLabel();
			}
		}
		return "";
	}

	private boolean isDeleteRecord(String rcdType) {
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, rcdType)
				|| StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, rcdType)) {
			return true;
		}
		return false;
	}

	public String getComboboxValue(Combobox combobox) {
		String comboValue = "";
		if (combobox.getSelectedItem() != null) {
			comboValue = combobox.getSelectedItem().getValue().toString();
		} else {
			combobox.setSelectedIndex(0);
		}
		return comboValue;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public List<RisksAndMitigants> getRisksAndMitigantsDetailList() {
		return risksAndMitigantsDetailList;
	}

	public void setRisksAndMitigantsDetailList(List<RisksAndMitigants> risksAndMitigantsDetailList) {
		this.risksAndMitigantsDetailList = risksAndMitigantsDetailList;
	}

	public RisksAndMitigantsDAO getRisksAndMitigantsDAO() {
		return risksAndMitigantsDAO;
	}

	public void setRisksAndMitigantsDAO(RisksAndMitigantsDAO risksAndMitigantsDAO) {
		this.risksAndMitigantsDAO = risksAndMitigantsDAO;
	}

	public List<SanctionConditions> getSanctionConditionsDetailList() {
		return sanctionConditionsDetailList;
	}

	public void setSanctionConditionsDetailList(List<SanctionConditions> sanctionConditionsDetailList) {
		this.sanctionConditionsDetailList = sanctionConditionsDetailList;
	}

	public GuarantorDetailService getGuarantorDetailService() {
		return guarantorDetailService;
	}

	public void setGuarantorDetailService(GuarantorDetailService guarantorDetailService) {
		this.guarantorDetailService = guarantorDetailService;
	}

	public List<DealRecommendationMerits> getDealRecommendationMeritsDetailList() {
		return dealRecommendationMeritsDetailList;
	}

	public void setDealRecommendationMeritsDetailList(
			List<DealRecommendationMerits> dealRecommendationMeritsDetailList) {
		this.dealRecommendationMeritsDetailList = dealRecommendationMeritsDetailList;
	}

	public List<DueDiligenceDetails> getDueDiligenceDetailsList() {
		return dueDiligenceDetailsList;
	}

	public void setDueDiligenceDetailsList(List<DueDiligenceDetails> dueDiligenceDetailsList) {
		this.dueDiligenceDetailsList = dueDiligenceDetailsList;
	}

	public DueDiligenceDetails getDueDiligenceDetails() {
		return dueDiligenceDetails;
	}

	public void setDueDiligenceDetails(DueDiligenceDetails dueDiligenceDetails) {
		this.dueDiligenceDetails = dueDiligenceDetails;
	}

	public DueDiligenceDetailsDAO getDueDiligenceDetailsDAO() {
		return dueDiligenceDetailsDAO;
	}

	public void setDueDiligenceDetailsDAO(DueDiligenceDetailsDAO dueDiligenceDetailsDAO) {
		this.dueDiligenceDetailsDAO = dueDiligenceDetailsDAO;
	}

	public NotesDAO getNotesDAO() {
		return notesDAO;
	}

	public void setNotesDAO(NotesDAO notesDAO) {
		this.notesDAO = notesDAO;
	}

	public QueryDetailDAO getQueryDetailDAO() {
		return queryDetailDAO;
	}

	public void setQueryDetailDAO(QueryDetailDAO queryDetailDAO) {
		this.queryDetailDAO = queryDetailDAO;
	}

	public List<RecommendationNotes> getRecommendationNotesDetailsList() {
		return recommendationNotesDetailsList;
	}

	public void setRecommendationNotesDetailsList(List<RecommendationNotes> recommendationNotesDetailsList) {
		this.recommendationNotesDetailsList = recommendationNotesDetailsList;
	}

	public RecommendationNotesDetailsDAO getRecommendationNotesDetailsDAO() {
		return recommendationNotesDetailsDAO;
	}

	public void setRecommendationNotesDetailsDAO(RecommendationNotesDetailsDAO recommendationNotesDetailsDAO) {
		this.recommendationNotesDetailsDAO = recommendationNotesDetailsDAO;
	}

	public SynopsisDetails getSynopsisDetails() {
		return synopsisDetails;
	}

	public void setSynopsisDetails(SynopsisDetails synopsisDetails) {
		this.synopsisDetails = synopsisDetails;
	}

	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}

	@Autowired
	public void setCustomerDataService(CustomerDataService customerDataService) {
		this.customerDataService = customerDataService;
	}

}