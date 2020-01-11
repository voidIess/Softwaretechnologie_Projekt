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

	public AttendanceManagement(AttendanceRepository attendances) {
		Assert.notNull(attendances, "AttendanceRepository must not be null!");
		this.attendances = attendances;
	}

	public void addAttendance(LocalDate date, long memberId, long duration) {
		if(attendances.findById(date).isEmpty()) {
			attendances.save(new Attendance(date));
		}
		Attendance attendance = attendances.findById(date).get();
		attendance.addMember(memberId);
		attendance.addTime(duration);
	}

	public void addAttendance(long memberId, long duration) {
		addAttendance(LocalDate.now(), memberId, duration);
	}

	public Streamable<Attendance> findAll() {
		return attendances.findAll();
	}

	public Optional<Attendance> findById(LocalDate date) {
		return attendances.findById(date);
	}

}
