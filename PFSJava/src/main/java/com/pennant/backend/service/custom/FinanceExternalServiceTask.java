package com.pennant.backend.service.custom;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;

import com.pennant.app.model.RateDetail;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.servicetasklog.ServiceTaskDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustEmployeeDetail;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.servicetask.ServiceTaskDetail;
import com.pennant.backend.service.collateral.CollateralMarkProcess;
import com.pennant.backend.service.dda.DDAControllerService;
import com.pennant.backend.service.finance.CustomServiceTask;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.constants.InterfaceConstants;
import com.pennant.coreinterface.model.collateral.CollateralMark;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.engine.workflow.model.ServiceTask;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.BlacklistCheck;
import com.pennanttech.pff.external.BreService;
import com.pennanttech.pff.external.BureauScore;
import com.pennanttech.pff.external.CibilConsumerService;
import com.pennanttech.pff.external.CriffBureauService;
import com.pennanttech.pff.external.ExperianBureauService;
import com.pennanttech.pff.external.ExternalDedup;
import com.pennanttech.pff.external.HoldFinanceService;
import com.pennanttech.pff.external.LegalDeskService;

public class FinanceExternalServiceTask implements CustomServiceTask {
	private static final Logger				logger	= Logger.getLogger(FinanceExternalServiceTask.class);

	// Open the below commented code once pff-interface configuration setup completed.
	@Autowired(required = false)
	private ExternalDedup externalDedup;

	@Autowired(required = false)
	private BlacklistCheck blacklistCheck;

	@Autowired(required = false)
	private ExperianBureauService experianBureauService;

	@Autowired(required = false)
	private CriffBureauService criffBureauService;

	@Autowired(required = false)
	private BureauScore bureauscore;
	
	@Autowired(required = false)
	private CibilConsumerService cibilConsumerService;
	
	@Autowired(required = false)
	private LegalDeskService legalDeskService;
	
	@Autowired(required = false)
	private HoldFinanceService		holdFinanceService;
	
	@Autowired(required = false)
	private BreService				breService;

	private CollateralMarkProcess	collateralMarkProcess;
	private DDAControllerService	ddaControllerService;
	private ServiceTaskDAO			serviceTaskDAO;

