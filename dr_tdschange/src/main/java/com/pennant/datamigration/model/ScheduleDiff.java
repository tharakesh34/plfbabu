package com.pennant.datamigration.model;

import java.util.*;
import java.math.*;

public class ScheduleDiff
{
    private String finReference;
    private Date schDate;
    private BigDecimal disbAmount;
    private BigDecimal balanceForPftCal;
    private BigDecimal profitCalc;
    private BigDecimal profitSchd;
    private BigDecimal principalSchd;
    private BigDecimal repayAmount;
    private BigDecimal profitBalance;
    private BigDecimal feeChargeAmt;
    private BigDecimal cpzAmount;
    private BigDecimal closingBalance;
    private BigDecimal schdPftPaid;
    private BigDecimal schdPriPaid;
    private BigDecimal tDSAmount;
    private BigDecimal tDSPaid;
    private BigDecimal partialPaidAmt;
    private BigDecimal limitDrop;
    private BigDecimal oDLimit;
    private BigDecimal availableLimit;
    private BigDecimal new_DisbAmount;
    private BigDecimal new_BalanceForPftCal;
    private BigDecimal new_ProfitCalc;
    private BigDecimal new_ProfitSchd;
    private BigDecimal new_PrincipalSchd;
    private BigDecimal new_RepayAmount;
    private BigDecimal new_ProfitBalance;
    private BigDecimal new_FeeChargeAmt;
    private BigDecimal new_CpzAmount;
    private BigDecimal new_ClosingBalance;
    private BigDecimal new_SchdPftPaid;
    private BigDecimal new_SchdPriPaid;
    private BigDecimal new_TDSAmount;
    private BigDecimal new_TDSPaid;
    private BigDecimal new_PartialPaidAmt;
    private BigDecimal new_LimitDrop;
    private BigDecimal new_ODLimit;
    private BigDecimal new_AvailableLimit;
    private String bpiOrHoliday;
    private String new_BpiOrHoliday;
    
    public ScheduleDiff() {
        this.finReference = null;
        this.disbAmount = BigDecimal.ZERO;
        this.balanceForPftCal = BigDecimal.ZERO;
        this.profitCalc = BigDecimal.ZERO;
        this.profitSchd = BigDecimal.ZERO;
        this.principalSchd = BigDecimal.ZERO;
        this.repayAmount = BigDecimal.ZERO;
        this.profitBalance = BigDecimal.ZERO;
        this.feeChargeAmt = BigDecimal.ZERO;
        this.cpzAmount = BigDecimal.ZERO;
        this.closingBalance = BigDecimal.ZERO;
        this.schdPftPaid = BigDecimal.ZERO;
        this.schdPriPaid = BigDecimal.ZERO;
        this.tDSAmount = BigDecimal.ZERO;
        this.tDSPaid = BigDecimal.ZERO;
        this.partialPaidAmt = BigDecimal.ZERO;
        this.limitDrop = BigDecimal.ZERO;
        this.oDLimit = BigDecimal.ZERO;
        this.availableLimit = BigDecimal.ZERO;
        this.new_DisbAmount = BigDecimal.ZERO;
        this.new_BalanceForPftCal = BigDecimal.ZERO;
        this.new_ProfitCalc = BigDecimal.ZERO;
        this.new_ProfitSchd = BigDecimal.ZERO;
        this.new_PrincipalSchd = BigDecimal.ZERO;
        this.new_RepayAmount = BigDecimal.ZERO;
        this.new_ProfitBalance = BigDecimal.ZERO;
        this.new_FeeChargeAmt = BigDecimal.ZERO;
        this.new_CpzAmount = BigDecimal.ZERO;
        this.new_ClosingBalance = BigDecimal.ZERO;
        this.new_SchdPftPaid = BigDecimal.ZERO;
        this.new_SchdPriPaid = BigDecimal.ZERO;
        this.new_TDSAmount = BigDecimal.ZERO;
        this.new_TDSPaid = BigDecimal.ZERO;
        this.new_PartialPaidAmt = BigDecimal.ZERO;
        this.new_LimitDrop = BigDecimal.ZERO;
        this.new_ODLimit = BigDecimal.ZERO;
        this.new_AvailableLimit = BigDecimal.ZERO;
        this.bpiOrHoliday = "";
        this.new_BpiOrHoliday = "";
    }
    
    public String getFinReference() {
        return this.finReference;
    }
    
    public void setFinReference(final String finReference) {
        this.finReference = finReference;
    }
    
    public Date getSchDate() {
        return this.schDate;
    }
    
    public void setSchDate(final Date schDate) {
        this.schDate = schDate;
    }
    
