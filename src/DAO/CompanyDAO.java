package DAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Service.CompanyService;
import Util.JDBCUtil;

public class CompanyDAO {
	
	private static CompanyDAO instance;
	private JDBCUtil jdbc = JDBCUtil.getInstance();
	
	private CompanyDAO() {
	}
	
	public static CompanyDAO getInstance() {
		if (instance == null) {
			instance = new CompanyDAO();
		}
		return instance;
	}
	
	
	// 유저에게 아이디, 비밀번호를 입력 받아 DB 체크
	public Map<String, Object> checkUser(String userId, String password) throws Exception {
		String sql = "SELECT "
				+ "		COM_ID, COM_PW, COM_NAME "
				+ " FROM "
				+ "		COMPANY "
				+ " WHERE "
				+ "		COM_ID = ?"
				+ " AND "
				+ "		COM_PW = ?";
		
		List<Object> companyList = new ArrayList<>();
		companyList.add(userId);
		companyList.add(password);
		
		return jdbc.selectOneMember(sql, companyList);
	}
	
	
	// 상품 추가
	// 1. 분류 코드 체크
	public List<Map<String, Object>> lprodCheck() throws Exception {
		String sql = "SELECT "
				+ "		LPROD_ID, LPROD_NAME"
				+ " FROM "
				+ "		LPROD";
	
		return jdbc.selectList(sql);
	}
	
	// 2. 상품 추가 데이터 삽입
	public int insertProd(Map<String, Object> prodList) throws Exception {
		String sql = "INSERT INTO PROD(PROD_ID, COM_ID, LPROD_ID, PROD_NAME, PROD_PRICE) "
				+ "		VALUES('P'||((SELECT MAX(SUBSTR(PROD_ID,2,5)) FROM PROD) + 1), "
				+ " '" + CompanyService.LoginCompany.get("COM_ID") + "', ?, ?, ?)"; 
		
		List<Object> prod = new ArrayList<>();
		prod.add(prodList.get("LPROD_ID"));
		prod.add(prodList.get("PROD_NAME"));
		prod.add(prodList.get("PROD_PRICE"));
		
		return jdbc.update(sql, prod);
	}
	
	//업체목록 불러오기
	public List<Map<String,Object>> companySelect() throws Exception {
		
		String sql = "SELECT COM_NAME, "
				+ "           COM_ADD, "
				+ "           COM_TEL, "
				+ "			  COM_ID "
				+ "      FROM COMPANY ";
		
		return jdbc.selectList(sql);
	}
	

	// 보유 중인 상품 목록 리스트
	public List<Map<String, Object>> prodList() throws Exception {
		String sql = "SELECT "
				+ "		PROD_ID, PROD_NAME, PROD_PRICE "
				+ " FROM "
				+ "		PROD "
				+ " WHERE "
				+ " 	COM_ID = '" + CompanyService.LoginCompany.get("COM_ID") + "' ";
	
		return jdbc.selectList(sql);
	}
	
	
	// 상품 정보 수정
	public int updateProdNP(String name, String price, Object prodId) throws Exception {
		String sql = "UPDATE PROD "
				+ " SET "
				+ "		PROD_NAME = '" + name + "', "
				+ " 	PROD_PRICE = '" + price + "'"
				+ " WHERE "
				+ "		PROD_ID = '" + prodId + "' ";
	
		return jdbc.update(sql);
	}
	
	
	// 업체 정보 수정
	// 업체 정보 목록
	public List<Map<String,	Object>> companyList() throws Exception {
		String sql = "SELECT "
				+ "		COM_NAME, COM_ADD, COM_TEL, COM_HP "
				+ " FROM COMPANY "
				+ " WHERE COM_ID = '" + CompanyService.LoginCompany.get("COM_ID") + "'";
	
		return jdbc.selectList(sql);
	}
	
	
	// 업체 이름 수정
	public int updateCompanyName(String comName) throws Exception {
		String sql = "UPDATE COMPANY "
				+ "	SET "
				+ "		COM_NAME = '" + comName + "' "
				+ " WHERE "
				+ "		COM_ID = '" + CompanyService.LoginCompany.get("COM_ID") + "' ";
		
		return jdbc.update(sql);
	}
	
	// 업체 주소 수정
	public int updateCompanyAdd(String comAdd) throws Exception {
		String sql = "UPDATE COMPANY "
				+ "	SET "
				+ "		COM_ADD = '" + comAdd + "' "
				+ " WHERE "
				+ "		COM_ID = '" + CompanyService.LoginCompany.get("COM_ID") + "' ";
		
		return jdbc.update(sql);
	}
	
	// 업체 전화번호 수정
	public int updateCompanyTel(String comTel) throws Exception {
		String sql = "UPDATE COMPANY "
				+ "	SET "
				+ "		COM_TEL = '" + comTel + "' "
				+ " WHERE "
				+ "		COM_ID = '" + CompanyService.LoginCompany.get("COM_ID") + "' ";
		
		return jdbc.update(sql);
	}
	
	// 사장님 연락처 수정
	public int updateCompanyHp(String comHp) throws Exception {
		String sql = "UPDATE COMPANY "
				+ "	SET "
				+ "		COM_HP = '" + comHp + "' "
				+ " WHERE "
				+ "		COM_ID = '" + CompanyService.LoginCompany.get("COM_ID") + "' ";
		
		return jdbc.update(sql);
	}
	
	
	// 업체 비밀번호 수정
	public int updateCompanyPw(String comPw) throws Exception {
		String sql = "UPDATE COMPANY "
				+ "	SET "
				+ "		COM_PW = '" + comPw + "' "
				+ " WHERE "
				+ "		COM_ID = '" + CompanyService.LoginCompany.get("COM_ID") + "' ";
		
		return jdbc.update(sql);
	}
}