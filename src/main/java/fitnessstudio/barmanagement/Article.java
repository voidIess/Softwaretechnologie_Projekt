package fitnessstudio.barmanagement;

import org.salespointframework.catalog.Product;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.LocalDate;

@Entity
public class Article extends Product {

	private String art;
	private String description;
	private LocalDate expirationDate;
	@OneToOne
	private Discount discount;

	public Article() {
	}

	public Article(String name, MonetaryAmount price, String art, String description, LocalDate expirationDate,
				   Discount discount) {
		super(name, price);
		this.art = art;
		this.description = description;
		this.expirationDate = expirationDate;
		this.discount = discount;

	}

	public String getArt() {
		return art;
	}

	public void setArt(String art) {
		this.art = art;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(LocalDate expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Discount getDiscount() {
		return discount;
	}

	public void setDiscount(Discount discount) {
		this.discount = discount;
	}


}
