package com.pennant.util; 
  
import java.util.Comparator;

import org.zkoss.zul.GroupsModelArray;
  
@SuppressWarnings({ "rawtypes", "serial" })
public class AdvancedGroupsModelArray extends GroupsModelArray { 
  
	@SuppressWarnings("unchecked")
	public AdvancedGroupsModelArray(Object[] data, Comparator cmpr) { 
        super(data, cmpr); 
  
    } 
  
	@SuppressWarnings("unchecked")
	public AdvancedGroupsModelArray(Object[] data, Comparator cmpr, int col) { 
        super(data, cmpr, col); 
    } 
  
    // Create GroupFoot Data 
    protected Object createGroupFoot(Object[] groupdata, int index, int col) { 
        // Return the sum number of each group 
        return groupdata.length; 
    } 
    // Create GroupHead Data - Need Column index 
    protected Object createGroupHead(Object[] groupdata, int index, int col) {       
        return new Object[]{groupdata[0], col}; 
    } 
}

