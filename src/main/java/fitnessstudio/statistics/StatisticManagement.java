package fitnessstudio.statistics;

import fitnessstudio.member.Member;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class StatisticManagement {

	private final AttendanceRepository attendances;

	public StatisticManagement(AttendanceRepository attendances) {
		Assert.notNull(attendances, "AttendanceRepository must not be null!");
		this.attendances = attendances;
	}

	public void addAttendance(Member member, long duration) {
		if(attendances.findById(LocalDate.now()).isEmpty()) {
			attendances.save(new Attendance(LocalDate.now()));
		}
		Attendance attendance = attendances.findById(LocalDate.now()).get();
		attendance.addMember(member);
		attendance.addTime(duration);
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