    public BigDecimal getBalanceForPftCal() {
        return this.balanceForPftCal;
    }
    
    public void setBalanceForPftCal(final BigDecimal balanceForPftCal) {
        this.balanceForPftCal = balanceForPftCal;
    }
    
    public BigDecimal getProfitCalc() {
        return this.profitCalc;
    }
    
    public void setProfitCalc(final BigDecimal profitCalc) {
        this.profitCalc = profitCalc;
    }
    
    public BigDecimal getProfitSchd() {
        return this.profitSchd;
    }
    
    public void setProfitSchd(final BigDecimal profitSchd) {
        this.profitSchd = profitSchd;
    }
    
    public BigDecimal getPrincipalSchd() {
        return this.principalSchd;
    }
    
    public void setPrincipalSchd(final BigDecimal principalSchd) {
        this.principalSchd = principalSchd;
    }
    
    public BigDecimal getRepayAmount() {
        return this.repayAmount;
    }
    
    public void setRepayAmount(final BigDecimal repayAmount) {
        this.repayAmount = repayAmount;
    }
    
    public BigDecimal getProfitBalance() {
        return this.profitBalance;
    }
    
    public void setProfitBalance(final BigDecimal profitBalance) {
        this.profitBalance = profitBalance;
    }
    
    public BigDecimal getDisbAmount() {
        return this.disbAmount;
    }
    
    public void setDisbAmount(final BigDecimal disbAmount) {
        this.disbAmount = disbAmount;
    }
    
    public BigDecimal getFeeChargeAmt() {
        return this.feeChargeAmt;
    }
    
    public void setFeeChargeAmt(final BigDecimal feeChargeAmt) {
        this.feeChargeAmt = feeChargeAmt;
    }
    
    public BigDecimal getCpzAmount() {
        return this.cpzAmount;
    }
    
    public void setCpzAmount(final BigDecimal cpzAmount) {
        this.cpzAmount = cpzAmount;
    }
    
    public BigDecimal getClosingBalance() {
        return this.closingBalance;
    }
    
    public void setClosingBalance(final BigDecimal closingBalance) {
        this.closingBalance = closingBalance;
    }
    
    public BigDecimal getSchdPftPaid() {
        return this.schdPftPaid;
    }
    
    public void setSchdPftPaid(final BigDecimal schdPftPaid) {
        this.schdPftPaid = schdPftPaid;
    }
    
    public BigDecimal getSchdPriPaid() {
        return this.schdPriPaid;
    }
    
    public void setSchdPriPaid(final BigDecimal schdPriPaid) {
        this.schdPriPaid = schdPriPaid;
    }
    
    public BigDecimal getTDSAmount() {
        return this.tDSAmount;
    }
    
    public void setTDSAmount(final BigDecimal tDSAmount) {
        this.tDSAmount = tDSAmount;
    }
    
    public BigDecimal getTDSPaid() {
        return this.tDSPaid;
    }
    
    public void setTDSPaid(final BigDecimal tDSPaid) {
        this.tDSPaid = tDSPaid;
    }
    
    public BigDecimal getPartialPaidAmt() {
        return this.partialPaidAmt;
    }
    
    public void setPartialPaidAmt(final BigDecimal partialPaidAmt) {
        this.partialPaidAmt = partialPaidAmt;
    }
    
    public BigDecimal getLimitDrop() {
        return this.limitDrop;
    }
    
    public void setLimitDrop(final BigDecimal limitDrop) {
        this.limitDrop = limitDrop;
    }
    
    public BigDecimal getODLimit() {
        return this.oDLimit;
    }
    
    public void setODLimit(final BigDecimal oDLimit) {
        this.oDLimit = oDLimit;
    }
    
    public BigDecimal getAvailableLimit() {
        return this.availableLimit;
    }
    
    public void setAvailableLimit(final BigDecimal availableLimit) {
        this.availableLimit = availableLimit;
    }
    
    public BigDecimal getNew_BalanceForPftCal() {
        return this.new_BalanceForPftCal;
    }
    
    public void setNew_BalanceForPftCal(final BigDecimal new_BalanceForPftCal) {
        this.new_BalanceForPftCal = new_BalanceForPftCal;
    }
    
    public BigDecimal getNew_ProfitCalc() {
        return this.new_ProfitCalc;
    }
    
    public void setNew_ProfitCalc(final BigDecimal new_ProfitCalc) {
        this.new_ProfitCalc = new_ProfitCalc;
    }
    
    public BigDecimal getNew_ProfitSchd() {
        return this.new_ProfitSchd;
    }
    
