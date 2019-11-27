package fitnessstudio.training;

enum  TrainingType {
	NORMAL("Normal"), TRIAL("Probe");

	private final String displayValue;

	TrainingType(String displayValue){
		this.displayValue = displayValue;
	}

	public String getDisplayValue() {
		return displayValue;
	}

}
