package fitnessstudio.barmanagement;

import org.javamoney.moneta.Money;
import org.jetbrains.annotations.NotNull;
import org.salespointframework.catalog.Product;

import org.salespointframework.quantity.Quantity;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.LocalDate;
import java.util.Optional;

@Entity
public class Article extends Product {

	private String type;
	private String description;
	// private LocalDate expirationDate;
	@OneToOne
	private Discount discount;
	private Quantity sufficientQuantity;
	//private Optional<Discount> discount;


	public Article() {
	}

	public Article(String name, MonetaryAmount price, String type, String description, Quantity sufficientQuantity) {
		super(name, price);
		this.type = type;
		this.description = description;
		this.sufficientQuantity = sufficientQuantity;
	}

	@NotNull
	@Override
	public MonetaryAmount getPrice() {
		if (hasDiscount()) {
			double deduction = super.getPrice().getNumber().longValue() * ((double) discount.getPercent() / 100);
			return Money.of(super.getPrice().getNumber().longValue() - deduction, "EUR");
		}
		else return super.getPrice();
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
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

	public Optional<Discount> getDiscount() {
		return Optional.ofNullable(discount);
	}

	public String getDiscountString() {
		return hasDiscount() ? discount.toString() : "";
	}

	public void setDiscount(Discount discount) {
		this.discount = discount;
	}

	private boolean hasDiscount() {
		Optional<Discount> optionalDiscount = getDiscount();
		if (optionalDiscount.isPresent()) {
			Discount discount = optionalDiscount.get();
			if (discount.isActive()) {
				return true;
			}
		}
		return false;
	}

}
