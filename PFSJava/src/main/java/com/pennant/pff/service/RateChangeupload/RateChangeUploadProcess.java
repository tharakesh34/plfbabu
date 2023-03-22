package com.pennant.pff.service.RateChangeupload;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jaxen.JaxenException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BaseRateDAO;
import com.pennant.backend.dao.finance.RateChangeUploadDAO;
import com.pennant.backend.financeservice.RateChangeService;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.pff.model.ratechangeupload.RateChangeUpload;
import com.pennant.pff.model.ratechangeupload.RateChangeUploadHeader;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Type;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;

public class RateChangeUploadProcess extends BasicDao<RateChangeUpload> {
	private RateChangeUploadDAO rateChangeUploadDAO;
	private PlatformTransactionManager transactionManager;
	private RateChangeService rateChangeService;
	private FinanceDetailService financeDetailService;
	private static BaseRateDAO baseRateDAO;

	public void process(RateChangeUploadHeader header) throws Exception {
		header.setStatus(header.getDeStatus().getStatus());
		header.setTotalRecords((int) header.getDeStatus().getTotalRecords());

		List<RateChangeUpload> uploads = header.getRateChangeUpload();

		int totalRecords = header.getTotalRecords();
		if (totalRecords != uploads.size()) {
			header.setFailureRecords(totalRecords - uploads.size());
		}
		if (header == null || uploads.isEmpty()) {
			header.setStatus("F");
			rateChangeUploadDAO.updateRemarks(header);
			return;
		}
		try {

			setFinancedetails(header);

			validate(header);

			processRateChange(header);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			if (header.getFailureRecords() > 0) {
				header.setStatus("F");
				if (App.TYPE == Type.WEB) {
					header.getDeStatus().setStatus(header.getStatus());
				}
			} else {
				header.setStatus("S");
			}
			rateChangeUploadDAO.updateRemarks(header);
			for (RateChangeUpload rcu : uploads) {
				rateChangeUploadDAO.logRcUpload(rcu.getErrorDetails(), rcu.getId());
			}

			if (App.TYPE == Type.WEB) {
				rateChangeUploadDAO.updateDeRemarks(header, header.getDeStatus());
			}
		}

	}

