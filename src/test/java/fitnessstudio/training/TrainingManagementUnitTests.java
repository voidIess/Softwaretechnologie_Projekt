package fitnessstudio.training;

import fitnessstudio.member.Member;
import fitnessstudio.member.MemberRepository;
import fitnessstudio.roster.Roster;
import fitnessstudio.roster.RosterManagement;
import fitnessstudio.roster.RosterRepository;
import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link fitnessstudio.training.TrainingManagement}.
 *
 * @author Bill Kippe
 */
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TrainingManagementUnitTests {

	@Autowired
	private TrainingManagement management;

	@Autowired
	private TrainingRepository trainings;

	@Autowired
	private MemberRepository members;

	@Autowired
	private StaffRepository staffs;

	@Autowired
	private RosterManagement rosters;


	private int week;
	private Long trainingId;
	private Member member;
	private Staff staff;
	private String time;

	@BeforeAll
	void setUp() {
		week = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);

		if (members.findAll().isEmpty()) {
			members.save(new Member());
		}

		if (staffs.findAll().isEmpty()) {
			staffs.save(new Staff());
		}

		member = members.findAll().toList().get(0);
		staff = staffs.findAll().toList().get(0);

		time = "10:00-12:00";


	}

	@Test
	@Order(1)
	void testCreateNormalTraining() {
		Training training = management.createTraining(member, new TrainingForm("NORMAL", staff.getStaffId() + "", "0",
			time, "Description", week), null);

		trainingId = training.getTrainingId();

		assertThat(trainings.findById(trainingId)).isNotEmpty();
		assertThat(training.getState()).isEqualTo(TrainingState.REQUESTED);
	}

	@Test
	@Order(2)
	void testAcceptTraining() {
		management.accept(trainingId);
		assertThat(trainings.findById(trainingId).get().getState()).isEqualTo(TrainingState.ACCEPTED);
	}

	@Test
	@Order(3)
	void testEndTraining() {
		management.end(trainingId);
		assertThat(trainings.findById(trainingId).get().getState()).isEqualTo(TrainingState.ENDED);
	}

	@Test
	@Order(4)
	void testGetAllTrainingsOfMember() {
		assertThat(management.getAllTrainingByMember(member).size()).isEqualTo(1);
	}

	@Test
	@Order(5)
	void testCreatTrialTraining() {
		Training training = management.createTraining(member, new TrainingForm("TRIAL", staff.getStaffId() + "", "1",
			time, "Description", week), null);

		trainingId = training.getTrainingId();

		assertThat(trainings.findById(trainingId)).isNotEmpty();
		assertThat(training.getState()).isEqualTo(TrainingState.REQUESTED);
	}

	@Test
	@Order(6)
	void testDeclineTraining() {
		management.decline(trainingId);
		assertThat(trainings.findById(trainingId).get().getState()).isEqualTo(TrainingState.DECLINED);
	}

	@Test
	@Order(7)
	void testDontCreateTrialTrainingWhenAlreadyFreeTrained() {
		TrainingForm form = new TrainingForm("TRIAL", staff.getStaffId() + "", "0",
			time, "Description", week);
		Training training = management.createTraining(member, form, new BeanPropertyBindingResult(form, "form"));

		assertThat(training).isNull();
	}

	@Test
	void testGetAllStaffs() {
		assertThat(management.getAllStaffs().size()).isEqualTo(staffs.findAll().toList().size());
	}
}
