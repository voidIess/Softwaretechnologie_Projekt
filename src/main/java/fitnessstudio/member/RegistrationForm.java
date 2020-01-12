package fitnessstudio.member;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

class RegistrationForm extends MemberForm {

	@NotEmpty(message = "{MemberForm.loginName.NotEmpty}")
	private final String userName;

	@NotEmpty(message = "{MemberForm.password.NotEmpty}") //
	private final String password;

	@NotNull(message = "{RegistrationForm.contract.NotNull}")
	private final Long contract;

	private String bonusCode;

	public RegistrationForm(String firstName, String lastName, String email, String userName, String password,
							String iban, String bic, Long contract, String bonusCode) {
		super(firstName, lastName, email, iban, bic);
		this.userName = userName;
		this.password = password;
		this.contract = contract;
		this.bonusCode = bonusCode;
	}

	public void setBonusCode (String bonusCode) {
		this.bonusCode = bonusCode;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public Long getContract() {
		return contract;
	}

	public String getBonusCode() {
		return bonusCode;
	}
}
