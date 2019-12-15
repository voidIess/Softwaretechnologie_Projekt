package fitnessstudio.staff;

import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StaffManagement {

	private final StaffRepository staffRepo;
	private final UserAccountManager userAccountManager;

	public StaffManagement(StaffRepository repository, UserAccountManager userAccountManager) {

		this.staffRepo = repository;
		this.userAccountManager = userAccountManager;
	}

	public List<Staff> getAllStaffs() {
		List<Staff> staffs = new ArrayList<>();
		for (Staff staff : staffRepo.findAll()) {
			if(staff.getUserAccount().isEnabled()) {
				staffs.add(staff);
			}
		}

		return staffs;
	}

	public Staff createStaff(StaffForm form, Errors result) {
		Staff staff = null;
		try {
			if (userAccountManager.findByUsername(form.getUsername()).isPresent()) {
				result.rejectValue("username", "register.duplicate.userAccountName");
				return null;
			}
			String email = form.getEmail();
			if (emailExists(email)) {
				result.rejectValue("email", "register.duplicate.userAccountEmail");
				return null;
			}
			UserAccount userAccount = userAccountManager.create(form.getUsername(),
					Password.UnencryptedPassword.of(form.getPassword()), email);
			staff = new Staff(userAccount, form.getFirstName(), form.getLastName(),
					Money.of(new BigDecimal(form.getSalary()), "EUR"));
			
			staff.getUserAccount().add(Role.of("STAFF"));
			staffRepo.save(staff);

		} catch (Exception ignored) {

		}
		return staff;
	}


	public void saveStaff(Staff staff) {
		staffRepo.save(staff);
	}

	public void removeStaff(long id) {
		Optional<Staff> staffs = this.findById(id);
		if (staffs.isPresent()) {
			Staff staff = staffs.get();
			UserAccountIdentifier identifier = staff.getUserAccount().getId();
			//staffRepo.deleteById(id);
			assert identifier != null;
			userAccountManager.disable(identifier);

		}
	}

	public Optional<Staff> findByUserAccount(UserAccount userAccount) {
		return staffRepo.findByUserAccount(userAccount);
	}

	public Optional<Staff> findById(Long staffId) {
		return staffRepo.findById(staffId);
	}

	boolean emailExists(String email) {
		for (UserAccount userAccount : userAccountManager.findAll()) {
			String userAccountEmail = userAccount.getEmail();
			if (userAccountEmail != null && userAccountEmail.equalsIgnoreCase(email)) return true;
		}
		return false;
	}

}
