package fitnessstudio.staff;


import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.Document;
import fitnessstudio.member.Member;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;
import java.util.Optional;

@Controller
public class StaffController {

	private final RosterRepository catalog;
	private final StaffManagement staffManagement;

	StaffController(RosterRepository repository, StaffManagement staffManagement) {
		this.catalog = repository;
		this.staffManagement = staffManagement;
	}

	@PreAuthorize("hasRole('STAFF')")
	@GetMapping(path = "/roster")
	String roster(Model model) {
		model.addAttribute("roster",catalog.findByRole(StaffRole.COUNTER, Sort.by("rosterEntryId").descending()));
		return "roster";
	}

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

	@PreAuthorize("hasRole('STAFF')")
	@PostMapping("/printPdf")
	public String printPdf(@LoggedIn Optional<UserAccount> userAccount, Model model) {

		if (userAccount.isEmpty()) {
			return "redirect:/login";
		}

		model.addAttribute("type", "payslip");
		model.addAllAttributes(staffManagement.createPdf(userAccount.get()));

		return "pdfView";
	}

}
