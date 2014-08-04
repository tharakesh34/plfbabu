<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
/**-----------Login Dailog Button Images--------------**/
.loginUser {
	background:#FFFFFF url(${c:encodeURL('/images/icons/user1.png')}) no-repeat 4px 4px;
	padding:4px 4px 4px 22px;
	border:1px solid #CCCCCC;
	width:10px;
	height:18px;
}.loginUser:focus{
	background: url(${c:encodeURL('/images/icons/user1.png')}) no-repeat 4px 4px;
}

.loginPassword {
	background:#FFFFFF url(${c:encodeURL('/images/icons/Old/lockedstate.gif')}) no-repeat 4px 4px;
	padding:4px 4px 4px 22px;
	border:1px solid #CCCCCC;
	width:10px;
	height:18px;
}.loginPassword:focus{
	background: url(${c:encodeURL('/images/icons/Old/lockedstate.gif')}) no-repeat 4px 4px;
}

/**       New Changes in CSS       */
.z-toolbar{
	background:#EBEBEB;
	padding:0px;
	padding-top:2px;
}
.z-toolbar-list{
	background:#EBEBEB;
}
.z-panel-tl, .z-panel-tr, .z-panel-tl-gray, .z-panel-tr-gray, .z-panel-bl, .z-panel-br {
  background: url("/PFSWeb/zkau/web/fce7d4bb/zul/img/wnd/panel-corner.png") no-repeat scroll 0 top transparent;
  font-size: 0;
  height: 0;
  line-height: 0;
  margin-right: 7px;
  border:none;
}
.z-panel-hm .z-panel-header, .z-panel-header {
   	background-image: none ;
 	background-color: #EBEBEB;
    color: #363636;
  	font-family: Verdana,Tahoma,Arial,Helvetica,sans-serif;
  	font-size: 12px;
  	font-weight: bold;
  	padding: 3px 0 7px;
}
.z-panel-hl {
  	background-image: none ;
  	background-color: #EBEBEB;
  	border: 1px solid #C5C5C5;
  	padding-left: 7px;
}

.z-panel-hr {
  background-image: none;
  background-color: #EBEBEB;
  padding-right: 7px;
}

 .bigbutton_normal {
    font-size: 12px;
    font-weight: normal;
    border-radius: 2px 2px 2px 2px;
    color: #FFFFFF;
    cursor: pointer;
    font-weight: bold;
    overflow: visible;
    padding: 2px 10px;
    width: auto;
  	border: none;
	background: #ff6600;
}
 

.button_search {
	padding: 3px 13px 4px 4px;
	border: 1px solid #636363;
	-webkit-border-radius: 3px;
	-moz-border-radius: 3px;
	border-radius: 4px;
	margin-right: 4px;
 	background: #5A87B5 url(${c:encodeURL("/images/icons/search 2.png")}) no-repeat 4px 4px ;
  	margin: 0;
  	width : 24px;
  	height : 24px;
  	overflow: hidden;
   	text-align: center;
  	vertical-align: middle;
  	white-space: nowrap;	
}
 
.button_print {
	padding: 3px 13px 4px 4px;
	border: 1px solid #636363;
	-webkit-border-radius: 3px;
	-moz-border-radius: 3px;
	border-radius: 4px;
	margin-right: 4px;
 	background: #5A87B5 url(${c:encodeURL("/images/icons/print2.png")}) no-repeat 4px 4px ;
  	margin: 0;
  	width : 24px;
  	height : 24px;
  	overflow: hidden;
   	text-align: center;
  	vertical-align: middle;
  	white-space: nowrap;	
}

.button_NewDialog {
	padding: 3px 13px 4px 4px;
	border: 1px solid #636363;
	-webkit-border-radius: 3px;
	-moz-border-radius: 3px;
	border-radius: 4px;
	margin-right: 4px;
 	background: #5A87B5 url(${c:encodeURL("/images/icons/add-window.png")}) no-repeat 4px 4px ;
  	margin: 0;
  	width : 24px;
  	height : 24px;
  	overflow: hidden;
   	text-align: center;
  	vertical-align: middle;
  	white-space: nowrap;	
}

.button_refresh {
 	padding: 3px 13px 4px 4px;
	border: 1px solid #636363;
	-webkit-border-radius: 3px;
	-moz-border-radius: 3px;
	border-radius: 4px;
	margin-right: 4px;
 	background: #5A87B5 url(${c:encodeURL("/images/icons/reload2.png")}) no-repeat 4px 4px ;
  	margin: 0;
  	width : 24px;
  	height : 24px;
  	overflow: hidden;
   	text-align: center;
  	vertical-align: middle;
  	white-space: nowrap;	
  }

.button_help {
   	padding: 3px 13px 4px 4px;
	border: 1px solid #636363;
	-webkit-border-radius: 3px;
	-moz-border-radius: 3px;
	border-radius: 4px;
	margin-right: 4px;
 	background: #5A87B5 url(${c:encodeURL("/images/icons/baloon-question.png")}) no-repeat 4px 4px ;
  	margin: 0;
  	width : 24px;
  	height : 24px;
  	overflow: hidden;
   	text-align: center;
  	vertical-align: middle;
  	white-space: nowrap;	
  }

/** SCLASS for the User Bar labels **/
.userBarLabel {
	font-size:15px !important;
	font-weight:bold !important;
}

/** SCLASS for the User Bar label texts **/
.userBarLabelText {
	font-size:15px !important;
	color: blue;
}

td.z-listcell {
    -moz-border-bottom-colors: none;
    -moz-border-left-colors: none;
    -moz-border-right-colors: none;
    -moz-border-top-colors: none;
    border-color: transparent transparent #E1E1E1 #FFFFFF;
    border-image: none;
    border-style: solid;
    border-width: 1px;
}
		
