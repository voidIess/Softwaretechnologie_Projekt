package fitnessstudio.member;

/**
 * Form for user input to find a {@link Member} by the given text.
 *
 * @author Bill Kippe
 * @version 1.0
 */
public class SearchForm {
	private String search;

	/**
	 * Creates a new {@link SearchForm} instance with the given search text.
	 * @param search (start of) ID of member(s) to find
	 */
	public SearchForm(String search){
		this.search = search;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}
}
