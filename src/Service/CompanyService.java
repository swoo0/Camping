package Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DAO.CompanyDAO;
import Main.MainHome;
import Util.ScanUtil;

public class CompanyService {

	private static CompanyService instance;

	public static Map<String, Object> LoginCompany;
	private CompanyDAO companyDAO = CompanyDAO.getInstance();
	private CompanyBookService companyBookService = CompanyBookService.getInstance();
	private ReviewService reviewService = ReviewService.getInstance();

	private CompanyService() {
	}

	public static CompanyService getInstance() {
		if (instance == null) {
			instance = new CompanyService();
		}
		return instance;
	}

	// 업체 로그인
	public void companylogin() throws Exception {
		while (true) {
			System.out.print("아이디 >> ");
			String userId = ScanUtil.nextLine();
			System.out.print("비밀번호 >> ");
			String userPw = ScanUtil.nextLine();
			Map<String, Object> company = companyDAO.checkUser(userId, userPw);
			if (company == null) {
				System.out.println("X 아이디 또는 비밀번호를 잘못 입력하셨습니다. X");
			} else {
				System.out.println("로그인 성공!");
				LoginCompany = company;
				companyHomeView();
				break;
			}
		}
	}

	public void companyHomeView() throws Exception {
		System.out.println("==================== 반갑습니다 사장님^^ ======================");
		System.out.println(" 1. 예약 확인 및 취소 ");
		System.out.println(" 2. 리뷰 조회 ");
		System.out.println(" 3. 상품 관리");
		System.out.println(" 4. 업체페이지 ");
		System.out.println(" 5. 로그아웃 ");
		System.out.println("===============================================================");
		System.out.print("환영합니다! " + LoginCompany.get("COM_ID") + "님 어떤 서비스를 이용하시겠습니까? >> ");
		int menu = ScanUtil.nextInt();
		switch (menu) {
		case 1:
			companyBookService.companyBookCheck();
			companyHomeView();
			break;
		case 2:
			reviewService.prodReview();
			new MainHome().backComHomeViewSelect();
			break;
		case 3:
			prodPwCheck();
			break;
		case 4:
			mypagePwCheck();
			break;
		case 5:
			System.out.println("===============================================================");
			System.out.println("로그아웃이 정상적으로 되었습니다!");
			LoginCompany = null;
			new MainHome().loginWindow();
			break;
		}
	}

	// 상품 관리 비밀번호 재확인
	public void prodPwCheck() throws Exception {
		System.out.print("비밀번호를 다시 입력 해주해요 >> ");
		String pw = ScanUtil.nextLine();
		if (pw.equals(LoginCompany.get("COM_PW"))) {
			prodCheck();
		} else {
			System.out.println("===============================================================");
			System.out.println("비밀번호가 일치하지 않습니다.");
			companyHomeView();
		}
	}

	// 보유 상품 확인
	public void prodCheck() throws Exception {
		
		List<Map<String, Object>> list = new ArrayList<>();
		list = companyDAO.prodList();
		int index = 1;
		if (list.size() == 0) {
			System.out.println("보유 중인 상품이 없습니다.");
			System.out.println("1. 상품 추가  2. 뒤로가기 ");
			System.out.println("원하는 서비스를 입력해주세요. ");
			int menu = ScanUtil.nextInt();
			switch (menu) {
			case 1:
				addProd();
				break;
			case 2:
				modifyProdView();
				break;
			}
		} else {
			System.out.println("========================= 상품 관리 ===========================");
			for (Map<String, Object> prodList : list) {
				System.out.println("상품 No. " + index++);
				System.out.println("상품코드 : " + prodList.get("PROD_ID"));
				System.out.println("상품명 : " + prodList.get("PROD_NAME"));
				System.out.println("가격 : " + prodList.get("PROD_PRICE"));
				System.out.println("===============================================================");
			}
			modifyProdView();
		}
	}

	
	// 상품 관리 화면
	public void modifyProdView() throws Exception {
		System.out.println("1. 상품 추가  2. 상품 수정  3. 뒤로가기");
		System.out.println("===============================================================");
		System.out.print("원하시는 서비스를 입력해주세요 >> ");
		int menu = ScanUtil.nextInt();
		switch (menu) {
		case 1:
			addProd();
			break;
		case 2:
			updateProd();
			break;
		case 3:
			companyHomeView();
			break;
		}
	}

