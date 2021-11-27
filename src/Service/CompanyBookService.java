package Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DAO.CompanyBookDAO;
import Main.MainHome;
import Util.ScanUtil;

public class CompanyBookService {

	private static CompanyBookService instance;
	private CompanyBookDAO companyBookDAO = CompanyBookDAO.getInstacne();

	private CompanyBookService() {
	}

	public static CompanyBookService getInstance() {
		if (instance == null) {
			instance = new CompanyBookService();
		}
		return instance;
	}

	// 업체 예약 내역 확인
	public void companyBookCheck() throws Exception {
		System.out.println("========================= 예약 내역 ===========================");

		List<Map<String, Object>> list = new ArrayList<>();
		list = companyBookDAO.companyBookList();

		int index = 1;
		if (list.size() == 0) {
			System.out.println("예약 내역이 없습니다.");
			new MainHome().backComHomeView();
		} else {
			for (Map<String, Object> companyBook : list) {
				System.out.println("예약 No. : " + index++);
				System.out.println("예약번호 : " + (String) companyBook.get("BOOK_ID"));
				System.out.println("예약날짜 : " + (String) companyBook.get("BOOK_DATE"));
				System.out.println("상품명   : " + (String) companyBook.get("PROD_NAME"));
				System.out.println("회원명   : " + (String) companyBook.get("MEM_NAME"));
				System.out.println("연락처   : " + (String) companyBook.get("MEM_HP"));
				System.out.println("===============================================================");

			}
			companyBookCancel();
		}
	}

	// (업체) 선택한 예약 내역 취소
	public void companyBookCancel() throws Exception {
		System.out.println("1. 예약 취소   2. 뒤로가기");
		System.out.print("원하시는 서비스를 입력하세요 >> ");
		int menu = ScanUtil.nextInt();
		switch (menu) {
		case 1:
			while (true) {
				List<Map<String, Object>> list = new ArrayList<>();
				list = companyBookDAO.companyBookList();
				System.out.print("삭제할 예약 No. 를 입력하세요 >> ");
				int selectNo = ScanUtil.nextInt();
				System.out.println("정말 삭제하시겠습니까? ");
				System.out.print("Y/N >> ");
				String selectYesNo = ScanUtil.nextLine();

				if (selectYesNo.equals("y") || selectYesNo.equals("Y")) {
					int updateCompany = companyBookDAO.deleteBook(list.get(selectNo - 1).get("BOOK_ID"));
					if (updateCompany > 0) {
						System.out.println("예약 정보가 삭제되었습니다.");
						break;
					} else {
						System.out.println("예약 정보가 없거나 잘못 입력하셨습니다.");
						continue;
					}
				} else if (selectYesNo.equals("n") || selectYesNo.equals("N")) {
					System.out.println("취소 되었습니다.");
					break;
				} else {
					System.out.println("잘못 입력하였습니다.");
					continue;
				}
			}
		case 2:
			System.out.println();
			new MainHome().backComHomeView();
			break;
		}
	}
}
