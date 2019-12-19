package fitnessstudio.barmanagement;

import org.javamoney.moneta.Money;
import org.jetbrains.annotations.NotNull;
import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Quantity;

import javax.money.MonetaryAmount;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import java.util.Optional;

@Entity
public class Article extends Product {

	private String type;
	private String description;
	// private LocalDate expirationDate;
	@OneToOne(targetEntity = Discount.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
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
		} else {
			return super.getPrice();
		}

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

	public Optional<Discount> getOptDiscount() {
		return Optional.ofNullable(discount);
	}

	public Discount getDiscount() {
		return discount;
	}

	public void setDiscount(Discount discount) {
		this.discount = discount;
	}

	public String getDiscountString() {
		return hasDiscount() ? discount.toString() : "";
	}

	private boolean hasDiscount() {
		Optional<Discount> optionalDiscount = getOptDiscount();
		if (optionalDiscount.isPresent()) {
			Discount discount = optionalDiscount.get();
			return discount.isActive();
		}
		return false;
	}

}