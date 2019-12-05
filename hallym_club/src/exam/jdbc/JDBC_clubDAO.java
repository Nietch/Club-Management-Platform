package exam.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import clubMember.clubMemberVo;
import exam.jdbc.ClubVO;
import util.JDBCUtil;

public class JDBC_clubDAO {

	private Connection conn;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;

	public int getTotal(String gb_cd, String search, String at_cd) {
		String sql = "SELECT COUNT(*) FROM CLUB WHERE CLUB_ID <> 1 AND CLUB_GB_CD LIKE ? AND CLUB_NM LIKE ? AND CLUB_AT_CD LIKE ?";
		int total = 0;
		try{
			conn = JDBCUtil.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, "%" + gb_cd + "%");
			pstmt.setString(2, "%" + search + "%");
			pstmt.setString(3, "%" + at_cd + "%");
			rs = pstmt.executeQuery();
			if(rs.next()){
				total = rs.getInt(1);
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeResource(rs, pstmt, conn);
		}
		return total;
	}
	public ArrayList<ClubVO> getClublist(String gb_cd, String search, String at_cd, int pageNumber) {

		ArrayList<ClubVO> list = new ArrayList<ClubVO>();
		String sql = "";

		int row_count = 0;
		try {

//			SQL = "SELECT SQL_CALC_FOUND_ROWS * FROM"
//					+ "( SELECT A.*, IF(B.STAFF_CD = '004001',B.NM, ''), IF(B.STAFF_CD = '004001',B.PHONE_NO, '') "
//					+ "FROM CLUB AS A LEFT JOIN CLUB_MEMBER AS B " + "ON A.CLUB_ID = B.CLUB_ID "
//					+ "WHERE A.CLUB_GB_CD LIKE ? AND A.CLUB_NM LIKE ? AND A.CLUB_AT_CD LIKE ? "
//					+ "ORDER BY B.STAFF_CD IS NULL ASC, STAFF_CD ASC)X GROUP BY CLUB_ID LIMIT " + (pageNumber - 1) * 4
//					+ "," + 4;
			sql = "SELECT * FROM (SELECT ROW_NUMBER() OVER(ORDER BY CLUB_NM) AS RK, A.*, NVL(B.NM, ' ') AS STAFF_NM, NVL(B.PHONE_NO, ' ') AS STAFF_NO "
					+ "FROM CLUB A LEFT OUTER JOIN(SELECT CLUB_ID, NM , PHONE_NO FROM CLUB_MEMBER WHERE STAFF_CD = '004001')B"
					+" ON A.CLUB_ID = B.CLUB_ID	WHERE A.CLUB_ID <> 1 AND A.CLUB_GB_CD LIKE ? AND A.CLUB_NM LIKE ? AND A.CLUB_AT_CD LIKE ? "
					+"ORDER BY A.CLUB_NM) WHERE RK > ? AND RK <= ?";

			conn = JDBCUtil.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, "%" + gb_cd + "%");
			pstmt.setString(2, "%" + search + "%");
			pstmt.setString(3, "%" + at_cd + "%");
			pstmt.setInt(4, (pageNumber - 1) * 4);
			pstmt.setInt(5, pageNumber * 4);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				ClubVO vo = new ClubVO();
				vo.setClub_id(rs.getInt("CLUB_ID"));
				vo.setClub_nm(rs.getString("CLUB_NM"));
				vo.setClub_gb_cd(rs.getString("CLUB_GB_CD"));
				vo.setClub_at_cd(rs.getString("CLUB_AT_CD"));
				vo.setCnt(rs.getInt("CLUB_CNT"));
				vo.setClub_aim(rs.getString("CLUB_AIM"));
				vo.setClub_active(rs.getString("CLUB_ACTIVE"));
				vo.setClub_room(rs.getString("CLUB_ROOM"));
				vo.setOpen_dt(rs.getString("OPEN_DT"));
				vo.setIntro_file_nm(rs.getString("INTRO_FILE_NM"));
				vo.setIntro_save_file_nm(rs.getString("INTRO_SAVE_FILE_NM"));
				vo.setPoster_file_nm(rs.getString("POSTER_FILE_NM"));
				vo.setPoster_save_file_nm(rs.getString("POSTER_SAVE_FILE_NM"));
				vo.setInput_id(rs.getString("INPUT_ID"));
				vo.setInput_ip(rs.getString("INPUT_IP"));
				vo.setInput_date(rs.getString("INPUT_DATE"));
				vo.setStaff_nm(rs.getString("STAFF_NM"));
				vo.setStaff_phone(rs.getString("STAFF_NO"));
				list.add(vo);
			}
			list.get(0).setRow_count(getTotal(gb_cd, search, at_cd));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeResource(rs, pstmt, conn);
		}
		return list;
	}

	// 우수 동아리 선정
	public ArrayList<ClubVO> getTopClub(String gb_cd, String at_cd) {

		ArrayList<ClubVO> list = new ArrayList<ClubVO>();
		String sql = "";

		int limit_cnt = 3;

		try {
			if (at_cd.equals(""))
				limit_cnt = 5;

//			SQL = "SELECT * FROM (SELECT A.*, IF(B.STAFF_CD = '004001',B.NM, ''), IF(B.STAFF_CD = '004001',B.PHONE_NO, '') FROM CLUB AS A "
//					+ "LEFT JOIN CLUB_MEMBER AS B " + "ON A.CLUB_ID = B.CLUB_ID "
//					+ "WHERE A.CLUB_GB_CD LIKE ? AND A.CLUB_AT_CD LIKE ?"
//					+ "ORDER BY B.STAFF_CD IS NULL ASC, STAFF_CD ASC)X "
//					+ "GROUP BY CLUB_ID ORDER BY CLUB_CNT DESC LIMIT " + limit_cnt;
			sql = "SELECT ROWNUM , Z.* FROM (SELECT  A.*, NVL(B.STAR,0) AS STAR_CNT FROM CLUB A LEFT OUTER JOIN("+
					"SELECT CLUB_ID, COUNT(*) AS STAR FROM CLUB_MEMBER WHERE STAR ='Y' GROUP BY CLUB_ID)B"+
					" ON A.CLUB_ID = B.CLUB_ID WHERE A.CLUB_ID <> 1 AND A.CLUB_GB_CD LIKE ? AND A.CLUB_AT_CD LIKE ? "+
					"ORDER BY STAR_CNT DESC, A.CLUB_CNT DESC)Z WHERE ROWNUM > 0 AND ROWNUM <= ?";
			
			conn = JDBCUtil.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, "%" + gb_cd + "%");
			pstmt.setString(2, "%" + at_cd + "%");
			pstmt.setInt(3, limit_cnt);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				ClubVO vo = new ClubVO();
				vo.setClub_id(rs.getInt("CLUB_ID"));
				vo.setClub_nm(rs.getString("CLUB_NM"));
				vo.setClub_gb_cd(rs.getString("CLUB_GB_CD"));
				vo.setClub_at_cd(rs.getString("CLUB_AT_CD"));
				vo.setCnt(rs.getInt("CLUB_CNT"));
				vo.setClub_aim(rs.getString("CLUB_AIM"));
				vo.setClub_active(rs.getString("CLUB_ACTIVE"));
				vo.setClub_room(rs.getString("CLUB_ROOM"));
				vo.setOpen_dt(rs.getString("OPEN_DT"));
				vo.setIntro_file_nm(rs.getString("INTRO_FILE_NM"));
				vo.setIntro_save_file_nm(rs.getString("INTRO_SAVE_FILE_NM"));
				vo.setPoster_file_nm(rs.getString("POSTER_FILE_NM"));
				vo.setPoster_save_file_nm(rs.getString("POSTER_SAVE_FILE_NM"));
				vo.setInput_id(rs.getString("INPUT_ID"));
				vo.setInput_ip(rs.getString("INPUT_IP"));
				vo.setInput_date(rs.getString("INPUT_DATE"));
				list.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeResource(rs, pstmt, conn);
		}
		return list;
	}

	public String getProfessor(int club_id) {
		String SQL = "";
		String prof_name = "";
		try {
			SQL = "SELECT * FROM CLUB_PROF WHERE CLUB_ID = ? AND PROG_GB_CD = '006001'";
			conn = JDBCUtil.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, club_id);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				prof_name = rs.getString(3);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeResource(rs, pstmt, conn);
		}
		return prof_name;
	}

	public ArrayList<ClubVO> getboardwriteClub(String student_id) {
		ArrayList<ClubVO> clubList = null;
		String SQL = "";
		try {
			SQL = "SELECT * FROM CLUB WHERE CLUB_ID = ANY(SELECT CLUB_ID FROM CLUB_MEMBER WHERE STUDENT_ID = ? AND JOIN_CD = 008001)";
			conn = JDBCUtil.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, student_id);
			rs = pstmt.executeQuery();
			clubList = new ArrayList<ClubVO>();
			while (rs.next()) {
				ClubVO vo = new ClubVO();
				vo.setClub_id(rs.getInt(1));
				vo.setClub_nm(rs.getString(2));
				clubList.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeResource(rs, pstmt, conn);
		}
		return clubList;
	}

	public int createClub(ClubVO CV, String student_id) {
		String SQL = "INSERT INTO club(CLUB_ID, CLUB_NM, CLUB_GB_CD, CLUB_AT_CD, CLUB_CNT, CLUB_AIM, CLUB_ACTIVE, CLUB_ROOM, "
				+ "OPEN_DT, INTRO_FILE_NM, INTRO_SAVE_FILE_NM, POSTER_FILE_NM, POSTER_SAVE_FILE_NM) VALUES((SELECT ifnull(MAX(CLUB_ID),0)+1 FROM CLUB AS C),?,?,?,?,?,?,?,?,?,?,?,?)";

		String SQL2 = "INSERT INTO CLUB_MEMBER(CLUB_ID, STUDENT_ID, STAFF_CD, JOIN_DT, JOIN_CD) VALUES "
				+ "((SELECT ifnull(MAX(CLUB_ID),0) FROM CLUB AS C),?,'004001',?,'008001')";
		try {
			conn = JDBCUtil.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, CV.getClub_nm());
			pstmt.setString(2, CV.getClub_gb_cd());
			pstmt.setString(3, CV.getClub_at_cd());
			pstmt.setInt(4, CV.getCnt());
			pstmt.setString(5, CV.getClub_aim());
			pstmt.setString(6, CV.getClub_active());
			pstmt.setString(7, CV.getClub_room());
			pstmt.setString(8, CV.getOpen_dt());
			pstmt.setString(9, CV.getIntro_file_nm());
			pstmt.setString(10, CV.getIntro_save_file_nm());
			pstmt.setString(11, CV.getPoster_file_nm());
			pstmt.setString(12, CV.getPoster_save_file_nm());
			pstmt.executeUpdate();
			JDBCUtil.closeResource(pstmt, conn);

			pstmt = conn.prepareStatement(SQL2);
			pstmt.setString(1, student_id);

			java.util.Date dt = new java.util.Date();
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
			String today = sdf.format(dt);

			pstmt.setString(2, today);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeResource(rs, pstmt, conn);
		}

		return -1; // 데이터베이스 오류

	}

	public String getIntro_FilePath(int club_id) {
		String SQL = "select POSTER_SAVE_FILE_NM from club where CLUB_ID=" + club_id + "";
		String Intro_FilePath = "";

		try {
			conn = JDBCUtil.getConnection();
			pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Intro_FilePath = rs.getString(1);
				return Intro_FilePath;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeResource(rs, pstmt, conn);
		}
		return null;
	}

	public ArrayList<String[]> getItems(int club_id) {
		String sql = "SELECT ITEM_NM,ITEM_CONT,TOT_CNT from item where CLUB_ID=" + club_id + " ORDER BY ITEM_NM";
		ArrayList<String[]> allClubItem = new ArrayList<>();

		try {
			conn = JDBCUtil.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String clubItem[] = new String[3];
				clubItem[0] = rs.getString(1);
				clubItem[1] = rs.getString(2);
				clubItem[2] = rs.getString(3);
				allClubItem.add(clubItem);
			}
			return allClubItem;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeResource(rs, pstmt, conn);
		}
		return null;
	}

	// intro, 동아리 조회
	public ArrayList<ClubVO> getClubIntro(String student_id) {
		ArrayList<ClubVO> clubList = null;
		String SQL = "";
		try {
			if (student_id == "") {
				SQL = "SELECT C.CLUB_ID, C.CLUB_NM, C.INTRO_SAVE_FILE_NM, C.CLUB_AIM, C.CLUB_ACTIVE, S.SO_NM, D.SO_NM FROM CLUB AS C "
						+ "JOIN SO_CD AS S ON C.CLUB_GB_CD = S.SO_CD JOIN SO_CD AS D ON C.CLUB_AT_CD = D.SO_CD "
						+ "ORDER BY C.CLUB_CNT DESC LIMIT 6";
				conn = JDBCUtil.getConnection();
				pstmt = conn.prepareStatement(SQL);
				rs = pstmt.executeQuery();
				clubList = new ArrayList<ClubVO>();

				while (rs.next()) {
					ClubVO vo = new ClubVO();
					vo.setClub_id(rs.getInt(1));
					vo.setClub_nm(rs.getString(2));
					vo.setIntro_save_file_nm(rs.getString(3));
					vo.setClub_aim(rs.getString(4));
					vo.setClub_active(rs.getString(5));
					vo.setClub_gb_cd(rs.getString(6));
					vo.setClub_at_cd(rs.getString(7));
					clubList.add(vo);
				}

			} else {
				SQL =

						"SELECT A.CLUB_ID, A.CLUB_NM, A.INTRO_SAVE_FILE_NM, IF(S.SO_NM IS NULL, '회원', S.SO_NM), GB.SO_NM, AT.SO_NM "
								+ "FROM CLUB AS A JOIN CLUB_MEMBER AS B ON A.CLUB_ID = B.CLUB_ID "
								+ "LEFT JOIN SO_CD AS S ON S.SO_CD = B.STAFF_CD "
								+ "JOIN SO_CD AS GB ON A.CLUB_GB_CD = GB.SO_CD JOIN SO_CD AS AT ON A.CLUB_AT_CD = AT.SO_CD "
								+ "WHERE STUDENT_ID = ? AND JOIN_CD = '008001' ORDER BY B.STAFF_CD IS NULL, B.STAFF_CD ";

				pstmt = conn.prepareStatement(SQL);
				pstmt.setString(1, student_id);
				rs = pstmt.executeQuery();
				clubList = new ArrayList<ClubVO>();
				while (rs.next()) {
					ClubVO vo = new ClubVO();
					vo.setClub_id(rs.getInt(1));
					vo.setClub_nm(rs.getString(2));
					vo.setIntro_save_file_nm(rs.getString(3));
					vo.setStaff_cd(rs.getString(4));
					vo.setClub_gb_cd(rs.getString(5));
					vo.setClub_at_cd(rs.getString(6));
					clubList.add(vo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeResource(rs, pstmt, conn);
		}
		return clubList;
	}

	public String getOpen_Dt(int club_id) {
		String sql = "SELECT OPEN_DT from club where CLUB_ID=" + club_id + ";";
		String open_dt = "";

		try {
			conn = JDBCUtil.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				open_dt = rs.getString(1);
			}
			return open_dt;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeResource(rs, pstmt, conn);
		}
		return null;
	}

	// 동아리 개설일 가져오기
	public String getMaster(int club_id) {
		String sql = "SELECT NM from club_member where CLUB_ID=" + club_id + " and STAFF_CD =" + "004001" + ";";
		String open_dt = "";

		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				open_dt = rs.getString(1);
			}
			return open_dt;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeResource(rs, pstmt, conn);
		}
		return null;
	}

	// 동아리 개설일 가져오기
	public String getClubNMs(int club_id) {
		String SQL = "SELECT CLUB_NM FROM club where club_id=" + club_id;
		String answer = "";
		try {
			conn = JDBCUtil.getConnection();
			pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				answer = rs.getString(1);
			}
			return answer;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeResource(rs, pstmt, conn);
		}
		return null;

	}

	public int getClubIds(String club_NM) {
		String SQL = "SELECT CLUB_ID FROM club where CLUB_NM='" + club_NM + "'";
		int answer = -1;
		try {
			conn = JDBCUtil.getConnection();
			pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				answer = rs.getInt(1);
			}
			return answer;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeResource(rs, pstmt, conn);
		}
		return answer;// 디비 오류
	}

	public ArrayList<String> getMyJoinClubList(String user_id) {
		ArrayList<String> allMyClub = new ArrayList<>();
		String sql = "SELECT CLUB_ID from club_member where STUDENT_ID = " + user_id + " and JOIN_CD = 008001;";

		try {
			conn = JDBCUtil.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String clubNM = getClubNMs(rs.getInt(1));
				allMyClub.add(clubNM);
			}
			return allMyClub;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeResource(rs, pstmt, conn);
		}
		return null;
	}

	public ArrayList<String> getMyWaitClubList(String user_id) {
		ArrayList<String> allMyClub = new ArrayList<>();
		String sql = "SELECT CLUB_ID from club_member where STUDENT_ID = " + user_id + " and JOIN_CD = 008003;";

		try {
			conn = JDBCUtil.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String clubNM = getClubNMs(rs.getInt(1));
				allMyClub.add(clubNM);
			}
			return allMyClub;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeResource(rs, pstmt, conn);
		}
		return null;
	}

	public int getJoin_cd(String username, String clubNM) {
		String sql = "SELECT STAFF_CD FROM club_member where STUDENT_ID=" + username
				+ " AND CLUB_ID=( SELECT CLUB_ID from club where CLUB_NM ='" + clubNM + "')";

		try {
			conn = JDBCUtil.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				if (rs.getString(1) != null) {
					if (rs.getString(1).equals("004001") || rs.getString(1).equals("004002"))
						return 0;
				}
			}
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeResource(rs, pstmt, conn);
		}
		return -1;
	}

	public ArrayList<ClubVO> getClubInfo(int club_id) {
		ArrayList<ClubVO> clubList = null;
		String sql = "";
		try {
			sql = "SELECT CLUB_ID, CLUB_NM, CLUB_GB_CD, CLUB_AT_CD, CLUB_CNT, CLUB_AIM, CLUB_ACTIVE, CLUB_ROOM, OPEN_DT "
					+ "FROM CLUB WHERE CLUB_ID = " + club_id;

			conn = JDBCUtil.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			clubList = new ArrayList<ClubVO>();
			while (rs.next()) {
				ClubVO vo = new ClubVO();
				vo.setClub_id(rs.getInt(1));
				vo.setClub_nm(rs.getString(2));
				vo.setClub_gb_cd(rs.getString(3));
				vo.setClub_at_cd(rs.getString(4));
				vo.setCnt(rs.getInt(5));
				vo.setClub_aim(rs.getString(6));
				vo.setClub_active(rs.getString(7));
				vo.setClub_room(rs.getString(8));
				vo.setOpen_dt(rs.getString(9));

				clubList.add(vo);
			}

		} catch (Exception e) {
			System.out.println("database fail");
		} finally {
			JDBCUtil.closeResource(rs, pstmt, conn);
		}
		return clubList;
	}

	public int updateClub(ClubVO CV) {
		String sql = "UPDATE CLUB SET CLUB_GB_CD = ? , CLUB_AT_CD = ?, CLUB_CNT = ?, CLUB_AIM = ?, CLUB_ACTIVE = ?, "
				+ " CLUB_ROOM = ?, OPEN_DT = ? ";
		try {

			if (CV.getIntro_save_file_nm() != null) {
				sql = sql + ", INTRO_SAVE_FILE_NM = '" + CV.getIntro_save_file_nm() + "'";
			}
			if (CV.getPoster_save_file_nm() != null) {
				sql = sql + ", POSTE_SAVE_FILE_NM = '" + CV.getPoster_save_file_nm() + "'";
			}

			sql = sql + " WHERE CLUB_ID = " + CV.getClub_id();
			conn = JDBCUtil.getConnection();
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, CV.getClub_gb_cd());
			pstmt.setString(2, CV.getClub_at_cd());
			pstmt.setInt(3, CV.getCnt());
			pstmt.setString(4, CV.getClub_aim());
			pstmt.setString(5, CV.getClub_active());
			pstmt.setString(6, CV.getClub_room());
			pstmt.setString(7, CV.getOpen_dt());
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeResource(pstmt, conn);
		}
		return -1;
	}

	public int addStar(int club_id, String student_id) {
		String sql = "UPDATE CLUB_MEMBER SET STAR = 'Y' WHERE CLUB_ID = ? AND STUDENT_ID = ? ";
		try {
			conn = JDBCUtil.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, club_id);
			pstmt.setString(2, student_id);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeResource(pstmt, conn);
		}
		return -1;
	}

	public int deleteStar(int club_id, String student_id) {
		String sql = "UPDATE CLUB_MEMBER SET STAR = 'N' WHERE CLUB_ID = ? AND STUDENT_ID = ? ";
		try {
			conn = JDBCUtil.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, club_id);
			pstmt.setString(2, student_id);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeResource(pstmt, conn);
		}
		return -1;
	}

	public String getStar(int club_id, String student_id) {
		String sql = "SELECT STAR FROM CLUB_MEMBER WHERE CLUB_ID = ? AND STUDENT_ID = ? ";
		String str = "N";
		try {
			conn = JDBCUtil.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, club_id);
			pstmt.setString(2, student_id);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				str = rs.getString(1);
			} else if (rs.wasNull()) {
				str = "M";
			} else {
				str = "N";
			}
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeResource(rs, pstmt, conn);
		}
		return null;
	}

	public int getStarCnt(int club_id) {
		String sql = "SELECT COUNT(STAR) FROM CLUB_MEMBER WHERE CLUB_ID = ? AND STAR = 'Y' ";
		int cnt = 0;
		try {
			conn = JDBCUtil.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, club_id);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				cnt = rs.getInt(1);
			} else {

			}
			return cnt;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeResource(rs, pstmt, conn);
		}
		return -1;
	}
}