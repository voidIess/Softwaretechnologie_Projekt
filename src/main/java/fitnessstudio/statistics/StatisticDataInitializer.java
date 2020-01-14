package fitnessstudio.statistics;

import org.salespointframework.core.DataInitializer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDate;

/**
 * Initializes some start entries in the statistics.
 *
 * @version 1.0
 * @author Lea Haeusler
 */
@Component
public class StatisticDataInitializer implements DataInitializer {

	private final StatisticManagement statisticManagement;

	public StatisticDataInitializer(StatisticManagement statisticManagement) {
		Assert.notNull(statisticManagement, "StatisticManagement must not be null");

		this.statisticManagement = statisticManagement;
	}

	/**
	 * Overwrites the {@link DataInitializer#initialize()} Method
	 * and adds some start {@link Attendance}s.
	 */
	@Override
	public void initialize() {

		LocalDate today = LocalDate.now();

		statisticManagement.addAttendance(today.minusDays(6), 0, 60);
		statisticManagement.addAttendance(today.minusDays(5), 0, 40);
		statisticManagement.addAttendance(today.minusDays(4), 0, 30);
		statisticManagement.addAttendance(today.minusDays(3), 0, 45);
		statisticManagement.addAttendance(today.minusDays(1), 1, 25);
		statisticManagement.addAttendance(today.minusDays(1), 1, 10);

	}
}
