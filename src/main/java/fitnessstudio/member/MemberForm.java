package fitnessstudio.member;

import javax.validation.constraints.NotEmpty;

public class MemberForm {

	@NotEmpty(message = "{MemberForm.firstName.NotEmpty}")
	private final String firstName;

	@NotEmpty(message = "{MemberForm.lastName.NotEmpty}")
	private final String lastName;

	@NotEmpty(message = "{MemberForm.email.notEmpty}")
	private final String email;

	@NotEmpty(message = "{MemberForm.iban.NotEmpty}") //
	private final String iban;

	@NotEmpty(message = "{MemberForm.bic.NotEmpty}")
	private final String bic;

	public MemberForm(String firstName, String lastName, String email, String iban, String bic) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.iban = iban;
		this.bic = bic;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getIban() {
		return iban;
	}

	public String getBic() {
		return bic;
	}

}
