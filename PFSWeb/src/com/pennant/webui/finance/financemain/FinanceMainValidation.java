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
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
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
		BigDecimal financeAmount = BigDecimal.ZERO;
		if (!((Decimalbox)window.getFellowIfAny("finAmount")).isReadonly()){
			Decimalbox finAmount =  ((Decimalbox)window.getFellowIfAny("finAmount"));
			financeAmount = new BigDecimal(finAmount.getValue()== null ? "0" : finAmount.getValue().toString());
			if(getFinanceType().getFinMinAmount().compareTo(BigDecimal.ZERO)>0 &&
					getFinanceType().getFinMaxAmount().compareTo(BigDecimal.ZERO)>0){
				finAmount.setConstraint(new RangeAmountValidator(getFinanceType().getFinMinAmount(), getFinanceType().getFinMaxAmount(), 
						Labels.getLabel("label_FinanceMainQDEDialog_FinAmount.value")));
			}
		}
		if(financeAmount.compareTo(BigDecimal.ZERO)>0){
			if (!((Decimalbox)window.getFellowIfAny("downPayment")).isReadonly() && !((Decimalbox)window.getFellowIfAny("downPayment")).isDisabled()){
				Decimalbox downPayment =  ((Decimalbox)window.getFellowIfAny("downPayment"));
				if(getFinanceType().getFinMinDownPayAmount().doubleValue() >0){
					downPayment.setConstraint(new RangeAmountValidator(financeAmount.equals(BigDecimal.ZERO) ? BigDecimal.ZERO:
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
			finStartDate.setConstraint(new PTDateValidator(Labels.getLabel("label_FinanceMainQDEDialog_FinStartDate.value"),true));
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
			finReference.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceMainQDEDialog_FinReference.value"),null,true));
		}
	}
	
	/**
	 * Sets the Validation by setting the accordingly constraints to the LOVFields.
	 */
	private static void doSetLOVValidation() {
		
		if (!((Longbox)window.getFellowIfAny("custID")).isReadonly()){
			Textbox lovDescCustCIF = (Textbox)window.getFellowIfAny("lovDescCustCIF");
		lovDescCustCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceMainQDEDialog_CustCIF.value"),null,true));
		}
		if (!((Textbox)window.getFellowIfAny("finCcy")).isReadonly()){
			Textbox lovDescFinCcyName = (Textbox)window.getFellowIfAny("lovDescFinCcyName");
			lovDescFinCcyName.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceMainQDEDialog_FinCcy.value"),null,true));
		}
	}
	
	public static void setFinanceType(FinanceType financeType) {
		FinanceMainValidation.financeType = financeType;
	}
	public static FinanceType getFinanceType() {
		return financeType;
	}

}
