package fitnessstudio.barmanagement;

import org.javamoney.moneta.Money;
import org.jetbrains.annotations.NotNull;
import org.salespointframework.catalog.Product;

import org.salespointframework.quantity.Quantity;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.LocalDate;

@Entity
public class Article extends Product {

	private String art;
	private String description;
	// private LocalDate expirationDate;
	@OneToOne
	private Discount discount;
	private Quantity sufficientQuantity;
	//private Optional<Discount> discount;


	public Article() {
	}

	public Article(String name, MonetaryAmount price, String art, String description, Discount discount, Quantity sufficientQuantity) {
		super(name, price);
		this.art = art;
		this.description = description;
		this.discount = discount;
		this.sufficientQuantity = sufficientQuantity;
	}

	@NotNull
	@Override
	public MonetaryAmount getPrice() {
		LocalDate today = LocalDate.now();
		if (discount.getPercent() == 0 || today.compareTo(discount.getEndDate()) > 0 ||
			discount.getStartDate().compareTo(today) > 0) {
			return super.getPrice();
		} else {
			double deduction = super.getPrice().getNumber().longValue() * ((double) discount.getPercent() / 100);
			return Money.of(super.getPrice().getNumber().longValue() - deduction, "EUR");
		}
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

	public Quantity getSufficientQuantity() {
		return sufficientQuantity;
	}

	public void setSufficientQuantity(Quantity sufficientQuantity) {
		this.sufficientQuantity = sufficientQuantity;
	}

	public Discount getDiscount() {
		return discount;
	}

	public void setDiscount(Discount discount) {
		this.discount = discount;
	}


}
