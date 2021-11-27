package DAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Service.CompanyService;
import Service.MemberService;
import Util.JDBCUtil;

public class ReviewDAO {

	private static ReviewDAO instance;
	private JDBCUtil jdbc = JDBCUtil.getInstance();
	
	private ReviewDAO() {
	}
	
	public static ReviewDAO getInstance() {
		if (instance == null) {
			instance = new ReviewDAO();
		}
		return instance;
	}
		
	// 리뷰 작성
	public int insertReview(Map<String, Object> reviewMember, Object select) throws Exception {
		
		String sql = "INSERT INTO REVIEW "
				+ " VALUES("
				+ " 	(SELECT 'RE'||(MAX(SUBSTR(REVIEW_ID,3,5))+1) FROM REVIEW), "
				+ "		'" + select + "', "
				+ "		(SELECT PROD_ID FROM BOOK WHERE BOOK_ID = " + "'" + (String)select + "'), "
				+ "		?, ?, ?)";
		
		List<Object> member = new ArrayList<>();
		member.add(reviewMember.get("REVIEW_TITLE"));
		member.add(reviewMember.get("REVIEW_CONTENT"));
		member.add(reviewMember.get("REVIEW_GRADE"));
		
		return jdbc.update(sql, member);
	}
	
	// 리뷰작성시 마일리지 지급
	public int writeMileage(Object bookId) throws Exception {
    	
    String sql = "UPDATE MEMBER "
    		+ " SET MEM_MILEAGE = (SELECT MEM_MILEAGE "
    		+ "                      FROM MEMBER "
    		+ "                     WHERE MEM_NO = '" + MemberService.LoginMember.get("MEM_NO") + "') "
    		+ " 						+ (SELECT BOOK_ESTIMATE "
    		+ "    							 FROM BOOK  "
    		+ "  							WHERE BOOK_ID = '" + bookId + "') / 100 * 3 " 
    		+ " WHERE MEM_NO = '" + MemberService.LoginMember.get("MEM_NO") + "'";
    	
    return jdbc.update(sql);
	}
	
	// 리뷰 작성시 예약테이블 리뷰작성여부 1로 변경
	public int writtenReview(Object bookId) throws Exception {
		String sql = "update BOOK "
				+ " set REVIEW_WRITTEN = 1 "
				+ " where BOOK_ID = '" + bookId + "'";
		
		return jdbc.update(sql);
	}
	
	// 리뷰 조회
	public List<Map<String, Object>> reviewList() throws Exception{
		
		String sql = "SELECT "
				+ "		A.BOOK_ID, A.PROD_ID, A.REVIEW_TITLE, A.REVIEW_CONTENT, A.REVIEW_GRADE "
				+ " FROM "
				+ "		REVIEW A, BOOK B "
				+ " WHERE "
				+ " 	A.BOOK_ID = B.BOOK_ID "
				+ " AND "
				+ "		B.MEM_NO = '" + MemberService.LoginMember.get("MEM_NO") + "' ";
		
		return jdbc.selectList(sql);
	}
	
	// 리뷰 수정
	public int updateReview(String title, String content, double rate, Object bookId) throws Exception {
		String sql = " UPDATE REVIEW "
				+ "     SET REVIEW_TITLE = '" + title + "', "
				+ "         REVIEW_CONTENT = '" + content + "', "
				+ "         REVIEW_GRADE = '" + rate + "'"
				+ "   WHERE BOOK_ID = '" + bookId + "'" ;
		
		return jdbc.update(sql);
	}
	
	// 리뷰 삭제
	public int deleteReview(Object bookId) throws Exception {
		String sql = "DELETE "
				+ "  FROM REVIEW "
				+ " WHERE BOOK_ID = '" + bookId + "'";
		
		return jdbc.update(sql);
	}
	
	
	// 업체 리뷰 확인
	// 1.업체 보유 상품 확인
	public List<Map<String, Object>> prodCheck() throws Exception {
		
		String sql = "SELECT "
				+ "		 PROD_NAME "
				+ " FROM "
				+ "		 PROD"
				+ " WHERE "
				+ "		 COM_ID =  '" + CompanyService.LoginCompany.get("COM_ID") + "' "
				+ " ORDER BY 1";
	
		return jdbc.selectList(sql) ;
	}
	
	// 2.보유 상품의 리뷰 확인
	public List<Map<String, Object>> prodReviewCheck(String prodName) throws Exception {
		
		String sql = "SELECT "
				+ "		B.PROD_NAME, A.REVIEW_TITLE, A.REVIEW_CONTENT, A.REVIEW_GRADE, C.MEM_ID "
				+ " FROM "
				+ "		PROD B, REVIEW A, MEMBER C, BOOK D "
				+ " WHERE "
				+ "		B.PROD_NAME = ? "
	            + " AND "
	            + "      A.PROD_ID = B.PROD_ID "
	            + " AND "
	            + "      A.BOOK_ID = D.BOOK_ID "
	            + " AND "
	            + "     C.MEM_NO = D.MEM_NO ";
		
		List<Object> reviewList = new ArrayList<>();
		reviewList.add(prodName);
				
		return jdbc.selectList(sql,reviewList);
	}
	
	// 회원의 현재 날짜 이전 예약목록 불러오기
	public List<Map<String, Object>> memberBookName() throws Exception {
		
		List<Map<String, Object>> reviewIndex = new ArrayList<>();
		 
		String sql = "SELECT "
				+ "			A.BOOK_ID, A.PROD_ID, B.PROD_NAME, TO_CHAR(TO_DATE(A.BOOK_DATE), 'YYYY\"년\" MM\"월\" DD\"일\"') AS BOOK_DATE, A.REVIEW_WRITTEN"
				+ "  FROM BOOK A, PROD B "
				+ " WHERE A.PROD_ID = B.PROD_ID "
				+ "   AND A.MEM_NO = '" + MemberService.LoginMember.get("MEM_NO") + "'"
				+ "   AND TO_CHAR(SYSDATE,'YYYYMMDD') >= A.BOOK_DATE "
				+ "   AND A.BOOK_ID NOT IN (SELECT BOOK_ID "
				+ "                           FROM REVIEW) "
				+ "	ORDER BY 4 ";
		 
		reviewIndex = jdbc.selectList(sql);
		 
		return reviewIndex;
	   }
	
}
