package com.pennant.webui.delegationdeviation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;

import com.pennant.UserWorkspace;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.delegationdeviation.DeviationHelper;
import com.pennant.backend.model.Property;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.ManualDeviation;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.solutionfactory.DeviationParam;
import com.pennant.backend.util.DeviationConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

public class DeviationRenderer {

	private static final Logger logger = LogManager.getLogger(DeviationRenderer.class);

	private static final String bold = " font-weight: bold;";
	private static final String boldAndRed = "font-weight:bold;color:red;";
	int ccyformat = 0;
	private transient UserWorkspace userWorkspace;
	private boolean approverScreen = false;
	private boolean workflow = false;

	List<DeviationParam> deviationParams = PennantAppUtil.getDeviationParams();
	List<ValueLabel> approveStatus = PennantStaticListUtil.getApproveStatus();
	ArrayList<ValueLabel> secRolesList = PennantAppUtil.getSecRolesList(null);
	List<Property> severities = PennantStaticListUtil.getManualDeviationSeverities();
	List<ValueLabel> delegators = new ArrayList<>();

	@Autowired
	private DeviationHelper deviationHelper;

	public void init(UserWorkspace userWorkspace, int ccyformat, boolean approverScreen, boolean workflow,
			List<ValueLabel> delegators) {
		this.userWorkspace = userWorkspace;
		this.ccyformat = ccyformat;
		this.approverScreen = approverScreen;
		this.workflow = workflow;
		this.delegators = delegators;
	}

	class CompareDeviation implements Comparator<FinanceDeviations> {

		public CompareDeviation() {

		}

		@Override
		public int compare(FinanceDeviations o1, FinanceDeviations o2) {
			return o1.getModule().compareTo(o2.getModule());
		}

	}