	@Override
	public boolean executeExternalServiceTask(AuditHeader auditHeader, ServiceTask serviceTask) throws Exception {
		logger.debug(Literal.ENTERING);

		FinanceDetail afinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain afinanceMain = afinanceDetail.getFinScheduleData().getFinanceMain();
		List<ErrorDetail> errors = new ArrayList<>();
		boolean taskExecuted = true;
		boolean executed = getServiceTaskStatus(serviceTask, afinanceMain.getFinReference());
		if(executed) {
			taskExecuted = true;
			return taskExecuted;
		}

		try {
			switch (serviceTask.getOperation()) {
			case PennantConstants.method_doCheckScore:
				doCheckScore(afinanceDetail);
				taskExecuted = true;
				break;
			case PennantConstants.method_doCheckExceptions:
				auditHeader = doCheckExceptions(auditHeader);
				taskExecuted = true;
				break;
			case PennantConstants.method_doCheckLPOApproval:
				errors = doCollateralMark(afinanceDetail.getFinanceCollaterals());
				if(!errors.isEmpty()) {
					auditHeader.getErrorMessage().addAll(errors);
				}
				taskExecuted = true;
				break;
			case PennantConstants.method_checkDDAResponse:
				errors = checkDDAResponse(afinanceDetail);
				if(!errors.isEmpty()) {
					auditHeader.getErrorMessage().addAll(errors);
				}
				taskExecuted = true;
				break;
			case PennantConstants.method_doClearQueues:
				afinanceDetail.getFinScheduleData().getFinanceMain().setNextTaskId("");
				taskExecuted = true;
				break;
			case PennantConstants.method_doFundsAvailConfirmed:
				errors = doFundsAvailConfirmed(afinanceDetail);
				if(!errors.isEmpty()) {
					auditHeader.getErrorMessage().addAll(errors);
				}
				taskExecuted = true;
				break;
			case PennantConstants.method_doCheckProspectCustomer:
				doCheckProspectCustomer(afinanceDetail);
				taskExecuted = true;
				break;
			case PennantConstants.method_doCheckSMECustomer:
				doCheckSMECustomer(afinanceDetail);
				taskExecuted = true;
				break;
			case PennantConstants.method_sendDDARequest:
				errors = sendDDARequest(afinanceDetail);
				if(!errors.isEmpty()) {
					auditHeader.getErrorMessage().addAll(errors);
				}
				taskExecuted = true;
				break;
			case PennantConstants.method_doCheckShariaRequired:
				//Setting this property which is used in workflow condition post sharia
				afinanceMain.setShariaApprovalReq(true);//FIXME:what is the default value for sharia required
				taskExecuted = true;
				break;
			case PennantConstants.method_externalDedup:
				try {
					auditHeader = externalDedup.checkDedup(auditHeader);
					taskExecuted = true;
				} catch (InterfaceException e) {
					logger.error("Exception in Dedup Bureau:", e);
					taskExecuted = true;
					setRemarks(auditHeader, PennantConstants.method_externalDedup, e.getMessage());
					//throw new InterfaceException("9999", e.getMessage());
				}
				break;
			case PennantConstants.method_hunter:
				try {
					auditHeader = blacklistCheck.checkHunterDetails(auditHeader);
					taskExecuted = true;
				} catch (InterfaceException e) {
					logger.error("Exception in Hunter Bureau:", e);
					taskExecuted = true;
					setRemarks(auditHeader, PennantConstants.method_hunter, e.getMessage());
					//throw new InterfaceException("9999", e.getMessage());
				}
				break;
			case PennantConstants.method_Experian_Bureau:
				try {
					auditHeader=experianBureauService.executeExperianBureau(auditHeader);
					taskExecuted = true;
				} catch (InterfaceException e) {
					logger.error("Exception in Experian Bureau:", e);
					taskExecuted = true;
					setRemarks(auditHeader, PennantConstants.method_Experian_Bureau, e.getMessage());
					//throw new InterfaceException("9999", e.getMessage());
				}
				break;
			case PennantConstants.method_Crif_Bureau:
				try {
					auditHeader=criffBureauService.executeCriffBureau(auditHeader);
					taskExecuted = true;
				} catch (InterfaceException e) {
					logger.error("Exception in CRIFF Bureau:", e);
					taskExecuted = true;
					setRemarks(auditHeader, PennantConstants.method_Crif_Bureau, e.getMessage());
					//throw new InterfaceException("9999", e.getMessage());
				}
				break;
			case PennantConstants.method_Cibil_Bureau:
				try {
					auditHeader = cibilConsumerService.getCibilConsumer(auditHeader);
					taskExecuted = true;
				} catch (InterfaceException e) {
					logger.error("Exception in CIBIL Bureau:", e);
					taskExecuted = true;
					setRemarks(auditHeader, PennantConstants.method_Cibil_Bureau, e.getMessage());
				}
				break;
			case PennantConstants.method_LegalDesk:
				try {
					setInstallmentType(auditHeader);
					setRateOfInst(auditHeader);
					auditHeader = legalDeskService.executeLegalDesk(auditHeader);
					taskExecuted = true;
				} catch (InterfaceException e) {
					logger.error("Exception in LegalDesk:", e);
					taskExecuted = true;
				}
				break;
			case PennantConstants.method_HoldFinance:
				try {
					auditHeader = holdFinanceService.executeHoldFinance(auditHeader);
					taskExecuted = true;
				} catch (InterfaceException e) {
					logger.error("Exception in HoldFinance:", e);
					taskExecuted = true;
				}
				break;
			case PennantConstants.method_bre:
				try {
					auditHeader = breService.executeBRE(auditHeader);
					taskExecuted = true;
				} catch (InterfaceException e) {
					logger.error("Exception in BRE:", e);
					taskExecuted = true;
					setRemarks(auditHeader, PennantConstants.method_bre, e.getMessage());
				}
				break;
			default:
				return taskExecuted;
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			ServiceTaskDetail serviceTaskDetail = new ServiceTaskDetail();
			serviceTaskDetail.setStatus("Failed");
			serviceTaskDetail.setRemarks(e.getMessage());
			logServiceTaskDetails(auditHeader, serviceTask, serviceTaskDetail);
			throw e;
		}
		ServiceTaskDetail serviceTaskDetail = new ServiceTaskDetail();
		// checking for whether service task executed successfully or not
		if (auditHeader.getErrorMessage() != null && !auditHeader.getErrorMessage().isEmpty()) {
			serviceTaskDetail.setStatus("Failed");
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				serviceTaskDetail.setRemarks(errorDetail.getCode()+":"+errorDetail.getMessage());
			}
		}
		serviceTaskDetail.setStatus("Success");
		serviceTaskDetail.setRemarks(Labels.getLabel("SERVICETASK_EXECUTED"));

		logServiceTaskDetails(auditHeader, serviceTask, serviceTaskDetail);
		return taskExecuted;
	}

