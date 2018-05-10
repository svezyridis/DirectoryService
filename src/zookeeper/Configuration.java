package zookeeper;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.zookeeper.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.json.JSONObject;

import java.io.*;


/**
 * Application Lifecycle Listener implementation class Zooconf
 *
 */
@WebListener
public class Configuration implements ServletContextListener {
	private static  List<String> zookeeperIPs = new ArrayList<String>();
	private static  String myip;
	private static String identifier;
	private static String secretkey;
	private static String dirpath;
	private static String authpath;
	private static String strgpath;
	
	public static String getKey() {
		return secretkey;
	}
	public static String getMyIdentifier() {
		return identifier;
	}
	public static String getMyIP() {
		return myip;
	}
	public static String getZookeeperIPs(){
		String host="";
		boolean first=true;
		for (String ip:zookeeperIPs) {
			if (first) {
				host+=ip;
				first =false;
			}
			else
				host=host+","+ip;				
		}
		return host;
	}

	   
	


	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		 System.err.println("Fileservice Context destroyed");
		 //TODO close zookeeper connection

		
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		URL resource = getClass().getResource("/");
		String path = resource.getPath();
		path = path.replace("WEB-INF/classes/", "conf/config.xml");
		//Read configuration file
	        try {
	            File inputFile = new File(path);
	            SAXBuilder saxBuilder = new SAXBuilder();
	            Document document = saxBuilder.build(inputFile);
	            System.out.println("Root element :" + document.getRootElement().getName());
	            Element classElement = document.getRootElement();
	            System.out.println("----------------------------");
	            Element setting=classElement.getChild("zookeeper");
	            System.out.println("\nCurrent Element :" 
		                  + setting.getName());
	            List<Element>ips=setting.getChildren("zooip");
	            for(Element ip:ips) {
	            	zookeeperIPs.add(ip.getValue().toString());
	                
	            	
	            }
	            dirpath=setting.getChild("dirservicepath").getValue();
	            authpath=setting.getChild("authservicepath").getValue();
	            strgpath=setting.getChild("storageservicepath").getValue();
	            identifier=classElement.getChild("identifier").getValue();    
	            secretkey=classElement.getChild("key").getValue();
	        

	            
	         } catch(JDOMException e) {
	            e.printStackTrace();
	         } catch(IOException ioe) {
	            ioe.printStackTrace();
	         }
	       try {
			myip= InetAddress.getLocalHost().toString();
			myip=myip+"/"+sce.getServletContext().getServletContextName();
			System.out.println(myip);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	       JSONObject configJSON=new JSONObject();
	       configJSON.put("URL", myip);
	       configJSON.put("key", secretkey);
	       configJSON.put("id", identifier);
	       
	       
	       
	       try {
	    	   System.out.println("trying to connect to zookeeper");  
			
			System.out.println("Checking if "+dirpath+" node exists");
			if(!Zookeeper.NodeExists(dirpath)) {
				Zookeeper.CreatePersistent(dirpath, "");
			}
			Zookeeper.Create(dirpath+"/"+identifier+"2",configJSON.toString());
			
		} catch (KeeperException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
	       
	       
	      
	       
	    }
	
		

	
}
