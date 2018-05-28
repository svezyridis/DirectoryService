package zookeeper;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


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

import storage.FileServices;



/**
 * Application Lifecycle Listener implementation class Zooconf
 *
 */
@WebListener
public class Configuration implements ServletContextListener {
	private static  List<String> zookeeperIPs = new ArrayList<String>();
	private String host="";
	private static String myip;
	private static String identifier;
	private static String secretkey;
	private static String name;
	private static String dirpath;
	private static String authpath;
	private static String strgpath;
	private static String zoouser; 
	private static String zoopass; 
	private  ZooKeeper zoo;
	private List<String> fsList = null;
	private List<Map> Systems=new ArrayList<Map>();
	final CountDownLatch connectedSignal = new CountDownLatch(1);
	private static Configuration ConfInstance = null;
	private static String DBURL;
	private static String DBUSER;
	private static String DBPASS;
	public static String getDBURL() {
		Configuration instance = getInstance();
		return instance.DBURL;
	}
	public static String getDBUSER() {
		Configuration instance = getInstance();
		return instance.DBUSER;
	}
	public static String getDBPASS() {
		Configuration instance = getInstance();
		return instance.DBPASS;
	}
	
	public static List<Map> getAvailableFs() {
		Configuration instance = getInstance();
		return instance.Systems;
	}	
	
	public static String getKey() {
		Configuration instance = getInstance();
		return instance.secretkey;
	}
	
	public static String getMyIdentifier() {
		Configuration instance = getInstance();
		return instance.identifier;
	}
	
	public static String getMyIP() {
		Configuration instance = getInstance();
		return instance.myip;
	}
	
	public static String getZookeeperIPs(){
		Configuration instance = getInstance();
		
		boolean first=true;
		for (String ip:zookeeperIPs) {
			if (first) {
				instance.host+=ip;
				first =false;
			}
			else
				instance.host=instance.host+","+ip;				
		}
		return instance.host;
	}
	
	private ZooKeeper zooConnect() throws IOException,InterruptedException {
		System.out.println("start zooConnect on "+getZookeeperIPs());
		
		ZooKeeper zk = new ZooKeeper(getZookeeperIPs(), 3000, new Watcher() {
			@Override
			public void process(WatchedEvent we) {
				if (we.getState() == KeeperState.SyncConnected) {
					connectedSignal.countDown();
				}
			}
		});
		connectedSignal.await();
		
		zk.addAuthInfo("digest", new String(zoouser+":"+zoopass).getBytes());
		
		System.out.println("finished zooConnect");

		return zk;
	}

	public static ZooKeeper getZooConnection() {
		Configuration instance = getInstance();
		return instance.zoo;
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
		Configuration instance = getInstance();
		try {
			Systems.clear();
			List<String> fsChildren = zoo.getChildren(strgpath, watcher);
			for (String fs : fsChildren) {
				Map<String,String> system = null;
				Stat stat = zoo.exists(strgpath+"/"+fs, watcher);
				byte[] data=zoo.getData(strgpath+"/"+fs, watcher, stat);
				JSONObject datajson=new JSONObject(new String(data));
				system=new HashMap<String,String>();
				system.put("identifier", datajson.getString("id"));
				system.put("URL", datajson.getString("URL"));
				system.put("keybase64", datajson.getString("key"));
				Systems.add(system);	
			}
		}
		catch (KeeperException ex) {
			System.err.println("getStatusText KeeperException "+ex.getMessage());
		}
		catch (InterruptedException ex) {
			System.err.println("getStatusText InterruptedException");
		}
		FileServices.updateDB();
		
	}
	public void ReadConfigurationFile() {
		Configuration instance=getInstance();
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
	            System.out.println("\nCurrent Element dirservice :" 
		                  + setting.getName());
	            List<Element>ips=setting.getChildren("zooip");
	            for(Element ip:ips) {
	            	instance.zookeeperIPs.add(ip.getValue().toString());
	            	System.out.println(ip.getValue().toString());
	                
	            	
	            }
	            instance.dirpath=setting.getChild("dirservicepath").getValue();
	            instance.authpath=setting.getChild("authservicepath").getValue();
	            instance.strgpath=setting.getChild("storageservicepath").getValue();
	            instance.zoouser=setting.getChild("zoouser").getValue();
	            instance.zoopass=setting.getChild("zoopass").getValue();
	            instance.identifier=classElement.getChild("identifier").getValue();    
	            instance.secretkey=classElement.getChild("key").getValue();
	            instance.name=classElement.getChild("name").getValue();
	            instance.myip=classElement.getChild("hostname").getValue();
	            instance.DBURL=classElement.getChild("dburl").getValue();
	            instance.DBUSER=classElement.getChild("dbuser").getValue();
	            instance.DBPASS=classElement.getChild("dbpass").getValue();
	            
	        

	            
	         } catch(JDOMException e) {
	            e.printStackTrace();
	         } catch(IOException ioe) {
	            ioe.printStackTrace();
	         }
		
	}
	public void PublishService(ServletContextEvent sce) {
		Configuration instance=getInstance();
		ACL acl = null;
		try {
			String base64EncodedSHA1Digest = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA1").digest((zoouser+":"+zoopass).getBytes()));
			acl = new ACL(ZooDefs.Perms.ALL, new Id("digest",zoouser+":" + base64EncodedSHA1Digest));
		}
		catch (NoSuchAlgorithmException ex) {
			System.err.println("destroy NoSuchAlgorithmException");
		}
		
	     
	       JSONObject configJSON=new JSONObject();
	     
	       configJSON.put("key", instance.secretkey);
	       configJSON.put("id", instance.identifier);
	       configJSON.put("name", instance.name);
	       configJSON.put("URL", instance.myip);
	       
	       System.out.println("publishing service");  
	       try {
			Stat stat = instance.zoo.exists(dirpath, false);
			if(stat==null) {
				System.out.println("Node does not exist, creating node");
				instance.zoo.create(dirpath, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
						CreateMode.PERSISTENT);
			}
			instance.zoo.create(dirpath+"/"+identifier, configJSON.toString().getBytes(),Arrays.asList(acl),
					CreateMode.EPHEMERAL);
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
		 System.err.println("Dirservice Context destroyed");
		 Configuration instance = getInstance();
			try {
				if (instance.zoo != null) {
					instance.zoo.close();
				}
			}
			catch ( InterruptedException ex) {
				System.err.println("destroy InterruptedException");
			}

		
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		Configuration instance = getInstance();
		instance.ReadConfigurationFile();
	
		try {
			instance.zoo = instance.zooConnect();
			instance.PublishService(sce);
			instance.initFsWatches();
			FileServices.updateDB();
	  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    }
	
		

	
}
