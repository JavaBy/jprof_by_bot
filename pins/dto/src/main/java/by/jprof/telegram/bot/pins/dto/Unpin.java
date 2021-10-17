package by.jprof.telegram.bot.pins.dto;

import java.util.Objects;

public class Unpin {
	private Long messageId;
	private Long chatId;
	private Long userId;
	private Long ttl;

	public Long getMessageId() {
		return messageId;
	}

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}

	public Long getChatId() {
		return chatId;
	}

	public void setChatId(Long chatId) {
		this.chatId = chatId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getTtl() {
		return ttl;
	}

	public void setTtl(Long ttl) {
		this.ttl = ttl;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Unpin unpin = (Unpin) o;
		return Objects.equals(messageId, unpin.messageId) && Objects.equals(chatId, unpin.chatId) && Objects.equals(userId, unpin.userId) && Objects.equals(ttl, unpin.ttl);
	}

	@Override
	public int hashCode() {
		return Objects.hash(messageId, chatId, userId, ttl);
	}

	@Override
	public String toString() {
		return "Unpin{" +
		       "messageId=" + messageId +
		       ", chatId=" + chatId +
		       ", userId=" + userId +
		       ", ttl=" + ttl +
		       '}';
	}
}
