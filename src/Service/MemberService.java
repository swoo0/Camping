package Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DAO.MemberBookDAO;
import DAO.MemberDAO;
import Main.MainHome;
import Util.ScanUtil;

public class MemberService {

	private static MemberService instance;

	public static Map<String, Object> LoginMember;

	private MemberDAO memberDAO = MemberDAO.getInstance();
	private MemberBookDAO memberBookDAO = MemberBookDAO.getInstance();
	private ReviewService reviewservice = ReviewService.getInstance();
	private MemberBookService memberBookService = MemberBookService.getInstance();

	private MemberService() {
	}

	public static MemberService getInstance() {
		if (instance == null) {
			instance = new MemberService();
		}
		return instance;
	}

	// 회원 로그인
	public void memberLogin() throws Exception {
		while (true) {
			System.out.print("아이디 >> ");
			String userId = ScanUtil.nextLine();
			System.out.print("비밀번호 >> ");
			String userPw = ScanUtil.nextLine();
			Map<String, Object> user = memberDAO.checkUser(userId, userPw);
			if (user == null) {
				System.out.println("X 아이디 또는 비밀번호를 잘못 입력하셨습니다. X");
			} else {
				System.out.println("로그인 성공!");
				LoginMember = user;
				memberHomeView();
				break;
			}
		}
	}

	// 로그인시 회원 홈 메뉴
	public void memberHomeView() throws Exception {
		System.out.println("===================== 야무지게 놀아보자~~ =====================");
		System.out.println(" 1. 예약하기 ");
		System.out.println(" 2. 예약목록 확인 및 취소 ");
		System.out.println(" 3. 리뷰 작성 ");
		System.out.println(" 4. 리뷰 확인/수정/삭제 ");
		System.out.println(" 5. 마이페이지");
		System.out.println(" 6. 로그아웃 ");
		System.out.println("===============================================================");
		System.out.print("환영합니다! " + LoginMember.get("MEM_ID") + "님 어떤 서비스를 이용하시겠습니까 >> ");
		int menu = ScanUtil.nextInt();
		switch (menu) {
		case 1:
			memberBookService.reservationProd();
			break;
		case 2:
			memberBookService.memberBookCheck();
			break;
		case 3:
			reviewservice.memberBookName();
			break;
		case 4:
			reviewservice.updateOrDeleteReview();
			break;
		case 5:
			memberInformation();
			myPageView();
			break;
		case 6:
			System.out.println("===============================================================");
			System.out.println("로그아웃이 정상적으로 되었습니다!");
			LoginMember = null;
			new MainHome().loginWindow();
			break;
		}
	}

	// 회원정보 수정 및 회원 탈퇴 뷰
	public void myPageView() throws Exception {
		System.out.println("==================== 회원정보 수정 및 탈퇴 ====================");
		System.out.println("===============================================================");
		System.out.println("1. 회원정보 수정     2. 회원탈퇴     3. 뒤로가기");
		System.out.print("어떤 서비스를 이용하시겠습니까 >> ");
		int select = ScanUtil.nextInt();

		switch (select) {
		case 1:
			modifyUserView();
			break;
		case 2:
			bookCheck();
			break;
		case 3:
			new MainHome().backMemHomeView();
			break;
		}
	}

	// 예약 여부 확인
	public void bookCheck() throws Exception {

		List<Map<String, Object>> list = new ArrayList<>();
		list = memberBookDAO.memberBookList();

		if (list.size() == 0) {
			deleteMember();
		} else {
			System.out.println("===============================================================");
			System.out.println(" X 예약 목록이 존재합니다 X");
			System.out.println("탈퇴를 원하시면 먼저 예약을 취소해 주세요!");
			new MainHome().backMemHomeView();
		}
	}

	// 회원 탈퇴
	public void deleteMember() throws Exception {
		System.out.println("정말 탈퇴하시겠습니까? ");
		System.out.print("Y/N >> ");
		String selectYesNo = ScanUtil.nextLine();

		if (selectYesNo.equals("y") || selectYesNo.equals("Y")) {
			userGetPassword();
		} else if (selectYesNo.equals("n") || selectYesNo.equals("N")) {
			memberHomeView();
		} else {
			System.out.println("잘못 입력하였습니다.");
			memberHomeView();
		}

	}

	// 로그인한 사용자가 입력한 비밀번호와 db에 담긴 비밀번호를 체크
	public void userGetPassword() throws Exception {
		while (true) {
			System.out.print("회원 탈퇴를 위해 비밀번호 재입력 해주세요 >> ");
			String pw = ScanUtil.nextLine();
			if (pw.equals(LoginMember.get("MEM_PW"))) {
				geDeleteCheck();
				break;
			} else {
				System.out.println("비밀번호가 일치 하지 않습니다.");
			}
		}
	}

