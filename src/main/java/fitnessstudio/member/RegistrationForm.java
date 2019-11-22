package fitnessstudio.member;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

class RegistrationForm {

	@NotEmpty(message = "{RegistrationForm.firstName.NotEmpty")
	private final String firstName;

	@NotEmpty(message = "{RegistrationForm.lastName.NotEmpty")
	private final String lastName;

	@NotEmpty(message = "{RegistrationForm.loginName.NotEmpty")
	private final String userName;

	@NotEmpty(message = "{RegistrationForm.password.NotEmpty}") //
	private final String password;

	@NotEmpty(message = "{RegistrationForm.iban.NotEmpty}") //
	private final String iban;

	@NotEmpty(message = "{RegistrationForm.bic.NotEmpty}")
	private final String bic;

	@NotNull(message = "{RegistrationForm.contract.NotNull")
	private final Long contract;

	public RegistrationForm(String firstName, String lastName, String userName, String password, String iban, String bic, Long contract) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.userName = userName;
		this.password = password;
		this.iban = iban;
		this.bic = bic;
		this.contract = contract;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() { return password;}

	public String getIban() {
		return iban;
	}

	public String getBic() {
		return bic;
	}

	public Long getContract(){
		return contract;
	}
}
