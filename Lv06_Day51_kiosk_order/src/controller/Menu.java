package controller;

public class Menu {
	
	private String name;
	private int price;
	
	public Menu(String name, int price) {
		this.name = name;
		this.price = price;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getPrice() {
		return this.price;
	}
	
	public String toStringMenu() {
		return String.format("%s\t%5d원", this.name, this.price);
	}
}
