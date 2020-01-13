package fitnessstudio.roster;

import com.mysema.commons.lang.Assert;
import fitnessstudio.staff.StaffManagement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Calendar;


/**
 * Spring MVC Controller fuer Anfragen auf den Dienstplan
 * @author Markus.
 */
@Controller
public class RosterController {

	private final RosterManagement rosterManagement;
	private final StaffManagement staffManagement;
	private String defaultLink = "redirect:/roster/";
	private String staffs = "staffs";

	/**
	 * Konstruktor des Controllers
	 * @param rosterManagement Management fuer Dienstplan
	 * @param staffManagement Management fuer Mitarbeiter
	 */
	RosterController(RosterManagement rosterManagement, StaffManagement staffManagement) {
		Assert.notNull(rosterManagement, "RosterManagement darf nicht 'null' sein!");
		Assert.notNull(staffManagement, "StaffManagement darf nicht 'null' sein!");
		this.rosterManagement = rosterManagement;
		this.staffManagement = staffManagement;
	}

	/**
	 * Baut die Website fuer den Standard-Dienstplan. Der Standard-Dienstplan ist der der heutigen Woche.
	 * Pfad: /roster
	 * @return redirect zu Link zu dem Dienstplan der heutigen Woche.
	 */
	@GetMapping("/roster")
	public String defaultRoster() {
		return defaultLink + Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
	}

	/**
	 * Nur fuer Staffs oder Boss einsehbar. Baut den Dienstplan einer beliebigen Woche.
	 * Pfad: /roster/{week}
	 * @param week Die Woche fuer die der Dienstplan gefunden werden soll
	 * @param model Model der Seite
	 * @return HTML-Template rosterView + Model
	 */
	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster/{week}")
	public String rosterView(@PathVariable int week, Model model) {
		model.addAttribute("roster", rosterManagement.getRosterByWeek(week).getRows());
		model.addAttribute("filter", false);
		model.addAttribute(staffs, staffManagement.getAllStaffs());
		model.addAttribute("week", week);
		model.addAttribute("header", RosterDataConverter.getWeekDatesByWeek(week));
		model.addAttribute("weeks", rosterManagement.getNextWeeks());
		return "roster/rosterView";
	}

	/**
	 * Nur fuer Staff oder Boss einsehbar. Filtert den Dienstplan einer bestimmten Woche nach einem Mitarbeiter
	 * Pfad: roster/{week}/{id}
	 * @param id ID des zu filternden Mitarbeiters
	 * @param week Woche des Dienstplans
	 * @param model Model der Seite
	 * @return HTML-Template rosterView mit nur den Eintraegen des Mitarbeiters mit der ID
	 */
	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster/{week}/{id}")
	public String rosterViewFiltered(@PathVariable long id, @PathVariable int week, Model model) {
		model.addAttribute("roster", rosterManagement.getRosterByWeek(week).getRows());
		model.addAttribute("filter", true);
		model.addAttribute("filterStaff", id);
		model.addAttribute(staffs, staffManagement.getAllStaffs());
		model.addAttribute("week", week);
		model.addAttribute("header", RosterDataConverter.getWeekDatesByWeek(week));
		model.addAttribute("weeks", rosterManagement.getNextWeeks());
		return "roster/rosterView";
	}

	/**
	 * Nur fuer Staffs oder Boss einsehbar.
	 * Formular zur Erstellung eines neuen Dienstplaneintrags
	 * Pfad: roster/newRoster/{week}
	 * @param week Kalenderwoche, fuer die der Dienstplaneintrag erstellt werden soll
	 * @param model Model der Seite.
	 * @param form Formular fuer den neuen Eintrag
	 * @param errors Fehler bei der Erstellung des Eintrages
	 * @return HTML-Template rosterNew mit Form im Model
	 */
	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster/newRosterEntry/{week}")
	public String newRosterEntry(@PathVariable int week, Model model, RosterEntryForm form, Errors errors) {
		model.addAttribute("form", form);
		model.addAttribute("times", rosterManagement.getRosterByWeek(week).getRows());
		model.addAttribute("roles", RosterDataConverter.getRoleList());
		model.addAttribute(staffs, staffManagement.getAllStaffs());
		model.addAttribute("errors", errors);
		model.addAttribute("week", week);
		return "roster/rosterNew";
	}

