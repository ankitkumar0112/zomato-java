import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;

public class APA2 {
	public static class Wallet {
		private float amount = 1000;
		private float points;

		public float getamount() {
			return amount;
		}

		public void setamount(float f) {
			amount = f;
		}

		public float getpoint() {
			return points;
		}

		public void setpoint(float f) {
			points += f;
		}

		public float getBill(ArrayList<Food> addtocart, ArrayList<Integer> qty, int restno, ArrayList<Restaurant> RList,
				ArrayList<Customer> CList, int number, Company C, Cart c) {
			float amount1 = 0;
			for (int i = 0; i < addtocart.size(); i++) {
				amount1 += ((addtocart.get(i).getPrice() * qty.get(i))) * (1 - (addtocart.get(i).getDiscount() / 100));
			}
			amount1 = RList.get(restno).Discount(amount1);
			float dupamount = amount1;
			float reward_point = RList.get(restno).REWARD(dupamount);
			amount1 = CList.get(number).finalcheck(amount1);
			float amount2 = CList.get(number).getDelivery(amount1);
			if (CList.get(number).getamount() + CList.get(number).getpoint() + reward_point >= amount1 + amount2) {
				c.setflag(0);
				RList.get(restno).setpoint(reward_point);
				CList.get(number).setpoint(reward_point);
				setAmountforCompany(amount1, C);
				setDeliveryforCompany(amount2, C);
			} else {
				c.setflag(1);
			}
			return amount1 + amount2;
		}
	}

	public static class Cart {
		Scanner in = new Scanner(System.in);
		ArrayList<Food> addtocart = new ArrayList<Food>();
		ArrayList<Integer> resqty = new ArrayList<Integer>();
		private int flag;
		private int restno;

		public int getflag() {
			return flag;
		}

		public void setflag(int a) {
			flag = a;
		}

		public void add(Food food, int qty, int rest) {
			addtocart.add(food); // food is being added to the cart of the person
			resqty.add(qty);
			restno = rest;
		}

		public void checkout(ArrayList<Customer> CList, ArrayList<Restaurant> RList, int number, Company C, Cart c,
				ArrayList<String> OrderHistory) {
			flag = 0;
			float billamount = CList.get(number).getBill(addtocart, resqty, restno, RList, CList, number, C, c);
			// System.out.println("Value of flag is :"+Integer.toString(flag));
			if (c.getflag() == 0) {
				if (CList.get(number).getamount() + CList.get(number).getpoint() >= billamount) {
					System.out.println("Items in Cart -");
					for (int ci = 0; ci < addtocart.size(); ci++) {
						System.out.println(Integer.toString(resqty.get(ci)) + " " + RList.get(restno).getname() + " : "
								+ addtocart.get(ci));
					}
					System.out.println("Total Order Value:" + Float.toString(billamount));
					System.out.println(" 1) Proceed to checkout");
					int checkpermission = in.nextInt();
					if (checkpermission == 1) {
						if (CList.get(number).getpoint() >= billamount) {
							System.out.println("Bought for" + Float.toString(billamount));
							CList.get(number).setpoint(CList.get(number).getpoint() - billamount);
						}
						if (CList.get(number).getpoint() < billamount) {
							System.out.println("Bought for" + Float.toString(billamount));
							float amt = billamount - CList.get(number).getpoint();
							CList.get(number).setpoint((float) 0.0);
							CList.get(number).setamount(CList.get(number).getamount() - amt);
						}
					}
					RList.get(restno).setnorder(addtocart.size());
					for (int n = 0; n < addtocart.size(); n++) {
						OrderHistory.add("Bought item:" + addtocart.get(n).getname() + " , quantity: "
								+ Integer.toString(resqty.get(n)) + "for Rs "
								+ Float.toString(addtocart.get(n).getPrice() * resqty.get(n)
										* (1 - addtocart.get(n).getDiscount() / 100))
								+ " from " + RList.get(restno).getname());
					}
					addtocart = new ArrayList<Food>();
				}
			} else {
				System.out.println("Insufficient balance");
				System.out.println("Want to edit cart? (1 for yes 2 for no)");
				int ccc = in.nextInt();
				if (ccc == 1) {
					for (int k = 0; k < addtocart.size(); k++) {
						System.out.println(addtocart.get(k));
					}
					System.out.println("Enter food name to be removed");
					in.nextLine();
					String removeid = in.nextLine();
					for (int h = 0; h < addtocart.size(); h++) {
						if (addtocart.get(h).getname().equals(removeid)) {
							System.out.println("Removed");
							addtocart.remove(h);
							resqty.remove(h);
						}
					} // item removed
					checkout(CList, RList, number, C, c, OrderHistory);
				}
			}
		}
	}

