package photoViewerDB;

import java.sql.*;
import java.io.*;

public class DBAbstraction {
	private Connection con;
	private Statement stmt;
	private int numPics;
	
	public DBAbstraction() {
		connectToDB();
		numPics = queryNumPics();
	}
	
	private void connectToDB() {
        String url = "jdbc:mysql://kc-sce-appdb01.kc.umkc.edu/jwbpp5";
        String userID = "jwbpp5";
        String password = "ABHtdq4kbT";
   
        try {
            Class.forName("com.mysql.jdbc.Driver");
            
        } catch(java.lang.ClassNotFoundException e) {
            System.out.println(e);
            System.exit(0);
        }
        try {
			con = DriverManager.getConnection(url,userID,password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
        try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private int queryNumPics() {
		String sqlNumPics = "SELECT COUNT(*) FROM photos";
		int numPics = 0;
		try {
			ResultSet rs = stmt.executeQuery(sqlNumPics);
			rs.next();
			numPics = rs.getInt(1);
			System.out.println("Num pics: " + numPics);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return numPics;	
	}
	
	public int getNumPics() {
		return numPics;
	}
	
	public ResultSet select(int id) {
		String sqlGetPic = "SELECT description, date, image FROM photos WHERE id=?";
		ResultSet result = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(sqlGetPic);
			pstmt.setInt(1, id);
			System.out.println(pstmt);
			result = pstmt.executeQuery();
			//pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean delete(int id) {
		String sqlDeletePic = "DELETE FROM photos WHERE id = ?";
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(sqlDeletePic);
			pstmt.setInt(1, id);
			System.out.println(pstmt);
			pstmt.executeUpdate();
			//pstmt.close();
			numPics--;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean updateDescDate(int id, String desc, String date) {
		String sqlUpdate = "UPDATE photos SET description = ?, date = ? WHERE id = ?";
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(sqlUpdate);
			pstmt.setString(1, desc);
			pstmt.setString(2, date);
			pstmt.setInt(3, id);
			System.out.println(pstmt);
			pstmt.executeUpdate();
			//pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
		
	}

	public boolean insert(int id, ByteArrayInputStream bis, int length) {
		String sqlInsert = "INSERT INTO photos VALUES ((?),(?),(?),(?))";
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(sqlInsert);
			pstmt.setInt(1, id);
			pstmt.setString(2, "Description goes here");
			pstmt.setString(3, "Date goes here");
			pstmt.setBinaryStream(4, bis, (int) length);
			pstmt.executeUpdate(); // execute prepared statement
			numPics++;
			//pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}