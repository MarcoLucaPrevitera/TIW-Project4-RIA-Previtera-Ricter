package it.polimi.tiw.ProjectTIW.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import it.polimi.tiw.ProjectTIW.beans.*;

public class TransferDAO {
   private Connection con;
   
   public TransferDAO(Connection connection) {
		this.con = connection;
	}
	
   public List<Transfer> findTransferByAccount(int accountId) throws SQLException{
	   ResultSet result = null;
	   PreparedStatement pstatement = null;
	   List<Transfer> transfers = new ArrayList<>();
	   String queryString = "SELECT t.account_origin, t.motivation, t.date, t.amount, orig.code AS code_orig, dest.code AS code_dest FROM transfer t JOIN account orig JOIN account dest ON t.account_origin=orig.id AND t.account_dest = dest.id  WHERE t.account_dest=? OR t.account_origin=? ORDER BY date DESC";

	   try{
		   pstatement = con.prepareStatement(queryString);
		   pstatement.setInt(1,accountId);
		   pstatement.setInt(2, accountId);
		   result = pstatement.executeQuery();

		   while (result.next()) {
			   Transfer transfer = new Transfer(); 
			   transfer.setDate(result.getTimestamp("date"));
			   transfer.setMotivation(result.getString("motivation"));
			   int accountOrigin = result.getInt("account_origin");
			   if(accountOrigin==accountId) {
				   transfer.setAmount(-result.getDouble("amount"));
				 
			   }
			   else {
				   transfer.setAmount(result.getDouble("amount"));
			   }
			   transfer.setAccountCodeDest(result.getString("code_dest"));
			   transfer.setAccountCodeOrigin(result.getString("code_orig"));
			   transfers.add(transfer);
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
	   return transfers;   
   }
	  
   
   
   public int createTransfer(Account accOrig, Account accDest, String motivation, Timestamp date, double amount) throws SQLException{
	   int transferId = -1;
	   AccountDAO accountDAO = new AccountDAO(con);
	   PreparedStatement pstatement = null;
	   String queryString = "INSERT into transfer(account_origin, account_dest, balance_origin, balance_dest, motivation, date, amount) VALUES(?,?,?,?,?,?,?)";

	   con.setAutoCommit(false);
		try {
			//1st insert in transfer
			pstatement = con.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);
			pstatement.setInt(1, accOrig.getId());
			pstatement.setInt(2, accDest.getId());
			pstatement.setDouble(3, accOrig.getBalance());
			pstatement.setDouble(4, accDest.getBalance());
			pstatement.setString(5, motivation);
			pstatement.setTimestamp(6, date);
			pstatement.setDouble(7, amount);
			pstatement.executeUpdate();
			
			//2nd update origin account
			accountDAO.updateAccountBalance(accOrig.getId(),-amount);
			
			//3rd update destination account
			accountDAO.updateAccountBalance(accDest.getId(),amount);
			con.commit();
			
			ResultSet generatedKeys = pstatement.getGeneratedKeys();
	            if (generatedKeys.next()) {
	                transferId = generatedKeys.getInt(1);
	            }
		}
		
		catch (SQLException e) {
			con.rollback();
			e.printStackTrace();
			throw new SQLException(e);
		}
		
		finally {
			   try {
				   pstatement.close();
				   con.setAutoCommit(true);
			   } catch (Exception e1) {
				   throw new SQLException(e1);
			   }
		}
		
		return transferId;
	}

   public Transfer findTransferById (int transferId) throws SQLException {
	   Transfer transfer = null;
	   PreparedStatement pstatement = null;
	   ResultSet result = null;
	   String query = "SELECT t.account_origin, t.motivation, t.date, t.amount, t.balance_origin, t.balance_dest, orig.code AS code_orig, dest.code AS code_dest FROM transfer t JOIN account orig JOIN account dest ON t.account_origin=orig.id AND t.account_dest = dest.id  WHERE t.id = ?";
	   
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, transferId);
			result = pstatement.executeQuery();

			if(result.next()) {
				transfer = new Transfer();
				transfer.setDate(result.getTimestamp("date"));
				transfer.setMotivation(result.getString("motivation"));
				transfer.setAccountId(result.getInt("account_origin"));
				transfer.setAccountCodeDest(result.getString("code_dest"));
				transfer.setAccountCodeOrigin(result.getString("code_orig"));
				transfer.setBalanceOrigin(result.getDouble("balance_origin"));
				transfer.setBalanceDest(result.getDouble("balance_dest"));
				transfer.setAmount(result.getDouble("amount"));
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
	  return transfer; 
   }
  

}
