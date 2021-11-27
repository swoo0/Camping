package Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DAO.ReviewDAO;
import Main.MainHome;
import Util.ScanUtil;

public class ReviewService {

	private static ReviewService instance;
	private ReviewDAO reviewDAO = ReviewDAO.getInstance();

	private ReviewService() {
	}

	public static ReviewService getInstance() {
		if (instance == null) {
			instance = new ReviewService();
		}
		return instance;
	}

	// 리뷰 작성을 위한 회원의 예약목록 출력
	public void memberBookName() throws Exception {
		System.out.println("===============================================================");
		System.out.println(MemberService.LoginMember.get("MEM_ID") + "님이 이용하신 내역");

		List<Map<String, Object>> list = new ArrayList<>();
		list = reviewDAO.memberBookName();

		int index = 1;
		if (list.size() == 0) {
			System.out.println("이용하신 내역이 없습니다.");
			new MainHome().backMemHomeView();
		} else {
			for (Map<String, Object> bookMember : list) {
				System.out.println("예약 No. " + index++);
				System.out.println("예약번호 : " + (String) bookMember.get("BOOK_ID"));
				System.out.println("상품코드 : " + (String) bookMember.get("PROD_ID"));
				System.out.println("상품명 : " + (String) bookMember.get("PROD_NAME"));
				System.out.println("예약날짜 : " + (String) bookMember.get("BOOK_DATE"));
				System.out.println("===============================================================");
			}
			writeReview();
		}
	}

	// 리뷰 작성
	public void writeReview() throws Exception {
		
		
		List<Map<String, Object>> list = new ArrayList<>();
		list = reviewDAO.memberBookName();

		
		System.out.println("===============================================================");
		System.out.print("몇번째 상품의 리뷰를 작성하시겠습니까? >> ");
		int select = ScanUtil.nextInt();

		Object selectBook = list.get(select - 1).get("BOOK_ID");

		System.out.println("========================= 리뷰 수정 ===========================");
		System.out.print("제목 >> ");
		String reviewTitle = ScanUtil.nextLine();
		System.out.print("내용 >> ");
		String reviewContent = ScanUtil.nextLine();
		System.out.print("평점 입력 부탁드려용(0.1 ~ 5.0) >> ");
		double rate = Double.parseDouble(ScanUtil.nextLine());

		Map<String, Object> reviewList = new HashMap<>();
		reviewList.put("REVIEW_TITLE", reviewTitle);
		reviewList.put("REVIEW_CONTENT", reviewContent);
		reviewList.put("REVIEW_GRADE", rate);
		
		int bb = Integer.parseInt(String.valueOf(list.get(select - 1).get("REVIEW_WRITTEN")));
		
		int result = 0; // 리뷰삽입 
		int a = 0; //마일리지 적립
		int writtenReview = 0; ////작성여부 1로 변경해주는 메소드
		
		
		if(bb == 0) {
			result = reviewDAO.insertReview(reviewList, selectBook);
			a = reviewDAO.writeMileage(selectBook);
			writtenReview = reviewDAO.writtenReview(selectBook); 
			if(result > 0 && a > 0 && writtenReview > 0) {
				System.out.println("리뷰 작성 완료!");
				System.out.println("소중한 리뷰 감사합니다~");
				System.out.println("리뷰작성 마일리지가 적립되었습니다.");
			}
		} else if(bb == 1) {
			result = reviewDAO.insertReview(reviewList, selectBook);
			System.out.println("리뷰 작성 완료!");
			System.out.println("소중한 리뷰 감사합니다~");
		} else if(bb == 2) {
			new MainHome().backMemHomeView();
		}
		new MainHome().backMemHomeView();
	}