    public void setNew_ProfitSchd(final BigDecimal new_ProfitSchd) {
        this.new_ProfitSchd = new_ProfitSchd;
    }
    
    public BigDecimal getNew_PrincipalSchd() {
        return this.new_PrincipalSchd;
    }
    
    public void setNew_PrincipalSchd(final BigDecimal new_PrincipalSchd) {
        this.new_PrincipalSchd = new_PrincipalSchd;
    }
    
    public BigDecimal getNew_RepayAmount() {
        return this.new_RepayAmount;
    }
    
    public void setNew_RepayAmount(final BigDecimal new_RepayAmount) {
        this.new_RepayAmount = new_RepayAmount;
    }
    
    public BigDecimal getNew_ProfitBalance() {
        return this.new_ProfitBalance;
    }
    
    public void setNew_ProfitBalance(final BigDecimal new_ProfitBalance) {
        this.new_ProfitBalance = new_ProfitBalance;
    }
    
    public BigDecimal getNew_DisbAmount() {
        return this.new_DisbAmount;
    }
    
    public void setNew_DisbAmount(final BigDecimal new_DisbAmount) {
        this.new_DisbAmount = new_DisbAmount;
    }
    
    public BigDecimal getNew_FeeChargeAmt() {
        return this.new_FeeChargeAmt;
    }
    
    public void setNew_FeeChargeAmt(final BigDecimal new_FeeChargeAmt) {
        this.new_FeeChargeAmt = new_FeeChargeAmt;
    }
    
    public BigDecimal getNew_CpzAmount() {
        return this.new_CpzAmount;
    }
    
    public void setNew_CpzAmount(final BigDecimal new_CpzAmount) {
        this.new_CpzAmount = new_CpzAmount;
    }
    
    public BigDecimal getNew_ClosingBalance() {
        return this.new_ClosingBalance;
    }
    
    public void setNew_ClosingBalance(final BigDecimal new_ClosingBalance) {
        this.new_ClosingBalance = new_ClosingBalance;
    }
    
    public BigDecimal getNew_SchdPftPaid() {
        return this.new_SchdPftPaid;
    }
    
    public void setNew_SchdPftPaid(final BigDecimal new_SchdPftPaid) {
        this.new_SchdPftPaid = new_SchdPftPaid;
    }
    
    public BigDecimal getNew_SchdPriPaid() {
        return this.new_SchdPriPaid;
    }
    
    public void setNew_SchdPriPaid(final BigDecimal new_SchdPriPaid) {
        this.new_SchdPriPaid = new_SchdPriPaid;
    }
    
    public BigDecimal getNew_TDSAmount() {
        return this.new_TDSAmount;
    }
    
    public void setNew_TDSAmount(final BigDecimal new_TDSAmount) {
        this.new_TDSAmount = new_TDSAmount;
    }
    
    public BigDecimal getNew_TDSPaid() {
        return this.new_TDSPaid;
    }
    
    public void setNew_TDSPaid(final BigDecimal new_TDSPaid) {
        this.new_TDSPaid = new_TDSPaid;
    }
    
    public BigDecimal getNew_PartialPaidAmt() {
        return this.new_PartialPaidAmt;
    }
    
    public void setNew_PartialPaidAmt(final BigDecimal new_PartialPaidAmt) {
        this.new_PartialPaidAmt = new_PartialPaidAmt;
    }
    
    public BigDecimal getNew_LimitDrop() {
        return this.new_LimitDrop;
    }
    
    public void setNew_LimitDrop(final BigDecimal new_LimitDrop) {
        this.new_LimitDrop = new_LimitDrop;
    }
    
    public BigDecimal getNew_ODLimit() {
        return this.new_ODLimit;
    }
    
    public void setNew_ODLimit(final BigDecimal new_ODLimit) {
        this.new_ODLimit = new_ODLimit;
    }
    
    public BigDecimal getNew_AvailableLimit() {
        return this.new_AvailableLimit;
    }
    
    public void setNew_AvailableLimit(final BigDecimal new_AvailableLimit) {
        this.new_AvailableLimit = new_AvailableLimit;
    }
    
    public String getBpiOrHoliday() {
        return this.bpiOrHoliday;
    }
    
    public void setBpiOrHoliday(final String bpiOrHoliday) {
        this.bpiOrHoliday = bpiOrHoliday;
    }
    
    public String getNew_BpiOrHoliday() {
        return this.new_BpiOrHoliday;
    }
    
    public void setNew_BpiOrHoliday(final String new_BpiOrHoliday) {
        this.new_BpiOrHoliday = new_BpiOrHoliday;
    }
}