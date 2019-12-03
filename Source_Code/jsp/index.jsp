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
<link rel="stylesheet" type="text/css" href="css/default.css?">

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

			<%
				if (username == null) {
			%>
			<ul id="snb">
				<li><a href="login.jsp">로그인</a></li>
				<li><a href="index.html">회원가입</a></li>
			</ul>
			<%
				} else {
			%>
			<ul id="snb">
				<li><span class="txt"> <%
 	out.print((String) session.getAttribute("username") + " 님 환영합니다.");
 %>
				</span></li>
				<li><a href='logoutAction.jsp'>로그아웃</a></li>
			</ul>
			<%
				}
			%>
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
			<div class="latest">
				<div class="notice">
					<h2>공지사항</h2>
					<ul>
						<jsp:useBean id="bbsDao" class="bbs.BbsDAO" />
						<%
							ArrayList<Bbs> bbs = bbsDao.get_intro(1, "007001");
							for (Bbs vo : bbs) {
						%>
						<li><a
							href="view.jsp?BOARD_NO=<%=vo.getBOARD_NO()%>&club_id=1&board_cd=007001"><%=vo.getTITLE()%></a>
							<span class="date"><%=vo.getINPUT_DATE()%></span></li>
						<%
							}
						%>
					</ul>
					<a href="board.jsp" class="more">더보기</a>
				</div>
				<div class="banner">
					<h2>배너</h2>
					<p>
						<img src="image/banner.gif" alt="지금 로그인하고 다양한 동아리를 만나세요!">
					</p>
				</div>
			</div>

			<jsp:useBean id="clubDao" class="exam.jdbc.JDBC_clubDAO" />

			<div class="club">
				<div class="intro">
					<h2>동아리</h2>
					<%
						if (username == null) {
					%>
					<%
						ArrayList<ClubVO> list = clubDao.getClubIntro("");
					%>
					<ul>
						<%
							for (ClubVO vo : list) {
						%>
						<li>
							<div class="thm">
								<img class="logo" src="image/poster/<%=vo.getIntro_file_nm()%>"
									onerror="this.src='image/error.png'" alt="">
							</div>
							<div class="tit">
								<a href="club_search.jsp?search=<%=vo.getClub_nm()%>"><%=vo.getClub_nm()%></a>
							</div>
							<div class="note"><%=vo.getClub_aim()%></div>
							<div class="tag">
								#<%=vo.getClub_active()%>
								#<%=vo.getClub_gb_cd()%>
								#<%=vo.getClub_at_cd()%></div>
						</li>
						<%
							}
						%>
					</ul>
					<%
						} else {
							ArrayList<ClubVO> list = clubDao.getClubIntro(username);
					%>
					<ul>
						<%
							for (ClubVO vo : list) {
						%>
						<li>
							<div class="thm">
								<img class="logo" src="image/poster/<%=vo.getIntro_file_nm()%>"
									onerror="this.src='image/error.png'" alt="">

							</div> <jsp:useBean id="dao" class="exam.jdbc.JDBC_clubDAO" /> <%String star_state = dao.getStar(vo.getClub_id(), username);%>
							<div class="tit">
								<a href="club_search.jsp?search=<%=vo.getClub_nm()%>"><%=vo.getClub_nm()%></a>

								<%if (star_state.equals("Y")) { %>
								<button type="button" class="star-btn"
									onclick="location.href='likeAction.jsp?club_id=<%=vo.getClub_id()%>&state=1'">
									<img src="image/star1.png" width="23" height="23">
								</button>
								<%
							} else if (star_state.equals("N")) {
							%>
								<button type="button" class="star-btn"
									onclick="location.href='likeAction.jsp?club_id=<%=vo.getClub_id()%>&state=0'">
									<img src="image/star0.png" width="23" height="23">
								</button>
								<%
							}%>

							</div>
							<div class="note"><%=vo.getStaff_cd() %></div>
							<div class="tag">
								#<%=vo.getClub_gb_cd()%>
								#<%=vo.getClub_at_cd()%></div>
						</li>
						<%
							}
						%>
					</ul>
					<%
						}
					%>
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