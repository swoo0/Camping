package DAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Service.CompanyService;
import Util.JDBCUtil;

public class CompanyBookDAO {
	
	private static CompanyBookDAO instance;
	private JDBCUtil jdbc = JDBCUtil.getInstance();
	
	private CompanyBookDAO() {
	}
	
	public static CompanyBookDAO getInstacne() {
		if(instance == null) {
			instance = new CompanyBookDAO();
		}
		return instance;
	}
	
	
	// 업체 예약확인
	public List<Map<String, Object>> companyBookList() throws Exception {
			
		List<Map<String, Object>> companyBook = new ArrayList<>();
			
		String sql = "SELECT "
				+ "		A.BOOK_ID, "
				+ "		TO_CHAR(TO_DATE(A.BOOK_DATE), 'YYYY\"년 \"MM\"월 \"DD\"일 \"') AS BOOK_DATE, "
				+ "     B.PROD_NAME, C.MEM_NAME, C.MEM_HP "
				+ " FROM "
				+ "		BOOK A, PROD B, MEMBER C, COMPANY D "
				+ " WHERE "
				+ "		A.PROD_ID = B.PROD_ID "
				+ " AND "
				+ "		A.MEM_NO = C.MEM_NO "
				+ " AND "
				+ "		B.COM_ID = D.COM_ID "
				+ " AND "
				+ "		D.COM_ID = '" + CompanyService.LoginCompany.get("COM_ID") + "' "
				+ "	AND	"
				+ "		TO_CHAR(SYSDATE,'YYYYMMDD') <= A.BOOK_DATE "
				+ " AND "
				+ "		A.BOOK_ID NOT IN (SELECT BOOK_ID "
				+ "                 	    FROM REVIEW) "
				+ " ORDER BY 2";
		
		companyBook = jdbc.selectList(sql);
			
		return companyBook;
	}
	
	// 업체 예약 취소
	public int deleteBook(Object bookId) throws Exception {
		String sql = " DELETE FROM BOOK "
				+ " WHERE BOOK_ID = '" + bookId + "' ";
			
		return jdbc.update(sql);
	}
	

}
