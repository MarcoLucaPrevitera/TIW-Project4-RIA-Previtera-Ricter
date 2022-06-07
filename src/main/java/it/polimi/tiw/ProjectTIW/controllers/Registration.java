package it.polimi.tiw.ProjectTIW.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.ProjectTIW.DAO.UserDAO;
import it.polimi.tiw.ProjectTIW.utils.ConnectionHandler;
import it.polimi.tiw.ProjectTIW.utils.PasswordHashGenerator;


@WebServlet("/Registration")
public class Registration extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public Registration() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}  


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserDAO userDAO = new UserDAO(connection);
		String username = null;
		String name = null;
		String surname = null;
		String password = null;
		String repeatPassword = null;
		String emailRegex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
		Pattern pattern = Pattern.compile(emailRegex);
		
		String errorResponse = null; 

		try {
			username = StringEscapeUtils.escapeJava(request.getParameter("username"));
			password = StringEscapeUtils.escapeJava(request.getParameter("password"));
			repeatPassword = StringEscapeUtils.escapeJava(request.getParameter("repeatPassword"));
			name = StringEscapeUtils.escapeJava(request.getParameter("name"));
			surname = StringEscapeUtils.escapeJava(request.getParameter("surname"));

			//check if parameters are null or empty
			if (username == null || password == null || repeatPassword==null || name == null || surname == null || username.isEmpty() || password.isEmpty() || repeatPassword.isEmpty() || name.isEmpty() || surname.isEmpty()) {
				throw new Exception("Missing or empty registration parameters");

			}
		}
		catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or empty registration parameters");
			return;
		}


		try {
			if (username.isBlank() || password.isBlank() || repeatPassword.isBlank() || name.isBlank() || surname.isBlank()) {
				errorResponse = "Invalid character";
			}
			else if(!userDAO.checkUsername(username)) {
				errorResponse = "The username already exists";
			}
			else if(!pattern.matcher(username).matches()) {
				errorResponse = "Wrong syntax in email address";
			}
			else if(!password.equals(repeatPassword)) {
				errorResponse = "Password and repeat password do not match";
			}
			
		
			
			if(errorResponse==null) { //we save the user
				String hashPassword = PasswordHashGenerator.getSHA(password);
				userDAO.createUser(username,name,surname,hashPassword);
				
		
			}
			
			else {
				
			}
		}
		
		catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "There was an error in the registration");
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
