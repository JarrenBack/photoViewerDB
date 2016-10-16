package photoViewerDB;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;


public class SerializeMap {
	public void serialize(HashMap<Integer,Integer> UIindexToId, String fileName) {
		try ( ObjectOutputStream out = 
				new ObjectOutputStream(new FileOutputStream(fileName))) {
			out.writeObject(UIindexToId);
		} catch (IOException ex) {
			System.out.print("IOException occured during serialization");
		}
	}
	
	public HashMap<Integer,Integer> deserialize(String fileName) {
		HashMap<Integer,Integer> UIindexToId = null;
		try ( ObjectInputStream in = 
				new ObjectInputStream(new FileInputStream(fileName))) {
			UIindexToId = (HashMap<Integer,Integer>) in.readObject();
		} catch (IOException | ClassNotFoundException ex) {
			System.out.println("Exception occured during deserialization");
		}
		return UIindexToId;
	}
}
