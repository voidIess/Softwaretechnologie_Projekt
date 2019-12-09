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

	RosterController(RosterManagement rosterManagement, StaffManagement staffManagement) {
		Assert.notNull(rosterManagement, "RosterManagement darf nicht 'null' sein!");
		Assert.notNull(staffManagement, "StaffManagement darf nicht 'null' sein!");
		this.rosterManagement = rosterManagement;
		this.staffManagement = staffManagement;
	}

	@GetMapping("/roster")
	String defaultRoster(Model model) {
		return "redirect:/roster/" + Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster/{week}")
	String rosterView(@PathVariable int week, Model model) {
		model.addAttribute("roster", rosterManagement.getRosterByWeek(week).getRows());
		model.addAttribute("filter", false);
		model.addAttribute("staffs", staffManagement.getAllStaffs());
		model.addAttribute("week", week);
		model.addAttribute("header", RosterDataConverter.getWeekDatesByWeek(week));
		model.addAttribute("weeks", rosterManagement.getNextWeeks());
		return "roster/rosterView";
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster/{week}/{id}")
	String rosterViewFiltered(@PathVariable long id, @PathVariable int week, Model model) {
		model.addAttribute("roster", rosterManagement.getRosterByWeek(week).getRows());
		model.addAttribute("filter", true);
		model.addAttribute("filterStaff", id);
		model.addAttribute("staffs", staffManagement.getAllStaffs());
		model.addAttribute("week", week);
		model.addAttribute("header", RosterDataConverter.getWeekDatesByWeek(week));
		model.addAttribute("weeks", rosterManagement.getNextWeeks());
		return "roster/rosterView";
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster/newRosterEntry/{week}")
	String newRosterEntry(@PathVariable int week, Model model, RosterEntryForm form, Errors errors) {
		model.addAttribute("form", form);
		model.addAttribute("times", rosterManagement.getRosterByWeek(week).getRows());
		model.addAttribute("roles", RosterDataConverter.getRoleList());
		model.addAttribute("staffs", staffManagement.getAllStaffs());
		model.addAttribute("errors", errors);
		model.addAttribute("week", week);
		return "roster/rosterNew";
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@PostMapping("/roster/newRosterEntry")
	String createNewRosterEntry(Model model, @Valid @ModelAttribute("form") RosterEntryForm form, Errors errors) {
		rosterManagement.createRosterEntry(form,-1, errors);
		if (errors.hasErrors()) {
			return newRosterEntry(form.getWeek(), model, form, errors);
		}
		return "redirect:/roster/" + form.getWeek();
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster/detail/{week}/{shift}/{day}/{id}")
	String showDetail(@PathVariable int week, @PathVariable int shift, @PathVariable int day, @PathVariable long id, RosterEntryForm form, Model model) {
		model.addAttribute("day", day);
		model.addAttribute("shift", shift);
		model.addAttribute("row", rosterManagement.getRosterByWeek(week).getRows().get(shift));
		model.addAttribute("form", form);
		model.addAttribute("rosterEntry", rosterManagement.getRosterEntryById(week, shift, day, id));
		model.addAttribute("roles", RosterDataConverter.getRoleList());
		model.addAttribute("week", week);
		return "roster/rosterDetail";
	}

	//TODO: show Tag des RosterEntryForm
	//TODO: Tests
	//TODO: Crash sicher machen
	//TODO: Nichts hinzufügen, wenn er bereits arbeitet, da sonst die sachen da ausgewählt werden.
	//TODO: Neuen Roster erstellen für die nächsten 4 Wochen
	//TODO: aktuelles Datum angeben
	//TODO: Beim erstellen eines Rosters schauen ob der bereits existiert, wenn ja, dann bitte einen neuen anlegen und den anderen löschen
	//TODO: Vorhandene Knöpfe zum filtern nach Rolle nutzen

	@PostMapping("/roster/editEntry/{id}")
	String editEntry(@Valid @ModelAttribute("form") RosterEntryForm form, Errors errors, @PathVariable long id, Model model) {
		rosterManagement.editEntry(form, id, errors);
		if (errors.hasErrors()){
			return showDetail(form.getWeek(),rosterManagement.getTimeIndex(form.getTimes().get(0)),form.getDay(),id, form, model);
		}
		return "redirect:/roster/" + form.getWeek();
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster/detail/delete/{week}/{shift}/{day}/{id}")
	String delete(@PathVariable int week, @PathVariable int shift, @PathVariable int day, @PathVariable long id) {
		rosterManagement.deleteEntry(week, shift, day, id);
		return "redirect:/roster/" + week;
	}

	//TODO: Trainingübersichts seite
	//TODO: entry erst erstellen wenn accepted
	//TODO: training löschen
	//TODO: es soll nicht möglich sein zu ändern wenn Training vorhanden
}