.z-label, .z-radio-cnt, .z-checkbox-cnt, .z-loading {
    color: #3a5976;
    font-family: 'PT Sans', Verdana,Tahoma,Arial,Helvetica,sans-serif;;
    font-size: 14px;
    font-weight: normal;
}

/** Change the styles for content in list cell **/
div.z-listfooter-cnt, div.z-listcell-cnt, div.z-listheader-cnt {
	 border: 0 none;
    color: #3a5976;
    font-family: 'PT Sans', Verdana,Tahoma,Arial,Helvetica,sans-serif;;
   
    font-weight: normal;
    margin: 0;
    padding: 0;
}

tr.z-listbox-odd {
    background: none repeat scroll 0 0 #FFFFFF;
}

/** ********************************** TREE ****************************** **/ 

 tr.z-treerow-over > td.mainItem   {
	background: none repeat scroll 0 0 #365B85
}
 
/** Change the styles for Menu Item Over a Selected Menu  **/
tr.z-treerow-over-seld > td.mainItem   {
	background: none repeat scroll 0 0 #365B85;
}

/** Change the styles for Selected Menu **/
tr.z-treerow-seld > td.mainItem {
	background: none repeat scroll 0 0 #365B85;
}
	  
div.z-tree-body td.mainItem.z-treecell,
div.z-tree-footer td.mainItem.z-treefooter {
    border: 1px solid #5A87B5;
    cursor: pointer;
    font-size: 12px;
    font-weight: normal;
    overflow: hidden;
    padding: 5px 0px 5px 0px;
}

td.mainItem.z-treecell {
    background-color: #4B6D93;
    border-color: transparent transparent transparent #FFFFFF;
}
 

/** Change the styles for content in tree cell **/
td.mainItem div.z-treefooter-cnt, 
td.mainItem div.z-treecell-cnt,
td.mainItem div.z-treecol-cnt {
    border: 0 none;
    color: #FFFFFF;
    font-family: 'PT Sans', Verdana,Tahoma,Arial,Helvetica,sans-serif;
    font-size: 13px;
    margin: 0;
}

div.z-tree-body td.mainItem.z-treecell,
div.z-tree-footer td.mainItem.z-treefooter {
    cursor: pointer;
    font-size: 14px;
    font-weight: normal;
    overflow: hidden;
    padding: 4px 2px 4px 2px;
}	  



/************* Subitem ***************************/
tr.z-treerow-over > td.subItem  {
	background: none repeat scroll 0 0 #EDEDED
}
 
/** Change the styles for Menu Item Over a Selected Menu  **/
tr.z-treerow-over-seld > td.subItem   {
	background: none repeat scroll 0 0 #EDEDED;
}

/** Change the styles for Selected Menu **/
tr.z-treerow-seld > td.subItem  {
	background: none repeat scroll 0 0 #EDEDED;
}
	  
div.z-tree-body td.subItem.z-treecell,
div.z-tree-footer td.subItem.z-treefooter {
    border: 1px solid #FCFCFC;
    cursor: pointer;
    font-size: 12px;
    font-weight: normal;
    overflow: hidden;
    padding: 5px 0px 5px 0px;
}

td.subItem.z-treecell {
    background-color: #F5F5F5;
    border-color: transparent transparent transparent #FFFFFF;
}
 

/** Change the styles for content in tree cell **/
td.subItem div.z-treefooter-cnt, 
td.subItem div.z-treecell-cnt,
td.subItem div.z-treecol-cnt {
    border: 0 none;
    color: #333333;
    font-family: 'PT Sans', Verdana,Tahoma,Arial,Helvetica,sans-serif;
    font-size: 13px;
    margin: 0;
}

div.z-tree-body td.subItem.z-treecell,
div.z-tree-footer td.subItem.z-treefooter {
    cursor: pointer;
    font-size: 14px;
    font-weight: normal;
    overflow: hidden;
    padding: 4px 2px 4px 2px;
}	  
 
 span.z-tree-root-close, span.z-tree-tee-close, span.z-tree-last-close {
		background-image: url(${c:encodeURL("/images/icons/arrow-circle-right.png")});
			background-position:0 0;
		
}

span.z-tree-root-open {
			background-image: url(${c:encodeURL("/images/icons/arrow-circle-down.png")});
			background-position:0 0;
}
 .z-treerow-ico-over span.z-tree-root-close {
    background-position: 0 0;
}

.z-treerow-ico-over span.z-tree-root-open {
    background-position: 0 0;
}

a.z-tree-ico, span.z-tree-line, span.checkmark-spacer {
  display: inline-block;
  height: 18px;
  vertical-align: top;
  width: 8px;
}

/** Change the styles for toolbar **/

/* ------------------- GROUPBOX -------------------- */

/* Title in bold letters */
.z-groupbox-hl .z-groupbox-header {
	color: #000000;
	font-weight: bold;
}

.z-groupbox .z-caption {
    color: #FF6600;
    cursor: pointer;
}
/** SCLASS to change the Group box content border to None */ 
.groupbox-cnt-noborder {
    border: none;
}

/** Change the styles for Groupbox Header **/
.z-groupbox-3d-hl .z-groupbox-3d-header {
	padding:2px 5px 2px 5px;
	background: #EEEEE0; 
}

.z-groupbox-3d-hl .z-groupbox-3d-header {
  color: #000000;
  font-family: Verdana,Tahoma,Arial,Helvetica,sans-serif;
  font-size: 12px;
  font-weight: bold;
}

/** Change the styles for List Header **/
div.z-listbox-header th.z-listheader-sort, .z-listheader .z-listheader-cnt {
 	background : #EEEEE0;
}
 
