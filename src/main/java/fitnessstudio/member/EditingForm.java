package fitnessstudio.member;
public class EditingForm extends MemberForm {

	public EditingForm(String firstName, String lastName, String email, String iban, String bic) {
		super(firstName, lastName, email, iban, bic);
	}

	public boolean isEmpty() {
		if(getFirstName()==null || getLastName()==null || getEmail()==null || getIban()==null || getBic()==null) {
			return true;
		}
		return false;
	}
}