	// 상품 추가
	public void addProd() throws Exception {
		List<Map<String, Object>> list = new ArrayList<>();
		list = companyDAO.lprodCheck();
		int i = 1;

		System.out.println("========================== 상품 추가 ==========================");
		System.out.println();
		System.out.println("========================== 상품 분류 ==========================");
		for (Map<String, Object> lprodList : list) {
			System.out.println(i++ + ". " + (String) lprodList.get("LPROD_NAME"));
		}
		System.out.println("===============================================================");
		System.out.print("분류 번호 >> ");
		int lprodSelect = ScanUtil.nextInt();

		Object lProd = list.get(lprodSelect - 1).get("LPROD_ID");

		System.out.print("상품명 >> ");
		String prodName = ScanUtil.nextLine();
		System.out.print("가격 >> ");
		String prodPrice = ScanUtil.nextLine();

		Map<String, Object> addProd = new HashMap<>();
		addProd.put("LPROD_ID", lProd);
		addProd.put("PROD_NAME", prodName);
		addProd.put("PROD_PRICE", prodPrice);
		int result = companyDAO.insertProd(addProd);

		if (result > 0) {
			System.out.println("상품 추가 성공 ! ");
			prodCheck();
		} else {
			System.out.println("X 상품 추가 실패 X");
			prodCheck();
		}
	}

	// 상품 정보 수정
	public void updateProd() throws Exception {
		List<Map<String, Object>> list = new ArrayList<>();
		list = companyDAO.prodList();

		System.out.print("수정 하실 상품 No.를 입력해주세요 >> ");
		int select = ScanUtil.nextInt();
		Object selectProdId = list.get(select - 1).get("PROD_ID");
		if (select > 0) {
			System.out.println("======================= 상품 정보 수정 ========================");
			System.out.print("상품명 >> ");
			String prodName = ScanUtil.nextLine();
			System.out.print("가격 >> ");
			String prodPrice = ScanUtil.nextLine();

			int result = companyDAO.updateProdNP(prodName, prodPrice, selectProdId);

			if (result > 0) {
				System.out.println("상품 정보 수정 완료 !");
				prodCheck();
				}
			} else {
				System.out.println("상품 정보 수정에 실패했습니다.");
				new MainHome().backProdHomeViewSelect();
		}
		

	}

	// 업체 비밀번호 재확인
	public void mypagePwCheck() throws Exception {
		System.out.print("비밀번호를 다시 입력 해주해요 >> ");
		String pw = ScanUtil.nextLine();
		if (pw.equals(LoginCompany.get("COM_PW"))) {
			CompanyCheck();
		} else {
			System.out.println("===============================================================");
			System.out.println("비밀번호가 일치하지 않습니다.");

		}
	}

	// 업체 정보 확인
	public void CompanyCheck() throws Exception {
		List<Map<String, Object>> list = new ArrayList<>();
		list = companyDAO.companyList();

		if (list.size() == 0) {
			System.out.println("업체 정보가 존재하지 않습니다.");
			modifyCompanyView();
		} else {
			for (Map<String, Object> comList : list) {
				System.out.println("========================== 업체 정보 ==========================");
				System.out.println("업체명 : " + comList.get("COM_NAME"));
				System.out.println("업체주소 : " + comList.get("COM_ADD"));
				System.out.println("업체전화번호 : " + comList.get("COM_TEL"));
				System.out.println("사장님 연락처 : " + comList.get("COM_HP"));
				System.out.println("===============================================================");
			}
		}
		System.out.println("1. 정보 수정 / 2. 뒤로가기 ");
		System.out.print("원하시는 서비스를 선택해주세요 >> ");
		int menu = ScanUtil.nextInt();
		switch (menu) {
		case 1:
			modifyCompanyView();
			break;
		case 2:
			companyHomeView();
			break;
		}
	}

