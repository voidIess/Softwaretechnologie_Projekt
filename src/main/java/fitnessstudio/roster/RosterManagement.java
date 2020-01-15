package fitnessstudio.roster;

import com.mysema.commons.lang.Assert;
import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffManagement;
import fitnessstudio.staff.StaffRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Klasse zum managen des Dienstplans
 */
@Service
public class RosterManagement {

	private final RosterRepository rosterRepository;
	private final StaffManagement staffManagement;
	private static final Logger LOG = LoggerFactory.getLogger(RosterManagement.class);

	RosterManagement(RosterRepository rosterRepository, StaffManagement staffs) {
		Assert.notNull(rosterRepository, "Das RosterRepository darf nicht null sein!");
		Assert.notNull(staffs, "Das StaffManagement darf nicht null sein!");
		this.staffManagement = staffs;
		this.rosterRepository = rosterRepository;
	}

	/**
	 * Loescht alle Eintraege aus allen Dienstplaenen, die mit dem Staff mit der StaffId in Verbindung stehen
	 * @param staffId ID des Staffs, dessen Eintraege geloescht werden sollen.
	 */
	public void deleteAllEntriesFromStaff(long staffId) {
		Staff staff = staffManagement.findById(staffId).orElse(null);
		if (staff == null) {
			return;
		}
		for (Roster roster : rosterRepository.findAll()) {
			for (TableRow row : roster.getRows()) {
				for (Slot slot : row.getSlots()) {
					deleteByStaff(slot, staffId);
				}
			}
			saveRoster(roster);
		}
	}

	private void deleteByStaff (Slot slot, long staffId){
		List<Integer> positions = new ArrayList<>();
		int i = 0;
		for (RosterEntry rosterEntry : slot.getEntries()) {
			if (rosterEntry.getStaff().getStaffId() == staffId) {
				positions.add(i);
			}
			i++;
		}
		for (int index : positions) {
			slot.getEntries().remove(index);
		}
	}

	private boolean checkData(Staff staff, Roster roster, RosterEntryForm form, Errors errors) {
		boolean noError =true;
		if (staff == null) {
			errors.rejectValue("roster.error.staff", "Dieser Staff existiert nicht!");
			noError = false;
		}

		if (roster == null) {
			errors.rejectValue("week", "Für diese Woche gibt es keinen Roster.");
			noError = false;
		}

		if (form.getTimes().isEmpty()) {
			errors.reject("time", "Bitte wähle mindestens eine Zeit aus!");
			noError = false ;
		}
		return noError;
	}

	/**
	 * Erstellt einen neuen Dienstplaneintrag
	 * @param form Formular fuer einen neuen Eintrag von einem Mitarbeiter/Chef
	 * @param training Sollte fuer den Eintrag ein Training vorgemerkt sein, wird die ID hier uebergeben.
	 *                    Sollte kein Training vorhanden wird RosterEntry.NONE uebergeben. (Ja ich weiß auch nicht
	 *                 warum ich nicht einfach ein Long genutzt habe und null uebergeben lasse)
	 * @param errors Zur Verifizierung des Formulars
	 */
	public void createEntry(RosterEntryForm form, long training, Errors errors) {
		Assert.notNull(form, "RosterForm darf nicht 'null' sein.");
		Assert.notNull(form.getTimes(), "Keine Liste gefunden.");
		Assert.notNull(form.getWeek(), "Keine Woche angegeben!");

		Staff staff = staffManagement.findById(form.getStaff()).orElse(null);
		Roster roster = getRosterByWeek(form.getWeek());
		StaffRole role = RosterDataConverter.stringToRole(form.getRole());
		int day = form.getDay();

		if (!checkData(staff, roster, form, errors )){
			return;
		}

		form.getTimes().forEach(time -> IntStream.range(0,Roster.AMOUNT_ROWS).forEach(i-> {
			if(roster.getRows().get(i).toString().equals(time)){
				try{
					RosterEntry rosterEntry = new RosterEntry(role, staff);
					roster.addEntry(i, day, rosterEntry);
					setTraining(training, role, rosterEntry);
				} catch (Exception e) {
					errors.reject("times", "Der Mitarbeiter arbeitet von " + time + " schon.");
				}
			}
		}));

		saveRoster(roster);
	}

	private void setTraining(long training, StaffRole role, RosterEntry rosterEntry) {
		if (isTraining(training, role)) {
			rosterEntry.setTraining(training);
		}
	}


	private boolean isTraining(long training, StaffRole role){
		return training != RosterEntry.NONE && role.equals(StaffRole.TRAINER);
	}

	/**
	 * Gibt einen RosterEntry anhand seiner ID zurueck.
	 * @param week Kalenderwoche des Dienstplans
	 * @param shift (y-Koordinate im Dienstplan), Nummer der Schicht (TableRow) (nicht die ID!)
	 * @param day (x-Koordinate im Dienstplan), Nummer des Tages (Slot) (0...6) (auch hier nicht die ID!)
	 * @param id ID des gesuchten Dienstplaneintrages
	 * @return RosterEntry mit der uebergegeben ID
	 */
	public RosterEntry getRosterEntryById(int week, int shift, int day, long id) {
		Roster roster = getRosterByWeek(week);
		Assert.notNull(roster, "Es gibt keinen Dienstplan für diese Woche!");
		Assert.isTrue(shift >= 0 && shift < Roster.AMOUNT_ROWS && day >= 0 && day < 7, "Dieser Slot existiert nicht!");
		for (RosterEntry rosterEntry : roster.getRows().get(shift).getSlots().get(day).getEntries()) {
			if (rosterEntry.getRosterEntryId() == id){
				return rosterEntry;
			}
		}
		return null;
	}

