package main.java.de.voidtech.ytparty.entities.persistent;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.json.JSONObject;

import main.java.de.voidtech.ytparty.entities.message.MessageBuilder;

@Entity(name = "Messages")
@Table(name = "Messages", indexes = @Index(columnList = "partyID", name = "index_message"))
public class ChatMessage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column
	private String partyID; 
	
	@Column
	private String author;
	
	@Column
	private String colour;
	
	@Column
	@Type(type = "org.hibernate.type.TextType")
	private String content;
	
	@Column
	private String messageModifiers;
	
	@Column
	private String avatar;
	
	@Deprecated
	ChatMessage() {
	}
	
	public ChatMessage(MessageBuilder builder)
	{
	  this.partyID = builder.getChatMessagePartyID();
	  this.author = builder.getChatMessageAuthor();
	  this.colour = builder.getChatMessageColour();
	  this.content = builder.getChatMessageContent();
	  this.messageModifiers = builder.getChatMessageMessageModifiers();
	  this.avatar = builder.getChatMessageAvatar();
	}

	public String getPartyID() {
		return this.partyID;
	}
	
	public String getAuthor() {
		return this.author;
	}
	
	public String getColour() {
		return this.colour;
	}
	
	public String getContent() {
		return this.content;
	}
	
	public String getMessageModifiers() {
		return this.messageModifiers;
	}
	
	public String getAvatar() {
		return this.avatar;
	}
	
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	public void setPartyID(String newPartyID) {
		this.partyID = newPartyID;
	}
	
	public void setAuthor(String newAuthor) {
		this.author = newAuthor;
	}
	
	public void setColour(String newColour) {
		this.colour = newColour;
	}
	
	public void setContent(String newContent) {
		this.content = newContent;
	}
	
	public void setMessageModifiers(String newModifiers) {
		this.messageModifiers = newModifiers;
	}

	public String convertToJson() {
		JSONObject data = new JSONObject().put("type", "party-chatmessage")
				.put("data", new JSONObject()
						.put("author", this.author)
						.put("colour", this.colour)
						.put("content", this.content)
						.put("avatar", this.avatar)
						.put("modifiers", this.messageModifiers));
		return data.toString();
	}
}