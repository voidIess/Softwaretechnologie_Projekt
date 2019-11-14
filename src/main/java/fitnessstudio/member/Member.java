package fitnessstudio.member;

import org.salespointframework.useraccount.UserAccount;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Member {

	@Id @GeneratedValue
	private long memberId;

	private String firstName;
	private String lastName;

	@OneToOne
	private UserAccount userAccount;

	Member() {}

	Member(UserAccount userAccount, String firstName, String lastName){
		this.userAccount = userAccount;
		this.firstName = firstName;
		this.lastName = lastName;

	}

	public long getMemberId() {
		return memberId;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getUserName() {
		return userAccount.getUsername();
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}
}
