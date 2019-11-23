package fitnessstudio.roster;

import com.mysema.commons.lang.Assert;
import fitnessstudio.staff.StaffRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RosterController {

	private final RosterManagement rosterManagement;
	private final StaffRepository staffs;

	RosterController(RosterManagement rosterManagement, StaffRepository staffRepository){
		Assert.notNull(rosterManagement, "RosterManagement darf nicht 'null' sein.");
		Assert.notNull(staffRepository, "Das StaffRepository darf nicht null sein.");
		this.staffs = staffRepository;
		this.rosterManagement = rosterManagement;
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster")
	String view_roster (Model model){
		model.addAttribute("roster",RosterManager.getRoster().getRows());
		return "rosterView";
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster/newrosterentry")
	String new_roster_entry(Model model, RosterEntryForm form, Errors errors) {
		model.addAttribute("form", form);
		model.addAttribute("times", RosterManager.getRoster().getRows());
		model.addAttribute("roles", RosterManager.getRoles());
		model.addAttribute("staffs", staffs.findAll());
		model.addAttribute("errors", errors);
		return "rosterNew";
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@PostMapping("/roster/newrosterentry")
	public String newRosterEntry(@Valid @ModelAttribute("form") RosterEntryForm form, Model model, Errors result) {
		rosterManagement.createRosterEntry(form, result);
		if (result.hasErrors()) {
			return new_roster_entry(model, form, result);
		}
		return "redirect:/roster";
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster/detail/delete/{slot}/{id}")
	String delete(@PathVariable Long slot, @PathVariable Long id, Model model) {
		rosterManagement.deleteRosterEntry(slot, id);
		return "redirect:/roster";
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster/detail/{day}/{slot}/{id}")
	String detail(@PathVariable Long day, @PathVariable Long slot, @PathVariable Long id, Model model) {
		model.addAttribute("shift", day);
		model.addAttribute("day", slot);
		model.addAttribute("rosterEntry", RosterManager.getEntryById(id));
		model.addAttribute("roles", RosterManager.getRoles());
		//rosterManagement.deleteRosterEntry(slot, id);
		return "rosterDetail";
	}

	//TODO: Tests
	//TODO: Bearbeiten und löschen
	//TODO: Nichts hinzufügen, wenn er bereits arbeitet, da sonst die sachen da ausgewählt werden.
}
