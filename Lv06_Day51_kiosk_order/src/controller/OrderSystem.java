package controller;

import java.util.Scanner;

public class OrderSystem {
	
	private final int QUIT = 0;
	
	private Scanner scan;
	private FileManager fileManager;
	
	private String name;
	
	private int total;
	private int orderCount;
	private Order[] list; 	// 전체 주문(Order) 내역
	// 파일 양식 
	// ㄴ total/cash/charges/count/menuIndex1/menuIndex1...
	// ㄴ total/cash/charges/count/menuIndex1/menuIndex1...
	// ㄴ total/cash/charges/count/menuIndex1/menuIndex1...
	
	private int menuCount;
	private Menu[] menus;	// 등록된 메뉴 목록 
	// 파일 양식 
	// ㄴ name/price 
	// ㄴ name/price 
	// ㄴ name/price 
	
	public OrderSystem(String name) {
		this.scan = new Scanner(System.in);
		this.fileManager = new FileManager();
		this.name = name;
		init();
	}
	
	private void init() {
		// 기본 메뉴 3가지 구성
		Menu menu2 = new Menu("분보싸오", 14000);
		Menu menu1 = new Menu("양지차돌쌀국수", 12000);
		Menu menu3 = new Menu("스프링롤", 4500);
		
		this.menus = new Menu[3];
		addNewMenu(menu1);
		addNewMenu(menu2);
		addNewMenu(menu3);	
	}
	
	// ... 시스템 실행에 필요한 private 메소드들~ 
	private void addNewMenu(Menu menu) {
		Menu[] temp = this.menus;
		this.menus = new Menu[this.menuCount +1];
		
		for(int i=0; i<this.menuCount; i++) {
			this.menus[i] = temp[i];
		}
		this.menus[this.menuCount] = menu;
		this.menuCount ++;
	}
	
	private void printMenusAll() {
		for(int i=0; i<this.menuCount; i++) {
			Menu menu = this.menus[i];
			System.out.printf("%d. %s\n", i+1, menu.toStringMenu());
		}
	}
	
	private void printCartAll(Order order) {
		int[] count = new int[this.menuCount];

		Menu[] list = order.getList();
		if (list != null) {
			// count
			for (int i = 0; i < list.length; i++) {
				Menu menu = list[i];
				for (int j = 0; j < this.menuCount; j++) {
					if (menu.getName().equals(this.menus[j].getName()))
						count[j]++;
				}
			}

			// print
			for (int i = 0; i < this.menuCount; i++)
				if (count[i] > 0)
					System.out.printf("%s\t%d개\t%d원\n", this.menus[i].getName(), count[i],
							this.menus[i].getPrice() * count[i]);
		}
	}
	
	private void printMainMenu(Order order) {
		System.out.println("-----  " + this.name + "  -----");
		printMenusAll();
		System.out.println("----------------------");
		printCartAll(order);
		System.out.println((this.menuCount + 1) + ". 결제하기");
		System.out.println((this.menuCount + 2) + ". 관리자");
		System.out.println("0. 종료");
		System.out.println("----------------------");
	}
	
