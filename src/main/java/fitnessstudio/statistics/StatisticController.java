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

	@GetMapping("/attendanceStatistic")
	@PreAuthorize("hasRole('BOSS')")
	public String showAttendanceStatistic(Model model) {
		model.addAttribute("averageTime", statisticManagement.getAverageTimeToday());
		model.addAttribute("memberAmount", statisticManagement.getMemberAmountToday());
		return "attendanceStatistic";
	}

}
