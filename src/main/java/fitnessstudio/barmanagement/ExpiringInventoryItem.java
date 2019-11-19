package fitnessstudio.barmanagement;

import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.MultiInventoryItem;
import org.salespointframework.quantity.Quantity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
public class ExpiringInventoryItem extends MultiInventoryItem {

	private LocalDate expirationDate;


	public ExpiringInventoryItem(Product product, Quantity quantity, LocalDate expiringItem) {
		super(product, quantity);
		this.expirationDate = expiringItem;
	}

	//unused
	protected ExpiringInventoryItem() {}

	private LocalDate getExpirationDate(){return expirationDate;}


}
