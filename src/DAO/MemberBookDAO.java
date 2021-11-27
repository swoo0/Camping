package DAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Service.MemberService;
import Util.JDBCUtil;

public class MemberBookDAO {
	
	public static MemberBookDAO instance;
	private JDBCUtil jdbc = JDBCUtil.getInstance();
	
	private MemberBookDAO() {}
	
	public static MemberBookDAO getInstance() {
		if(instance == null) {
			instance = new MemberBookDAO();
		}
		return instance;
	}
	
	
	// 예약 가능한 상품목록 조회
	public List<Map<String, Object>> possibleProdList(String date, String comId ,String lprod) throws Exception {
		
		String sql = "SELECT "
				+ "		PROD_NAME, PROD_ID, PROD_PRICE "
				+ "	FROM "
				+ "		PROD "
				+ " WHERE "
				+ "		PROD_ID not in (SELECT DISTINCT PROD_ID "
				+ "                       FROM BOOK "
				+ "                      WHERE BOOK_DATE = '" + date + "') " 
				+ "	AND "
				+ "		LPROD_ID = '" + lprod + "'"
				+ " AND COM_ID = '" + comId + "'"
				+ " ORDER BY 1 ";
		
		return jdbc.selectList(sql);
	}
	
	// 쿠폰 사용
	public int useCoupon() throws Exception {
		
		String sql = "UPDATE MEMBER "
				+ " SET MEM_COUPON = "
				+ "  (SELECT MEM_COUPON FROM MEMBER WHERE MEM_ID = "
				+ " '" + MemberService.LoginMember.get("MEM_ID") + "')"
				+ " - '" + 1 + "'"
				+ " WHERE MEM_ID = '" + MemberService.LoginMember.get("MEM_ID") + "'";
		
		return jdbc.update(sql);
	}
	
	// 마일리지 사용
	public int useMileage(int selectMileage) throws Exception {
		
		String sql = "UPDATE MEMBER "
				+ "	SET MEM_MILEAGE = "
				+ "  	(SELECT MEM_MILEAGE FROM MEMBER WHERE MEM_ID = "
				+ " '"		+ MemberService.LoginMember.get("MEM_ID") + "')"
				+ " 	- '" + selectMileage + "'"
				+ " WHERE "
				+ "		MEM_ID = '" + MemberService.LoginMember.get("MEM_ID") + "'";
		
		return jdbc.update(sql);
	}
	
	
	// 예약 과정 완료 후 BOOK 테이블 예약내역 INSERT
	public int reservationProduct(Object prod, String date, int estimate) throws Exception {
			
		String sql = "INSERT INTO "
				+ "		BOOK VALUES('B'||((select MAX(SUBSTR(BOOK_ID, 2, 5)) from BOOK) + 1), "
				+ "'" + MemberService.LoginMember.get("MEM_NO") + "', " 
				+ "'" + prod + "', " 
				+ "'" + date + "', "
				+ "'" + estimate + "', "
				+ " '0') ";
			
		return jdbc.update(sql);
	}	
	
	
	
	// 회원 예약 목록 불러오기
	public List<Map<String, Object>> memberBookList() throws Exception {
		
		List<Map<String, Object>> myBook = new ArrayList<>();
		
		String sql = "SELECT "
				+ "		A.BOOK_ID, A.MEM_NO, A.PROD_ID, B.MEM_NAME, C.PROD_NAME, "
				+ "		TO_CHAR(TO_DATE(A.BOOK_DATE), 'YYYY\"년 \"MM\"월 \"DD\"일 \"') AS BOOK_DATE,"
				+ " 	A.BOOK_ESTIMATE "
				+ " FROM "
				+ "		BOOK A, MEMBER B, PROD C"
				+ " WHERE "
				+ "		A.MEM_NO = B.MEM_NO "
				+ " AND "
				+ "		A.MEM_NO = '" + MemberService.LoginMember.get("MEM_NO") + "'"
				+ " AND "
				+ "     A.PROD_ID = C.PROD_ID "
				+ " AND "
				+ "		TO_CHAR(SYSDATE,'YYYYMMDD') <= A.BOOK_DATE "
				+ " AND "
				+ "		A.BOOK_ID NOT IN (SELECT BOOK_ID "
				+ "                         FROM REVIEW) "
				+ " ORDER BY 5";
		
		myBook = jdbc.selectList(sql);
			
		return myBook;
	}
	
	
	// 예약 취소
	public int deleteBook(String bookId) throws Exception {
		String sql = " DELETE "
				+ " FROM BOOK WHERE BOOK_ID = '"+ bookId +"'";
			
		return jdbc.update(sql);
	}	
	
}