	private int inputNumber() {
		System.out.print("input : ");
		String input = this.scan.next();
		int number = 0;
		
		try {
			number = Integer.parseInt(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return number;
	}
	
	private void order(Order order, int menuIndex) {
		// order.list -> 크기를 키우고 -> menuIndex 번째의 주문한 메뉴를 추가 
		Menu[] temp = order.getList();
		Menu[] list = new Menu[order.getCount() +1];
		
		for(int i=0; i<order.getCount(); i++)
			list[i] = temp[i];
		list[order.getCount()] = this.menus[menuIndex];
		
		// 새로만든 리스트를 -> order.list 에 업데이트 
		order.setList(list);
		order.setCount(order.getCount() +1);
	}
	
	private boolean isMenu(int select) {
		return select > 0 && select <= this.menuCount;
	}
	
	private boolean isPaying(int select) {
		return select == this.menuCount + 1;
	}
	
	private boolean isAdmin(int select) {
		return select == this.menuCount + 2;
	}
	
	private void printReceipt(Order order) {
		System.out.println("--- 영 수 증 ---");
		printCartAll(order);
		System.out.println("--------------");
		System.out.printf("총 결제금액 : %d원\n", order.getTotal());
		System.out.printf("받은 금액 : %d원\n", order.getCash());
		System.out.printf("거스름돈 : %d원\n", order.getCharges());
		System.out.println("--------------");
	}
	
	private void addOrderRecord(Order order) {
		Order[] temp = this.list;
		this.list = new Order[this.orderCount +1];
		for(int i=0; i<this.orderCount; i++)
			this.list[i] = temp[i];
		this.list[this.orderCount] = order;
		this.orderCount ++;
	}
	
	private void pay(Order order) {
		Menu[] list = order.getList();
		
		int total = 0;
		for(int i=0; i<order.getCount(); i++) {
			Menu menu = list[i];
			total += menu.getPrice();
		}
		
		int cash = inputNumber();
		int charges = 0;
		
		if(total <= cash) {		
			charges = cash - total;
			
			order.setTotal(total);
			order.setCash(cash);
			order.setCharges(charges);
			
			// 영수증 발행 
			printReceipt(order);
			
			// 주문 건 추가 
			addOrderRecord(order);
			
			// 매출 증가 
			this.total += total;
		}
		else 
			System.out.println("현금이 부족합니다.");
	}
	
	private void printAdminMenu() {
		System.out.println("1. 전제 주문내역 조회");
		System.out.println("2. 총 매출 조회");
		System.out.println("3. 판매 상품 추가");
		System.out.println("0. 뒤로가기");
	}
	
	private boolean checkAdmin() {
		System.out.print("id : ");
		String id = this.scan.next();
		System.out.print("password : ");
		String password = this.scan.next();
		
		if(id.equals("admin") && password.equals("qwer"))
			return true;
		return false;
	}
	
	private void printOrdersAll() {
		for(int i=0; i<this.orderCount; i++) {
			printReceipt(this.list[i]);
			System.out.println(">>>");
		}
	}
	
	private void printTotal() {
		System.out.println("총 매출액 : " + this.total);
	}
	
	private boolean isDuplMenu(String name) {
		boolean dupl = false;
		
		for(int i=0; i<this.menuCount; i++) {
			Menu menu = this.menus[i];
			if(menu.getName().equals(name))
				dupl = true;
		}
		return dupl;
	}
	
	private void addNewMenu() {
		System.out.print("메뉴명 : ");
		this.scan.next();
		String name = this.scan.nextLine();
		System.out.print("가격 ");
		int price = inputNumber();
		
		if(!isDuplMenu(name)) {
			Menu menu = new Menu(name, price);
			addNewMenu(menu);
			
//			Menu[] temp = this.menus;
//			this.menus = new Menu[this.menuCount +1];
//			
//			for(int i=0; i<this.menuCount; i++)
//				this.menus[i] = temp[i];
//			this.menus[this.menuCount] = menu;
//			this.menuCount ++;
			
			System.out.println("신메뉴 등록 완료");
		}
		else {
			System.out.println("이미 존재하는 메뉴명입니다.");
		}
	}
	
	private void adminRun() {
		if(checkAdmin()) {
			while(true) {
				printAdminMenu();
				int select = inputNumber();
				
				if(select == 1) printOrdersAll();
				else if(select == 2) printTotal();
				else if(select == 3) addNewMenu();
				else if(select == QUIT) break;
			}
		}
	}
	
	private void save() {
		this.fileManager.save(this.menus, this.list);
	}
	
	private void load() {
		// this.list
		this.list = this.fileManager.loadOrders();

		if(this.list != null)
			this.orderCount = this.list.length;
		
		// this.menus
		this.menus = this.fileManager.loadMenus();
		
		if(this.menus != null)
			this.menuCount = this.menus.length;
		else
			init();
	}
	
	public void run() {
		Order order = new Order();
		while(true) {
			printMainMenu(order);
			int select = inputNumber();
			if(isMenu(select))
				order(order, select-1);
			else if(isPaying(select)) {
				pay(order);
				order = new Order();
			}
			else if(isAdmin(select)) {				
				adminRun();
			}
			else if(select == QUIT)
				break;
			
			save();
		}
	}
	
	public String getName() {
		return this.name;
	}	
}