	/**
	 * Online Auftrag zur Erstellung des neuen Eintrages
	 * Pfad: roster/newRosterEntry
	 * @param model Model der Seite
	 * @param form Ausgefuelltes Formular
	 * @param errors Fehler beim Erstellen
	 * @return Sollten Fehler aufgetreten sein, redirect zur Korrektur der Fehler. Wenn nicht redirect auf Seite
	 * des Dienstplans.
	 */
	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@PostMapping("/roster/newRosterEntry")
	public String createNewRosterEntry(Model model, @Valid @ModelAttribute("form") RosterEntryForm form, Errors errors) {
		rosterManagement.createEntry(form, -1, errors);
		if (errors.hasErrors()) {
			return newRosterEntry(form.getWeek(), model, form, errors);
		}
		return defaultLink + form.getWeek();
	}

	/**
	 * Zeigt Details eines Eintrags im Dientsplan. Nur fuer Staffs und Boss einsehbar.
	 * Pfad: /roster/detail/{week}/{shift}/{day}/{id}
	 * @param week Kalenderwoche des Eintrags
	 * @param shift Schichtnummer (nicht ID!) des Eintrags
	 * @param day Tagesnummer (Nicht SlotID!) des Eintrags
	 * @param id ID des Eintrages
	 * @param form Formular mit Eintraegen
	 * @param model Model der Seite
	 * @return HTML-Template rosterDetail mit Details des Eintrages
	 */
	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster/detail/{week}/{shift}/{day}/{id}")
	public String showDetail(@PathVariable int week, @PathVariable int shift,
							 @PathVariable int day,
							 @PathVariable long id, RosterEntryForm form, Model model) {
		model.addAttribute("day", day);
		model.addAttribute("shift", shift);
		model.addAttribute("row", rosterManagement.getRosterByWeek(week).getRows().get(shift));
		model.addAttribute("form", form);
		model.addAttribute("rosterEntry", rosterManagement.getRosterEntryById(week, shift, day, id));
		model.addAttribute("roles", RosterDataConverter.getRoleList());
		model.addAttribute("week", week);
		return "roster/rosterDetail";
	}

	/**
	 * Online Auftrag zur Bearbeitung eines Dienstplaneintrags. Pfad: /roster/editEntry/{id}
	 * @param form Formular mit enthaltenen Aenderungen
	 * @param errors Fehler beim Editieren des Eintrags
	 * @param id ID des Dienstplaneintrages
	 * @param model Model der Seite
	 * @return Redirect zu Dienstplan der Woche. Wenn Fehler redirect zum Bearbeiten
	 */
	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@PostMapping("/roster/editEntry/{id}")
	public String editEntry(@Valid @ModelAttribute("form") RosterEntryForm form, Errors errors,
							@PathVariable long id, Model model) {
		rosterManagement.editEntry(form, id, errors);
		if (errors.hasErrors()) {
			return showDetail(form.getWeek(),
				rosterManagement.getTimeIndex(form.getTimes().get(0)),
				form.getDay(), id, form, model);
		}
		return defaultLink + form.getWeek();
	}

	/**
	 * Loeschen eines Eintrags. Pfad: /roster/detail/delete/{week}/{shift}/{day}/{id}
	 * @param week Woche des entsprechenden Eintrags
	 * @param shift Schichtnummer (nicht ID!) des entsprechenden Eintrags
	 * @param day Tag (Slotnummer, nicht ID) des entsprechenden Eintrags
	 * @param id ID des Eintrags
	 * @return Egal ob Fehler oder nicht, redirect auf /roster/{week}
	 */
	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/roster/detail/delete/{week}/{shift}/{day}/{id}")
	public String delete(@PathVariable int week, @PathVariable int shift,
						 @PathVariable int day, @PathVariable long id) {
		rosterManagement.deleteEntry(week, shift, day, id);
		return defaultLink + week;
	}

	/**
	 *  Loescht einen Staff (Ist nur in diesem Package um Cycles zu vermeiden :P)
	 * @param id ID des zu loeschenden Staffs
	 * @param model Model der Seite
	 * @return redirect auf die /staffs (Uebersicht aller Staffs)
	 */
	@PreAuthorize("hasRole('BOSS') ")
	@PostMapping("/staff/delete/{id}")
	public String deleteStaff(@PathVariable long id, Model model) {
		rosterManagement.deleteAllEntriesFromStaff(id);
		staffManagement.removeStaff(id);
		return "redirect:/staffs";
	}


}








