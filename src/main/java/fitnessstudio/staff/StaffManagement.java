package fitnessstudio.staff;

import org.springframework.util.Assert;
import org.salespointframework.useraccount.*;
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

	public Optional<Staff> findByUserAccount(UserAccount userAccount){
		return staffRepo.findByUserAccount(userAccount);
	}

	public List<Staff> getAllStaffs(){
		return staffRepo.findAll().toList();
	}

	public Optional<Staff> findById(Long staffId) {return staffRepo.findById(staffId);}

}
