package com.pennant.pff.template;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.letter.LetterUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

public class TemplateUtil {
	private TemplateUtil() {
		super();
	}

	private static List<ValueLabel> events = null;
	private static List<ValueLabel> modules = null;
	private static List<ValueLabel> formats = null;
	private static List<ValueLabel> templatesFor = null;
	private static List<ValueLabel> agreementType = null;

	public static List<ValueLabel> getEvents() {
		if (events != null) {
			return events;
		}

		events = new ArrayList<>(5);

		events.add(new ValueLabel(FinServiceEvent.ORG, Labels.getLabel("label_FinSerEvent_Origination")));
		events.add(new ValueLabel(FinServiceEvent.RATECHG, Labels.getLabel("label_FinSerEvent_AddRateChange")));
		events.add(new ValueLabel(FinServiceEvent.ADDDISB, Labels.getLabel("label_FinSerEvent_AddDisbursement")));
		events.add(new ValueLabel(FinServiceEvent.RECEIPT, Labels.getLabel("label_FinSerEvent_Receipt")));
		events.add(new ValueLabel(FinServiceEvent.COVENANT, Labels.getLabel("label_FinSerEvent_Covenants")));
		events.add(new ValueLabel(FinServiceEvent.PUTCALL, Labels.getLabel("label_FinSerEvent_PutCall")));
		events.add(new ValueLabel(FinServiceEvent.COLLATERAL_LTV_BREACHS,
				Labels.getLabel("label_FinSerEvent_Collateral_Ltv_Breaches")));
		events.add(new ValueLabel(FinServiceEvent.CANCELFIN, Labels.getLabel("label_VasEvent_Cancellation")));
		events.add(new ValueLabel(FinServiceEvent.DUEALERTS, Labels.getLabel("label_VasEvent_DueAlerts")));
		events.add(new ValueLabel("SecurityUser", Labels.getLabel("label_OTP_SecurityUser")));
		events.addAll(LetterUtil.getLetterTypes());

		return events;
	}

	public static List<ValueLabel> getModules() {
		if (modules != null) {
			return modules;
		}

		modules = new ArrayList<>(7);

		modules.add(new ValueLabel(NotificationConstants.MAIL_MODULE_FIN,
				Labels.getLabel("label_MailTemplateDialog_Finance")));
		modules.add(new ValueLabel(NotificationConstants.MAIL_MODULE_PROVIDER,
				Labels.getLabel("label_MailTemplateDialog_Provider")));
		modules.add(new ValueLabel(NotificationConstants.TEMPLATE_FOR_OTP,
				Labels.getLabel("label_MailTemplateDialog_OTP")));
		modules.add(new ValueLabel(NotificationConstants.TEMPLATE_FOR_UPD,
				Labels.getLabel("label_MailTemplateDialog_PWD")));

		return modules;
	}

	public static List<ValueLabel> getFormats() {
		if (formats != null) {
			return formats;
		}
		formats = new ArrayList<>(2);
		formats.add(new ValueLabel(NotificationConstants.TEMPLATE_FORMAT_PLAIN,
				Labels.getLabel("common.template.format.plain")));
		formats.add(new ValueLabel(NotificationConstants.TEMPLATE_FORMAT_HTML,
				Labels.getLabel("common.template.format.html")));
		return formats;

	}

	public static List<ValueLabel> getTemplatesFor() {

		if (templatesFor != null) {
			return templatesFor;
		}

		templatesFor = new ArrayList<ValueLabel>(9);

		templatesFor.add(new ValueLabel(NotificationConstants.TEMPLATE_FOR_CN,
				Labels.getLabel("label_MailTemplateDialog_CustomerNotification")));
		templatesFor.add(new ValueLabel(NotificationConstants.TEMPLATE_FOR_AE,
				Labels.getLabel("label_MailTemplateDialog_AlertNotification")));
		templatesFor.add(new ValueLabel(NotificationConstants.TEMPLATE_FOR_SP,
				Labels.getLabel("label_MailTemplateDialog_SourcingPartnerNotification")));
		templatesFor.add(new ValueLabel(NotificationConstants.TEMPLATE_FOR_DSAN,
				Labels.getLabel("label_MailTemplateDialog_DSANotification")));
		templatesFor.add(new ValueLabel(NotificationConstants.TEMPLATE_FOR_PVRN,
				Labels.getLabel("label_MailTemplateDialog_PNNotification")));
		templatesFor.add(new ValueLabel(NotificationConstants.TEMPLATE_FOR_SU,
				Labels.getLabel("label_MailTemplateDialog_SecurityUser")));
		return templatesFor;
	}

	public static List<ValueLabel> getAgreementType() {

		if (agreementType != null) {
			return agreementType;
		}
		agreementType = new ArrayList<ValueLabel>(2);
		agreementType.add(new ValueLabel(PennantConstants.DOC_TYPE_PDF, Labels.getLabel("label_AgreementType_PDF")));
		agreementType.add(new ValueLabel(PennantConstants.DOC_TYPE_WORD, Labels.getLabel("label_AgreementType_WORD")));
		return agreementType;
	}
}
