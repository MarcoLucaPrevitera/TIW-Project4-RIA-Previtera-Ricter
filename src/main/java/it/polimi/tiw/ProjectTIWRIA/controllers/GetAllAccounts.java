package it.polimi.tiw.ProjectTIWRIA.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.polimi.tiw.ProjectTIWRIA.DAO.AccountDAO;
import it.polimi.tiw.ProjectTIWRIA.beans.Account;
import it.polimi.tiw.ProjectTIWRIA.beans.User;
import it.polimi.tiw.ProjectTIWRIA.utils.ConnectionHandler;


@WebServlet("/GetAllAccounts")
public class GetAllAccounts extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Connection connection = null;
    
    public GetAllAccounts() {
        super();
    }

	public void init() throws ServletException {
	
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession s = request.getSession();
		User user = (User) s.getAttribute("user");
		AccountDAO accountDAO = new AccountDAO(connection);
		List<Account> accounts = new ArrayList<Account>();

		try {
			accounts = accountDAO.findAccountsByUser(user.getId());
			
			String json = new Gson().toJson(accounts);
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
			
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Unable to retreive missions");
			return;
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
