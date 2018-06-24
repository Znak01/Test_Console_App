package purchases;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

public class Service {

	private static Scanner sc = new Scanner(System.in);
	
	private Map<String, List<Expense>> map;

	public Service() {
		map = new TreeMap<String, List<Expense>>();
	}


	public void console() throws Exception {
		List<String> command = Arrays.asList(sc.nextLine().split(" "));
		
		switch (command.get(0)) {
		case "add":
			double cost = 0;
			
			if(command.size()<5) {
				System.out.println("Wrong command");
			} else {
				try {
					cost = Double.parseDouble(command.get(2));
				} catch (Exception e) {
					System.out.println(command.get(2) + " is not number");
				}
				
				if(command.get(3).length()>3 || MyHttpClient.sendGet().indexOf(command.get(3).toUpperCase()) == -1) {
					System.out.println("There are no such currency");
				} else {
					addExpense(dataForm(command.get(1)), new Expense(command.get(4), cost, command.get(3).toUpperCase()));
				}
			}
			console();
			break;
			
		case "list":
			if(command.size()>2) {
				System.out.println("Wrong command");
			} else {
				listOfExpenses();
			}
			console();
			break;
			
		case "clear":
			if(command.size()>3) {
				System.out.println("Wrong command");
			} else {
				deleteExpense(dataForm(command.get(1)));
			}
			console();
			break;
			
		case "total":
			if(command.size()>3) {
				System.out.println("Wrong command");
			} else {
				if(command.get(1).length()>3) {
					System.out.println("There are no such currency");
				} else {
					total(command.get(1));
				}
			}
			console();
			break;
			
		default:
			System.out.println("Wrong command");
			console();
			break;
		}
	}
	
	
	public void addExpense(String data, Expense ex) {
		if(data == null) {} 
		else {	
		if(map.isEmpty() || !map.containsKey(data) || data.isEmpty()) {
			List<Expense> expenses = new ArrayList<>();
			expenses.add(ex);
			map.put(data, expenses);
		} else {
			map.forEach((k,v)->{
				if(k.equals(data)) {
					v.add(ex);
				} 
			});	
		}
		}
	}
	
	public void listOfExpenses() {
		map.forEach((k,v) -> {
			System.out.println(k);
		for (Expense expense : v) {
			System.out.println(expense.getName() + " " + expense.getCost() + " " + expense.getCurrency());
		}
				});
	}
	
	public void deleteExpense(String data) {
		if(data == null) {} 
		else {	
		if(!map.containsKey(data)) {
			System.out.println("Thera are no such data");
		} else {
			map.entrySet().removeIf(entry -> entry.getKey().equals(data));
		}
		}
	}
	
	public String dataForm(String data) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	Date date;
    	String output = null;
    	
    	try {
    	   date = dateFormat.parse(data);
    	   output = dateFormat.format(date);
    	} catch (Exception e) {
    	   System.out.println("Wrong data format");
    	} 
    	return output;
		
	}
	
	public double indexOfEUR(String currency) throws Exception {
		if(currency.equals("EUR")) {
			return 1;
		} else {
			StringBuffer result = MyHttpClient.sendGet();
		    int findi = result.indexOf(currency);
		    String s = result.substring(findi + 5, result.indexOf(",", findi));
		    return  Double.parseDouble(s);
		}

	}
	
	public void total(String currency) throws Exception {
		double sum = 0;
		double index = indexOfEUR(currency);
		
		Iterator<Entry<String, List<Expense>>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, List<Expense>> next = iter.next();
			for (Expense ex : next.getValue()) {
				
				if(ex.getCurrency().equals(currency)) {
					sum += ex.getCost();	
				} 
				if(!ex.getCurrency().equals(currency) && ex.getCurrency().equals("EUR")) {
					sum += ex.getCost()*index;
				} if(currency.equals("EUR") || (!ex.getCurrency().equals(currency) && !ex.getCurrency().equals("EUR"))) {
					sum += ex.getCost()*indexOfEUR(ex.getCurrency())*index;
				}
			} 
		}
		System.out.println(sum);
	}


	public Map<String, List<Expense>> getMap() {
		return map;
	}
	
}
