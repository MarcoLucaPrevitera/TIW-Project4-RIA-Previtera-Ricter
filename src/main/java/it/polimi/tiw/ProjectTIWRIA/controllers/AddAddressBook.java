package it.polimi.tiw.ProjectTIWRIA.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.ProjectTIWRIA.DAO.AddressDAO;
import it.polimi.tiw.ProjectTIWRIA.beans.User;

import it.polimi.tiw.ProjectTIWRIA.utils.ConnectionHandler;

@WebServlet("/AddAddressBook")
@MultipartConfig
public class AddAddressBook extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;   
    
    public AddAddressBook() {
        super();
    
    }

    
    public void init() throws ServletException {
    	connection = ConnectionHandler.getConnection(getServletContext());
    
	}

    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession s = request.getSession();
		User user = (User) s.getAttribute("user");
		AddressDAO addressDAO = new AddressDAO(connection);
		int userId = user.getId();
		int accountId;
		
		try {
			accountId = Integer.parseInt(request.getParameter("contactAccountId"));
			
		}
		catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect parameter");
			return;
		}
		
		try {
			addressDAO.addAddressBook(userId,accountId);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("There was an error in the server");
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