	public static class Customer extends Wallet {
		private Cart cart = new Cart();
		private String name;
		private ArrayList<String> OrderHistory = new ArrayList<String>();

		Customer(String name) {
			this.name = name;
		}

		public ArrayList<String> getHistory() {
			return OrderHistory;
		}

		public String getname() {
			return name;
		}

		public void setname(String S) {
			name = S;
		}

		public void Select(Food food, int qtyneeded, int restno) { // restno is Restaurant number
			cart.add(food, qtyneeded, restno);
		}

		public void Checkout(ArrayList<Customer> CList, ArrayList<Restaurant> RList, int number, Company C, int FLAG) {
			cart.checkout(CList, RList, number, C, cart, OrderHistory);
		}

		public float getDelivery(float amount) {
			return (float) 40;
		} // rs 40 per delivery for normal

		public float finalcheck(float amount) {
			return amount;
		} // special offers on more discount

		public void PrintReward() {
			System.out.println("Total points: " + Float.toString(getpoint()));
		}

		public void Display(ArrayList<Customer> CList, ArrayList<Restaurant> RList, int number) {
			for (int k = 0; k < OrderHistory.size(); k++) {
				System.out.println(OrderHistory.get(k));
			}
		}

		public String getCategory() {
			return " ";
		}
	}

	public static class Elite extends Customer {
		Elite(String name) {
			super(name);
		}

		@Override
		public float getDelivery(float amount) {
			return (float) 0.0;
		} // free delivery

		@Override
		public float finalcheck(float amount) {
			if (amount > 200) {
				return amount - 50;
			}
			return amount;
		}

		@Override
		public String getCategory() {
			return "(Elite)";
		}
	}

	public static class Special extends Customer {
		Special(String name) {
			super(name);
		}

		@Override
		public float getDelivery(float amount) {
			return (float) 20;
		} // rs 20 per delivery

		@Override
		public float finalcheck(float amount) {
			if (amount > 200) {
				return amount - 25;
			}
			return amount;
		}

		@Override
		public String getCategory() {
			return "(Special)";
		}
	}

	public static class Food {
		final private int ID;
		private float discount;
		private float price;
		private String name;
		private String category;
		private int qty;

		Food(int ID, float discount, float price, String name, String category, int qty) {
			this.ID = ID;
			this.discount = discount;
			this.price = price;
			this.name = name;
			this.category = category;
			this.qty = qty;
		}

		public String getname() {
			return name;
		}

		public float getPrice() {
			return price;
		}

		public float getDiscount() {
			return discount;
		}

		public String toString() {
			return Integer.toString(ID) + " " + name + " " + Float.toString(price) + " " + Integer.toString(qty) + " "
					+ Float.toString(discount) + "% off " + category;
		}

		public void setname(String S) {
			name = S;
		}

		public void setprice(float p) {
			price = p;
		}

		public void setqty(int q) {
			qty = q;
		}

		public void setdisc(float disco) {
			discount = disco;
		}

		public void setcate(String S) {
			category = S;
		}
	}

	public static class Restaurant extends Wallet {
		private String name;
		private int norder;
		private float personaldisc;
		ArrayList<Food> FList = new ArrayList<Food>();
		Scanner in = new Scanner(System.in);

