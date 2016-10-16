package photoViewerDB;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import photoViewerDB.SerializeMap;

public class DBHelper {
	private DBAbstraction db = new DBAbstraction();
	
	//UIindex is key and Id in db is value
	private HashMap<Integer, Integer> UIindexToId;
	private SerializeMap serMap = new SerializeMap();
	private final String filename = "src/photoViewerDB/UIindexToId.ser";
	
	public DBHelper() {
		UIindexToId = serMap.deserialize(filename);
	}
	
	public boolean updateDescDate(int index, String desc, String date) {
		int id = UIindexToId.get(index);
		return db.updateDescDate (id, desc, date);
	}

	public boolean addPhoto (int picNumInUI, Path pathOfPhoto) {
		byte[] data = null;
		updateMap(picNumInUI, "add");
		int id = UIindexToId.get(picNumInUI);
		try {
			data = Files.readAllBytes(pathOfPhoto);
		} catch (IOException e) {
			e.printStackTrace();	
		}
		
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		if (db.insert(id, bis, (int) data.length))
			return true;
		
		//if the insertion of the new pic was unsuccessful, remove that item from the map
		UIindexToId.remove(picNumInUI);
		return false;
	}
	
	public boolean delete (int index) {
		int id = UIindexToId.get(index);
		if (db.delete(id)) {
			updateMap(index, "delete");
			return true;
		}
		return false;
	}
	
	//This method calls the select query in the DBabstraction which returns a result set
	//It takes the result set and make a Photo out of it and return that
	public Photo get(int index) {
		int id = UIindexToId.get(index);
		ResultSet rs = db.select(id);
		Photo photo = null;
		try {
			photo = rsToPhoto(rs);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return photo;
	}

	public boolean isEmpty() {
		return db.getNumPics() == 0;
	}


	public int getNumPics() {
		return db.getNumPics();
	}

	private void updateMap (int picNumInUI, String typeOfOp) {
		if (typeOfOp.equals("delete"))
			updateMapForDelete(picNumInUI);
		else if (typeOfOp.equals("add")) {
			int numPics =  db.getNumPics();
			//if there are no pics in the db, then make the first photo have an id of 0
			if (numPics == 0) 	
				UIindexToId.put(1, 0);
			else
				updateMapAdd(picNumInUI, numPics);
		}
		serMap.serialize(UIindexToId, filename);
	}
	
	private void updateMapForDelete(int numToDelete) {
		System.out.println("Delete first");
		//Remove the one the user select
		UIindexToId.remove(numToDelete);
		//Starting with the next image, we will update all subsequent items in the map
		numToDelete++;
		//while we still have items in our map
		while (UIindexToId.containsKey(numToDelete)) {
			//make the current item in the map the one before it.
			UIindexToId.put(numToDelete-1, UIindexToId.get(numToDelete));
			numToDelete++;
		}
		//remove the last one since we have already reassociated it unless it has already been removed
		if (UIindexToId.containsKey(numToDelete-1))
			UIindexToId.remove(numToDelete-1);
	}
	
	private void updateMapAdd(int picNumInUI, int numPics) {
		//We'll always make the id of the current pic in the ui to be the greatest id in the db plus 1
		int i = 1;
		int max = 0;
		//Finding the maximum id in the db
		while (UIindexToId.containsKey(i)) {
			int curVal = UIindexToId.get(i);
			if (curVal > max)
				max = curVal;
			i++;
		}
		//Reassigning all of the UIindeces after the pic that we're adding
		for (i = db.getNumPics() + 1; i > picNumInUI; --i)
			UIindexToId.put(i, UIindexToId.get(i-1));
		
		//Adding the last pic to the map
		UIindexToId.put(picNumInUI, max+1);
	}

	private Photo rsToPhoto (ResultSet rs) throws SQLException {
		rs.next();
		String desc = rs.getString(1);
		String date = rs.getString(2);
		ImageIcon image = new ImageIcon (getImage(rs));
		return (new Photo(desc, date, image));
	}

	private byte[] getImage(ResultSet rs) {
		int c;
		//boolean found = rs.next();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		InputStream in;
		try {
			in = rs.getBinaryStream("image");
			while ((c = in.read()) != -1)
				bos.write(c);
			return bos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
