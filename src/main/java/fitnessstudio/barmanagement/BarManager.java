package fitnessstudio.barmanagement;

import fitnessstudio.member.Member;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.MultiInventory;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.quantity.Quantity;
import org.springframework.data.util.Streamable;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.money.MonetaryAmount;
import java.time.LocalDate;

public class BarManager {

	private ExpiringInventory inventory;
	private BarCatalog catalog;
	private Cart cart;

	public BarManager(ExpiringInventory inventory, BarCatalog catalog, Cart cart) {

		Assert.notNull(inventory, "BarManager needs a non-null inventory");
		Assert.notNull(catalog, "BarManager needs a non-null catalog");
		Assert.notNull(cart, "BarManager needs a non-null cart");

		this.inventory = inventory;
		this.catalog = catalog;
		this.cart = cart;
	}


	public Streamable<ExpiringInventoryItem> getExipredItems(){
		return inventory.findByExpirationDateAfterOrderByExpirationDateAsc(LocalDate.now());
	}
	public boolean addArticleToCart(Article article, Quantity quantity){

		Quantity inventoryQuantity = inventory.findByProduct(article).getTotalQuantity();

		Quantity allreadyOrderedQuantity = cart.stream().filter(x -> x.getProduct()
			.equals(article)).findFirst().map(CartItem::getQuantity).orElse(Quantity.NONE);

		// add the desired or amount to the cart, or nothing if not enough items in stock
		Quantity addingQuantity = allreadyOrderedQuantity.add(quantity).isGreaterThan(inventoryQuantity) ? Quantity.NONE : quantity;

		cart.addOrUpdateItem(article, addingQuantity);

		return !addingQuantity.equals(Quantity.NONE);
	}
	public Iterable<Article> getAllArticles(){
		return catalog.findAll();
	}
	public Streamable<Article> getAvailableArticles(){
		throw new NotImplementedException();
	}
	public Streamable<CartItem> getCartItems(){
		throw new NotImplementedException();
	}
	public MonetaryAmount getCartPrice(){
		throw new NotImplementedException();
	}
	public void checkoutCart(Member customer, PaymentMethod paymentMethod){
		throw new NotImplementedException();
	}
	public void addNewArticleToCatalog(){
		throw new NotImplementedException();
	}
	public void removeArticleFromCatalog(){
		throw new NotImplementedException();
	}
	public void removeExpiredArticlesFromInventory(){
		throw new NotImplementedException();
	}
	public void restockInventory(Quantity quantity, Article article, LocalDate expirationDate){
		throw new NotImplementedException();
	}
	public Streamable<Article> getLowStockArticles(){
		throw new NotImplementedException();
	}
	public Quantity getArticleQuantity(Article article){
		throw new NotImplementedException();
	}
}
