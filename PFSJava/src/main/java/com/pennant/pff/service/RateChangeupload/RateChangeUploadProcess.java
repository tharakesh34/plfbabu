package com.pennant.pff.service.RateChangeupload;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jaxen.JaxenException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.RateChangeUploadDAO;
import com.pennant.backend.financeservice.RateChangeService;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.FinanceConstants;
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

public class RateChangeUploadProcess extends BasicDao<RateChangeUpload> {
	private RateChangeUploadDAO rateChangeUploadDAO;
	private PlatformTransactionManager transactionManager;
	private RateChangeService rateChangeService;
	private FinanceDetailService financeDetailService;

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
			if (totalRecords == header.getFailureRecords()) {
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
			FinServiceInstruction finInst = null;
			try {
				finInst = prepareFinServiceInstruction(rcu);
			} catch (Exception e) {
				rcu.setStatus("F");
				rcu.setRemarks("Error While Preparing Service Instructions");
				rateChangeUploadDAO.updateRateChangeDetails(rcu);
				transactionManager.commit(txStatus);
				logger.debug(Literal.EXCEPTION, e);
				fail = fail + 1;
				continue;
			}

			// validate service instruction data
			AuditDetail auditDetail = rateChangeService.doValidations(finInst);

			if (CollectionUtils.isNotEmpty(auditDetail.getErrorDetails())) {
				rcu.setStatus("F");
				rcu.setRemarks(auditDetail.getErrorDetails().get(0).getError());
				rateChangeUploadDAO.updateRateChangeDetails(rcu);
				transactionManager.commit(txStatus);
				fail = fail + 1;
				continue;
			}

			// fetch finance data
			String eventCode = AccountEventConstants.ACCEVENT_RATCHG;
			FinanceDetail financeDetail = financeDetailService.getFinSchdDetailById(finReference, "_AView", false);

			financeDetail.setAccountingEventCode(eventCode);
			AuditHeader auditHeader = null;
			try {
				auditHeader = preFinSchdData(finInst, financeDetail);
			} catch (Exception e) {
				txStatus = transactionManager.getTransaction(txDef);
				rcu.setStatus("F");
				if (StringUtils.isNotBlank(e.getMessage())) {
					rcu.setRemarks(e.getMessage().substring(0, 1999));
				} else {
					rcu.setRemarks("Un-Handled Exception");
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

			if (StringUtils.isNotEmpty(errorMsg)) {
				rcu.setStatus("F");
				rcu.setRemarks(code + " : " + errorMsg);
				fail = fail + 1;
			} else {
				rcu.setStatus("S");
				rcu.setRemarks("");
				sucess = sucess + 1;
			}
			// rcu.setStatus("S");
			// sucess++;
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
		finMain.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_RATECHG);
		
		if (CalculationConstants.RPYCHG_TILLMDT.equals(fsi.getRecalType())) {
			finMain.setRecalToDate(finMain.getMaturityDate());
		} else if (CalculationConstants.RPYCHG_TILLDATE.equals(fsi.getRecalType())) {
			finMain.setRecalToDate(fsi.getRecalToDate());
		}
		
		if (StringUtils.isBlank(fsi.getPftDaysBasis())) {
			fsi.setPftDaysBasis(finMain.getProfitDaysBasis());
		}

		fsi.setModuleDefiner(FinanceConstants.FINSER_EVENT_RATECHG);

		// call schedule calculator for Rate change
		fsd = rateChangeService.getRateChangeDetails(fsd, fsi,
				FinanceConstants.FINSER_EVENT_RATECHG);
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
		finMain.setUserDetails(user);
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
				financeDetail.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}

	private FinServiceInstruction prepareFinServiceInstruction(RateChangeUpload rcu) throws ParseException {
		FinServiceInstruction fsi = new FinServiceInstruction();
	
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
		List<FinanceMain> finMain = rateChangeUploadDAO.getFinanceMain(header.getId());
		
		logger.info("Extracting finance details...");
		
		String error = "Loan is Not Active.";
		for (RateChangeUpload rcu : header.getRateChangeUpload()) {
			StringBuilder remarks = new StringBuilder(StringUtils.trimToEmpty(rcu.getRemarks()));
			for (FinanceMain fm : finMain) {
				if (rcu.getFinReference().equals(fm.getFinReference())) {
					rcu.setFinanceMain(fm);
					if (!fm.isFinIsActive()) {
						if (remarks.length() > 0) {
							remarks.append(", ");
						}
						remarks.append(error);
						rcu.setErrorDetail(getErrorDetail("RCU002", error));
					}
					break;
				}
			}
			rcu.setRemarks(remarks.toString());
		}

		error = "Loan not exists.";
		for (RateChangeUpload rcu : header.getRateChangeUpload()) {
			StringBuilder remarks = new StringBuilder(rcu.getRemarks());
			if (rcu.getFinanceMain() == null) {
				if (remarks.length() > 0) {
					remarks.append(", ");
				}
				remarks.append(error);
				rcu.setRemarks(remarks.toString());
				rcu.setErrorDetail(getErrorDetail("RCU002", error));
			}
		}
 
		error = "Entity Code is Invalid.";
		for(RateChangeUpload rcu : header.getRateChangeUpload()) {
			StringBuilder remarks = new StringBuilder(rcu.getRemarks());
			if(!StringUtils.equals(rcu.getFinanceMain().getEntityCode(), header.getEntityCode())) {
				if(remarks.length() > 0) {
					remarks.append(",");
				}
				remarks.append(error);
				rcu.setRemarks(remarks.toString());
				rcu.setErrorDetail(getErrorDetail("RCU003", error));
			}
		}

	}

	private void validate(RateChangeUploadHeader rateChangeUploadHeader) {
		logger.info("Validationg the records...");
		
		for (RateChangeUpload rateChange : rateChangeUploadHeader.getRateChangeUpload()) {
			StringBuilder remarks = new StringBuilder(rateChange.getRemarks());

			String error = "BaseRate Code is not valid";
			if (StringUtils.isNotEmpty(rateChange.getBaseRateCode())) {
				boolean baseRate = rateChangeUploadDAO.getRateCodes(rateChange.getBaseRateCode());
				if (!baseRate) {
					if (remarks.length() > 0) {
						remarks.append(", ");
					}
					remarks.append(error);
					rateChange.setRemarks(remarks.toString());
					rateChange.setStatus("F");
					rateChange.setErrorDetail(getErrorDetail("RCU001", error));
					rateChange.setRemarks(remarks.toString());
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
				rateChange.setRemarks(remarks.toString());
				rateChange.setStatus("F");
				rateChange.setErrorDetail(getErrorDetail("RCU001", error));
				rateChange.setRemarks(remarks.toString());
				continue;

			}

			error = "Either Base Rate or Actual Rate Should be Entered";
			if (rateChange.getMargin().compareTo(BigDecimal.ZERO) > 0
					&& rateChange.getActualRate().compareTo(BigDecimal.ZERO) > 0) {
				if (remarks.length() > 0) {
					remarks.append(", ");
				}
				remarks.append(error);
				rateChange.setRemarks(remarks.toString());
				rateChange.setStatus("F");
				rateChange.setErrorDetail(getErrorDetail("RCU001", error));
				rateChange.setRemarks(remarks.toString());
				continue;

			}
			
			error = "Either Base Rate or Actual Rate Should be Entered";
			if (StringUtils.isBlank(rateChange.getBaseRateCode())
					&& rateChange.getActualRate().compareTo(BigDecimal.ZERO) <= 0 && rateChange.getMargin().compareTo(BigDecimal.ZERO)<= 0) {
				if (remarks.length() > 0) {
					remarks.append(", ");
				}
				remarks.append(error);
				rateChange.setRemarks(remarks.toString());
				rateChange.setStatus("F");
				rateChange.setErrorDetail(getErrorDetail("RCU001", error));
				rateChange.setRemarks(remarks.toString());
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

			error = "Actual Rate should be lesser/Equal to 9999 and greater than 0";
			if (rateChange.getActualRate().intValue() > 9999 || rateChange.getActualRate().intValue() < 0) {
				if (remarks.length() > 0) {
					remarks.append(", ");
				}
				remarks.append(error);
				rateChange.setErrorDetail(getErrorDetail("RCU004", error));
			}

			if (remarks.length() > 0) {
				rateChange.setRemarks(remarks.toString());
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

}