	// 업체 정보 수정 화면
	public void modifyCompanyView() throws Exception {
		System.out.println("======================= 업체 정보 수정 ========================");
		System.out.println(" 1. 업체명 변경");
		System.out.println(" 2. 업체 주소 변경");
		System.out.println(" 3. 업체 전화번호 변경");
		System.out.println(" 4. 사장님 연락처 변경");
		System.out.println(" 5. 비밀번호 변경 ");
		System.out.println(" 6. 뒤로가기");
		System.out.println("===============================================================");
		System.out.print("원하는 서비스를 입력해주세요 >> ");

		int menu = ScanUtil.nextInt();
		switch (menu) {
		case 1:
			modifyCompanyName();
			new MainHome().backComPageViewSelect();
			break;
		case 2:
			modifyCompanyAdd();
			new MainHome().backComPageViewSelect();
			break;
		case 3:
			modifyCompanyTel();
			new MainHome().backComPageViewSelect();
		case 4:
			modifyCompanyHp();
			new MainHome().backComPageViewSelect();
			break;
		case 5:
			modifyCompanyPw();
			new MainHome().backComPageViewSelect();
			break;
		case 6:
			companyHomeView();
			break;
		}

	}

	// 업체 이름 변경
	public void modifyCompanyName() throws Exception {
		System.out.print("변경하실 이름을 입력해주세요 >> ");
		String name = ScanUtil.nextLine();
		int resultName = companyDAO.updateCompanyName(name);

		if (resultName > 0) {
			System.out.println("변경이 완료되었습니다! ");
		} else {
			System.out.println("X 변경 실패 X");
		}
	}

	// 업체 주소 변경
	public void modifyCompanyAdd() throws Exception {
		System.out.print("변경하실 주소를 입력해주세요 >> ");
		String add = ScanUtil.nextLine();
		int resultAdd = companyDAO.updateCompanyAdd(add);

		if (resultAdd > 0) {
			System.out.println("변경이 완료되었습니다! ");
		} else {
			System.out.println("X 변경 실패 X");
		}
	}

	// 업체 전화번호 변경
	public void modifyCompanyTel() throws Exception {
		System.out.print("변경하실 전화번호를 입력해주세요 >> ");
		String tel = ScanUtil.nextLine();
		int resultTel = companyDAO.updateCompanyTel(tel);

		if (resultTel > 0) {
			System.out.println("변경이 완료되었습니다! ");
		} else {
			System.out.println("X 변경 실패 X");
		}
	}

	// 사장님 연락처 변경
	public void modifyCompanyHp() throws Exception {
		System.out.print("변경하실 사장님 연락처를 입력해주세요 >> ");
		String hp = ScanUtil.nextLine();
		int resultHp = companyDAO.updateCompanyHp(hp);
		if (resultHp > 0) {
			System.out.println("변경이 완료되었습니다! ");
		} else {
			System.out.println("X 변경 실패 X");
		}
	}

	// 업체 비밀번호 변경
	public void modifyCompanyPw() throws Exception {
		System.out.print("기존 비밀번호를 입력 해주해요 >> ");
		String oldPw = ScanUtil.nextLine();
		if (oldPw.equals(LoginCompany.get("COM_PW"))) {
			System.out.print("변경하실 비밀번호를 입력해주세요 >> ");
			String newPw = ScanUtil.nextLine();
			System.out.print("변경하실 비밀번호를 재입력해주세요 >> ");
			String reNewPw = ScanUtil.nextLine();
			if (newPw.equals(reNewPw)) {
				int resultName = companyDAO.updateCompanyPw(newPw);
				if (resultName > 0) {
					System.out.println("변경이 완료되었습니다! ");
					System.out.println("===============================================================");
					System.out.println("비밀번호 변경으로 자동 로그아웃 되었습니다.");
					LoginCompany = null;
					new MainHome().loginWindow();
				} else {
					System.out.println("X 변경 실패 X");
					modifyCompanyPw();
				}
			} else {
				System.out.println("===============================================================");
				System.out.println("비밀번호가 일치하지 않습니다.");
				modifyCompanyPw();
			}
		} else {
			System.out.println("===============================================================");
			System.out.println("비밀번호가 일치하지 않습니다.");
			modifyCompanyPw();
		}
	}

}