/** Change the styles for content in List Header **/
div.z-listbox-header th.z-listheader-sort div.z-listheader-cnt, .z-listheader .z-listheader-cnt   {
 	cursor: pointer;
 	font-size: 12px;
	font-weight: bold;
	color:#3a5976;
	padding-bottom: 5px;
	padding-left: 5px;
	padding-right: 5px;
	padding-top: 5px;
}

/** Change the styles for content in List Cell **/
.z-listcell {
	height: 20px;
	color:black;
}

/** Change the styles for Labels **/
z-label, .z-radio-cnt, .z-checkbox-cnt, .z-loading {
    font-family: 'PT Sans', Verdana,Tahoma,Arial,Helvetica,sans-serif;;
    font-size: 14px;
    font-weight: normal;
    color: #666666
}

/** TEST STARTED
.z-tab-close, .z-tab-ver-close {
	background-image: url(${c:encodeURL('~./images/Pennant/close.png')});
    background-repeat: no-repeat;
    cursor: pointer;
    display: block;
    height: 16px;
    opacity: 0.8;
    position: absolute;
    right: 3px;
    top: 5px;
    width: 16px;
}
.z-tab-ver-close {
    left: 3px;
    top: 3px;
	background-image: url(${c:encodeURL('~./images/Pennant/close.png')});

}
.z-tab-close:hover, .z-tab-close-over, .z-tab-ver-close:hover, .z-tab-ver-close-over {
	background-image: url("~./images/Pennant/close.png");
 }**/

/** TEST STARTED**/

/** Change the styles for Selected List Item **/
tr.z-listitem-seld {
background:#F9F9F9; 
}

/** Change the styles for List Item Over **/
tr.z-listitem-over  {
background:#F5F5F5;
}

/** Change the styles for List Item Over Selected **/
tr.z-listitem-over-seld {
background:#F9F9F9;
}

tr.z-listgroup {
	background: #EBEBEB;
}

.z-listgroupfoot {
    background: none repeat-x scroll 0 0 #EBEBEB;
    height:0px;
}

td.z-listgroupfoot-inner div.z-listcell-cnt {
    color: #636363;
    font-family: Verdana,Tahoma,Arial,Helvetica,sans-serif;
    font-size: 12px;
    font-weight: normal;
    padding:0px
}

/*---Textbox , Decimalbox, Intbox, Longbox, Combobox ------*/

.z-textbox {
	background-color: #FFFFFF;
	border-style: solid;
	border-width: 1px;
	border-color: #A3A3A3;
	color: #000000;
}

.z-textbox.z-textbox-disd {
	border-style: solid;
	border-width: 1px;
	border-color: #A3A3A3;
	background-color: #F2F2F2;
	color: #000000;
}

.z-textbox.z-textbox-readonly {
	border-style: solid;
	border-width: 1px;
	border-color: #A3A3A3;
	background-color: #F2F2F2;
	color: #000000;
}

.z-textbox.z-textbox-focus {
	background-color:#FFF7F2;
 	border-style: solid;
	border-width: 1px;
	border-color: #4B4B4B;
	color: #000000;
}

.z-textbox {
	background-color: #FFFFFF;
	border-style: solid;
	border-width: 1px;
	border-color: #A3A3A3;
	width: 200px;
	color: #000000;
}

.z-decimalbox,.z-intbox,.z-longbox,.z-doublebox {
	background-color: #FFFFFF;
	border-style: solid;
	border-width: 1px;
	border-color: #A3A3A3;
	height: 15px;
	width: 200px;
	color: #000000;
	text-align:right;
}

.z-textbox-focus,.z-textbox-focus input,.z-decimalbox-focus,.z-decimalbox-focus input,.z-intbox-focus,.z-intbox-focus input,.z-longbox-focus,.z-longbox-focus input,.z-doublebox-focus,.z-doublebox-focus input
	{
	-moz-border-bottom-colors: 1px solid #4B4B4B;
	-moz-border-image: none;
	-moz-border-left-colors: 1px solid #4B4B4B;
	-moz-border-right-colors: 1px solid #4B4B4B;
	-moz-border-top-colors: 1px solid #4B4B4B;
	background: none repeat-x scroll 0 0 #FFF7F2;
	border-color: #4B4B4B;
	border-right: 1px solid #4B4B4B;
	border-style: solid;
	border-width: 1px;
}

.z-bandbox.z-bandbox-focus .z-bandbox-inp {
	background-color: #FFF7F2;
	border-style: solid;
	border-width: 1px;
	border-color: #4B4B4B;
	color: #000000;
}

.z-bandbox.z-bandbox-disd .z-bandbox-inp {
	border-style: solid;
	border-width: 1px;
	border-color: #A3A3A3;
	background-color: #F2F2F2;
	color: #000000;
}
.z-bandbox .z-bandbox-inp {
	background-color: #FFFFFF;
	border-style: solid;
	border-width: 1px;
	border-color: #A3A3A3;
	color: #000000;
}

.z-timebox .z-timebox-inp {
	background-color: #FFFFFF;
	border-style: solid;
	border-width: 1px;
	border-color: #A3A3A3;
	color: #000000;
}

.z-timebox.z-timebox-focus .z-timebox-inp {
	background-color: #FFF7F2;
	border-style: solid;
	border-width: 1px;
	border-color: #4B4B4B;
	color: #000000;
}

.z-timebox .z-timebox-readonly.z-timebox-inp {
	border-style: solid;
	border-width: 1px;
	border-color: #A3A3A3;
	background-color: #F2F2F2;
	color: #000000;
}

.z-timebox.z-timebox-disd .z-timebox-inp {
	border-style: solid;
	border-width: 1px;
	border-color: #A3A3A3;
	background-color: #F2F2F2;
	color: #000000;
}

/*---------------------- Datebox -------------------*/

 .z-datebox .z-datebox-inp{
 	background-color : #FFFFFF; 
	border-style : solid; 
	border-width : 1px; 
	border-color : #A3A3A3; 
	color: #000000;
 }

