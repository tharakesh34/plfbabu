package com.pennant.backend.service.customermasters.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.dao.customermasters.CustomerIncomeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pennapps.pff.sampling.dao.SamplingDAO;

public class CustomerIncomeValidation {

	private CustomerIncomeDAO customerIncomeDAO;
	private CustomerDetails customerDetails;
	protected SamplingDAO samplingDAO;

	public CustomerIncomeValidation(CustomerIncomeDAO customerIncomeDAO) {
		this.customerIncomeDAO = customerIncomeDAO;
		samplingDAO = (SamplingDAO) SpringBeanUtil.getBean("samplingDAO");
	}

	/**
	 * @return the customerIncomeDAO
	 */
	public CustomerIncomeDAO getCustomerIncomeDAO() {
		return customerIncomeDAO;
	}

	public AuditHeader incomeValidation(AuditHeader auditHeader, String method){
		customerDetails = new CustomerDetails();
		customerDetails.setCustomer(new Customer());
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), 0, method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> incomeListValidation(CustomerDetails customerDetails, String method,String  usrLanguage){
		this.customerDetails = customerDetails;
		List<AuditDetail> auditDetails = customerDetails.getAuditDetailMap().get("Income");
		if(auditDetails!=null && !auditDetails.isEmpty()){
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail =   validate(auditDetails.get(i), 0,  method, usrLanguage);
				details.add(auditDetail); 		
				if(!(auditDetail.getErrorDetails() != null && auditDetail.getErrorDetails().isEmpty())){
					break;
				}
			}
			return details;
		}
		return new ArrayList<AuditDetail>();
	}

	
	public List<AuditDetail> incomeListValidation(List<AuditDetail> auditDetails, long samplingId, String method, String usrLanguage) {
		if (auditDetails != null && auditDetails.size() > 0) {
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail = validate(auditDetails.get(i), samplingId, method, usrLanguage);
				details.add(auditDetail);
			}
			return details;
		}
		return new ArrayList<AuditDetail>();
	}

	private AuditDetail validate(AuditDetail auditDetail, long samplingId, String method,String  usrLanguage){
		CustomerIncome customerIncome= (CustomerIncome) auditDetail.getModelData();
		if ("sampling".equals(customerIncome.getInputSource())) {
			customerIncome.setLinkId(samplingDAO.getIncomeLinkIdByCustId(customerIncome.getCustId(),samplingId));
		} else {
			customerIncome.setLinkId(customerIncomeDAO.getLinkId(customerIncome.getCustId()));
		}
		
		CustomerIncome tempCustomerIncome= null;
		/*if (customerIncome.isWorkflow()){
			tempCustomerIncome = getCustomerIncomeDAO().getCustomerIncomeById(customerIncome,"_Temp" ,customerIncome.getInputSource());
		}*/

		CustomerIncome befCustomerIncome= getCustomerIncomeDAO().getCustomerIncomeById(customerIncome,"",customerIncome.getInputSource());
		

/*		String[] valueParm = new String[3];
		String[] errParm = new String[3];

		valueParm[0] = StringUtils.trimToEmpty(customerIncome.getCustCif());
		valueParm[1] = customerIncome.getIncomeType();
		valueParm[2] = String.valueOf(customerIncome.isJointCust());

		errParm[0] = PennantJavaUtil.getLabel("IncomeDetails") +" , " + PennantJavaUtil.getLabel("label_CustCIF") + ":" + valueParm[0]+ " and ";
		errParm[1] = PennantJavaUtil.getLabel("label_CustIncomeType") + ":"+valueParm[1];

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
*/
		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !customerIncome.isWorkflow()){
			customerIncome.setBefImage(befCustomerIncome);	
		}
		return auditDetail;
	}

}
