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
import java.util.List;

@Controller
public class RosterController {

	private final RosterManagement rosterManagement;
	private final StaffManagement staffs;
	private static final String ROSTER = "redirect:/roster";

	RosterController(RosterManagement rosterManagement, StaffManagement staffs){
		Assert.notNull(rosterManagement, "RosterManagement darf nicht 'null' sein.");
		Assert.notNull(staffs, "Das StaffRepository darf nicht 'null' sein.");
		this.staffs = staffs;
		this.rosterManagement = rosterManagement;
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster/{week}")
	String view_roster_week (@PathVariable int week, Model model){
		model.addAttribute("roster",RosterManager.getRosterByWeek(week).getRows());
		model.addAttribute("filter", false);
		model.addAttribute("staffs", staffs.getAllStaffs());
		model.addAttribute("week", week);
		model.addAttribute("weeks", RosterManager.getNextWeeks());
		model.addAttribute("header", RosterManager.getWeekDatesByWeek(week));
		return "roster/rosterView";
	}


	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster")
	String view_roster (Model model){
		return "redirect:/roster/" + Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster/{week}/{id}")
	String view_roster_filtered (@PathVariable int week, @PathVariable long id, Model model){
		model.addAttribute("roster",RosterManager.getRosterByWeek(week).getRows());
		model.addAttribute("filter", true);
		model.addAttribute("filterStaff", id);
		model.addAttribute("weeks",RosterManager.getNextWeeks());
		model.addAttribute("staffs", staffs.getAllStaffs());
		model.addAttribute("header", RosterManager.getWeekDatesByWeek(week));
		return "roster/rosterView";
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster/newrosterentry/{week}")
	String new_roster_entry(@PathVariable int week, Model model, RosterEntryForm form, Errors errors) {
		model.addAttribute("form", form);
		model.addAttribute("times", RosterManager.getRosterByWeek(week).getRows());
		model.addAttribute("roles", RosterManager.getRoles());
		model.addAttribute("staffs", staffs.getAllStaffs());
		model.addAttribute("errors", errors);
		model.addAttribute("week", week);
		return "roster/rosterNew";
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@PostMapping("/roster/newrosterentry")
	public String newRosterEntry(@Valid @ModelAttribute("form") RosterEntryForm form, Model model, Errors result) {
		rosterManagement.createRosterEntry(form, form.getWeek(), result);
		if (result.hasErrors()) {
			return new_roster_entry(form.getWeek(), model, form, result);
		}
		return ROSTER+"/"+form.getWeek();
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster/detail/delete/{slot}/{id}")
	String delete(@PathVariable Long slot, @PathVariable Long id) {
		rosterManagement.deleteRosterEntry(slot, id);
		return ROSTER;
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@PostMapping("/roster/edit")
	String edit(@Valid @ModelAttribute("form") RosterEntryForm form, Errors result) {
		rosterManagement.editRosterEntry(form);
		return ROSTER;
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster/detail/{week}/{day}/{slot}/{id}")
	String detail(@PathVariable int week, @PathVariable Long day, @PathVariable Long slot, @PathVariable Long id, RosterEntryForm form,  Model model) {
		model.addAttribute("shift", day);
		model.addAttribute("row", RosterManager.getRowById(day));
		model.addAttribute("day", slot);
		model.addAttribute("form", form);
		model.addAttribute("rosterEntry", RosterManager.getEntryById(id));
		model.addAttribute("roles", RosterManager.getRoles());
		model.addAttribute("week", week);
		return "roster/rosterDetail";
	}

	//TODO: Tests
	//TODO: Crash sicher machen
	//TODO: Nichts hinzufügen, wenn er bereits arbeitet, da sonst die sachen da ausgewählt werden.
	//TODO: Neuen Roster erstellen für die nächsten 4 Wochen
	//TODO: aktuelles Datum angeben
	//TODO: Beim erstellen eines Rosters schauen ob der bereits existiert, wenn ja, dann bitte einen neuen anlegen und den anderen löschen
	//TODO: Vorhandene Knöpfe zum filtern nach Rolle nutzen

}
