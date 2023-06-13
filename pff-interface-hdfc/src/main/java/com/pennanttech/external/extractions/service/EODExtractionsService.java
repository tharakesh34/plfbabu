package com.pennanttech.external.extractions.service;

import com.pennanttech.external.EODExtractionsHook;
import com.pennanttech.external.app.constants.ExtIntfConfigConstants;

public class EODExtractionsService implements EODExtractionsHook, ExtIntfConfigConstants {

	private UCICExtractionService ucicExtractionService;

	private FinconExtractionService finconExtractionService;

	private ALMExtractionService almExtractionService;

	private BaselOneExtractionService baselOneExtractionService;

	private BaselTwoExtractionService baselTwoExtractionService;

	private RPMSExtractionService rpmsExtractionService;

	private RBIADFExtractionService rbiAdfExtractionService;

	@Override
	public void processUCICExtraction() {
		ucicExtractionService.processExtraction();
	}

	@Override
	public void processFinconGLExtraction() {
		finconExtractionService.processExtraction();
	}

	@Override
	public void processALMReportExtraction() {
		almExtractionService.processExtraction();
	}

	@Override
	public void processBaselOneExtraction() {
		baselOneExtractionService.processExtraction();
	}

	@Override
	public void processBaselTwoExtraction() {
		baselTwoExtractionService.processExtraction();
	}

	@Override
	public void processRPMSExtraction() {
		rpmsExtractionService.processExtraction();
	}

	@Override
	public void processExtRBIADFExtraction() {
		rbiAdfExtractionService.processExtraction();
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