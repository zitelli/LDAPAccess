package br.com.alura.LDAPAccess.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LogUtil 
{
	private Logger logger;	
	
	public LogUtil(Class<?> classe)
	{
		 try 
		 {
			logger =  Logger.getLogger(classe.getName());		
			logger.setUseParentHandlers(false);
	        MyFormatter formatter = new MyFormatter();
	        ConsoleHandler handler = new ConsoleHandler();   
			handler.setFormatter(formatter);  
		    logger.addHandler(handler);
		    
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Logger logger() 
	{
		return logger;	
	}
}
class MyFormatter extends java.util.logging.Formatter {
    //
    // Create a DateFormat to format the logger timestamp.
    //
    private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
 
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder(1000);
        builder.append(df.format(new Date(record.getMillis()))).append(" - ");
        builder.append("[").append(record.getSourceClassName()).append(".");
        builder.append(record.getSourceMethodName()).append("] - ");
        builder.append("[").append(record.getLevel()).append("] - ");
        builder.append(formatMessage(record));
        builder.append("\n");
        return builder.toString();
    }
 
    public String getHead(Handler h) {
        return super.getHead(h);
   }

    public String getTail(Handler h) {
        return super.getTail(h);
    }
}
