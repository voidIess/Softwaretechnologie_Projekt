package fitnessstudio.roster;

import com.mysema.commons.lang.Assert;
import fitnessstudio.staff.StaffManagement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Calendar;

@Controller
public class RosterController {

	private final RosterManagement rosterManagement;
	private final StaffManagement staffManagement;
	private String defaultLink = "redirect:/roster/";
	private String staffs = "staffs";

	RosterController(RosterManagement rosterManagement, StaffManagement staffManagement) {
		Assert.notNull(rosterManagement, "RosterManagement darf nicht 'null' sein!");
		Assert.notNull(staffManagement, "StaffManagement darf nicht 'null' sein!");
		this.rosterManagement = rosterManagement;
		this.staffManagement = staffManagement;
	}

	@GetMapping("/roster")
	public String defaultRoster(Model model) {
		return defaultLink + Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster/{week}")
	public String rosterView(@PathVariable int week, Model model) {
		model.addAttribute("roster", rosterManagement.getRosterByWeek(week).getRows());
		model.addAttribute("filter", false);
		model.addAttribute(staffs, staffManagement.getAllStaffs());
		model.addAttribute("week", week);
		model.addAttribute("header", RosterDataConverter.getWeekDatesByWeek(week));
		model.addAttribute("weeks", rosterManagement.getNextWeeks());
		return "roster/rosterView";
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster/{week}/{id}")
	public String rosterViewFiltered(@PathVariable long id, @PathVariable int week, Model model) {
		model.addAttribute("roster", rosterManagement.getRosterByWeek(week).getRows());
		model.addAttribute("filter", true);
		model.addAttribute("filterStaff", id);
		model.addAttribute(staffs, staffManagement.getAllStaffs());
		model.addAttribute("week", week);
		model.addAttribute("header", RosterDataConverter.getWeekDatesByWeek(week));
		model.addAttribute("weeks", rosterManagement.getNextWeeks());
		return "roster/rosterView";
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster/newRosterEntry/{week}")
	public String newRosterEntry(@PathVariable int week, Model model, RosterEntryForm form, Errors errors) {
		model.addAttribute("form", form);
		model.addAttribute("times", rosterManagement.getRosterByWeek(week).getRows());
		model.addAttribute("roles", RosterDataConverter.getRoleList());
		model.addAttribute(staffs, staffManagement.getAllStaffs());
		model.addAttribute("errors", errors);
		model.addAttribute("week", week);
		return "roster/rosterNew";
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@PostMapping("/roster/newRosterEntry")
	public String createNewRosterEntry(Model model, @Valid @ModelAttribute("form") RosterEntryForm form, Errors errors) {
		rosterManagement.createEntry(form, -1, errors);
		if (errors.hasErrors()) {
			return newRosterEntry(form.getWeek(), model, form, errors);
		}
		return defaultLink + form.getWeek();
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster/detail/{week}/{shift}/{day}/{id}")
	public String showDetail(@PathVariable int week, @PathVariable int shift, @PathVariable int day, @PathVariable long id, RosterEntryForm form, Model model) {
		model.addAttribute("day", day);
		model.addAttribute("shift", shift);
		model.addAttribute("row", rosterManagement.getRosterByWeek(week).getRows().get(shift));
		model.addAttribute("form", form);
		model.addAttribute("rosterEntry", rosterManagement.getRosterEntryById(week, shift, day, id));
		model.addAttribute("roles", RosterDataConverter.getRoleList());
		model.addAttribute("week", week);
		return "roster/rosterDetail";
	}

	@PostMapping("/roster/editEntry/{id}")
	public String editEntry(@Valid @ModelAttribute("form") RosterEntryForm form, Errors errors, @PathVariable long id, Model model) {
		rosterManagement.editEntry(form, id, errors);
		if (errors.hasErrors()) {
			return showDetail(form.getWeek(), rosterManagement.getTimeIndex(form.getTimes().get(0)), form.getDay(), id, form, model);
		}
		return defaultLink + form.getWeek();
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster/detail/delete/{week}/{shift}/{day}/{id}")
	public String delete(@PathVariable int week, @PathVariable int shift, @PathVariable int day, @PathVariable long id) {
		rosterManagement.deleteEntry(week, shift, day, id);
		return defaultLink + week;
	}


	@PreAuthorize("hasRole('BOSS') ")
	@PostMapping("/staff/delete/{id}")
	public String deleteStaff(@PathVariable long id, Model model) {
		rosterManagement.deleteAllEntriesFromStaff(id);
		staffManagement.removeStaff(id);
		return "redirect:/staffs";
	}


}