		Restaurant(String name) {
			this.name = name;
		}

		public void setnorder(int a) {
			norder += a;
		}

		public int getnorder() {
			return norder;
		}

		public ArrayList<Food> getFoodList() {
			return FList;
		}

		public String getname() {
			return name;
		}

		public void setname(String S) {
			name = S;
		}

		public void Addfood() {
			String name;
			String price;
			int qty;
			String category;
			String offer;
			System.out.println("Enter food item details");
			System.out.println("Food name:");
			name = in.nextLine();
			System.out.println("item price:");
			price = in.nextLine();
			System.out.println("item quantity:");
			qty = in.nextInt();
			System.out.println("item category:");
			in.nextLine();
			category = in.nextLine();
			System.out.println("Offer:");
			offer = in.nextLine();
			Food food = new Food(FList.size() + 1, Float.parseFloat(offer), Float.parseFloat(price), name, category,
					qty);
			System.out.println(food);
			FList.add(food);
		}

		public void Editfood() {
			System.out.println("Choose item by code");
			for (int i = 0; i < FList.size(); i++) {
				System.out.println(FList.get(i));
			}
			int foodch = in.nextInt();
			System.out.println("Choose an attribute to edit:");
			System.out.println("1) Name");
			System.out.println("2) Price");
			System.out.println("3) Quantity");
			System.out.println("4) Category");
			System.out.println("5) Offer");
			int attchoice = in.nextInt();
			in.nextLine();
			if (attchoice == 1) {
				System.out.println("Enter the new Name:");
				String str = in.nextLine();
				FList.get(foodch - 1).setname(str);
			} else if (attchoice == 2) {
				System.out.println("Enter the new price:");
				// in.nextLine();
				String prc = in.nextLine();
				FList.get(foodch - 1).setprice(Float.parseFloat(prc));
			} else if (attchoice == 3) {
				System.out.println("Enter the new Quantity:");
				int qq = in.nextInt();
				FList.get(foodch - 1).setqty(qq);
			} else if (attchoice == 4) {
				System.out.println("Enter the new Category:");
				String cat = in.nextLine();
				FList.get(foodch - 1).setcate(cat);
			} else {
				System.out.println("Enter the new Offer:");
				String off = in.nextLine();
				FList.get(foodch - 1).setdisc(Float.parseFloat(off));
			}
			System.out.println(FList.get(foodch - 1));
		}

		public void PrintReward() {
			System.out.println("Total rewards:" + Float.toString(getpoint()));
		}

		public void setprdis() {
			System.out.println("Enter offer on total bill value: ");
			String matter = in.nextLine();
			personaldisc = Float.parseFloat(matter);
		}

		public float getprdis() {
			return personaldisc;
		}

		public float Discount(float amount) {
			return amount;
		} // the Restaurant discount for normal rests is zero

		public float REWARD(float spentamt) {
			float point = 0;
			while (spentamt >= 100) {
				point += 5;
				spentamt -= 100;
			}
			return point;
		}
	}

	public static class FastFood extends Restaurant {
		FastFood(String name) {
			super(name);
		}

		@Override
		public float Discount(float amount) {
			return amount - (getprdis() / 100);
		}

		@Override
		public float REWARD(float spentamt) {
			float point = 0;
			while (spentamt >= 150) {
				point += 10;
				spentamt -= 150;
			}
			return point;
		}
	}

	public static class Authentic extends Restaurant {
		Authentic(String name) {
			super(name);
		}

		@Override
		public float Discount(float amount) {
			amount -= amount * (getprdis() / 100);
			if (amount > 100)
				return amount - 50;
			else
				return amount;
		}

		@Override
		public float REWARD(float spentamt) {
			float point = 0;
			while (spentamt >= 200) {
				point += 25;
				spentamt -= 200;
			}
			return point;
		}
	}

	public static class Company {
		float amount;
		float delivery;

