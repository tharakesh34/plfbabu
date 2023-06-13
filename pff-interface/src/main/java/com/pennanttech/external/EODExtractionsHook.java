package com.pennanttech.external;

public interface EODExtractionsHook {

	void processUCICExtraction();

	void processFinconGLExtraction();

	void processALMReportExtraction();

	void processBaselOneExtraction();

	void processBaselTwoExtraction();

	void processRPMSExtraction();

	void processExtRBIADFExtraction();

}