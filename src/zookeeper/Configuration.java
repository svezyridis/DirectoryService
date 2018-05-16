package zookeeper;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.zookeeper.*;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
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
	private static String zoouser; 
	private static String zoopass; 
	private static ZooKeeper zoo;
	private List<String> fsList = null;
	final CountDownLatch connectedSignal = new CountDownLatch(1);
	private static Configuration ConfInstance = null;
	
	
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
	
	private ZooKeeper zooConnect() throws IOException,InterruptedException {
		System.err.println("start zooConnect");
		
		ZooKeeper zk = new ZooKeeper(getZookeeperIPs(), 3000, new Watcher() {
			@Override
			public void process(WatchedEvent we) {
				if (we.getState() == KeeperState.SyncConnected) {
					connectedSignal.countDown();
				}
			}
		});
		connectedSignal.await();
		
		//zk.addAuthInfo("digest", new String(zoouser+":"+zoopass).getBytes()); 
		
		System.out.println("finished zooConnect");

		return zk;
	}
	
class FsWatcher implements Watcher {
        
        public void process(WatchedEvent event) {
            System.err.println("Watcher triggered");
			Watcher watcher = new FsWatcher();
			watchForFsChanges(watcher);
        }
    }

	private void initFsWatches() {
		fsList = Collections.synchronizedList(new ArrayList<String>());
		Watcher watcher = new FsWatcher();
		watchForFsChanges(watcher);
	}
	
	
	private void watchForFsChanges(Watcher watcher) {
		// we want to get the list of available FS, and watch for changes
		try {
			fsList.clear();
			List<String> fsChildren = zoo.getChildren("/DirServices", watcher);
			for (String fs : fsChildren) {
				// TODO: need probably also it's associated data
				fsList.add(fs);
			}
		}
		catch (KeeperException ex) {
			System.err.println("getStatusText KeeperException "+ex.getMessage());
		}
		catch (InterruptedException ex) {
			System.err.println("getStatusText InterruptedException");
		}
		for(String f:fsList) {
			System.out.println(f);
		}
	}
	public void ReadConfigurationFile() {
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
	            zoouser=setting.getChild("zoouser").getValue();
	            zoopass=setting.getChild("zoopass").getValue();
	            identifier=classElement.getChild("identifier").getValue();    
	            secretkey=classElement.getChild("key").getValue();
	        

	            
	         } catch(JDOMException e) {
	            e.printStackTrace();
	         } catch(IOException ioe) {
	            ioe.printStackTrace();
	         }
		
	}
	
	public void PublishService(ServletContextEvent sce) {
		ACL acl = new ACL();
		try {
			String base64EncodedSHA1Digest = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA1").
					digest((zoopass).getBytes()));
			acl.setPerms(ZooDefs.Perms.ALL);
			acl.setId(new Id("digest",zoouser+":"+base64EncodedSHA1Digest));
		}
		catch (NoSuchAlgorithmException ex) {
			System.err.println("destroy NoSuchAlgorithmException");
		}
		List<ACL> aclList=new ArrayList<ACL>();
		aclList.add(acl);
		
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
	       
	       System.out.println("trying to connect to zookeeper");  
	       try {
			zoo = zooConnect();
			Stat stat = zoo.exists(dirpath, false);
			if(stat==null) {
				System.out.println("Node does not exist, creating node");
				zoo.create(dirpath, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
						CreateMode.PERSISTENT);
			}
			zoo.create(dirpath+"/"+identifier+"2", configJSON.toString().getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,
					CreateMode.EPHEMERAL);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static Configuration getInstance() {
		if (ConfInstance == null) {
			ConfInstance = new Configuration();
		}
		return ConfInstance;
	}

	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		 System.err.println("Fileservice Context destroyed");
		 //TODO close zookeeper connection

		
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ReadConfigurationFile();
		Configuration instance = getInstance();
		try {
			instance.zoo = instance.zooConnect();
			instance.PublishService(sce);
			instance.initFsWatches();
	  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    }
	
		

	
}
