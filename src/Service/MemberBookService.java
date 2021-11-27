package Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import DAO.CompanyDAO;
import DAO.MemberBookDAO;
import DAO.ReviewDAO;
import Main.MainHome;
import Util.ScanUtil;

public class MemberBookService {

	private int estimate = 0; // 최종결제
	private static MemberBookService instance;

	private MemberBookDAO memberBookDAO = MemberBookDAO.getInstance();
	private CompanyDAO companyDAO = CompanyDAO.getInstance();
	private ReviewDAO reviewDAO = ReviewDAO.getInstance();

	private MemberBookService() {
	}

	public static MemberBookService getInstance() {
		if (instance == null) {
			instance = new MemberBookService();
		}
		return instance;
	}

	// 날짜 확인 (날짜 유효성 체크)
	public String dateCheck() {
	       String date = "";
	      while(true){
	         System.out.print("예약하실 날짜를 입력해주세요 ex)20xxxxxx >> ");
	         date = ScanUtil.nextLine();

	         SimpleDateFormat dateFormatParser = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);

	         dateFormatParser.setLenient(false);
	       try{
	          dateFormatParser.parse(date);
	          break;
	      } catch (ParseException e){
	          System.out.println("올바른 형식이 아닙니다! ex)20xxxxxx");
	          continue;
	       }
	      }
	         return date;
	      }
	
	public String selectCompany() throws Exception {
		List<Map<String,Object>> companyList = new ArrayList<>();
		int index = 1;
		companyList = companyDAO.companySelect();
		
		for (Map<String, Object> map : companyList) {
			System.out.print(index++ + ".");
			System.out.printf("  %-10s \t", map.get("COM_NAME"));
			System.out.printf("  %-12s \t", map.get("COM_ADD"));
			System.out.printf("  %5s ", map.get("COM_TEL"));
			System.out.println();
		}
		System.out.println("===============================================================");
		System.out.print("원하시는 업체를 선택해주세요 >> ");
		int select = ScanUtil.nextInt();
		
		return String.valueOf(companyList.get(select-1).get("COM_ID"));
	}

	// 예약하기
	public void reservationProd() throws Exception {
	haha: while(true) {
		String date = dateCheck();
		String dateStandard = LocalDate.now().plusMonths(6).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String datePast = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

		if (Integer.parseInt(date) > Integer.parseInt(dateStandard)
				|| Integer.parseInt(date) < Integer.parseInt(datePast)) {
			System.out.println("예약가능한 날짜는 오늘부터 " + dateStandard + "까지 입니다!");
			continue haha;
		} else {
			String lprod = null;
			System.out.println("===============================================================");
			System.out.println("              1. 카라반     2. 글램핑     3.  캠핑             ");
			System.out.println("===============================================================");
			System.out.print("테마를 선택해주세요 >> ");
			int select = ScanUtil.nextInt();
			switch (select) {
			case 1:
				lprod = "LC101";
				break;
			case 2:
				lprod = "LG201";
				break;
			case 3:
				lprod = "LR301";
				break;
			}
			
			List<Map<String, Object>> list = new ArrayList<>();

			loop: while (true) {
				System.out.println("==================== 에약 가능 업체 목록 ======================");
				System.out.println("    업체명                주소                    전화번호");
				System.out.println("===============================================================");
				int i = 1;
				String comId = selectCompany();
				list = memberBookDAO.possibleProdList(date, comId ,lprod);
				if(list.size() == 0) {
					System.out.println("예약가능한 상품목록이 없습니다.");
					new MainHome().backMemHomeView();
				} else {
				for (Map<String, Object> m : list) {
					System.out.println(i++ + ". " + (String) m.get("PROD_NAME"));
				}
				System.out.println("===============================================================");
				System.out.println("1. 예약 진행   2. 리뷰 보기    3. 뒤로 가기");
				System.out.print("원하는 서비스를 입력해주세요 >> ");
				int choice = ScanUtil.nextInt();
				sw1: switch (choice) {
				case 1:
					System.out.print("예약하실 상품의 번호를 입력해주세요 >> ");
					int prodSelect = ScanUtil.nextInt();
					Object selectProd = list.get(prodSelect - 1).get("PROD_ID");
					
					int prodPrice = Integer.parseInt(String.valueOf(list.get(prodSelect - 1).get("PROD_PRICE")));
					re: while (true) {
						if (Integer.parseInt(String.valueOf(MemberService.LoginMember.get("MEM_COUPON"))) > 0) {
							System.out.print("쿠폰을 사용하시겠습니까?  1. Y 2. N >> ");
							String useCoupon = ScanUtil.nextLine();
							
							if (useCoupon.equals("y") || useCoupon.equals("Y") || useCoupon.equals("1")) {
								estimate = prodPrice - (prodPrice / 100 * 10);
								pay(selectProd, date);
								memberBookDAO.useCoupon();
								break re;
							} else if (useCoupon.equals("n") || useCoupon.equals("N") || useCoupon.equals("2")) {
								estimate = prodPrice;
								pay(selectProd, date);
								break re;
							} else {
								System.out.println("잘못 입력하셨습니다.");
								continue re;
							}
						} else {
							estimate = prodPrice;
							pay(selectProd, date);
							break re;
						}
					}
					break sw1;
				case 2:
					System.out.print("조회하실 상품의 번호를 입력해주세요 >> ");
					int searchProd = ScanUtil.nextInt();
					prodReview(String.valueOf(list.get(searchProd - 1).get("PROD_NAME")));
					continue loop;
				case 3:
					break sw1;
				}
					
				break loop;
			}
		}
		break haha;
		}
	}
	new MainHome().backMemHomeView();
	}

	// 예약할 때 상품별 리뷰 확인
	public void prodReview(String select) throws Exception {

		List<Map<String, Object>> list = new ArrayList<>();
		list = reviewDAO.prodReviewCheck(select);

		if (list.size() == 0) {
			System.out.println("리뷰가 존재하지 않습니다.");
		} else {
			int i = 0;
			System.out.println();
			System.out.println("'" + select + "'" + "의 리뷰입니다.");
			System.out.println("===============================================================");
			for (Map<String, Object> prodReview : list) {
				System.out.println("No. " + ++i);
				System.out.println(" ◆아이디: " + (String) prodReview.get("MEM_ID"));
				System.out.println(" ♧제목: " + (String) prodReview.get("REVIEW_TITLE"));
				System.out.println(" ♣내용: " + (String) prodReview.get("REVIEW_CONTENT"));
				System.out.print(" ★평점: " + prodReview.get("REVIEW_GRADE"));
				double grade = Double.parseDouble((String.valueOf(prodReview.get("REVIEW_GRADE"))));
				if (grade == 5.0) {
					System.out.println(" ★★★★★");
				} else if (grade >= 4.0) {
					System.out.println(" ★★★★☆");
				} else if (grade >= 3.0) {
					System.out.println(" ★★★☆☆");
				} else if (grade >= 2.0) {
					System.out.println(" ★★☆☆☆");
				} else if (grade >= 1.0) {
					System.out.println(" ★☆☆☆☆");
				} else if (grade < 1.0) {
					System.out.println(" ☆☆☆☆☆");
				}
				System.out.println("===============================================================");
			}
		}
	}

	// 마일리지 사용여부
	public void pay(Object selectProd, String date) throws Exception {

		if (Integer.parseInt(String.valueOf(MemberService.LoginMember.get("MEM_MILEAGE"))) == 0) {

			int a = memberBookDAO.reservationProduct(selectProd, date, estimate);
			if (a > 0) {
				System.out.println("예약이 완료되었습니다");
			} else {
				System.out.println("예약이 실패하였습니다");
			}
		} else if (Integer.parseInt(String.valueOf(MemberService.LoginMember.get("MEM_MILEAGE"))) > 0) {
			wh: while (true) {
				System.out.println(MemberService.LoginMember.get("MEM_MILEAGE") + "마일리지를 보유중입니다.");
				System.out.print("사용할 마일리지를 입력해주세요.>> ");
				int selectMileage = ScanUtil.nextInt();

				if (Integer.parseInt(String.valueOf(MemberService.LoginMember.get("MEM_MILEAGE"))) < selectMileage) {
					System.out.println("보유하신 마일리지가 사용하실 마일리지보다 작습니다.");
					continue wh;
				} else if (Integer
						.parseInt(String.valueOf(MemberService.LoginMember.get("MEM_MILEAGE"))) >= selectMileage) {
					estimate -= selectMileage;
					int a = memberBookDAO.reservationProduct(selectProd, date, estimate);
					int b = memberBookDAO.useMileage(selectMileage);
					if (a > 0 && b > 0) {
						System.out.println("예약이 완료되었습니다");
						break wh;
					} else {
						System.out.println("예약이 실패하였습니다");
						break wh;
					}
				}
			}
		}
	}

	// 회원 예약 내역 확인
	public void memberBookCheck() throws Exception {
		System.out.println("========================== 예약 내역 ==========================");
		
		List<Map<String, Object>> list = new ArrayList<>();
		list = memberBookDAO.memberBookList();

		int index = 1;
		if (list.size() == 0) {
			System.out.println("예약하신 내역이 없습니다.");
			new MainHome().backMemHomeView();
		} else {
			for (Map<String, Object> mybook : list) {
				System.out.println("예약 No. " + index++);
				System.out.println("예약번호 : " + (String) mybook.get("BOOK_ID"));
				System.out.println("회원명 : " + (String) mybook.get("MEM_NAME"));
				System.out.println("상품명 : " + (String) mybook.get("PROD_NAME"));
				System.out.println("예약날짜 : " + (String) mybook.get("BOOK_DATE"));
				System.out.println("===============================================================");
			}
			memberBookCancel();
		}
	}

	// (회원) 선택한 예약 내역 취소
	public void memberBookCancel() throws Exception {
		System.out.println("0. 예약 취소   /  1. 뒤로가기");
		System.out.print("원하시는 서비스 번호를 입력하세요 >> ");
		int select = ScanUtil.nextInt();
		if (select == 0) {
			while (true) {
				System.out.println();
				List<Map<String, Object>> list = new ArrayList<>();
				list = memberBookDAO.memberBookList();

				System.out.print("삭제할 예약 No. 를 입력하세요 >> ");
				int selectNo = ScanUtil.nextInt();
				System.out.println("정말 삭제하시겠습니까? ");
				System.out.print("Y/N >> ");
				String selectYesNo = ScanUtil.nextLine();

				if (selectYesNo.equals("y") || selectYesNo.equals("Y")) {
					System.out.println();
					int updateMember = memberBookDAO.deleteBook((String) list.get(selectNo - 1).get("BOOK_ID"));
					if (updateMember > 0) {
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
		}
		System.out.println();
		new MainHome().backMemHomeView();
	}

}
