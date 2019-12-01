package fitnessstudio.statistics;

import fitnessstudio.member.Member;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
public class StatisticManagement {

	private final AttendanceRepository attendances;

	public StatisticManagement(AttendanceRepository attendances) {
		Assert.notNull(attendances, "AttendanceRepository must not be null!");
		this.attendances = attendances;
	}

	public void addAttendance(long memberId, long duration) {
		if(attendances.findById(LocalDate.now()).isEmpty()) {
			attendances.save(new Attendance(LocalDate.now()));
		}
		Attendance attendance = attendances.findById(LocalDate.now()).get();
		attendance.addMember(memberId);
		attendance.addTime(duration);
	}

	public Streamable<Attendance> findAll() {
		return attendances.findAll();
	}

	public Optional<Attendance> findById(LocalDate date) {
		return attendances.findById(date);
	}

	//just for testing
	public long getAverageTimeToday() {
		Optional<Attendance> attendance = attendances.findById(LocalDate.now());
		if(attendance.isEmpty()) {
			return 0;
		} else {
			return attendance.get().getAverageTime();
		}
	}

	//just for testing
	public long getMemberAmountToday() {
		Optional<Attendance> attendance = attendances.findById(LocalDate.now());
		if(attendance.isEmpty()) {
			return 0;
		} else {
			return attendance.get().getMemberAmount();
		}
	}

}
