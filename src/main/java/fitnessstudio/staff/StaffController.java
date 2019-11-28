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
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class StaffController {

	private final StaffManagement staffManagement;

	StaffController(StaffManagement staffManagement) {
		Assert.notNull(staffManagement, "StaffManagement must not be null");
		this.staffManagement = staffManagement;
	}

	// shows information about one staff
	@GetMapping("/staff")
	public String detail(@LoggedIn Optional<UserAccount> userAccount, Model model) {

		return userAccount.map(user -> {

			Optional<Staff> staff = staffManagement.findByUserAccount(user);

			if (staff.isPresent()) {
				model.addAttribute("staff", staff.get());
				return "staffDetail";
			}
			return "redirect:/login";
		}).orElse("redirect:/login");
	}

	// prints payslip of given staff
	@PreAuthorize("hasRole('STAFF')")
	@PostMapping("/printPdfPayslip")
	public String printPdfPayslip(@LoggedIn Optional<UserAccount> userAccount, Model model) {

		if (userAccount.isEmpty()) {
			return "redirect:/login";
		}

		model.addAttribute("type", "payslip");
		model.addAllAttributes(staffManagement.createPdfPayslip(userAccount.get()));

		return "pdfView";
	}
}
