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

/**
 * represents an article in the bar
 */
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

	/**
	 * generates an Article which is the
	 *
	 * @param name               Name of the article
	 * @param price              Unreduced price of the article
	 * @param type               Category for our article
	 * @param description        Description of the article
	 * @param sufficientQuantity If the stock gets below this quantity,
	 *                           it will be shown as an article, which needs to be restock
	 */
	public Article(String name, MonetaryAmount price, String type, String description, Quantity sufficientQuantity) {
		super(name, price);
		this.type = type;
		this.description = description;
		this.sufficientQuantity = sufficientQuantity;
	}

	/**
	 * @return returns current price with the reductions already applied
	 * @see org.salespointframework.catalog.Product#getPrice()
	 */
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

	/**
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type set the type of article
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return description of article
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description set the description of article
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return sufficient quantity of article
	 */
	public Quantity getSufficientQuantity() {
		return sufficientQuantity;
	}

	/**
	 * @param sufficientQuantity sets the sufficientQuantity of article
	 */
	public void setSufficientQuantity(Quantity sufficientQuantity) {
		this.sufficientQuantity = sufficientQuantity;
	}

	/**
	 * @return OptionalDiscount
	 */
	public Optional<Discount> getOptDiscount() {
		return Optional.ofNullable(discount);
	}

	/**
	 * @return discount of the article
	 */
	public Discount getDiscount() {
		return discount;
	}

	/**
	 * @param discount sets the discount of article
	 */
	public void setDiscount(Discount discount) {
		this.discount = discount;
	}

	/**
	 * @return string of the discount
	 */
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
