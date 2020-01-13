package fitnessstudio.statistics;

import fitnessstudio.member.Member;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AttendanceUnitTests {

	@Autowired
	private AttendanceRepository repository;

	@Autowired
	private StatisticManagement statistics;

	private Member member;

	@BeforeAll
	void setUp() {
		member = new Member();
	}

	@Test
	@Order(1)
	void testCreateAttendance() {
		long size = repository.count();
		statistics.addAttendance(member.getMemberId(),2);
		assertThat(repository.count()).isEqualTo(size+1);
	}

	@Test
	@Order(2)
	void testFindAll() {
		assertThat(statistics.findAllAttendances().isEmpty()).isFalse();
	}

	@Test
	@Order(3)
	void testFindById() {
		assertThat(statistics.findAttendanceById(LocalDate.now()).isPresent()).isTrue();
	}

	@Test
	@Order(4)
	void testGetAverageTimeToday() {
		assertThat(statistics.getAverageTimeOfToday()).isEqualTo(2);
	}

	@Test
	@Order(5)
	void testAddAttendanceWithSameDate() {
		//size shouldn't change (one attendance per day)
		long size = repository.count();
		statistics.addAttendance(member.getMemberId(), 4);
		assertThat(repository.count()).isEqualTo(size);
	}

	@Test
	@Order(6)
	void testGetMemberAmountToday() {
		assertThat(statistics.getMemberAmountOfToday()).isEqualTo(1);
	}

	@Test
	@Order(7)
	void testCalculateAverage() {
		assertThat(statistics.getAverageTimeOfToday()).isEqualTo(3);
	}

	@Test
	@Order(8)
	void testGetAverageTimesOfThisWeek() {
		assertThat(statistics.getAverageTimesOfThisWeek().length).isEqualTo(7);
		assertThat(statistics.getAverageTimesOfThisWeek()[LocalDate.now().getDayOfWeek().getValue()-1]).isEqualTo(3);
	}

	@Test
	@Order(9)
	void testGetMemberAmountsOfThisWeek() {
		assertThat(statistics.getMemberAmountsOfThisWeek().length).isEqualTo(7);
		assertThat(statistics.getMemberAmountsOfThisWeek()[LocalDate.now().getDayOfWeek().getValue()-1]).isEqualTo(1);
	}

	@Test
	@Order(10)
	void testGetDaysOfWeek() {
		String[] week = statistics.getDaysOfWeek();
		assertThat(Arrays.stream(week).anyMatch(day -> day.equals(LocalDate.now().toString()))).isTrue();
	}

}
