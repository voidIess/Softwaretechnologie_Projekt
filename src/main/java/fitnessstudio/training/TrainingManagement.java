package fitnessstudio.training;


import fitnessstudio.member.Member;
import fitnessstudio.member.MemberManagement;
import fitnessstudio.roster.Roster;
import fitnessstudio.roster.RosterDataConverter;
import fitnessstudio.roster.RosterEntryForm;
import fitnessstudio.roster.RosterManagement;
import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffManagement;
import fitnessstudio.staff.StaffRole;
import org.salespointframework.useraccount.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TrainingManagement {

	private static final Logger LOG = LoggerFactory.getLogger(MemberManagement.class);

	private final TrainingRepository trainings;
	private final MemberManagement memberManagement;
	private final StaffManagement staffManagement;
	private final RosterManagement rosterManagement;

	public TrainingManagement(TrainingRepository trainings, MemberManagement memberManagement, StaffManagement staffManagement, RosterManagement rosterManagement) {
		Assert.notNull(trainings, "TrainingRepository must not be null");
		Assert.notNull(memberManagement, "MemberManagement must not be null");
		Assert.notNull(staffManagement, "StaffManagement must not be null");
		Assert.notNull(rosterManagement, "RosterManagement must not be null");

		this.trainings = trainings;
		this.memberManagement = memberManagement;
		this.staffManagement = staffManagement;
		this.rosterManagement = rosterManagement;
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

		List<String> times = new ArrayList<>();
		times.add(time);

		RosterEntryForm rosterform = new RosterEntryForm(
			staff.getStaffId(),
			RosterDataConverter.roleToString(StaffRole.TRAINER),
			Integer.parseInt(day),
			times,
			form.getWeek()
		);

		if (!rosterManagement.isFree(rosterform)) {
			result.rejectValue("staff", "training.staff.notFree");
			return null;
		}

		if (type.equals(TrainingType.TRIAL)) {
			memberManagement.trainFree(member);
		}

		return trainings.save(new Training(type, staff, member, Integer.parseInt(day),
			time, Roster.DURATION, form.getDescription(), form.getWeek()));
	}

	public void decline(Long trainingId) {
		Optional<Training> trainingOptional = findById(trainingId);
		trainingOptional.ifPresent(Training::decline);
	}

	public boolean accept(Long trainingId) {
		Training training = findById(trainingId).orElse(null);
		if (training != null) {
			List<String> list = new ArrayList<>();
			list.add(training.getStartTime());
			RosterEntryForm rosterEntryForm = new RosterEntryForm(
				training.getTrainer().getStaffId(),
				RosterDataConverter.roleToString(StaffRole.TRAINER),
				training.getDay(),
				list,
				training.getWeek()
			);
			if (rosterManagement.isFree(rosterEntryForm)) {
				rosterManagement.createEntry(rosterEntryForm, trainingId, null);
				training.accept();
				trainings.save(training);
				return true;
			}
		}
		return false;
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


	public RosterManagement getRosterManagement() {
		return rosterManagement;
	}

	@Scheduled(cron = "0 * * * * *")
	public void checkTrainings() {
		LOG.info("Updating trainings ..");
		for (Training training : getAllTrainings()) {
			if (training.getMember().isPaused()) {
				end(training.getTrainingId());
			}
		}
	}
}
