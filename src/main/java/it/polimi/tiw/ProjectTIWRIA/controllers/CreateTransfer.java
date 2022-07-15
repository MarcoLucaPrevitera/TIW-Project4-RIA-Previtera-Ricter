package it.polimi.tiw.ProjectTIWRIA.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import it.polimi.tiw.ProjectTIWRIA.DAO.AccountDAO;
import it.polimi.tiw.ProjectTIWRIA.DAO.TransferDAO;
import it.polimi.tiw.ProjectTIWRIA.beans.Account;
import it.polimi.tiw.ProjectTIWRIA.beans.Transfer;
import it.polimi.tiw.ProjectTIWRIA.beans.User;
import it.polimi.tiw.ProjectTIWRIA.utils.ConnectionHandler;


@WebServlet("/CreateTransfer")
@MultipartConfig

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
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing or incorrect transfer parameters");
			return;
		}
		
		
		try{
			accountOrig = accountDAO.findAccountById(originAccountId);
			if(accountOrig == null){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Account not found");
				return;
				}
			if(accountOrig.getUserId()!=user.getId()){
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("User not allowed");
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
				
				Transfer transfer = transferDAO.findTransferById(transferId);
				
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("account_id_dest",accountDest.getId());
				jsonObject.addProperty("code_origin",transfer.getAccountCodeOrigin());
				jsonObject.addProperty("code_dest",transfer.getAccountCodeDest());
				jsonObject.addProperty("prev_balance_origin",transfer.getBalanceOrigin());
				jsonObject.addProperty("curr_balance_origin",transfer.getBalanceOrigin() - transfer.getAmount());
				jsonObject.addProperty("prev_balance_dest",transfer.getBalanceDest());
				jsonObject.addProperty("curr_balance_dest",transfer.getBalanceDest() + transfer.getAmount());
			
				String json = new Gson().toJson(jsonObject);
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(json);
			}
			
			else {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println(errorResponse);
			}
			
			
			
		} catch(Exception e ) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("There was an error in the transfer");
			
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
