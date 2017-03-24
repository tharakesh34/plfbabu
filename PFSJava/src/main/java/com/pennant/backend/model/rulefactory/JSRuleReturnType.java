package com.pennant.backend.model.rulefactory;

import java.util.List;

import com.pennant.backend.model.ValueLabel;

public class JSRuleReturnType {
	private String 	componentType;
	private String resultLabel = "";
	private List<ValueLabel> listOfData = null;
	private String	moduleName;
	private String 	valueColumn;
	private String[] validateColumns;
	private boolean multiSelection = false;
	
	public String getComponentType() {
		return componentType;
	}
	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}
	
	public String getResultLabel() {
		return resultLabel;
	}
	public void setResultLabel(String resultLabel) {
		this.resultLabel = resultLabel;
	}
	
	public List<ValueLabel> getListOfData() {
		return listOfData;
	}
	public void setListOfData(List<ValueLabel> listOfData) {
		this.listOfData = listOfData;
	}
	
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public String getValueColumn() {
		return valueColumn;
	}
	public void setValueColumn(String valueColumn) {
		this.valueColumn = valueColumn;
	}
	
	public String[] getValidateColumns() {
		return validateColumns;
	}
	public void setValidateColumns(String[] validateColumns) {
		this.validateColumns = validateColumns;
	}
	public boolean isMultiSelection() {
		return multiSelection;
	}
	public void setMultiSelection(boolean multiSelection) {
		this.multiSelection = multiSelection;
	}
}
