package fitnessstudio.member;

import org.jetbrains.annotations.NotNull;

/**
 * Formular  fuer Freundeseinladungen
 */
public class FriendInviteForm {

	@NotNull
	private final String email;

	@NotNull
	private final String friendsname;

	@NotNull
	private final Long friendsId;

	public FriendInviteForm(String email,String friendsname, Long friendsId) {
		this.email = email;
		this.friendsname = friendsname;
		this.friendsId = friendsId;
	}

	public String getEmail() {
		return email;
	}

	public String getFriendsname() {
		return friendsname;
	}

	public Long getFriendsId() {
		return friendsId;
	}
}
