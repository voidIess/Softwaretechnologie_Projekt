package fitnessstudio.statistics;

import fitnessstudio.member.MemberManagement;
import org.salespointframework.core.DataInitializer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDate;

@Component
@Order(101)	//after MemberDataInitializer
public class StatisticDataInitializer implements DataInitializer {

	private final StatisticManagement statisticManagement;
	private final MemberManagement memberManagement;

	public StatisticDataInitializer(StatisticManagement statisticManagement, MemberManagement memberManagement) {

		Assert.notNull(statisticManagement, "StatisticManagement must not be null");
		Assert.notNull(memberManagement, "MemberManagement must not be null");

		this.statisticManagement = statisticManagement;
		this.memberManagement = memberManagement;
	}

	@Override
	public void initialize() {

		//Id of default member
		long memberId = memberManagement.findAll().toList().get(0).getMemberId();

		LocalDate today = LocalDate.now();

		statisticManagement.addAttendance(today.minusDays(6), memberId, 60);
		statisticManagement.addAttendance(today.minusDays(5), memberId, 15);
		statisticManagement.addAttendance(today.minusDays(4), memberId, 30);
		statisticManagement.addAttendance(today.minusDays(3), memberId, 45);
		statisticManagement.addAttendance(today.minusDays(1), memberId, 10);

	}
}
