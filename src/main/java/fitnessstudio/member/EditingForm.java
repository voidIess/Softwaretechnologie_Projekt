package fitnessstudio.member;
public class EditingForm extends MemberForm {

	public EditingForm(String firstName, String lastName, String iban, String bic) {
		super(firstName, lastName, iban, bic);
	}

	public boolean isEmpty() {
		if(getFirstName()==null || getLastName()==null || getIban()==null || getBic()==null) {
			return true;
		}
		return false;
	}
}
