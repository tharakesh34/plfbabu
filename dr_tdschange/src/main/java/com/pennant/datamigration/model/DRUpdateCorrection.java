package com.pennant.datamigration.model;

import java.io.*;
import com.pennant.backend.model.Repayments.*;
import com.pennant.backend.model.finance.*;
import com.pennant.backend.model.financemanagement.*;
import java.util.*;

public class DRUpdateCorrection implements Serializable
{
    private static final long serialVersionUID = 1183720618731771888L;
    private List<ReceiptAllocationDetail> updRadList;
    private List<FinRepayHeader> updRphList;
    private FinExcessAmount updFea;
    private List<FinanceRepayments> updRpdList;
    private List<FinReceiptDetail> updRcdList;
    private List<FinReceiptHeader> updRchList;
    private List<RepayScheduleDetail> updRsdList;
    private List<FinanceScheduleDetail> updFsdList;
    private List<PresentmentDetail> updPmdList;
    private List<FinRepayHeader> newRphList;
    private List<FinanceRepayments> newRpdList;
    private List<RepayScheduleDetail> newRsdList;
    private List<ReceiptAllocationDetail> newRadList;
    private List<FinRepayHeader> dltRphList;
    private List<FinanceRepayments> dltRpdList;
    private List<RepayScheduleDetail> dltRsdList;
    private List<ReceiptAllocationDetail> dltRadList;
    private FinExcessAmount dltFea;
    
    public DRUpdateCorrection() {
        this.updRadList = new ArrayList<ReceiptAllocationDetail>(1);
        this.updRphList = new ArrayList<FinRepayHeader>(1);
        this.updFea = new FinExcessAmount();
        this.updRpdList = new ArrayList<FinanceRepayments>(1);
        this.updRcdList = new ArrayList<FinReceiptDetail>(1);
        this.updRchList = new ArrayList<FinReceiptHeader>(1);
        this.updRsdList = new ArrayList<RepayScheduleDetail>(1);
        this.updFsdList = new ArrayList<FinanceScheduleDetail>(1);
        this.updPmdList = new ArrayList<PresentmentDetail>(1);
        this.newRphList = new ArrayList<FinRepayHeader>(1);
        this.newRpdList = new ArrayList<FinanceRepayments>(1);
        this.newRsdList = new ArrayList<RepayScheduleDetail>(1);
        this.newRadList = new ArrayList<ReceiptAllocationDetail>(1);
        this.dltRphList = new ArrayList<FinRepayHeader>(1);
        this.dltRpdList = new ArrayList<FinanceRepayments>(1);
        this.dltRsdList = new ArrayList<RepayScheduleDetail>(1);
        this.dltRadList = new ArrayList<ReceiptAllocationDetail>(1);
        this.dltFea = new FinExcessAmount();
    }
    
    public List<FinanceRepayments> getNewRpdList() {
        return this.newRpdList;
    }
    
    public List<FinRepayHeader> getDltRphList() {
        return this.dltRphList;
    }
    
    public void setDltRphList(final List<FinRepayHeader> dltRphList) {
        this.dltRphList = dltRphList;
    }
    
    public List<FinanceRepayments> getDltRpdList() {
        return this.dltRpdList;
    }
    
    public void setDltRpdList(final List<FinanceRepayments> dltRpdList) {
        this.dltRpdList = dltRpdList;
    }
    
    public List<ReceiptAllocationDetail> getDltRadList() {
        return this.dltRadList;
    }
    
    public void setDltRadList(final List<ReceiptAllocationDetail> dltRadList) {
        this.dltRadList = dltRadList;
    }
    
    public void setNewRpdList(final List<FinanceRepayments> newRpdList) {
        this.newRpdList = newRpdList;
    }
    
    public List<RepayScheduleDetail> getNewRsdList() {
        return this.newRsdList;
    }
    
    public void setNewRsdList(final List<RepayScheduleDetail> newRsdList) {
        this.newRsdList = newRsdList;
    }
    
    public List<ReceiptAllocationDetail> getNewRadList() {
        return this.newRadList;
    }
    
    public void setNewRadList(final List<ReceiptAllocationDetail> newRadList) {
        this.newRadList = newRadList;
    }
    
    public static long getSerialversionuid() {
        return 1183720618731771888L;
    }
    
    public List<ReceiptAllocationDetail> getUpdRadList() {
        return this.updRadList;
    }
    
    public void setUpdRadList(final List<ReceiptAllocationDetail> updRadList) {
        this.updRadList = updRadList;
    }
    
    public List<FinRepayHeader> getUpdRphList() {
        return this.updRphList;
    }
    
    public void setUpdRphList(final List<FinRepayHeader> updRphList) {
        this.updRphList = updRphList;
    }
    
    public FinExcessAmount getUpdFea() {
        return this.updFea;
    }
    
    public void setUpdFea(final FinExcessAmount updFea) {
        this.updFea = updFea;
    }
    
    public List<FinRepayHeader> getNewRphList() {
        return this.newRphList;
    }
    
    public void setNewRphList(final List<FinRepayHeader> newRphList) {
        this.newRphList = newRphList;
    }
    
    public List<FinanceRepayments> getUpdRpdList() {
        return this.updRpdList;
    }
    
    public void setUpdRpdList(final List<FinanceRepayments> updRpdList) {
        this.updRpdList = updRpdList;
    }
    
    public List<FinReceiptDetail> getUpdRcdList() {
        return this.updRcdList;
    }
    
    public void setUpdRcdList(final List<FinReceiptDetail> updRcdList) {
        this.updRcdList = updRcdList;
    }
    
    public List<FinReceiptHeader> getUpdRchList() {
        return this.updRchList;
    }
    
    public void setUpdRchList(final List<FinReceiptHeader> updRchList) {
        this.updRchList = updRchList;
    }
    
    public List<RepayScheduleDetail> getUpdRsdList() {
        return this.updRsdList;
    }
    
    public void setUpdRsdList(final List<RepayScheduleDetail> updRsdList) {
        this.updRsdList = updRsdList;
    }
    
    public List<RepayScheduleDetail> getDltRsdList() {
        return this.dltRsdList;
    }
    
    public void setDltRsdList(final List<RepayScheduleDetail> dltRsdList) {
        this.dltRsdList = dltRsdList;
    }
    
    public List<FinanceScheduleDetail> getUpdFsdList() {
        return this.updFsdList;
    }
    
    public void setUpdFsdList(final List<FinanceScheduleDetail> updFsdList) {
        this.updFsdList = updFsdList;
    }
    
    public List<PresentmentDetail> getUpdPmdList() {
        return this.updPmdList;
    }
    
    public void setUpdPmdList(final List<PresentmentDetail> updPmdList) {
        this.updPmdList = updPmdList;
    }
    
    public FinExcessAmount getDltFea() {
        return this.dltFea;
    }
    
    public void setDltFea(final FinExcessAmount dltFea) {
        this.dltFea = dltFea;
    }
}