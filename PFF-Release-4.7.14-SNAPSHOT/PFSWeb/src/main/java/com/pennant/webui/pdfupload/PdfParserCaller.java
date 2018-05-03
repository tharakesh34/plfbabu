 package com.pennant.webui.pdfupload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennanttech.document.DocumentParser;

public class PdfParserCaller {

	private static final Logger logger = Logger.getLogger(PdfParserCaller.class);

	@Autowired
	private DocumentParser documentParser;

	public Map<String, Object> callDocumentParser(List<CustomerDocument> custDocumentDetails) {
		logger.debug("Entering");
		Map<String, Object> allHashParseResult = new HashMap<>();
		for (CustomerDocument customerDocument : custDocumentDetails) {
			try {
				customerDocument.setYear(null);
				allHashParseResult.putAll(parsePdf(customerDocument));
			} catch (Exception e) {
				logger.error("Exception :", e);
			}
		}
		logger.debug("Leaving");
		return allHashParseResult;
	}

	public Map<String, Object> parsePdf(CustomerDocument customerDocument){
		logger.debug("Entering");
		Map<String, Object> parserResult = new HashMap<>(1);
		// this is a pdf file
		if (!customerDocument.isDocIsPdfExtRequired()) {
			return parserResult;
		}
		
		if (customerDocument.getCustDocImage() == null && customerDocument.getDocRefId() != Long.MIN_VALUE) {
			customerDocument.setCustDocImage(PennantApplicationUtil.getDocumentImage(customerDocument.getDocRefId()));
		}

		if (customerDocument.getCustDocImage() == null) {
			logger.warn("file byte array is empty");
			return parserResult;
		}
		
		parserResult = documentParser.getValueByTypeNYear(customerDocument.getCustDocImage(),
				customerDocument.getCustDocName(), customerDocument.getPdfPassWord(),
				customerDocument.getPdfMappingRef(), customerDocument.getYear());

		logger.debug("Leaving");
		return parserResult;
	}

}

