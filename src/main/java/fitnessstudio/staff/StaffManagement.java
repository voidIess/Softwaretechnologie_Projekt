package fitnessstudio.staff;

import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Verwaltet Mitarbeiter
 */
@Service
@Transactional
public class StaffManagement {

	private final StaffRepository staffRepo;
	private final UserAccountManager userAccountManager;

	public StaffManagement(StaffRepository repository, UserAccountManager userAccountManager) {

		this.staffRepo = repository;
		this.userAccountManager = userAccountManager;
	}

	public List<Staff> getAllStaffs() {
		List<Staff> staffs = new ArrayList<>();
		for (Staff staff : staffRepo.findAll()) {
			if (staff.getUserAccount().isEnabled()) {
				staffs.add(staff);
			}
		}

		return staffs;
	}

	/**
	 * Einen neuen Mitarbeiter erstellen
	 * @param form Ausgefülltes Formular mit Angaben zum Mitarbeiter
	 * @param result Fehler, die beim Erstellen des Mitarbeiters auftreten
	 * @return Objekt des erstellten Mitarbeiters.
	 */
	public Staff createStaff(StaffForm form, Errors result) {
		Staff staff = null;
		try {
			if (userAccountManager.findByUsername(form.getUsername()).isPresent()) {
				result.rejectValue("username", "register.duplicate.userAccountName");
				return null;
			}
			String email = form.getEmail();
			if (emailExists(email)) {
				result.rejectValue("email", "register.duplicate.userAccountEmail");
				return null;
			}
			UserAccount userAccount = userAccountManager.create(form.getUsername(),
					Password.UnencryptedPassword.of(form.getPassword()), email);
			staff = new Staff(userAccount, form.getFirstName(), form.getLastName(),
					Money.of(new BigDecimal(form.getSalary()), "EUR"));

			staff.getUserAccount().add(Role.of("STAFF"));
			staffRepo.save(staff);

		} catch (Exception ignored) {
			//no staff created
		}
		return staff;
	}

	/**
	 * Speichert Aenderungen an einem Mitarbeiter im Repository
	 * @param staff Mitarbeiter mit Aenderungen
	 */
	public void saveStaff(Staff staff) {
		staffRepo.save(staff);
	}

	/**
	 * Entfernt Mitarbeiter
	 * @param id ID des Mitarbeiters
	 */
	public void removeStaff(long id) {
		Optional<Staff> staffs = this.findById(id);
		if (staffs.isPresent()) {
			Staff staff = staffs.get();
			UserAccountIdentifier identifier = staff.getUserAccount().getId();
			//staffRepo.deleteById(id);
			assert identifier != null;
			userAccountManager.disable(identifier);

		}
	}

	/**
	 * Findet einen Mitarbeiter anhand seines Spring UserAccounts.
	 * @param userAccount userAccount, nachdem gesucht wird.
	 * @return Ergebnis der Suche
	 */
	public Optional<Staff> findByUserAccount(UserAccount userAccount) {
		return staffRepo.findByUserAccount(userAccount);
	}

	/**
	 * Findet einen Mitarbeiter anhand seiner ID
	 * @param staffId ID des Mitarbeiters
	 * @return
	 */
	public Optional<Staff> findById(Long staffId) {
		return staffRepo.findById(staffId);
	}

	/**
	 * Ueberprueft, ob es bereits einen Mitarbeiter mit einer bestimmten Email Adresse gibt
	 * @param email Email Adresse mit der die existierenden verglichen werden sollen.
	 * @return true wenn vorhanden, false wenn noch nicht vorhanden.
	 */
	boolean emailExists(String email) {
		for (UserAccount userAccount : userAccountManager.findAll()) {
			String userAccountEmail = userAccount.getEmail();
			if (userAccountEmail.equalsIgnoreCase(email)) {
				return true;
			}
		}
		return false;
	}

}
