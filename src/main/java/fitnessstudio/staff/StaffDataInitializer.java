package fitnessstudio.staff;

import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StaffDataInitializer implements DataInitializer {
	private static final Logger LOG = LoggerFactory.getLogger(StaffDataInitializer.class);
	private final StaffManagement staffs;
	private final UserAccountManager userAccounts;


	StaffDataInitializer(StaffManagement staffs, UserAccountManager userAccounts){
		this.userAccounts = userAccounts;
		this.staffs = staffs;
	}

	@Override
	public void initialize() {

		/*Staff staff = staffs.createStaff("staff", "123", "Markus", "Wieland", 100);
		if (staff != null)
			LOG.info("Creating default STAFF (user: 'staff', pass: '123', id: "+ staff.getStaffId() +")");

		Staff staff2 = staffs.createStaff("obi", "123", "Obi", "Babobi", 100);
		if (staff2 != null)
			LOG.info("Creating default STAFF (user: 'obi', pass: '123', id: "+ staff2.getStaffId() +")");*/

	}
}
