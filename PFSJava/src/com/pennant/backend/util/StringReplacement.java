package com.pennant.backend.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.List;

import com.pennant.backend.model.GlobalVariable;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class StringReplacement {

	String templateStr = "";

	public static String getReplacedQuery(String templateStr,List globalList,List subQueryList) {

		StringWriter result = new StringWriter();

		try {

			Configuration cfg = new Configuration();
			// Create a data-model
			LinkedHashMap root = new LinkedHashMap();
			
			if(globalList!=null && globalList.size()>0){
				for(int i=0; i<globalList.size();i++){
					GlobalVariable globalVariable = (GlobalVariable)globalList.get(i);
					String str = (globalVariable.getVarName()).substring(2,  (globalVariable.getVarName()).length()-1);
					root.put(str, "("+globalVariable.getVarValue()+")");
				}
			}
			// Prepare string template
			Template t1 = new Template("SQLReplacement", new StringReader(templateStr), cfg);

			// Process the output to StringWriter and convert that to String
			t1.process(root, result);

			// Load Data
			// Prepare string template
			while(result.getBuffer().toString().contains("${")){
				Template t = new Template("SQLReplacement", new StringReader(result.getBuffer().toString()), cfg);
				result = new StringWriter();
				// Process the output to StringWriter and convert that to String
				t.process(root, result);
				System.out.println(result.getBuffer());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.getBuffer().toString();
	}
	public static String getReplacedQueryToInsert(String templateStr,List globalList,List subQueryList) {

		StringWriter result = new StringWriter();
		try {

			Configuration cfg = new Configuration();
			// Create a data-model
			LinkedHashMap root = new LinkedHashMap();

			for(int i=0; i<globalList.size();i++){
				GlobalVariable globalVariable = (GlobalVariable)globalList.get(i);
				String str = (globalVariable.getVarName()).substring(2,  (globalVariable.getVarName()).length()-1);
				root.put(str, "("+globalVariable.getVarValue()+")");
			}
			// Prepare string template
			Template t1 = new Template("SQLReplacement", new StringReader(templateStr), cfg);

			// Process the output to StringWriter and convert that to String
			t1.process(root, result);

			// Load Data
			// Prepare string template
			while(result.getBuffer().toString().contains("${")){
				Template t = new Template("SQLReplacement", new StringReader(result.getBuffer().toString()), cfg);
				result = new StringWriter();
				// Process the output to StringWriter and convert that to String
				t.process(root, result);
				System.out.println(result.getBuffer());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.getBuffer().toString();
	}
}