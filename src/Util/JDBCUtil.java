package Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBCUtil {
	/*
	* 싱글톤 : 인스턴스 생성을 제한해서 하나의 인스턴스만 사용하기 위한 디자인 패턴
	* 생성자가 여러 차례 호출되더라도 실제로 생성되는 객체는 하나이며 최초 생성 이후 호출된 생성자는
	* 최초의 생성자가 생성한 객체를 리턴. 여러클래스에서 여러 인스턴스를 만들면 자원이 낭비되고
	* 버그를 발생시킬 수 있는 DB 커넥션풀, 스레드 풀 등의 경우. 
	* 이러한 이유로 오직 하나의 인스턴스만 생성하고 여러곳에서 생성된 객체를 어디에서든지 참조 할수 있도록 하는 패턴
	* 그러면 메모리측면에서도 이점이 있고 다른 클래스간 데이터 공유가 쉬움
	*/
	
	private static JDBCUtil instance;
	String url = "jdbc:oracle:thin:@localhost:1521:xe";
	String user = "CAMPING";
	String password = "1256";
	
	Connection connection = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;
	
	
	// 다른 클래스에서 객체 생성 못하게 private로 생성자를 제한
	private JDBCUtil() {
	}
	
	// 인스턴스 변수
	
	// 메모리에 할당되지 않았다면 인스턴스를 할당하는 메서드
	public static JDBCUtil getInstance() {
		if (instance == null) {
			instance = new JDBCUtil();
		}
		return instance;
	}
	
	
	//로그인한 회원 정보담는 메서드
	public Map<String,Object> selectOneMember(String sql, List<Object> memberList) throws Exception {
		
		Map<String,Object> memberIdPw = null;
		
		connection = DriverManager.getConnection(url, user, password);
		preparedStatement = connection.prepareStatement(sql);
		
		//preparedStatement에 들어온 쿼리 조건 ?에 parameter로 받은 memberList에 담긴 id와 password에
		//값을 대입하는 과정 List 타입이 Object이므로 setObject.
		for (int i = 0; i < memberList.size(); i++) {
			preparedStatement.setObject(i+1, memberList.get(i));
		}
		resultSet = preparedStatement.executeQuery();
		
		//메타데이터는 저장된 데이터 자체가 아닌 해당 데이터에 대한 정보를 갖는 데이터를 말한다.
		//메타데이터를 사용하기 위해 ResultSet 클래스의 getMetaData() 메서드를 호출하여
		//ResultSetMetaData 객체를 얻으면 해당 ResultSet과 관련된 메타데이터를 손쉽게 사용 가능
		ResultSetMetaData metaData = resultSet.getMetaData();
		
		int columnCount = metaData.getColumnCount(); // 컬럼의 개수를 반환
		
		if (resultSet.next()) {
			memberIdPw = new HashMap<>();
			for (int i = 1; i <= columnCount; i++) {
				//조건에 충족하는 id와 password가 있다면 컬럼에 데이터 삽입
				memberIdPw.put(metaData.getColumnName(i), resultSet.getObject(i));				
			}
		}
		
		connection.close();
		preparedStatement.close();
		resultSet.close();
		
		return memberIdPw; 
	}
	
	//로그인한 업체 정보담는 메서드
	public Map<String,Object> selectOneCompany(String sql, List<Object> companyList) throws Exception {
		
		Map<String,Object> companyIdPw = null;
		
		connection = DriverManager.getConnection(url, user, password);
		preparedStatement = connection.prepareStatement(sql);

		for (int i = 0; i < companyList.size(); i++) {
			preparedStatement.setObject(i+1, companyList.get(i));
		}
		
		resultSet = preparedStatement.executeQuery();
		ResultSetMetaData metaData = resultSet.getMetaData();
		
		int columnCount = metaData.getColumnCount(); 
		
		if(resultSet.next()) {
			companyIdPw = new HashMap<>();
			for (int i = 1; i < columnCount; i++) {
				companyIdPw.put(metaData.getColumnName(i), resultSet.getObject(i));				
			}
		}
		
		connection.close();
		preparedStatement.close();
		resultSet.close();
		
		return companyIdPw; 
	}
	
	//예약리스트 처리
	public List<Map<String, Object>> selectList(String sql) throws Exception {
		
		List<Map<String, Object>> list = new ArrayList<>();
		
		connection = DriverManager.getConnection(url, user, password);
		preparedStatement = connection.prepareStatement(sql);
		ResultSet resultSet = preparedStatement.executeQuery();
		
		ResultSetMetaData metaData = resultSet.getMetaData();
		
		int columnCount = metaData.getColumnCount();
		
		while(resultSet.next()) {
			Map<String, Object> row = new HashMap<>();
			for(int i = 1; i <= columnCount; i++) {
				row.put(metaData.getColumnName(i), resultSet.getObject(i));
			}
			list.add(row);
		}
		
		resultSet.close();
		preparedStatement.close();
		connection.close();
		
		return list;
	}
	
	//조건이 필요한 selectList 메소드
	public List<Map<String, Object>> selectList(String sql, List<Object> param) throws Exception {
				
		List<Map<String, Object>> list = new ArrayList<>();
		
		connection = DriverManager.getConnection(url, user, password);
		preparedStatement = connection.prepareStatement(sql);

		for (int i = 0; i < param.size(); i++) {
			preparedStatement.setObject(i+1, param.get(i));
		}
		
		ResultSet resultSet = preparedStatement.executeQuery();
		ResultSetMetaData metaData = resultSet.getMetaData();
				
		int columnCount = metaData.getColumnCount();
				
		while(resultSet.next()) {
			Map<String, Object> row = new HashMap<>();
			for (int i = 1; i <= columnCount; i++) {
				row.put(metaData.getColumnName(i), resultSet.getObject(i));
			}
			list.add(row);
		}
		
		resultSet.close();
		preparedStatement.close();
		connection.close();
		
		return list;
		}
	
	
	//조건이 있는 DML결과 반환
	public int update(String sql, List<Object> param) throws Exception {
		int result = 0;
		connection = DriverManager.getConnection(url,user,password);
		preparedStatement = connection.prepareStatement(sql);

		for (int i = 0; i < param.size(); i++) {
			preparedStatement.setObject(i+1, param.get(i));
		}
		result = preparedStatement.executeUpdate();
		
		preparedStatement.close();
		connection.close();
		
		return result;
		
	}
	
	//DML 결과 반환
	public int update(String sql) throws Exception {
		int result = 0;
		connection = DriverManager.getConnection(url,user,password);
		preparedStatement = connection.prepareStatement(sql);
		result = preparedStatement.executeUpdate();
		

		preparedStatement.close();
		connection.close();
		
		return result;
	}
	

}
