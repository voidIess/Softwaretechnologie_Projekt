package fitnessstudio.staff;


import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class StaffController {

	private static final String REDIRECT = "redirect:/login";
	private static final String REDIRECT_HOME = "redirect:/";
	private static final String STATUS = "status";
	private static final String ERROR = "error";
	private static final String STAFFS = "staff/staffList";
	private final StaffManagement staffManagement;


	StaffController(StaffManagement staffManagement) {
		Assert.notNull(staffManagement, "StaffManagement must not be null");
		this.staffManagement = staffManagement;
	}

	@GetMapping("/staffs")
	public String getAllStaffs(Model model) {
		model.addAttribute("staffs", staffManagement.getAllStaffs());
		return STAFFS;
	}

	@PreAuthorize("hasRole('BOSS') ")
	@GetMapping(path = "/staff/{id}")
	public String detail(@PathVariable long id, Model model) {
		Optional<Staff> staff = staffManagement.findById(id);
		if (staff.isPresent()) {
			model.addAttribute("staff", staff.get());
			return "staff/staffDetail";
		}
		model.addAttribute(STATUS, "400");
		model.addAttribute(ERROR, "ID_NOT FOUND");
		return ERROR;
	}

	@PreAuthorize("hasRole('BOSS') ")
	@GetMapping("/newStaff")
	public String addStaff(Model model, StaffForm form, Errors results) {
		model.addAttribute("form", form);
		model.addAttribute("error", results);
		return "staff/add_staff";
	}


	@PreAuthorize("hasRole('BOSS') ")
	@PostMapping("/newStaff")
	public String addStaff(@Valid @ModelAttribute("form") StaffForm form, Model model, Errors result) {
		if (result == null) {
			return REDIRECT_HOME;
		} else if (result.hasErrors()) {
			return addStaff(model, form, result);
		} else if (staffManagement.createStaff(form, result) != null) {
			return REDIRECT_HOME;
		} else {
			return addStaff(model, form, result);
		}
	}

	@GetMapping("/staffDetail")
	public String getAccount(@LoggedIn Optional<UserAccount> userAccount, Model model) {

		return userAccount.map(user -> {

			Optional<Staff> staff = staffManagement.findByUserAccount(user);

			if (staff.isPresent()) {
				model.addAttribute("staff", staff.get());
				return "staff/staffDetail";
			}
			return REDIRECT;
		}).orElse(REDIRECT);
	}

	@PreAuthorize("hasRole('STAFF')")
	@PostMapping("/printPdfPayslip")
	public String printPdfPayslip(@LoggedIn Optional<UserAccount> userAccount, Model model) {

		return userAccount.map(user -> {

			Optional<Staff> staff = staffManagement.findByUserAccount(user);

			if (staff.isPresent()) {
				if (staff.get().workedLastMonth()) {
					model.addAttribute("type", "payslip");
					model.addAttribute("staff", staff.get());
					return "pdfView";
				}
				return "redirect:/staff/staffDetail";
			}
			return REDIRECT;
		}).orElse(REDIRECT);
	}
}
