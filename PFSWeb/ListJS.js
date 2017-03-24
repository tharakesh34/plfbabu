function raiseErrors() {

}
function raiseValidation(str) {
	var myList = new Array();
	var pos = str.indexOf("=");
	while (pos != -1) {
		var key = str.substring(0, pos).trim();
		str = str.substring(pos + 1).trim();
		pos = str.indexOf(",");
		var val = str.substring(0, pos).trim();

		if (key == "DATEVALUE") {
			if (val == "") {
				myList.push("DATEVALUE:Information Must be filled out");
			} else if (!(/\d{4}\/\d{2}\/\d{2}\s+\d{2}:\d{2}/.test(val))) {
				myList.push("DATEVALUE:Waring = Allow this format only "
						+ " Tue March 13 16:00:00 EDT 2012 ");
			} else if (val == "") {
				myList.push("DATEVALUE:Error");
			}
		}
		if (key == "MULTILINE") {
			if (val == "") {
				myList.push("MULTILINE:MULTILINE must be filled out");
			} else if (!(/^\s\S+$/.test(val))) {
				myList.push("MULTILINE:Allow multipl lines only ");
			} else
				myList.push("MULTILINE:true ");
		}
		if (key == "TIMEVALUE") {
			if (val == "") {
				myList.push("TIMEVALUE:TIMEVALUE must be filled out");
			} else if (!(/^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$/.test(val))) {
				myList.push("TIMEVALUE:Allow Time format 01:59:59 only ");
			} else
				myList.push("TIMEVALUE:true ");
		}
		if (key == "STATICCOMBO") {
			if (val == "") {
				myList.push("STATICCOMBO:STATICCOMBO must be filled out");
			} else if (!(/^[a-zA-Z0-9]+$/.test(val))) {
				myList.push("STATICCOMBO:Allow alphabets ");
			} else
				myList.push("STATICCOMBO:true) ");

		}
		str = str.substring(pos + 1).trim();
		pos = str.indexOf("=");

		
	}
	return myList.toString();
}