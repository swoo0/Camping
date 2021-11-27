package Main;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import DAO.MemberDAO;
import Service.MemberService;
import Service.CompanyService;
import Util.ScanUtil;

public class MainHome {

	private MemberDAO memberDao = MemberDAO.getInstance();
	private MemberService memberService = MemberService.getInstance();
	private CompanyService companyService = CompanyService.getInstance();

	public MainHome() throws Exception {
	}

	public void loginWindow() throws Exception {
		String[] str = { "놀", "러", "가", "고", "싶", "다" };

		for (int i = 0; i < 63; i++) {
//			Thread.sleep(30);
			System.out.print("=");
		}

		System.out.println();
		System.out.print("|");

		for (int i = 0; i < str.length; i++) {
//			Thread.sleep(500);
			System.out.print("     " + str[i] + "   ");
		}

		System.out.print(" |");
		System.out.println();

		for (int i = 0; i < 63; i++) {
//			Thread.sleep(30);
			System.out.print("=");
		}

		System.out.println();
		System.out.print("|\t\t\t\t\t\t\t      |");
		System.out.println();
		System.out.println("| \t\t        1. 회원 로그인 \t\t\t      |");
		System.out.print("|\t\t\t\t\t\t\t      |");
		System.out.println();
		System.out.println("| \t\t        2. 업체 로그인 \t\t\t      |");
		System.out.print("|\t\t\t\t\t\t\t      |");
		System.out.println();
		System.out.println("| \t\t        3. 회원가입\t\t\t      |");
		System.out.print("|\t\t\t\t\t\t\t      |");
		System.out.println();
		System.out.println("| \t\t        4. 종료하기 \t\t\t      |");
		System.out.print("|\t\t\t\t\t\t\t      |");

		System.out.println();

		for (int i = 0; i < 63; i++) {
//			Thread.sleep(30);
			System.out.print("=");
		}

		System.out.println();

		try {
			menu: while (true) {
				System.out.print("어떤 서비스를 이용하시겠습니까? >> ");
				int menu = ScanUtil.nextInt();
				switch (menu) {
				case 1:
					memberService.memberLogin();
					break;
				case 2:
					companyService.companylogin();
					break;
				case 3:
					memberSignUp();
					break;
				case 4:
					System.out.println("이용해주셔서 감사합니다.");
					break;
				default:
					System.out.println("올바른 입력형식이 아닙니다!");
					continue;
				}
				break menu;
			}
		} catch (Exception e) {
			System.out.println("올바른 입력형식이 아닙니다!");
			loginWindow();
		}

	}

	// 아이디 중복체크및 유효성 체크
	public String userIdCheck() throws Exception {
		String id = "";
		while (true) {
			// 중복여부판단 false면 중복
			boolean overlap = true;
			// 유효성 판별 false면 위배
			boolean effective = false;
			// 영어대소문자와 영어 4글자 이상 10글자 이하
			String regexId = "[a-zA-Z-_0-9]{4,10}";

			System.out.print("아이디 >> ");
			id = ScanUtil.nextLine();
			Map<String, Object> user = memberDao.memberIdCheck(id);

			if (user != null) {
				overlap = false;
			}

			Pattern pattern = Pattern.compile(regexId);
			Matcher matcher = pattern.matcher(id);
			effective = matcher.matches();

			if (!effective) {
				System.out.println("아이디는 영문자 대소문자와 숫자 4~10글자로 입력하셔야 합니다!");
			} else if (!overlap) {
				System.out.println("이미 존재하는 아이디 입니다.");
			} else {
				break;
			}
		}
		return id;
	}

	// 비밀번호 확인
	public String userPwCheck() {
		String password = "";
		String checkPassword = "";

		while (true) {
			System.out.print("비밀번호 입력 >> ");
			password = ScanUtil.nextLine();
			System.out.print("비밀번호 확인 위해 재입력 >> ");
			checkPassword = ScanUtil.nextLine();

			if (password.equals(checkPassword)) {
				break;
			} else {
				System.out.println("비밀번호가 일치하지 않습니다! ");
				continue;
			}
		}
		return password;
	}

