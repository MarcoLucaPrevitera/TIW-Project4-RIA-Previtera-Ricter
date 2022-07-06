package it.polimi.tiw.ProjectTIWRIA.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.polimi.tiw.ProjectTIWRIA.beans.Account;

public class AccountDAO {
	private Connection con;
	
	public AccountDAO(Connection connection) {
		this.con = connection;
	}
	
	public List<Account> findAccountsByUser(int userId) throws SQLException{
		List<Account> resultList = new ArrayList<>();
		PreparedStatement pstatement = null;
		ResultSet result = null;
		String query = "SELECT  id, code, balance FROM account  WHERE user = ?";
		
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, userId);
			result = pstatement.executeQuery();

			while(result.next()) {
				Account account = new Account();
				account.setId(result.getInt("id"));
				account.setCode(result.getString("code"));
				account.setBalance(result.getDouble("balance"));
				account.setUserId(userId);
				resultList.add(account);
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
		return resultList;
	}
	
	
	public Account findAccountById(int accountId) throws SQLException{
		PreparedStatement pstatement = null;
		ResultSet result = null;
		Account account = null;
		String query = "SELECT  code, balance,user FROM account  WHERE id = ?";

		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, accountId);
			result = pstatement.executeQuery();

			if(result.next()) {
				account = new Account();
				account.setId(accountId);
				account.setCode(result.getString("code"));
				account.setBalance(result.getDouble("balance"));
				account.setUserId(result.getInt("user"));
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
		return account;	

	}
	
	
	//check and return the destination account
	public Account checkUserAccount(String username, String accountCodeNumber) throws SQLException {

		PreparedStatement pstatement = null;
		ResultSet result = null;
		Account account = null;

		String query = "SELECT  a.id, code, balance, a.user FROM account as a ,user as u  WHERE u.id=a.user AND username = ? AND code= ?" ;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, username);
			pstatement.setString(2, accountCodeNumber);
			result = pstatement.executeQuery();

			if(result.next()) {
				account = new Account();
				account.setId(result.getInt("id"));
				account.setCode(result.getString("code"));
				account.setBalance(result.getDouble("balance"));
				account.setUserId(result.getInt("user"));
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
		return account;
	}
	
	
	//update the total after transfer
	public void updateAccountBalance(int accountId, double transactionValue) throws SQLException{
		PreparedStatement pstatement = null;
		String query = "UPDATE account SET balance = balance + ? WHERE id = ?";
		try {pstatement = con.prepareStatement(query);
			pstatement.setDouble(1, transactionValue);
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
	
	public void createRandomAccount(int userId) throws SQLException{
		PreparedStatement pstatement = null;
		String randomCode = null;
		String query = "INSERT into account(code, user, balance) VALUES(?,?,0)";
		try {
			Random rand = new Random();
			int random = 100000 + rand.nextInt(900000);
			randomCode = "IT"+random+"GG";
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, randomCode);
			pstatement.setInt(2, userId);
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
	
	
	
	
	
}
