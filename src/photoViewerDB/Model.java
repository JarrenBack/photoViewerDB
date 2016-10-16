package photoViewerDB;

import java.nio.file.Path;
import javax.swing.ImageIcon;

public class Model {
	private DBHelper dbHelper = new DBHelper();
	private Photo nullPhoto = new Photo (null, null, null);
	
	public boolean addPhoto (int picNumInUI, Path pathOfPhoto) {
		return dbHelper.addPhoto(picNumInUI, pathOfPhoto);
	}
	
	//To-do if the db wasn't able to delete the photo then return false
	public boolean deletePhoto(int numPicToDelete) {
		if (numPicToDelete < 0 || dbHelper.isEmpty())
			return false;
		if (dbHelper.delete(numPicToDelete))
			return true;
		return false;
	}
	
	//To-do if the db wasn't able to update the photo then return false
	public void saveDescDate(int picNumInUI, String desc, String date) {
		int numPics = dbHelper.getNumPics();
		if (picNumInUI <= 0 || dbHelper.isEmpty() || picNumInUI >= numPics) 
			return;
		dbHelper.updateDescDate (picNumInUI, desc, date);
	}
	
	public int getNumPhotos() {
		return dbHelper.getNumPics();
	}
	
	public Photo getPic(int picNumInUI) {
		int numPics = dbHelper.getNumPics();
		if (picNumInUI < 0 || dbHelper.isEmpty() || picNumInUI > numPics)
			return nullPhoto;
		Photo p = dbHelper.get(picNumInUI);
		if (!p.equals(null))
			return p;
		return nullPhoto;
	}
}
