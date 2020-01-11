package fitnessstudio.statistics;

import org.salespointframework.core.DataInitializer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDate;

@Component
public class StatisticDataInitializer implements DataInitializer {

	private final AttendanceManagement attendanceManagement;

	public StatisticDataInitializer(AttendanceManagement attendanceManagement) {

		Assert.notNull(attendanceManagement, "AttendanceManagement must not be null");

		this.attendanceManagement = attendanceManagement;
	}

	@Override
	public void initialize() {

		LocalDate today = LocalDate.now();

		attendanceManagement.addAttendance(today.minusDays(6), 0, 60);
		attendanceManagement.addAttendance(today.minusDays(5), 0, 40);
		attendanceManagement.addAttendance(today.minusDays(5), 1, 25);
		attendanceManagement.addAttendance(today.minusDays(4), 0, 30);
		attendanceManagement.addAttendance(today.minusDays(3), 0, 45);
		attendanceManagement.addAttendance(today.minusDays(1), 1, 10);

	}
}
