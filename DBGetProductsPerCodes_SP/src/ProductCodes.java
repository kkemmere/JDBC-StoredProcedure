
// Created by Kevin Kemmerer CS485 Advanced Database Systems

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.StringTokenizer;

public class ProductCodes {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/classicmodels?serverTimezone=UTC";
	
	// Random number generator
	private static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// The name of the MySQL account
		String dbUser = args[0];
		// Password for MySQL account
		String passWord = args[1];
		System.out.println(dbUser + ", " + passWord);
							
		Connection conn = null;
		Statement stmt = null;
		ResultSet rsGetCat = null;
		ResultSet rsdbmd = null;
		ResultSet rs = null;
		
		try {
			
			
			// Open a connection to the MySQL Database Manager 
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, dbUser, passWord);
			System.out.println("Connection is valid: " + conn.isValid(2));
			System.out.println();
						
			String delimString ="";
			
			// Don't know size of array so use ArrayList
			ArrayList<String> codesList = new ArrayList<String>();
			
			// Get codes with query
			String sqlCodes = "select distinct productCode\n" + "from products;\n"; 
			PreparedStatement psCodes = conn.prepareStatement(sqlCodes, rs.TYPE_SCROLL_SENSITIVE, rs.CONCUR_UPDATABLE);
			
			rs = psCodes.executeQuery();
			
			// Get all codes
			while (rs.next())
			{
				codesList.add(rs.getString("productCode"));
			}
			
			//Print product codes
			System.out.println(Arrays.toString(codesList.toArray()));
			System.out.println();
			
			// Create random
			Random rand = new Random();
			
			// Get random int in range from 2-6 using getRandomNumberInRange() method
			int ranNum = getRandomNumberInRange(2,6);
			
			// Using ranNum(random number in range from 2-6) and rand(random method) to get random string from product code list
			for (int i = 0; i < ranNum; i++)
			{
				delimString += codesList.get(rand.nextInt(codesList.size())) + " ";
			}
			
			System.out.println("Delimited String: " + delimString);
			
			
			// Stored Procedure Query
			String ProdCodesProcedure = "CALL classicmodels.getProductsPerCodes(?)";
			CallableStatement csProdCodes = conn.prepareCall(ProdCodesProcedure);
			
			// Get number of current codes in string
			int delimCodesNum = 0;
			StringTokenizer tokens = new StringTokenizer(delimString);
			delimCodesNum = tokens.countTokens();
			System.out.println("Number of codes in delimited string: " + delimCodesNum);
			
			// Create string array to be used to feed query
			String[] delimAry = delimString.split(" ");
			
			// For loop to pass query each product code 1 by 1
			// Not the most efficient could somehow pass the whole delimited string instead
			for (int i = 0; i < delimCodesNum; i++)
			{
				// Passing delimited product code to procedure
				csProdCodes.setString(1, delimAry[i]);
				rs = csProdCodes.executeQuery();
			
				String productCode = "";
				String productName = "";
				String productLine = "";
				String productVendor = "";
				int quantityinstock;
				
				while (rs.next())
				{
					// Info from stored procedure (Product Codes)
					productCode = rs.getString("productCode");
					productName = rs.getString("productName");
					productLine = rs.getString("productLine");
					productVendor = rs.getString("productVendor");
					quantityinstock = rs.getInt("quantityInStock");
					
					System.out.println();
					System.out.println("Info from this product code: " + productCode);
					System.out.println("Product Name: " + productName + " | " + "Product Line: " + productLine + " | " + "Product Vendor: " + productVendor + " | " + "Quantity In Stock: " + quantityinstock);;
					System.out.println();
				}
				
		}
					
		} catch (SQLException se) { 
			
			// Handle errors for Database
			// See https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlexception.html
			// 8.	There, print out the SQL Exception, the SQL State Code, and any Error Code if an exception were to occur.
			System.out.println("SQL Exception: " + se.getMessage());
			System.out.println("SQLState Code: " + se.getSQLState());
			System.out.println("Error Code: " + se.getErrorCode());
		} finally {
				try {
					// 6.	Upon completion, ensure that all Connections, Statements, and ResultSets are closed.
					if (rs != null) rs.close();
					if (stmt != null) stmt.close();
					if (conn != null) conn.close();
					System.out.println("All connections are closed:" + conn.isClosed());
				} catch (SQLException se2) {}
				// End of finally
			}


	}

}