.z-datebox.z-datebox-focus .z-datebox-inp{
	background-color : #FFF7F2; 
	border-style : solid; 
	border-width : 1px; 
	border-color : #4B4B4B;
	color: #000000;
 }

.z-datebox .z-datebox-readonly.z-datebox-inp{
	border-style : solid; 
	border-width : 1px; 
	border-color : #A3A3A3; 
	background-color : #F2F2F2; 
	color: #000000;
 }

.z-datebox.z-datebox-disd .z-datebox-inp{
	border-style : solid; 
	border-width : 1px; 
	border-color : #A3A3A3; 
	background-color : #F2F2F2;
	color: #000000; 
 } 
 
/** ***********************************  Window  ************************************* **/
.z-window-modal-shadow,.z-window-overlapped-shadow,.z-window-popup-shadow,.z-window-embedded-shadow,.z-window-highlighted-shadow
	{
	-moz-border-radius-bottomleft: 1px;
	-moz-border-radius-bottomright: 1px;
	-moz-border-radius-topleft: 1px;
	-moz-border-radius-topright: 1px;
	-moz-box-shadow: 0 0 3px rgba(0, 0, 0, 0.5);
}

.z-window-highlighted-br,.z-window-overlapped-br {
	-moz-background-clip: border;
	-moz-background-origin: padding;
	-moz-background-size: auto auto;
	background-attachment: scroll;
	background-color: #EBEBEB;
	background-image: none;
	background-position: right bottom;
	background-repeat: no-repeat;
	font-size: 0;
	height: 5px;
	line-height: 0;
	margin-right: -5px;
}

.z-window-highlighted-cl,.z-window-overlapped-cl {
	-moz-background-clip: border;
	-moz-background-origin: padding;
	-moz-background-size: auto auto;
	background-attachment: scroll;
	background-color: #EBEBEB;
	background-image: none;
	background-position: 0 0;
	background-repeat: repeat-y;
	padding-left: 6px;
}
.z-window-modal-br{
	height: 0px;

}
.z-window-modal-cl{
	background-image:none;
	background-color: #FFFFFF;
} 
.z-window-modal-cr{
	background-image:none;
	background-color: #FFFFFF;
}

.z-window-modal-bl, .z-window-highlighted-bl, .z-window-overlapped-bl {
    background-position: 0 0px;
    height: 0px;
    margin-right: 0px;
}
.z-window-modal-br, .z-window-highlighted-br, .z-window-overlapped-br {
    height: 0px;
    margin-right: 0px;
    position: relative;
}
.z-window-modal-hl{
  	background: none no-repeat scroll 0 0 #FFFFFF;
  	padding-left: 6px;
}
.z-window-modal-hr{
  background: none no-repeat scroll 0 0 #FFFFFF;
  padding-left: 6px;
}

.z-window-modal-header{
	back
}

.z-window-highlighted-cr,.z-window-overlapped-cr {
	-moz-background-clip: border;
	-moz-background-origin: padding;
	-moz-background-size: auto auto;
	background-attachment: scroll;
	background-color: #EBEBEB;
	background-image: none;
	background-position: right 0;
	background-repeat: repeat-y;
	padding-right: 6px;
}

.z-window-embedded-cnt {
    background: none repeat scroll 0 0 white;
    border: none;
    margin: none;
    padding: 0px;
}

.z-window-embedded-tr, .z-window-modal-tr, .z-window-highlighted-tr, .z-window-overlapped-tr, .z-window-popup-tr {
  height: 0px;
}
.z-window-embedded-tl, .z-window-modal-tl, .z-window-highlighted-tl, .z-window-overlapped-tl, .z-window-popup-tl {
  height: 0px;
}

.z-window-embedded-hl, .z-window-modal-hl, .z-window-highlighted-hl, .z-window-overlapped-hl, .z-window-popup-hl {
  background: none no-repeat scroll 0 0 #EBEBEB;
  padding-left: 6px;
}

.z-paging-inp,.z-paging input.z-paging-inp {
	height: 16px;
}

.z-spinner-rounded-disd,.z-doublespinner-rounded-disd,.z-textbox.z-textbox-disd,.z-timebox-rounded-disd,.z-datebox-rounded-disd,.z-bandbox-rounded-disd,.z-combobox-rounded-disd,.z-spinner-disd,.z-doublespinner-disd,.z-timebox-disd,.z-datebox-disd,.z-bandbox-disd,.z-datebox.z-datebox-disd .z-datebox-inp,.z-combobox.z-combobox-disd .z-combobox-inp,.z-decimalbox.z-decimalbox-disd,.z-intbox.z-intbox-disd,.z-longbox.z-longbox-disd
	{
	opacity: 1.0;
	color: #000000 !important;
}

div.z-listfooter-cnt,div.z-listcell-cnt,div.z-listheader-cnt {
	padding-left: 5px;
}

/*-----------------------treerow--------------------*/
div.z-tree {
	background: transparent;
	border:none;
}

/*----------------------- list box --------------*/
 div.z-listbox-body .z-listcell {
	padding-bottom: 0px;
	padding-left: 5px;
	padding-right: 5px;
	padding-top: 0px;
}

div.z-listbox-body .defListcell.z-listcell,div.z-listbox-footer .defListfooter.z-listfooter
	{
	cursor: default;
	background-color: #FFFFFF;
	border-bottom: none;
	border-top: none;
}

/* ------------------- GRID -------------------- */
/* cut the vertical borders in the rows of the GRID */
tr.z-row td.z-row-inner {
	border-right: 0px #CCC;
}

