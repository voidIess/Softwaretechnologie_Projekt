package fitnessstudio.barmanagement;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.Optional;

@Entity
public class Article extends org.salespointframework.catalog.Product {
	private String art;
	private String description;
	private LocalDate expirationDate;
	//private Optional<Discount> discount;


	private Article(){}

	public Article(String name, MonetaryAmount price, String art, String description, LocalDate expirationDate) {
		super(name, price);
		this.art = art;
		this.description = description;
		this.expirationDate = expirationDate;
		//this.discount = Optional.empty();
	}



}
