<%@ page language="java" contentType="text/html; charset=utf-8" session="false"
%><%@ page import="com.yd.svrplatform.comm_mdl.context.MainContext"
%><%@ page import="com.yd.svrplatform.comm_mdl.context.UserContext"
%><%@ page import="javax.servlet.http.HttpSession"
%><%@ page import="java.net.URLEncoder"
%><%

String message = (String)request.getAttribute("message");
String returnCode = (String)request.getAttribute("returnCode");
String operId = "";
HttpSession session = request.getSession(false);
if (session != null) {
	UserContext user = (UserContext)session.getAttribute("user");
	if (user != null) operId = user.getOperId();
}

MainContext.clearThreadLocal();

message = URLEncoder.encode(message, "UTF-8");
returnCode = URLEncoder.encode("错误代码：" + returnCode, "UTF-8");
request.getRequestDispatcher("/ydpx/parsepage?$page=common/end.ydpx&_OPERID=" + operId + "&_RESULT_PAGE_TYPE=2&_RESULT_PAGE_TITLE=" +
		message + "&_RESULT_PAGE_CONTENT=" + returnCode).forward(request, response);
%>