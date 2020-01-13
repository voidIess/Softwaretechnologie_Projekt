package fitnessstudio.statistics;

import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Management class to interact with the {@link AttendanceRepository}
 *
 * @author Lea Haeusler
 */
@Service
@Transactional
public class AttendanceManagement {

	private final AttendanceRepository attendances;

	/**
	 * Creates a new {@link AttendanceManagement} instance with then given {@link AttendanceRepository}.
	 *
	 * @param attendances 	must not be {@literal null}.
	 */
	AttendanceManagement(AttendanceRepository attendances) {
		Assert.notNull(attendances, "AttendanceRepository must not be null!");
		this.attendances = attendances;
	}

	/**
	 * Method which adds a new visit to the {@link Attendance} instance of the given day in the {@link AttendanceRepository}.
	 * (Creates a new {@link Attendance} instance for the given day if not existent.)
	 *
	 * @param date			day of the visit
	 * @param memberId		ID of the member who visited the studio
	 * @param duration		duration of the visit in minutes
	 */
	void addAttendance(LocalDate date, long memberId, long duration) {
		if(attendances.findById(date).isEmpty()) {
			attendances.save(new Attendance(date));
		}
		Attendance attendance = attendances.findById(date).get();
		attendance.addMember(memberId);
		attendance.addTime(duration);
	}

	/**
	 * Method which adds a new visit to the {@link Attendance} instance of today in the {@link AttendanceRepository}.
	 * (Creates a new {@link Attendance} instance for today if not existent.)
	 *
	 * @param memberId		ID of the member who visited the studio
	 * @param duration		duration of the visit in minutes
	 */
	void addAttendance(long memberId, long duration) {
		addAttendance(LocalDate.now(), memberId, duration);
	}

	/**
	 * Returns all {@link Attendance}s saved in the {@link AttendanceRepository}.
	 *
	 * @return all {@link Attendance}s
	 */
	Streamable<Attendance> findAll() {
		return attendances.findAll();
	}

	/**
	 * Returns the {@link Attendance} of the given date if saved in the {@link AttendanceRepository}.
	 *
	 * @param date		day of {@link Attendance} instance
	 * @return all {@link Attendance} of the given date
	 */
	Optional<Attendance> findById(LocalDate date) {
		return attendances.findById(date);
	}

}