	// MemberDAO의 deleteUser()메서드로 부터 쿼리를 날리고 결과를 리턴 받음
	// update 성공하면 1 실패하면 0
	public void geDeleteCheck() throws Exception {
		int result = memberDAO.deleteUser(LoginMember);

		if (result > 0) {
			System.out.print("회원탈퇴 진행중");
			for (int i = 0; i < 4; i++) {
				Thread.sleep(1000);
				System.out.print(".");
			}
			System.out.println();
			System.out.println("회원탈퇴 성공! 로그인 화면으로 돌아갑니다.");
			LoginMember = null;
			new MainHome().loginWindow();
		} else {
			System.out.println("===============================================================");
			System.out.println("회원탈퇴가 정상적으로 이뤄지지 않았습니다.");
		}
	}

	// 회원정보 불러오기
	public void memberInformation() throws Exception {
		List<Map<String, Object>> memberInfo = new ArrayList<>();
		memberInfo = memberDAO.memberList();

		System.out.println("===============================================================");
		for (Map<String, Object> info : memberInfo) {
			System.out.println("회원번호 : " + info.get("MEM_NO"));
			System.out.println("회원아이디 : " + info.get("MEM_ID"));
			System.out.println("회원비밀번호 : " + info.get("MEM_PW"));
			System.out.println("회원이름 : " + info.get("MEM_NAME"));
			System.out.println("회원주소 : " + info.get("MEM_ADD"));
			System.out.println("회원전화번호 : " + info.get("MEM_HP"));
			System.out.println("마일리지 : " + info.get("MEM_MILEAGE") + " 점");
			System.out.println("쿠폰 : " + info.get("MEM_COUPON") + " 개");
		}
	}

	// 회원정보 수정 화면
	public void modifyUserView() throws Exception {
		System.out.println("===============================================================");
		System.out.println(" 1. 비밀번호 ");
		System.out.println(" 2. 이름 ");
		System.out.println(" 3. 주소 ");
		System.out.println(" 4. 전화번호 ");
		System.out.print("어떤 정보를 수정하시겠습니까 >> ");

		int menu = ScanUtil.nextInt();
		switch (menu) {
		case 1:
			modifyUserPw();
			memberHomeView();
			break;
		case 2:
			modifyUserName();
			memberHomeView();
			break;
		case 3:
			modifyUserAddress();
			memberHomeView();
			break;
		case 4:
			modifyUserHp();
			memberHomeView();
			break;
		}
	}

	// 회원 정보 수정
	public void modifyUser() throws Exception {
		System.out.print("회원정보 수정을 위해 비밀번호를 다시 입력 해주세요 >> ");
		String pw = ScanUtil.nextLine();
		if (pw.equals(LoginMember.get("MEM_PW"))) {
			modifyUserView();
		} else {
			System.out.println("===============================================================");
			System.out.println("비밀번호가 일치 하지 않습니다.");
			memberHomeView();
		}

	}

	// 회원 비밀번호 수정
	public void modifyUserPw() throws Exception {
		System.out.print("변경하실 비밀번호를 입력해주세요 >> ");
		String pw = ScanUtil.nextLine();
		int resultPw = memberDAO.updateUserPw(pw);

		if (resultPw > 0) {
			System.out.println("비밀번호 변경이 완료되었습니다! ");
		} else {
			System.out.println(" X 비밀번호 변경 실패 X");
		}
	}

	// 회원 이름 변경
	public void modifyUserName() throws Exception {
		System.out.print("변경하시려는 성함을 입력해주세요 >>");
		String name = ScanUtil.nextLine();
		int resultName = memberDAO.updateUserName(name);

		if (resultName > 0) {
			System.out.println(" 변경이 완료되었습니다! ");
		} else {
			System.out.println(" X 변경 실패 X");
		}
	}

	// 회원 주소 변경
	public void modifyUserAddress() throws Exception {
		System.out.print("변경하시려는 주소를 입력해주세요 >> ");
		String address = ScanUtil.nextLine();
		int resultAddress = memberDAO.updateUserAddress(address);

		if (resultAddress > 0) {
			System.out.println(" 변경이 완료되었습니다! ");
		} else {
			System.out.println(" X 변경 실패 X");
		}
	}

	// 회원 전화번호 변경
	public void modifyUserHp() throws Exception {
		System.out.print("변경하시려는 전화번호를 입력해주세요 >> ");
		String hp = new MainHome().userHpCheck();
		int resultHp = memberDAO.updateUserAddress(hp);

		if (resultHp > 0) {
			System.out.println(" 변경이 완료되었습니다! ");
		} else {
			System.out.println(" X 변경 실패 X");
		}
	}

}
