package fitnessstudio.staff;


import org.javamoney.moneta.Money;
import org.jetbrains.annotations.NotNull;
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
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.Optional;

@Controller
public class StaffController {

	private static final String REDIRECT = "redirect:/login";
	private static final String REDIRECT_HOME = "redirect:/";
	private static final String REDIRECT_STAFFS = "redirect:/staffs";
	private static final String STATUS = "status";
	private static final String ERROR = "error";
	private static final String STAFFS = "staff/staffList";
	private static final String STAFF = "staff";
	private final StaffManagement staffManagement;


	StaffController(StaffManagement staffManagement) {
		Assert.notNull(staffManagement, "StaffManagement must not be null");
		this.staffManagement = staffManagement;
	}

	@PreAuthorize("hasRole('BOSS') ")
	@GetMapping("/staffs")
	public String getAllStaffs(Model model) {
		model.addAttribute("staffs", staffManagement.getAllStaffs());
		return STAFFS;
	}

	@PreAuthorize("hasRole('BOSS') ")
	@GetMapping("/staff/{id}")
	public String detail(@PathVariable long id, Model model) {
		Optional<Staff> staff = staffManagement.findById(id);
		if (staff.isPresent()) {
			model.addAttribute(STAFF, staff.get());
			return "staff/staffDetail";
		}
		model.addAttribute(STATUS, "400");
		model.addAttribute(ERROR, "ID NOT FOUND");
		return ERROR;
	}

	@PreAuthorize("hasRole('BOSS') ")
	@GetMapping("/newStaff")
	public String addStaff(Model model, StaffForm form, Errors results) {
		model.addAttribute("form", form);
		model.addAttribute(ERROR, results);
		return "staff/add_staff";
	}


	@PreAuthorize("hasRole('BOSS') ")
	@PostMapping("/newStaff")
	public String addStaff(@Valid @ModelAttribute("form") StaffForm form, Model model, Errors result) {
		if (result == null || staffManagement.createStaff(form, result) != null) {
			return REDIRECT_HOME;
		} else if (result.hasErrors()) {
			return addStaff(model, form, result);
		} else {
			return addStaff(model, form, result);
		}
	}

	@PreAuthorize("hasRole('BOSS') ")
	@GetMapping("/staff/edit/{id}")
	public String editStaff(@PathVariable long id, Model model, EditStaffForm form) {
		Optional<Staff> staffs = staffManagement.findById(id);
		if (staffs.isPresent()) {
			Staff staff = staffs.get();
			model.addAttribute(STAFF, staff);
			model.addAttribute("id", id);
			model.addAttribute("form", getEditStaffForm(staff));
			return "staff/edit_staff";
		}
		return ERROR;
	}

	// for keeping previous value in input field
	@NotNull
	private EditStaffForm getEditStaffForm(Staff staff) {
		return new EditStaffForm() {

			@Override
			public @NotEmpty(message = "Vorname ist leer.") String getFirstName() {
				return staff.getFirstName();
			}

			@Override
			public @NotEmpty(message = "Nachname ist leer.") String getLastName() {
				return staff.getLastName();
			}

			@Override
			public @NotEmpty(message = "Email ist leer.") String getEmail() {
				return staff.getUserAccount().getEmail();
			}

			@Override
			public @NotEmpty(message = "Gehalt ist leer.") String getSalary() {
				return staff.getSalary().getNumber().toString();
			}
		};
	}

	@PreAuthorize("hasRole('BOSS') ")
	@PostMapping("/staff/edit/{id}")
	public String editStaff(@PathVariable long id, @Valid EditStaffForm form, Model model) {
		Optional<Staff> staffs = staffManagement.findById(id);
		if (staffs.isPresent()) {
			Staff staff = staffs.get();
			staff.setFirstName(form.getFirstName());
			staff.setLastName(form.getLastName());
			staff.setSalary(Money.of(new BigDecimal(form.getSalary()), "EUR"));
			staff.getUserAccount().setEmail(form.getEmail());
			staffManagement.saveStaff(staff);
			return REDIRECT_STAFFS;
		}

		return ERROR;
	}

	@GetMapping("/staffDetail")
	public String getAccount(@LoggedIn Optional<UserAccount> userAccount, Model model) {

		return userAccount.map(user -> {

			Optional<Staff> staff = staffManagement.findByUserAccount(user);

			if (staff.isPresent()) {
				model.addAttribute(STAFF, staff.get());
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
					model.addAttribute(STAFF, staff.get());
					return "pdfView";
				}
				return "redirect:/staff/staffDetail";
			}
			return REDIRECT;
		}).orElse(REDIRECT);
	}
}
