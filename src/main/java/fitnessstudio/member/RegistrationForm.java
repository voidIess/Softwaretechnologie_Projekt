package fitnessstudio.member;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Form for user input to register a new {@link Member}.
 *
 * @version 1.0
 */
class RegistrationForm extends MemberForm {

	@NotEmpty(message = "{MemberForm.loginName.NotEmpty}")
	private final String userName;

	@NotEmpty(message = "{MemberForm.password.NotEmpty}") //
	private final String password;

	@NotNull(message = "{RegistrationForm.contract.NotNull}")
	private final Long contract;

	private String bonusCode;

	/**
	 *
	 * @param firstName		first name of user
	 * @param lastName		last name of user
	 * @param email			unique email address of user
	 * @param userName		unique user name
	 * @param password		password of user account
	 * @param iban			IBAN of user
	 * @param bic			BIC of user
	 * @param contract		chosen contract of user
	 * @param bonusCode		recruitment code existing member
	 */
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
