package fitnessstudio.barmanagement;

import org.salespointframework.quantity.Quantity;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.Optional;

@Entity
public class Article extends org.salespointframework.catalog.Product {
	private String art;
	private String description;
	private Quantity sufficientQuantity;
	//private Optional<Discount> discount;


	private Article(){}

	public Article(String name, MonetaryAmount price, String art, String description, LocalDate expirationDate) {
		super(name, price);
		this.art = art;
		this.description = description;
		//this.discount = Optional.empty();
	}

	public Quantity getSufficientQuantity() {
		return sufficientQuantity;
	}
}
