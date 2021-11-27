package DAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Service.MemberService;
import Util.JDBCUtil;

public class MemberDAO {
	
	private static MemberDAO instance;
	private JDBCUtil jdbc = JDBCUtil.getInstance();

	private MemberDAO() {
	}
	
	public static MemberDAO getInstance() {
		if (instance == null) {
			instance = new MemberDAO();
		}
		return instance;
	}
	
	
	// MemberService에서 사용자가 입력한 아이디 비밀번호를 검사
	public Map<String, Object> checkUser(String userId, String password) throws Exception {
		String sql = "SELECT "
				+ "		MEM_ID, MEM_PW, MEM_NAME, MEM_NO ,MEM_MILEAGE, MEM_COUPON "
				+ " FROM "
				+ "		MEMBER "
				+ " WHERE "
				+ "		MEM_ID = ?"
				+ " AND "
				+ "		MEM_PW = ?";
		
		List<Object> memberList = new ArrayList<>();
		memberList.add(userId);
		memberList.add(password);
		
		return jdbc.selectOneMember(sql, memberList);
	}
	
	// 회원가입한 데이터 삽입
	public int insertUser(Map<String, Object> memberList) throws Exception {
		String sql = "INSERT INTO MEMBER (MEM_NO, MEM_ID, MEM_PW, MEM_NAME, MEM_ADD, MEM_HP) "
					+ " VALUES('M'||((select MAX(SUBSTR(MEM_NO, 2, 5)) from MEMBER) + 1), ?, ?, ?, ?, ?)";
		List<Object> member = new ArrayList<>();
		member.add(memberList.get("MEM_ID"));
		member.add(memberList.get("MEM_PW"));
		member.add(memberList.get("MEM_NAME"));
		member.add(memberList.get("MEM_ADD"));
		member.add(memberList.get("MEM_HP"));

		return jdbc.update(sql, member);
	}
	
	// 쿠폰 지급
	public int plusCoupon(String userId) throws Exception {
		String sql = "UPDATE MEMBER "
	   			   + "   SET MEM_COUPON = "
					+ "  (SELECT MEM_COUPON FROM MEMBER WHERE MEM_ID = "
					+ " '" + userId + "')"
					+ " + '" + 1 + "'"
					+ " WHERE MEM_ID = '" + userId + "'";
			
			return jdbc.update(sql);
		}
	
	// 회원가입 되어있는 데이터 출력(아이디 중복체크여부)
	public Map<String, Object> memberIdCheck(String userId) throws Exception{
		String sql = " SELECT MEM_ID "
					 + " FROM MEMBER"
				    + " WHERE MEM_ID = ?"; 
		
		List<Object> idList = new ArrayList<>();
		idList.add(userId);
		
		return jdbc.selectOneMember(sql, idList);
	}
	
	// 회원 목록 
	public List<Map<String, Object>> memberList() throws Exception {
		
		String sql = "SELECT MEM_NO, MEM_ID, MEM_PW, MEM_NAME, MEM_ADD, MEM_HP, MEM_MILEAGE, MEM_COUPON" 
				    + " FROM MEMBER "
				   + " WHERE MEM_ID = '" + MemberService.LoginMember.get("MEM_ID") + "'";
		
		return jdbc.selectList(sql);
	}
	
	
	// 회원 탈퇴
	public int deleteUser(Map<String, Object> memberList) throws Exception {
		String sql = "DELETE FROM MEMBER "
					+ " WHERE "
					+ "	MEM_ID = '" + MemberService.LoginMember.get("MEM_ID") +"'";
			
		return jdbc.update(sql);
	}
	
	// 회원 이름 수정
	public int updateUserName(String userName) throws Exception {
		String sql = "UPDATE MEMBER"
	            + " SET MEM_NAME = '" + userName +"'"
	            + " WHERE MEM_ID = '" + MemberService.LoginMember.get("MEM_ID") + "'";
	    
	   return jdbc.update(sql);
	   }
	   
	// 회원 주소 수정
	public int updateUserAddress(String userAddress) throws Exception {
		String sql = "UPDATE MEMBER"
				+ " SET MEM_ADD = '" + userAddress +"'"
				+ " WHERE MEM_ID = '" + MemberService.LoginMember.get("MEM_ID") + "'";
   
		return jdbc.update(sql);
	}
   
	// 회원 전화번호 수정
	public int updateUserHp(String userHp) throws Exception {
		String sql = "UPDATE MEMBER"
				+ " SET MEM_HP = '" + userHp +"'"
				+ " WHERE MEM_ID = '" + MemberService.LoginMember.get("MEM_ID") + "'";
   
		return jdbc.update(sql);
	}
	
	// 회원 비밀번호 수정
	public int updateUserPw(String userPw) throws Exception {
		String sql = "UPDATE MEMBER "
	            + " SET MEM_PW = '" + userPw + "'"
	            + " WHERE MEM_ID = '" + MemberService.LoginMember.get("MEM_ID") + "'";
	      
	    return jdbc.update(sql);
	}

}
