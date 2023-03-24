package com.pennanttech.external.presentment.handler;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.SysParamUtil;
import com.pennanttech.external.ExternalPresentmentHook;
import com.pennanttech.external.config.ExternalConfig;
import com.pennanttech.external.constants.InterfaceConstants;
import com.pennanttech.external.dao.ExtInterfaceDao;
import com.pennanttech.external.presentment.dao.ExtPresentmentDAO;
import com.pennanttech.external.presentment.model.ExtPresentmentFile;
import com.pennanttech.external.presentment.service.ACHService;
import com.pennanttech.external.presentment.service.ExtPDCService;
import com.pennanttech.external.presentment.service.SIInternalService;
import com.pennanttech.external.presentment.service.SIService;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

public class PresentmentRequestHandler implements ExternalPresentmentHook, InterfaceConstants {
	private static final Logger logger = LogManager.getLogger(PresentmentRequestHandler.class);

	private ExtPresentmentDAO externalPresentmentDAO;
	private ExtInterfaceDao extInterfaceDao;

	private SIService siService;
	private SIInternalService siInternalService;
	private ACHService achService;
	private ExtPDCService extPdcService;

	public PresentmentRequestHandler() {
		super();
	}

	/**
	 * @param presentmentHeader
	 */
	@Override
	public void processPresentmentRequest(PresentmentHeader presentmentHeader) {
		logger.debug(Literal.ENTERING);

		// Fetch External configuration once for all the interfaces types
		List<ExternalConfig> list = extInterfaceDao.getExternalConfig();
		Date appDate = SysParamUtil.getAppDate();

		String configTYpe = "";

		if (PLF_NACH.equals(presentmentHeader.getMandateType()) || PLF_ECS.equals(presentmentHeader.getMandateType())
				|| PLF_E_NACH.equals(presentmentHeader.getMandateType())
				|| PLF_E_MANDATE.equals(presentmentHeader.getMandateType())) {
			configTYpe = CONFIG_NACH_REQ;
		}

		if (PLF_SI.equals(presentmentHeader.getMandateType())) {
			configTYpe = CONFIG_SI_REQ;
		}

		if (PLF_IPDC.equals(presentmentHeader.getMandateType())) {
			configTYpe = CONFIG_IPDC_REQ;
		}

		if (PLF_PDC.equals(presentmentHeader.getMandateType())) {
			configTYpe = CONFIG_PDC_REQ;
		}

		ExternalConfig externalConfig = getDataFromList(list, configTYpe);

		if (externalConfig == null) {
			return;
		}

		// Below condition is for SI Request File generation
		if (CONFIG_SI_REQ.equals(configTYpe)) {
			processSI(externalConfig, presentmentHeader, appDate);
		}

		// Below condition is for SI Internal Request File generation
		if (CONFIG_IPDC_REQ.equals(configTYpe)) {
			processSIInternal(externalConfig, presentmentHeader, appDate);
		}

		// Below condition is for ACH Internal Request File generation
		if (CONFIG_NACH_REQ.equals(configTYpe)) {
			processNACH(externalConfig, presentmentHeader, appDate);
		}

		// Below condition is for External PDC Internal Request File generation
		if (CONFIG_PDC_REQ.equals(configTYpe)) {
			processExtPDC(externalConfig, presentmentHeader, appDate);
		}

		logger.debug(Literal.LEAVING);
	}

	private void processExtPDC(ExternalConfig externalConfig, PresentmentHeader presentmentHeader, Date appDate) {
		logger.debug(Literal.ENTERING);
		List<ExtPresentmentFile> presentmentList = externalPresentmentDAO
				.getExternalPDCPresentmentDetails(presentmentHeader);
		if (presentmentList.isEmpty()) {
			return;
		}
		extPdcService.processExtPDCPresentments(externalConfig, presentmentList, appDate);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * @param externalConfig
	 * @param presentmentHeader
	 */
	private void processNACH(ExternalConfig externalConfig, PresentmentHeader presentmentHeader, Date appDate) {
		logger.debug(Literal.ENTERING);

		List<ExtPresentmentFile> presentmentList = externalPresentmentDAO.getACHPresentmentDetails(presentmentHeader);

		if (presentmentList.isEmpty()) {
			return;
		}

		Date schDate = presentmentList.get(0).getSchDate();
		String batchRef = String.valueOf(presentmentHeader.getId());// presentmentList.get(0).getPresentmentRef();
		achService.processACHRequest(externalConfig, presentmentList, schDate, batchRef, appDate);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * @param externalConfig
	 * @param presentmentHeader
	 */
	private void processSI(ExternalConfig externalConfig, PresentmentHeader presentmentHeader, Date appDate) {
		logger.debug(Literal.ENTERING);

		List<ExtPresentmentFile> presentmentList = externalPresentmentDAO.getSIPresentmentDetails(presentmentHeader);

		if (presentmentList.isEmpty()) {
			return;
		}

		siService.processSIRequest(externalConfig, presentmentList, appDate);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * @param externalConfig
	 * @param presentmentHeader
	 */
	private void processSIInternal(ExternalConfig externalConfig, PresentmentHeader presentmentHeader, Date appDate) {
		logger.debug(Literal.ENTERING);

		List<ExtPresentmentFile> presentmentList = externalPresentmentDAO
				.getSiInternalPresentmentDetails(presentmentHeader);

		if (presentmentList.isEmpty()) {
			logger.debug("No IPDC record found with cheque");
			return;
		}

		siInternalService.processSiInternalPresentments(externalConfig, presentmentList, appDate);

		logger.debug(Literal.LEAVING);
	}

	public void setExternalPresentmentDAO(ExtPresentmentDAO externalPresentmentDAO) {
		this.externalPresentmentDAO = externalPresentmentDAO;
	}

	public void setSiService(SIService siService) {
		this.siService = siService;
	}

	public void setSiInternalService(SIInternalService siInternalService) {
		this.siInternalService = siInternalService;
	}

	public void setAchService(ACHService achService) {
		this.achService = achService;
	}

	public void setExtPdcService(ExtPDCService extPdcService) {
		this.extPdcService = extPdcService;
	}

	public void setExtInterfaceDao(ExtInterfaceDao extInterfaceDao) {
		this.extInterfaceDao = extInterfaceDao;
	}

}
