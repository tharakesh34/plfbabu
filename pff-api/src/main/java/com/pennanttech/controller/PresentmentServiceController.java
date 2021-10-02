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
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.ws.model.presentment.PresentmentResponse;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class PresentmentServiceController extends ExtendedTestClass {
	private static Logger logger = LogManager.getLogger(PresentmentServiceController.class);

	private PresentmentDetailService presentmentDetailService;
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

			logger.debug(Literal.LEAVING);
			return response;
		}

		if (!StringUtils.equals(returnStatus, PennantJavaUtil.getLabel("label_PresentmentExtractedMessage"))) {
			String[] valueParm = new String[4];
			valueParm[0] = "No Records";
			valueParm[1] = "found. Change ";
			valueParm[2] = "the search";
			valueParm[3] = "Criteria";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("30550", valueParm));

			logger.debug(Literal.LEAVING);
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

			logger.debug(Literal.LEAVING);
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

		List<Long> includeList = presentmentDetailService.getIncludeList(ph.getId());
		ph.setIncludeList(includeList);

		List<Long> excludeList = presentmentDetailService.getManualExcludeList(ph.getId());
		ph.setExcludeList(excludeList);

		List<PresentmentDetail> presentments = ph.getPresentmentDetailsList();
		if (CollectionUtils.isNotEmpty(presentments)) {
			for (PresentmentDetail pd : presentments) {
				if (includeList.contains(pd.getId())) {
					excludeList.add(pd.getId());
					includeList.remove(pd.getId());
				} else {
					String[] valueParm = new String[4];
					valueParm[0] = "Presentment Id: ";
					valueParm[1] = String.valueOf(pd.getId());
					valueParm[2] = "is not present in";
					valueParm[3] = "IncludeList";
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus("30550", valueParm));
					return response;
				}
			}

			ph.setExcludeList(excludeList);
			ph.setIncludeList(includeList);

			presentmentDetailService.saveModifiedPresentments(excludeList, includeList, ph.getId(),
					ph.getPartnerBankId());
		}

		// checking include List exists or not in Presentment Batch
		includeExists = this.presentmentDetailService.searchIncludeList(ph.getId(), 0);
		if (!includeExists) {
			String[] valueParm = new String[1];
			valueParm[0] = "Include List";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("41002", valueParm));
			return response;
		}

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

	public PresentmentResponse getApprovedPresentment(PresentmentDetail pd) {
		logger.debug(Literal.ENTERING);

		PresentmentResponse presentmentResponse = new PresentmentResponse();
		try {
			PresentmentDetail apd = presentmentDetailDAO.getPresentmentById(pd.getId());
			if (apd == null) {
				String[] valueParm = new String[4];
				valueParm[0] = "Presentment";
				valueParm[1] = "Details";
				valueParm[2] = "not Found/Approved for presentmentId";
				valueParm[3] = "and finreference";
				presentmentResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("30550", valueParm));
				return presentmentResponse;
			}

			Presentment presentment = presentmentDetailDAO.getPresentmentByBatchId(apd.getPresentmentRef(), "");
			if (presentment == null) {
				String[] valueParm = new String[4];
				valueParm[0] = "Presentment";
				valueParm[1] = "Details";
				valueParm[2] = "not approved for id";
				valueParm[3] = String.valueOf(pd.getId());
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

		PresentmentDetailExtract pde = new PresentmentDetailExtract(dataSource);
		pde.setUserDetails(new LoggedInUser());
		presentmentDetailService.setProperties(pde);

		long headerId = presentmentDetailDAO.logHeader(presentment.getBatchId(), null, "IMPORT", 0);

		logger.info("Import header-ID: {}", headerId);

		try {

			PresentmentDetail pd = presentmentDetailDAO.getPresentmentByRef(presentment.getBatchId());

			if (pd == null) {
				String[] valueParm = new String[4];
				valueParm[0] = "Presentment Details not available";
				valueParm[1] = "";
				valueParm[2] = "for batch id";
				valueParm[3] = presentment.getBatchId();
				return APIErrorHandlerService.getFailedStatus("30550", valueParm);
			}

			if (RepayConstants.PEXC_SUCCESS.equals(pd.getStatus())
					|| RepayConstants.PEXC_BOUNCE.equals(pd.getStatus())) {
				String[] valueParm = new String[4];
				valueParm[0] = "The Presentment with";
				valueParm[1] = "the presentment reference";
				valueParm[2] = presentment.getBatchId();
				valueParm[3] = "already processed";
				return APIErrorHandlerService.getFailedStatus("30550", valueParm);
			}

			if (!pd.getFinReference().equals(presentment.getAgreementNo())) {
				String[] valueParm = new String[4];
				valueParm[0] = "FinReference";
				valueParm[1] = "is Invalid";
				valueParm[2] = ":";
				valueParm[3] = presentment.getAgreementNo();
				return APIErrorHandlerService.getFailedStatus("30550", valueParm);
			}

			presentmentDetailDAO.logRequest(headerId, presentment);

			pde.processingPrsentments(headerId, presentment.getBatchId());

		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		} finally {
			presentmentDetailDAO.deleteByHeaderId(headerId);
		}

		logger.debug(Literal.LEAVING);
		return APIErrorHandlerService.getSuccessStatus();
	}

	public PresentmentDetailService getPresentmentDetailService() {
		return presentmentDetailService;
	}

	public void setPresentmentDetailService(PresentmentDetailService presentmentDetailService) {
		this.presentmentDetailService = presentmentDetailService;
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
