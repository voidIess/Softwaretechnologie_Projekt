package member;

import javax.validation.constraints.NotEmpty;

class RegistrationForm {

	@NotEmpty(message = "{RegistrationForm.firstName.NotEmpty")
	private final String firstName;

	@NotEmpty(message = "{RegistrationForm.lastName.NotEmpty")
	private final String lastName;

	@NotEmpty(message = "{RegistrationForm.loginName.NotEmpty")
	private final String userName;

	@NotEmpty(message = "{RegistrationForm.password.NotEmpty}") //
	private final String password;

	public RegistrationForm(String firstName, String lastName, String userName, String password) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.userName = userName;
		this.password = password;
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

	public String getPassword() {

		return password;
	}
}
