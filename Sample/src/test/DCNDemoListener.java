/**
 * 
 */
package test;

import oracle.jdbc.dcn.DatabaseChangeEvent;
import oracle.jdbc.dcn.DatabaseChangeListener;

/**
 * @author LSA-AS2444DXBDB05
 *
 */
public class DCNDemoListener implements DatabaseChangeListener{

	DBChangeNotification demo;
	  DCNDemoListener(DBChangeNotification dem)
	  {
	    demo = dem;
	  }
	  public void onDatabaseChangeNotification(DatabaseChangeEvent e)
	  {
	    Thread t = Thread.currentThread();
	    System.out.println("DCNDemoListener: got an event ("+this+" running on thread "+t+")");
	    System.out.println(e.toString());
	    synchronized( demo ){ demo.notify();}
	  }
	
}
