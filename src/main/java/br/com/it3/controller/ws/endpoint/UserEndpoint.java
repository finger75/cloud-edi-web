package br.com.it3.controller.ws.endpoint;

import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import br.com.it3.controller.ws.sessions.UserSessionHandler;
import br.com.it3.model.entities.User;
import br.com.it3.model.enums.Profile;

@ApplicationScoped
@ServerEndpoint("/user")
public class UserEndpoint {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Inject
	private UserSessionHandler sessionHandler;

	@OnOpen
	public void open(Session session) {
		logger.info("[user] open connection with sesson id " + session.getId());
		sessionHandler.addSession(session);
	}
	
	@OnClose
	public void close(Session session) {
		logger.info(String.format("[user] Session %s closed", session.getId()));
		sessionHandler.removeSession(session);
	}
	
	@OnError
	public void onError(Throwable error) {
		 Logger.getLogger(UserEndpoint.class.getName()).log(Level.SEVERE, null, error);
	}
	
	@OnMessage
	public void onMessage(String message, Session session) {
		try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();
            String action = jsonMessage.getString("action");
            
            logger.info("[user] action="+action);
            
            if ("add".equals(action)) {
                User user = formUser(jsonMessage);
                sessionHandler.addUser(user, session);
            } else if ("remove".equals(action)) {
                int id = (int) jsonMessage.getInt("id");
                sessionHandler.removeUser(id);
            } else if ("update".equals(action)) {
                User user = formUser(jsonMessage);
                sessionHandler.updateUser(user, session);
            } else if ("list".equals(action)) {
            	sessionHandler.getUsers(session);
            } else if ("edit".equals(action)) {
            	int id = (int) jsonMessage.getInt("id");
            	sessionHandler.editUser(id, session);
            } else if ("login".equals(action)) {
            	String username = jsonMessage.getString("username");
            	String password = jsonMessage.getString("password");
            	sessionHandler.doLogin(username, password, session);
            } else if ("listDashboard".equals(action)) {
            	sessionHandler.listDashboard(session);
            } else if ("close".equals(action)) {
            	try {
					session.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
	}
	
	private User formUser(JsonObject jsonMessage) {
		User user = new User();
		user.setStatus(jsonMessage.getString("status"));
        user.setName(jsonMessage.getString("name"));
        user.setEmail(jsonMessage.getString("email"));
        user.setProfile(Profile.valueOf(jsonMessage.getString("profile")));
        user.setUsername(jsonMessage.getString("username"));

        if ("add".equals(jsonMessage.getString("action"))) {
	        user.setPassword(jsonMessage.getString("password")); //TODO Criprografar senha com MD5
        } else {
        	user.setId(Long.valueOf(jsonMessage.getString("id")));
        }
        logger.info(jsonMessage.toString());
		return user;
	}
	
}
