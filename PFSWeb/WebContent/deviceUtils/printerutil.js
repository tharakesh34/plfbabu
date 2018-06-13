PrinterUtil = (function() {
	'use strict';
	var obj = {};
	var componentId;
	var onSuccess;
	var printSocket = new WebSocket("ws://127.0.0.1:10010/print");
	obj.print = function(windowId, successCallback,data) {
		componentId = windowId;
		onSuccess = successCallback;
		var request = {
				"request" :data,
				"reqMethod" : "print"
		}
		if(printSocket.readyState == 1 ){
			printSocket.send(JSON.stringify(request));	
		}else{
			zAu
			.send(new zk.Event(zk.Widget.$('$'+componentId), 'onPrintError',
					"Printer Connection Doesnt Exists"));
			console.log("Connection error");
		}
		
	};
	obj.getPrinters = function() {
		var request = {
			"reqMethod" : "getPrinter",
			"request" : ""
		};
		printSocket.send(JSON.stringify(request));
	};
	printSocket.onmessage = function(message) {
		zAu
		.send(new zk.Event(zk.Widget.$('$'+componentId), onSuccess,
				JSON.parse(message.data)));
	}
	printSocket.onopen = function() {
		console.log("connection opened");
	};

	printSocket.onclose = function() {
		console.log("connection closed");

	};

	printSocket.onerror = function wserror(message) {
		console.log("error: " + message);
		zAu
		.send(new zk.Event(zk.Widget.$('$'+componentId), 'onPrintError',
				message.data));
	}

	return obj;

})();
