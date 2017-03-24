<html>
<head>
<title><%=response.getStatus()%> Error page</title>
<style>
.p {
	font-family: "PT Sans", Verdana, Tahoma, Arial, Helvetica, sans-serif;
	font-size: 14px;
	color: #FFFFFF;
}

tr.noBorder td {
	border: 0;
}
</style>
</head>
<body bgcolor="#2B4F81">
	<table border="0" style="width: 100%">
		<tr>
			<td>
				<div align="left">
					<img src="PLFSmallLogo.png" align="left"
						style="margin-left: 0px; margin-top: 0px margin-bottom:0px" />
				</div>
				<div align="left" style="margin-top: 15px; margin-left: 80px;">
					<span
						style="font-size: 22px; color: #FFFFFF; font-family: Verdana, Tahoma, Arial, Helvetica, sans-serif;">pennApps
						Finance Factory</span>
				</div>
			</td>
		</tr>
	</table>
	<table border="1" style="width: 100%; border: 1px solid #c5c5c5">
		<tr class="noBorder">
			<td>&nbsp;</td>
		</tr>
		<tr class="noBorder">
			<td>&nbsp;</td>
		</tr>
		<tr class="noBorder">
			<td>&nbsp;</td>
		</tr>
		<tr class="noBorder">
			<td>&nbsp;</td>
		</tr>
		<tr class="noBorder">
			<td>&nbsp;</td>
		</tr>
		<tr class="noBorder">
			<td>&nbsp;</td>
		</tr>
		<tr class="noBorder">
			<td>&nbsp;</td>
		</tr>
		<tr class="noBorder">
			<td>&nbsp;</td>
		</tr>
		<tr class="noBorder">
			<td>
				<div style="text-align: center">
					<%
						if (response.getStatus() == 404) {
					%>
					<p class="p">Error: 404</p>
					<p class="p">Resource not found/unavailable. Please contact
						administrator</p>
					<%
						} else if (response.getStatus() == 403) {
					%>
					<p class="p">Access denied. Refresh for login into application
						again or Please contact administrator</p>
					<%
						} else if (response.getStatus() == 500) {
					%>
					<p class="p">Internal Server Error. Please contact
						administrator</p>
					<%
						} else if (response.getStatus() == 410) {
					%>
					<p class="p">Resource requested is no longer available and will
						not be available again. Please contact administrator</p>
					<%
						}
					%>
				</div>
			</td>
		</tr>
		<tr class="noBorder">
			<td>&nbsp;</td>
		</tr>
		<tr class="noBorder">
			<td>&nbsp;</td>
		</tr>
		<tr class="noBorder">
			<td>&nbsp;</td>
		</tr>
		<tr class="noBorder">
			<td>&nbsp;</td>
		</tr>
		<tr class="noBorder">
			<td>&nbsp;</td>
		</tr>
		<tr class="noBorder">
			<td>&nbsp;</td>
		</tr>
		<tr class="noBorder">
			<td>&nbsp;</td>
		</tr>
		<tr class="noBorder">
			<td>&nbsp;</td>
		</tr>
		<tr class="noBorder">
			<td>&nbsp;</td>
		</tr>
	</table>
</body>
</html>