package com.pennanttech.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.interfacebajaj.fileextract.PresentmentDetailExtract;
import com.pennanttech.model.presentment.Presentment;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pff.notifications.service.NotificationService;
import com.pennanttech.ws.model.presentment.PresentmentResponse;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class PresentmentServiceController extends ExtendedTestClass {
	private static Logger logger = LogManager.getLogger(PresentmentServiceController.class);

	private PresentmentDetailService presentmentDetailService;
	private NotificationService notificationService;
	private DataSource dataSource;
	private PresentmentDetailDAO presentmentDetailDAO;
	private FinanceMainDAO financeMainDAO;

	public PresentmentServiceController() {
		super();
	}

	public PresentmentResponse getExtractedPresentments(PresentmentHeader ph) {
		logger.debug(Literal.ENTERING);
		PresentmentResponse response = new PresentmentResponse();

		List<PresentmentDetail> presements = new ArrayList<>();

		String returnStatus = null;
		try {
			returnStatus = presentmentDetailService.savePresentmentDetails(ph);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		if (!StringUtils.equals(returnStatus, PennantJavaUtil.getLabel("label_PresentmentExtractedMessage"))) {
			String[] valueParm = new String[4];
			valueParm[0] = "No Records";
			valueParm[1] = "found. Change ";
			valueParm[2] = "the search";
			valueParm[3] = "Criteria";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("30550", valueParm));
			return response;
		}

		Collection<Long> presentmentHeaders = ph.getGroups().values();

		presements = setPresentmentDetails(presentmentHeaders);

		if (CollectionUtils.isEmpty(presements)) {
			String[] valueParm = new String[4];
			valueParm[0] = "Extracted Presentments";
			valueParm[1] = "Include List";
			valueParm[2] = "is";
			valueParm[3] = "Empty";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("30550", valueParm));
			return response;
		}

		response.setPresentmentDetails(presements);
		response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		logger.debug(Literal.LEAVING);
		return response;
	}

	private List<PresentmentDetail> setPresentmentDetails(Collection<Long> presentmentHeaders) {
		List<PresentmentDetail> pdList = new ArrayList<>();

		List<Long> headerIdList = new ArrayList<>();

		for (Long headerId : presentmentHeaders) {
			headerIdList.add(headerId);
		}

		pdList.addAll(presentmentDetailDAO.getIncludePresentments(headerIdList));
		return pdList;

	}

	public PresentmentResponse approvePresentments(PresentmentHeader ph) {
		logger.debug(Literal.ENTERING);

		PresentmentResponse response = new PresentmentResponse();

		Long isApproved = presentmentDetailDAO.getApprovedPresentmentCount(ph.getId());

		if (isApproved == null || isApproved > 0) {
			String[] valueParm = new String[4];
			valueParm[0] = "Presentment";
			valueParm[1] = "Header";
			valueParm[2] = "already approved with HeaderId";
			valueParm[3] = String.valueOf(ph.getId());
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("30550", valueParm));
			return response;
		}

		boolean includeExists = this.presentmentDetailDAO.searchIncludeList(ph.getId(), 0);

		if (!includeExists) {
			String[] valueParm = new String[1];
			valueParm[0] = "Include List";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("41002", valueParm));
			return response;
		}

		List<Long> excludeList = presentmentDetailService.getExcludePresentmentDetailIdList(ph.getId(), true);
		ph.setExcludeList(excludeList);
		ph.setUserAction("Submit");

		try {
			presentmentDetailService.updatePresentmentDetails(ph);
			ph.setUserAction("Approve");
			presentmentDetailService.updatePresentmentDetails(ph);

		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		// fetching approved Presentments Count
		Long approvedPresentments = presentmentDetailDAO.getApprovedPresentmentCount(ph.getId());
		if (approvedPresentments <= 0) {
			String[] valueParm = new String[4];
			valueParm[0] = "Approved";
			valueParm[1] = "Presentments";
			valueParm[2] = "not found with";
			valueParm[3] = String.valueOf(ph.getId());
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("30550", valueParm));
			return response;
		}
		response.setApprovedPresentments(approvedPresentments);
		response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		logger.debug(Literal.LEAVING);
		return response;
	}

	public PresentmentResponse getApprovedPresentment(PresentmentDetail presentmentDetail) {
		logger.debug(Literal.ENTERING);

		PresentmentResponse presentmentResponse = new PresentmentResponse();
		try {
			String PresentmentRef = presentmentDetailDAO.getPresentmentReference(presentmentDetail.getId(),
					presentmentDetail.getFinReference());
			if (StringUtils.isBlank(PresentmentRef)) {
				String[] valueParm = new String[4];
				valueParm[0] = "Presentment";
				valueParm[1] = "Details";
				valueParm[2] = "not Found/Approved for presentmentId";
				valueParm[3] = "and finreference";
				presentmentResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("30550", valueParm));
				return presentmentResponse;
			}

			Presentment presentment = presentmentDetailDAO.getPresentmentByBatchId(PresentmentRef, "");
			if (presentment == null) {
				String[] valueParm = new String[4];
				valueParm[0] = "Presentment";
				valueParm[1] = "Details";
				valueParm[2] = "not approved for id";
				valueParm[3] = String.valueOf(presentmentDetail.getId());
				presentmentResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("30550", valueParm));
				return presentmentResponse;
			}
			BigDecimal chequeAmount = presentment.getChequeAmount().multiply(new BigDecimal(100));
			presentment.setChequeAmount(chequeAmount);
			presentmentResponse.setPresentment(presentment);
			presentmentResponse.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			presentmentResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return presentmentResponse;
		}
		logger.debug(Literal.LEAVING);
		return presentmentResponse;

	}

	public WSReturnStatus uploadPresentment(Presentment presentment) {
		logger.debug(Literal.ENTERING);

		PresentmentDetailExtract detailExtract = new PresentmentDetailExtract(dataSource, presentmentDetailService,
				notificationService);
		PostingsPreparationUtil ppu = (PostingsPreparationUtil) SpringBeanUtil.getBean("postingsPreparationUtil");
		FinanceRepaymentsDAO frDAO = (FinanceRepaymentsDAO) SpringBeanUtil.getBean("financeRepaymentsDAO");
		detailExtract.setPostingsPreparationUtil(ppu);
		detailExtract.setFinanceRepaymentsDAO(frDAO);
		try {
			detailExtract.clearTables();

			String status = presentmentDetailDAO.getPresementStatus(presentment.getBatchId());

			if (StringUtils.isBlank(status)) {
				String[] valueParm = new String[4];
				valueParm[0] = "Presentment Details not available";
				valueParm[1] = "or already processed";
				valueParm[2] = "for batch id";
				valueParm[3] = presentment.getBatchId();
				return APIErrorHandlerService.getFailedStatus("30550", valueParm);
			}

			if (RepayConstants.PEXC_SUCCESS.equals(status) || RepayConstants.PEXC_BOUNCE.equals(status)) {
				String[] valueParm = new String[4];
				valueParm[0] = "The Presentment with";
				valueParm[1] = "the presentment reference";
				valueParm[2] = presentment.getBatchId();
				valueParm[3] = "already processed";
				return APIErrorHandlerService.getFailedStatus("30550", valueParm);
			}

			PresentmentDetail presentmentDetail = presentmentDetailDAO.getPresentmentDetail(presentment.getBatchId());

			if (presentmentDetail != null && SysParamUtil.getAppDate().compareTo(presentmentDetail.getSchDate()) < 0) {
				String[] valueParm = new String[4];
				valueParm[0] = "The presentment not";
				valueParm[1] = "proceed with schedule date";
				valueParm[2] = "greater than";
				valueParm[3] = "application bussiness date";
				return APIErrorHandlerService.getFailedStatus("30550", valueParm);
			}

			if (!presentmentDetail.getFinReference().equals(presentment.getAgreementNo())) {
				String[] valueParm = new String[4];
				valueParm[0] = "FinReference";
				valueParm[1] = "is Invalid";
				valueParm[2] = ":";
				valueParm[3] = presentment.getAgreementNo();
				return APIErrorHandlerService.getFailedStatus("30550", valueParm);
			}

			MapSqlParameterSource map = defaultReadData(presentment);
			detailExtract.insertData(map);
			detailExtract.processingPrsentments();

		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug(Literal.LEAVING);
		return APIErrorHandlerService.getSuccessStatus();

	}

	public MapSqlParameterSource defaultReadData(Presentment presentment) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("BranchCode", presentment.getBrCode());
		map.addValue("AgreementNo", presentment.getAgreementNo());
		map.addValue("InstalmentNo", "0");
		map.addValue("BFLReferenceNo", presentment.getBrCode());
		map.addValue("Batchid", presentment.getBatchId());
		map.addValue("AmountCleared", presentment.getChequeAmount());
		map.addValue("ClearingDate", presentment.getSetilmentDate());
		map.addValue("Status", presentment.getStatus());

		map.addValue("Name", presentment.getDestAccHolder());
		map.addValue("UMRNNo", presentment.getUmrnNo());
		map.addValue("AccountType", presentment.getAccType());
		map.addValue("PaymentDue", presentment.getCycleDate());
		map.addValue("ReasonCode", presentment.getReturnReason());

		map.addValue("Failure reason", presentment.getReturnReason());
		logger.debug(Literal.LEAVING);

		return map;
	}

	public PresentmentDetailService getPresentmentDetailService() {
		return presentmentDetailService;
	}

	public void setPresentmentDetailService(PresentmentDetailService presentmentDetailService) {
		this.presentmentDetailService = presentmentDetailService;
	}

	public NotificationService getNotificationService() {
		return notificationService;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public PresentmentDetailDAO getPresentmentDetailDAO() {
		return presentmentDetailDAO;
	}

	public void setPresentmentDetailDAO(PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
}
