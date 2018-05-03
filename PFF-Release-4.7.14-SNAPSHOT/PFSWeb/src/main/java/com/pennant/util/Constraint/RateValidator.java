package com.pennant.util.Constraint;

import java.math.BigDecimal;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;

public class RateValidator implements Constraint{

	private int len;
	private int decPos;
	private String fieldParm="";
	private boolean zeroAllowed=true;
	
	public RateValidator(int len,int decPos) {
		this.setLen(len);
		this.setDecPos(decPos);
	}

	public RateValidator(int len,int decPos,String fieldParm) {
		this.setLen(len);
		this.setDecPos(decPos);
		this.setFieldParm(fieldParm);
	}

	public RateValidator(int len,int decPos,String fieldParm,boolean zeroAllowed) {
		this.setLen(len);
		this.setDecPos(decPos);
		this.setFieldParm(fieldParm);
		this.setZeroAllowed(zeroAllowed);
	}

	public void validate(Component comp, Object value) throws WrongValueException {
		
		double rateValue = 0;
		double maxRate = Math.pow(10, len-decPos);
		
		if (value!=null){
			//rateValue= (Double) value;
			rateValue= new BigDecimal(value.toString()).doubleValue();
			
		}else{
			throw new WrongValueException(comp, Labels.getLabel("RATE_NO_ZERO",new String[]{fieldParm}));
		}
		
		if (rateValue<0){
			throw new WrongValueException(comp, Labels.getLabel("RATE_NO_LESS_ZERO",new String[]{fieldParm}));
		}

		if (!this.isZeroAllowed()){
			if(rateValue==0){
				throw new WrongValueException(comp, Labels.getLabel("RATE_NO_ZERO",new String[]{fieldParm}));
			}
		}

		if (rateValue>=maxRate){
			throw new WrongValueException(comp, Labels.getLabel("RATE_RANGE",new String[] {fieldParm,String.valueOf(maxRate)}));
		}
		
	}

	public void setLen(int len) {
		this.len = len;
	}

	public int getLen() {
		return len;
	}

	public void setDecPos(int decPos) {
		this.decPos = decPos;
	}

	public int getDecPos() {
		return decPos;
	}
	public String getFieldParm() {
		return fieldParm;
	}

	public void setFieldParm(String fieldParm) {
		this.fieldParm = fieldParm;
	}

	public boolean isZeroAllowed() {
		return zeroAllowed;
	}

	public void setZeroAllowed(boolean zeroAllowed) {
		this.zeroAllowed = zeroAllowed;
	}

	
	
}
