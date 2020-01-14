package fitnessstudio.statistics;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

/**
 * Spring MVC Controller to show the statistics.
 *
 * @author Lea Haeusler
 */
@Controller
public class StatisticController {

	private final StatisticManagement statisticManagement;

	/**
	 * Creates a {@link StatisticController} instance with the given {@link StatisticManagement}.
	 *
	 * @param statisticManagement Must not be {@literal null}.
	 */
	StatisticController(StatisticManagement statisticManagement){
		Assert.notNull(statisticManagement, "StatisticManagement must not be null!");

		this.statisticManagement = statisticManagement;
	}

	/**
	 * Shows the current statistics of member behaviour, selling earnings and cost/revenue balance.
	 *
	 * @param model model of the page
	 * @return html template statistics with the charts
	 */
	@GetMapping("/admin/attendanceStatistic")
	@PreAuthorize("hasRole('BOSS')")
	public String showAttendanceStatistic(Model model) {
		model.addAttribute("week", statisticManagement.getDaysOfWeek());
		model.addAttribute("averageTimes", statisticManagement.getAverageTimesOfThisWeek());
		model.addAttribute("memberAmounts", statisticManagement.getMemberAmountsOfThisWeek());
		model.addAttribute("sellingEarnings", statisticManagement.getSellingEarningsOfThisWeek());
		model.addAttribute("expendituresPer", statisticManagement.getPercentageExpenditure());
		model.addAttribute("revenuesPer", statisticManagement.getPercentageRevenue());
		model.addAttribute("expendituresAbs", statisticManagement.getStaffExpenditurePerMonth());
		model.addAttribute("revenuesAbs", statisticManagement.getMemberRevenuePerMonth());
		model.addAttribute("today", LocalDate.now().getDayOfWeek());
		return "statistic/statistic";
	}

}
