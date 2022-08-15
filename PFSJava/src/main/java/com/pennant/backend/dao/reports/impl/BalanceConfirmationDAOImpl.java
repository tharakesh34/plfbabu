package com.pennant.backend.dao.reports.impl;

import java.math.BigDecimal;

import com.pennant.backend.dao.reports.BalanceConfirmationDAO;
import com.pennant.backend.model.systemmasters.BalanceConfirmation;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

public class BalanceConfirmationDAOImpl extends SequenceDao<BalanceConfirmation> implements BalanceConfirmationDAO {

	@Override
	public BalanceConfirmation getBalanceConfirmation(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(
				"  C.CustShrtname Name, fm.FinID, fm.Finreference FinReference, CA.CUSTADDRHNBR|| ' '||CA.CUSTADDRSTREET Address1");
		sql.append(", City.PCCITYNAME ||' '||Prov.CPPROVINCENAME||' '||Nat.COUNTRYDESC Address2, PC.PinCode Address3");
		sql.append(", Fp.FirstDisbDate DisbDate, fp.TOTALPRIBAL PrincipalOS, fp.ODPROFIT+tds.Duetds ProfitOS");
		sql.append(", fm.APPLICATIONNO SanctionRefNo");
		sql.append(", case when F.TaxComponent = 'E'");
		sql.append(" then t2.TOTPENALTYBAL + t2.TOTPENALTYBAL*0.18 +t2.LPIBAL");
		sql.append(" else coalesce(t2.TOTPENALTYBALLPIBAL,0) end ODRem");
		sql.append(", coalesce(finFee.Remaining,0) FeeDue");
		sql.append(" from Financemain fm");
		sql.append(" inner join Customers C on C.Custid = fm.CUSTID");
		sql.append(" left join CUSTOMERADDRESSES CA on CA.Custid = C.CUSTID and CA.CUSTADDRTYPE = 'COMADDR1'");
		sql.append(" left join Pincodes PC on Pc.PincodeId = CA.PincodeId");
		sql.append(" left join FinpftDetails fp on fp.Finreference = fm.Finreference");
		sql.append(" left join (select Fsd.Finreference,Sum(TDSAMOUNT - TDSPAID) duetds from FINSCHEDULEDETAILS  Fsd");
		sql.append(" where SCHDATE <= (select TO_DATE(SysParmvalue,'YYYY/MM/DD') SysParmvalue");
		sql.append(" from smtparameters where sysparmcode = 'APP_DATE')");
		sql.append(" group by Fsd.Finreference) tds on tds.Finreference = fm.FINREFERENCE");
		sql.append(" left join RMTPROVINCEVSCITY City on city.PCcity = CA.CUSTADDRCITY");
		sql.append(" left join RMTCOUNTRYVSPROVINCE Prov on Prov.CPPROVINCE = CA.CUSTADDRPROVINCE");
		sql.append(" left join BMTCountries Nat on Nat.COUNTRYCODE = CA.CUSTADDRCOUNTRY");
		sql.append(" left join(select finreference, sum(remainingfee)  Remaining from finfeedetail t1");
		sql.append(
				" where feeid not in (select feetypeid from feetypes where feetypecode not in ('BOUNCE')) and feeschedulemethod not in ('DISB','POSP')  group by finreference)FinFee  on Finfee.finreference=fm.finreference");
		sql.append(" left join (select Finreference,SUM(TOTPENALTYBAL) TOTPENALTYBAL,SUM(LPIBAL) LPIBAL,");
		sql.append(" SUM(TOTPENALTYBAL + LPIBAL) TOTPENALTYBALLPIBAL from finoddetails");
		sql.append(" Group by Finreference) T2 on fm.Finreference=T2.Finreference");
		sql.append(" left join feetypes F on F.Feetypecode = 'ODC'");
		sql.append(" Where fm.Finreference = ?");

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
			BalanceConfirmation bc = new BalanceConfirmation();

			bc.setCustShrtName(rs.getString("Name"));
			bc.setFinID(rs.getLong("FinID"));
			bc.setFinReference(rs.getString("FinReference"));
			bc.setAddress1(rs.getString("Address1"));
			bc.setAddress2(rs.getString("Address2"));
			bc.setPinCode(rs.getString("Address3"));
			bc.setDisbursementDate(JdbcUtil.getDate(rs.getDate("DisbDate")));
			bc.setPrincipalOS(rs.getString("PrincipalOS"));
			bc.setProfitOS(rs.getString("ProfitOS"));
			bc.setSanctionRefNo(rs.getString("SanctionRefNo"));
			BigDecimal otherCharges = rs.getBigDecimal("ODRem").add(rs.getBigDecimal("FeeDue"));
			bc.setOtherCharges(otherCharges.toString());
			BigDecimal totalOSBalance = rs.getBigDecimal("PrincipalOS").add(rs.getBigDecimal("ProfitOS"))
					.add(otherCharges);
			bc.setTotalOSBalance(totalOSBalance.toString());

			return bc;
		}, finReference);
	}

}
