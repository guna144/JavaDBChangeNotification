package test;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDriver;
import oracle.jdbc.OracleStatement;
import oracle.jdbc.dcn.DatabaseChangeEvent;
import oracle.jdbc.dcn.DatabaseChangeListener;
import oracle.jdbc.dcn.DatabaseChangeRegistration;
import oracle.jdbc.dcn.RowChangeDescription;


public class Sample {

//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		System.out.print("Welcome");
//	}
	
	static final String USERNAME = "system";
    static final String PASSWORD = "bptest";
    static String URL = "jdbc:oracle:thin:@10.51.197.90:1521:xe";
     
    public static void main(String[] args) {
    	Sample oracleDCN = new Sample();
        try {
             oracleDCN.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

	
    private void run() throws Exception{
        OracleConnection conn = connect();
        Properties prop = new Properties();
        prop.setProperty(OracleConnection.DCN_NOTIFY_ROWIDS, "true");
        DatabaseChangeRegistration dcr = conn.registerDatabaseChangeNotification(prop);
         
        try{
            dcr.addListener(new DatabaseChangeListener() {
 
                public void onDatabaseChangeNotification(DatabaseChangeEvent dce) {
                	
                	RowChangeDescription[] rowsChanged = dce.getTableChangeDescription()[0].getRowChangeDescription();
                    String requestId = null;
                    String subRequestId = null;
                    String accountNumber = null;
                    String mSISDN = "'971564439259'";
                    String serial1 = "'240920170019'";
                    String rowId = null;
                    List<DataModel> dataCollection = new ArrayList<DataModel>();
                    Random  rand = new Random();
                    System.out.println("************************** DataBase operation done! ********************* "+rowsChanged.length);
                    int index = 1; int tagNumber = rand.nextInt(500); 
                    int recordCount = rowsChanged.length;
                    
                	for(RowChangeDescription  row : rowsChanged) {
                		
                		if(index == 11) { 
                			index = 1;
                			tagNumber = rand.nextInt(500);
                		}
//                		
                		rowId = row.getRowid().stringValue();
	                    System.out.println("Changed row id : "+rowId);
	                    System.out.println("Row operation : "+row.getRowOperation().toString());
	                    
	                    //synchronized(this) {
		                    try {
					            Statement stmt = conn.createStatement();
					            ((OracleStatement) stmt).setDatabaseChangeRegistration(dcr);
					            ResultSet rs = stmt.executeQuery("select * from resourcetbl where rowid='"+rowId+"'");
					            DataModel data = new DataModel();
					            
					            while (rs.next()) {
					            	data.setRequestId(rs.getString("REQUEST_ID"));
					            	data.setSubRequestId(rs.getString("SUBREQUEST_ID"));
					            	data.setAccountNumber(rs.getString("ACCOUNT_NUMBER"));
					            	data.setTestPlan(rs.getString("TEST_PLAN"));
					            	data.setChannel(rs.getString("CHANNEL"));
					            	data.setTestingType(rs.getString("TESTING_TYPE"));
					            	data.setMsisdn(rs.getString("MSISDN"));
					            	data.setSerial1(rs.getString("SERIAL1"));
					            	data.setTagName("CHANNEL2: " + rs.getString("CHANNEL")+"; " + "TESTING_TYPE: " + rs.getString("TESTING_TYPE"));
					            	dataCollection.add(data);
					            	System.out.println(dataCollection);
					            }
					            rs.close();
					            stmt.close();
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	                   // }
		                    index++;
		                    
		                    if(dataCollection != null && dataCollection.size() == recordCount) {
		                		try {
		                        	System.out.println("Start");
		                        	WSClient.startValidationService(requestId, subRequestId, accountNumber, mSISDN, serial1, dataCollection);
		    						System.out.println("End");
		    					} catch (IOException e) {
		    						e.printStackTrace();
		    					}	
		                	}
                	}// for loop end
                }
            });
             
            Statement stmt = conn.createStatement();
            ((OracleStatement) stmt).setDatabaseChangeRegistration(dcr);
            ResultSet rs = stmt.executeQuery("select * from resourcetbl where rownum=1");
            while (rs.next()) {
            }
            rs.close();
            stmt.close();
        }catch(SQLException ex){
            if (conn != null)
            {
                conn.unregisterDatabaseChangeNotification(dcr);
                conn.close();
            }
            throw ex;
        }
    }
 
    OracleConnection connect() throws SQLException {
        OracleDriver dr = new OracleDriver();
        Properties prop = new Properties();
        prop.setProperty("user", Sample.USERNAME);
        prop.setProperty("password", Sample.PASSWORD);
        return (OracleConnection) dr.connect(Sample.URL, prop);
    }
}