/* Make Plain Grid+smaller comps Padding */
/* "overflow:hidden ;" not working in Chrome/Safari */
.GridLayoutNoBorder tr.z-row td.z-row-inner,tr.z-row,div.z-grid-body div.z-cell,div.z-grid
	{
	border: none;
	zoom: 1;
	background: white;
	border-top: none;
	border-left: none;
	border-right: none;
	border-bottom: none;
	padding: 0px;
	border:0px;
}

.z-grid-header-bg {
	background-image: url("");
    font-size: 0;
    height: 1px;
    margin-right: -11px;
    margin-top: -1px;
    position: relative;
    top: 0;
}

.z-paging-inp,.z-paging input.z-paging-inp {
	font-family: Verdana, Tahoma, Arial, Helvetica, sans-serif;
	font-size: 14px;
	height: 15px;
	line-height: 15px;
}

.labelError {
	color: red
}

.labelAppName {
	font-size: 200%;
	font-style: calibri;
	color: #C35617;
}

.labelMandatory {
	font-size: 14px;
	font-style: calibri;
	color: red;
	padding-left: 0px;
}

.fontSizeMedium {
	font-size: 15px;
}

/* body Bereich */
body {
	padding: 0 0;
	/* 0 padding on top and bottom and 0 padding on right and left */
}

/* ------------------- TOOLBAR -------------------- */
/* remove white strips between the toolbars */
.hboxRemoveWhiteStrips td.z-hbox-sep {
	width: 0;
	padding: 0;
}


/* ------------------- TEXTBOX -------------------- */
/* Textbox: Disabled / readonly background color */
.z-textbox-readonly,.z-textbox-text-disd {
	background: #ECEAE4;
}

/* ------------------- DECIMALBOX -------------------- */
/* Decimalbox: Disabled / readonly background color */
.z-decimalbox-readonly,.z-decimalbox-text-disd {
	background: #ECEAE4;
}

/* ------------------- DOUBLEBOX -------------------- */
/* Doublebox: Disabled / readonly background color */
.z-doublebox-readonly,.z-doublebox-text-disd {
	background: #ECEAE4;
}

/* ------------------- INTBOX -------------------- */
/* intbox: Disabled / readonly background color */
.z-intbox-readonly,.z-intbox-text-disd {
	background: #ECEAE4;
}

/* ------------------- DATEBOX -------------------- */
/* Disabled datebox should have black font */
.z-datebox {
	font: #000000;
}

/* ------------------- GRID -------------------- */
/* cut the vertical borders in the rows of the GRID */
tr.z-row td.z-row-inner {
	border-right: 0px #CCC;
}

/* Make Plain Grid+smaller comps Padding */
/* "overflow:hidden ;" not working in Chrome/Safari */
.GridLayoutNoBorder tr.z-row td.z-row-inner,tr.z-row,div.z-grid-body div.z-cell,div.z-grid {
	border: none;
	zoom: 1;
	background: white;
	border-top: none;
	border-left: none;
	border-right: none;
	border-bottom: none;
	padding: 1px;
}


.z-window-embedded-header,  .z-window-popup-header, .z-window-highlighted-header, .z-window-overlapped-header {
   background: #EEEEE0;
   color: #000000;
    padding-bottom: 6px;
    padding-top: 3px;
    font-weight:bold;
}
.z-window-popup-header,.z-window-highlighted-header,.z-window-overlapped-header {
	background-image: none;
 	font-size: 14px;
	font-weight: bold;
}
.z-window-embedded-cl, .z-window-embedded-cr {
	background-image: none;
    background-color: none;
}

.z-window-embedded-hm, .z-window-overlapped-hm, .z-window-popup-hm {
    border: 1px solid #CCCCCC;
    padding-left: 1px;
}
.z-window-embedded-tl, .z-window-embedded-tr, .z-window-embedded-bl, .z-window-embedded-br {
	background-image: none;
    background-color: none;
}

.z-window-embedded-tr,.z-window-modal-tr,.z-window-highlighted-tr,.z-window-overlapped-tr,.z-window-popup-tr {
	background-image: none;
	background-color: none;
}

.z-window-embedded-hl,.z-window-modal-hl,.z-window-highlighted-hl,.z-window-overlapped-hl,.z-window-popup-hl {
	background-image: none;
	background-color: none;
}

.z-window-embedded-hr,.z-window-modal-hr,.z-window-highlighted-hr,.z-window-overlapped-hr,.z-window-popup-hr {
	background-image: none;
	background-color: none;
}
.z-window-modal-header{
	background-color:#FFFFFF;
	color:#000000;
	font-weight:bold;
}
.z-window-modal-hl{
	background-color:#FFFFFF;
}


/* ------------------- Loading -------------------- */
.z-loading {
	background-color: #808080;
	border: 1px outset #A0A0A0;
	font-weight: bold;
	padding: 2px;
}

.z-loading-indicator {
	color: gray;
	border: 0 none;
}

.z-apply-loading-icon,.z-loading-icon,.z-renderdefer {
	background-image: url("/PFSWeb/zkau/web/57f5af2c/zk/img/progress3.gif");
}

/* ------------------- Mandatory Field color -------------------- */

 

.ToUpeerCase {
	text-transform: uppercase;
}


.z-separator-ver.mandatory,.z-separator-ver-bar.mandatory {
	width: 2px; /*PRO*/
	height: 18px; /*PRO*/
	background-color: #FF0000; /*PRO*/
	text-align: : right;
}

/* ------------------- Calendar -------------------- */

.z-calendars-dd-rope {
	background-color: #EBEBEB;
}

.z-calendars-inner {
	background-color: #CCCCCC
}

.z-calendar-caldayrow .z-calendar-over,.z-calendar-title-over .z-calendar-ctrler
	{
	color: #EBEBEB;
}

.z-calendar-calyear .z-calendar-over,.z-calendar-calmon .z-calendar-over,.z-calendar-caldayrow .z-calendar-over
	{
	background-color: #EBEBEB;
}

