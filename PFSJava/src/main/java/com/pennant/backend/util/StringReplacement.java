package com.pennant.backend.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.model.applicationmaster.Query;
import com.pennanttech.pennapps.core.model.GlobalVariable;
import com.pennanttech.pennapps.core.resource.Literal;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class StringReplacement {
	String templateStr = "";
	private static Logger logger = LogManager.getLogger(StringReplacement.class);

	public static String getReplacedQuery(String templateStr, List<GlobalVariable> globalList,
			List<Query> subQueryList) {
		logger.debug("Entering");
		StringWriter result = new StringWriter();

		try {
			Configuration cfg = new Configuration();
			// Create a data-model
			Map<String, String> root = new LinkedHashMap<String, String>();

			if (globalList != null && globalList.size() > 0) {
				for (int i = 0; i < globalList.size(); i++) {
					GlobalVariable globalVariable = (GlobalVariable) globalList.get(i);
					String str = (globalVariable.getName()).substring(2, (globalVariable.getName()).length() - 1);
					root.put(str, "(" + globalVariable.getValue() + ")");
				}
			}
			// Prepare string template
			Template t1 = new Template("SQLReplacement", new StringReader(templateStr), cfg);

			// Process the output to StringWriter and convert that to String
			t1.process(root, result);

			// Load Data
			// Prepare string template
			while (result.getBuffer().toString().contains("${")) {
				Template t = new Template("SQLReplacement", new StringReader(result.getBuffer().toString()), cfg);
				result = new StringWriter();
				// Process the output to StringWriter and convert that to String
				t.process(root, result);
			}
		} catch (TemplateException | IOException e) {
			logger.error("Exception: ", e);
		}

		logger.debug("Leaving");
		return result.getBuffer().toString();
	}

	public static String getReplacedQueryToInsert(String templateStr, List<GlobalVariable> globalList,
			List<Query> subQueryList) {
		logger.debug("Entering");
		StringWriter result = new StringWriter();
		try {

			Configuration cfg = new Configuration();
			// Create a data-model
			Map<String, String> root = new LinkedHashMap<String, String>();

			for (int i = 0; i < globalList.size(); i++) {
				GlobalVariable globalVariable = (GlobalVariable) globalList.get(i);
				String str = (globalVariable.getName()).substring(2, (globalVariable.getName()).length() - 1);
				root.put(str, "(" + globalVariable.getValue() + ")");
			}
			// Prepare string template
			Template t1 = new Template("SQLReplacement", new StringReader(templateStr), cfg);

			// Process the output to StringWriter and convert that to String
			t1.process(root, result);

			// Load Data
			// Prepare string template
			while (result.getBuffer().toString().contains("${")) {
				Template t = new Template("SQLReplacement", new StringReader(result.getBuffer().toString()), cfg);
				result = new StringWriter();
				// Process the output to StringWriter and convert that to String
				t.process(root, result);
			}
		} catch (TemplateException | IOException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug("Leaving");
		return result.getBuffer().toString();
	}
}