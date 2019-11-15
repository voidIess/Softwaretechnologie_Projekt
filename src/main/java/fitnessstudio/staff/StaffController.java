package fitnessstudio.staff;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class StaffController {


	private final RosterRepository catalog;
	private final StaffRepository staffs;
	private final RosterManagement rosterManagement;

	StaffController(RosterRepository repository, StaffRepository staffs, RosterManagement rosterManagement) {
		this.rosterManagement = rosterManagement;
		this.staffs = staffs;
		this.catalog = repository;
	}

	//TODO: catch exception if role != staff
	//TODO: add Entry for Roster

	@PreAuthorize("hasRole('STAFF')")
	@GetMapping(path = "/roster")
	String roster(Model model) {
		model.addAttribute("roster",catalog.findAll());
		return "roster";
	}

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

	@PostMapping("/roster/newRoster")
	public String newRosterEntry(@Valid @ModelAttribute("form") RosterEntryForm form, Model model, Errors result) {

		if (result.hasErrors()) {
			return rosterEntry(model, form, result);
		}

		rosterManagement.createRosterEntry(form, result);
		return "redirect:/roster";
	}


}
