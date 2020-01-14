package fitnessstudio.member;

import org.jetbrains.annotations.NotNull;

/**
 * The in-/output form for a {@link Member} to invite a friend.
 *
 * @version 1.0
 */
public class FriendInviteForm {

	@NotNull
	private final String email;

	@NotNull
	private final String friendsName;

	@NotNull
	private final Long friendsId;

	/**
	 * Creates a new {@link FriendInviteForm} instance with the given parameters.
	 *
	 * @param email			email address of friend
	 * @param friendsName	name of member
	 * @param friendsId		ID of member
	 */
	public FriendInviteForm(String email,String friendsName, Long friendsId) {
		this.email = email;
		this.friendsName = friendsName;
		this.friendsId = friendsId;
	}

	public String getEmail() {
		return email;
	}

	public String getFriendsName() {
		return friendsName;
	}

	public Long getFriendsId() {
		return friendsId;
	}
}
