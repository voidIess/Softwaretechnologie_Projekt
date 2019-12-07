package fitnessstudio.staff;

<<<<<<< HEAD
import org.springframework.util.Assert;
=======
import com.mysema.commons.lang.Assert;
import org.javamoney.moneta.Money;
>>>>>>> feature/rosterNewBill
import org.salespointframework.useraccount.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StaffManagement {

	private final StaffRepository staffRepo;
	private final UserAccountManager userAccounts;

	public StaffManagement(StaffRepository repository, UserAccountManager userAccounts){

		this.staffRepo = repository;
		this.userAccounts = userAccounts;
	}

	public Staff createStaff (String username, String password, String firstName, String lastName, int money){
		try {
			Staff staff = new Staff(userAccounts.create(username, Password.UnencryptedPassword.of(password), Role.of("STAFF")), firstName, lastName, Money.of(money, "EUR"));
			staffRepo.save(staff);
			return staff;
		} catch (Exception e) {
			return null;
		}
	}

	public void saveStaff(Staff staff) {
		staffRepo.save(staff);
	}

	public Optional<Staff> findByUserAccount(UserAccount userAccount){
		return staffRepo.findByUserAccount(userAccount);
	}

	public List<Staff> getAllStaffs(){
		return staffRepo.findAll().toList();
	}

	public Optional<Staff> findById(Long staffId) {return staffRepo.findById(staffId);}

}
