package fitnessstudio.staff;

import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class StaffManagement {

	private final StaffRepository staffRepo;
	private final UserAccountManager userAccounts;

	public StaffManagement(StaffRepository repository, UserAccountManager userAccounts) {

		this.staffRepo = repository;
		this.userAccounts = userAccounts;
	}

	public List<Staff> getAllStaffs() {
		return staffRepo.findAll().toList();
	}

	public Staff createStaff(StaffForm form) {
		try {
			Staff staff = new Staff(userAccounts.create(form.getUsername(),
					Password.UnencryptedPassword.of(form.getPassword()),
					Role.of("STAFF")), form.getFirstName(), form.getLastName(),
					Money.of(new BigDecimal(form.getSalary()), "EUR"));
			staffRepo.save(staff);
			return staff;
		} catch (Exception e) {
			return null;
		}
	}


	public void saveStaff(Staff staff) {
		staffRepo.save(staff);
	}

	public Optional<Staff> findByUserAccount(UserAccount userAccount) {
		return staffRepo.findByUserAccount(userAccount);
	}

	public Optional<Staff> findById(Long staffId) {
		return staffRepo.findById(staffId);
	}

}
