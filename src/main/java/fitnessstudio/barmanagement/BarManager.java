package fitnessstudio.barmanagement;

import fitnessstudio.member.Member;
import org.aspectj.weaver.ast.Not;
import org.salespointframework.inventory.MultiInventory;
import org.salespointframework.order.CartItem;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.quantity.Quantity;
import org.springframework.data.util.Streamable;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.money.MonetaryAmount;
import java.time.LocalDate;

public class BarManager {

	MultiInventory inventory;
	BarCatalog catalog;

	public Streamable<MultiInventory> getExpiredItems(){
		throw new NotImplementedException();
	}
	public boolean addArticleToCart(){
		throw new NotImplementedException();
	}
	public Streamable<Article> getAllArticles(){
		throw new NotImplementedException();
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
