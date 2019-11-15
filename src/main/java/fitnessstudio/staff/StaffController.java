package fitnessstudio.staff;

import com.mysema.commons.lang.Assert;
import org.salespointframework.inventory.InventoryItem;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class StaffController {


	private final RosterRepository catalog;
	private final StaffRepository staffs;
	private final RosterManagement rosterManagement;

	StaffController(RosterRepository repository, StaffRepository staffs, RosterManagement rosterManagement) {
		Assert.notNull(repository, "RosterRepository must not be null");
		Assert.notNull(staffs, "StaffRepository must not be null");
		Assert.notNull(rosterManagement, "RosterManagement must not be null");

		this.rosterManagement = rosterManagement;
		this.staffs = staffs;
		this.catalog = repository;
	}

	//TODO: catch exception if role != staff
	//TODO: add Entry for Roster


	/*// Fängt ab, wenn jemand mit einer Rolle != Staff auf die Seite möchte
	@ExceptionHandler({AccessDeniedException.class})
	public String error() {
		return "redirect:/";
	}*/

	@GetMapping("/roster/delete/{id}")
	String delete(@PathVariable Long id, Model model) {

		rosterManagement.deleteEntry(id);

		return "redirect:/roster";
	}

	// Zeigt den Roster an
	@PreAuthorize("hasRole('STAFF')")
	@GetMapping(path = "/roster")
	String roster(Model model) {
		model.addAttribute("roster",catalog.findAll());
		return "roster";
	}

	//Seite zu einen neuen RosterEintrag
	@GetMapping("/roster/newRoster")
	@PreAuthorize("hasRole('STAFF')")
	public String rosterEntry(Model model, RosterEntryForm form, Errors results) {
		List<String> roles = new ArrayList<>();
		roles.add("Thekenkraft");
		roles.add("Trainer");
		model.addAttribute("form", form);
		model.addAttribute("error", results);
		model.addAttribute("staffs", staffs.findAll());
		model.addAttribute("roles", roles);
		return "rosterNew";
	}

	//Erstellt einen neuen Eintrag

	@PostMapping("/roster/newRoster")
	public String newRosterEntry(@Valid @ModelAttribute("form") RosterEntryForm form, Model model, Errors result) {
		rosterManagement.createRosterEntry(form, result);
		if (result.hasErrors()) {
			return rosterEntry(model, form, result);
		}
		return "redirect:/roster";
	}
}
