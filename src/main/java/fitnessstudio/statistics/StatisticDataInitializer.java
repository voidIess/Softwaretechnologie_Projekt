package fitnessstudio.statistics;

import fitnessstudio.contract.ContractManagement;
import fitnessstudio.member.Member;
import fitnessstudio.member.MemberManagement;
import org.salespointframework.core.DataInitializer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDate;

@Component
public class StatisticDataInitializer implements DataInitializer {

	private final StatisticManagement statisticManagement;

	public StatisticDataInitializer(StatisticManagement statisticManagement) {
		Assert.notNull(statisticManagement, "StatisticManagement must not be null");

		this.statisticManagement = statisticManagement;
	}

	@Override
	public void initialize() {

		LocalDate today = LocalDate.now();

		statisticManagement.addAttendance(today.minusDays(6), 0, 60);
		statisticManagement.addAttendance(today.minusDays(5), 0, 40);
		statisticManagement.addAttendance(today.minusDays(5), 1, 25);
		statisticManagement.addAttendance(today.minusDays(4), 0, 30);
		statisticManagement.addAttendance(today.minusDays(3), 0, 45);
		statisticManagement.addAttendance(today.minusDays(1), 1, 10);

	}
}
