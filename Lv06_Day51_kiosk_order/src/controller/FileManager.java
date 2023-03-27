package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class FileManager {

	private FileWriter fileWriter;
	private FileReader fileReader;
	private BufferedReader bufferedReader;
	private BufferedReader bufferedReaderCore;

	private String menuFileName;
	private String orderFileName;

	private File menuFile;
	private File orderFile;

	public FileManager() {
		this.menuFileName = "menus.txt";
		this.orderFileName = "orders.txt";

		this.menuFile = new File(menuFileName);
		this.orderFile = new File(orderFileName);
	}

	// save
	public void save(Menu[] menus, Order[] orders) {
		saveMenus(menus);
		saveOrders(orders, menus);
	}
	
	public void saveMenus(Menu[] menus) { // {Menu1, Menu2, Menu3 ...}
		String data = "";
		
		if (menus != null) {
			for (int i = 0; i < menus.length; i++) {
				Menu menu = menus[i];
				
				data += menu.getName() + "/";
				data += menu.getPrice();
				
				if(i != menus.length-1)
					data += "/n";
			}
		}
		
		try {
			this.fileWriter = new FileWriter(menuFile);
			this.fileWriter.write(data);
			
			this.fileWriter.close();
			System.out.println("등록메뉴 저장 성공");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("등록메뉴 저장 실패");
		}	
	}
	
	private int getIndexByMenu(Menu menu, Menu[] menus) {
		int index = -1;
		
		if(menus != null) {
			for(int i=0; i<menus.length; i++) {
				Menu target = menus[i];
				if(menu.getName().equals(target.getName()))
					index = i;
			}
		}
		return index;
	}

	public void saveOrders(Order[] orders, Menu[] menus) {
		String data = "";
		// total/cash/charges/count/menuIndex1/menuIndex1... 
		
		if(orders!= null & menus != null) {
			for(int i=0; i<orders.length; i++) {
				Order order = orders[i];
				data += order.getTotal() + "/";
				data += order.getCash() + "/";
				data += order.getCharges() + "/";
				data += order.getCount() + "/";
				
				for(int j=0; j<order.getCount(); j++) {
					Menu menu = order.getList()[j];
					int index = getIndexByMenu(menu, menus);
				
					data += index;
					
					if(j != order.getCount()-1)
						data += "/";
				}
				
				if(i != orders.length-1)
					data += "\n";
			}
		}
		
		try {
			this.fileWriter = new FileWriter(this.orderFile);
			this.fileWriter.write(data);
			this.fileWriter.close();
			System.out.println("주문내역 저장 완료");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("주문내역 저장 실패");
		}
	}
	
	// load
	public Menu[] loadMenus() {
		Menu[] menus = null;
		int index = 0;
		
		// name / price
		try {
			this.fileReader = new FileReader(menuFile);
			this.bufferedReader = new BufferedReader(this.fileReader);
			
			while(this.bufferedReader.ready()) {
				String[] data = this.bufferedReader.readLine().split("/");
				// name / price
				
				String name = data[0];
				int price = Integer.parseInt(data[1]);
				
				Menu menu = new Menu(name, price);
				
				Menu[] temp = menus;
				menus = new Menu[index +1];
				
				for(int i=0; i<index; i++) 
					menus[i] = temp[i];
				menus[index] = menu;
				index++;
			}
			
			this.fileReader.close();
			this.bufferedReader.close();
			
			System.out.println("등록메뉴 파일로드 성공");
			
			return menus;
		} catch (Exception e) {
			e.printStackTrace();		
			System.err.println("등록메뉴 파일로드 실패");
		}
		return menus;
	}
	
	private Menu[] createOrderList(int[] indexes) {
		Menu[] orderList =null; // indexes를 가지고 완성할 주문 내역
		Menu[] menus = loadMenus(); // 로드된 등록된 메뉴들
		
		if(indexes != null) {
			orderList = new Menu[indexes.length];
			
			for(int i=0; i<indexes.length; i++) {
				int menuIndex = indexes[i];
				orderList[i] = menus[i];
			}
		}
		return orderList;
	}

	public Order[] loadOrders() {
		Order[] list = null;
		int index = 0;
		
		if(orderFile.exists()) {
			try {
				this.fileReader = new FileReader(orderFile);
				this.bufferedReaderCore = new BufferedReader(this.fileReader);
				
				while(this.bufferedReader.ready()) {
					// 1. 파일에서 라인 읽기 후 /구분자로 문자 분리
					String[] data = this.bufferedReaderCore.readLine().split("/");
					// total/cash/charges/count/menuIndex1/menuIndex1 ...
					//   0    1      2      3  /  4 인덱스부터는 count 값 영향받음

					// 2. 데이터를 형변환함(Order 객체 만들 준비)
					int total = Integer.parseInt(data[0]);
					int cash = Integer.parseInt(data[1]);
					int charges = Integer.parseInt(data[2]);
					int count = Integer.parseInt(data[3]);
					
					// 로드된 메뉴 기준으로 -> OrderSystem.menus 배열의 인덱스 정보
					int[] indexes = new int[count];
					for(int i=0; i<count; i++) {
						indexes[i] = Integer.parseInt(data[4+i]); 
					}
					Menu[] orderList = createOrderList(indexes);

					// 3. 준비된 데이터들로 Order 객체 생성
					Order order = new Order(count, orderList, total, cash, charges);
					
					// 4. list 완성하기
					Order[] temp = list;
					list = new Order[index+1];
					for(int i=0; i<index; i++) 
						list[i] = temp[i];
					list[index] = order;
					index++;
				}
				
				this.fileReader.close();
				this.bufferedReaderCore.close();
				System.err.println("주문 이력 파일 로드 성공");
				
				// 5. list 최종본을 반환(return)
				return list;
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("주문 이력 파일 로드 실패");
			}
		}
		return list;
	}
}
