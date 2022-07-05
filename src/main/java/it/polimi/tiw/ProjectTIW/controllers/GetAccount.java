package it.polimi.tiw.ProjectTIW.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.polimi.tiw.ProjectTIW.DAO.AccountDAO;
import it.polimi.tiw.ProjectTIW.DAO.TransferDAO;
import it.polimi.tiw.ProjectTIW.beans.Account;
import it.polimi.tiw.ProjectTIW.beans.AccountDetail;
import it.polimi.tiw.ProjectTIW.beans.Transfer;
import it.polimi.tiw.ProjectTIW.beans.User;
import it.polimi.tiw.ProjectTIW.utils.*;


@WebServlet("/AccountDetails")
public class GetAccount extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection con;
	public GetAccount() {
		super(); 
	}

	public void init() throws ServletException {
		con = ConnectionHandler.getConnection(getServletContext());}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int accountId;
		HttpSession session = request.getSession();
		try { 
			accountId = Integer.parseInt(request.getParameter("accountId"));
		}
		catch (NumberFormatException e){
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad parameter");
			return;
		}
		
		User user =(User) session.getAttribute("user");
		AccountDAO accountDAO = new AccountDAO(con);
		Account account = null;
		List<Transfer> transfers = null;
		
		try {
			account = accountDAO.findAccountById(accountId);
			if(account == null)
			  {response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Account not found");
				return;}
			//if(account.getUserId()!=user.getId())
			//  {response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed");
			//	return;}
			TransferDAO transferDAO = new TransferDAO(con);
			transfers = transferDAO.findTransferByAccount(accountId);
			
			
			AccountDetail accountDetail = new AccountDetail(account,transfers);
			String json = new Gson().toJson(accountDetail);
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
		}
		catch(SQLException e){
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error in sql request");
			return;
		}
		
		
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(con);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	

}
