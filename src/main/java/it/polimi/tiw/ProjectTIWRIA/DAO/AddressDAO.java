package it.polimi.tiw.ProjectTIWRIA.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.ProjectTIWRIA.beans.Contact;
public class AddressDAO {

private Connection con;
	
	public AddressDAO(Connection connection) {
		this.con = connection;
	}
	
	public void addAddressBook(int userId, int accountId) throws SQLException{
		PreparedStatement pstatement = null;
		String query = "INSERT into address_book (user, contact_account) VALUES(?, ?)";
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, userId);
			pstatement.setInt(2, accountId);
			pstatement.executeUpdate();
		}
		
		catch (SQLException e) {
			e.printStackTrace();
			throw new SQLException(e);
		}
		
		finally {
			try {
				pstatement.close();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
		}
	}
	
	public List<Contact> getAddressBook(int userId) throws SQLException {
		PreparedStatement pstatement =null;
		List<Contact> contacts = new ArrayList<>();
		ResultSet result = null;
		
		String query = "SELECT a.code , u.username FROM address_book AS ab, user AS u, account AS a WHERE ab.contact_account = a.id AND a.user = u.id AND ab.user = ?";
		
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, userId);
			result = pstatement.executeQuery();	
			
			while (result.next()) {
				   Contact contact = new Contact(); 
				   contact.setAccountCode(result.getString("code"));
				   contact.setUsername(result.getString("username"));
				   contacts.add(contact);
			}
		}
		
		catch (SQLException e) {
			e.printStackTrace();
			throw new SQLException(e);
		}
		
		finally {
			try {
				pstatement.close();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
		}
		
		return contacts;
	}

	
}
