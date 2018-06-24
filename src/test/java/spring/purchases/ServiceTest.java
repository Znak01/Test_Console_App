package spring.purchases;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.emptyStandardInputStream;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hamcrest.collection.IsMapContaining;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;

import purchases.Expense;
import purchases.MyHttpClient;
import purchases.Service;

public class ServiceTest {

	private Service ser;
	private Map<String, List<Expense>> expectedMap;
	private List<Expense> list;
	private static final double DELTA = 1e-15;
	
	private final InputStream systemIn = System.in;
//    private final PrintStream systemOut = System.out;
//	
//    private ByteArrayInputStream testIn;
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	
	@Rule
	  public final TextFromStandardInputStream systemInMock
	    = emptyStandardInputStream();
	
	@Before
	public void setUp() throws Exception {
		ser = new Service();
		list = new ArrayList<Expense>();
		list.add(new Expense("Jogurt", 12, "USD"));
		expectedMap = new TreeMap<String, List<Expense>>();
	}
	
	@Before
	public void setUpStreams() {
	    System.setOut(new PrintStream(outContent));
	    System.setErr(new PrintStream(errContent));
	}

	@Test
	public final void testService() {
		expectedMap = new TreeMap<String, List<Expense>>();
		Assert.assertNotNull(expectedMap);
	}
	
//    private void provideInput(String data) {
//        testIn = new ByteArrayInputStream(data.getBytes());
//        System.setIn(testIn);
//    }
    
	@Test
	public final void testConsole() throws Exception {
//		systemInMock.provideLines("asw");
		
//		String input = "add5";
//		provideInput(input);
//		ser.console();
//		
//		assertEquals("Wrong command\r\n", outContent.toString());
	}

	@Test
	public final void testAddExpense() {
		ser.addExpense("2017-04-25", new Expense("Jogurt", 12, "USD"));
		expectedMap.put("2017-04-25", list);
		
		assertThat(ser.getMap(), is(expectedMap));
		assertThat(ser.getMap().size(), is(1));
		assertThat(ser.getMap(), IsMapContaining.hasEntry("2017-04-25", list));
		assertThat(ser.getMap(), IsMapContaining.hasKey("2017-04-25"));
		assertThat(ser.getMap(), IsMapContaining.hasValue(list));
		
	}

	@Test
	public final void testListOfExpenses() {
		ser.addExpense("2017-04-25", new Expense("Jogurt", 12, "USD"));
		ser.listOfExpenses();
		String s = "2017-04-25"+ "\r\n" + "Jogurt 12.0 USD\r\n";
		assertEquals(s, outContent.toString());
		
	}

	@Test
	public final void testDeleteExpense() {
		ser.addExpense("2017-04-25", new Expense("Jogurt", 12, "USD"));
		ser.deleteExpense("2017-04-25");
		expectedMap.remove("2017-04-25");
		
		assertThat(ser.getMap(), is(expectedMap));
	}

	@Test
	public final void testDataForm() {
		String expected = "2017-04-25";
		String actual = ser.dataForm("2017-04-25");
		
		assertEquals(expected, actual);
		assertNotEquals("2017.04.25", actual);
	}
	

	@Test
	public final void testIndexOfEUR() throws Exception {
		double actual = ser.indexOfEUR("USD");
		
		StringBuffer result = MyHttpClient.sendGet();
	    int findi = result.indexOf("USD");
	    String s = result.substring(findi + 5, result.indexOf(",", findi));
	    double expected = Double.parseDouble(s);
		
	    assertEquals(1, ser.indexOfEUR("EUR"), DELTA);
	    assertEquals(expected, actual, DELTA);
	}

	@Test
	public final void testTotalSame() throws Exception {
		ser.addExpense("2017-04-25", new Expense("Jogurt", 12, "USD"));
		ser.total("USD");
		
		assertEquals("12.0\r\n", outContent.toString());
	}
	
	
	@Test
	public final void testTotalOther() throws Exception {
		ser.addExpense("2017-04-25", new Expense("Jogurt", 12, "USD"));
		
		double index = ser.indexOfEUR("PLN");
		double expected = 12.0*ser.indexOfEUR("USD")*index;
		ser.total("PLN");
		
		assertEquals(Double.toString(expected)+"\r\n", outContent.toString());
	}
	
	@After
	public void cleanUpStreams() {
		System.setIn(systemIn);
		System.setOut(null);
	    System.setErr(null);
	}

}
