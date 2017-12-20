package framework;

import java.sql.ResultSet;
import java.sql.SQLException;

import DataObject.Reservation;

public class OBuilder {
	private static OBuilder objBuilder = null;
	private OBuilder(){};
	public static OBuilder getObjectBuilder(){
		if (objBuilder == null){
			objBuilder = new OBuilder();
		}
		return objBuilder;
	}
	
	public Reservation createReservation(ResultSet rs){
                Reservation r = null;
		try{
			String resName = rs.getString(Constants.COLUMN_RES_NAME);
			int partySize = rs.getInt(Constants.COLUMN_PARTY_SIZE);
                        String day = rs.getString(Constants.COLUMN_DAY);
			String hour = rs.getString(Constants.COLUMN_HOUR);
			String tableName = rs.getString(Constants.COLUMN_TABLE_NAME);
                        String resAcc = rs.getString(Constants.COLUMN_RES_ACC);
                        String month = rs.getString(Constants.COLUMN_MONTH);
                        int    ID    = rs.getInt(Constants.COLUMN_ID);
			r = new Reservation(resName,partySize,day,hour,tableName,resAcc,month,ID);
		}catch (SQLException e){
			e.printStackTrace();
		}
		return r;
        }
}

