package fitnessstudio.training;


import fitnessstudio.member.Member;
import fitnessstudio.member.MemberManagement;
import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffManagement;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

import javax.transaction.Transactional;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TrainingManagement {

	private final TrainingRepository trainings;
	private final MemberManagement memberManagement;
	private final StaffManagement staffManagement;

	public TrainingManagement(TrainingRepository trainings, MemberManagement memberManagement, StaffManagement staffManagement) {
		Assert.notNull(trainings, "TrainingRepository must not be null");
		Assert.notNull(memberManagement, "MemberManagement must not be null");
		Assert.notNull(staffManagement, "StaffManagement must not be null");

		this.trainings = trainings;
		this.memberManagement = memberManagement;
		this.staffManagement = staffManagement;
	}

	public Training createTraining(Member member, TrainingForm form, Errors result) {
		var trainer = form.getStaff();
		var time = form.getTime();
		var day = form.getDay();

		if (form.getType().isEmpty()) {
			result.rejectValue("type", "training.type.missing");
			return null;
		}

		var type = TrainingType.valueOf(form.getType());

		if (type.equals(TrainingType.TRIAL) && member.isFreeTrained()) {
			result.rejectValue("type", "training.type.alreadyFreeTrained");
			return null;
		}

		Optional<Staff> staffOptional = staffManagement.findById(Long.parseLong(trainer));
		if (staffOptional.isEmpty()) {
			result.rejectValue("staff", "training.staff.notFound");
			return null;
		}
		Staff staff = staffOptional.get();

		if (type.equals(TrainingType.TRIAL)) {
			memberManagement.trainFree(member);
		}

		return trainings.save(new Training(type, staff, member, Integer.parseInt(day),
			LocalTime.parse(time), 90, form.getDescription()));
	}

	public void decline(Long trainingId) {
		Optional<Training> trainingOptional = findById(trainingId);
		trainingOptional.ifPresent(Training::decline);
	}

	public void accept(Long trainingId) {
		Optional<Training> trainingOptional = findById(trainingId);
		trainingOptional.ifPresent(Training::accept);
	}

	public void end(Long trainingId) {
		Optional<Training> trainingOptional = findById(trainingId);
		trainingOptional.ifPresent(Training::end);
	}

	public Optional<Member> findByUserAccount(UserAccount userAccount) {
		return memberManagement.findByUserAccount(userAccount);
	}

	public List<Staff> getAllStaffs() {
		return staffManagement.getAllStaffs();
	}

	public List<TrainingType> getTypes() {
		return new ArrayList<>(Arrays.asList(TrainingType.values()));
	}

	public List<Training> getAllTrainingByMember(Member member) {
		return trainings.findAll().filter(t ->
			t.getMember().equals(member)
		).toList();
	}

	public List<Training> getAllAcceptedTrainings() {
		return trainings.findAll().filter(t -> t.getState().equals(TrainingState.ACCEPTED)).toList();
	}

	public List<Training> getAllRequestedTrainings() {
		return trainings.findAll().filter(t -> t.getState().equals(TrainingState.REQUESTED)).toList();
	}

	public List<Training> getAllTrainings() {
		return trainings.findAll().toList();
	}

	public Optional<Training> findById(Long id) {
		return trainings.findById(id);
	}

	public void createRosterEntryForTrainer () {

	}
}