.z-calendar-calyear td.z-calendar-seld,.z-calendar-calmon td.z-calendar-seld,.z-calendar-calday td.z-calendar-seld
	{
	background-color: #EBEBEB;
}

.z-calendars-month-date {
	background-color: #EBEBEB;
}

.z-calendars-week-cnt {
	background-color: #EBEBEB;
}

.z-calendars-month-header {
	background-color: #CCCCCC;
}

.z-calendars-week-cnt .z-calendars-timezone {
	background-color: #EBEBEB;
}

.z-calendars-daylong-body {
	background-color: #EBEBEB;
}

tr.z-row-over>td.z-row-inner {
	border-bottom: 1px solid #E3F2FF;
	border-top: 1px solid #E3F2FF;
}

tr.z-row-over>td.z-row-inner,tr.z-row-over>.z-cell {
	background-image: none;
}

/*---------------------------- TAB DESIGNING ------------------------*/
.z-tabpanel, .z-tabbox-ver .z-tabpanels-ver {
    border-color: #E1E1E1;
}

.z-tabs .z-tabs-cnt {
    border-bottom: 0px thin #E1E1E1;
}
.listCellLabel{
	color:#FF6600;
}
.z-fieldset legend {
    color: #FF6600;
    font-family: 'PT Sans', Verdana,Tahoma,Arial,Helvetica,sans-serif;;
    font-size: 14px;
    font-weight: bold;
}
.z-paging {
    background: none repeat scroll 0 0 #F9F9F9;
    border-color: #CFCFCF;
    border-style: solid;
    border-width: 0 1px 1px 1px;
    display: block;
    padding: 2px;
    position: relative;
}

