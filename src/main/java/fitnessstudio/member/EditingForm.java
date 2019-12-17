package fitnessstudio.member;
public class EditingForm extends MemberForm {

	public EditingForm(String firstName, String lastName, String email, String iban, String bic) {
		super(firstName, lastName, email, iban, bic);
	}

	public boolean isEmpty() {
		return  isNameNull() || getEmail() == null || isBankingNull();
	}

	private boolean isNameNull(){
		return getFirstName() == null || getLastName() == null;
	}

	private boolean isBankingNull() {
		return getIban() == null || getBic() == null;
	}
}
