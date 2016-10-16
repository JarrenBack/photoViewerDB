package photoViewerDB;

import javax.swing.ImageIcon;

public class Photo {
	private String description, date;
	private ImageIcon image;
	
	Photo(String desc, String date, ImageIcon image) {
		this.description = desc;
		this.date = date;
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public String getDate() {
		return date;
	}

	public ImageIcon getImageIcon() {
		return image;
	}
}