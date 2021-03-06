package main.java.de.voidtech.ytparty.handlers.party;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import main.java.de.voidtech.ytparty.annotations.Handler;
import main.java.de.voidtech.ytparty.entities.ephemeral.AuthResponse;
import main.java.de.voidtech.ytparty.entities.ephemeral.GatewayConnection;
import main.java.de.voidtech.ytparty.entities.ephemeral.Party;
import main.java.de.voidtech.ytparty.entities.message.MessageBuilder;
import main.java.de.voidtech.ytparty.entities.persistent.ChatMessage;
import main.java.de.voidtech.ytparty.entities.persistent.User;
import main.java.de.voidtech.ytparty.handlers.AbstractHandler;
import main.java.de.voidtech.ytparty.service.ChatMessageService;
import main.java.de.voidtech.ytparty.service.GatewayAuthService;
import main.java.de.voidtech.ytparty.service.GatewayResponseService;
import main.java.de.voidtech.ytparty.service.PartyService;
import main.java.de.voidtech.ytparty.service.UserService;

@Handler
public class JoinPartyHandler extends AbstractHandler {

	@Autowired
	private GatewayResponseService responder;
	
	@Autowired
	private GatewayAuthService authService;
	
	@Autowired
	private PartyService partyService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ChatMessageService messageService;
	
	@Override
	public void execute(GatewayConnection session, JSONObject data) {
		String token = data.getString("token");
		String roomID = data.getString("roomID");
		
		AuthResponse tokenResponse = authService.validateToken(token); 
		AuthResponse partyIDResponse = authService.validatePartyID(roomID);
		
		if (!tokenResponse.isSuccessful()) responder.sendError(session, tokenResponse.getMessage(), this.getHandlerType());
		else if (!partyIDResponse.isSuccessful()) responder.sendError(session, partyIDResponse.getMessage(), this.getHandlerType());
			else {
				String username = tokenResponse.getActingString();
				User user = userService.getUser(username);
				Party party = partyService.getParty(roomID);
				session.setName(user.getEffectiveName());
				responder.sendSuccess(session, new JSONObject()
						.put("video", party.getVideoID())
						.put("canControl", party.canControlRoom(username))
						.put("theme", party.getRoomColour())
						.put("owner", party.getOwnerName()), this.getHandlerType());
				
				ChatMessage joinMessage = new MessageBuilder()
						.partyID(roomID)
						.author(MessageBuilder.SYSTEM_AUTHOR)
						.colour(party.getRoomColour())
						.content(String.format("%s has joined the party!", user.getEffectiveName()))
						.modifiers(MessageBuilder.SYSTEM_MODIFIERS)
						.avatar(MessageBuilder.SYSTEM_AVATAR)
						.buildToChatMessage();
				party.addToSessions(session);
				deliverMessageHistory(session, roomID);
				responder.sendChatMessage(party, joinMessage);
			}
		}

	private void deliverMessageHistory(GatewayConnection session, String roomID) {
		List<ChatMessage> messageHistory = messageService.getMessageHistory(roomID);
		responder.sendChatHistory(session, messageHistory);
	}

	@Override
	public String getHandlerType() {
		return "party-joinparty";
	}
	
	@Override
	public boolean requiresRateLimit() {
		return true;
	}
}