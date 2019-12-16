package club_Agree;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.mysql.jdbc.PreparedStatement;

import clubMember.clubMemberVo;

public class clubAgreeDAO {
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	Statement st;

	public clubAgreeDAO() {
		try {
			String dbURL = "jdbc:mysql://localhost/mydb";
			String dbID = "root";
			String dbPassword = "2865";
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(dbURL, dbID, dbPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void db_close() {
		try {
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
			if (st != null)
				st.close();
		} catch (SQLException e) {
			System.out.println(e + "=> DataBase Error");
		}

	}

	public int agree_save(int club_id, int board_no, String student_id) {
		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

		String currentTime = sdf.format(dt);

		String SQL = "INSERT INTO club_agree_member(CLUB_ID,BOARD_NO,STUDENT_ID,INPUT_DATE) VALUES(?,?,?,?)";

		try {

			PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(SQL);

			pstmt.setInt(1, club_id);
			pstmt.setInt(2, board_no);
			pstmt.setString(3, student_id);
			pstmt.setString(4, currentTime);
			pstmt.executeUpdate();
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db_close();
		}
		return -1; // 데이터베이스 오류
	}

	public int disagree_save(int club_id, int board_no, String student_id) {

		String SQL = "Delete from club_agree_member where CLUB_ID=" + club_id + " AND BOARD_NO=" + board_no
				+ " AND STUDENT_ID=" + student_id;

		try {
			PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(SQL);
			pstmt.executeUpdate();
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db_close();
		}

		return -1;
	}

	public int check_agree(int club_id, int board_no, String student_id) {
		String SQL = "select * from club_agree_member where CLUB_ID=" + club_id + " AND BOARD_NO=" + board_no
				+ " AND STUDENT_ID=" + student_id;

		try {
			PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if (rs.next())
				return 1;
			else
				return 0;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db_close();
		}

		return -1;
	}

	public ArrayList<String> getAgreeMember(int club_id,int board_no) {
		String SQL = "select STUDENT_ID from club_agree_member where CLUB_ID=" + club_id+" AND board_no="+board_no;
		ArrayList<String> AgreeList = new ArrayList<String>();
		try {
			PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				AgreeList.add(rs.getString(1));
			}

			return AgreeList;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db_close();
		}

		return null;
		
	}
/*
	public ArrayList<String[]> getAgreeMember_info(ArrayList<String> AgreeList) {
		for (int i = 0; i < AgreeList.size(); i++) {
			String SQL = "select STUDENT_ID from club_agree_member where CLUB_ID=" + club_id;
		}

		String SQL = "select STUDENT_ID from club_agree_member where CLUB_ID=" + club_id;
		return null;
	}
*/
}