.z-paging-info, .z-paging div.z-paging-info {
    color: #363636;
    font-family: Arial;
    font-size: 14px;
    position: absolute;
    right: 8px;
    top: 8px;
}
/* --- Color codes for Finance Schedule details --- */ 
.color_Disbursement {
    background-color: #F87217;
} 
.color_ReviewRate { 
    background-color: #E6A9EC; 
}            
.color_GracePeriodendDate { 
	background-color: #726E6D; 
}
.color_LastScheduleRecord{ 
	background-color: #726E6D;
}
.color_Deferred { 
	background-color: #E0FFFF; 
}
.color_Repayment { 
	background-color: #008000; 
}
.color_EarlyRepayment { 
	background-color: #008000; 
}
.color_RepaymentOverdue { 
	background-color: #FF0000; 
}
/*- Image Blink-*/
@-webkit-keyframes blink {
    0% {
        opacity: 1;
    }
    50% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
@-moz-keyframes blink {
    0% {
        opacity: 1;
    }
    50% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}

.imgBlink{
    -webkit-animation: blink 1s;
    -webkit-animation-iteration-count: infinite;
    -moz-animation: blink 1s;
    -moz-animation-iteration-count: infinite;

}

.cssCmdBtn {
	background-color:#008F91;
	border-radius:2px;
	border: 1px solid transparent;
	display:inline-block;
	color:#ffffff;
	font-family:arial;
	font-size:11px;
	font-weight:bold;
	padding:3px 10px;
	text-decoration:none;
	text-transform: uppercase;
}.cssCmdBtn:hover {
	background-color:#61C7C9;
	cursor: pointer;	
}.cssCmdBtn:active {
	position:relative;
	top:1px;
}

/** Change the background of the right side top user bar */ 
.vbox_Userbar{
	padding:2px 5px 2px 5px;
	border-radius:2px;
	background: #808080; 

}
/** Change the attributes of the labels used in the right side top user bar */
.label_UserBar{
	color: #FFFFFF !important;;
	font-size:14px !important;
}
 
.menubar_userbar{
 	color: #FFFFFF;
 	border-radius:2px;
	background:#4F81BD; 
}

.menu_Userbar{
  padding:5px 2px 2px 5px;		
  color: #FFFFFF;
  font-family: 'PT Sans', Verdana,Tahoma,Arial,Helvetica,sans-serif;;
  font-size: 14px;
  font-weight: bold;
  white-space: nowrap;
}

											/****************  Tab Box and panels ****************/


.z-tab .z-tab-text {
    color: white;
    font-style: normal;
}
 .z-tab-hl, .z-tab-seld .z-tab-hl,.z-tab .z-tab-hl:hover { 
   	color: #ffffff;
   	border-right: 2px solid #ffffff;
   	cursor: pointer;
} 
 .z-tab-seld .z-tab-text {
    color: #ffffff;
    cursor: default;
    font-weight: normal;
    padding: 4px 1px 5px;
}
/** Change the styles for Tabs **/
 .z-tab-hl , .z-tab-hr , .z-tab-hm{
	height: 25px;
	
	border-top-left-radius: 2px;
	border-top-right-radius: 2px;
	background:  #365B85; 
}
/** Change the styles for Tab Hand Over **/
.z-tab .z-tab-hl:hover, .z-tab .z-tab-hl:hover .z-tab-hr, .z-tab .z-tab-hl:hover .z-tab-hm,.z-tab .z-tab-hl:hover .z-tab-text   {
    color: #ffffff;
    background: #365B85; 
	border-top-left-radius: 2px;
	border-top-right-radius: 2px;
}
 /** Change the styles for Selected Tabs **/
.z-tab-seld .z-tab-hl , .z-tab-seld .z-tab-hr , .z-tab-seld .z-tab-hm{
    color: #ffffff;
    height: 25px;
    background: #BC5C5C; 
	border-top-left-radius: 2px;
	border-top-right-radius: 2px;
}

/** Change the styles for Tab Selected Hand Over **/
.z-tab-seld .z-tab-hl:hover, .z-tab-seld .z-tab-hl:hover .z-tab-hr, .z-tab-seld .z-tab-hl:hover .z-tab-hm,.z-tab-seld .z-tab-hl:hover .z-tab-text   {
    color: #ffffff;
    background: #BC5C5C; 
	border-top-left-radius: 2px;
	border-top-right-radius: 2px;
}



											/************** Command Button Styles ***********/		
/**  Command button and Hover **/
.z-button-login {
	background-color:#00C896;
	border-radius:2px;
	border: 1px solid transparent;
	display:inline-block;
	color:#ffffff;
	font-family:arial;
	font-size:11px;
	font-weight:bold;
	padding:3px 10px;
	text-decoration:none;
	text-transform: uppercase;
}.z-button-login:hover {
	background-color:#79CCB0;
	cursor: pointer;
	color:#000000;	
}.z-button-login:active {
	position:relative;
	top:1px;
}
											

/**  Command button and Hover **/
.z-button-os {
	background-color:#00B050;
	border-radius:2px;
	border: 1px solid transparent;
	display:inline-block;
	color:#ffffff;
	font-family:arial;
	font-size:11px;
	font-weight:bold;
	padding:3px 10px;
	text-decoration:none;
	text-transform: uppercase;
}.z-button-os:hover {
	background-color:#2AC771;
	cursor: pointer;
	color:#000000;	
}.z-button-os:active {
	position:relative;
	top:1px;
}
/**  Command button Disabled **/
.z-button-os-disd{
	background-color:#AAAAAA;
	cursor: default;
}.z-button-os-disd:hover{
	background-color:#AAAAAA;
	color:#ffffff;
	cursor: default;
}
.cssHbox{
	border:1px solid rgb(163, 163, 163);
	border-radius: 2px 2px 2px 2px;
}
.cssLabel{
	width:14em;
	white-space:nowrap;
	overflow:hidden;
	text-overflow:ellipsis;
}

.cssBtnSearch {
	background: #00B050 url(${c:encodeURL("/images/icons/search.png")}) no-repeat center center;
	width:25px;
	border-radius:0px;
	display:inline-block;
	border: 1px solid transparent;
}.cssBtnSearch:hover {
	background: #2AC771 url(${c:encodeURL("/images/icons/search.png")}) no-repeat center center;
	cursor: pointer;
}.cssBtnSearch:active {
	position:relative;
	top:1px;
}
/**  Search button Disabled **/
.cssBtnSearch.z-button-os-disd{
	background-color:#AAAAAA;
	cursor: default;
}.z-button-os-disd:hover{
	background-color:#AAAAAA;
	color:#ffffff;
	cursor: default;
}

													/******** Combo Box Styles ***********/				
/** ======Combobox intial Load====**/
.z-combobox .z-combobox-inp {
	background-color: #FFFFFF;
	border-style: solid;
	border-width: 1px;
	border-color: #A3A3A3;
	height: 16px;
	color: #000000;
	padding-right:2px;
}

.z-combobox.z-combobox-focus .z-combobox-inp {
	background-color: #FFF7F2;
	border-style: solid;
	border-width: 1px;
	border-color: #4B4B4B;
	color: #000000;
}
.z-combobox.z-combobox-disd .z-combobox-inp {
	border-style: solid;
	border-width: 1px;
	border-color: #4B4B4B;
	background-color: #F2F2F2;
	color: #000000;
}
.z-combobox-rounded-pp, .z-bandbox-rounded-pp, .z-datebox-rounded-pp, .z-combobox-pp, .z-bandbox-pp, .z-datebox-pp {
    background: none repeat scroll 0 0 white;
    border: 1px solid #4B4B4B;
    display: block;
    font-size: 14px;
    padding: 2px;
    position: absolute;
}
.z-combobox-rounded-pp, .z-bandbox-rounded-pp, .z-combobox-pp, .z-bandbox-pp {
    font-family: 'PT Sans', Verdana,Tahoma,Arial,Helvetica,sans-serif;;
    overflow: auto;
}
.z-combobox-rounded-pp .z-comboitem-text, .z-combobox-rounded-pp .z-comboitem-btn, .z-combobox-pp .z-comboitem-text, .z-combobox-pp .z-comboitem-btn {
    cursor: pointer;
    font-size: 14px;
    white-space: nowrap;
}
.z-combobox-rounded-pp .z-comboitem-inner, .z-combobox-rounded-pp .z-comboitem-cnt, .z-combobox-pp .z-comboitem-inner, .z-combobox-pp .z-comboitem-cnt {
    color: #888888;
    font-size: 14px;
    padding-left: 6px;
}
.z-combobox-rounded-pp .z-comboitem, .z-combobox-rounded-pp .z-comboitem a, .z-combobox-rounded-pp .z-comboitem a:visited, .z-combobox-pp .z-comboitem, .z-combobox-pp .z-comboitem a, .z-combobox-pp .z-comboitem a:visited {
    color: black;
    font-size: 14px;
    font-weight: normal;
    text-decoration: none;
}

.z-combobox-rounded-pp .z-comboitem-seld, .z-combobox-pp .z-comboitem-seld {
    background: none repeat scroll 0 0 #2AC771;
    color: #FFFFFF;
    border: 1px solid #FFFFFF;
}
.z-combobox-rounded-pp .z-comboitem-over, .z-combobox-pp .z-comboitem-over {
    background: none repeat scroll 0 0 #2AC771;
}
.z-combobox-rounded-pp .z-comboitem-over-seld, .z-combobox-pp .z-comboitem-over-seld {
    background: none repeat scroll 0 0 #2AC771;
}
.z-comboitem .z-comboitem-text {
    font-size: 14px;
}

/** Combobox button and Hover**/
.z-combobox .z-combobox-btn{
	background: #00B050 url(${c:encodeURL("/images/icons/arrow-circle-down.png")}) no-repeat center center;
	vertical-align: top;
	width:20px;
	height:21px;
	border-image: none;
	overflow: hidden;
	display: inline-block;
}.z-combobox .z-combobox-btn:hover{
	background: #2AC771 url(${c:encodeURL("/images/icons/arrow-circle-down.png")}) no-repeat center center;
	border-image: none;
	overflow: hidden;
	display: inline-block;
}

/** combobox button Disabled **/
.z-combobox-disd.z-combobox .z-combobox-btn{
	background: #AAAAAA url(${c:encodeURL("/images/icons/arrow-circle-down.png")}) no-repeat center center;
	vertical-align: top;
	width:20px;
	height:21px;
	border-image: none;
	overflow: hidden;
	display: inline-block;
}
										/** ------------ Date Box Styles ------------------ **/		
/** Datebox button and hover **/
.z-datebox .z-datebox-btn {
  	background: #00B050 url(${c:encodeURL("/images/icons/calendar.png")}) no-repeat center center;
	vertical-align: top;
	width:20px;
	height:20px;
	border-image: none;
	overflow: hidden;
	display: inline-block;
	border-radius: 0px 2px 2px 0px;
}.z-datebox .z-datebox-btn:hover{
  	background: #2AC771 url(${c:encodeURL("/images/icons/calendar.png")}) no-repeat center center;
	vertical-align: top;
	cursor: pointer;
	border-image: none;
	overflow: hidden;
	display: inline-block;
	border-radius: 0px 2px 2px 0px;
}	
/** Datebox button Disabled **/
.z-datebox-disd.z-datebox .z-datebox-btn {
	background: #AAAAAA url(${c:encodeURL("/images/icons/calendar.png")}) no-repeat center center;
	vertical-align: top;
	cursor: pointer;
	border-image: none;
	overflow: hidden;
	display: inline-block;
	border-radius: 0px 2px 2px 0px;
}		

.toolbar-start{
	float:left; 
	border-style: none; 
	padding-top:2px;
	padding-left:5px;
}
.toolbar-center{
 	border-style: none;
 	padding-top: 2px;
}
.toolbar-end{
	float: right; 
	border-style: none;
	padding-top: 2px;
	padding-right: 5px;
}	
.label-heading{
	font-Weight:bold;
	font-size:16px !important;
	color:black;
}
.label-status{
	font-Weight:bold;
	padding-left:50px;
}
.south-dialog{
	border:none;
	padding:5px;
	height:48px;
}
.gb-dialog{
	border:none;
	padding:5px;
}
.decimalToString{ 
	border:none; 
	background-color:white;
	font-weight:bold;
}
.listboxHeading{
	font-Weight:bold;
	color:#FF6600;
	font-size:12px !important;
	padding:2px 2px 2px 4px;
}
.listboxHeadingBlack{
	font-Weight:bold;
	color:#555;
	font-size:12px !important;
	padding:2px 2px 2px 4px;
}

.feeWaiver{
	border:1px;
	font-weight:bold;
	color:green;
	background-color:#FFDCDC;
	float:left;
}

.amtDisplayFormat{
	font-weight:bold;
	text-align:right;
}

.inlineMargin{
	margin-right:10px;
}


/** Change the styles for content in list cell **/
.highlighted_List_Cell {
 	border-color: #E1E1E1 #E1E1E1 #E1E1E1 #E1E1E1 !important;
 	 border-width: 1px;
     color: #3a5976;
    font-family: 'PT Sans', Verdana,Tahoma,Arial,Helvetica,sans-serif;;
    background-color:#B7DEE8;
    font-weight: normal;
    margin: 0;
    padding: 0;
    text-align:right;
}

/** Change the styles for content in list cell **/
.highlighted_List_Cell_MainTotal {
 	border-color: #E1E1E1 #E1E1E1 #E1E1E1 #E1E1E1 !important;
 	 border-width: 1px;
     color: #3a5976;
    font-family: 'PT Sans', Verdana,Tahoma,Arial,Helvetica,sans-serif;;
    background-color:#92D050;
    font-weight: normal;
    margin: 0;
    padding: 0;
    text-align:right;
}
/** Clients.showNotification **/
.z-notification .z-notification-cl, 
.z-notification	.z-notification-cnt { 
	height: 60px; 
	width: 300px; 	 		
    font-family: Verdana,Tahoma,Arial,Helvetica,sans-serif;
    font-size: 12px;
    font-weight: normal;
    margin: 0 !important;
}
.z-notification-info .z-notification-cl {
	background-color: #4F81BD;
}
.z-notification-info .z-notification-pointer-r {
	border-left-color: #4F81BD;
}
.z-notification-info .z-notification-pointer-l {
	border-right-color: #4F81BD;
}
.z-notification-info .z-notification-pointer-d {
	border-upper-color: #4F81BD;
}
.z-notification-info .z-notification-pointer-u {
	border-down-color: #4F81BD;
}

.ckeditorReadonly{
	border:1px rgb(163, 163, 163) solid;
}

.button_Label {
	font-size: 9px;
	font-style: Verdana;
	color: #FFFFFF;
	font-weight:bold;
	padding: 5px 3px;
	border: 1px solid yellowgreen;
	background-color: green;
}
.button_Label_Disable {
	font-size: 9px;
	font-style: Verdana;
	color: #FFFFFF;
	font-weight:bold;
	padding: 5px 3px;
	border: 1px solid yellowgreen;
	background-color: grey;
	
}

.queryBuilder_Label {
	font-size: 12px;
	font-style: calibri;
	color: #000000;
	font-weight:bold;
	padding-left: 0px;
}

.javaScriptBuilderComboitemfont .z-combobox-rounded-inp,  .z-combobox-inp{
font-size:11px;
}

.javaScriptBuilderfont{
font-size:11px;
}
</style>