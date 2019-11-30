package fitnessstudio.barmanagement;

import fitnessstudio.member.Member;
import org.salespointframework.inventory.InventoryItems;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.quantity.Quantity;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDate;

@Service
public class BarManager {

	private ExpiringInventory inventory;
	private ArticleCatalog catalog;

	public BarManager(ExpiringInventory inventory, ArticleCatalog catalog) {

		Assert.notNull(inventory, "BarManager needs a non-null inventory");
		Assert.notNull(catalog, "BarManager needs a non-null catalog");

		this.inventory = inventory;
		this.catalog = catalog;
	}

	public InventoryItems<ExpiringInventoryItem> getExipredItems() {
		return inventory.findByExpirationDateAfterOrderByExpirationDateAsc(LocalDate.now());
	}

	public boolean addArticleToCart(Article article, Quantity quantity, Cart cart) {

		Quantity inventoryQuantity = inventory.findByProduct(article).getTotalQuantity();

		Quantity allreadyOrderedQuantity = cart.stream().filter(x -> x.getProduct()
			.equals(article)).findFirst().map(CartItem::getQuantity).orElse(Quantity.NONE);

		// add the desired or amount to the cart, or nothing if not enough items in stock
		Quantity addingQuantity = allreadyOrderedQuantity.add(quantity).isGreaterThan(inventoryQuantity) ? Quantity.NONE : quantity;

		cart.addOrUpdateItem(article, addingQuantity);

		return !addingQuantity.equals(Quantity.NONE);
	}

	public Iterable<Article> getAllArticles() {
		return catalog.findAll();
	}

	// return articles, which are in stock and not expired
	public Streamable<UniqueInventoryItem> getAvailableArticles() {
		return Streamable.of(catalog.findAll())
			.map(x -> new UniqueInventoryItem(x, inventory.findByProductAndExpirationDateBefore(x, LocalDate.now()).getTotalQuantity()))
			.filter(x -> !x.getQuantity().isZeroOrNegative());
	}


	public void checkoutCart(Member customer, PaymentMethod paymentMethod) {
		//TODO implement this
	}

	public void addNewArticleToCatalog(Article article) {
		catalog.save(article);
	}

	public void removeArticleFromCatalog(Article article) {
		// TODO check if this is necessary
		inventory.deleteAll(inventory.findByProduct(article));
		catalog.delete(article);
	}

	public void removeExpiredArticlesFromInventory() {
		inventory.deleteAll(this.getExipredItems());

	}

	public void restockInventory(Quantity quantity, Article article, LocalDate expirationDate) {
		ExpiringInventoryItem item = inventory.findByProduct(article)
			.filter(x -> expirationDate.equals(x.getExpirationDate()))
			.iterator().next();

		item.increaseQuantity(quantity);
		inventory.save(item);
	}

	public Streamable<Article> getLowStockArticles() {
		return Streamable.of(catalog.findAll()).filter(
			x -> inventory.findByProductAndExpirationDateBefore(x, LocalDate.now()).getTotalQuantity()
				.isLessThan(x.getSufficientQuantity()));
	}

	public Quantity getArticleQuantity(Article article) {
		return inventory.findByProductAndExpirationDateBefore(article, LocalDate.now()).getTotalQuantity();
	}
}