	/**
	 * Erzeugt eine Liste aller vorhandenen Dienstplaene
	 * @return Eine Liste aus Kalenderwochen Nummern
	 */
	public List<Integer> getNextWeeks() {
		Iterable<Roster> rosters = rosterRepository.findAll();
		List<Integer> weeks = new LinkedList<>();
		for (Roster r : rosters) {
			weeks.add(r.getWeek());
		}
		return weeks;
	}

	/**
	 * Speichert alle Aenderungen an einem Dienstplan
	 * @param roster Dienstplan der gespeichert werden soll
	 */
	public void saveRoster(Roster roster) {
		rosterRepository.save(roster);
	}

	/**
	 * Gibt einen Dienstplan anhand seiner Woche zurueck
	 * @param week Kalenderwochen Nummer
	 * @return Dienstplan anhand einer Kalenderwoche
	 */
	public Roster getRosterByWeek(int week) {
		return rosterRepository.findByWeek(week).orElse(null);
	}

	/**
	 * Bearbeitet einen existierenden Dienstplaneintrag
	 * @param form Formular mit den Aenderungen des Eintrags
	 * @param id Id des existierenden Dienstplaneintrags
	 * @param errors Verifizierung der Aenderungen
	 */
	public void editEntry(RosterEntryForm form, long id, Errors errors) {
		Roster roster = getRosterByWeek(form.getWeek());
		RosterEntry entry;
		Assert.notNull(roster, "Es gibt keinen Dienstplan für diese Woche!");
		String time = form.getTimes().iterator().next();
		for (int i = 0; i < roster.getRows().size(); i++) {
			if (roster.getRows().get(i).toString().equals(time)) {
				try {
					entry = getRosterEntryById(form.getWeek(), i, form.getDay(), id);
					entry.setRole(RosterDataConverter.stringToRole(form.getRole()));

				} catch (Exception e) {
					errors.reject("Edit", "Der Mitarbeiter hat zu dieser Zeit einen Termin.");
				}
			}
		}

		saveRoster(roster);
	}

	/**
	 * Loescht Eintrag mit Training aus dem Dienstplan
	 * @param week Woche aus der das Training stammt.
	 * @param shift Schicht des Eintrags mit Training
	 * @param day Slot des Eintrags mit Training
	 * @param id ID des Eintrags
	 */
	public void deleteEntryByTraining(int week, int shift, int day, long id) {
		Roster roster = getRosterByWeek(week);
		Assert.notNull(roster, "Keinen Roster gefunden.");
		List<RosterEntry> entries = roster.getRows().get(shift).getSlots().get(day).getEntries();
		for (RosterEntry entry : entries) {
			if (entry.getTraining() == id) {
				entries.remove(entry);
				break;
			}
		}
		saveRoster(roster);
	}

	/**
	 * Loescht einen Eintrag anhand seiner ID aus dem Dienstplan
	 * @param week Woche des Eintrags
	 * @param shift Schicht des Eintrags
	 * @param day Tag des Eintrags
	 * @param id ID des Eintrags
	 */
	public void deleteEntry(int week, int shift, int day, long id) {
		Roster roster = getRosterByWeek(week);
		Assert.notNull(roster, "Es gibt keinen Roster für diese Woche!");
		RosterEntry rosterEntry = getRosterEntryById(week, shift, day, id);
		Assert.notNull(rosterEntry, "Keinen RosterEntry gefunden.");
		try {
			roster.deleteEntry(shift, day, id);
		} catch (Exception ignore) {
			LOG.info("Eintrag konnte nicht gelöscht werden.");
		}
		saveRoster(roster);
	}

	/**
	 * Erzeugt eine Liste Strings. Diese Strings sind der Form hh:mm-hh:mm (fuer die erste Spalte)
	 * @return List an Zeiten fuer die erste Spalte im Dienstplan
	 */
	public List<String> getTimes() {
		Roster roster = rosterRepository.findAll().iterator().next();
		if (roster == null) {
			return new ArrayList<>();
		}
		List<String> times = new ArrayList<>();
		for (TableRow row : roster.getRows()) {
			times.add(row.toString());
		}
		return times;
	}

	/**
	 * Findet den Index eines Strings der form hh:mm-hh:mm aus den TableRows heraus.
	 * @param time String mit die Eintraege von getTimes() verglichen werden.
	 * @return Sollte der String ungleich aller Strings der in getTimes erzeugten Strings sein,
	 * so wird -1 returned. Sonst die Nummer (nicht ID!) der Reihe
	 */
	public int getTimeIndex(String time) {
		List<String> times = getTimes();
		for (int i = 0; i < getTimes().size(); i++) {
			if (times.get(i).equals(time)){
				return i;
			}
		}
		return -1;
	}

	/**
	 * Ueberprueft ob ein Mitarbeiter zu einer bestimmten Zeit noch verfuegbar ist.
	 * @param form Formular aus dem ein neuer Eintrag entstehen soll, sollte es diesen "Test" bestehen.
	 * @return true wenn der Mitarbeiter noch nicht arbeitet, false
	 * wenn der Mitarbeiter zu dieser Zeit bereits verplant ist
	 */
	public boolean isFree(RosterEntryForm form) {
		int shift = getTimeIndex(form.getTimes().get(0));
		return !getRosterByWeek(form.getWeek()).getRows().get(shift).getSlots()
				.get(form.getDay()).isTaken(staffManagement.findById(form.getStaff())
				.orElse(null));
	}
}
