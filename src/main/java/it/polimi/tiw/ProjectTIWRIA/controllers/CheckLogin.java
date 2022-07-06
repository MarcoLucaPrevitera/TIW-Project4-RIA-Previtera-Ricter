package it.polimi.tiw.ProjectTIWRIA.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import it.polimi.tiw.ProjectTIWRIA.DAO.UserDAO;
import it.polimi.tiw.ProjectTIWRIA.beans.User;
import it.polimi.tiw.ProjectTIWRIA.utils.ConnectionHandler;
import it.polimi.tiw.ProjectTIWRIA.utils.PasswordHashGenerator;


@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
       
  
	public void init() throws ServletException{
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
    public CheckLogin() {
        super();
    }

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserDAO userDAO = new UserDAO(connection);
		User user = null;
		String username = null;
		String password = null;
		try {
		username = StringEscapeUtils.escapeJava(request.getParameter("username"));
		password = StringEscapeUtils.escapeJava(request.getParameter("password"));
		
		if (username == null || password == null || username.isEmpty() || password.isEmpty() ) {
			throw new Exception("Missing or empty credential value");
			}
		}catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing credential value");
			return;
		}
		
		try {
			String hashPassword = PasswordHashGenerator.getSHA(password);
			user = userDAO.checkCredentials(username,hashPassword);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Error in checking credentials");
			return;
		}
		
		if (user == null){
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Incorrect credentials");
		}
		else {
			
			request.getSession().setAttribute("user", user);
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("name", user.getName());
			jsonObject.addProperty("surname",user.getSurname());
			jsonObject.addProperty("username",user.getUsername());
			
			String json = new Gson().toJson(jsonObject);
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
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
