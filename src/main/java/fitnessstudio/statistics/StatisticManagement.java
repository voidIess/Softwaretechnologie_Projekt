package fitnessstudio.statistics;

import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.DayOfWeek;
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
			LocalDate date = getLastMonday(today).plusDays(i);
			Optional<Attendance> attendance = findById(date);
			if(attendance.isPresent()) {
				times[i] = attendance.get().getAverageTime();
			}
			if(date.equals(today)) {
				break;
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
			LocalDate date = getLastMonday(today).plusDays(i);
			Optional<Attendance> attendance = findById(date);
			if(attendance.isPresent()) {
				amounts[i] = attendance.get().getMemberAmount();
			}
			if(date.equals(today)) {
				break;
			}
		}

		return amounts;
	}

	private LocalDate getLastMonday(LocalDate date) {
		LocalDate lastMonday = date;
		while (!lastMonday.getDayOfWeek().equals(DayOfWeek.MONDAY)){
			lastMonday = lastMonday.minusDays(1);
		}
		return lastMonday;
	}

	public String[] getDaysOfWeek() {
		String[] week = new String[7];
		LocalDate monday = getLastMonday(LocalDate.now());

		for (int i=0; i<week.length; i++) {
			week[i] = monday.plusDays(i).toString();
		}

		return week;
	}

}
