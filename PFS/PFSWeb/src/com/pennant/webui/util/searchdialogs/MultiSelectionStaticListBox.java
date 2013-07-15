/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  MultiSelectionStaticListBox.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  19-07-2011    
 *                                                                  
 * Modified Date    :  19-07-2011    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                         * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/

package com.pennant.webui.util.searchdialogs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Center;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.North;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Separator;
import org.zkoss.zul.South;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

import com.pennant.backend.model.ModuleListcode;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.service.PagedListService;
import com.pennant.util.PennantLOVListUtil;


/**
 * This class creates a modal window as a dialog in which the user <br>
 * can search and select a branch object. By onClosing this box <b>returns</b>
 * an object or null. <br>
 * The object can returned by selecting and clicking the OK button or by
 * DoubleClicking on an item from the list.<br>
 * Further the count of results can limited by manipulating the value of a table
 * field for the sql where clause.<br>
 */
public class MultiSelectionStaticListBox extends Window implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(MultiSelectionStaticListBox.class);
	private Paging _paging;
	private int pageSize = 10;
	private Listbox listbox;
	private ListModelList listModelList;
	private final int _height = 410;
	private int _width = 300;

	private Object objClass = null;
	private transient PagedListService pagedListService;
	private String selectedValues=null;
	private Map<String,String> checkMap=new HashMap<String,String>();
	private ArrayList<ValueLabel>  arrayList;
	private ModuleListcode moduleListcode;
	
	
	public MultiSelectionStaticListBox() {
		super();
	}

	/**
	 * The Call method.
	 * 
	 * @param parent
	 *            The parent component
	 * @return a BeanObject from the listBox or null.
	 */
	
	public static Object show(Component parent,String listCode,String selectedValues) {
		return new MultiSelectionStaticListBox(parent,listCode,selectedValues).getObject();
	}	
	

	/**
	 * Private Constructor. So it can only be created with the static show()
	 * method.<br>
	 * 
	 * @param parent
	 */
	private MultiSelectionStaticListBox(Component parent,String listCode,String selectedValues) {
		super();
		this.selectedValues=selectedValues;
		setList(listCode);
		setCheckMap();
		setParent(parent);
		createBox();
	}

	/**
	 * Creates the components, sets the model and show the window as modal.<br>
	 */
	private void createBox() {
		logger.debug("Entering Method createBox()"); 
		// Window
		this.setWidth(String.valueOf(this._width+5) + "px");
		this.setHeight(String.valueOf(this._height) + "px");
		this.setVisible(true);
		this.setClosable(true);
		this.setTitle(moduleListcode.getModuleListName());
		
		// Borderlayout
		final Borderlayout bl = new Borderlayout();
		bl.setHeight("100%");
		bl.setWidth("100%");
		bl.setParent(this);

		final Center center = new Center();
		center.setBorder("none");
		center.setFlex(true);
		center.setParent(bl);

		// Borderlayout
		final Borderlayout bl2 = new Borderlayout();
		bl2.setHeight("100%");
		bl2.setWidth("100%");
		bl2.setParent(center);

		final North north2 = new North();
		north2.setBorder("none");
		north2.setHeight("26px");
		north2.setParent(bl2);

		// Paging
		this._paging = new Paging();
		this._paging.setDetailed(true);
		this._paging.addEventListener("onPaging", new OnPagingEventListener());
		this._paging.setPageSize(getPageSize());
		this._paging.setParent(north2);

		final Center center2 = new Center();
		center2.setBorder("none");
		center2.setFlex(true);
		center2.setParent(bl2);
		
		// Div Center area
		final Div divCenter2 = new Div();
		divCenter2.setWidth("100%");
		divCenter2.setHeight("100%");
		divCenter2.setParent(center2);

		// Listbox
		this.listbox = new Listbox();
		// listbox.setStyle("border: none;");
		this.listbox.setHeight("290px");
		this.listbox.setMultiple(true);
		this.listbox.setVisible(true);
		this.listbox.setParent(divCenter2);
		this.listbox.setItemRenderer(new SearchBoxItemRenderer());

		final Listhead listhead = new Listhead();
		listhead.setParent(this.listbox);

		final Listheader listheaderCode = new Listheader();
		listheaderCode.setSclass("BCMListHeaderCode");
		listheaderCode.setParent(listhead);
		listheaderCode.setLabel(moduleListcode.getFieldHeading()[0]);
		
		final Listheader listheaderDesc= new Listheader();
		listheaderDesc.setSclass("BCMListHeaderDesc");
		listheaderDesc.setParent(listhead);
		listheaderDesc.setLabel(moduleListcode.getFieldHeading()[1]);
		
		final South south2 = new South();
		south2.setBorder("none");
		south2.setHeight("26px");
		south2.setParent(bl2);

		// hbox for holding the Textbox + SearchButton
		final Hbox hbox = new Hbox();
		hbox.setPack("stretch");
		hbox.setStyle("padding-left: 5px");
		hbox.setWidth("100%");
		hbox.setHeight("27px");
		hbox.setParent(south2);

		final South south = new South();
		south.setBorder("none");
		south.setHeight("30px");
		south.setParent(bl);

		final Div divSouth = new Div();
		divSouth.setWidth("100%");
		divSouth.setHeight("100%");
		divSouth.setParent(south);

		final Separator sep = new Separator();
		sep.setBar(true);
		sep.setOrient("horizontal");
		sep.setParent(divSouth);

		// Button
		final Button btnOK = new Button();
		btnOK.setStyle("padding-left: 5px");
		btnOK.setLabel("OK");
		btnOK.addEventListener("onClick", new OnCloseListener());
		btnOK.setParent(divSouth);

		/**
		 * init the model.<br>
		 * The ResultObject is a helper class that holds the generic list and
		 * the totalRecord count as int value.
		 */
		
		arrayList = moduleListcode.getValueLabels();
		if(arrayList!=null){
			this._paging.setTotalSize(arrayList.size());
		}else{
			this._paging.setTotalSize(0);
		}

		setListModelList(new ListModelList(getListData(0)));
		this.listbox.setModel(getListModelList());
		
		try {
			doModal();
		} catch (final SuspendNotAllowedException e) {
			logger.fatal("", e);
			this.detach();
		} //Upgraded to ZK-6.5.1.1 Removed interrupted exception 	

		
		logger.debug("Leaving Method createBox()");
	}

	/**
	 * Inner ListItemRenderer class.<br>
	 */
	final class SearchBoxItemRenderer implements ListitemRenderer<ValueLabel> {
		//Upgraded to ZK-6.5.1.1 Added casting to String 	
		@Override
		public void render(Listitem item, ValueLabel valueLabel, int count) throws Exception {

			//ValueLabel valueLabel = (ValueLabel) data;
			
			Listcell lc = new Listcell();
			Checkbox cbCode = new Checkbox();
			cbCode.setLabel(valueLabel.getValue());
			cbCode.setValue(valueLabel.getValue());
			cbCode.setChecked(checkMap.containsKey(valueLabel.getValue()));
			cbCode.addEventListener("onCheck", new onCheckBoxCheked());
			lc.appendChild(cbCode);
			lc.setParent(item);

			lc = new Listcell(valueLabel.getLabel());
			lc.setParent(item);

			item.setAttribute("data", valueLabel);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onDoubleClicked");
		}
	}

	/**
	 * "onPaging" EventListener for the paging component. <br>
	 * <br>
	 * Calculates the next page by currentPage and pageSize values. <br>
	 * Calls the method for refreshing the data with the new rowStart and
	 * pageSize. <br>
	 */
	public final class OnPagingEventListener implements EventListener {
		@Override
		public void onEvent(Event event) throws Exception {

			final PagingEvent pe = (PagingEvent) event;
			final int pageNo = pe.getActivePage();
			final int start = pageNo * getPageSize();
			// refresh the list
			refreshModel(start);
		}
	}

	public final class onCheckBoxCheked implements EventListener{
		
		public void onEvent(Event event) throws Exception {
			
			Checkbox checkbox =  (Checkbox) event.getTarget();
			if (checkbox.isChecked()){
				//Upgraded to ZK-6.5.1.1 Added casting to String 	
				checkMap.put(checkbox.getValue().toString(), checkbox.getValue().toString());
			}else{
				checkMap.remove(checkbox.getValue());
			}
		}
		
	}
	
	void refreshModel(int start) {
		logger.debug("Entering Method refreshModel");
		
		// clear old data
		getListModelList().clear();
		if(arrayList!=null){
			this._paging.setTotalSize(arrayList.size());
		}else{
			this._paging.setTotalSize(0);
		}
		// set the model
		setListModelList(new ListModelList(getListData(start)));
		this.listbox.setModel(getListModelList());

		logger.debug("Leaving Method refreshModel");
	}

	/**
	 * Inner OnCloseListener class.<br>
	 */
	final class OnCloseListener implements EventListener {
		@Override
		public void onEvent(Event event) throws Exception {

			String returnValues=null;
			Object[] object= checkMap.values().toArray();
			for (int i = 0; i < object.length; i++) {
				if (returnValues==null){
					returnValues=object[i].toString();
				}else{
					returnValues=returnValues+","+object[i].toString();
				}
			}
			
			setObject(returnValues);
			onClose();
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public Object getObject() {
		return this.objClass;
	}

	private void setObject(Object objClass) {
		this.objClass = objClass;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setListModelList(ListModelList listModelList) {
		this.listModelList = listModelList;
	}

	public ListModelList getListModelList() {
		return this.listModelList;
	}

	public PagedListService getPagedListService() {
		if (this.pagedListService==null){
			this.pagedListService= (PagedListService) SpringUtil.getBean("pagedListService");	
		}
		return pagedListService;
	}
	
	public void setCheckMap() {
	
		if (!StringUtils.trimToEmpty(selectedValues).equals("")){
			String[] checkedValues= selectedValues.split(",");
			for (int i = 0; i < checkedValues.length; i++) {
				this.checkMap.put(checkedValues[i], checkedValues[i]);
			}
		}
	}

	public Collection <ValueLabel>   getListData(int start) {
		Collection <ValueLabel>  list = new ArrayList<ValueLabel>();
		
		if (arrayList!=null){
			for (int i = start; i < arrayList.size(); i++) {
				list.add(arrayList.get(i));
				if(list.size()==getPageSize()){
					break;
				}
			}
		}  
		return list;
	}

	public void setList(String listCode){
		this.moduleListcode = PennantLOVListUtil.getModuleMap(listCode);
		System.out.println(moduleListcode);
	}
	
}
