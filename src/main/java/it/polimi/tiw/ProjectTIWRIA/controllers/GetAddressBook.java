package it.polimi.tiw.ProjectTIWRIA.controllers;

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

import it.polimi.tiw.ProjectTIWRIA.DAO.AddressDAO;
import it.polimi.tiw.ProjectTIWRIA.beans.Contact;
import it.polimi.tiw.ProjectTIWRIA.beans.User;
import it.polimi.tiw.ProjectTIWRIA.utils.ConnectionHandler;


@WebServlet("/GetAddressBook")
public class GetAddressBook extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection; 

    public GetAddressBook() {
        super();
    }
    
    public void init() throws ServletException {
    	connection = ConnectionHandler.getConnection(getServletContext());
    
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession s = request.getSession();
		User user = (User) s.getAttribute("user");
		AddressDAO addressDAO = new AddressDAO(connection);
		
		try {
			List<Contact> contacts = addressDAO.getAddressBook(user.getId());
			String json = new Gson().toJson(contacts);
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
			
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("There was an error in the server");
			return;
		}
	}


}
