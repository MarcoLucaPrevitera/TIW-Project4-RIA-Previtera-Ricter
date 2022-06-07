package it.polimi.tiw.ProjectTIW.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.ProjectTIW.DAO.AccountDAO;
import it.polimi.tiw.ProjectTIW.DAO.TransferDAO;
import it.polimi.tiw.ProjectTIW.beans.Account;
import it.polimi.tiw.ProjectTIW.beans.User;
import it.polimi.tiw.ProjectTIW.utils.ConnectionHandler;


@WebServlet("/CreateTransfer")
public class CreateTransfer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

       

    public CreateTransfer() {
        super();
    }
    
    public void init() throws ServletException {
    	connection = ConnectionHandler.getConnection(getServletContext());
    
	}



	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		TransferDAO transferDAO = new TransferDAO(connection);
		AccountDAO accountDAO = new AccountDAO(connection);
		HttpSession session = request.getSession();
		
		User user = (User) session.getAttribute("user");
		
		Account accountOrig = null;
		Account accountDest = null;
		
		String usernameDest = null;
		String codeAccountDest = null;
		String motivation = null;
		double amount = -1 ;
		int originAccountId = -1;
		
		String errorResponse = null;
		
		try {
			usernameDest = StringEscapeUtils.escapeJava(request.getParameter("usernameDest"));
			codeAccountDest = StringEscapeUtils.escapeJava(request.getParameter("codeAccountDest"));
			motivation = StringEscapeUtils.escapeJava(request.getParameter("motivation"));
			amount = Double.parseDouble(request.getParameter("amount"));
			originAccountId = Integer.parseInt(request.getParameter("originAccount"));
			
			if(usernameDest == null || codeAccountDest== null || motivation==null || usernameDest.isEmpty() || codeAccountDest.isEmpty() || motivation.isEmpty() || amount < 0.01 || originAccountId == -1 || motivation.isBlank()){
				throw new Exception("Missing or incorrect transfer parameters");
			}
		}
		catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect transfer parameters");
			return;
		}
		
		
		try{
			accountOrig = accountDAO.findAccountById(originAccountId);
			if(accountOrig == null){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Account not found");
				return;
				}
			if(accountOrig.getUserId()!=user.getId()){
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed");
				return;
				}
			
			accountDest = accountDAO.checkUserAccount(usernameDest,codeAccountDest);
			
			if (amount>accountOrig.getBalance()) {
				errorResponse = "There are insufficient funds in your account! ";
			}
			else if (accountDest==null){
				errorResponse = "The user does not match the given Account Number! ";
			}
			else if(accountOrig.getId() == accountDest.getId()) {
				errorResponse = "Account Number payer and payee coincide! ";
			}
			
			
			if(errorResponse == null) {
				Date date = new Date();
				long time = date.getTime();
				Timestamp ts = new Timestamp(time);
				int transferId = transferDAO.createTransfer(accountOrig,accountDest,motivation,ts,amount);
				
	
				
			}
			
			else {


			}
			
			
			
		} catch(Exception e ) {
			
		}
		
	}
	
	
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
