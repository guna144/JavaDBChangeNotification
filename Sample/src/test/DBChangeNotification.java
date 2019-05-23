/**
 * 
 */
package test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDriver;
import oracle.jdbc.OracleStatement;
import oracle.jdbc.dcn.DatabaseChangeRegistration;

/**
 * @author LSA-AS2444DXBDB05
 *
 */
public class DBChangeNotification {
	static final String USERNAME= "system";
	static final String PASSWORD= "bptest";
	static String URL;
	  
	  public static void main(String[] argv)
	  {
//	    if(argv.length < 1)
//	    {
//	      System.out.println("Error: You need to provide the URL in the first argument.");
//	      System.out.println("  For example: > java -classpath .:ojdbc5.jar DBChangeNotification \"jdbc:oracle:thin: @(DESCRIPTION=(ADDRESS=(PROTOCOL=tcp)(HOST=yourhost.yourdomain.com)(PORT=1521))(CONNECT_DATA=	(SERVICE_NAME=yourservicename)))\"");
//	 
//	      System.exit(1);
//	    }
	    URL = "jdbc:oracle:thin:@10.51.197.90:1521:xe";  //argv[0];
	    DBChangeNotification demo = new DBChangeNotification();
	    try
	    {
	      demo.run();
	    }
	    catch(SQLException mainSQLException )
	    {
	      mainSQLException.printStackTrace();
	    }
	  }
	 
	  void run() throws SQLException
	  {
	    OracleConnection conn = connect();
	      
	    // first step: create a registration on the server:
	    Properties prop = new Properties();
	    
	    // if connected through the VPN, you need to provide the TCP address of the client.
	    // For example:
	    // prop.setProperty(OracleConnection.NTF_LOCAL_HOST,"14.14.13.12");
	 
	    // Ask the server to send the ROWIDs as part of the DCN events (small performance
	    // cost):
	    prop.setProperty(OracleConnection.DCN_NOTIFY_ROWIDS,"true");
	// 
	//Set the DCN_QUERY_CHANGE_NOTIFICATION option for query registration with finer granularity.
	 prop.setProperty(OracleConnection.DCN_QUERY_CHANGE_NOTIFICATION,"true");
	 
	    // The following operation does a roundtrip to the database to create a new
	    // registration for DCN. It sends the client address (ip address and port) that
	    // the server will use to connect to the client and send the notification
	    // when necessary. Note that for now the registration is empty (we haven't registered
	    // any table). This also opens a new thread in the drivers. This thread will be
	    // dedicated to DCN (accept connection to the server and dispatch the events to 
	    // the listeners).
	    DatabaseChangeRegistration dcr = conn.registerDatabaseChangeNotification(prop);
	 
	    try
	    {
	      // add the listenerr:
	      DCNDemoListener list = new DCNDemoListener(this);
	      dcr.addListener(list);
	       
	      // second step: add objects in the registration:
	      Statement stmt = conn.createStatement();
	      // associate the statement with the registration:
	      ((OracleStatement)stmt).setDatabaseChangeRegistration(dcr);
	      ResultSet rs = stmt.executeQuery("select * from resourcetbl"); // where deptno='45'");
	      while (rs.next())
	      {}
	      String[] tableNames = dcr.getTables();
	      for(int i=0;i<tableNames.length;i++)
	        System.out.println(tableNames[i]+" is part of the registration.");
	      rs.close();
	      stmt.close();
	    }
	    catch(SQLException ex)
	    {
	      // if an exception occurs, we need to close the registration in order
	      // to interrupt the thread otherwise it will be hanging around.
	      if(conn != null)
	        conn.unregisterDatabaseChangeNotification(dcr);
	      throw ex;
	    }
	    finally
	    {
	      try
	      {
	        // Note that we close the connection!
	        conn.close();
	      }
	      catch(Exception innerex){ innerex.printStackTrace(); }
	    }
	    
//	    synchronized( this ) 
//	    {
//	      // The following code modifies the dept table and commits:
//	      try
//	      {
//	        OracleConnection conn2 = connect();
//	        conn2.setAutoCommit(false);
//	        Statement stmt2 = conn2.createStatement();
//	        stmt2.executeUpdate("Insert into resourcetbl (REQUEST_ID,SUBREQUEST_ID,ACCOUNT_NUMBER,STATUS) values (571,1945550,'0503770847',90)",
//	Statement.RETURN_GENERATED_KEYS);
//	        ResultSet autoGeneratedKey = stmt2.getGeneratedKeys();
//	        if(autoGeneratedKey.next())
//	          System.out.println("inserted one row with ROWID="+autoGeneratedKey.getString(1));      
//	        stmt2.executeUpdate("Insert into resourcetbl (REQUEST_ID,SUBREQUEST_ID,ACCOUNT_NUMBER,STATUS) values (577,1945556,'0503771695',90)",
//	Statement.RETURN_GENERATED_KEYS);
//	        autoGeneratedKey = stmt2.getGeneratedKeys();
//	        if(autoGeneratedKey.next())
//	          System.out.println("inserted one row with ROWID="+autoGeneratedKey.getString(1));
//	        stmt2.close();
//	        conn2.commit();
//	        conn2.close();
//	      }
//	      catch(SQLException ex) { ex.printStackTrace(); }
//	 
//	      // wait until we get the event
//	      try{ this.wait();} catch( InterruptedException ie ) {}
//	    }
	    
	    // At the end: close the registration (comment out these 3 lines in order
	    // to leave the registration open).
	    OracleConnection conn3 = connect();
	    conn3.unregisterDatabaseChangeNotification(dcr);
	    conn3.close();
	  }
	  
	  /**
	   * Creates a connection the database.
	   */
	  OracleConnection connect() throws SQLException
	  {
	    OracleDriver dr = new OracleDriver();
	    Properties prop = new Properties();
	    prop.setProperty("user",DBChangeNotification.USERNAME);
	    prop.setProperty("password",DBChangeNotification.PASSWORD);
	    return (OracleConnection)dr.connect(DBChangeNotification.URL,prop);
	  }
}