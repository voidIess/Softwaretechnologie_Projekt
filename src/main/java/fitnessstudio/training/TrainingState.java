package fitnessstudio.training;

/**
 * Enum to represent current {@link Training} state.
 *
 * @author Bill Kippe
 * @version 1.0
 */
enum TrainingState {
	REQUESTED("ANGEFRAGT"), ACCEPTED("AKZEPTIERT"),
	DECLINED("ABGELEHNT"), ENDED("BEENDET");

	private final String displayValue;

	TrainingState(String displayValue) {
		this.displayValue = displayValue;
	}

	public String getDisplayValue() {
		return displayValue;
	}
}
