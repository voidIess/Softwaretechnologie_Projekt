package fitnessstudio.statistics;

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

	public long getAverageTimeToday() {
		Optional<Attendance> attendance = attendances.findById(LocalDate.now());
		if(attendance.isEmpty()) {
			return 0;
		} else {
			return attendance.get().getAverageTime();
		}
	}

	public long[] getAverageTimesOfLastWeek() {
		long[] times = new long[7];
		LocalDate today = LocalDate.now();

		for(int i=0; i<7; i++) {
			Optional<Attendance> attendance = findById(today.minusDays(i));
			if(attendance.isPresent()) {
				times[i] = attendance.get().getAverageTime();
			}
		}

		return times;
	}

	public long getMemberAmountToday() {
		Optional<Attendance> attendance = attendances.findById(LocalDate.now());
		if(attendance.isEmpty()) {
			return 0;
		} else {
			return attendance.get().getMemberAmount();
		}
	}

	public long[] getMemberAmountsOfLastWeek() {
		long[] amounts = new long[7];
		LocalDate today = LocalDate.now();

		for(int i=0; i<7; i++) {
			Optional<Attendance> attendance = findById(today.minusDays(i));
			if(attendance.isPresent()) {
				amounts[i] = attendance.get().getMemberAmount();
			}
		}

		return amounts;
	}

}
