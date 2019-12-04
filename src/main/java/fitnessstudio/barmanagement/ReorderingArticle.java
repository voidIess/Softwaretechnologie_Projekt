package fitnessstudio.barmanagement;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ReorderingArticle {

	private @Id
	@GeneratedValue
	long id;
	private String name;
	private MonetaryAmount price;
	private String description;

	public ReorderingArticle() {

	}

	public ReorderingArticle(String name, MonetaryAmount price, String description) {
		this.name = name;
		this.price = price;
		this.description = description;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public MonetaryAmount getPrice() {
		return price;
	}

	public String getDescription() {
		return description;
	}
}
