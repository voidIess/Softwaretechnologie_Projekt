package fitnessstudio.member;

/**
 * In-/output form for a {@link Member} to change its personal data.
 *
 * @version 1.0
 */
public class EditingForm extends MemberForm {

	/**
	 * Creates a new {@link EditingForm} with the given parameters.
	 *
	 * @param firstName	first name of member
	 * @param lastName	last name of member
	 * @param email		email address of member
	 * @param iban		IBAN of member
	 * @param bic		BIC of member
	 */
	public EditingForm(String firstName, String lastName, String email, String iban, String bic) {
		super(firstName, lastName, email, iban, bic);
	}

	/**
	 * Checks if all input fields are empty.
	 * @return boolean whether all inputs are empty
	 */
	public boolean isEmpty() {
		return  isNameNull() || getEmail() == null || isBankingNull();
	}

	/**
	 * Checks if input fields for members first ore last name are empty.
	 * @return boolean whether name input ist empty
	 */
	private boolean isNameNull(){
		return getFirstName() == null || getLastName() == null;
	}

	/**
	 * Checks if input fields for members IBAN or BIC are empty.
	 * @return boolean whether banking input is empty
	 */
	private boolean isBankingNull() {
		return getIban() == null || getBic() == null;
	}
}
