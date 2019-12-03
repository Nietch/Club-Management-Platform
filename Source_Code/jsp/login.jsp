<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="bbs.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="exam.jdbc.ClubVO"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=1200">
<title>한림대학교 동아리</title>
<link rel="stylesheet" type="text/css" href="css/common.css?after">
<link rel="stylesheet" type="text/css" href="css/login.css?after">
</head>
<body>

	<%
		request.setCharacterEncoding("UTF-8");

		String username = null;
		if (session.getAttribute("username") != null) {
			username = (String) session.getAttribute("username");
		}
	%>

	<div id="wrap">
		<div id="header">
			<h1>
				<a href="index.jsp"><img src="image/logo.gif" alt="한림대학교 동아리"></a>
			</h1>
			<a href="#content" class="skip">본문 바로가기</a>

			<ul id="snb">
				<li><a href="login.jsp">로그인</a></li>
				<li><a href="index.html">회원가입</a></li>
			</ul>

			<ul id="gnb">
				<li><a href="club_search.jsp">동아리조회</a></li>
				<li><a href="top_club.jsp">우수동아리</a></li>
				<li><a href="board.jsp">공지사항</a></li>
				<li><a href="jido.jsp">주변음식점</a></li>
			</ul>
		</div>
		<hr>
		<div id="visual">
			<img src="image/visual.jpg" alt="">
		</div>

		<hr>


		<div id="content">
			<div class="box">
				<div class="h">회원 로그인</div>
				<div class="info">
					<form method="post" action="LoginAction.jsp" style="display: block;">
						<div class="id">
							<input type="text" name="username" placeholder="아이디 입력"
								 onfocus="this.select()" >
						</div>
						
						<div class="pwd">
							<input type="passWord" name="password" placeholder="비밀번호 입력"
							 onfocus="this.select()">
						</div>
						
						<div class="ok">
							<input type="submit" value="로그인 ">
						</div>
						

						 <a href='#'
							onclick='javascript:window.open("signUp.jsp","name99", "width=720px,height=720px");'>회원가입</a>
					</form>
				</div>
			</div>

		</div>
		<hr>
		<div id="footer">
			<div class="copyright">
				<address>Copyright 2019. 김정인, 김진섭. All Rights Reserved.</address>
			</div>
		</div>
	</div>
</body>
</html>