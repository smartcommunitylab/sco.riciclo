<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Rifiuti console</title>

<style type="text/css">
a:link {
	color: #3366CC;
	text-decoration: none
}

a:visited {
	color: #3366CC;
	text-decoration: none
}

a:hover {
	color: #3366CC;
	text-decoration: underline
}

a:active {
	color: #3366CC;
	text-decoration: none
}
</style>

</head>
<body>
	<font face="verdana" size="4">
		<p>Giudicarie</p>

		<div>
			<form:form method="post" modelAttribute="fileList"
				action="giudicarie/savefiles" enctype="multipart/form-data">
				<table id="fileTable">
					<tr>
						<td>Modello</td>
						<td><input name="files[0]" type="file" /></td>
					</tr>
					<tr>
						<td>Isole</td>
						<td><input name="files[1]" type="file" /></td>
					</tr>
					<tr>
						<td>CRM</td>
						<td><input name="files[2]" type="file" /></td>
					</tr>
				</table>
				<br />

				<input type="submit" value="submit">

			</form:form>
		</div>


		</div>

	</font>
</body>
</html>
