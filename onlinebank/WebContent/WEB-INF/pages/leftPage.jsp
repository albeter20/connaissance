<div id="ddblueblockmenu">
  <div class="menutitle">Account Operations</div>
  <ul>
    <li><a href="${pageContext.request.contextPath}/main">Welcome,&nbsp;<%=(String)session.getAttribute("cust_name")%></a></li>
    <li><a href="${pageContext.request.contextPath}/createAccount">Create Account</a></li>
	<li><a href="${pageContext.request.contextPath}/depositAccount">Deposite</a></li>
    <li><a href="${pageContext.request.contextPath}/withdrawMoney">Do Withdraw</a></li>
    <li><a href="${pageContext.request.contextPath}/get-balance.jsp">Get Balance</a></li>
	<li><a href="${pageContext.request.contextPath}/transfer.jsp">Trasnsfer Amount</a></li>
	<li><a href="${pageContext.request.contextPath}/view-reports.jsp">View Report</a></li>
	<li><a href="${pageContext.request.contextPath}/logOff.jsp">LogOut</a></li>
  </ul>
  <div class="menutitle">&nbsp;</div>
</div>