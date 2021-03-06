package dataAccessLayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;

import businessLogicLayer.Log;
import constants.DalEnum;
import constants.Severity;
import serviceLayer.CtrlLog;

public class DataBaseAccess {

	public static Connection getConnection( ) {
		CtrlLog ctrlLog = CtrlLog.getInstance( );

		Connection cnx = null;
		try{
			Class.forName("YOUR SQL DRIVER CLASS NAME");
			Log driverOk = new Log(Severity.Information, new Date( ), "Driver O.K.");

			ctrlLog.writeLog(driverOk, DalEnum.TextFile);

			String url = "jdbc:DATABASE://DATABASE_ADDRESS";
			String user = "USERNAME";
			String passwd = "PASSWORD";

			cnx = DriverManager.getConnection(url, user, passwd);

		}catch(SQLException e){
			Log log = new Log(e, Severity.Critical, new Date( ), "Could not connect to database!");

			ctrlLog.writeLog(log, DalEnum.TextFile);
		}catch(Exception e){
			Log log = new Log(e, Severity.Critical, new Date( ),
					"An exception occured while trying to connect to the database!");

			ctrlLog.writeLog(log, DalEnum.TextFile);
		}

		return cnx;
	}
}
