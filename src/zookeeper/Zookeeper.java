package zookeeper;
import static org.junit.Assert.*;

import java.util.List;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;




public class Zookeeper {
	private static ZKClientManager zkmanager = new ZKClientManager(Configuration.getZookeeperIPs());
	// ZNode Path
	private String path = "/DirService";
	byte[] data = "somedata".getBytes();



	public static void Create(String path, String data) throws KeeperException, InterruptedException {
		// data in byte array
		byte[] databytes=data.getBytes();
		zkmanager.create(path, databytes);
		//Stat stat = zkmanager.getZNodeStats(path);
		//assertNotNull(stat);
		//zkmanager.delete(path);
	}

	public static void CreatePersistent(String path, String data) throws KeeperException, InterruptedException {
		// data in byte array
		byte[] databytes=data.getBytes();
		zkmanager.createPersistent(path, databytes);
		//Stat stat = zkmanager.getZNodeStats(path);
		//assertNotNull(stat);
		//zkmanager.delete(path);
	}

	public void testGetZNodeStats(String path) throws KeeperException,
			InterruptedException {
		Stat stat = zkmanager.getZNodeStats(path);
		assertNotNull(stat);
		assertNotNull(stat.getVersion());
		zkmanager.delete(path);

	}
	public static boolean NodeExists(String path) throws KeeperException, InterruptedException{
		Stat stat = zkmanager.getZNodeStats(path);
		return (stat!=null);
	}


	public void testGetZNodeData() throws KeeperException, InterruptedException {
		zkmanager.create(path, data);
		String data = (String)zkmanager.getZNodeData(path,false);
		assertNotNull(data);
		zkmanager.delete(path);
	}


	public void testUpdate() throws KeeperException, InterruptedException {
		zkmanager.create(path, data);
		String data = "www.java.globinch.com Updated Data";
		byte[] dataBytes = data.getBytes();
		zkmanager.update(path, dataBytes);
		String retrivedData = (String)zkmanager.getZNodeData(path,false);
		assertNotNull(retrivedData);
		zkmanager.delete(path);
	}


	public void testGetZNodeChildren() throws KeeperException, InterruptedException {
		zkmanager.create(path, data);
		List<String> children= zkmanager.getZNodeChildren(path);
		assertNotNull(children);
		zkmanager.delete(path);
	}


	public void testDelete() throws KeeperException, InterruptedException {
		zkmanager.create(path, data);
		zkmanager.delete(path);
		Stat stat = zkmanager.getZNodeStats(path);
		assertNull(stat);
	}

}
