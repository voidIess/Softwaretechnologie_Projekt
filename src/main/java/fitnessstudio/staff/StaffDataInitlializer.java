package fitnessstudio.staff;

import fitnessstudio.roster.RosterManager;
import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;

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
