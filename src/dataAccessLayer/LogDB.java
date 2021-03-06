/**
 * 
 */
package dataAccessLayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import businessLogicLayer.Log;
import constants.DalEnum;
import constants.Severity;
import serviceLayer.CtrlLog;

/**
 * DataBaseAccessObject
 * 
 * @author Pascal Marcouyoux
 * @url https://github.com/P-marcou
 */
public class LogDB {

	private CtrlLog			ctrlLog				= CtrlLog.getInstance( );

	private final String	SELECT_BY_ID		= "SELECT * FROM Logs WHERE identifier = ?;";

	private final String	SELECT_BY_SEVERITY	= "SELECT * FROM Logs WHERE severity = ?;";

	private final String	SELECT_ALL			= "SELECT * FROM Logs;";

	private final String	INSERT				= "INSERT INTO Logs VALUES(?,?,?,?,?);";

	private final String	UPDATE				= "UPDATE Logs SET severity = ?, date = ?, message = ?, exception = ? WHERE identifier = ?;";

	private final String	DELETE				= "DELETE FROM Logs WHERE identifier = ?;";

	public LogDB( ) {

	}

	/**
	 * This method return the Log which his id
	 * 
	 * @param id
	 * @return the log
	 * @throws SQLException
	 */
	public Log getOneById(int id) throws SQLException {
		Log returned = null;
		Connection cnx = null;

		try{
			cnx = DataBaseAccess.getConnection( );
			PreparedStatement request = cnx.prepareStatement(SELECT_BY_ID);
			request.setInt(1, id);
			ResultSet result = request.executeQuery( );

			// Moving to first and checking if it's also the last, so it's the
			// only result in this ResultSet
			result.first( );
			if(result.isLast( )){
				int identifier = result.getInt("identifier");
				Exception exception = (Exception) result.getObject("exception");
				Severity severity = Severity.values( )[result.getInt("severity")];
				Date date = result.getDate("date");
				String message = result.getString("message");

				returned = new Log(identifier, exception, severity, date, message);
			}else{
				Log logExp = new Log(Severity.Error, new Date( ),
						"The Select by id returned more than one result. Your Database might be corrupt !");

				ctrlLog.writeLog(logExp, DalEnum.TextFile);
			}
		}catch(SQLException e){
			Log logExp = new Log(e, Severity.Critical, new Date( ), "Could not connect to database!");

			ctrlLog.writeLog(logExp, DalEnum.TextFile);
			throw e;

		}catch(Exception e){
			Log logExp = new Log(e, Severity.Critical, new Date( ),
					"An exception occured while trying to connect to the database!");

			ctrlLog.writeLog(logExp, DalEnum.TextFile);
			throw e;
		}finally{
			try{
				if(cnx != null && !cnx.isClosed( )){
					cnx.close( );
				}
			}catch(SQLException e){
				Log logExp = new Log(e, Severity.Critical, new Date( ), "Could not connect to database!");

				ctrlLog.writeLog(logExp, DalEnum.TextFile);
				throw e;
			}
		}

		return returned;
	}

	public ArrayList<Log> getOneBySeverity(int svrt) throws SQLException {
		ArrayList<Log> returned = new ArrayList<Log>( );
		Log toAdd = null;
		Connection cnx = null;

		try{
			cnx = DataBaseAccess.getConnection( );
			PreparedStatement request = cnx.prepareStatement(SELECT_BY_SEVERITY);
			request.setInt(1, svrt);
			ResultSet result = request.executeQuery( );

			// Moving to first and checking if it's also the last, so it's the
			// only result in this ResultSet
			while(result.next( )){
				int identifier = result.getInt("identifier");
				Exception exception = (Exception) result.getObject("exception");
				Severity severity = Severity.values( )[result.getInt("severity")];
				Date date = result.getDate("date");
				String message = result.getString("message");

				toAdd = new Log(identifier, exception, severity, date, message);

				returned.add(toAdd);
			}
		}catch(SQLException e){
			Log logExp = new Log(e, Severity.Critical, new Date( ), "Could not connect to database!");

			ctrlLog.writeLog(logExp, DalEnum.TextFile);
			throw e;

		}catch(Exception e){
			Log logExp = new Log(e, Severity.Critical, new Date( ),
					"An exception occured while trying to connect to the database!");

			ctrlLog.writeLog(logExp, DalEnum.TextFile);
			throw e;
		}finally{
			try{
				if(cnx != null && !cnx.isClosed( )){
					cnx.close( );
				}
			}catch(SQLException e){
				Log logExp = new Log(e, Severity.Critical, new Date( ), "Could not close database!");

				ctrlLog.writeLog(logExp, DalEnum.TextFile);
				throw e;
			}
		}

		return returned;
	}