	public void renderAutoDeviations(List<FinanceDeviations> financeDeviations,
			List<FinanceDeviations> appovedDeviations, Listbox listbox) {
		logger.debug("Entering");

		listbox.getItems().clear();
		List<FinanceDeviations> renderList = deviationHelper.mergeList(financeDeviations, appovedDeviations);

		if (renderList == null || renderList.isEmpty()) {
			return;
		}

		Collections.sort(renderList, new CompareDeviation());

		String module = "";
		final boolean DEV_HIGHER_APPROVAL = StringUtils.equals((SysParamUtil.getValueAsString("DEV_HIGHER_APPROVAL")),
				"Y") ? true : false;

		for (FinanceDeviations deviationDetail : renderList) {
			// to show other deviation which in pending queue but should not be editable
			boolean readOnly = true;
			boolean approved = false;

			if (deviationDetail.isApproved()) {
				approved = true;
			}

			if (DeviationConstants.MULTIPLE_APPROVAL && !approved) {
				if (DEV_HIGHER_APPROVAL) {
					boolean allowedLevel = false;

					for (ValueLabel delegator : delegators) {
						if (!allowedLevel && deviationDetail.getDelegationRole().equals(delegator.getValue())) {
							allowedLevel = true;
						}

						if (allowedLevel && userWorkspace.getUserRoles().contains(delegator.getValue())) {
							readOnly = false;
							break;
						}
					}
				} else {
					if (userWorkspace.getUserRoles().contains(deviationDetail.getDelegationRole())) {
						readOnly = false;
					}
				}
			}

			boolean deviationNotallowed = false;
			Listcell listcell;
			if (!module.equals(deviationDetail.getModule())) {
				module = deviationDetail.getModule();
				Listgroup listgroup = new Listgroup();
				listcell = new Listcell(Labels.getLabel("listGroup_" + deviationDetail.getModule()));
				listcell.setStyle(bold);
				listgroup.appendChild(listcell);
				listbox.appendChild(listgroup);
			}
			if (StringUtils.isEmpty(deviationDetail.getDelegationRole())) {
				deviationNotallowed = true;
			}

			Listitem listitem = new Listitem();
			if (deviationDetail.isMarkDeleted()) {
				listitem.setStyle("background: #f5f5f5;");
			}

			if (StringUtils.equals(DeviationConstants.CAT_AUTO, deviationDetail.getDeviationCategory())) {
				String deviationCodedesc = deviationHelper.getDeviationDesc(deviationDetail, deviationParams);
				listcell = getNewListCell(deviationCodedesc, deviationNotallowed);
				listcell.setTooltiptext(deviationCodedesc);
			} else {
				listcell = new Listcell(
						deviationDetail.getDeviationCode() + " - " + deviationDetail.getDeviationDesc());
				listcell.setStyle("overflow: hidden; text-overflow: ellipsis; white-space: nowrap;");
				listcell.setTooltiptext(deviationDetail.getDeviationDesc());

			}
			listitem.appendChild(listcell);

			listcell = getNewListCell(deviationDetail.getDeviationType(), deviationNotallowed);
			listitem.appendChild(listcell);

			if (StringUtils.equals(DeviationConstants.CAT_AUTO, deviationDetail.getDeviationCategory())) {
				String deviationValue = deviationHelper.getDeviationValue(deviationDetail, ccyformat);
				listcell = getNewListCell(deviationValue, deviationNotallowed);
			} else {
				listcell = new Listcell(deviationDetail.getDeviationValue());
			}
			listitem.appendChild(listcell);

			listcell = getNewListCell(deviationDetail.getUserRole(), deviationNotallowed);
			listitem.appendChild(listcell);

			listcell = new Listcell(
					PennantStaticListUtil.getlabelDesc(deviationDetail.getDelegationRole(), secRolesList));
			listitem.appendChild(listcell);

			listcell = getNewListCell(DateUtil.formatToShortDate(deviationDetail.getDeviationDate()),
					deviationNotallowed);
			listitem.appendChild(listcell);

			listcell = getNewListCell(
					PennantStaticListUtil.getlabelDesc(deviationDetail.getApprovalStatus(), approveStatus),
					deviationNotallowed);

			if (approverScreen || workflow) {
				if (!approved) {
					listcell = getNewListCell("", deviationNotallowed);
					Combobox combobox = new Combobox();
					combobox.setReadonly(true);
					combobox.setWidth("100px");
					combobox.setId("combo_"
							+ (deviationDetail.getDeviationId() < 0 ? 0 : deviationDetail.getDeviationId())
							+ deviationDetail.getDeviationCode() + StringUtils.trimToEmpty(deviationDetail.getModule())
							+ deviationDetail.getDeviationCategory());
					combobox.addForward("onChange", "", "onChangeAutoDevStatus", deviationDetail);
					fillComboBox(combobox, deviationDetail.getApprovalStatus(), approveStatus);
					combobox.setDisabled(readOnly);
					listcell.appendChild(combobox);
				}
			}
			listitem.appendChild(listcell);

			String lable = "";
			Button button = new Button();
			if (approverScreen || workflow) {
				if (!approved) {
					lable = "add";
					button.addForward("onClick", "", "onClickAddNotes", deviationDetail);
				} else {
					lable = "view";
					button.addForward("onClick", "", "onClickViewNotes", deviationDetail);
				}

			} else {
				if (approved) {
					lable = "view";
					button.addForward("onClick", "", "onClickViewNotes", deviationDetail);
				}

			}
			listcell = getNewListCell("", deviationNotallowed);

			button.setLabel(lable);
			button.setDisabled(readOnly);
			if (StringUtils.isNotBlank(lable)) {
				listcell.appendChild(button);
			}
			listitem.appendChild(listcell);

			listcell = getNewListCell(deviationDetail.getDeviationUserId(), deviationNotallowed);
			listitem.appendChild(listcell);
			listcell = getNewListCell(deviationDetail.getDelegatedUserId(), deviationNotallowed);
			listitem.appendChild(listcell);
			listitem.setAttribute("data", deviationDetail);
			listbox.appendChild(listitem);
		}
		logger.debug("Leaving");
	}

