package com.pennant.pennapps.pff.niyogin;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantStaticListUtil;

@Component()
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CustomAppList {

	@Autowired
	private PennantStaticListUtil appList;

	@PostConstruct
	public void overrideLists() {
		// Remove not required  schedule calculation codes
		appList.removeScheduleCalculationCode(CalculationConstants.RPYCHG_CURPRD);
		appList.removeScheduleCalculationCode(CalculationConstants.RPYCHG_ADJMDT);
		appList.removeScheduleCalculationCode(CalculationConstants.RPYCHG_TILLDATE);
		appList.removeScheduleCalculationCode(CalculationConstants.RPYCHG_ADDTERM);
		appList.removeScheduleCalculationCode(CalculationConstants.RPYCHG_ADDRECAL);
		appList.removeScheduleCalculationCode(CalculationConstants.RPYCHG_STEPPOS);
		appList.removeScheduleCalculationCode(CalculationConstants.RPYCHG_ADJTERMS);
		
		// Remove BIP Methods
		appList.removeBpiMethods(FinanceConstants.BPI_CAPITALIZE);
		
	}

}
