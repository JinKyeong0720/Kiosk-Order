package controller;

// 주문 단위 (건 바이 건) 
public class Order {
	
	private int count;
	private Menu[] list;
	private int total, cash, charges;
	
	public Order() {} // 27:25
	public Order(int count, Menu[] list, int total, int cash, int charges) {
		this.count = count;
		this.list = list;
		this.total = total;
		this.cash = cash;
		this.charges = charges;
	}
	
	public int getCount() {
		return this.count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	public Menu[] getList() {
		// return this.list;							// 배열의 주소 (사용X) 
		// 복제본을 생성 -> 복제본의 주소를 반환 
		Menu[] list = null;
		if(this.count > 0) {
			list = new Menu[this.count];
			for(int i=0; i<this.count; i++) {
//				list[i] = this.list[i];					// 객체의 주소 (사용X)
				String name = this.list[i].getName();
				int price = this.list[i].getPrice();
				Menu menu = new Menu(name, price);
				list[i] = menu; 						// 객체 넣어주세요~ 
			}
		}
		return list;
	}
	
	public void setList(Menu[] list) {
		this.list = list;
	}
	
	public int getTotal() {
		return this.total;
	}
	
	public void setTotal(int total) {
		this.total = total;
	}
	
	public int getCash() {
		return this.cash;
	}
	
	public void setCash(int cash) {
		this.cash = cash;
	}
	
	public int getCharges() {
		return this.charges;
	}
	
	public void setCharges(int charges) {
		this.charges = charges;
	}
}