		public void setamount(float a) {
			amount += a;
		}

		public void setdelivery(float d) {
			delivery += d;
		}

		public float getamount() {
			return amount;
		}

		public float getdelivery() {
			return delivery;
		}
	}

	public static void setAmountforCompany(float a, Company C) {
		C.setamount(a * (float) 0.01);
	}

	public static void setDeliveryforCompany(float a, Company C) {
		C.setdelivery(a);
	}

	public interface CHOICE {
		public void Choice(ArrayList<Customer> CList, ArrayList<Restaurant> RList, Company C);
	}

	public class CHOICE1 implements CHOICE {
		@Override
		public void Choice(ArrayList<Customer> CList, ArrayList<Restaurant> RList, Company C) {
			Scanner in = new Scanner(System.in);
			int choice2 = 0;
			int choice3 = 0;
			System.out.println("Choose Restaurant");
			System.out.println("1) Haldirams (Authentic)");
			System.out.println("2) Aggarwal");
			System.out.println("3) The Chinese (Authentic)");
			System.out.println("4) WOW Momos (Fast Food)");
			System.out.println("5) Paradise");
			choice2 = in.nextInt();
			if (choice2 != 6) {
				do {
					System.out.println("Welcome " + RList.get(choice2 - 1).getname());
					System.out.println("1) Add item");
					System.out.println("2) Edit item");
					System.out.println("3) Print Rewards");
					System.out.println("4) Discount on bill value");
					System.out.println("5) Exit");
					choice3 = in.nextInt();
					if (choice3 == 1) {
						RList.get(choice2 - 1).Addfood();
					}
					if (choice3 == 2) {
						RList.get(choice2 - 1).Editfood();
					}
					if (choice3 == 3) {
						RList.get(choice2 - 1).PrintReward();
					}
					if (choice3 == 4) {
						RList.get(choice2 - 1).setprdis();
					}
				} while (choice3 != 5);
			}
		}
	}

	public class CHOICE2 implements CHOICE {
		@Override
		public void Choice(ArrayList<Customer> CList, ArrayList<Restaurant> RList, Company C) {
			Scanner in = new Scanner(System.in);
			int choice2 = 0;
			int choice3 = 0;
			System.out.println("1. Martin (Elite)");
			System.out.println("2. Sam (Elite)");
			System.out.println("3. John (Special)");
			System.out.println("4. Luke");
			System.out.println("5. Ram");
			choice2 = in.nextInt();
			if (choice2 != 6) {
				do {
					System.out.println("Welcome " + CList.get(choice2 - 1).getname());
					System.out.println("Customer Menu");
					System.out.println("1) Select Restaurant");
					System.out.println("2) Checkout cart");
					System.out.println("3) Reward won");
					System.out.println("4) Print the recent orders");
					System.out.println("5) Exit");
					choice3 = in.nextInt();
					if (choice3 == 1) {
						System.out.println("Choose Restaurant");
						System.out.println("1) Haldirams (Authentic)");
						System.out.println("2) Aggarwal");
						System.out.println("3) The Chinese (Authentic)");
						System.out.println("4) WOW Momos (Fast Food)");
						System.out.println("5) Paradise");
						System.out.println("6) Exit");
						int Rchoice = in.nextInt();
						for (int i = 0; i < RList.get(Rchoice - 1).getFoodList().size(); i++) {
							System.out.println(RList.get(Rchoice - 1).getFoodList().get(i));
						}
						int choiceoffood = in.nextInt();
						System.out.println("Enter item quantity:");
						int choiceofqty = in.nextInt();
						System.out.println("Food selected: ");
						System.out.println(RList.get(Rchoice - 1).getFoodList().get(choiceoffood - 1));
						Food f = RList.get(Rchoice - 1).getFoodList().get(choiceoffood - 1);
						CList.get(choice2 - 1).Select(f, choiceofqty, Rchoice - 1);
					}
					if (choice3 == 2) {
						CList.get(choice2 - 1).Checkout(CList, RList, choice2 - 1, C, 0);
					}
					if (choice3 == 3) {
						CList.get(choice2 - 1).PrintReward();
					}
					if (choice3 == 4) {
						CList.get(choice2 - 1).Display(CList, RList, choice2 - 1);
					}
				} while (choice3 != 5);
			}
		}
	}

