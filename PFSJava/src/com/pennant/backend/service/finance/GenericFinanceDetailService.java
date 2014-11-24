package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.Interface.service.PostingsInterfaceService;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.AccountEngineExecutionRIA;
import com.pennant.app.util.AccountProcessUtil;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FinanceProfitDetailFiller;
import com.pennant.app.util.OverDueRecoveryPostingsUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SuspensePostingUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.commitment.CommitmentDAO;
import com.pennant.backend.dao.commitment.CommitmentMovementDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.finance.DefermentDetailDAO;
import com.pennant.backend.dao.finance.DefermentHeaderDAO;
import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinStatusDetailDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.financemanagement.FinanceStepDetailDAO;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.dao.rulefactory.FinFeeChargesDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.commitment.CommitmentMovement;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.DefermentDetail;
import com.pennant.backend.model.finance.FinContributorDetail;
import com.pennant.backend.model.finance.FinContributorHeader;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.lmtmasters.CarLoanDetail;
import com.pennant.backend.model.lmtmasters.CommidityLoanHeader;
import com.pennant.backend.model.lmtmasters.EducationalLoan;
import com.pennant.backend.model.lmtmasters.GenGoodsLoanDetail;
import com.pennant.backend.model.lmtmasters.GoodsLoanDetail;
import com.pennant.backend.model.lmtmasters.HomeLoanDetail;
import com.pennant.backend.model.lmtmasters.MortgageLoanDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEAmountCodesRIA;
import com.pennant.backend.model.rulefactory.AECommitment;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.contractor.ContractorAssetDetailService;
import com.pennant.backend.service.finance.impl.FinanceDetailServiceImpl;
import com.pennant.backend.service.lmtmasters.CarLoanDetailService;
import com.pennant.backend.service.lmtmasters.CommidityLoanDetailService;
import com.pennant.backend.service.lmtmasters.EducationalLoanService;
import com.pennant.backend.service.lmtmasters.GenGoodsLoanDetailService;
import com.pennant.backend.service.lmtmasters.GoodsLoanDetailService;
import com.pennant.backend.service.lmtmasters.HomeLoanDetailService;
import com.pennant.backend.service.lmtmasters.MortgageLoanDetailService;
import com.pennant.backend.service.lmtmasters.SharesDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public abstract class GenericFinanceDetailService extends GenericService<FinanceDetail>{
	private final static Logger logger = Logger.getLogger(FinanceDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	
	private CustomerStatusCodeDAO customerStatusCodeDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceDisbursementDAO financeDisbursementDAO;
	private DefermentHeaderDAO defermentHeaderDAO;
	private DefermentDetailDAO defermentDetailDAO;
	private RepayInstructionDAO repayInstructionDAO;
	private FinODPenaltyRateDAO finODPenaltyRateDAO;	
	private FinanceTypeDAO financeTypeDAO;
	private DocumentDetailsDAO documentDetailsDAO;
	private CustomerDocumentDAO customerDocumentDAO;
	private CommitmentDAO commitmentDAO;
	private PostingsDAO postingsDAO;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private CommitmentMovementDAO commitmentMovementDAO;
	private FinLogEntryDetailDAO finLogEntryDetailDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private OverdueChargeRecoveryDAO recoveryDAO;
	private FinanceSuspHeadDAO financeSuspHeadDAO;
	private TransactionEntryDAO transactionEntryDAO;
	private FinFeeChargesDAO finFeeChargesDAO;
	private FinanceStepDetailDAO  financeStepDetailDAO;

	private AccountEngineExecution engineExecution;
	private AccountEngineExecutionRIA engineExecutionRIA;
	private PostingsInterfaceService postingsInterfaceService;
	private CustomerDAO customerDAO;
	private AccountProcessUtil accountProcessUtil;
	private FinanceProfitDetailFiller financeProfitDetailFiller;
	private PostingsPreparationUtil postingsPreparationUtil;
	private FinStatusDetailDAO finStatusDetailDAO; 
	private SuspensePostingUtil suspensePostingUtil;
	private OverDueRecoveryPostingsUtil recoveryPostingsUtil;

	// Service Classes
	private EligibilityDetailService eligibilityDetailService;
	private GuarantorDetailService guarantorDetailService;
	private JointAccountDetailService jointAccountDetailService;
	private AgreementDetailService agreementDetailService;
	private ScoringDetailService scoringDetailService;
	private CheckListDetailService checkListDetailService;
	private CarLoanDetailService carLoanDetailService;
	private EducationalLoanService educationalLoanService;
	private HomeLoanDetailService homeLoanDetailService;
	private MortgageLoanDetailService mortgageLoanDetailService;
	private GoodsLoanDetailService goodsLoanDetailService;
	private GenGoodsLoanDetailService genGoodsLoanDetailService;
	private CommidityLoanDetailService commidityLoanDetailService;
	private ContractorAssetDetailService contractorAssetDetailService;
	private SharesDetailService sharesDetailService;

	public enum Assets {
		VEHICLE,
		HOME, 
		EDUCATON, 
		EQPMENT, 
		GOODS, 
		COMIDITY, 
		SHARES,
		GENGOODS,
		NOTAPP
	}
	
	private enum FINEVENT {
		ADDDBSF , ADDDBSN , ADDDBSP , DEFRPY , DEFFRQ , EARLYPAY , EARLYSTL , LATEPAY , RATCHG , SCDCHG , COMPOUND ;
	}
	
	
	protected String excludeFields = "calculateRepay,equalRepay,eventFromDate,eventToDate,increaseTerms,"
			+ "allowedDefRpyChange,availedDefRpyChange,allowedDefFrqChange,availedDefFrqChange,recalFromDate,recalToDate,excludeDeferedDates,"
			+ "financeScheduleDetails,disbDate, disbursementDetails,repayInstructions, rateChanges, defermentHeaders,addTermAfter,"
			+ "defermentDetails,scheduleMap,reqTerms,errorDetails,carLoanDetail,educationalLoan,homeLoanDetail,"
			+ "mortgageLoanDetail,proceedDedup,actionSave, finRvwRateApplFor,finGrcRvwRateApplFor,curDisbursementAmt,amount,"
			+ "exception,amountBD,amountUSD,maturity,availCommitAmount,guarantorIDTypeName,curFeeChargeAmt,"
			+ "name,lovCustCIFName,primaryExposure,secondaryExposure,guarantorExposure,worstStatus,status,sumPrimaryDetails,sumSecondaryDetails,"
			+ "sumGurantorDetails, isExtSource, commidityLoanDetails, limitStatus,fundsAvailConfirmed,pftIntact,adjTerms";

	/**
	 * setFinanceAsset Set AssetDetail to FinanceDetail, based on the assetCode specified for the FinanceType
	 * 
	 * @param financeDetail
	 * @param tableType
	 */
	public void setAssetDetail(FinanceDetail financeDetail, String tableType) {
		logger.debug("Entering ");

		String assetCode = financeDetail.getFinScheduleData().getFinanceType().getLovDescAssetCodeName();
		String finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
		Assets assest = Assets.valueOf(assetCode.toUpperCase());

		switch(assest) {
			case VEHICLE:
				CarLoanDetail  carLoanDetail;
				carLoanDetail = getCarLoanDetailService().getCarLoanDetailById(finReference);			
				financeDetail.setCarLoanDetail(carLoanDetail);
				break;
			case EDUCATON:
				getEducationalLoanService().setEducationalLoanDetails(financeDetail, tableType);
				break;
			case HOME:
				HomeLoanDetail homeLoanDetail;
				homeLoanDetail = getHomeLoanDetailService().getHomeLoanDetailById(finReference);
				financeDetail.setHomeLoanDetail(homeLoanDetail);
				break;
			case EQPMENT:
				MortgageLoanDetail mortgageLoanDetail;
				mortgageLoanDetail =  getMortgageLoanDetailService().getMortgageLoanDetailById(finReference);
				financeDetail.setMortgageLoanDetail(mortgageLoanDetail);
				break;
			case GOODS:
				List<GoodsLoanDetail> goodsLoanDetails;
				goodsLoanDetails =  getGoodsLoanDetailService().getGoodsLoanDetailById(finReference).getGoodsLoanDetailList();
				financeDetail.setGoodsLoanDetails(goodsLoanDetails);
				break;
			case GENGOODS:
				List<GenGoodsLoanDetail> genGoodsLoanDetails;
				genGoodsLoanDetails = getGenGoodsLoanDetailService().getGenGoodsLoanDetailById(finReference).getGenGoodsLoanDetailList();
				financeDetail.setGenGoodsLoanDetails(genGoodsLoanDetails);
				break;
			case COMIDITY:
				CommidityLoanHeader commidityLoanHeader = null; 
				commidityLoanHeader = getCommidityLoanDetailService().getCommidityLoanHeaderById(finReference);
				if(commidityLoanHeader != null) {
					financeDetail.setCommidityLoanHeader(commidityLoanHeader);
					financeDetail.setCommidityLoanDetails(commidityLoanHeader.getCommidityLoanDetails());
				}
				break;
			case SHARES:
				getSharesDetailService().setSharesDetails(financeDetail, tableType);
				break;
			default:
				break;
		}
		logger.debug("Leaving ");
	}

	/**
	 * Method to delete loan asset
	 * 
	 * @param finDetail
	 * @param tableType
	 * @param isWIF
	 * @return auditList
	 * */
	public List<AuditDetail> assetDeletion(FinanceDetail financeDetail, String tableType, String auditTranType) {
		logger.debug("Entering ");

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		
		CarLoanDetail carLoanDetail = financeDetail.getCarLoanDetail();
		EducationalLoan educationalLoan = financeDetail.getEducationalLoan();
		HomeLoanDetail homeLoanDetail = financeDetail.getHomeLoanDetail();
		List<GoodsLoanDetail>  goodsLoanDetails = financeDetail.getGoodsLoanDetails();
		List<GenGoodsLoanDetail>  genGoodsLoanDetails = financeDetail.getGenGoodsLoanDetails();
		MortgageLoanDetail mortgageLoanDetail = financeDetail.getMortgageLoanDetail();

		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();

		if (financeDetail.getCarLoanDetail() != null) {
			auditDetailList.add(getCarLoanDetailService().delete(carLoanDetail, tableType, auditTranType));
		}

		if (educationalLoan != null) {
			auditDetailList = getEducationalLoanService().delete(financeDetail, tableType, auditTranType);
		}

		if (homeLoanDetail != null) {
			auditDetailList.add(getHomeLoanDetailService().delete(homeLoanDetail, tableType, auditTranType));
		}

		if (mortgageLoanDetail != null) {
			auditDetailList.add(getMortgageLoanDetailService().delete(mortgageLoanDetail, tableType, auditTranType));
		}

		if (goodsLoanDetails != null && !goodsLoanDetails.isEmpty()) {
			auditDetailList.addAll(getGoodsLoanDetailService().delete(goodsLoanDetails, tableType, auditTranType));
		}

		if (genGoodsLoanDetails != null && !genGoodsLoanDetails.isEmpty()) {
			auditDetailList.addAll(getGenGoodsLoanDetailService().delete(genGoodsLoanDetails, tableType, auditTranType));
		}

		//Commidity Loan Detail
		CommidityLoanHeader commidityLoanHeader = financeDetail.getCommidityLoanHeader();
		if (commidityLoanHeader != null) {
			commidityLoanHeader.setCommidityLoanDetails(financeDetail.getCommidityLoanDetails());
			auditDetailList.addAll(getCommidityLoanDetailService().delete(commidityLoanHeader, tableType, auditTranType));
		}
		
		auditList.addAll(auditDetailList);
		logger.debug("Leaving ");
		return auditList;
	}
	
	/**
	 * Method to delete Co-borrower & Guarantors Details
	 * 
	 * @param finDetail
	 * @param tableType
	 * @return auditList
	 * */
	public List<AuditDetail> jointGuarantorDeletion(FinanceDetail financeDetail, String tableType, String auditTranType) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		List<JointAccountDetail> jointAccountDetails = financeDetail.getJountAccountDetailList();
		List<GuarantorDetail> guarantorDetails = financeDetail.getGurantorsDetailList();

		if (jointAccountDetails != null && !jointAccountDetails.isEmpty()) {
			auditDetails.addAll(getJointAccountDetailService().delete(jointAccountDetails, tableType, auditTranType));
		}

		if (guarantorDetails != null) {
			getGuarantorDetailService().delete(guarantorDetails, tableType, auditTranType);
		}


		logger.debug("Leaving ");
		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param contributorHeader
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	public List<AuditDetail> setContributorAuditData(FinContributorHeader contributorHeader,
			String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinContributorDetail(""));

		for (int i = 0; i < contributorHeader.getContributorDetailList().size(); i++) {

			FinContributorDetail contributorDetail = contributorHeader.getContributorDetailList().get(i);
			contributorDetail.setWorkflowId(contributorHeader.getWorkflowId());
			contributorDetail.setFinReference(contributorHeader.getFinReference());

			boolean isRcdType = false;

			if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if (method.equals("saveOrUpdate") && (isRcdType == true)) {
				contributorDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			contributorDetail.setRecordStatus(contributorHeader.getRecordStatus());
			contributorDetail.setUserDetails(contributorHeader.getUserDetails());
			contributorDetail.setLastMntOn(contributorHeader.getLastMntOn());

			if (!contributorDetail.getRecordType().equals("")) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						contributorDetail.getBefImage(), contributorDetail));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}
	
	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param detail
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	public List<AuditDetail> setDocumentDetailsAuditData(FinanceDetail detail,
			String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();		
		DocumentDetails object = new DocumentDetails();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (int i = 0; i < detail.getDocumentDetailsList().size(); i++) {
			DocumentDetails documentDetails = detail.getDocumentDetailsList().get(i);
			documentDetails.setWorkflowId(detail.getWorkflowId());
			boolean isRcdType = false;

			if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if (method.equals("saveOrUpdate") && (isRcdType == true)) {
				documentDetails.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			documentDetails.setRecordStatus(detail.getFinScheduleData().getFinanceMain().getRecordStatus());
			documentDetails.setUserDetails(detail.getFinScheduleData().getFinanceMain().getUserDetails());
			documentDetails.setLastMntOn(detail.getFinScheduleData().getFinanceMain().getLastMntOn());

			if (!documentDetails.getRecordType().equals("")) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						documentDetails.getBefImage(), documentDetails));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Common Method for Finance schedule list validation
	 * 
	 * @param list
	 * @param method
	 * @param userDetails
	 * @param lastMntON
	 * @return
	 * @throws InterruptedException
	 */
	public List<AuditDetail> getListAuditDetails(List<AuditDetail> list) {
		logger.debug("Entering");
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		if (list != null && list.size() > 0) {

			for (int i = 0; i < list.size(); i++) {

				String transType = "";
				String rcdType = "";
				Object object = ((AuditDetail) list.get(i)).getModelData();
				String[] fields = PennantJavaUtil.getFieldDetails(object, excludeFields);

				try {

					rcdType = object.getClass().getMethod("getRecordType").invoke(object).toString();

					if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						transType = PennantConstants.TRAN_ADD;
					} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
							|| rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						transType = PennantConstants.TRAN_DEL;
					} else {
						transType = PennantConstants.TRAN_UPD;
					}

					if (!(transType.equals(""))) {
						// check and change below line for Complete code
						Object befImg = object.getClass().getMethod("getBefImage", object.getClass().getClasses()).invoke(
								object, object.getClass().getClasses());
						auditDetailsList.add(new AuditDetail(transType, ((AuditDetail) list.get(i))
								.getAuditSeq(), fields[0], fields[1], befImg, object));
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}
		logger.debug("Leaving");
		return auditDetailsList;
	}


	/**
	 * Method For Preparing List of AuditDetails for Contributor Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	public List<AuditDetail> processingDocumentDetailsList(List<AuditDetail> auditDetails,
			String type, String finReference, FinanceMain financeMain) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			DocumentDetails documentDetails = (DocumentDetails) auditDetails.get(i).getModelData();
			if(!documentDetails.isDocIsCustDoc()){
				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = false;
				String rcdType = "";
				String recordStatus = "";
				if (type.equals("")) {
					approveRec = true;
					documentDetails.setRoleCode("");
					documentDetails.setNextRoleCode("");
					documentDetails.setTaskId("");
					documentDetails.setNextTaskId("");
				}
				documentDetails.setLastMntBy(financeMain.getLastMntBy());
				documentDetails.setWorkflowId(0);

				if(documentDetails.isDocIsCustDoc()){
					approveRec = true;
				}

				if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					deleteRecord = true;
				} else if (documentDetails.isNewRecord()) {
					saveRecord = true;
					if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (documentDetails.getRecordType().equalsIgnoreCase(
							PennantConstants.RCD_DEL)) {
						documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (documentDetails.getRecordType().equalsIgnoreCase(
							PennantConstants.RCD_UPD)) {
						documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}

				} else if (documentDetails.getRecordType().equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_NEW)) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (documentDetails.getRecordType().equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_UPD)) {
					updateRecord = true;
				} else if (documentDetails.getRecordType().equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_DEL)) {
					if (approveRec) {
						deleteRecord = true;
					} else if (documentDetails.isNew()) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				}

				if (approveRec) {
					rcdType = documentDetails.getRecordType();
					recordStatus = documentDetails.getRecordStatus();
					documentDetails.setRecordType("");
					documentDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (saveRecord) {
					if (StringUtils.trimToEmpty(documentDetails.getReferenceId()).equals("")) {
						documentDetails.setReferenceId(finReference);
					}
					getDocumentDetailsDAO().save(documentDetails, type);
				}

				if (updateRecord) {
					getDocumentDetailsDAO().update(documentDetails, type);
				}

				if (deleteRecord) {
					getDocumentDetailsDAO().delete(documentDetails, type);
				}

				if (approveRec) {
					documentDetails.setRecordType(rcdType);
					documentDetails.setRecordStatus(recordStatus);
				}
				auditDetails.get(i).setModelData(documentDetails);
			}else{
    			CustomerDocument custdoc =getCustomerDocument(documentDetails,financeMain);
    			if (custdoc.isNewRecord()) {
    				getCustomerDocumentDAO().save(custdoc,"");
    			} else {
    				getCustomerDocumentDAO().update(custdoc,"");
    			}
    		
            }
		}
		logger.debug("Leaving");
		return auditDetails;

	}


	private CustomerDocument getCustomerDocument(DocumentDetails documentDetails, FinanceMain financeMain){
		CustomerDocument customerDocument = null;
		if(financeMain != null){
			customerDocument = getCustomerDocumentDAO().getCustomerDocumentById(financeMain.getCustID(), documentDetails.getDocCategory(), "");
		}	

		if(customerDocument == null) {
			customerDocument = new CustomerDocument();
			customerDocument.setCustDocExpDate(documentDetails.getCustDocExpDate());
			customerDocument.setCustDocIsAcrive(documentDetails.isCustDocIsAcrive());
			customerDocument.setCustDocIssuedCountry(documentDetails.getCustDocIssuedCountry());
			customerDocument.setLovDescCustDocIssuedCountry(documentDetails.getLovDescCustDocIssuedCountry());
			customerDocument.setCustDocIssuedOn(documentDetails.getCustDocIssuedOn());
			customerDocument.setCustDocIsVerified(documentDetails.isCustDocIsVerified());
			customerDocument.setCustDocRcvdOn(documentDetails.getCustDocRcvdOn());
			customerDocument.setCustDocSysName(documentDetails.getCustDocSysName());
			customerDocument.setCustDocTitle(documentDetails.getCustDocTitle());
			customerDocument.setCustDocVerifiedBy(documentDetails.getCustDocVerifiedBy());
			customerDocument.setNewRecord(true);
		} 

		customerDocument.setCustID(financeMain.getCustID());
		customerDocument.setLovDescCustCIF(financeMain.getLovDescCustCIF());
		customerDocument.setLovDescCustShrtName(financeMain.getLovDescCustShrtName());

		customerDocument.setCustDocImage(documentDetails.getDocImage());
		customerDocument.setCustDocImage(documentDetails.getDocImage());
		customerDocument.setCustDocType(documentDetails.getDoctype());
		customerDocument.setCustDocCategory(documentDetails.getDocCategory());
		customerDocument.setCustDocName(documentDetails.getDocName());
		customerDocument.setLovDescCustDocCategory(documentDetails.getLovDescDocCategoryName());

		customerDocument.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		customerDocument.setRecordType("");
		customerDocument.setUserDetails(documentDetails.getUserDetails());
		customerDocument.setVersion(documentDetails.getVersion());
		customerDocument.setLastMntBy(documentDetails.getLastMntBy());
		customerDocument.setLastMntOn(documentDetails.getLastMntOn());
		return customerDocument;
	}

	/**
	 * Method for Execute posting Details on Core Banking Side
	 * @param auditHeader
	 * @param curBDay
	 * @return
	 * @throws AccountNotFoundException
	 */
	public AuditHeader executeAccountingProcess(AuditHeader auditHeader, Date curBDay) throws AccountNotFoundException{
		logger.debug("Entering");

		long linkedTranId = Long.MIN_VALUE;
		List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();
		AEAmountCodes amountCodes = null;
		BigDecimal cmtPostAmt = BigDecimal.ZERO;
		Commitment commitment = null;

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		BeanUtils.copyProperties((FinanceDetail) auditHeader.getAuditDetail().getModelData(), financeDetail);
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String productType = financeMain.getLovDescProductCodeName();
		
		Date dateValueDate = financeMain.getFinStartDate();
		if(!financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
			dateValueDate = curBDay;
		}

		DataSet dataSet = AEAmounts.createDataSet(financeMain,
				financeDetail.getAccountingEventCode(), dateValueDate,dateValueDate);

		//Profit Details Data Fetching for Maintenance Record, otherwise Profit Detail object not Exists
		FinanceProfitDetail pftDetail = null;
		if(financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
			pftDetail = new FinanceProfitDetail();
		}else{
			pftDetail = getProfitDetailsDAO().getFinProfitDetailsById(financeMain.getFinReference());
		}

		amountCodes = AEAmounts.procAEAmounts(financeMain, financeDetail.getFinScheduleData()
				.getFinanceScheduleDetails(), pftDetail, curBDay);

		try {
			if (!financeDetail.getFinScheduleData().getFinanceType().isAllowRIAInvestment()) {

				Map<String, FeeRule> feeRuleDetailsMap = null;
				if (financeDetail.getFinScheduleData().getFeeRules() != null
						&& financeDetail.getFinScheduleData().getFeeRules().size() > 0) {

					feeRuleDetailsMap = new HashMap<String, FeeRule>();
					for (FeeRule feeRule : financeDetail.getFinScheduleData().getFeeRules()) {
						if (!feeRuleDetailsMap.containsKey(feeRule.getFeeCode())) {
							feeRuleDetailsMap.put(feeRule.getFeeCode(), feeRule);
						}
					}
				}
				
				if(financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){

					List<FinanceDisbursement> disbursementDetails = financeDetail.getFinScheduleData().getDisbursementDetails();

					// Loop Repetation for Multiple Disbursement
					if(disbursementDetails != null && disbursementDetails.size() > 0){

						Map<Long, BigDecimal> advPendingDueMap = new HashMap<Long, BigDecimal>();			

						for (FinanceDisbursement disbursement : financeDetail.getFinScheduleData().getDisbursementDetails()) {

							if (disbursement.getDisbDate().after((Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR))) {
								if("B".equals(disbursement.getDisbType())){
									continue;						
								}
								dataSet.setFinEvent("ADDDBSF");
							} else {
								dataSet.setFinEvent("ADDDBSP");
							}

							if(!StringUtils.trimToEmpty(disbursement.getDisbAccountId()).equals("")){
								dataSet.setDisburseAccount(disbursement.getDisbAccountId());
							}
							dataSet.setDisburseAmount(disbursement.getDisbAmount());
							dataSet.setCurDisbRet(disbursement.getDisbRetAmount());
							dataSet.setNetRetDue(disbursement.getNetRetDue());
							dataSet.setClaimAmt(disbursement.getDisbClaim());
							dataSet.setGrcPftTillNow(calculateTillGrcProfit(financeDetail.getFinScheduleData(), disbursement.getDisbDate()));

							//Net Customer Advance Amount Calculation
							BigDecimal netAdvDue = BigDecimal.ZERO;
							if("B".equals(StringUtils.trimToEmpty(disbursement.getDisbType()))){
								dataSet.setFinEvent("ISTBILL");//TODO--- Hard code FIXME

								if(advPendingDueMap.containsKey(disbursement.getDisbBeneficiary())){
									netAdvDue = advPendingDueMap.get(disbursement.getDisbBeneficiary());
								}

								BigDecimal balAdv = netAdvDue.subtract(disbursement.getNetAdvDue());
								if(balAdv.compareTo(BigDecimal.ZERO) > 0){
									dataSet.setAdvDue(balAdv);
									balAdv = BigDecimal.ZERO;
								}else{
									dataSet.setAdvDue(BigDecimal.ZERO);
								}

								advPendingDueMap.put(disbursement.getDisbBeneficiary(), balAdv);

							}else if("A".equals(StringUtils.trimToEmpty(disbursement.getDisbType()))){

								if(advPendingDueMap.containsKey(disbursement.getDisbBeneficiary())){
									netAdvDue = advPendingDueMap.get(disbursement.getDisbBeneficiary());
								}
								netAdvDue = netAdvDue.add(disbursement.getNetAdvDue());
								advPendingDueMap.put(disbursement.getDisbBeneficiary(), netAdvDue);
							}
							
							//Fee Rules Checking with Existing Fees with in Process & Latest Accounting Entry Details
							boolean reExecuteFees = false;
							List<String> feeCodeList = getTransactionEntryDAO().getListFeeCodes(Long.valueOf(getAccSetId(dataSet.getFinEvent(), 
									financeDetail.getFinScheduleData().getFinanceType())));
							if(feeCodeList == null || feeCodeList.isEmpty()){
								if(feeRuleDetailsMap != null && feeRuleDetailsMap.size() >0){
									reExecuteFees = true;
								}
							}else{
								for (int i = 0; i < feeCodeList.size(); i++) {

									String[] feelist = null;
									if(feeCodeList.get(i).contains(",")){
										feelist = feeCodeList.get(i).split(",");
									}

									if(feelist != null && feelist.length > 0){
										for (int j = 0; j < feelist.length; j++) {
											if(feeRuleDetailsMap == null || !feeRuleDetailsMap.containsKey(feelist[j].trim())){
												reExecuteFees = true;
												break;
											}
										}
									}else{
										if(feeRuleDetailsMap != null && !feeRuleDetailsMap.containsKey(feeCodeList.get(i).trim())){
											reExecuteFees = true;
											break;
										}
									}
								}
							}
							
							//If Fees Mismatch with Existing Executed Fees to the Accounting Entry Fee Details
							if(reExecuteFees){
								ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
								errorDetails.add(new ErrorDetails("Accounting Engine", PennantConstants.ERR_UNDEF, "E",
										PennantJavaUtil.getLabel("label_mismatchFeeswithAccounting"), new String[] {}, new String[] {}));
								auditHeader.setErrorList(errorDetails);
								logger.debug("Leaving");
								return auditHeader;
							}

							List<ReturnDataSet> returnSetEntries = getEngineExecution().getAccEngineExecResults(dataSet, amountCodes, "Y",
									feeRuleDetailsMap,false, financeDetail.getFinScheduleData().getFinanceType());

							list.addAll(returnSetEntries);
						}
					}
					// Finance GraceEnd Posting Details
					if (StringUtils.trimToEmpty(productType).equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_IJARAH)) {
						if(financeMain != null && StringUtils.trimToEmpty(financeMain.getRecordType()).equals(PennantConstants.RECORD_TYPE_NEW)){
							if(financeMain.getGrcPeriodEndDate() != null && financeMain.getGrcPeriodEndDate().compareTo(curBDay) <= 0){
								List<ReturnDataSet> returnSetEntries = null;
								dataSet.setFinEvent("GRACEEND");
								dataSet.setFeeAmount(financeMain.getFeeChargeAmt() == null ?  BigDecimal.ZERO : financeMain.getFeeChargeAmt());
								returnSetEntries = getEngineExecution().getAccEngineExecResults(dataSet, amountCodes, "Y",
										null,false, financeDetail.getFinScheduleData().getFinanceType());	
								list.addAll(returnSetEntries);
							}
						}
					}

				}else{

					//Accounting Execution for Maintenance
					list.addAll(getEngineExecution().getAccEngineExecResults(dataSet, amountCodes, "Y",
							feeRuleDetailsMap,false, financeDetail.getFinScheduleData().getFinanceType(),financeDetail.getPremiumDetail()));
				}

				//Stage Accounting Process Execution
				if (financeDetail.getStageAccountingList() != null && financeDetail.getStageAccountingList().size() > 0) {
					dataSet.setFinEvent("STAGE");
					list.addAll(getEngineExecution().getStageExecResults(dataSet, amountCodes, "Y",
							financeMain.getRoleCode(), feeRuleDetailsMap, financeDetail.getFinScheduleData().getFinanceType(), financeDetail.getPremiumDetail()));
				}

			} else {
				List<AEAmountCodesRIA> riaDetailList = new ArrayList<AEAmountCodesRIA>();
				List<FinContributorDetail> contributorDetailList = null;

				financeDetail.getFinContributorHeader().getContributorDetailList();

				if(financeDetail.getFinContributorHeader() != null) {
					contributorDetailList = financeDetail.getFinContributorHeader().getContributorDetailList();
					if (contributorDetailList != null && !contributorDetailList.isEmpty()) {
						riaDetailList = getEngineExecutionRIA().prepareRIADetails(contributorDetailList, dataSet.getFinReference());
					}
				}

				if(financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){

					// Loop Repetition for Multiple Disbursement
					List<FinanceDisbursement> disbursementDetails = financeDetail.getFinScheduleData().getDisbursementDetails();
					List<ReturnDataSet> returnSetEntries = null;
					if(disbursementDetails != null && !disbursementDetails.isEmpty()){

						for (FinanceDisbursement disbursement : disbursementDetails) {

							if(disbursement.getDisbAmount().compareTo(BigDecimal.ZERO) > 0){

								if (disbursement.getDisbDate().after((Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR))) {
									dataSet.setFinEvent("ADDDBSF");
								} else {
									dataSet.setFinEvent("ADDDBSP");
								}

								dataSet.setDisburseAccount(disbursement.getDisbAccountId());
								dataSet.setDisburseAmount(disbursement.getDisbAmount());
								dataSet.setCurDisbRet(disbursement.getDisbRetAmount());
								dataSet.setNetRetDue(disbursement.getNetRetDue());
								returnSetEntries = getEngineExecutionRIA().getAccEngineExecResults(dataSet, amountCodes, "Y", riaDetailList);

								list.addAll(returnSetEntries);
							}
						}
					}
					// Finance GraceEnd Posting Details
					if (StringUtils.trimToEmpty(productType).equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_IJARAH)) {
						if(financeMain != null && StringUtils.trimToEmpty(financeMain.getRecordType()).equals(PennantConstants.RECORD_TYPE_NEW)){
							if(financeMain.getGrcPeriodEndDate() != null && financeMain.getGrcPeriodEndDate().compareTo(curBDay) <= 0){
								DataSet dataset = AEAmounts.createDataSet(financeMain, financeDetail.getAccountingEventCode(), financeMain.getFinStartDate(), financeMain.getFinStartDate());
								dataset.setFinEvent("GRACEEND");
								list.addAll(getEngineExecutionRIA().getAccEngineExecResults(dataset, amountCodes, "Y", riaDetailList));
							}
						}
					}
				}else{

					//Accounting Execution for Maintainance
					list.addAll( getEngineExecutionRIA().getAccEngineExecResults(dataSet, amountCodes, "Y", riaDetailList));
				}
			}

			// Finance Commitment Reference Posting Details
			if (!StringUtils.trimToEmpty(financeMain.getFinCommitmentRef()).equals("") && 
					financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				commitment = getCommitmentDAO().getCommitmentById(financeMain.getFinCommitmentRef(), "");

				AECommitment aeCommitment = new AECommitment();
				aeCommitment.setCMTAMT(commitment.getCmtAmount());
				aeCommitment.setCHGAMT(commitment.getCmtCharges());
				aeCommitment.setDISBURSE(CalculationUtil.getConvertedAmount(financeMain.getFinCcy(), commitment.getCmtCcy(),
						financeMain.getFinAmount().subtract(financeMain.getDownPayment() == null ? BigDecimal.ZERO : financeMain.getDownPayment())));
				aeCommitment.setRPPRI(BigDecimal.ZERO);

				list.addAll(getEngineExecution().getCommitmentExecResults(aeCommitment, commitment, "CMTDISB", "Y", null));
			}

		} catch (Exception e) {
			logger.error(e);
			ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
			errorDetails.add(new ErrorDetails("Accounting Engine", PennantConstants.ERR_UNDEF, "E",
					"Accounting Engine Failed to Create Postings:"+e.getMessage(), new String[] {}, new String[] {}));
			auditHeader.setErrorList(errorDetails);
			list = null;
		}

		if(list != null && !list.isEmpty() ) {

			// Method for validating Postings with interface program and
			// return results
			if (list.get(0).getLinkedTranId() == Long.MIN_VALUE) {
				linkedTranId = getPostingsDAO().getLinkedTransId(list.get(0));
			} else {
				linkedTranId = list.get(0).getLinkedTranId();
			}
			
			//Method for Checking for Reverse Calculations Based upon Negative Amounts
			for (ReturnDataSet returnDataSet : list) {
				
				returnDataSet.setLinkedTranId(linkedTranId);

				if (returnDataSet.getPostAmount().compareTo(BigDecimal.ZERO) < 0) {

					String tranCode = returnDataSet.getTranCode();
					String revTranCode = returnDataSet.getRevTranCode();
					String debitOrCredit = returnDataSet.getDrOrCr();

					returnDataSet.setTranCode(revTranCode);
					returnDataSet.setRevTranCode(tranCode);

					returnDataSet.setPostAmount(returnDataSet.getPostAmount().negate());

					if (debitOrCredit.equals("C")) {
						returnDataSet.setDrOrCr("D");
					} else {
						returnDataSet.setDrOrCr("C");
					}
				}
			}

			//Core Banking Posting Process call
			list = getPostingsInterfaceService().doFillPostingDetails(list, financeMain.getFinBranch(), linkedTranId, "Y");

			if (list != null && list.size() > 0) {
				ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
				boolean isFetchFinAc = false;
				boolean isFetchCistIntAc = false;
				for (int i = 0; i < list.size(); i++) {
					ReturnDataSet set = list.get(i);
					set.setLinkedTranId(linkedTranId);
					set.setPostDate(curBDay);
					if (!("0000".equals(StringUtils.trimToEmpty(set.getErrorId())) || "".equals(StringUtils.trimToEmpty(set.getErrorId())))) {
						errorDetails.add(new ErrorDetails(set.getAccountType(), set.getErrorId(), "E",
								set.getErrorMsg() + " " + PennantApplicationUtil.formatAccountNumber(set.getAccount()), new String[] {}, new String[] {}));
					} else {
						set.setPostStatus("S");
						if ("CMTDISB".equals(set.getFinEvent())) {
							cmtPostAmt = set.getPostAmount();
						}
					}

					if(financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
						if (!isFetchFinAc && set.getAccountType().equals(
								financeDetail.getFinScheduleData().getFinanceType().getFinAcType())) {
							isFetchFinAc = true;
							financeMain.setFinAccount(set.getAccount());
						}
						if (!isFetchCistIntAc && set.getAccountType().equals(
								financeDetail.getFinScheduleData().getFinanceType().getPftPayAcType())) {
							isFetchCistIntAc = true;
							financeMain.setFinCustPftAccount(set.getAccount());
						}
					}
				}
				auditHeader.setErrorList(errorDetails);
			}
		}

		if (auditHeader.getErrorMessage() == null || auditHeader.getErrorMessage().size() == 0) {
			
			// Past due Deferment Details Process
			if(!financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW) && 
					(StringUtils.trimToEmpty(financeMain.getRcdMaintainSts()).equals(PennantConstants.ADD_DEFF) ||
					StringUtils.trimToEmpty(financeMain.getRcdMaintainSts()).equals(PennantConstants.CHGFRQ))){
				
				List<Date> pastdueDefDateList = new ArrayList<Date>();
				
				if(StringUtils.trimToEmpty(financeMain.getRcdMaintainSts()).equals(PennantConstants.ADD_DEFF)){
					List<DefermentDetail> curDefList= financeDetail.getFinScheduleData().getDefermentDetails();

					//Fetching Existing Deferment Details
					List<DefermentDetail> prvDefList= getDefermentDetailDAO().getDefermentDetails(financeMain.getFinReference(), "", false);

					// Writing into Map as of Schedule Date wise for Easy Checking of Current Deferment Cases
					Map<Date, DefermentDetail> prvDefMap = new HashMap<Date, DefermentDetail>();
					for (int i = 0; i < prvDefList.size(); i++) {
						if(!prvDefMap.containsKey(prvDefList.get(i).getDeferedSchdDate())){
							prvDefMap.put(prvDefList.get(i).getDeferedSchdDate(), prvDefList.get(i));
						}else{
							prvDefMap.remove(prvDefList.get(i).getDeferedSchdDate());
							prvDefMap.put(prvDefList.get(i).getDeferedSchdDate(), prvDefList.get(i));
						}
					}

					prvDefList = null;

					//Cross Check for Past due Deferments by Current Deferment Details with Existed List on Main table
					for (int i = 0; i < curDefList.size(); i++) {
						if(!prvDefMap.containsKey(curDefList.get(i).getDeferedSchdDate())){
							if(curDefList.get(i).getDeferedSchdDate().compareTo(curBDay) < 0){
								pastdueDefDateList.add(curDefList.get(i).getDeferedSchdDate());
							}
						}
					}
				}
				
				if(StringUtils.trimToEmpty(financeMain.getRcdMaintainSts()).equals(PennantConstants.CHGFRQ)){
					
					List<FinanceScheduleDetail> schDetailList= financeDetail.getFinScheduleData().getFinanceScheduleDetails();
					List<Date> schDateList = new ArrayList<Date>();
					for (int i = 0; i < schDetailList.size(); i++) {
	                    FinanceScheduleDetail curSchd = schDetailList.get(i);
						if(!(curSchd.isRepayOnSchDate() ||
								(curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0))){
							continue;
						}
						if(curSchd.getSchDate().compareTo(curBDay) > 0){
							break;
						}
						schDateList.add(curSchd.getSchDate());
                    }
					
					//Fetch Past Due Modified Schedule Details List
					if(!schDateList.isEmpty()){
						pastdueDefDateList = getFinODDetailsDAO().getMismatchODDates(financeMain.getFinReference(), schDateList);
					}
				}
				
				// Process on Overdue Details with Past due Deferment Schedules
				if(!pastdueDefDateList.isEmpty()){
					
					//Fetching Overdue Penalty Recovery Details, If any Paid either Partially or Fully
					BigDecimal totPenaltyPaid = getRecoveryDAO().getPaidPenaltiesbySchDates(financeMain.getFinReference(), pastdueDefDateList);
					
					// DataSet Creation
					dataSet.setFinEvent("LATEPAY");
					dataSet.setNewRecord(false);

					// AmountCodes Preparation-- Overdue Paid Amount Reversal for 
					amountCodes.setPENALTY(totPenaltyPaid.negate());
					amountCodes.setWAIVER(BigDecimal.ZERO);
					
					// Accounting Set Execution to get Posting Details List
					Date dateAppDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR).toString());
					List<Object> resultList = null;
					try {
	                    resultList = getPostingsPreparationUtil().processPostingDetails(dataSet, amountCodes, false, false, "Y", dateAppDate, false, Long.MIN_VALUE);
                    } catch (IllegalAccessException e) {
	                   logger.error(e.getMessage());
                    } catch (InvocationTargetException e) {
                       logger.error(e.getMessage());
                    }
					
					//If Reversal Posting for Overdue Recover Details are Successful 
					if(resultList != null & (Boolean)resultList.get(0)){
						
						//Save History of Overdue Details and Overdue Recovery Details
						getRecoveryDAO().saveODDeferHistory(financeMain.getFinReference(), pastdueDefDateList);
						getFinODDetailsDAO().saveODDeferHistory(financeMain.getFinReference(), pastdueDefDateList);
						
						//Get Maximum Overdue Days with in Deletion Past Due Terms
						int maxODDays = getFinODDetailsDAO().getMaxODDaysOnDeferSchd(financeMain.getFinReference(), pastdueDefDateList);
						
						//Delete Overdue Recovery Details & Overdue Details
						getRecoveryDAO().deleteODDeferHistory(financeMain.getFinReference(), pastdueDefDateList);
						getFinODDetailsDAO().deleteODDeferHistory(financeMain.getFinReference(), pastdueDefDateList);
						
						//Finance Status Insertion newly with today's Value date on this Finance
						String curFinStatus = getCustomerStatusCodeDAO().getFinanceStatus(financeMain.getFinReference(), true);
						String finStsReason = PennantConstants.FINSTSRSN_MANUAL;
						boolean isStsChanged = false;
						
						if(!financeMain.getFinStatus().equals(curFinStatus)){
							isStsChanged = true;
						}
						
						//Finance Status Details insertion, if status modified then change to High Risk Level
						if(isStsChanged){
							FinStatusDetail statusDetail = new FinStatusDetail();
							statusDetail.setFinReference(financeMain.getFinReference());
							statusDetail.setValueDate(dateValueDate);
							statusDetail.setCustId(financeMain.getCustID());
							statusDetail.setFinStatus(curFinStatus);			
							statusDetail.setFinStatusReason(finStsReason);			
							
							getFinStatusDetailDAO().saveOrUpdateFinStatus(statusDetail);
						}
						
						financeMain.setFinStatus(curFinStatus);
						
						//First OD Date and Last OD Date checking (Setting NULL in case past due Deferment Schedule dates and OD Dates are equal )
						//& Rebuild AmountCodes preparation
						for (int i = 0; i < pastdueDefDateList.size(); i++) {
	                        if(pftDetail.getFirstODDate() != null && pftDetail.getFirstODDate().compareTo(pastdueDefDateList.get(i)) == 0){
	                        	pftDetail.setFirstODDate(null);
	                        }
	                        if(pftDetail.getLastODDate() != null && pftDetail.getLastODDate().compareTo(pastdueDefDateList.get(i)) == 0){
	                        	pftDetail.setLastODDate(null);
	                        }
                        }
						
						if(StringUtils.trimToEmpty(financeMain.getRcdMaintainSts()).equals(PennantConstants.ADD_DEFF)){
							suspenseCheckProcess(financeMain, PennantConstants.ADD_DEFF, dateValueDate, 
									financeDetail.getFinScheduleData().getFinanceType().isAllowRIAInvestment(), curFinStatus, maxODDays);
						}
						
						//AEAmounts Recalculation after Removing Overdue Details 
						amountCodes = AEAmounts.procAEAmounts(financeMain, financeDetail.getFinScheduleData().getFinanceScheduleDetails(), pftDetail, curBDay);
					}else{
						
						String error = (String) resultList.get(2);
						
						ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
						errorDetails.add(new ErrorDetails("", error.substring(0, error.indexOf('-')), "E",error, new String[] {}, new String[] {}));
						auditHeader.setErrorList(errorDetails);
					}
				}
			}
					
			if (auditHeader.getErrorMessage() == null || auditHeader.getErrorMessage().size() == 0) {

				// save Postings
				if (list != null && !list.isEmpty()) {
					getPostingsDAO().saveBatch(list, "", false);
				}

				//Update disbursement details
				if(financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					List<FinanceDisbursement> financeDisbursements = financeDetail.getFinScheduleData().getDisbursementDetails();
					for (FinanceDisbursement disbursement : financeDisbursements) {
						disbursement.setLinkedTranId(linkedTranId);
					}
				}

				// Save/Update Finance Profit Details
				FinanceProfitDetail profitDetail = doSave_PftDetails(amountCodes, financeDetail.getFinScheduleData(), pftDetail);

				//Account Details Update
				getAccountProcessUtil().procAccountUpdate(list, profitDetail.getTdPftAccrued());

				if (commitment != null && cmtPostAmt.compareTo(BigDecimal.ZERO) > 0) {
					getCommitmentDAO().updateCommitmentAmounts(commitment.getCmtReference(), cmtPostAmt,commitment.getCmtExpDate());
					CommitmentMovement movement = prepareCommitMovement(commitment, financeMain,
							cmtPostAmt, linkedTranId);
					if (movement != null) {
						getCommitmentMovementDAO().save(movement, "");
					}
				}
			}
		}

		logger.debug("Leaving");
		return auditHeader;
	}
	
	/**
	 * Method for Processing Suspense Preparation after Finance Maintenance
	 * @param financeMain
	 * @param processType
	 * @param dateValueDate
	 * @param alwRIA
	 * @param curFinsts
	 * @param maxODDays
	 * @throws AccountNotFoundException
	 */
	protected void suspenseCheckProcess(FinanceMain financeMain, String processType, Date dateValueDate, boolean alwRIA, 
			String curFinsts, int maxODDays) throws AccountNotFoundException{
		
		boolean chkSuspProcess = false;
		
		//Checking Conditions for Suspense Calculations
		if(processType.equals(PennantConstants.ADD_DEFF)){
			
			//Get Current Maximum Overdue Days after Deletion Past Due Terms
			int curMaxODDays = getFinODDetailsDAO().getMaxODDaysOnDeferSchd(financeMain.getFinReference(), null);
			if(curMaxODDays < maxODDays){
				chkSuspProcess = true;
			}
			
		}else{
			chkSuspProcess = true;
		}
		
		if(chkSuspProcess){

			// Deletion of Suspense Details Depends on Releases
			getFinanceSuspHeadDAO().updateSuspFlag(financeMain.getFinReference());

			//Get Maximum Days Overdue Details Object with Overdue Amount
			FinODDetails odDetail = getFinODDetailsDAO().getMaxDaysFinODDetails(financeMain.getFinReference());

			// Recreation of Suspense Details , if Finance is in Suspense with Current Max Overdue Days
			if(odDetail != null){
				FinRepayQueue repayQueue = new FinRepayQueue();
				repayQueue.setFinReference(financeMain.getFinReference());
				repayQueue.setCustomerID(financeMain.getCustID());
				repayQueue.setBranch(financeMain.getFinBranch());
				repayQueue.setFinType(financeMain.getFinType());
				repayQueue.setRpyDate(odDetail.getFinODSchdDate());
				repayQueue.setFinRpyFor(odDetail.getFinODFor());

				try {
					getSuspensePostingUtil().suspensePreparation(financeMain, repayQueue, dateValueDate, alwRIA, true);
				} catch (IllegalAccessException e) {
					logger.error(e.getMessage());
				} catch (InvocationTargetException e) {
					logger.error(e.getMessage());
				}
			}
		}

		// Customer Status Change Date & Status Update after Suspense Details updations
		String custSts = getCustomerDAO().getCustWorstStsbyCurFinSts(financeMain.getCustID(), financeMain.getFinReference(), curFinsts); 
		List<Long> custIdList = new ArrayList<Long>(1);
		custIdList.add(financeMain.getCustID());
		List<FinStatusDetail> suspDateSts = getFinanceSuspHeadDAO().getCustSuspDate(custIdList);

		Date suspFromdate = null;
		if(suspDateSts != null && !suspDateSts.isEmpty()){
			suspFromdate = suspDateSts.get(0).getValueDate();
		}

		FinStatusDetail statusDetail = new FinStatusDetail();
		List<FinStatusDetail> custStatuses = new ArrayList<FinStatusDetail>(1);
		statusDetail.setCustId(financeMain.getCustID());
		statusDetail.setFinStatus(custSts);
		statusDetail.setValueDate(suspFromdate);
		custStatuses.add(statusDetail);

		getFinStatusDetailDAO().updateCustStatuses(custStatuses);
		
	}
	
	/**
	 * Method For Fetching Accounting Set ID Depends on Finance Event
	 * @param finEvent
	 * @return
	 */
	private String getAccSetId(String finEvent, FinanceType financeType){
		
		//Execute entries depend on Finance Event
		String accountingSetId = "";
		FINEVENT eventCode = FINEVENT.valueOf(finEvent);

		switch (eventCode) {
	        case ADDDBSF:
	        	accountingSetId = financeType.getFinAEAddDsbFD();
		        break;
	        case ADDDBSN:
	        	accountingSetId = financeType.getFinAEAddDsbFDA();
	        	break;
	        case ADDDBSP:
	        	accountingSetId = financeType.getFinAEAddDsbOD();
	        	break;
	        case DEFRPY:
	        	accountingSetId = financeType.getFinDefRepay();
	        	break;
	        case DEFFRQ:
	        	accountingSetId = financeType.getFinDeffreq();
	        	break;
	        case EARLYPAY:
	        	accountingSetId = financeType.getFinAEEarlyPay();
	        	break;
	        case EARLYSTL:
	        	accountingSetId = financeType.getFinAEEarlySettle();
	        	break;
	        case LATEPAY:
	        	accountingSetId = financeType.getFinLatePayRule();
	        	break;
	        case RATCHG:
	        	accountingSetId = financeType.getFinAERateChg();
	        	break;
	        case SCDCHG:
	        	accountingSetId = financeType.getFinSchdChange();
	        	break;
	        case COMPOUND:
	        	accountingSetId = financeType.getFinAECapitalize();
	        	break;
	        default:
	        	accountingSetId = "0";
		        break;
	    }
		
		return accountingSetId;
	}

	protected AuditHeader executeStageAccounting(AuditHeader auditHeader, List<ReturnDataSet> list) throws AccountNotFoundException{
		logger.debug("Entering");

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		long linkedTranId = Long.MIN_VALUE;
		AEAmountCodes amountCodes = null;
		Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);

		DataSet dataSet = AEAmounts.createDataSet(financeMain, "STAGE",
				financeMain.getFinStartDate(), financeMain.getFinStartDate());

		amountCodes = AEAmounts.procAEAmounts(financeMain, financeDetail.getFinScheduleData().getFinanceScheduleDetails(),
				new FinanceProfitDetail(), financeMain.getFinStartDate());

		try {
			list.addAll(getEngineExecution().getStageExecResults(dataSet, amountCodes, "Y",
					financeMain.getRoleCode(), null, financeDetail.getFinScheduleData().getFinanceType(), financeDetail.getPremiumDetail()));
		} catch (Exception e) {
			logger.error(e);
			ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
			errorDetails.add(new ErrorDetails("Accounting Engine", PennantConstants.ERR_UNDEF, "E",
					"Accounting Engine Failed to Create Postings:"+e.getMessage(), new String[] {}, new String[] {}));
			auditHeader.setErrorList(errorDetails);
		}

		// Method for validating Postings with interface program and
		// return results
		if (list.get(0).getLinkedTranId() == Long.MIN_VALUE) {
			linkedTranId = getPostingsDAO().getLinkedTransId(list.get(0));
		} else {
			linkedTranId = list.get(0).getLinkedTranId();
		}
		list = getPostingsInterfaceService().doFillPostingDetails(list,
				financeMain.getFinBranch(), linkedTranId, "Y");
		if (list != null && list.size() > 0) {
			ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
			for (int i = 0; i < list.size(); i++) {
				ReturnDataSet set = list.get(i);
				set.setLinkedTranId(linkedTranId);
				set.setPostDate(curBDay);
				if (!("0000".equals(set.getErrorId()) || "".equals(set.getErrorId()))) {
					errorDetails.add(new ErrorDetails(set.getAccountType(), set.getErrorId(),
							"E", set.getErrorMsg(), new String[] {}, new String[] {}));
				} else {
					set.setPostStatus("S");
				}
			}
			auditHeader.setErrorList(errorDetails);
		}
		logger.debug("Leaving");
		return auditHeader;
	}


	/**
	 * Method for Calculate Grace Profit Till 
	 * @return
	 */
	private BigDecimal calculateTillGrcProfit(FinScheduleData scheduleData, Date disbDate){
		logger.debug("Entering");

		BigDecimal totGrcPftTillNow = BigDecimal.ZERO;
		List<FinanceScheduleDetail> list = scheduleData.getFinanceScheduleDetails();
		for (FinanceScheduleDetail curSchdl : list) {
			if(curSchdl.getSchDate().compareTo(disbDate) <= 0){
				totGrcPftTillNow = totGrcPftTillNow.add(curSchdl.getProfitCalc());
			}else{
				break;
			}
		}
		logger.debug("Leaving");
		return totGrcPftTillNow;
	}


	/**
	 * Method for Add a Movement Entry for Commitment Disbursement Event
	 * 
	 * @param commitment
	 * @param financeMain
	 * @param postAmount
	 * @param linkedtranId
	 * @return
	 */
	public CommitmentMovement prepareCommitMovement(Commitment commitment,
			FinanceMain financeMain, BigDecimal postAmount, long linkedtranId) {

		CommitmentMovement movement = new CommitmentMovement();
		Date curBussDate =(Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);

		movement.setCmtReference(commitment.getCmtReference());
		movement.setFinReference(financeMain.getFinReference());
		movement.setFinBranch(financeMain.getFinBranch());
		movement.setFinType(financeMain.getFinType());
		movement.setMovementDate(curBussDate);
		movement.setMovementOrder(1);
		movement.setMovementType("DA");
		movement.setMovementAmount(postAmount);
		movement.setCmtAmount(commitment.getCmtAmount());
		movement.setCmtUtilizedAmount(commitment.getCmtUtilizedAmount().add(postAmount));
		if(commitment.getCmtExpDate().compareTo(curBussDate) < 0){
			movement.setCmtAvailable(BigDecimal.ZERO);
		}else{
			movement.setCmtAvailable(commitment.getCmtAvailable().subtract(postAmount));
		}
		movement.setCmtCharges(BigDecimal.ZERO);
		movement.setLinkedTranId(linkedtranId);
		movement.setVersion(1);
		movement.setLastMntBy(financeMain.getLastMntBy());
		movement.setLastMntOn(financeMain.getLastMntOn());
		movement.setRecordStatus("Approved");
		movement.setRoleCode("");
		movement.setNextRoleCode("");
		movement.setTaskId("");
		movement.setNextTaskId("");
		movement.setRecordType("");
		movement.setWorkflowId(0);

		return movement;

	}

	/**
	 * Method to save profit details
	 * 
	 * @param finScheduleData
	 * */
	private FinanceProfitDetail doSave_PftDetails(AEAmountCodes aeAmountCodes,
			FinScheduleData finScheduleData, FinanceProfitDetail profitDetail) {
		logger.debug("Entering");
		
		// Save/Update Finance Profit Details
		FinanceProfitDetail finProfitDetails = profitDetail;

		Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();
		List<FeeRule> feeRuleList = finScheduleData.getFeeRules();

		if(financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
			
			finProfitDetails.setFinReference(financeMain.getFinReference());
			finProfitDetails.setFinBranch(financeMain.getFinBranch());
			finProfitDetails.setFinType(financeMain.getFinType());
			finProfitDetails.setRepayFrq(financeMain.getRepayFrq());
			finProfitDetails.setCustCIF(financeMain.getLovDescCustCIF());
			finProfitDetails.setFinCcy(financeMain.getFinCcy());
			finProfitDetails.setFinPurpose(financeMain.getFinPurpose());
			finProfitDetails.setFinContractDate(financeMain.getFinContractDate());
			finProfitDetails.setFinApprovedDate(financeMain.getFinApprovedDate());
			finProfitDetails.setFinStartDate(financeMain.getFinStartDate());
			finProfitDetails.setFinAmount(financeMain.getFinAmount());
			finProfitDetails.setDownPayment(financeMain.getDownPayment());
			finProfitDetails.setCustId(financeMain.getCustID());

			finProfitDetails.setTAKAFULPaidAmt(BigDecimal.ZERO);
			finProfitDetails.setTAKAFULInsCal(BigDecimal.ZERO);
			finProfitDetails.setAdminPaidAmt(BigDecimal.ZERO);

			if(feeRuleList != null && feeRuleList.size() > 0){
				for (FeeRule feeRule : feeRuleList) {
					if(feeRule.getFeeCode().equals("TAKAFUL")){
						finProfitDetails.setTAKAFULPaidAmt(feeRule.getPaidAmount());
						finProfitDetails.setTAKAFULInsCal(feeRule.getFeeAmount());
					}
					if(feeRule.getFeeCode().equals("ADMIN")){
						finProfitDetails.setAdminPaidAmt(feeRule.getPaidAmount());
					}
				}
			}

			finProfitDetails.setDisbAccountId(financeMain.getDisbAccountId());	
			finProfitDetails.setDisbActCcy(financeMain.getFinCcy());
			finProfitDetails.setFinCustPftAccount(financeMain.getFinCustPftAccount());
			finProfitDetails.setIncomeAccount("");
			finProfitDetails.setUEIncomeSuspAccount("");
			finProfitDetails.setFinCommitmentRef(financeMain.getFinCommitmentRef());
			finProfitDetails.setFinIsActive(financeMain.isFinIsActive());
		}
		
		if(aeAmountCodes.getFirstRepayDate() == null) {
			finProfitDetails.setFirstRepayDate(financeMain.getFinStartDate());
		} else {
			finProfitDetails.setFirstRepayDate(aeAmountCodes.getFirstRepayDate());
		}

		finProfitDetails.setAcrTillLBD(finProfitDetails.getTdPftAccrued()); 
		finProfitDetails.setAmzTillLBD(finProfitDetails.getAmzTillNBD());

		finProfitDetails.setFinStatus(financeMain.getFinStatus());
		finProfitDetails.setFinStsReason(financeMain.getFinStsReason());
		finProfitDetails.setFinAccount(financeMain.getFinAccount());	
		finProfitDetails.setFinAcType(financeType.getFinAcType());
		finProfitDetails.setRepayAccountId(financeMain.getRepayAccountId());	
		finProfitDetails.setFinCategory(financeType.getFinCategory());
		finProfitDetails.setMaturityDate(financeMain.getMaturityDate());
		
		finProfitDetails = getFinanceProfitDetailFiller().prepareFinPftDetails(aeAmountCodes, finProfitDetails, curBDay);

		String worstSts = getCustomerStatusCodeDAO().getFinanceStatus(finProfitDetails.getFinReference(), false);
		finProfitDetails.setFinWorstStatus(worstSts);

		if (getProfitDetailsDAO().getFinProfitDetailsById(financeMain.getFinReference()) == null) {
			getProfitDetailsDAO().save(finProfitDetails, "");
		} else {
			getProfitDetailsDAO().update(finProfitDetails, false);
		}

		logger.debug("Leaving");
		return finProfitDetails;
	}

	/**
	 * Method to delete schedule, disbursement, deferementheader, defermentdetail,repayinstruction, ratechanges lists.
	 * 
	 * @param scheduleData
	 * @param tableType
	 * @param isWIF
	 */
	public void listDeletion(FinScheduleData scheduleData, String tableType, boolean isWIF) {
		logger.debug("Entering ");

		getFinanceScheduleDetailDAO().deleteByFinReference(scheduleData.getFinReference(), tableType, isWIF, 0);
		getFinanceDisbursementDAO().deleteByFinReference(scheduleData.getFinReference(), tableType, isWIF, 0);
		getDefermentHeaderDAO().deleteByFinReference(scheduleData.getFinReference(), tableType, isWIF, 0);
		getDefermentDetailDAO().deleteByFinReference(scheduleData.getFinReference(), tableType, isWIF, 0);
		getRepayInstructionDAO().deleteByFinReference(scheduleData.getFinReference(), tableType, isWIF, 0);

		//Fee Charge Details & Finance Overdue PenaltyRate Details
		if (!StringUtils.trimToEmpty(tableType).equals("") || isWIF) {
			getPostingsDAO().deleteChargesBatch(scheduleData.getFinReference(), isWIF, tableType);
		}
		if (!isWIF) {
			getFinODPenaltyRateDAO().delete(scheduleData.getFinReference(), tableType);
		}

		logger.debug("Leaving ");
	}

	/**
	 * Method to save what if inquiry lists
	 */
	public void listSave(FinScheduleData finDetail, String tableType, boolean isWIF , long logKey) {
		logger.debug("Entering ");
		HashMap<Date, Integer> mapDateSeq = new HashMap<Date, Integer>();

		// Finance Schedule Details
		for (int i = 0; i < finDetail.getFinanceScheduleDetails().size(); i++) {
			finDetail.getFinanceScheduleDetails().get(i).setLastMntBy(finDetail.getFinanceMain().getLastMntBy());
			finDetail.getFinanceScheduleDetails().get(i).setFinReference(finDetail.getFinReference());
			int seqNo = 0;

			if (mapDateSeq.containsKey(finDetail.getFinanceScheduleDetails().get(i).getSchDate())) {
				seqNo = mapDateSeq.get(finDetail.getFinanceScheduleDetails().get(i).getSchDate());
				mapDateSeq.remove(finDetail.getFinanceScheduleDetails().get(i).getSchDate());
			}
			seqNo = seqNo + 1;
			mapDateSeq.put(finDetail.getFinanceScheduleDetails().get(i).getSchDate(), seqNo);
			finDetail.getFinanceScheduleDetails().get(i).setSchSeq(seqNo);
			finDetail.getFinanceScheduleDetails().get(i).setLogKey(logKey);
		}

		getFinanceScheduleDetailDAO().saveList(finDetail.getFinanceScheduleDetails(), tableType, isWIF);

		// Finance Disbursement Details
		mapDateSeq = new HashMap<Date, Integer>();
		Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
		for (int i = 0; i < finDetail.getDisbursementDetails().size(); i++) {
			finDetail.getDisbursementDetails().get(i).setFinReference(finDetail.getFinReference());
			finDetail.getDisbursementDetails().get(i).setDisbReqDate(curBDay);
			int seqNo = 0;

			if (mapDateSeq.containsKey(finDetail.getDisbursementDetails().get(i).getDisbDate())) {
				seqNo = mapDateSeq.get(finDetail.getDisbursementDetails().get(i).getDisbDate());
				mapDateSeq.remove(finDetail.getDisbursementDetails().get(i).getDisbDate());
			} 
			seqNo = seqNo + 1;

			mapDateSeq.put(finDetail.getDisbursementDetails().get(i).getDisbDate(), seqNo);
			finDetail.getDisbursementDetails().get(i).setDisbSeq(seqNo);
			finDetail.getDisbursementDetails().get(i).setDisbIsActive(true);
			finDetail.getDisbursementDetails().get(i).setDisbDisbursed(true);
			finDetail.getDisbursementDetails().get(i).setLogKey(logKey);
		}
		getFinanceDisbursementDAO().saveList(finDetail.getDisbursementDetails(), tableType, isWIF);

		//Finance Defferment Header Details
		for (int i = 0; i < finDetail.getDefermentHeaders().size(); i++) {
			finDetail.getDefermentHeaders().get(i).setFinReference(finDetail.getFinReference());
			finDetail.getDefermentHeaders().get(i).setLogKey(logKey);
		}
		getDefermentHeaderDAO().saveList(finDetail.getDefermentHeaders(), tableType, isWIF);

		//Finance Defferment Details
		for (int i = 0; i < finDetail.getDefermentDetails().size(); i++) {
			finDetail.getDefermentDetails().get(i).setFinReference(finDetail.getFinReference());
			finDetail.getDefermentDetails().get(i).setLogKey(logKey);
		}
		getDefermentDetailDAO().saveList(finDetail.getDefermentDetails(), tableType, isWIF);

		//Finance Repay Instruction Details
		for (int i = 0; i < finDetail.getRepayInstructions().size(); i++) {
			finDetail.getRepayInstructions().get(i).setFinReference(finDetail.getFinReference());
			finDetail.getRepayInstructions().get(i).setLogKey(logKey);
		}
		getRepayInstructionDAO().saveList(finDetail.getRepayInstructions(), tableType, isWIF);

		//Finance Overdue Penalty Rates
		if (!isWIF && logKey == 0) {
			FinODPenaltyRate penaltyRate = finDetail.getFinODPenaltyRate();
			if (penaltyRate == null) { 
				penaltyRate = new FinODPenaltyRate();
				penaltyRate.setApplyODPenalty(false);
				penaltyRate.setODIncGrcDays(false);
				penaltyRate.setODChargeType("");
				penaltyRate.setODChargeAmtOrPerc(BigDecimal.ZERO);
				penaltyRate.setODChargeCalOn("");
				penaltyRate.setODGraceDays(0);
				penaltyRate.setODAllowWaiver(false);
				penaltyRate.setODMaxWaiverPerc(BigDecimal.ZERO);
			}
			penaltyRate.setFinReference(finDetail.getFinReference());
			penaltyRate.setFinEffectDate(DateUtility.getUtilDate());
			getFinODPenaltyRateDAO().save(penaltyRate, tableType);
		}

		logger.debug("Leaving ");
	}

	/**
	 * Method for saving List of Fee Charge details
	 * 
	 * @param finDetail
	 * @param tableType
	 */
	public void saveFeeChargeList(FinScheduleData finScheduleData, boolean isWIF, String tableType) {
		logger.debug("Entering");

		if (finScheduleData.getFeeRules() != null && finScheduleData.getFeeRules().size() > 0) {
			//Finance Fee Charge Details
			for (int i = 0; i < finScheduleData.getFeeRules().size(); i++) {
				finScheduleData.getFeeRules().get(i)
				.setFinReference(finScheduleData.getFinReference());
			}
			getPostingsDAO().saveChargesBatch(finScheduleData.getFeeRules(),isWIF, tableType);
		}

		logger.debug("Leaving");
	}
	
	/**
	 * Method for saving List of Fee Charge details
	 * 
	 * @param finDetail
	 * @param tableType
	 */
	public void saveStepDetailList(FinScheduleData finScheduleData, boolean isWIF, String tableType) {
		logger.debug("Entering");
		
		if (finScheduleData.getStepPolicyDetails() != null && finScheduleData.getStepPolicyDetails().size() > 0) {
			//Finance Fee Charge Details
			for (int i = 0; i < finScheduleData.getStepPolicyDetails().size(); i++) {
				finScheduleData.getStepPolicyDetails().get(i).setFinReference(finScheduleData.getFinReference());
			}
			getFinanceStepDetailDAO().saveList(finScheduleData.getStepPolicyDetails(),isWIF, tableType);
		}
		
		logger.debug("Leaving");
	}


	/**
	 * Method to get Schedule related data.
	 * 
	 * @param finReference
	 *            (String)
	 * @param isWIF
	 *            (boolean)
	 * **/
	public FinScheduleData getFinSchDataByFinRef(String finReference, String type, long logKey) {
		logger.debug("Entering");

		FinScheduleData finSchData = new FinScheduleData();
		if(logKey == 0){
			finSchData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(finReference, type, false));
		}
		finSchData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(
				finReference, type, false));
		if(logKey != 0){
			finSchData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(
					finReference, type, false));
		}
		finSchData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference,
				type, false));
		finSchData.setDefermentHeaders(getDefermentHeaderDAO().getDefermentHeaders(finReference,
				type, false));
		finSchData.setDefermentDetails(getDefermentDetailDAO().getDefermentDetails(finReference,
				type, false));
		if(logKey == 0){
			finSchData.setFinanceType(getFinanceTypeDAO().getFinanceTypeByID(
					finSchData.getFinanceMain().getFinType(), type));
			finSchData.setFeeRules(getPostingsDAO().getFeeChargesByFinRef(finReference,false, ""));
			finSchData.setRepayDetails(getFinanceRepaymentsByFinRef(finReference, false));
			finSchData.setPenaltyDetails(getFinancePenaltysByFinRef(finReference));
			finSchData.setAccrueValue(getProfitDetailsDAO().getAccrueAmount(finReference));
		}
		logger.debug("Leaving");
		return finSchData;
	}

	/**
	 * Method to get FinanceRepayments By FinReference
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	public List<FinanceRepayments> getFinanceRepaymentsByFinRef(final String id, boolean isRpyCancelProc) {
		return getFinanceRepaymentsDAO().getFinRepayListByFinRef(id, isRpyCancelProc, "");
	}

	/**
	 * Method to get FinanceRepayments By FinReference
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	public List<OverdueChargeRecovery> getFinancePenaltysByFinRef(final String id) {
		return getRecoveryDAO().getFinancePenaltysByFinRef(id, "");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setCustomerStatusCodeDAO(CustomerStatusCodeDAO customerStatusCodeDAO) {
		this.customerStatusCodeDAO = customerStatusCodeDAO;
	}
	public CustomerStatusCodeDAO getCustomerStatusCodeDAO() {
		return customerStatusCodeDAO;
	}
	
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}
	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public String getExcludeFields() {
		return excludeFields;
	}
	public void setExcludeFields(String excludeFields) {
		this.excludeFields = excludeFields;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}
	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public DefermentHeaderDAO getDefermentHeaderDAO() {
		return defermentHeaderDAO;
	}
	public void setDefermentHeaderDAO(DefermentHeaderDAO defermentHeaderDAO) {
		this.defermentHeaderDAO = defermentHeaderDAO;
	}

	public DefermentDetailDAO getDefermentDetailDAO() {
		return defermentDetailDAO;
	}
	public void setDefermentDetailDAO(DefermentDetailDAO defermentDetailDAO) {
		this.defermentDetailDAO = defermentDetailDAO;
	}

	public RepayInstructionDAO getRepayInstructionDAO() {
		return repayInstructionDAO;
	}
	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public FinODPenaltyRateDAO getFinODPenaltyRateDAO() {
		return finODPenaltyRateDAO;
	}
	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

	public DocumentDetailsDAO getDocumentDetailsDAO() {
		return documentDetailsDAO;
	}
	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public CustomerDocumentDAO getCustomerDocumentDAO() {
		return customerDocumentDAO;
	}
	public void setCustomerDocumentDAO(CustomerDocumentDAO customerDocumentDAO) {
		this.customerDocumentDAO = customerDocumentDAO;
	}

	public EligibilityDetailService getEligibilityDetailService() {
		return eligibilityDetailService;
	}
	public void setEligibilityDetailService(EligibilityDetailService eligibilityDetailService) {
		this.eligibilityDetailService = eligibilityDetailService;
	}

	public GuarantorDetailService getGuarantorDetailService() {
		return guarantorDetailService;
	}
	public void setGuarantorDetailService(GuarantorDetailService guarantorDetailService) {
		this.guarantorDetailService = guarantorDetailService;
	}

	public JointAccountDetailService getJointAccountDetailService() {
		return jointAccountDetailService;
	}
	public void setJointAccountDetailService(JointAccountDetailService jointAccountDetailService) {
		this.jointAccountDetailService = jointAccountDetailService;
	}

	public AgreementDetailService getAgreementDetailService() {
		return agreementDetailService;
	}
	public void setAgreementDetailService(AgreementDetailService agreementDetailService) {
		this.agreementDetailService = agreementDetailService;
	}

	public ScoringDetailService getScoringDetailService() {
		return scoringDetailService;
	}
	public void setScoringDetailService(ScoringDetailService scoringDetailService) {
		this.scoringDetailService = scoringDetailService;
	}

	public CheckListDetailService getCheckListDetailService() {
		return checkListDetailService;
	}
	public void setCheckListDetailService(CheckListDetailService checkListDetailService) {
		this.checkListDetailService = checkListDetailService;
	}

	public CarLoanDetailService getCarLoanDetailService() {
		return carLoanDetailService;
	}
	public void setCarLoanDetailService(CarLoanDetailService carLoanDetailService) {
		this.carLoanDetailService = carLoanDetailService;
	}

	public EducationalLoanService getEducationalLoanService() {
		return educationalLoanService;
	}
	public void setEducationalLoanService(EducationalLoanService educationalLoanService) {
		this.educationalLoanService = educationalLoanService;
	}

	public HomeLoanDetailService getHomeLoanDetailService() {
		return homeLoanDetailService;
	}
	public void setHomeLoanDetailService(HomeLoanDetailService homeLoanDetailService) {
		this.homeLoanDetailService = homeLoanDetailService;
	}

	public MortgageLoanDetailService getMortgageLoanDetailService() {
		return mortgageLoanDetailService;
	}
	public void setMortgageLoanDetailService(MortgageLoanDetailService mortgageLoanDetailService) {
		this.mortgageLoanDetailService = mortgageLoanDetailService;
	}

	public GoodsLoanDetailService getGoodsLoanDetailService() {
		return goodsLoanDetailService;
	}
	public void setGoodsLoanDetailService(GoodsLoanDetailService goodsLoanDetailService) {
		this.goodsLoanDetailService = goodsLoanDetailService;
	}

	public GenGoodsLoanDetailService getGenGoodsLoanDetailService() {
		return genGoodsLoanDetailService;
	}
	public void setGenGoodsLoanDetailService(GenGoodsLoanDetailService genGoodsLoanDetailService) {
		this.genGoodsLoanDetailService = genGoodsLoanDetailService;
	}

	public CommidityLoanDetailService getCommidityLoanDetailService() {
		return commidityLoanDetailService;
	}
	public void setCommidityLoanDetailService(CommidityLoanDetailService commidityLoanDetailService) {
		this.commidityLoanDetailService = commidityLoanDetailService;
	}

	public ContractorAssetDetailService getContractorAssetDetailService() {
		return contractorAssetDetailService;
	}
	public void setContractorAssetDetailService(
			ContractorAssetDetailService contractorAssetDetailService) {
		this.contractorAssetDetailService = contractorAssetDetailService;
	}

	public SharesDetailService getSharesDetailService() {
		return sharesDetailService;
	}
	public void setSharesDetailService(SharesDetailService sharesDetailService) {
		this.sharesDetailService = sharesDetailService;
	}

	public PostingsDAO getPostingsDAO() {
		return postingsDAO;
	}
	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}
	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public AccountEngineExecutionRIA getEngineExecutionRIA() {
		return engineExecutionRIA;
	}
	public void setEngineExecutionRIA(AccountEngineExecutionRIA engineExecutionRIA) {
		this.engineExecutionRIA = engineExecutionRIA;
	}

	public PostingsInterfaceService getPostingsInterfaceService() {
		return postingsInterfaceService;
	}
	public void setPostingsInterfaceService(PostingsInterfaceService postingsInterfaceService) {
		this.postingsInterfaceService = postingsInterfaceService;
	}

	public CommitmentDAO getCommitmentDAO() {
		return commitmentDAO;
	}
	public void setCommitmentDAO(CommitmentDAO commitmentDAO) {
		this.commitmentDAO = commitmentDAO;
	}

	public FinLogEntryDetailDAO getFinLogEntryDetailDAO() {
		return finLogEntryDetailDAO;
	}
	public void setFinLogEntryDetailDAO(FinLogEntryDetailDAO finLogEntryDetailDAO) {
		this.finLogEntryDetailDAO = finLogEntryDetailDAO;
	}

	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinanceRepaymentsDAO getFinanceRepaymentsDAO() {
		return financeRepaymentsDAO;
	}
	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public OverdueChargeRecoveryDAO getRecoveryDAO() {
		return recoveryDAO;
	}
	public void setRecoveryDAO(OverdueChargeRecoveryDAO recoveryDAO) {
		this.recoveryDAO = recoveryDAO;
	}

	public FinanceSuspHeadDAO getFinanceSuspHeadDAO() {
		return financeSuspHeadDAO;
	}
	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}

	public FinanceProfitDetailDAO getProfitDetailsDAO() {
		return profitDetailsDAO;
	}
	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public void setCommitmentMovementDAO(CommitmentMovementDAO commitmentMovementDAO) {
		this.commitmentMovementDAO = commitmentMovementDAO;
	}
	public CommitmentMovementDAO getCommitmentMovementDAO() {
		return commitmentMovementDAO;
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public AccountProcessUtil getAccountProcessUtil() {
		return accountProcessUtil;
	}
	public void setAccountProcessUtil(AccountProcessUtil accountProcessUtil) {
		this.accountProcessUtil = accountProcessUtil;
	}

	public FinanceProfitDetailFiller getFinanceProfitDetailFiller() {
		return financeProfitDetailFiller;
	}
	public void setFinanceProfitDetailFiller(FinanceProfitDetailFiller financeProfitDetailFiller) {
		this.financeProfitDetailFiller = financeProfitDetailFiller;
	}

	public void setTransactionEntryDAO(TransactionEntryDAO transactionEntryDAO) {
		this.transactionEntryDAO = transactionEntryDAO;
	}
	public TransactionEntryDAO getTransactionEntryDAO() {
		return transactionEntryDAO;
	}

	public FinFeeChargesDAO getFinFeeChargesDAO() {
	    return finFeeChargesDAO;
    }
	public void setFinFeeChargesDAO(FinFeeChargesDAO finFeeChargesDAO) {
	    this.finFeeChargesDAO = finFeeChargesDAO;
    }

	public PostingsPreparationUtil getPostingsPreparationUtil() {
	    return postingsPreparationUtil;
    }
	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
	    this.postingsPreparationUtil = postingsPreparationUtil;
    }

	public FinStatusDetailDAO getFinStatusDetailDAO() {
	    return finStatusDetailDAO;
    }
	public void setFinStatusDetailDAO(FinStatusDetailDAO finStatusDetailDAO) {
	    this.finStatusDetailDAO = finStatusDetailDAO;
    }
	
	public SuspensePostingUtil getSuspensePostingUtil() {
		return suspensePostingUtil;
	}
	public void setSuspensePostingUtil(SuspensePostingUtil suspensePostingUtil) {
		this.suspensePostingUtil = suspensePostingUtil;
	}

	public OverDueRecoveryPostingsUtil getRecoveryPostingsUtil() {
	    return recoveryPostingsUtil;
    }
	public void setRecoveryPostingsUtil(OverDueRecoveryPostingsUtil recoveryPostingsUtil) {
	    this.recoveryPostingsUtil = recoveryPostingsUtil;
    }
	
	public FinanceStepDetailDAO getFinanceStepDetailDAO() {
		return financeStepDetailDAO;
	}
	public void setFinanceStepDetailDAO(FinanceStepDetailDAO financeStepDetailDAO) {
		this.financeStepDetailDAO = financeStepDetailDAO;
	}
	

}
