package it.polimi.tiw.ProjectTIW.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.ProjectTIW.DAO.AccountDAO;
import it.polimi.tiw.ProjectTIW.DAO.TransferDAO;
import it.polimi.tiw.ProjectTIW.beans.Account;
import it.polimi.tiw.ProjectTIW.beans.Transfer;
import it.polimi.tiw.ProjectTIW.beans.User;
import it.polimi.tiw.ProjectTIW.utils.ConnectionHandler;


@WebServlet("/TransferResult")
public class TransferResult extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection con;


	public TransferResult() {
		super();
	}

	public void init() throws ServletException {
		con = ConnectionHandler.getConnection(getServletContext());
		
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();

		int transferId = -1;

		try {
			transferId = Integer.parseInt(request.getParameter("id"));
		}
		catch(Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad parameter");
			return;
		}


		User user = (User) session.getAttribute("user");
		TransferDAO transferDAO = new TransferDAO(con);
		AccountDAO accountDAO = new AccountDAO(con);
		Transfer transfer = null;
		Account accountTransfer = null;

		try {
			transfer = transferDAO.findTransferById(transferId);
			if(transfer == null){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Transfer not found");
				return;
			}

			accountTransfer = accountDAO.findAccountById(transfer.getAccountId());

			if(accountTransfer.getUserId()!=user.getId()){
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed ");
				return;
			}
		}
		catch(SQLException e){
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error in sql request");
			return;
		}

	
	}



	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