	private void processRateChange(RateChangeUploadHeader header) {
		int fail = header.getFailureRecords();
		int sucess = header.getSucessRecords();
		for (RateChangeUpload rcu : header.getRateChangeUpload()) {
			TransactionStatus txStatus = null;
			String finReference = rcu.getFinReference();
			String status = rcu.getStatus();
			logger.info("Processing RateChange upload>> {}:", finReference);

			if ("F".equals(status)) {
				rateChangeUploadDAO.updateRateChangeDetails(rcu);
				fail++;
				continue;
			}

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
			txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

			txStatus = this.transactionManager.getTransaction(txDef);

			// Process Rate Change
			FinServiceInstruction fsi = null;
			try {
				fsi = prepareFinServiceInstruction(rcu);
			} catch (Exception e) {
				rcu.setStatus("F");
				rcu.setUploadStatusRemarks("Error While Preparing Service Instructions");
				rateChangeUploadDAO.updateRateChangeDetails(rcu);
				transactionManager.commit(txStatus);
				logger.debug(Literal.EXCEPTION, e);
				fail = fail + 1;
				continue;
			}

			// validate service instruction data
			AuditDetail auditDetail = rateChangeService.doValidations(fsi);

			if (CollectionUtils.isNotEmpty(auditDetail.getErrorDetails())) {
				rcu.setStatus("F");
				rcu.setUploadStatusRemarks(auditDetail.getErrorDetails().get(0).getError());
				rateChangeUploadDAO.updateRateChangeDetails(rcu);
				transactionManager.commit(txStatus);
				fail = fail + 1;
				continue;
			}

			// fetch finance data
			String eventCode = AccountingEvent.RATCHG;
			FinanceDetail financeDetail = financeDetailService.getServicingFinance(rcu.getFinID(), eventCode,
					FinServiceEvent.RATECHG, "");

			financeDetail.getFinScheduleData().setFinFeeDetailList(new ArrayList<>());
			financeDetail.setCovenantTypeList(new ArrayList<>());

			financeDetail.setAccountingEventCode(eventCode);
			AuditHeader auditHeader = null;
			try {
				auditHeader = preFinSchdData(fsi, financeDetail);
			} catch (Exception e) {
				txStatus = transactionManager.getTransaction(txDef);
				rcu.setStatus("F");
				if (StringUtils.isNotBlank(e.getMessage())) {
					if (e.getMessage().length() > 2000) {
						rcu.setUploadStatusRemarks(e.getMessage().substring(0, 1999));
					} else {
						rcu.setUploadStatusRemarks(e.getMessage());
					}
				} else {
					rcu.setUploadStatusRemarks("Un-Handled Exception");
				}
				rateChangeUploadDAO.updateRateChangeDetails(rcu);
				logger.debug(Literal.EXCEPTION, e);
				transactionManager.commit(txStatus);
				fail = fail + 1;
				continue;
			}

			String errorMsg = "";
			String code = "";
			if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
				errorMsg = auditHeader.getErrorMessage().get(0).getError();
				code = auditHeader.getErrorMessage().get(0).getCode();
			}

			List<ErrorDetail> errorDetails = auditHeader.getAuditDetail().getErrorDetails();
			if (CollectionUtils.isNotEmpty(errorDetails)) {
				errorMsg = errorDetails.get(0).getError();
				code = errorDetails.get(0).getCode();
			}

			if (StringUtils.isNotEmpty(errorMsg)) {
				rcu.setStatus("F");
				rcu.setUploadStatusRemarks(code + " : " + errorMsg);
				fail = fail + 1;
			} else {
				rcu.setStatus("S");
				rcu.setUploadStatusRemarks("");
				sucess = sucess + 1;
			}
			rateChangeUploadDAO.updateRateChangeDetails(rcu);

			this.transactionManager.commit(txStatus);
			logger.info("Completed RateChange upload >> {}:", finReference);

		}

