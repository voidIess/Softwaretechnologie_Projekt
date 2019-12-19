package fitnessstudio.statistics;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatisticController {

	private final StatisticManagement statisticManagement;

	StatisticController(StatisticManagement statisticManagement){
		Assert.notNull(statisticManagement, "StatisticManagement must not be null!");

		this.statisticManagement = statisticManagement;
	}

	@GetMapping("/admin/attendanceStatistic")
	@PreAuthorize("hasRole('BOSS')")
	public String showAttendanceStatistic(Model model) {
		model.addAttribute("week", statisticManagement.getDaysOfWeek());
		model.addAttribute("averageTimes", statisticManagement.getAverageTimesOfThisWeek());
		model.addAttribute("memberAmounts", statisticManagement.getMemberAmountsOfThisWeek());
		model.addAttribute("sellingEarnings", statisticManagement.getSellingEarningsOfThisWeek());
		return "statistic/statistic";
	}

}