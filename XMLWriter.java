import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLWriter {
	public void writeToXML(String xmlFile, ArrayList<HashMap> custInfoList, String startDate, String endDate,
			int counter, String orderTotal, ArrayList<String> productLineList, ArrayList<HashMap> productInfoList,
			ArrayList<HashMap> employeeInfoList) {
		// check if file path contains .xml
		String xmlFilePath;
		if (xmlFile.substring(xmlFile.length() - 4).equals(".xml")
				|| xmlFile.substring(xmlFile.length() - 4).equals(".XML")) {
			xmlFilePath = xmlFile;
		} else {
			// append .xml to file as it does not exist
			xmlFilePath = xmlFile + ".xml";
		}

		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = documentFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			
			e.printStackTrace();
		}

		Document document = documentBuilder.newDocument();

		try {
			// root element
			Element root = document.createElement("year_end_summary");
			document.appendChild(root);

			Element year = document.createElement("year");
			root.appendChild(year);
			Element startDateTag = document.createElement("start_date");
			startDateTag.appendChild(document.createTextNode(startDate));
			year.appendChild(startDateTag);
			Element endDateTag = document.createElement("end_date");
			endDateTag.appendChild(document.createTextNode(endDate));
			year.appendChild(endDateTag);

			Element customer_list = document.createElement("customer_list");
			root.appendChild(customer_list);

			Element customer = document.createElement("customer");
			customer_list.appendChild(customer);
			if (custInfoList.isEmpty() == false) {
				for (HashMap i : custInfoList) {
					Element customer_name = document.createElement("customer_name");
					customer_name.appendChild(document.createTextNode(i.get("customerName").toString()));
					customer.appendChild(customer_name);

					Element address = document.createElement("address");
					address.appendChild(document.createTextNode(i.get("addressLine1").toString()));
					customer.appendChild(address);
				}
			}

			// create number of orders section in xml structure 
			Element num_orders = document.createElement("num_orders");
			num_orders.appendChild(document.createTextNode(Integer.toString(counter)));
			customer.appendChild(num_orders);

			Element order_value = document.createElement("order_value");
			order_value.appendChild(document.createTextNode(orderTotal));
			customer.appendChild(order_value);

			//create product info section in xml structure
			Element product_list = document.createElement("product_list");
			root.appendChild(product_list);
			Element product_set = document.createElement("product_set");
			product_list.appendChild(product_set);

			if (productLineList.isEmpty() == false && productInfoList.isEmpty() == false) {
				for (String i : productLineList) {
					Element product_line_name = document.createElement("product_line_name");
					product_line_name.appendChild(document.createTextNode(i));
					product_set.appendChild(product_line_name);

					for (HashMap h : productInfoList) {
						if (i.equals(h.get("productLine"))) {
							Element product = document.createElement("product");
							product_set.appendChild(product);

							Element product_name = document.createElement("product_name");
							product_name.appendChild(document.createTextNode(h.get("productName").toString()));
							product.appendChild(product_name);

							Element product_vendor = document.createElement("product_vendor");
							product_vendor.appendChild(document.createTextNode(h.get("productVendor").toString()));
							product.appendChild(product_vendor);

							Element units_sold = document.createElement("units_sold");
							units_sold.appendChild(document.createTextNode(h.get("unitsOrdered").toString()));
							product.appendChild(units_sold);

							Element total_sales = document.createElement("total_sales");
							total_sales.appendChild(document.createTextNode(h.get("totalSales").toString()));
							product.appendChild(total_sales);
						}
					}
				}
			}

			// create employee list structure in xml tree structure
			Element staff_list = document.createElement("staff_list");
			root.appendChild(staff_list);

			if (employeeInfoList.isEmpty() == false) {
				for (HashMap h : employeeInfoList) {
					Element employee = document.createElement("employee");
					staff_list.appendChild(employee);

					Element first_name = document.createElement("first_name");
					first_name.appendChild(document.createTextNode(h.get("firstName").toString()));
					employee.appendChild(first_name);

					Element last_name = document.createElement("last_name");
					last_name.appendChild(document.createTextNode(h.get("lastName").toString()));
					employee.appendChild(last_name);

					Element office_city = document.createElement("office_city");
					office_city.appendChild(document.createTextNode(h.get("city").toString()));
					employee.appendChild(office_city);

					Element active_customers = document.createElement("active_customers");
					active_customers.appendChild(document.createTextNode(h.get("activeCustomers").toString()));
					employee.appendChild(active_customers);

					Element total_sales = document.createElement("total_sales");
					total_sales.appendChild(document.createTextNode(h.get("totalSales").toString()));
					employee.appendChild(total_sales);
				}
			}

			// create the xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(new File(xmlFilePath));
			transformer.transform(domSource, streamResult);
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
		//
	}
}