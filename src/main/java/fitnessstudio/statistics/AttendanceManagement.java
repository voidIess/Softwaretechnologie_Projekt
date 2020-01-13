package fitnessstudio.statistics;

import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
public class AttendanceManagement {

	private final AttendanceRepository attendances;

	AttendanceManagement(AttendanceRepository attendances) {
		Assert.notNull(attendances, "AttendanceRepository must not be null!");
		this.attendances = attendances;
	}

	void addAttendance(LocalDate date, long memberId, long duration) {
		if(attendances.findById(date).isEmpty()) {
			attendances.save(new Attendance(date));
		}
		Attendance attendance = attendances.findById(date).get();
		attendance.addMember(memberId);
		attendance.addTime(duration);
	}

	void addAttendance(long memberId, long duration) {
		addAttendance(LocalDate.now(), memberId, duration);
	}

	Streamable<Attendance> findAll() {
		return attendances.findAll();
	}

	Optional<Attendance> findById(LocalDate date) {
		return attendances.findById(date);
	}

}