	// 전화번호 유효성 검사
	public String userHpCheck() {
		String hp = "";
		re: while (true) {
			boolean effective = false;
			String regexHp = "^01(?:0|1[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$";
			System.out.print("전화번호 >> ");
			hp = ScanUtil.nextLine();

			Pattern pattern = Pattern.compile(regexHp);
			Matcher matcher = pattern.matcher(hp);
			effective = matcher.matches();

			if (!effective) {
				System.out.println("올바른 형식이 아닙니다. ex)010-????-???? ");
				continue re;
			} else {
				break;
			}
		}
		return hp;
	}

	// 쿠폰 지급 여부
	public void paymentsCoupon(String userId, int result) throws Exception {
		if (result > 0) {
			System.out.println("회원가입 성공!");
			System.out.println("회원가입 기념 10% 할인 쿠폰이 지급되었습니다^^");
			System.out.println();
			System.out.println("===============================================");
			System.out.println("|                                             |");
			System.out.println("|      ││   00              /                 |");
			System.out.println("|      ││  0  0       οο   /  οο     할 인    |");
			System.out.println("|      ││  0  0       οο  /   οο     쿠 폰    |");
			System.out.println("|      ││   00           /                    |");
			System.out.println("|                                             |");
			System.out.println("===============================================");
			System.out.println();
			memberDao.plusCoupon(userId);
			memberService.memberLogin();
		}
	}

	// 회원 가입
	public void memberSignUp() throws Exception {
		System.out.println("========================== 회원 가입 ==========================");

		String userId = userIdCheck();
		String userPw = userPwCheck();
		System.out.print("이름 >> ");
		String userName = ScanUtil.nextLine();
		System.out.print("주소 >> ");
		String userAdd = ScanUtil.nextLine();
		String userHp = userHpCheck();

		Map<String, Object> signUpMember = new HashMap<>();
		signUpMember.put("MEM_ID", userId);
		signUpMember.put("MEM_PW", userPw);
		signUpMember.put("MEM_NAME", userName);
		signUpMember.put("MEM_ADD", userAdd);
		signUpMember.put("MEM_HP", userHp);
		int result = memberDao.insertUser(signUpMember);
		paymentsCoupon(userId, result);

	}

	// 회원 홈화면으로 이동
	public void backMemHomeView() throws Exception {
		memberService.memberHomeView();
	}

	// 회원 홈화면으로 선택 이동
	public void backMemHomeViewSelect() throws Exception {
		System.out.print("목록으로 돌아가시려면 1번을 입력해주세요 >> ");
		int select = ScanUtil.nextInt();
		if (select == 1) {
			memberService.memberHomeView();
		}
	}

	// 업체 홈화면으로 이동
	public void backComHomeView() throws Exception {
		companyService.companyHomeView();
	}

	// 업체 홈화면으로 선택 이동
	public void backComHomeViewSelect() throws Exception {
		while (true) {
			System.out.print("q 뒤로가기 >> ");
			String select = ScanUtil.nextLine();
			if (select.equals("q") || select.equals("ㅂ")) {
				companyService.companyHomeView();
				break;
			} else {
				System.out.println("잘못 입력 하셨습니다.");
				continue;
			}
		}
	}

	// 업체 상품관리 홈 화면 선택 이동
	public void backProdHomeViewSelect() throws Exception {
		while (true) {
			System.out.print("q 뒤로가기 >> ");
			String select = ScanUtil.nextLine();
			if (select.equals("q") || select.equals("ㅂ")) {
				companyService.modifyProdView();
				break;
			} else {
				System.out.println("잘못 입력 하셨습니다.");
				continue;
			}
		}
	}

	// 업체 마이페이지 화면 선택 이동
	public void backComPageViewSelect() throws Exception {
		while (true) {
			System.out.print("q 뒤로가기 >> ");
			String select = ScanUtil.nextLine();
			if (select.equals("q") || select.equals("ㅂ")) {
				companyService.modifyCompanyView();
				break;
			} else {
				System.out.println("잘못 입력 하셨습니다.");
				continue;
			}
		}
	}

}