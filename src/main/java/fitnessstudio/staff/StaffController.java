package fitnessstudio.staff;


import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class StaffController {

	private static  final String REDIRECT = "redirect:/login";
	private final StaffManagement staffManagement;

	StaffController(StaffManagement staffManagement) {
		Assert.notNull(staffManagement, "StaffManagement must not be null");
		this.staffManagement = staffManagement;
	}

	@GetMapping("/staff")
	public String detail(@LoggedIn Optional<UserAccount> userAccount, Model model) {

		return userAccount.map(user -> {

			Optional<Staff> staff = staffManagement.findByUserAccount(user);

			if (staff.isPresent()) {
				model.addAttribute("staff", staff.get());
				return "staffDetail";
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
				if(staff.get().workedLastMonth()) {
					model.addAttribute("type", "payslip");
					model.addAttribute("staff", staff.get());
					return "pdfView";
				}
				return "redirect:/staff";
			}
			return REDIRECT;
		}).orElse(REDIRECT);
	}
}