	public ArrayList<Log> getAll( ) throws SQLException {
		ArrayList<Log> returned = new ArrayList<Log>( );
		Log toAdd = null;
		Connection cnx = null;

		try{
			cnx = DataBaseAccess.getConnection( );
			PreparedStatement request = cnx.prepareStatement(SELECT_ALL);
			ResultSet result = request.executeQuery( );

			// Moving to first and checking if it's also the last, so it's the
			// only result in this ResultSet
			while(result.next( )){
				int identifier = result.getInt("identifier");
				Exception exception = (Exception) result.getObject("exception");
				Severity severity = Severity.values( )[result.getInt("severity")];
				Date date = result.getDate("date");
				String message = result.getString("message");

				toAdd = new Log(identifier, exception, severity, date, message);
				returned.add(toAdd);
			}
		}catch(SQLException e){
			Log logExp = new Log(e, Severity.Critical, new Date( ), "Could not connect to database!");

			ctrlLog.writeLog(logExp, DalEnum.TextFile);
			throw e;

		}catch(Exception e){
			Log logExp = new Log(e, Severity.Critical, new Date( ),
					"An exception occured while trying to connect to the database!");

			ctrlLog.writeLog(logExp, DalEnum.TextFile);
			throw e;
		}finally{
			try{
				if(cnx != null && !cnx.isClosed( )){
					cnx.close( );
				}
			}catch(SQLException e){
				Log logExp = new Log(e, Severity.Critical, new Date( ), "Could not close database!");

				ctrlLog.writeLog(logExp, DalEnum.TextFile);
				throw e;
			}
		}

		return returned;
	}

	public Log insert(Log log) throws SQLException {
		Connection cnx = null;
		try{
			if(null != log){
				cnx = DataBaseAccess.getConnection( );
				PreparedStatement request = cnx.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
				request.setInt(1, log.getSeverity( ).ordinal( ));
				request.setDate(2, (java.sql.Date) log.getDate( ));
				request.setString(3, log.getMessage( ));
				request.setObject(4, log.getException( ));
				request.executeUpdate( );

				ResultSet result = request.getGeneratedKeys( );

				while(result.next( )){
					int identifier = result.getInt("identifier");
					Exception exception = (Exception) result.getObject("exception");
					Severity severity = Severity.values( )[result.getInt("severity")];
					Date date = result.getDate("date");
					String message = result.getString("message");

					log = new Log(identifier, exception, severity, date, message);
				}
			}
		}catch(SQLException e){
			Log logExp = new Log(e, Severity.Critical, new Date( ), "Could not connect to database!");

			ctrlLog.writeLog(logExp, DalEnum.TextFile);
			throw e;

		}catch(Exception e){
			Log logExp = new Log(e, Severity.Critical, new Date( ),
					"An exception occured while trying to connect to the database!");

			ctrlLog.writeLog(logExp, DalEnum.TextFile);
			throw e;
		}finally{
			try{
				if(cnx != null && !cnx.isClosed( )){
					cnx.close( );
				}
			}catch(SQLException e){
				Log logExp = new Log(e, Severity.Critical, new Date( ), "Could not close database!");

				ctrlLog.writeLog(logExp, DalEnum.TextFile);
				throw e;
			}
		}

		return log;
	}

	public Log update(Log log) throws SQLException {
		Connection cnx = null;

		try{
			cnx = DataBaseAccess.getConnection( );
			PreparedStatement request = cnx.prepareStatement(UPDATE);
			request.setInt(1, log.getSeverity( ).ordinal( ));
			request.setDate(2, (java.sql.Date) log.getDate( ));
			request.setString(3, log.getMessage( ));
			request.setObject(4, log.getException( ));
			request.setInt(5, log.getIdentifier( ));

			request.executeUpdate( );
		}catch(SQLException e){
			Log logExp = new Log(e, Severity.Critical, new Date( ), "Could not connect to database!");

			ctrlLog.writeLog(logExp, DalEnum.TextFile);
			throw e;

		}catch(Exception e){
			Log logExp = new Log(e, Severity.Critical, new Date( ),
					"An exception occured while trying to connect to the database!");

			ctrlLog.writeLog(logExp, DalEnum.TextFile);
			throw e;
		}finally{
			try{
				if(cnx != null && !cnx.isClosed( )){
					cnx.close( );
				}
			}catch(SQLException e){
				Log logExp = new Log(e, Severity.Critical, new Date( ), "Could not close database!");

				ctrlLog.writeLog(logExp, DalEnum.TextFile);
				throw e;
			}
		}

		return log;
	}

	public void delete(int id) throws SQLException {
		Connection cnx = null;

		try{
			cnx = DataBaseAccess.getConnection( );
			PreparedStatement request = cnx.prepareStatement(DELETE);
			request.setInt(1, id);

			request.executeUpdate( );

		}catch(SQLException e){
			Log logExp = new Log(e, Severity.Critical, new Date( ), "Could not connect to database!");

			ctrlLog.writeLog(logExp, DalEnum.TextFile);
			throw e;

		}catch(Exception e){
			Log logExp = new Log(e, Severity.Critical, new Date( ),
					"An exception occured while trying to connect to the database!");

			ctrlLog.writeLog(logExp, DalEnum.TextFile);
			throw e;
		}finally{
			try{
				if(cnx != null && !cnx.isClosed( )){
					cnx.close( );
				}
			}catch(SQLException e){
				Log logExp = new Log(e, Severity.Critical, new Date( ), "Could not close database!");

				ctrlLog.writeLog(logExp, DalEnum.TextFile);
				throw e;
			}
		}
	}
}
