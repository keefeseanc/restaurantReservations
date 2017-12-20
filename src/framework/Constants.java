package framework;

public interface Constants {
	//STORED PROCEDURE NAMES
	public static String SP_CHECK_RESERVATION = "procChkRes";
	public static String SP_MAKE_RESERVATION = "procMkRes";
	public static String SP_UPDATE_RESERVATION = "procUpRes";
	public static String SP_DELETE_RESERVATION = "procDelRes";
        public static String SP_CHECK_AVAILABILITY = "procAvail";
        public static String SP_GET_TODAYS_RES = "procToday";
        public static String SP_GET_HOURLY_TABLES = "procHourly";
	
	//COLUMN NAMES
	public static String COLUMN_RES_NAME = "resName";
        public static String COLUMN_RES_ACC = "resAcc";
        public static String COLUMN_PARTY_SIZE = "partySize";
        public static String COLUMN_DAY = "day";
        public static String COLUMN_HOUR = "hour";
        public static String COLUMN_TABLE_NAME = "tableName";
	public static String COLUMN_MONTH = "month";
        public static String COLUMN_ID = "ID";
}


