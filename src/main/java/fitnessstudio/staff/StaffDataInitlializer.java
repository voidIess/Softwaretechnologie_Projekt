package fitnessstudio.staff;

import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.UserAccountManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StaffDataInitlializer implements DataInitializer {
	private static final Logger LOG = LoggerFactory.getLogger(StaffDataInitlializer.class);
	private final StaffRepository staffs;
	private final UserAccountManager userAccounts;


	StaffDataInitlializer(StaffRepository staffs, UserAccountManager userAccounts){
		this.userAccounts = userAccounts;
		this.staffs = staffs;
	}

	@Override
	public void initialize() {

		//Roster roster = new Roster(1);
		//UserAccount userAccount, String firstName, String lastName

	}
}
