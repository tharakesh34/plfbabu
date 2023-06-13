package com.pennanttech.external.extractions.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennanttech.external.EODExtractionsHook;
import com.pennanttech.external.app.constants.ExtIntfConfigConstants;
import com.pennanttech.pennapps.core.resource.Literal;

public class EODExtractionsService implements EODExtractionsHook, ExtIntfConfigConstants {

	private static final Logger logger = LogManager.getLogger(EODExtractionsService.class);

	private UCICExtractionService ucicExtractionService;

	private FinconExtractionService finconExtractionService;

	private ALMExtractionService almExtractionService;

	private BaselOneExtractionService baselOneExtractionService;

	private BaselTwoExtractionService baselTwoExtractionService;

	private RPMSExtractionService rpmsExtractionService;

	private RBIADFExtractionService rbiAdfExtractionService;

	@Override
	public void processUCICExtraction() {
		logger.debug(Literal.ENTERING);
		try {
			ucicExtractionService.processExtraction();
		} catch (Exception e) {
			logger.debug(Literal.LEAVING, e);
		}
	}

	@Override
	public void processFinconGLExtraction() {
		logger.debug(Literal.ENTERING);
		try {
			finconExtractionService.processExtraction();
		} catch (Exception e) {
			logger.debug(Literal.LEAVING, e);
		}
	}

	@Override
	public void processALMReportExtraction() {
		logger.debug(Literal.ENTERING);
		try {
			almExtractionService.processExtraction();
		} catch (Exception e) {
			logger.debug(Literal.LEAVING, e);
		}
	}

	@Override
	public void processBaselOneExtraction() {
		logger.debug(Literal.ENTERING);
		try {
			baselOneExtractionService.processExtraction();
		} catch (Exception e) {
			logger.debug(Literal.LEAVING, e);
		}
	}

	@Override
	public void processBaselTwoExtraction() {
		logger.debug(Literal.ENTERING);
		try {
			baselTwoExtractionService.processExtraction();
		} catch (Exception e) {
			logger.debug(Literal.LEAVING, e);
		}
	}

	@Override
	public void processRPMSExtraction() {
		logger.debug(Literal.ENTERING);
		try {
			rpmsExtractionService.processExtraction();
		} catch (Exception e) {
			logger.debug(Literal.LEAVING, e);
		}
	}

	@Override
	public void processExtRBIADFExtraction() {
		logger.debug(Literal.ENTERING);
		try {
			rbiAdfExtractionService.processExtraction();
		} catch (Exception e) {
			logger.debug(Literal.LEAVING, e);
		}
	}

	public void setUcicExtractionService(UCICExtractionService ucicExtractionService) {
		this.ucicExtractionService = ucicExtractionService;
	}

	public void setFinconExtractionService(FinconExtractionService finconExtractionService) {
		this.finconExtractionService = finconExtractionService;
	}

	public void setAlmExtractionService(ALMExtractionService almExtractionService) {
		this.almExtractionService = almExtractionService;
	}

	public void setBaselOneExtractionService(BaselOneExtractionService baselOneExtractionService) {
		this.baselOneExtractionService = baselOneExtractionService;
	}

	public void setBaselTwoExtractionService(BaselTwoExtractionService baselTwoExtractionService) {
		this.baselTwoExtractionService = baselTwoExtractionService;
	}

	public void setRpmsExtractionService(RPMSExtractionService rpmsExtractionService) {
		this.rpmsExtractionService = rpmsExtractionService;
	}

	public void setRbiAdfExtractionService(RBIADFExtractionService rbiAdfExtractionService) {
		this.rbiAdfExtractionService = rbiAdfExtractionService;
	}

}