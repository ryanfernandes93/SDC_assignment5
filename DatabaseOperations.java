import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;

public class DatabaseOperations {
	Connection connect = null;
	Statement statement = null;
	ResultSet resultSet = null;
	BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	String startDate, endDate, xmlFile;
	// Hashmap to store key value pairs of customer name and address
	HashMap<String, String> custInfo;
	ArrayList<HashMap> custInfoList = new ArrayList<HashMap>();

	// Hashmap to store key value pairs of product details
	HashMap<String, String> productInfo;
	ArrayList<HashMap> productInfoList = new ArrayList<HashMap>();
	ArrayList<String> productLineList = new ArrayList<String>();

	// Hashmap to store key value pairs of employee name and address
	HashMap<String, String> employeeInfo;
	ArrayList<HashMap> employeeInfoList = new ArrayList<HashMap>();
	int counter = 0;
	String orderTotal = "0";

	public void connectSQL() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://db.cs.dal.ca:3306?serverTimezone=UTC", "fernandes",
					"B00828919");
			statement = connect.createStatement();
			// System.out.println("Connection established");
			runQuery();
		} catch (ClassNotFoundException e) {
			// Class not found exception
			System.out.println("Class not found exception");
			e.printStackTrace();
		} catch (SQLException e) {
			// SQL Exception
			System.out.println("SQL Exception");
			e.printStackTrace();
		} catch (IOException e) {
			// IOException
			e.printStackTrace();
		}
	}

	public void runQuery() throws IOException {
		try {
			statement.execute("use csci3901;");
			// Input start range of report
			System.out.println("input start date in YYYY-MM-DD format");
			startDate = input.readLine();
			// input end range of report
			System.out.println("input end date in YYYY-MM-DD format");
			endDate = input.readLine();
			System.out.println("input output xml filename");
			xmlFile = input.readLine();
			// run query to fetch cutomer name and address
			String sqlQuery = "SELECT customerName,addressLine1 FROM customers WHERE customerNumber IN (SELECT customerNumber FROM orders WHERE orderDate BETWEEN '"
					+ startDate + "' AND '" + endDate + "'AND status !='cancelled');";
			resultSet = statement.executeQuery(sqlQuery);
			counter = 0;
			// add to arraylist

			// chck if result set is empty
			if (!resultSet.next()) {
				xmlWriter();
			} else {
				do {
					// Output customer name and address
					//store in hashmap key value pair
					custInfo = new HashMap<String, String>();
					custInfo.put("customerName", resultSet.getString("customerName"));
					custInfo.put("addressLine1", resultSet.getString("addressLine1"));
					//append to arraylist
					custInfoList.add(custInfo);
					counter++;
				} while (resultSet.next());

				// run query to fetch order total for duration
				sqlQuery = "SELECT SUM(quantityOrdered * priceEach) AS orderTotal FROM orderdetails WHERE orderNumber IN (SELECT orderNumber FROM orders WHERE orderDate BETWEEN '"
						+ startDate + "' AND '" + endDate + "' AND status !='cancelled');";
				resultSet = statement.executeQuery(sqlQuery);
				// add to arraylist
				resultSet.next();
				orderTotal = resultSet.getString("orderTotal");

				// run query to fetch product info
				sqlQuery = "SELECT productName,productLine,productVendor,SUM(quantityOrdered) AS unitsOrdered,SUM(quantityOrdered*priceEach) AS totalSales FROM products NATURAL JOIN orders NATURAL JOIN orderdetails WHERE orderDate BETWEEN '"
						+ startDate + "' AND '" + endDate + "' AND status !='cancelled' GROUP BY productCode ORDER BY productLine;";
				resultSet = statement.executeQuery(sqlQuery);
				// add to arraylist
				while (resultSet.next()) {
					// add first element to product line vendor
					if (productLineList.size() == 0) {
						productLineList.add(resultSet.getString("productLine"));
					} else {
						boolean existFlag = false;
						for (String i : productLineList) {
							if (i.equals(resultSet.getString("productLine"))) {
								existFlag = true;
							}
						}

						if (!existFlag) {
							productLineList.add(resultSet.getString("productLine"));
						}
					}
					//store in hashmap key value pair
					productInfo = new HashMap<String, String>();
					productInfo.put("productName", resultSet.getString("productName"));
					productInfo.put("productLine", resultSet.getString("productLine"));
					productInfo.put("productVendor", resultSet.getString("productVendor"));
					productInfo.put("unitsOrdered", resultSet.getString("unitsOrdered"));
					productInfo.put("totalSales", resultSet.getString("totalSales"));
					//append to arraylist
					productInfoList.add(productInfo);
				}

				// run query to fetch employee information during the date
				sqlQuery = "SELECT firstName,lastName,offices.city,SUM(priceEach) AS totalSales,COUNT(distinct customerNumber) AS activeCustomers FROM orderdetails NATURAL JOIN orders NATURAL JOIN customers INNER JOIN employees ON employees.employeeNumber=customers.salesRepEmployeeNumber INNER JOIN offices ON offices.officeCode=employees.officeCode WHERE orderDate BETWEEN '"
						+ startDate + "' AND '" + endDate + "' AND status !='cancelled' GROUP BY employeeNumber;";
				resultSet = statement.executeQuery(sqlQuery);
				// add to arraylist
				while (resultSet.next()) {
					//store in hashmap key value pair
					employeeInfo = new HashMap<String, String>();
					employeeInfo.put("firstName", resultSet.getString("firstName"));
					employeeInfo.put("lastName", resultSet.getString("lastName"));
					employeeInfo.put("city", resultSet.getString("city"));
					employeeInfo.put("totalSales", resultSet.getString("totalSales"));
					employeeInfo.put("activeCustomers", resultSet.getString("activeCustomers"));
					//append to arraylist
					employeeInfoList.add(employeeInfo);
				}

				xmlWriter();
			}
			//close the result set and db connections
			resultSet.close();
			statement.close();
			connect.close();
		} catch (SQLException e) {
			
			//System.out.println("result set error");
			e.printStackTrace();
		}
	}

	public void xmlWriter() {
		XMLWriter xmlWriterObj = new XMLWriter();
		xmlWriterObj.writeToXML(xmlFile, custInfoList, startDate, endDate, counter, orderTotal, productLineList,
				productInfoList, employeeInfoList);
	}

}
