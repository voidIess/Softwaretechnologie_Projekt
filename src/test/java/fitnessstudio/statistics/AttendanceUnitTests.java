package fitnessstudio.statistics;

import fitnessstudio.member.Member;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AttendanceUnitTests {

	@Autowired
	private StatisticManagement management;

	@Autowired
	private AttendanceRepository repository;

	private Member member;

	@BeforeAll
	void setUp() {
		member = new Member();
	}

	@Test
	@Order(1)
	void testCreateAttendance() {
		long size = repository.count();
		management.addAttendance(member.getMemberId(),2);
		assertThat(repository.count()).isEqualTo(size+1);
	}

	@Test
	@Order(2)
	void testFindAll() {
		assertThat(management.findAll().isEmpty()).isFalse();
	}

	@Test
	@Order(3)
	void testFindById() {
		assertThat(management.findById(LocalDate.now()).isPresent()).isTrue();
	}

	@Test
	@Order(4)
	void testGetAverageTimeToday() {
		assertThat(management.getAverageTimeOfToday()).isEqualTo(2);
	}

	@Test
	@Order(5)
	void testAddAttendanceWithSameDate() {
		//size shouldn't change (one attendance per day)
		long size = repository.count();
		management.addAttendance(member.getMemberId(), 4);
		assertThat(repository.count()).isEqualTo(size);
	}

	@Test
	@Order(6)
	void testGetMemberAmountToday() {
		assertThat(management.getMemberAmountOfToday()).isEqualTo(1);
	}

	@Test
	@Order(7)
	void testCalculateAverage() {
		assertThat(management.getAverageTimeOfToday()).isEqualTo(3);
	}

}