	private void setRateOfInst(AuditHeader auditHeader) {
		//FIXME: How to get RateUtil for Niyogin project
		try {
			FinanceDetail finDeatil = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
			FinanceMain finMain = finDeatil.getFinScheduleData().getFinanceMain();	
			
			BigDecimal rate = BigDecimal.ZERO;
			if (finMain.getRepayBaseRate() != null) {
				RateDetail details = RateUtil.rates(finMain.getRepayBaseRate(), finMain.getFinCcy(), finMain.getRepaySpecialRate(),
						finMain.getRepayMargin(),finMain.getRpyMinRate(), finMain.getRpyMaxRate());
				rate = details.getNetRefRateLoan();
			} else {
				rate = finMain.getRepayProfitRate();
			}
			finDeatil.getExtendedFieldRender().getMapValues().put("RATE_LEGALDESK", 
					PennantApplicationUtil.formatRate(rate.doubleValue(), 9));
			
			
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}

	/**
	 * Method for set the finance frequency type to the ExtendedMap for temporary rendering purpose.
	 * 
	 * @param auditHeader
	 */
	private void setInstallmentType(AuditHeader auditHeader) {
		//FIXME: How to get Installment Type for Niyogin project
		try {
			FinanceDetail finDeatil = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
			FinanceMain finMain = finDeatil.getFinScheduleData().getFinanceMain();
			String repayFrq = finMain.getRepayFrq();
			if (StringUtils.isNotBlank(repayFrq)) {
				String frequencyCode = repayFrq.substring(0, 1);
				ArrayList<ValueLabel> freequencyList = FrequencyUtil.getFrequency();
				for (ValueLabel valueLabe : freequencyList) {
					if (StringUtils.equals(valueLabe.getValue(), frequencyCode)) {
						finDeatil.getExtendedFieldRender().getMapValues().put("INSTALLMENTTYPE_LEGALDESK",
								valueLabe.getLabel());
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}
	
	/**
	 * Method for set Reason code and remarks.
	 * 
	 * @param auditHeader
	 * @param method
	 * @param message
	 */
	private void setRemarks(AuditHeader auditHeader, String method, String message) {
		FinanceDetail finDeatil = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		if(finDeatil.getExtendedFieldRender() != null) {
			Map<String, Object> extendedMap = finDeatil.getExtendedFieldRender().getMapValues();
			if(message != null && message.length() > 149) {
				message = message.substring(0, 143);
			}
			if(StringUtils.equals(method, PennantConstants.method_Experian_Bureau)) {
				extendedMap.put("REASONCODE", "9999");
				extendedMap.put("REMARKSEXPERIANBEA", message);
			} else if(StringUtils.equals(method, PennantConstants.method_Crif_Bureau)) {
				extendedMap.put("REASONCODECRIF", "9999");
				extendedMap.put("REMARKSCRIF", message);
			} else if(StringUtils.equals(method, PennantConstants.method_Cibil_Bureau)) {
				/*extendedMap.put("REASONCODECRIF", "9999");
				extendedMap.put("REMARKSCRIF", message);*/
			} else if(StringUtils.equals(method, PennantConstants.method_externalDedup)) {
				extendedMap.put("REASONCODEINTERNAL", "9999");
				extendedMap.put("REMARKSINTERNAL", message);
				extendedMap.put("EXDREQUESTSEND", true);
			} else if(StringUtils.equals(method, PennantConstants.method_hunter)) {
				extendedMap.put("REASONCODEHUNTER", "9999");
				extendedMap.put("REMARKSHUNTER", message);
				extendedMap.put("HUNTREQSEND", true);
			}else if(StringUtils.equals(method, PennantConstants.method_bre)) {
				extendedMap.put("BREREQSEND", true);
				extendedMap.put("REASONCODEBRE", "9999");
				extendedMap.put("REMARKSBRE", message);
			}
		}
	}

	/**
	 * Method for validate the execution status of service task.
	 * 
	 * @param serviceTask
	 * @param reference
	 * @return boolean
	 */
	private boolean getServiceTaskStatus(ServiceTask serviceTask, String reference) {
		logger.debug(Literal.ENTERING);
		boolean executed = false;
		if(serviceTask.isRerunnable()) {
			executed = false;
		} else {
			String module = FinanceConstants.MODULE_NAME;
			String serviceTaskName = serviceTask.getOperation();
			List<ServiceTaskDetail> details = serviceTaskDAO.getServiceTaskDetails(module, reference, serviceTaskName);
			if(details.isEmpty()) {
				executed = false;
			} else {
				for (ServiceTaskDetail serviceTaskDetail : details) {
					if(StringUtils.equals(serviceTaskDetail.getStatus(), "Success")) {
						executed = true;
						break;
					} else {
						executed = false;
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return executed;
	}

	private void logServiceTaskDetails(AuditHeader auditHeader, ServiceTask serviceTask, ServiceTaskDetail serviceTaskDetail) {
		logger.debug(Literal.ENTERING);

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		serviceTaskDetail.setServiceModule(FinanceConstants.MODULE_NAME);
		serviceTaskDetail.setReference(financeDetail.getFinScheduleData().getFinanceMain().getFinReference());
		serviceTaskDetail.setServiceTaskId(serviceTask.getId());
		serviceTaskDetail.setServiceTaskName(serviceTask.getOperation());
		serviceTaskDetail.setUserId(financeDetail.getUserDetails().getUserId());
		serviceTaskDetail.setExecutedTime(new Timestamp(System.currentTimeMillis()));
		if(serviceTaskDetail.getRemarks() != null && serviceTaskDetail.getRemarks().length() > 200) {
			serviceTaskDetail.setRemarks(serviceTaskDetail.getRemarks().substring(0, 190));
		}

		serviceTaskDAO.save(serviceTaskDetail, "");

		logger.debug(Literal.LEAVING);
	}

	private void doCheckScore(FinanceDetail afinanceDetail) {
		afinanceDetail.getFinScheduleData().getFinanceMain().setScore(afinanceDetail.getScore());
	}

	/**
	 * Method for send DDA request to interface
	 * 
	 * @param afinanceDetail
	 * @return
	 */
	private List<ErrorDetail> sendDDARequest(FinanceDetail afinanceDetail) {
		logger.debug(Literal.ENTERING);
		List<ErrorDetail> errors = new ArrayList<>();
		String finRepayMethod = afinanceDetail.getFinScheduleData().getFinanceMain().getFinRepayMethod();
		if (StringUtils.equals(finRepayMethod, FinanceConstants.REPAYMTH_AUTODDA)) {
			try {
				getDdaControllerService().doDDARequestProcess(afinanceDetail, false);
				boolean isAllowDpSp = afinanceDetail.getFinScheduleData().getFinanceType().isAllowDownpayPgm();
				if (isAllowDpSp) {
					boolean ahaDpEnable = true;
					getDdaControllerService().doDDARequestProcess(afinanceDetail, ahaDpEnable);
				}
			} catch (InterfaceException e) {
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail(e.getErrorCode(), e.getErrorMessage(), null)));
			}
		}

		logger.debug(Literal.LEAVING);
		return errors;
	}

	private void doCheckProspectCustomer(FinanceDetail afinanceDetail) {
		if (afinanceDetail.getCustomerDetails() != null) {
			CustEmployeeDetail custempDetail = afinanceDetail.getCustomerDetails().getCustEmployeeDetail();
			if(custempDetail != null) {
				if (StringUtils.equalsIgnoreCase(custempDetail.getEmpStatus(),PennantConstants.PFF_CUSTCTG_SME)) {
					afinanceDetail.getFinScheduleData().getFinanceMain().setSmecustomer(true);
				}
			}
		}
	}

	private void doCheckSMECustomer(FinanceDetail afinanceDetail) {
		if (afinanceDetail.getCustomerDetails() != null) {
			CustEmployeeDetail custempDetail = afinanceDetail.getCustomerDetails().getCustEmployeeDetail();
			if(custempDetail != null) {
				if (StringUtils.equalsIgnoreCase(custempDetail.getEmpStatus(),PennantConstants.PFF_CUSTCTG_SME)) {
					afinanceDetail.getFinScheduleData().getFinanceMain().setSmecustomer(true);
				}
			}
		}
	}

	/**
	 * Method for checking fundsAvail confirmation
	 * 
	 * @param afinanceDetail
	 * @return
	 */
	private List<ErrorDetail> doFundsAvailConfirmed(FinanceDetail afinanceDetail) {
		logger.debug(Literal.ENTERING);
		List<ErrorDetail> errors = new ArrayList<>();
		String nextRoleCode = StringUtils.trimToEmpty(afinanceDetail.getFinScheduleData().getFinanceMain()
				.getNextRoleCode());
		String nextRoleCodes[] = nextRoleCode.split(",");

		if (nextRoleCodes.length > 1) {
			afinanceDetail.getFinScheduleData().getFinanceMain().setFundsAvailConfirmed(false);
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("9999", Labels.getLabel("message.Conformation_Check"), null)));
		} else {
			afinanceDetail.getFinScheduleData().getFinanceMain().setFundsAvailConfirmed(true);
		}

		logger.debug(Literal.LEAVING);
		return errors;
	}

	/**
	 * Method for checking DDA Response
	 * 
	 * @param afinanceDetail
	 * @return
	 */
	private List<ErrorDetail> checkDDAResponse(FinanceDetail afinanceDetail) {
		logger.debug(Literal.ENTERING);
		List<ErrorDetail> errors = new ArrayList<>();
		FinanceMain financeMain = afinanceDetail.getFinScheduleData().getFinanceMain();
		if (StringUtils.equals(financeMain.getFinRepayMethod(), FinanceConstants.REPAYMTH_AUTODDA)) {
			try {
				boolean ddaStatus = getDdaControllerService().validateDDAStatus(financeMain.getFinReference());
				boolean ddaDpSpStatus = true;
				boolean isAllowDpSp = afinanceDetail.getFinScheduleData().getFinanceType().isAllowDownpayPgm();
				if (isAllowDpSp) {
					String linkedRef = financeMain.getFinReference() + "_DP";
					ddaDpSpStatus = getDdaControllerService().validateDDAStatus(linkedRef);
				}

				if (ddaStatus && ddaDpSpStatus) {
					//processCompleted = true;//FIXME:change the condition
				}
			} catch (InterfaceException pfe) {
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail(pfe.getErrorCode(), pfe.getErrorMessage(), null)));
			}
		}

		logger.debug(Literal.LEAVING);
		return errors;
	}

	/**
	 * Method for Checking exception List based upon Requirements
	 */
	public AuditHeader doCheckExceptions(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		FinanceDetail aFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		//Check for Exception 
		aFinanceMain.setException(false);

		//*** Case 1 : Amount Case Check Exception for 100K BHD ***
		String dftCcy = SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY);
		final BigDecimal finAmount = PennantApplicationUtil.formateAmount(aFinanceMain.getFinAmount(),
				CurrencyUtil.getFormat(aFinanceMain.getFinCcy()));
		if (dftCcy.equals(aFinanceMain.getFinCcy())) {
			aFinanceMain.setAmount(finAmount);
		} else {
			//Covert Amount into BHD Format 
			Currency fCurrency = CurrencyUtil.getCurrencyObject(aFinanceMain.getFinCcy());
			aFinanceMain.setAmount(finAmount.multiply(fCurrency.getCcySpotRate()));
		}

		if (aFinanceMain.getAmount().compareTo(BigDecimal.valueOf(100000.000)) > 0) {
			aFinanceMain.setException(true);
		}
		aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
		auditHeader.getAuditDetail().setModelData(aFinanceDetail);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Method for send Collateral mark request to interface
	 * 
	 * @param list
	 * @return
	 * @throws InterfaceException
	 * @throws InterruptedException
	 */
	private List<ErrorDetail> doCollateralMark(List<FinCollaterals> list) {
		logger.debug(Literal.ENTERING);

		List<ErrorDetail> errors = new ArrayList<>();
		if (list != null && !list.isEmpty()) {
			CollateralMark collateralMarkRply = getCollateralMarkProcess().markCollateral(list);
			if (collateralMarkRply != null) {
				if (!StringUtils.equals(collateralMarkRply.getReturnCode(), InterfaceConstants.SUCCESS_CODE)) {
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("9999", collateralMarkRply.getReturnText(), null)));
				}
			} else {
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("9999", Labels.getLabel("COLLATERAL_MARK_FAILED"), null)));
			}
		}
		logger.debug(Literal.LEAVING);
		return errors;
	}

	public CollateralMarkProcess getCollateralMarkProcess() {
		return collateralMarkProcess;
	}

	public void setCollateralMarkProcess(CollateralMarkProcess collateralMarkProcess) {
		this.collateralMarkProcess = collateralMarkProcess;
	}

	public DDAControllerService getDdaControllerService() {
		return ddaControllerService;
	}

	public void setDdaControllerService(DDAControllerService ddaControllerService) {
		this.ddaControllerService = ddaControllerService;
	}

	public void setServiceTaskDAO(ServiceTaskDAO serviceTaskDAO) {
		this.serviceTaskDAO = serviceTaskDAO;
	}
}