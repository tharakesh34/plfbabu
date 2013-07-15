package com.pennant.webui.finance.financemain;

import java.math.BigDecimal;

import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.util.Constraint.IntValidator;
import com.pennant.util.Constraint.RangeAmountValidator;

public class FinanceMainValidation {

	protected static  Window  window; // autoWired
	private static FinanceType financeType;
	
	/**
	 * Method for Check validation of calling 
	 * @param win
	 * @param financeDetail
	 */
	public static void doCheckValidation(Window financeWindow, FinScheduleData financeDetail) {
		window = financeWindow;
		setFinanceType(financeDetail.getFinanceType());
		doSetValidation();
		doSetLOVValidation();
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private static void doSetValidation() {
		BigDecimal financeAmount = new BigDecimal(0);
		if (!((Decimalbox)window.getFellowIfAny("finAmount")).isReadonly()){
			Decimalbox finAmount =  ((Decimalbox)window.getFellowIfAny("finAmount"));
			financeAmount = new BigDecimal(finAmount.getValue()== null ? "0" : finAmount.getValue().toString());
			if(getFinanceType().getFinMinAmount().compareTo(new BigDecimal(0))>0 &&
					getFinanceType().getFinMaxAmount().compareTo(new BigDecimal(0))>0){
				finAmount.setConstraint(new RangeAmountValidator(getFinanceType().getFinMinAmount(), getFinanceType().getFinMaxAmount(), 
						Labels.getLabel("label_FinanceMainQDEDialog_FinAmount.value")));
			}
		}
		if(financeAmount.compareTo(new BigDecimal(0))>0){
			if (!((Decimalbox)window.getFellowIfAny("downPayment")).isReadonly() && !((Decimalbox)window.getFellowIfAny("downPayment")).isDisabled()){
				Decimalbox downPayment =  ((Decimalbox)window.getFellowIfAny("downPayment"));
				if(getFinanceType().getFinMinDownPayAmount().doubleValue() >0){
					downPayment.setConstraint(new RangeAmountValidator(financeAmount.equals(new BigDecimal(0)) ? new BigDecimal(0):
						financeAmount.divide(getFinanceType().getFinMinDownPayAmount()),
						financeAmount, Labels.getLabel("label_FinanceMainQDEDialog_DownPayment.value")));
				}else{
					downPayment.setConstraint(new AmountValidator(downPayment.getMaxlength(),
							0, Labels.getLabel("label_FinanceMainQDEDialog_DownPayment.value"),false));
				}
			}
		}
		if (!((Datebox)window.getFellowIfAny("finStartDate")).isDisabled()){
			Datebox finStartDate =  ((Datebox)window.getFellowIfAny("finStartDate"));
			finStartDate.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY"
					,new String[]{Labels.getLabel("label_FinanceMainQDEDialog_FinStartDate.value")}));
		}
		if (!((Intbox)window.getFellowIfAny("numberOfTerms")).isReadonly()){
			Intbox numberOfTerms = ((Intbox)window.getFellowIfAny("numberOfTerms"));
			if(getFinanceType().getFinMinTerm() != 0 && getFinanceType().getFinMaxTerm() != 0){
				numberOfTerms.setConstraint(new RangeAmountValidator(new BigDecimal(getFinanceType().getFinMinTerm()),
						new BigDecimal(getFinanceType().getFinMaxTerm()), Labels.getLabel("label_FinanceMainQDEDialog_NumberOfTerms.value")));
			}else{
				numberOfTerms.setConstraint(new IntValidator(numberOfTerms.getMaxlength(), Labels.getLabel("label_FinanceMainQDEDialog_NumberOfTerms.value"), false));
			}
		}
		if(!((Textbox)window.getFellowIfAny("finReference")).isReadonly() && !getFinanceType().isFinIsGenRef()) {
			Textbox finReference = ((Textbox)window.getFellowIfAny("finReference"));
			finReference.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_FinanceMainQDEDialog_FinReference.value") }));
		}
	}
	
	/**
	 * Sets the Validation by setting the accordingly constraints to the LOVFields.
	 */
	private static void doSetLOVValidation() {
		
		if (!((Longbox)window.getFellowIfAny("custID")).isReadonly()){
			Textbox lovDescCustCIF = (Textbox)window.getFellowIfAny("lovDescCustCIF");
		lovDescCustCIF.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY"
				,new String[]{Labels.getLabel("label_FinanceMainQDEDialog_CustCIF.value")}));
		}
		if (!((Textbox)window.getFellowIfAny("finCcy")).isReadonly()){
			Textbox lovDescFinCcyName = (Textbox)window.getFellowIfAny("lovDescFinCcyName");
			lovDescFinCcyName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY"
					,new String[]{Labels.getLabel("label_FinanceMainQDEDialog_FinCcy.value")}));
		}
	}
	
	public static void setFinanceType(FinanceType financeType) {
		FinanceMainValidation.financeType = financeType;
	}
	public static FinanceType getFinanceType() {
		return financeType;
	}

}
