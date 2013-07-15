package com.pennant.util.Constraint;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;

public class LongValidator implements Constraint{

	private int len;
	private String fieldParm="";

	
	public LongValidator(int len) {
		this.setLen(len);
	}
	
	public LongValidator(int len,String fieldParm) {
		this.setLen(len);
		this.setFieldParm(fieldParm);
	}

	public void validate(Component comp, Object value) throws WrongValueException {
		
		boolean validate=true;
		long rateValue = 0;
		double maxValue = Math.pow(10, (len));
		
		if (value!=null){
			//rateValue= (Double) value;
			rateValue= Long.parseLong(value.toString());
			
		}
		
		if (rateValue==0){
			throw new WrongValueException(comp, Labels.getLabel("FIELD_NO_ZERO",new String[]{fieldParm}));
		}
		
		if (rateValue<=(maxValue*-1) || rateValue>=maxValue){
			throw new WrongValueException(comp, Labels.getLabel("FIELD_RANGE",new String[] {fieldParm,String.valueOf(maxValue*-1),String.valueOf(maxValue)}));
		}

		if (!validate){
			throw new WrongValueException(comp, Labels.getLabel("FIELD_NO_NUMBER",new String[]{fieldParm}));
		}		
	}

	public void setLen(int len) {
		this.len = len;
	}

	public int getLen() {
		return len;
	}
	public String getFieldParm() {
		return fieldParm;
	}

	public void setFieldParm(String fieldParm) {
		this.fieldParm = fieldParm;
	}
	
}
