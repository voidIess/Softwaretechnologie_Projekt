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

/**
 * Spring MVC Controller fuer Anfragen auf Mitarbeiter
 */
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

	/**
	 * Liste aller bestehenden Mitarbeiter
	 * @param model Model der Seite
	 * @return HTML-Template staffList mit allen Mitarbeitern
	 */
	@PreAuthorize("hasRole('BOSS') ")
	@GetMapping("/staffs")
	public String getAllStaffs(Model model) {
		model.addAttribute("staffs", staffManagement.getAllStaffs());
		return STAFFS;
	}

	/**
	 * Details eines Mitarbeiters
	 * @param id ID des Mitarbeiters
	 * @param model Model der Seite
	 * @return HTML-Templatae staffDetail mit Details des Mitarbeiters. ERROR bei Fehler.
	 */
	@PreAuthorize("hasRole('BOSS') ")
	@GetMapping("/staff/{id}")
	public String detail(@PathVariable long id, Model model) {
		Optional<Staff> staff = staffManagement.findById(id);
		if (staff.isPresent()) {
			model.addAttribute(STAFF, staff.get());
			model.addAttribute("form", new SalaryForm() {
				@Override
				public @NotEmpty(message = "Gehalt ist leer.") String getSalary() {
					return staff.get().getSalary().getNumber().toString();
				}
			});
			return "staff/staffDetail";
		}
		model.addAttribute(STATUS, "400");
		model.addAttribute(ERROR, "ID NOT FOUND");
		return ERROR;
	}

	/**
	 * Mitarbeiter hinzufuegen
	 * @param model Model der Seite mit Formular
	 * @param form Formular fuer die Angaben zum Mitarbeiter
	 * @param results Fehler beim Erstellen des Mitarbeiters.
	 * @return HTML-Template add_staff
	 */
	@PreAuthorize("hasRole('BOSS') ")
	@GetMapping("/newStaff")
	public String addStaff(Model model, StaffForm form, Errors results) {
		model.addAttribute("form", form);
		model.addAttribute(ERROR, results);
		return "staff/add_staff";
	}


	/**
	 * Onlineauftrag zum Erstellen des Mitarbeiters
	 * @param form Formular mit Angaben zum Mitarbeiter
	 * @param model Model der Seite für eventuelle Korrekturen
	 * @param result Fehler, die beim Erstellen aufgetreten sind
	 * @return Bei Fehlern redirect auf addStaff, wenn nicht redirect auf Startseite
	 */
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


	/**
	 * Mitarbeiter bearbeiten
	 * @param id ID des Mitarbeiters
	 * @param model Model der Seite
	 * @return ERROR wenn Mitarbeiter nicht exisitiert, sonst HTML-Template editStaff
	 */
	@PreAuthorize("hasRole('STAFF') ")
	@GetMapping("/staff/edit/{id}")
	public String editStaff(@PathVariable long id, Model model) {
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

		};
	}

	/**
	 * Online Auftrag zum Bearbeiten des Mitarbeiters
	 * @param id ID des Mitarbeiters
	 * @param form Formular mit Aenderungen am Mitarbeiter
	 * @param model Model der Seite
	 * @return Wenn Änderungen erfolgreich redirect zum Bearbeiten, sonst ERROR
	 */
	@PreAuthorize("hasRole('STAFF') ")
	@PostMapping("/staff/edit/{id}")
	public String editStaff(@PathVariable long id, @Valid EditStaffForm form, Model model) {
		Optional<Staff> staffs = staffManagement.findById(id);
		if (staffs.isPresent()) {
			Staff staff = staffs.get();
			staff.setFirstName(form.getFirstName());
			staff.setLastName(form.getLastName());
			//staff.setSalary(Money.of(new BigDecimal(form.getSalary()), "EUR"));
			staff.getUserAccount().setEmail(form.getEmail());
			staffManagement.saveStaff(staff);
			return "redirect:/staff/edit/" + staff.getStaffId();
		}

		return ERROR;
	}

	/**
	 * Gehalt des Mitarbeiters bearbeiten
	 * @param id ID des Mitarbeiters
	 * @param form Formular mit Aenderungen
	 * @return ERROR bei Fehler, sonst redirect zu Mitarbeiter Uebersicht
	 */
	@PreAuthorize("hasRole('BOSS') ")
	@PostMapping("/staff/update/{id}")
	public String editSalary(@PathVariable long id, @Valid SalaryForm form) {
		Optional<Staff> staffs = staffManagement.findById(id);
		if (staffs.isPresent()) {
			Staff staff = staffs.get();
			staff.setSalary(Money.of(new BigDecimal(form.getSalary()), "EUR"));
			staffManagement.saveStaff(staff);
			return REDIRECT_STAFFS;
		}
		return ERROR;
	}


	/**
	 * Details des eingeloggten Mitarbeiters anzeigen
	 * @param userAccount userAccount des derzeitig eingeloggten Mitarbeiters
	 * @param model Model der Seite
	 * @return HTML-Template staffDetail, bei Fehlern redirect aufs Login
	 */
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

	/**
	 * Zeigt den Gehaltsschein des eingeloggten Mitarbeiters als PDF an
	 * @param userAccount derzeitig eingeloggter userAccount
	 * @param model Model der Seite
	 * @return HTML-Template pdfView wenn es einen Lohnschein gibt. Redirect auf staffDetail wenn es keinen gibt.
	 * Redirect zu Login wenn es UserAccount nicht gibt
	 */
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