	// 리뷰 수정 및 삭제
	public void updateOrDeleteReview() throws Exception {
		List<Map<String, Object>> list = new ArrayList<>();
		list = reviewDAO.reviewList();
		int index = 1;
		if (list.size() == 0) {
			System.out.println("작성하신 리뷰 내역이 없습니다.");
			new MainHome().backMemHomeView();
		} else {
			System.out.println("===============================================================");
			System.out.println("작성하신 리뷰 목록입니다.");
			for (Map<String, Object> reviewlist : list) {
				System.out.println("리뷰 No." + index++);
				System.out.println("리뷰제목 : " + reviewlist.get("REVIEW_TITLE"));
				System.out.println("리뷰내용 : " + reviewlist.get("REVIEW_CONTENT"));
				System.out.println("평점 : " + reviewlist.get("REVIEW_GRADE"));
				System.out.println("===============================================================");
			}
		}

		System.out.println("1. 리뷰수정   2. 리뷰삭제  3. 뒤로가기");
		System.out.print("원하시는 서비스를 선택해주세요 >> ");
		int updateDeleteSelect = ScanUtil.nextInt();

		switch (updateDeleteSelect) {
		case 1:
			System.out.println("===============================================================");
			System.out.print("몇번째 리뷰를 수정하시겠습니까? >> ");
			int select1 = ScanUtil.nextInt();

			Object selectBook1 = list.get(select1 - 1).get("BOOK_ID");

			System.out.println("=========================== 리뷰 수정 =========================");
			System.out.print("제목 >> ");
			String reviewTitle = ScanUtil.nextLine();
			System.out.print("내용 >> ");
			String reviewContent = ScanUtil.nextLine();
			System.out.print("평점 입력 부탁드려용(0.1 ~ 5.0) >> ");
			double rate = Double.parseDouble(ScanUtil.nextLine());

			int result1 = reviewDAO.updateReview(reviewTitle, reviewContent, rate, selectBook1);

			if (result1 > 0) {
				System.out.println("리뷰 수정 완료!");
			} else {
				System.out.println("리뷰 수정에 실패했습니다.");
			}
			break;

		case 2:
			System.out.println("===============================================================");
			System.out.print("몇번째 리뷰를 삭제하시겠습니까? >> ");
			int select2 = ScanUtil.nextInt();
			Object selectBook2 = list.get(select2 - 1).get("BOOK_ID");

			int result2 = reviewDAO.deleteReview(selectBook2);

			if (result2 > 0) {
				System.out.println("리뷰 삭제 완료!");
			} else {
				System.out.println("리뷰 삭제에 실패했습니다.");
			}
			break;
		case 3:
			break;
		}
		new MainHome().backMemHomeView();
	}

	// 업체 리뷰 확인
	public void prodReview() throws Exception {
		List<Map<String, Object>> list = new ArrayList<>();
		list = reviewDAO.prodCheck();

		if (list.size() == 0) {
			System.out.println("상품이 존재하지 않습니다.");
		} else {
			System.out.println();
			System.out.println("========================= 리뷰 조회 ===========================");
			int i = 1;
			for (Map<String, Object> prodList : list) {
				System.out.println(i++ + ". " + prodList.get("PROD_NAME"));
			}

			System.out.println("===============================================================");
			System.out.println("조회 하실 상품을 입력해주세요.");
			System.out.print("상품번호 >> ");
			int reviewProd = ScanUtil.nextInt();
			System.out.println();

			String selectProd = (String) list.get(reviewProd - 1).get("PROD_NAME");
			list = reviewDAO.prodReviewCheck(selectProd);
			if (list.size() == 0) {
				System.out.println("리뷰가 존재하지 않습니다.");
				new MainHome().backComHomeViewSelect();
			} else {
				i = 1;
				System.out.println("'" + selectProd + "'" + "의 리뷰입니다.");
				System.out.println("===============================================================");
				for (Map<String, Object> prodReview : list) {
					System.out.println("No." + i++);
					System.out.println("◆아이디 : " + (String) prodReview.get("MEM_ID"));
					System.out.println("♧제목 : " + (String) prodReview.get("REVIEW_TITLE"));
					System.out.println("♣내용 : " + (String) prodReview.get("REVIEW_CONTENT"));
					System.out.print("★평점 : " + prodReview.get("REVIEW_GRADE"));
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
			new MainHome().backComHomeViewSelect();
		}

	}
}