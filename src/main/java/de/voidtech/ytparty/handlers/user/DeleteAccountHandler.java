package main.java.de.voidtech.ytparty.handlers.user;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import main.java.de.voidtech.ytparty.annotations.Handler;
import main.java.de.voidtech.ytparty.entities.ephemeral.AuthResponse;
import main.java.de.voidtech.ytparty.entities.ephemeral.GatewayConnection;
import main.java.de.voidtech.ytparty.entities.persistent.User;
import main.java.de.voidtech.ytparty.handlers.AbstractHandler;
import main.java.de.voidtech.ytparty.service.GatewayAuthService;
import main.java.de.voidtech.ytparty.service.GatewayResponseService;
import main.java.de.voidtech.ytparty.service.UserService;
import main.java.de.voidtech.ytparty.service.UserTokenService;

@Handler
public class DeleteAccountHandler extends AbstractHandler {

	@Autowired
	private UserService userService;
	
	@Autowired
	private GatewayResponseService responder;

	@Autowired
	private UserTokenService tokenService;
	
	@Autowired
	private GatewayAuthService authService;
	
	@Override
	public void execute(GatewayConnection session, JSONObject data) {
		String token = data.getString("token");
		String password = data.getString("password");
		AuthResponse tokenResponse = authService.validateToken(token); 
		
		if (!tokenResponse.isSuccessful()) responder.sendError(session, tokenResponse.getMessage(), this.getHandlerType());
		else {
			String username = tokenResponse.getActingString();
			User user = userService.getUser(username);
			if (!user.checkPassword(password)) responder.sendError(session, "The password you entered is not correct!", this.getHandlerType());
			else {
				tokenService.removeToken(username);
				userService.removeUser(username);
				responder.sendSuccess(session, new JSONObject().put("message", "Account deleted!"), this.getHandlerType());	
			}
		}
	}

	@Override
	public String getHandlerType() {
		return "user-deleteaccount";
	}
	
	@Override
	public boolean requiresRateLimit() {
		return true;
	}

}