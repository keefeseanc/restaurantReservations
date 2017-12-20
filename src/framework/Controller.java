package framework;
import java.sql.*;
import java.util.ArrayList;

import DataObject.*;
import static framework.OBuilder.getObjectBuilder;


public class Controller implements Constants{
	private Connection dbConnection = null;
	private String driverClass = null;
	private static Controller DBController = null;
	private OBuilder objectBuilder = null;
	private DatabaseMetaData dbMetaData = null;

	private OBuilder  getObjectBuilder(){
		if (objectBuilder == null){
			objectBuilder = OBuilder.getObjectBuilder(); 
		}
		return objectBuilder;
	}
	private Connection getDbConnection() {
		if (dbConnection == null){
			dbConnection = createDatabaseConnection();
		}
		return dbConnection;
	}
	private String getDriverClass() {
		if (driverClass == null){
			driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		}
		return driverClass;
	}
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}
	public static Controller getDBController() {
		if (DBController == null){
			DBController = new Controller();
		}
		return DBController;
	}
	private DatabaseMetaData getDBMetaData(){
		try {
			dbMetaData = getDbConnection().getMetaData();
		} catch (SQLException e) {
			System.out.println(e);
		}
		return dbMetaData;
	}
	
	/** CONSTRUCTORS *************************/
	private Controller(){
		
	}

	/** DATABASE CONNECTION CODE **********************/
	private Connection createDatabaseConnection() {
		Connection conn = null;
		String connectString = "jdbc:sqlserver://reservations.database.windows.net:1433;database=Reservation;user=opprobrious@reservations;password=13ANGels!!;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30";
		//First, we make sure the Driver exists
		try {
			Class.forName(getDriverClass());
		} catch (java.lang.ClassNotFoundException e) {
			StringBuffer buf = new StringBuffer();
			buf.append("No driver class found for: ");
			buf.append(getDriverClass());
			System.out.println(buf.toString());
			System.exit(0);
		}
		//Driver class exists, now try to open the connection
		try {
			conn = DriverManager.getConnection(connectString);
		} catch (SQLException e) {
			StringBuffer buf = new StringBuffer();
			buf.append("There was a problem with the following connection string: ");
			buf.append(connectString);
			buf.append("\n\nHere is the exception:\n");
			buf.append(e.toString());
			System.out.println(buf.toString());
			System.exit(0);
		}
		return conn;
	}

	/** UTILITIES   ******************************************/
	public ArrayList<String> getDatabaseNames(){
		/**
		 * This method gets a list of all the databases on the server owned by dbo
		 */
	    ArrayList<String> databases = new ArrayList<String>();
		ResultSet res;
		try {
			res = getDBMetaData().getCatalogs();
		    while (res.next()) {
		    	databases.add(res.getString("TABLE_CAT"));
			}
			res.close();
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return databases;
}
	public ArrayList<String> getTableNames(){
		/**
		 * This method gets a list of all tables in the currently active database
		 */
		ArrayList<String> tables = new ArrayList<String>();
		DatabaseMetaData dbmd = getDBMetaData();
		if (dbmd != null){
			try {
				ResultSet rs = dbmd.getTables(null, null, "%", null);
				while(rs.next()){
					if (rs.getString(2).equals("dbo")){
						tables.add(rs.getString(3));
					}
				}
			} catch (SQLException e) {
				System.out.println(e);
				e.printStackTrace();
			}
		}
		return tables;
	}
	public void printResultSet(ResultSet rs){
		/**
		 * This method just prints the contents of a ResultSet
		 * to the output console.
		 */
		try{
			boolean columnHeadingsPrinted = false;
			while (rs.next()){
				if(! columnHeadingsPrinted){
					//print the table's column headings
					for(int i=1; i<=rs.getMetaData().getColumnCount(); i++){
						System.out.print(rs.getMetaData().getColumnLabel(i));
						System.out.print(":\t");
					}
					System.out.println();
					columnHeadingsPrinted = true;
				}
				//now print a row of data	
				for(int i=1; i<=rs.getMetaData().getColumnCount(); i++){
					System.out.print(rs.getString(i));
					System.out.print("\t");
				}
				System.out.println();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	public void printQueryResults(String query) {
		try {
			Statement s = getDbConnection().createStatement();
			ResultSet rs = s.executeQuery(query);
			printResultSet(rs);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		
		
	}
	
	
	public ResultSet executeQuery(String query){
		 //Executes the query passed in on the active database and returns a ResultSet
		Statement s = null;
		ResultSet rs = null;
		try {
			s = getDbConnection().createStatement();
			rs = s.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	public ResultSet executeStoredProcedure(String procName, ArrayList<List> nvpList) {
		/**
		 * Calls the stored procedure passed in, sending it
		 * the parameters passed in.  Returns the resulting ResultSet 
		 */
		ResultSet rs = null;
		StringBuffer buf = new StringBuffer();
		buf.append("{ call ");
		buf.append(procName);
		buf.append("(");
		for (int i=0; i<nvpList.size(); i++){
			if (i > 0) buf.append(", ");
			buf.append("?");
		}
		buf.append(") }");
		String sql = buf.toString();
		try {
			CallableStatement cs = getDbConnection().prepareCall(sql);
			for (int i=0; i<nvpList.size(); i++){
				cs.setString( nvpList.get(i).getName(), nvpList.get(i).getValue());
			}
			rs = cs.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
        
   
        
	public void executeCUDStoredProcedure(String procName, ArrayList<List> nvpList) {
		/**
		 * Calls the stored procedure passed in, sending it
		 * the parameters passed in.  Should only be used for stored procs that create, update,
		 * or delete records
		 */
		StringBuffer buf = new StringBuffer();
		buf.append("{ call ");
		buf.append(procName);
		buf.append("(");
		for (int i=0; i<nvpList.size(); i++){
			if (i > 0) buf.append(", ");
			buf.append("?");
		}
		buf.append(") }");
		String sql = buf.toString();
		try {
			CallableStatement cs = getDbConnection().prepareCall(sql);
			for (int i=0; i<nvpList.size(); i++){
				cs.setString( nvpList.get(i).getName(), nvpList.get(i).getValue());
			}
			cs.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void makeReservation(Reservation r){
		ArrayList<List> nvpList = new ArrayList<List>();
		nvpList.add(new List(COLUMN_RES_NAME, r.getResName()));
		nvpList.add(new List(COLUMN_PARTY_SIZE, Integer.toString(r.getPartySize())));
                nvpList.add(new List(COLUMN_DAY, r.getDay()));
		nvpList.add(new List(COLUMN_HOUR, r.getHour()));
                nvpList.add(new List(COLUMN_TABLE_NAME, r.getTableName()));
                nvpList.add(new List(COLUMN_RES_ACC, r.getResAcc()));
                nvpList.add(new List(COLUMN_MONTH, r.getMonth()));
		executeCUDStoredProcedure(SP_MAKE_RESERVATION, nvpList);
	}
        
        public void updateReservation(Reservation r){
		ArrayList<List> nvpList = new ArrayList<List>();
		nvpList.add(new List(COLUMN_RES_NAME, r.getResName()));
		nvpList.add(new List(COLUMN_PARTY_SIZE, Integer.toString(r.getPartySize())));
                nvpList.add(new List(COLUMN_DAY, r.getDay()));
		nvpList.add(new List(COLUMN_HOUR, r.getHour()));
                nvpList.add(new List(COLUMN_TABLE_NAME, r.getTableName()));
                nvpList.add(new List(COLUMN_RES_ACC, r.getResAcc()));
                nvpList.add(new List(COLUMN_MONTH, r.getMonth()));
                nvpList.add(new List(COLUMN_ID, Integer.toString(r.getID())));
		executeCUDStoredProcedure(SP_UPDATE_RESERVATION, nvpList);
	}

	public void deleteReservation(Reservation r){
                ArrayList<List> nvpList = new ArrayList<List>();
		nvpList.add(new List(COLUMN_RES_NAME, r.getResName()));
		nvpList.add(new List(COLUMN_PARTY_SIZE, Integer.toString(r.getPartySize())));
                nvpList.add(new List(COLUMN_DAY, r.getDay()));
		nvpList.add(new List(COLUMN_HOUR, r.getHour()));
                nvpList.add(new List(COLUMN_TABLE_NAME, r.getTableName()));
                nvpList.add(new List(COLUMN_RES_ACC, r.getResAcc()));
                nvpList.add(new List(COLUMN_MONTH, r.getMonth()));
		executeCUDStoredProcedure(SP_DELETE_RESERVATION, nvpList);
	}
        
        public Reservation checkReservation(Reservation r){
                Reservation res = null;
                ResultSet rs = null;
                ArrayList<List> nvpList = new ArrayList<List>();
		nvpList.add(new List(COLUMN_RES_NAME, r.getResName()));
		nvpList.add(new List(COLUMN_PARTY_SIZE, Integer.toString(r.getPartySize())));
                nvpList.add(new List(COLUMN_DAY, r.getDay()));
		nvpList.add(new List(COLUMN_HOUR, r.getHour()));
                nvpList.add(new List(COLUMN_TABLE_NAME, r.getTableName()));
                nvpList.add(new List(COLUMN_RES_ACC, r.getResAcc()));
                nvpList.add(new List(COLUMN_MONTH, r.getMonth()));
		rs = executeStoredProcedure(SP_CHECK_AVAILABILITY, nvpList);
		try {
			if (rs.next()){
				res = getObjectBuilder().createReservation(rs);
			}
                    } catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
        
        public ArrayList getTodaysReservations(Reservation r){
                ArrayList list = new ArrayList();
                Reservation res = null;
                ResultSet rs = null;
                ArrayList<List> nvpList = new ArrayList<List>();
		nvpList.add(new List(COLUMN_RES_NAME, r.getResName()));
		nvpList.add(new List(COLUMN_PARTY_SIZE, Integer.toString(r.getPartySize())));
                nvpList.add(new List(COLUMN_DAY, r.getDay()));
		nvpList.add(new List(COLUMN_HOUR, r.getHour()));
                nvpList.add(new List(COLUMN_TABLE_NAME, r.getTableName()));
                nvpList.add(new List(COLUMN_RES_ACC, r.getResAcc()));
                nvpList.add(new List(COLUMN_MONTH, r.getMonth()));
		rs = executeStoredProcedure(SP_GET_TODAYS_RES, nvpList);
		try {
			while (rs.next()){
				list.add(getObjectBuilder().createReservation(rs));
			}
                    } catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
        
        public ArrayList getHourlyTables(Reservation r){
                ArrayList list = new ArrayList();
                Reservation res = null;
                ResultSet rs = null;
                ArrayList<List> nvpList = new ArrayList<List>();
		nvpList.add(new List(COLUMN_RES_NAME, r.getResName()));
		nvpList.add(new List(COLUMN_PARTY_SIZE, Integer.toString(r.getPartySize())));
                nvpList.add(new List(COLUMN_DAY, r.getDay()));
		nvpList.add(new List(COLUMN_HOUR, r.getHour()));
                nvpList.add(new List(COLUMN_TABLE_NAME, r.getTableName()));
                nvpList.add(new List(COLUMN_RES_ACC, r.getResAcc()));
                nvpList.add(new List(COLUMN_MONTH, r.getMonth()));
		rs = executeStoredProcedure(SP_GET_HOURLY_TABLES, nvpList);
		try {
			while (rs.next()){
				list.add(getObjectBuilder().createReservation(rs));
			}
                    } catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
}

