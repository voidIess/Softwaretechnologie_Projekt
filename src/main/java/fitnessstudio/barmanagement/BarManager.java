package fitnessstudio.barmanagement;

import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.InventoryItems;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.quantity.Quantity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.money.MonetaryAmount;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Objects;

@Service
@Transactional
public class BarManager {

	private static final Logger LOG = LoggerFactory.getLogger(CatalogDataInitializer.class);
	private ExpiringInventory inventory;
	private ArticleCatalog catalog;
	private DiscountRepository discountRepository;


	public BarManager(ExpiringInventory inventory, ArticleCatalog catalog, DiscountRepository discountRepository) {

		this.discountRepository = discountRepository;

		Assert.notNull(inventory, "BarManager needs a non-null inventory");
		Assert.notNull(catalog, "BarManager needs a non-null catalog");

		this.inventory = inventory;
		this.catalog = catalog;
	}

	public InventoryItems<ExpiringInventoryItem> getExipredItems() {
		return inventory.findByExpirationDateAfterOrderByExpirationDateAsc(LocalDate.now());
	}

	public void addArticleToCart(Article article, Quantity quantity, Cart cart) {

		Quantity inventoryQuantity = inventory.findByProduct(article).getTotalQuantity();

		Quantity allreadyOrderedQuantity = cart.stream().filter(x -> x.getProduct()
				.equals(article)).findFirst().map(CartItem::getQuantity).orElse(Quantity.NONE);

		// add the desired or amount to the cart, or nothing if not enough items in stock
		Quantity addingQuantity = allreadyOrderedQuantity.add(quantity).
				isGreaterThan(inventoryQuantity) ? Quantity.NONE : quantity;

		cart.addOrUpdateItem(article, addingQuantity);

	}

	public Iterable<Article> getAllArticles() {
		return catalog.findAll();
	}

	// return articles, which are in stock and not expired
	public Streamable<UniqueInventoryItem> getAvailableArticles() {
		Streamable<UniqueInventoryItem> items = Streamable.of(catalog.findAll())
				.map(x -> new UniqueInventoryItem(x, inventory.findByProductAndExpirationDateAfter(x, LocalDate.now())
						.getTotalQuantity()))
				.filter(x -> !x.getQuantity().isZeroOrNegative());

		LOG.info(items.map(InventoryItem::toString).toList().toString());
		return items;
	}

	public void addNewArticleToCatalog(Article article) {
		catalog.save(article);
	}

	public void removeArticleFromCatalog(ProductIdentifier id) {
		inventory.deleteAll(inventory.findByProductIdentifier(id));
		catalog.deleteById(id);
	}

	public void removeExpiredArticlesFromInventory() {
		inventory.deleteAll(this.getExipredItems());

	}


	public void restockInventory(Quantity quantity, Article article, LocalDate expirationDate) {
		if (!quantity.equals(Quantity.NONE)) {
			ExpiringInventoryItem item = inventory.findByProduct(article).stream()
					.filter(x -> expirationDate.equals(x.getExpirationDate())).findFirst()
					.orElse(new ExpiringInventoryItem(article, Quantity.NONE, expirationDate));

			item.increaseQuantity(quantity);
			inventory.save(item);
		}
	}

	public Streamable<Article> getLowStockArticles() {
		return Streamable.of(catalog.findAll()).filter(
				article -> article.getSufficientQuantity().isGreaterThanOrEqualTo(
					inventory.findByProductAndExpirationDateAfter(article, LocalDate.now()).getTotalQuantity()));
	}

	public Quantity getArticleQuantity(Article article) {
		return inventory.findByProductAndExpirationDateAfter(article, LocalDate.now()).getTotalQuantity();
	}

	Article getById(ProductIdentifier id) {
		return catalog.findById(id).orElse(new Article());
	}

	public void editArticle(ProductIdentifier id, String name, String type, String description,
							MonetaryAmount price, Quantity sufficientQuantity, LocalDate startDate, int percent,
							LocalDate endDate) {
		catalog.findAll().forEach(article -> {
			if (Objects.equals(article.getId(), id)) {

				article.setName(name);
				article.setType(type);
				article.setDescription(description);
				article.setPrice(price);
				article.setSufficientQuantity(sufficientQuantity);
				discountRepository.delete(discountRepository.findAll().iterator().next());
				Discount discount = new Discount(startDate, endDate, percent);
				discountRepository.save(discount);
				article.setDiscount(discount);
				catalog.save(article);
			}

		});

	}

	public boolean stockAvailable(ProductIdentifier id, Quantity quantity) {
		Article article = getById(id);
		Quantity stockQuantity = getArticleQuantity(article);

		return !stockQuantity.isLessThan(quantity);
	}

	// this will remove non-expired articles from the inventory
	public void removeStock(ProductIdentifier id, Quantity quantity) {
		// abort if there isnt enough Quantity anyway
		if (!stockAvailable(id, quantity)) {
			return;
		}

		Article article = getById(id);
		InventoryItems<ExpiringInventoryItem> orderedItems = inventory.
				findByProductAndExpirationDateAfterOrderByExpirationDateAsc(article, LocalDate.now());
		for (ExpiringInventoryItem item : orderedItems) {
			Quantity itemQuantity = item.getQuantity();
			if (itemQuantity.isLessThan(quantity)) {
				quantity.subtract(itemQuantity);
				inventory.delete(item);
			} else {
				if (item.getQuantity().isEqualTo(quantity)) {
					inventory.delete(item);
				} else {
					item.decreaseQuantity(quantity);
					inventory.save(item);
				}
				break;
			}
		}
	}

}
