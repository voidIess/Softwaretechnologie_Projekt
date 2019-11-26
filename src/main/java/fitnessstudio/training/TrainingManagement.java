package fitnessstudio.training;


import fitnessstudio.member.Member;
import fitnessstudio.member.MemberManagement;
import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffManagement;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TrainingManagement {

	private final TrainingRepository trainings;
	private final UserAccountManager userAccounts;
	private final MemberManagement memberManagement;
	private final StaffManagement staffManagement;

	public TrainingManagement(TrainingRepository trainings, UserAccountManager userAccounts, MemberManagement memberManagement, StaffManagement staffManagement) {
		Assert.notNull(trainings, "TrainingRepository must not be null");
		Assert.notNull(userAccounts, "UserAccountManager must not be null");
		Assert.notNull(memberManagement, "MemberManagement must not be null");
		Assert.notNull(staffManagement, "StaffManagement must not be null");

		this.trainings = trainings;
		this.userAccounts = userAccounts;
		this.memberManagement = memberManagement;
		this.staffManagement = staffManagement;
	}



	public Optional<Member> findByUserAccount(UserAccount userAccount){
		return memberManagement.findByUserAccount(userAccount);
	}

	public List<Staff> getAllStaffs(){return staffManagement.getAllStaffs();}

	public List<TrainingType> getTypes(){
		return new ArrayList<>(Arrays.asList(TrainingType.values()));
	}
}
