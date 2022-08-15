package com.pennant.backend.service.customermasters.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.customermasters.GSTDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.GSTDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class GSTDetailValidation {
	private static final Logger logger = LogManager.getLogger(GSTDetailValidation.class);

	private GSTDetailDAO gstDetailDAO;

	public GSTDetailValidation(GSTDetailDAO gstDetailDAO) {
		this.gstDetailDAO = gstDetailDAO;
	}

	public AuditHeader gstDetailValidation(AuditHeader auditHeader, String method) {
		AuditDetail auditDetail = validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		return auditHeader;
	}

	public List<AuditDetail> gstDetailListValidation(List<AuditDetail> adList, String method, String usrLan) {
		if (CollectionUtils.isEmpty(adList)) {
			return new ArrayList<>();
		}

		List<AuditDetail> details = new ArrayList<>();

		adList.forEach(ad -> details.add(validate(ad, method, usrLan)));

		return details;
	}

	private AuditDetail validate(AuditDetail auditDetail, String method, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		GSTDetail gstDetail = (GSTDetail) auditDetail.getModelData();

		long code = gstDetail.getCustID();
		String category = gstDetail.getStateCode();

		if (gstDetail.isNewRecord() && PennantConstants.RECORD_TYPE_NEW.equals(gstDetail.getRecordType())
				&& gstDetailDAO.isDuplicateKey(gstDetail,
						gstDetail.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];
			parameters[0] = PennantJavaUtil.getLabel("label_CustCIF") + ": " + code;
			parameters[1] = PennantJavaUtil.getLabel("label_GstState") + ": " + category;

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}
}
