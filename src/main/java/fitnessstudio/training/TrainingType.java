package fitnessstudio.training;

/**
 * Enum to represent {@link Training} type.
 *
 * @author Bill Kippe
 * @version 1.0
 */
enum TrainingType {
	NORMAL("Normal"), TRIAL("Probe");

	private final String displayValue;

	TrainingType(String displayValue) {
		this.displayValue = displayValue;
	}

	public String getDisplayValue() {
		return displayValue;
	}

}
