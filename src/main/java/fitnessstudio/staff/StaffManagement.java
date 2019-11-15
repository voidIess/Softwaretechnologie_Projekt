package fitnessstudio.staff;

import com.mysema.commons.lang.Assert;
import org.salespointframework.useraccount.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.AssertTrue;
import java.util.HashMap;
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

	public Optional<Staff> findByUserAccount(UserAccount userAccount){
		return staffRepo.findByUserAccount(userAccount);
	}

	Map<String, Object> createPdf(UserAccount account) {

		Optional<Staff> opt = staffRepo.findByUserAccount(account);
		Assert.isTrue(opt.isPresent(), "There is no existing staff for this account");
		Staff staff = opt.get();

		Map<String, Object> map = new HashMap<>();
		map.put("firstName", staff.getFirstName());
		map.put("lastName", staff.getLastName());
		map.put("salary", staff.getSalary());

		return map;
	}

}
