package fitnessstudio.barmanagement;

import fitnessstudio.member.Member;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.Cart;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.quantity.Quantity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.context.support.RequestHandledEvent;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Objects;

@Service
@Transactional
public class BarManager {

	private static final Logger LOG = LoggerFactory.getLogger(CatalogDataInitializer.class);

	private final ReorderingRepository reorderingRepository;
	private final UniqueInventory<UniqueInventoryItem> inventory;
	private final ArticleCatalog catalog;
	RequestHandledEvent requestHandledEvent;

	public BarManager(UniqueInventory<UniqueInventoryItem> inventory, ReorderingRepository reorderingRepository, ArticleCatalog catalog) {


		Assert.notNull(inventory, "BarManager needs a non-null inventory");
		Assert.notNull(catalog, "BarManager needs a non-null catalog");

		this.inventory = inventory;
		this.catalog = catalog;
		this.reorderingRepository = reorderingRepository;
	}

	// when start the Spring boot app
	@EventListener(ApplicationReadyEvent.class)
	public void start() {
		checkExpiringItems(requestHandledEvent);
		checkLowStock(requestHandledEvent);
		// TODO: check if any task need this event
	}

//------------------------------------for reordering Items--------------------------------------------------------------
    @EventListener
	public void checkExpiringItems(RequestHandledEvent e) {

		inventory.findAll().forEach(uniqueInventoryItem -> {

			Article article = (Article) uniqueInventoryItem.getProduct();
			LocalDate today = LocalDate.now();

			if (today.compareTo(article.getExpirationDate()) > 0) {
				ReorderingArticle reArticle = new ReorderingArticle(article.getName(), article.getPrice(),
						article.getDescription());
				reorderingRepository.save(reArticle);
				inventory.delete(uniqueInventoryItem);
				catalog.delete(article);
			}
		});

	}

	@EventListener
	public void checkLowStock(RequestHandledEvent e) {

		inventory.findItemsOutOfStock().forEach(uniqueInventoryItem -> {
			Article article = (Article) uniqueInventoryItem.getProduct();
			ReorderingArticle reArticle = new ReorderingArticle(article.getName(), article.getPrice(),
					article.getDescription());
			reorderingRepository.save(reArticle);
			inventory.delete(uniqueInventoryItem);
			catalog.delete(article);
		});

	}

	public Streamable<ReorderingArticle> getReorderingArticles() {
		return Streamable.of(reorderingRepository.findAll());
	}

	public void addReorderingArticle(ReorderingArticle reorderingArticle) {
		reorderingRepository.save(reorderingArticle);
	}

//----------------------------------------------------------------------------------------------------------------------

	/*
	public InventoryItems<ExpiringArticle> getExipredItems() {
		return inventory.findByExpirationDateAfterOrderByExpirationDateAsc(LocalDate.now());
	}
	 */

	/*
	public boolean addArticleToCart(Article article, Quantity quantity, Cart cart) {

		Quantity inventoryQuantity = inventory.findByProduct(article).getTotalQuantity();

		Quantity allreadyOrderedQuantity = cart.stream().filter(x -> x.getProduct()
				.equals(article)).findFirst().map(CartItem::getQuantity).orElse(Quantity.NONE);

		// add the desired or amount to the cart, or nothing if not enough items in stock
		Quantity addingQuantity = allreadyOrderedQuantity.add(quantity).isGreaterThan(inventoryQuantity) ? Quantity.NONE : quantity;

		cart.addOrUpdateItem(article, addingQuantity);

		return !addingQuantity.equals(Quantity.NONE);
	} */

	public Iterable<Article> getAllArticles() {
		return catalog.findAll();
	}


	// return articles, which are in stock and not expired
	public Streamable<UniqueInventoryItem> getAvailableArticles() {
		Streamable<UniqueInventoryItem> items = Streamable.of(catalog.findAll())
				.map(article -> new UniqueInventoryItem(article, getArticleQuantity(article)))
				.filter(x -> !x.getQuantity().isZeroOrNegative());

		LOG.info(items.map(InventoryItem::toString).toList().toString());
		return items;
	}

	public Iterable<UniqueInventoryItem> getAllItems(){
		return inventory.findAll();
	}


	public void checkoutCart(Member customer, PaymentMethod paymentMethod, Cart cart) {
		//TODO implement this
	}

	public void addNewArticleToCatalog(Article article) {
		catalog.save(article);
	}
	/*
	public void removeArticleFromCatalog(Article article) {
		// TODO check if this is necessary
		inventory.deleteAll(inventory.findByProduct(article));
		catalog.delete(article);
	}

	public void removeExpiredArticlesFromInventory() {
		inventory.deleteAll(this.getExipredItems());

	}

	public void restockInventory(Quantity quantity, Article article, LocalDate expirationDate) {
		ExpiringArticle item = inventory.findByProduct(article)
				.filter(x -> expirationDate.equals(x.getExpirationDate()))
				.iterator().next();

		item.increaseQuantity(quantity);
		inventory.save(item);
	}

	// dont need to write again Salepoints has this
	 public Streamable<Article> getLowStockArticles() {
		return Streamable.of(catalog.findAll()).filter(
				x -> inventory.findByProductAndExpirationDateAfter(x, LocalDate.now()).getTotalQuantity()
						.isLessThan(x.getSufficientQuantity()));
	}
*/

	public Quantity getArticleQuantity(Article article) {
		return inventory.findByProductIdentifier(Objects.requireNonNull(article.getId()))
				.map(InventoryItem::getQuantity)
				.orElse(Quantity.of(0));
		//return inventory.findByProductAndExpirationDateAfter(article, LocalDate.now()).getTotalQuantity();
	}



}
