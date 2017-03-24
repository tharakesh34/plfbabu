package com.pennant.webui.rulefactory.rule;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

public class BuilderUtilListbox {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ListModel getLogicModelLabel() {//Using

		List data = new ArrayList();		
		data.add("--select--"); 		
		data.add("AND"); 		
		data.add("OR"); 

		return new  ListModelList(data);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ListModel getLogicModelValue() {//Using
		
		List data = new ArrayList();		
		data.add("#"); 		
		data.add(" && "); 		
		data.add(" || "); 
		
		return new  ListModelList(data);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ListModel getBooleanOperators() {//Using

		List data = new ArrayList();		
		data.add("true"); 		
		data.add("false"); 

		return new  ListModelList(data);
	}

}
