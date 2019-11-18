import java.sql.*;
import java.io.*;

public class FetchData {

	public static void main(String[] args) {
		// establish connection with database to start database operations
		DatabaseOperations op = new DatabaseOperations();
		op.connectSQL();
	}

}
