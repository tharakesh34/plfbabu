package com.pennanttech.pff.eod.auto.knockoff.reval;

import org.springframework.batch.item.database.JdbcCursorItemReader;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.AutoKnockOffExcess;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

public class AutoKnockOffProcessTaskletItemReader extends JdbcCursorItemReader<AutoKnockOffExcess> {

	public AutoKnockOffProcessTaskletItemReader() {
		super.setSql(getSql());
	}

	private String valueDate = null;

	@Override
	public String getSql() {

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" aked.ID, ake.ID ExcessID, ake.FinID, ake.FinReference, ake.AmountType, ake.ValueDate");
		sql.append(", ake.BalanceAmount, aked.KnockOffID, aked.Code, aked.Description, aked.ExecutionDays");
		sql.append(", aked.FinType, aked.FeeTypeCode, aked.KnockOffOrder, aked.FeeOrder");
		sql.append(", ake.ExecutionDay, ake.ThresholdValue, aked.FinCcy, ake.PayableId");
		sql.append(", coalesce(frhCount, 0) FrhCount, coalesce(fmtCount, 0) FmtCount");
		sql.append(" From AUTO_KNOCKOFF_EXCESS ake");
		sql.append(" Inner Join AUTO_KNOCKOFF_EXCESS_DETAILS aked on aked.ExcessId = ake.ID");
		sql.append(" Left join (select count(*) frhcount, Reference from FinReceiptHeader_Temp rh");
		sql.append(" Inner join FinReceiptDetail_Temp rd on rd.ReceiptId = rh.ReceiptId");
		sql.append(" Group by Reference) rh on rh.Reference = ake.FinReference");
		/* FIXME : change to FinID */
		sql.append(" Left join (select count(*) fmtcount, FinID from FinanceMain_Temp");
		sql.append(" Group by FinID) fmt on ake.FinID = fmt.FinID");
		sql.append(" Where ake.ProcessingFlag = 0");
		sql.append(" and valueDate = ");

		if (valueDate == null) {
			getValueDate();
		}

		sql.append(valueDate);
		sql.append(" order by aked.ID, aked.KnockOffOrder, aked.FeeOrder");

		return sql.toString();
	}

	private void getValueDate() {
		valueDate = "'" + DateUtil.format(SysParamUtil.getAppDate(), DateFormat.FULL_DATE) + "'";
	}

}
