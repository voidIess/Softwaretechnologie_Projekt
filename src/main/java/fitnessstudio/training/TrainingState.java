package fitnessstudio.training;

enum TrainingState {
	REQUESTED("ANGEFRAGT"), ACCEPTED("AKZEPTIERT"), DECLINED("ABGELEHNT"), ENDED("BEENDET");

	private final String displayValue;

	TrainingState(String displayValue) {
		this.displayValue = displayValue;
	}

	public String getDisplayValue() {
		return displayValue;
	}
}
