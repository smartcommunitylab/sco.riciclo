<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>   
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Upload failure ${appId}</title>
		
		<style type="text/css">
			a:link { color: #3366CC; text-decoration: none}
			a:visited { color: #3366CC; text-decoration: none}
			a:hover { color: #3366CC; text-decoration: underline}
			a:active { color: #3366CC; text-decoration: none}
		</style>		
		
	</head>
	<body>
		<font face="verdana" size="4">
			<p>Failure  ${appId}</p>
			<a href=".">Home</a>
			<h3>Error</h3>
			<p>${error}</p>
			<br/>
			<h3>Exception:</h3>
			<ol>
				<p>${exception}</p>
				<c:forEach items="${trace}" var="traceLine">
	         	${traceLine} <br>
				</c:forEach>
			</ol>
			<br/>			
			<h3>Validation errors:</h3>
			<ol>
				<c:forEach items="${validationErrors}" var="validationError">
	         	${validationError} <br>
				</c:forEach>
			</ol> <br /> <a href=".">Home</a>
	
		</font>
	
	</body>
</html>
