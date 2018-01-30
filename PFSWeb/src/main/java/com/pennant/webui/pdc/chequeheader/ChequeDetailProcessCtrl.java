package com.pennant.webui.pdc.chequeheader;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseCtrl;

public class ChequeDetailProcessCtrl extends GFCBaseCtrl<ChequeHeader> {
	private static final long	serialVersionUID	= 2304108712400544137L;
	private static final Logger	logger				= Logger.getLogger(ChequeDetailProcessCtrl.class);

	private Window				parentWindow;
	private Listbox				listbox;
	private String				ccy;
	private int					ccyFormat;
	private boolean				multiParty;
	private String				role;

	public void init(Window window, Listbox listbox, String ccy, boolean multiParty, String role) {
		this.ccyFormat = CurrencyUtil.getFormat(ccy);
		this.ccy = ccy;
		this.multiParty = multiParty;
		this.role = role;
		this.listbox = listbox;
		this.parentWindow = window;
	}

	public void doFillChequeDetails(Listbox listbox, List<ChequeDetail> chequeDetails,
			List<FinanceScheduleDetail> financeScheduleDetails) {
		if (chequeDetails != null && chequeDetails.size() > 0) {
			for (ChequeDetail chequeDetail : chequeDetails) {
				boolean readonly = false;

				if (StringUtils.trimToEmpty(chequeDetail.getRecordType()).equals(PennantConstants.RCD_DEL)) {
					readonly = true;
				}

				Listitem listitem = new Listitem();
				listitem.setAttribute("data", chequeDetail);
				Listcell listcell;

				// ChequeSerialNo
				listcell = new Listcell(String.format("%06d", chequeDetail.getChequeSerialNo()));
				listcell.setParent(listitem);

				// Bank branch id
				listcell = new Listcell();
				ExtendedCombobox bankBranchID = new ExtendedCombobox();
				bankBranchID.setModuleName("BankBranch");
				bankBranchID.setReadonly(true);
				bankBranchID.setValueColumn("BranchCode");
				bankBranchID.setDescColumn("BranchDesc");
				bankBranchID.setDisplayStyle(2);
				bankBranchID.setValidateColumns(new String[] { "BranchCode" });
				bankBranchID.setValue(String.valueOf(chequeDetail.getBankBranchID()));
				bankBranchID.setDescription(chequeDetail.getBankBranchIDName());
				listcell.appendChild(bankBranchID);
				listcell.setParent(listitem);

				// AccountNo
				listcell = new Listcell(chequeDetail.getAccountNo());
				listcell.setParent(listitem);

				// Emi ref
				listcell = new Listcell();
				Combobox emiReference = getCombobox("1", financeScheduleDetails);
				Combobox emi = getCombobox(chequeDetail.geteMIRefNo(), financeScheduleDetails);
				emiReference.setValue(emi.getSelectedItem().getLabel());
				readOnlyComponent(readonly, emiReference);
				listcell.appendChild(emiReference);
				listcell.setParent(listitem);

				// Amount
				listcell = new Listcell();
				Decimalbox emiAmount = new Decimalbox();
				emiAmount.setFormat(PennantConstants.in_amountFormate2);
				//String curField = getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy();
				emiAmount.setValue(PennantApplicationUtil.formateAmount(chequeDetail.getAmount(), 2));
				emiAmount.addForward("onChange", this.parentWindow, "onChangeEmiAmount", emiAmount);
				readOnlyComponent(readonly, emiAmount);
				listcell.appendChild(emiAmount);
				listcell.setParent(listitem);

				// Bank branch id
				listcell = new Listcell();
				Button delButton = new Button("Delete");
				Object[] objected = new Object[2];
				objected[0] = chequeDetail;
				objected[1] = listitem;
				delButton.addForward("onClick", this.parentWindow, "onClickDeleteButton", objected);
				readOnlyComponent(readonly, delButton);
				listcell.appendChild(delButton);
				listcell.setParent(listitem);

				// only to avoid the number format exception while setting the
				// value to bean
				listcell = new Listcell(chequeDetail.getAmount().toString());
				listcell.setParent(listitem);
				listcell.setVisible(false);
				// listbox.setStyle("overflow:auto");
				listbox.appendChild(listitem);
			}
		}
	}

	private Combobox getCombobox(String eminumber, List<FinanceScheduleDetail> financeScheduleDetails) {
		List<FinanceScheduleDetail> list = financeScheduleDetails;
		Combobox combobox = new Combobox();
		combobox.setSclass(PennantConstants.mandateSclass);
		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue(PennantConstants.List_Select);
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		combobox.setReadonly(true);
		for (FinanceScheduleDetail valueLabel : list) {
			if (valueLabel.isRepayOnSchDate() || valueLabel.isPftOnSchDate()) {
				comboitem = new Comboitem();
				comboitem.setValue(valueLabel.getInstNumber());
				comboitem.setLabel(DateUtility.formatToShortDate(valueLabel.getSchDate()));
				combobox.appendChild(comboitem);
				if (String.valueOf(valueLabel.getInstNumber()).equals(String.valueOf(eminumber))) {
					combobox.setSelectedItem(comboitem);
				}
			}
		}
		return combobox;
	}
}
