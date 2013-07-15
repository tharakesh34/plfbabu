package com.pennant.util.Constraint;

import java.math.BigDecimal;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;

public class RangeAmountValidator implements Constraint{

	private BigDecimal minAmount;
	private BigDecimal maxAmount;
	private String fieldParm="Amount";
	
	public RangeAmountValidator(BigDecimal minAmount,BigDecimal maxAmount,String fieldParm) {
		this.setMinAmount(minAmount);
		this.setMaxAmount(maxAmount);
		this.setFieldParm(fieldParm);
	}
	
	@Override
	public void validate(Component comp, Object value)
			throws WrongValueException {

		boolean validate=true;
		double rateValue = 0;
		
		if (value!=null){
			rateValue= new BigDecimal(value.toString()).doubleValue();
		}
		
		if (rateValue==0){
			throw new WrongValueException(comp, Labels.getLabel("FIELD_NO_ZERO",new String[]{fieldParm}));
		}
		
		if (rateValue < (minAmount.doubleValue()) || rateValue > maxAmount.doubleValue()){
			throw new WrongValueException(comp, Labels.getLabel("FIELD_RANGE",
					new String[] {fieldParm,String.valueOf(minAmount),String.valueOf(maxAmount)}));
		}

		if (!validate){
			throw new WrongValueException(comp, Labels.getLabel("FIELD_NO_NUMBER",new String[]{fieldParm}));
		}		
		
	}

	public void setMinAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}
	public BigDecimal getMinAmount() {
		return minAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}
	public BigDecimal getMaxAmount() {
		return maxAmount;
	}
	
	public String getFieldParm() {
		return fieldParm;
	}
	public void setFieldParm(String fieldParm) {
		this.fieldParm = fieldParm;
	}
}

