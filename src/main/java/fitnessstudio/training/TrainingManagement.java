package fitnessstudio.training;


import fitnessstudio.email.EmailService;
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

/**
 * Implementation of business logic related to {@link Training}s.
 * @author Bill Kippe
 * @author Markus Wieland
 * @version 1.0
 */
@Service
@Transactional
public class TrainingManagement {

	private static final Logger LOG = LoggerFactory.getLogger(TrainingManagement.class);

	private final TrainingRepository trainings;
	private final MemberManagement memberManagement;
	private final StaffManagement staffManagement;
	private final RosterManagement rosterManagement;
	private final EmailService emailService;

	/**
	 * Creates a new {@link TrainingManagement} with then given variables.
	 *
	 * @param trainings must not be {@literal null}.
	 * @param memberManagement must not be {@literal null}.
	 * @param staffManagement must not be {@literal null}.
	 * @param rosterManagement must not be {@literal null}.
	 * @param emailService must not be {@literal null}.
	 */
	public TrainingManagement(TrainingRepository trainings, MemberManagement memberManagement,
							  StaffManagement staffManagement, RosterManagement rosterManagement,
							  EmailService emailService) {

		Assert.notNull(trainings, "TrainingRepository must not be null");
		Assert.notNull(memberManagement, "MemberManagement must not be null");
		Assert.notNull(staffManagement, "StaffManagement must not be null");
		Assert.notNull(rosterManagement, "RosterManagement must not be null");
		Assert.notNull(emailService, "EmailService must not be null");

		this.trainings = trainings;
		this.memberManagement = memberManagement;
		this.staffManagement = staffManagement;
		this.rosterManagement = rosterManagement;
		this.emailService = emailService;
	}

	/**
	 * Creates a new {@link Training} using the information given in the {@link TrainingForm} and the related member.
	 *
	 * @param member	member of training
	 * @param form		form for creation
	 * @param result	saves errors
	 * @return the new {@link Training} instance.
	 */
	public Training createTraining(Member member, TrainingForm form, Errors result) {
		var trainer = form.getStaff();
		var time = form.getTime();
		var day = form.getDay();
		var type = TrainingType.valueOf(form.getType());

		if (type.equals(TrainingType.TRIAL) && member.isFreeTrained()) {
			result.rejectValue("type", "training.type.alreadyFreeTrained");
		}

		Optional<Staff> staffOptional = staffManagement.findById(Long.parseLong(trainer));
		if (staffOptional.isEmpty()) {
			result.rejectValue("staff", "training.staff.notFound");
		}
		Staff staff = staffOptional.get();

		List<String> times = new ArrayList<>();
		times.add(time);

		RosterEntryForm rosterForm = new RosterEntryForm(
			staff.getStaffId(),
			RosterDataConverter.roleToString(StaffRole.TRAINER),
			Integer.parseInt(day),
			times,
			form.getWeek()
		);

		if (!rosterManagement.isFree(rosterForm)) {
			result.rejectValue("staff", "training.staff.notFree");
		}

		if (result != null && result.hasFieldErrors()) {
			return null;
		}

		if (type.equals(TrainingType.TRIAL)) {
			memberManagement.trainFree(member);
		}

		return trainings.save(new Training(type, staff, member, Integer.parseInt(day),
			time, Roster.DURATION, form.getDescription(), form.getWeek()));
	}

	/**
	 * Declines the given Training with its ID.
	 *
	 * @param trainingId	ID of training
	 */
	public void decline(Long trainingId) {
		Optional<Training> trainingOptional = findById(trainingId);
		trainingOptional.ifPresent(training -> {
			if (training.decline() && emailService != null){
				Member to = training.getMember();
				emailService.sendTrainingStateUpdated(to.getUserAccount().getEmail(), to.getFirstName(), training.getTrainingId());
			}
			trainings.save(training);
		});
	}

	/**
	 * Accepts the given training when its requested.
	 *
	 * @param trainingId	ID of training
	 * @return if its able to be declined.
	 */
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
				if(training.accept() && emailService != null){
					Member to = training.getMember();
					emailService.sendTrainingStateUpdated(to.getUserAccount().getEmail(), to.getFirstName(),
						training.getTrainingId());
				}
				trainings.save(training);
				return true;
			}
		}
		return false;
	}

	/**
	 * Ends the given training when its already accepted.
	 *
	 * @param trainingId	ID of training
	 */
	public void end(Long trainingId) {
		Optional<Training> trainingOptional = findById(trainingId);
		trainingOptional.ifPresent(Training::end);

		Training training = trainingOptional.get();
		int week = training.getWeek();
		int day = training.getDay();
		int time = rosterManagement.getTimeIndex(training.getStartTime());

		rosterManagement.deleteEntryByTraining(week, time, day, trainingId);

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

	/**
	 * Scheduled method to test every minute if trainings exists though membership is paused.
	 */
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
