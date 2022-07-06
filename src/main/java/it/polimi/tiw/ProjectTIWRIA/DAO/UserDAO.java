package it.polimi.tiw.ProjectTIWRIA.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import it.polimi.tiw.ProjectTIWRIA.beans.User;

public class UserDAO {
	private Connection con;
	
	public UserDAO(Connection connection) {
		this.con = connection;
	}
	
	public User checkCredentials(String username, String password) throws SQLException {
		PreparedStatement pstatement = null;
		ResultSet result = null;
		User user = null;

		String query = "SELECT  id, username, name, surname FROM user  WHERE username = ? AND password = ?";
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, username);
			pstatement.setString(2, password);
			result = pstatement.executeQuery();

			if(result.next()) {
				user = new User();
				user.setId(result.getInt("id"));
				user.setUsername(result.getString("username"));
				user.setName(result.getString("name"));
				user.setSurname(result.getString("surname"));
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			throw new SQLException(e);
		} 
		finally {
			try {
				result.close();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				pstatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}		
		return user;
	}

	
	public boolean checkUsername(String username) throws SQLException {
		PreparedStatement pstatement = null;
		ResultSet result = null;
		String query = "SELECT id FROM user WHERE username = ?";
		boolean usernameFree = false;
		
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, username);
			result = pstatement.executeQuery();

			if (!result.isBeforeFirst()) { // no results
				usernameFree = true;
			}
		}

		catch (SQLException e) {
			e.printStackTrace();
			throw new SQLException(e);
		} 
		
		finally {
			try {
				result.close();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				pstatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
		
		return usernameFree;
	}
	
	
	public void createUser(String username, String name, String surname, String password) throws SQLException{
		PreparedStatement pstatement = null;
		int createdId = -1;
		String query = "INSERT into user (username, name, surname, password) VALUES(?, ?, ?, ?)";
		try {
			pstatement = con.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
			pstatement.setString(1, username);
			pstatement.setString(2, name);
			pstatement.setString(3, surname);
			pstatement.setString(4, password);
			pstatement.executeUpdate();
			
			ResultSet generatedKeys = pstatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                createdId = generatedKeys.getInt(1);
            }
			
			AccountDAO accountDAO = new AccountDAO(con);
			accountDAO.createRandomAccount(createdId);
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
}