	public class CHOICE3 implements CHOICE {
		@Override
		public void Choice(ArrayList<Customer> CList, ArrayList<Restaurant> RList, Company C) {
			Scanner in = new Scanner(System.in);
			int choice2 = 0;
			int choice3 = 0;
			System.out.println("1) Customer List");
			System.out.println("2) Restaurant List");
			choice2 = in.nextInt();
			if (choice2 == 1) {
				for (int i = 0; i < CList.size(); i++) {
					System.out.println(Integer.toString(i + 1) + ". " + CList.get(i).getname());
				}
				choice3 = in.nextInt();
				System.out.println(CList.get(choice3 - 1).getname() + " " + CList.get(choice3 - 1).getCategory() + " "
						+ Float.toString(CList.get(choice3 - 1).getamount()));
			} else {
				for (int i = 0; i < RList.size(); i++) {
					System.out.println(Integer.toString(i + 1) + ". " + RList.get(i).getname());
				}
				choice3 = in.nextInt();
				System.out.println(RList.get(choice3 - 1).getname() + " "
						+ Integer.toString(RList.get(choice3 - 1).getnorder()) + " order(s)");
			}
		}
	}

	public static class Menu {
		Scanner in = new Scanner(System.in);
		Company C = new Company();
		APA2 A = new APA2();
		ArrayList<Customer> CList = new ArrayList<Customer>(5);
		ArrayList<Restaurant> RList = new ArrayList<Restaurant>(5);

		public void useChoice(CHOICE c, ArrayList<Customer> CList, ArrayList<Restaurant> RList, Company C) {
			c.Choice(CList, RList, C);
		}

		Elite Martin = new Elite("Martin");
		Elite Sam = new Elite("Sam");
		Special John = new Special("John");
		Customer Luke = new Customer("Luke");
		Customer Ram = new Customer("Ram");
		Authentic Haldirams = new Authentic("Haldirams");
		Restaurant Aggarwal = new Restaurant("Aggarwal");
		Authentic Chinese = new Authentic("The Chinese");
		FastFood Wow = new FastFood("WOW Momos");
		Restaurant Paradise = new Restaurant("Paradise");

		Menu() {
			CList.add(Martin);
			CList.add(Sam);
			CList.add(John);
			CList.add(Luke);
			CList.add(Ram);
			RList.add(Haldirams);
			RList.add(Aggarwal);
			RList.add(Chinese);
			RList.add(Wow);
			RList.add(Paradise);
		}

		int choice1 = 0;

		public void run() {
			do {
				System.out.println("Welcome to Zomato:");
				System.out.println("1) Enter as Restaurant Owner");
				System.out.println("2) Enter as Customer");
				System.out.println("3) Check User Details");
				System.out.println("4) Company Account details");
				System.out.println("5) Exit");
				choice1 = in.nextInt();
				switch (choice1) {
					case 1:
						CHOICE choice1 = A.new CHOICE1();
						useChoice(choice1, CList, RList, C);
						break;
					case 2:
						CHOICE choice2 = A.new CHOICE2();
						useChoice(choice2, CList, RList, C);
						break;
					case 3:
						CHOICE choice3 = A.new CHOICE3();
						useChoice(choice3, CList, RList, C);
						break;
					case 4:
						System.out.println("Total Company Balance: " + Float.toString(C.getamount()));
						System.out.println("Total Delivery Charged Collected: " + Float.toString(C.getdelivery()));
						break;
				}
			} while (choice1 != 5);
		}
	}

	public static void main(String[] args) {
		Menu menu = new Menu();
		menu.run();
	}
}