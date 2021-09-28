package com.pennant.pff.core.presentment;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;

public class PresentmentResponseRowmapper implements RowMapper<PresentmentDetail> {

	@Override
	public PresentmentDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
		PresentmentDetail pd = new PresentmentDetail();

		pd.setId(rs.getLong("ID"));
		pd.setHeaderId(rs.getLong("PRESENTMENTID"));
		// pd.setResponseId(rs.getLong("RESPONSEID"));
		pd.setFinID(rs.getLong("FINID"));
		pd.setFinReference(rs.getString("FINREFERENCE"));
		pd.setHostReference(rs.getString("HOST_REFERENCE"));
		pd.setFinType(rs.getString("FINTYPE"));
		pd.setFinisActive(rs.getBoolean("FINISACTIVE"));
		pd.setSchDate(rs.getDate("SCHDATE"));
		pd.setMandateId(rs.getLong("MANDATEID"));
		pd.setMandateType(rs.getString("MANDATETYPE"));
		pd.setSchAmtDue(rs.getBigDecimal("SCHAMTDUE"));
		pd.setSchPriDue(rs.getBigDecimal("SCHPRIDUE"));
		pd.setSchPftDue(rs.getBigDecimal("SCHPFTDUE"));
		pd.setSchFeeDue(rs.getBigDecimal("SCHFEEDUE"));
		pd.setSchInsDue(rs.getBigDecimal("SCHINSDUE"));
		pd.setSchPenaltyDue(rs.getBigDecimal("SCHPENALTYDUE"));
		pd.setAdvanceAmt(rs.getBigDecimal("ADVANCEAMT"));
		pd.setExcessID(rs.getLong("EXCESSID"));
		pd.setAdviseAmt(rs.getBigDecimal("ADVISEAMT"));
		pd.setPresentmentAmt(rs.getBigDecimal("PRESENTMENTAMT"));
		pd.settDSAmount(rs.getBigDecimal("TDSAMOUNT"));
		pd.setExcludeReason(rs.getInt("EXCLUDEREASON"));
		pd.setEmiNo(rs.getInt("EMINO"));
		pd.setStatus(rs.getString("STATUS"));
		pd.setBounceCode(rs.getString("BOUNCE_CODE"));
		pd.setBounceRemarks(rs.getString("BOUNCE_REMARKS"));
		pd.setClearingStatus(rs.getString("CLEARING_STATUS"));
		pd.setPresentmentRef(rs.getString("PRESENTMENTREF"));
		pd.setEcsReturn(rs.getString("ECSRETURN"));
		pd.setReceiptID(rs.getLong("RECEIPTID"));
		pd.setErrorCode(rs.getString("ERRORCODE"));
		pd.setErrorDesc(rs.getString("ERRORDESC"));
		pd.setManualAdviseId(JdbcUtil.getLong(rs.getObject("MANUALADVISEID")));
		pd.setAccountNo(rs.getString("ACCOUNT_NUMBER"));
		// pd.setAcType(rs.getString("ACTYPE"));
		pd.setUtrNumber(rs.getString("UTR_Number"));

		return pd;
	}

}