		header.setFailureRecords(fail);
		header.setSucessRecords(sucess);

	}

	private AuditHeader preFinSchdData(FinServiceInstruction fsi, FinanceDetail fd)
			throws InterfaceException, JaxenException {
		FinScheduleData fsd = fd.getFinScheduleData();
		FinanceMain finMain = fsd.getFinanceMain();

		finMain.setEventFromDate(fsi.getFromDate());
		finMain.setEventToDate(fsi.getToDate());
		finMain.setRecalFromDate(fsi.getRecalFromDate());
		finMain.setRecalType(fsi.getRecalType());
		finMain.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		finMain.setRecalSchdMethod(finMain.getScheduleMethod());
		finMain.setRcdMaintainSts(FinServiceEvent.RATECHG);

		if (CalculationConstants.RPYCHG_TILLMDT.equals(fsi.getRecalType())) {
			finMain.setRecalToDate(finMain.getMaturityDate());
		} else if (CalculationConstants.RPYCHG_TILLDATE.equals(fsi.getRecalType())) {
			finMain.setRecalToDate(fsi.getRecalToDate());
		}

		if (StringUtils.isBlank(fsi.getPftDaysBasis())) {
			fsi.setPftDaysBasis(finMain.getProfitDaysBasis());
		}

		fsi.setModuleDefiner(FinServiceEvent.RATECHG);

		// call schedule calculator for Rate change
		fsd = rateChangeService.getRateChangeDetails(fsd, fsi, FinServiceEvent.RATECHG);
		fd.setFinScheduleData(fsd);
		int version = fd.getFinScheduleData().getFinanceMain().getVersion();
		finMain.setVersion(version + 1);
		finMain.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		fsd.setSchduleGenerated(true);

		LoggedInUser user = null;
		if (SessionUserDetails.getLogiedInUser() != null) {
			user = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		} else {
			user = new LoggedInUser();
			user.setLoginUsrID(0);
			user.setUsrLanguage(SysParamUtil.getValueAsString("APP_LNG"));
		}

		fd.setUserDetails(user);
		fd.setModuleDefiner(FinServiceEvent.RATECHG);
		fd.setCustomerDetails(new CustomerDetails());
		fd.getCustomerDetails().setCustID(finMain.getCustID());
		finMain.setUserDetails(user);
		finMain.setAccountsOfficerReference(String.valueOf(finMain.getAccountsOfficer()));
		finMain.setDmaCodeReference(String.valueOf(finMain.getDmaCode()));
		BigDecimal pftChg = fsd.getPftChg();
		fsi.setPftChg(pftChg);
		fsd.getFinServiceInstructions().add(fsi);
		// Saving and updating the existing
		fd.setDirectFinalApprove(true);

		AuditHeader auditHeader = getAuditHeader(fd, PennantConstants.TRAN_WF);

		auditHeader = financeDetailService.doApprove(auditHeader, fsi.isWif());

		return auditHeader;
	}

	private AuditHeader getAuditHeader(FinanceDetail financeDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, financeDetail.getBefImage(), financeDetail);
		return new AuditHeader(financeDetail.getFinScheduleData().getFinReference(), null, null, null, auditDetail,
				financeDetail.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	private FinServiceInstruction prepareFinServiceInstruction(RateChangeUpload rcu) throws ParseException {
		FinServiceInstruction fsi = new FinServiceInstruction();

		fsi.setFinID(rcu.getFinID());
		fsi.setFinReference(rcu.getFinReference());
		fsi.setBaseRate(rcu.getBaseRateCode());
		fsi.setSplRate(null);
		fsi.setMargin(rcu.getMargin());
		fsi.setActualRate(rcu.getActualRate());
		fsi.setRecalType(rcu.getRecalType());
		fsi.setRemarks(rcu.getUploadStatusRemarks());
		fsi.setServiceReqNo(rcu.getServiceReqNo());
		fsi.setReqFrom(UploadConstants.RATE_CHANGE_UPLOAD);
		fsi.setFinEvent(UploadConstants.RATE_CHANGE_UPLOAD);
		fsi.setReference(String.valueOf(rcu.getId()));
		fsi.setRecalFromDate(parseDate(rcu.getRecalFromDate()));
		fsi.setRecalToDate(parseDate(rcu.getRecalToDate()));
		fsi.setFromDate(parseDate(rcu.getFromDate()));
		fsi.setToDate(parseDate(rcu.getToDate()));
		fsi.setModuleDefiner(FinServiceEvent.RATECHG);

		return fsi;
	}

	private Date parseDate(Date date) throws ParseException {
		if (date == null) {
			return null;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(PennantConstants.DBDateFormat);
		return dateFormat.parse(date.toString());
	}

	private ErrorDetail getErrorDetail(String code, String message) {
		ErrorDetail ed = new ErrorDetail();
		ed.setCode(code);
		ed.setMessage(message);
		return ed;
	}

	private void setFinancedetails(RateChangeUploadHeader header) {
		List<FinanceMain> fmList = rateChangeUploadDAO.getFinanceMain(header.getId());

		logger.info("Extracting finance details...");

		String error = "Loan is Not Active.";
		for (RateChangeUpload rcu : header.getRateChangeUpload()) {
			StringBuilder remarks = new StringBuilder(StringUtils.trimToEmpty(rcu.getUploadStatusRemarks()));
			for (FinanceMain fm : fmList) {
				if (rcu.getFinReference().equals(fm.getFinReference())) {
					rcu.setFinID(fm.getFinID());
					rcu.setFinanceMain(fm);
				}
			}

			if (fmList.isEmpty()) {
				error = "Not valid Loan.";
				if (remarks.length() > 0) {
					remarks.append(", ");
				}

				remarks.append(error);
				rcu.setUploadStatusRemarks(remarks.toString());
				rcu.setErrorDetail(getErrorDetail("RCU002", error));
				break;
			}

			FinanceDetail fd = financeDetailService.getFinSchdDetailById(rcu.getFinID(), "_AView", false);
			List<FinanceScheduleDetail> schedules = fd.getFinScheduleData().getFinanceScheduleDetails();
			FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

			List<Date> dates = new ArrayList<>();
			Set<String> tempSet = new HashSet<>();

			for (FinanceMain finMain : fmList) {
				if (rcu.getFinReference().equals(finMain.getFinReference())) {
					rcu.setFinanceMain(finMain);
					if (!finMain.isFinIsActive()) {
						if (remarks.length() > 0) {
							remarks.append(", ");
						}
						remarks.append(error);
						rcu.setUploadStatusRemarks(remarks.toString());
						rcu.setErrorDetail(getErrorDetail("RCU002", error));
					}
					break;
				}
			}

			error = "Duplicate Reference .";
			if (!tempSet.add(rcu.getFinReference())) {
				if (remarks.length() > 0) {
					remarks.append(", ");
				}

				remarks.append(error);
				rcu.setUploadStatusRemarks(remarks.toString());
				rcu.setErrorDetail(getErrorDetail("RCU002", error));
				break;
			}

			error = "Loan not exists.";
			if (rcu.getFinanceMain() == null) {
				if (remarks.length() > 0) {
					remarks.append(", ");
				}

				remarks.append(error);
				rcu.setUploadStatusRemarks(remarks.toString());
				rcu.setErrorDetail(getErrorDetail("RCU002", error));
			}

			error = "Entity Code is Invalid.";
			String code = rcu.getFinanceMain().getEntityCode();
			if (!StringUtils.equals(code, header.getEntityCode())) {
				if (remarks.length() > 0) {
					remarks.append(",");
				}

				remarks.append(error);
				rcu.setUploadStatusRemarks(remarks.toString());
				rcu.setErrorDetail(getErrorDetail("RCU003", error));
			}
			Date appDate = SysParamUtil.getAppDate();
			error = " RateChange From Date should be after ";

			if (rcu.getFromDate() == null) {
				rcu.setFromDate(appDate);
			}

			List<FinanceScheduleDetail> finSchdDetails = fd.getFinScheduleData().getFinanceScheduleDetails();
			for (FinanceScheduleDetail fsd : finSchdDetails) {
				if (fsd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0
						|| fsd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0 || fsd.getPresentmentId() > 0) {
					dates.add(fsd.getSchDate());
				}

				for (Date lastPaidDate : dates) {
					if (DateUtil.compare(rcu.getFromDate(), lastPaidDate) < 0) {
						if (remarks.length() > 0) {
							remarks.append(",");
						}

						lastPaidDate = dates.get(dates.size() - 1);
						remarks.append(error + DateUtil.formatToLongDate(lastPaidDate));
						rcu.setUploadStatusRemarks(remarks.toString());
						rcu.setErrorDetail(getErrorDetail("RCU003", error));
						break;
					}
					lastPaidDate = dates.get(dates.size() - 1);
					remarks.append(error + DateUtil.formatToLongDate(lastPaidDate));
					rcu.setUploadStatusRemarks(remarks.toString());
					rcu.setErrorDetail(getErrorDetail("RCU003", error));
					break;
				}
			}

			error = " RateChange To Date should be after ";
			if (rcu.getToDate() != null) {
				for (Date lastPaidDate : dates) {
					if (DateUtil.compare(rcu.getToDate(), lastPaidDate) <= 0) {
						if (remarks.length() > 0) {
							remarks.append(",");
						}

						lastPaidDate = dates.get(dates.size() - 1);
						remarks.append(error + DateUtil.formatToLongDate(lastPaidDate));
						rcu.setUploadStatusRemarks(remarks.toString());
						rcu.setErrorDetail(getErrorDetail("RCU003", error));
						break;
					}
				}
			}

			error = "Recal From Date Should Be Selected Proper Schedule Date";
			if (CalculationConstants.RPYCHG_TILLDATE.equals(rcu.getRecalType())) {
				Date befinst = DateUtility.addMonths(fm.getMaturityDate(), -1);
				if (DateUtility.compare(rcu.getRecalFromDate(), fm.getMaturityDate()) == 0
						|| DateUtility.compare(rcu.getRecalFromDate(), befinst) == 0) {
					if (remarks.length() > 0) {
						remarks.append(",");
					}

					remarks.append(error);
					rcu.setUploadStatusRemarks(remarks.toString());
					rcu.setErrorDetail(getErrorDetail("RCU0004", error));
					break;

				}
			}

			error = "Recal To Date Should not be Maturity Date when Recal Type is Till Date ";
			if (CalculationConstants.RPYCHG_TILLDATE.equals(rcu.getRecalType())) {
				if (DateUtility.compare(rcu.getRecalToDate(), fm.getMaturityDate()) == 0) {
					if (remarks.length() > 0) {
						remarks.append(",");
					}

					remarks.append(error);
					rcu.setUploadStatusRemarks(remarks.toString());
					rcu.setErrorDetail(getErrorDetail("RCU0004", error));
					break;
				}
			}

			error = "Recal From Date Sholud not be MaturityDate when Recal Type is Till maturity";
			if (CalculationConstants.RPYCHG_TILLMDT.equals(rcu.getRecalType())) {
				if (DateUtil.compare(rcu.getRecalFromDate(), fm.getMaturityDate()) == 0) {
					if (remarks.length() > 0) {
						remarks.append(",");
					}

					remarks.append(error);
					rcu.setUploadStatusRemarks(remarks.toString());
					rcu.setErrorDetail((getErrorDetail("RCU0005", error)));
					break;
				}
			}

			error = "Recal From Date and Recal To Data should be empty with Recal Type is Adjust Terms or Current Period";
			if (CalculationConstants.RPYCHG_ADJTERMS.equals(rcu.getRecalType())
					|| CalculationConstants.RPYCHG_CURPRD.equals(rcu.getRecalType())) {
				if (rcu.getRecalFromDate() != null || rcu.getRecalToDate() != null) {
					if (remarks.length() > 0) {
						remarks.append(",");
					}

					remarks.append(error);
					rcu.setUploadStatusRemarks(remarks.toString());
					rcu.setErrorDetail((getErrorDetail("RCU0005", error)));
					break;
				}
			}
		}
	}

	private void validate(RateChangeUploadHeader rateChangeUploadHeader) {
		logger.info("Validationg the records...");

		for (RateChangeUpload rateChange : rateChangeUploadHeader.getRateChangeUpload()) {
			StringBuilder remarks = new StringBuilder(StringUtils.trimToEmpty(rateChange.getUploadStatusRemarks()));

			String error = "BaseRate Code is not valid";
			if (StringUtils.isNotEmpty(rateChange.getBaseRateCode())) {
				boolean baseRate = rateChangeUploadDAO.getRateCodes(rateChange.getBaseRateCode());
				if (!baseRate) {
					if (remarks.length() > 0) {
						remarks.append(", ");
					}
					remarks.append(error);
					rateChange.setUploadStatusRemarks(remarks.toString());
					rateChange.setStatus("F");
					rateChange.setErrorDetail(getErrorDetail("RCU001", error));
					continue;
				}
			}

			error = "Margin Not Allowed With Out base Rate";
			if (StringUtils.isBlank(rateChange.getBaseRateCode())
					&& rateChange.getMargin().compareTo(BigDecimal.ZERO) != 0 && rateChange.getMargin() != null) {
				if (remarks.length() > 0) {
					remarks.append(", ");
				}
				remarks.append(error);
				rateChange.setUploadStatusRemarks(remarks.toString());
				rateChange.setStatus("F");
				rateChange.setErrorDetail(getErrorDetail("RCU001", error));
				continue;
			}

			if (StringUtils.isNotBlank(rateChange.getBaseRateCode())) {
				error = "Interest Rate Codes not available for the Schedule date.";
				FinanceDetail financeDetail = financeDetailService.getFinSchdDetailByRef(rateChange.getFinReference(),
						"_AView", false);
				FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
				List<BaseRate> baseRatesHist = baseRateDAO.getBaseRateHistByType(rateChange.getBaseRateCode(),
						finMain.getFinCcy(), finMain.getFinStartDate());
				if (baseRatesHist.isEmpty()) {
					if (remarks.length() > 0) {
						remarks.append(", ");
					}
					remarks.append(error);
					rateChange.setUploadStatusRemarks(remarks.toString());
					rateChange.setStatus("F");
					rateChange.setErrorDetail(getErrorDetail("RCU001", error));
					continue;
				}
			}

			error = "Either Base Rate or Actual Rate Should be Entered";
			if (StringUtils.isNotBlank(rateChange.getBaseRateCode())
					&& rateChange.getActualRate().compareTo(BigDecimal.ZERO) > 0) {
				if (remarks.length() > 0) {
					remarks.append(", ");
				}
				remarks.append(error);
				rateChange.setUploadStatusRemarks(remarks.toString());
				rateChange.setStatus("F");
				rateChange.setErrorDetail(getErrorDetail("RCU001", error));
				continue;

			}

			error = "Either Base Rate or Actual Rate Should be Entered";
			if (rateChange.getMargin().compareTo(BigDecimal.ZERO) > 0
					&& rateChange.getActualRate().compareTo(BigDecimal.ZERO) > 0) {
				if (remarks.length() > 0) {
					remarks.append(", ");
				}
				remarks.append(error);
				rateChange.setUploadStatusRemarks(remarks.toString());
				rateChange.setStatus("F");
				rateChange.setErrorDetail(getErrorDetail("RCU001", error));
				continue;

			}

			/*
			 * if (rateChange.getSpecialRate() != null || StringUtils.isNotEmpty(rateChange.getSpecialRate())) { error =
			 * "SpecialRate Code is not valid"; boolean splRate =
			 * rateChangeUploadDAO.getSplRateCodes(rateChange.getSpecialRate()); if (!splRate) { if (remarks.length() >
			 * 0) { remarks.append(", "); } remarks.append(error); rateChange.setRemarks(remarks.toString());
			 * rateChange.setStatus("F"); rateChange.setErrorDetail(getErrorDetail("RCU001", error));
			 * rateChange.setRemarks(remarks.toString()); continue; } }
			 */
			error = "Margin should be greater/Equal to -9999 and lesser/Equal to 9999 ";
			if (rateChange.getMargin().intValue() > 9999 || rateChange.getMargin().intValue() < -9999) {
				if (remarks.length() > 0) {
					remarks.append(", ");
				}
				remarks.append(error);
				rateChange.setErrorDetail(getErrorDetail("RCU004", error));
			}

			error = "Actual Rate should be lesser/Equal to 9999 and greater than/Equal to 0";
			if (rateChange.getActualRate().intValue() > 9999 || rateChange.getActualRate().intValue() < 0) {
				if (remarks.length() > 0) {
					remarks.append(", ");
				}
				remarks.append(error);
				rateChange.setErrorDetail(getErrorDetail("RCU004", error));
			}

			if (remarks.length() > 0) {
				rateChange.setUploadStatusRemarks(remarks.toString());
				rateChange.setStatus("F");
			}
		}
	}

	public void setRateChangeUploadDAO(RateChangeUploadDAO rateChangeUploadDAO) {
		this.rateChangeUploadDAO = rateChangeUploadDAO;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setRateChangeService(RateChangeService rateChangeService) {
		this.rateChangeService = rateChangeService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public static void setBaseRateDAO(BaseRateDAO baseRateDAO) {
		RateChangeUploadProcess.baseRateDAO = baseRateDAO;
	}

}
