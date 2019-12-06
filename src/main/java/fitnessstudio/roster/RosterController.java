package fitnessstudio.roster;

import com.mysema.commons.lang.Assert;
import fitnessstudio.staff.StaffManagement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Calendar;

@Controller
public class RosterController {

	private final RosterManagement rosterManagement;
	private final StaffManagement staffManagement;

	RosterController(RosterManagement rosterManagement, StaffManagement staffManagement) {
		Assert.notNull(rosterManagement, "RosterManagement darf nicht 'null' sein!");
		Assert.notNull(staffManagement, "StaffManagement darf nicht 'null' sein!");
		this.rosterManagement = rosterManagement;
		this.staffManagement = staffManagement;
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster")
	String defaultRoster (Model model){
		return "redirect:/roster/" + Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster/{week}")
	String rosterView (@PathVariable int week, Model model){
		model.addAttribute("roster", rosterManagement.getRosterByWeek(week).getRows());
		model.addAttribute("filter", false);
		model.addAttribute("staffs", staffManagement.getAllStaffs());
		model.addAttribute("week", week);
		model.addAttribute("header", RosterDataConverter.getWeekDatesByWeek(week));
		model.addAttribute("weeks", rosterManagement.getNextWeeks());
		return "roster/rosterView";
	}









}