	public void renderManualDeviations(List<FinanceDeviations> finDeviations, List<FinanceDeviations> aprvdDeviations,
			Listbox listbox) {
		logger.debug("Entering");

		listbox.getItems().clear();
		// ### 05-05-2018- Start- story #361(tuleap server) Manual Deviations

		// Address soft delete of approved / deleted manual deviations.
		for (FinanceDeviations deviation : finDeviations) {
			for (FinanceDeviations apprDeviation : aprvdDeviations) {
				if (deviation.getDeviationId() == apprDeviation.getDeviationId()) {
					aprvdDeviations.remove(apprDeviation);
					deviation.setMarkDeleted(true);

					break;
				}
			}
		}
		// ### 05-05-2018- End- story #361(tuleap server) Manual Deviations

		List<FinanceDeviations> renderList = deviationHelper.mergeList(finDeviations, aprvdDeviations);

		if (renderList == null || renderList.isEmpty()) {
			return;
		}

		for (FinanceDeviations deviation : renderList) {
			// to show other deviation which in pending queue but should not be editable
			boolean pending = false;
			boolean approved = false;

			if (deviation.isApproved()) {
				approved = true;
			}

			if (DeviationConstants.MULTIPLE_APPROVAL && !approved) {
				if (!userWorkspace.getUserRoles().contains(deviation.getDelegationRole())) {
					pending = true;
				}
			}

			boolean devNotallowed = false;
			Listcell listcell;

			if (StringUtils.isEmpty(deviation.getDelegationRole())) {
				devNotallowed = true;
			}

			Listitem listitem = new Listitem();

			// Deviation
			listcell = getNewListCell(deviation.getDeviationCodeName() + " - " + deviation.getDeviationCodeDesc(),
					devNotallowed);
			listcell.setStyle("overflow: hidden; text-overflow: ellipsis; white-space: nowrap;");
			listcell.setTooltiptext(deviation.getDeviationCodeDesc());
			listitem.appendChild(listcell);

			// Severity
			listcell = getNewListCell(PennantStaticListUtil.getPropertyValue(severities, deviation.getSeverity()),
					devNotallowed);
			listitem.appendChild(listcell);

			// Raised User
			listcell = getNewListCell(deviation.getRaisedUser(), devNotallowed);
			listitem.appendChild(listcell);

			// Raised Role
			listcell = getNewListCell(deviation.getUserRole(), devNotallowed);
			listitem.appendChild(listcell);

			// Raised On
			listcell = getNewListCell(DateUtil.formatToShortDate(deviation.getDeviationDate()), devNotallowed);
			listitem.appendChild(listcell);

			// Approval Authority
			listcell = new Listcell(PennantStaticListUtil.getlabelDesc(deviation.getDelegationRole(), secRolesList));
			listitem.appendChild(listcell);

			// Approval Status
			String status = PennantStaticListUtil.getlabelDesc(deviation.getApprovalStatus(), approveStatus);
			if (deviation.isMarkDeleted()) {
				status += " [Cancelled]";
			}

			listcell = getNewListCell(status, devNotallowed);

			if (approverScreen) {
				if (!approved) {
					listcell = getNewListCell("", devNotallowed);
					Combobox combobox = new Combobox();
					combobox.setReadonly(true);
					combobox.setWidth("100px");
					combobox.setId("combo_" + (deviation.getDeviationId() < 0 ? 0 : deviation.getDeviationId())
							+ deviation.getDeviationCode() + StringUtils.trimToEmpty(deviation.getModule())
							+ deviation.getDeviationCategory());
					fillComboBox(combobox, deviation.getApprovalStatus(), approveStatus);
					combobox.setDisabled(pending);
					listcell.appendChild(combobox);
				}

			}

			listitem.appendChild(listcell);

			// Remarks
			listcell = getNewListCell(StringUtils.trimToEmpty(deviation.getRemarks()), devNotallowed);
			listcell.setStyle("overflow: hidden; text-overflow: ellipsis; white-space: nowrap;");
			listcell.setTooltiptext(deviation.getRemarks());
			listitem.appendChild(listcell);

			// Mitigants
			listcell = getNewListCell(StringUtils.trimToEmpty(deviation.getMitigants()), devNotallowed);
			listcell.setStyle("overflow: hidden; text-overflow: ellipsis; white-space: nowrap;");
			listcell.setTooltiptext(deviation.getMitigants());
			listitem.appendChild(listcell);

			// Operation
			listcell = new Listcell(PennantJavaUtil.getLabel(deviation.getRecordType()));
			listitem.appendChild(listcell);

			listitem.setAttribute("data", deviation);
			if (!approverScreen || workflow) {
				ComponentsCtrl.applyForward(listitem, "onDoubleClick=onManualDeviationItemDoubleClicked");
			}

			listbox.appendChild(listitem);
		}
		logger.debug("Leaving");
	}

	private Listcell getNewListCell(String val, boolean colrRed) {
		Listcell listcell = new Listcell(val);
		if (colrRed) {
			listcell.setStyle(boldAndRed);
		}
		return listcell;
	}

	/**
	 * Method to fill the combobox with given list of values
	 * 
	 * @param combobox
	 * @param value
	 * @param list
	 */
	public void fillComboBox(Combobox combobox, String value, List<ValueLabel> list) {
		logger.debug("Entering fillComboBox()");
		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.pending"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		combobox.setReadonly(true);
		for (ValueLabel valueLabel : list) {
			comboitem = new Comboitem();
			comboitem.setValue(valueLabel.getValue());
			comboitem.setLabel(valueLabel.getLabel());
			combobox.appendChild(comboitem);

			if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(valueLabel.getValue()))) {
				combobox.setSelectedItem(comboitem);
			}
		}
		logger.debug("Leaving fillComboBox()");
	}

	public void setDescriptions(List<FinanceDeviations> financeDeviations) {
		if (financeDeviations != null && !financeDeviations.isEmpty()) {
			for (FinanceDeviations finDeviations : financeDeviations) {
				if (StringUtils.equals(DeviationConstants.CAT_MANUAL, finDeviations.getDeviationCategory())) {
					long parseLong = Long.parseLong(finDeviations.getDeviationCode());
					ManualDeviation deviation = deviationHelper.getManualDeviationDesc(parseLong);
					if (deviation != null) {
						finDeviations.setDeviationCodeName(deviation.getCode());
						finDeviations.setDeviationCodeDesc(deviation.getDescription());
						finDeviations.setSeverity(deviation.getSeverity());
					}
				}
			}
		}

	}
}
