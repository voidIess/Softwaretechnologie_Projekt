package fitnessstudio.bar;

import fitnessstudio.AbstractIntegrationTests;
import fitnessstudio.barmanagement.*;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BarIntegrationTest extends AbstractIntegrationTests {

	@Autowired
	MockMvc mvc;

	@Autowired
	ArticleCatalog catalog;

	@Autowired
	BarManager barManager;

	@Test
	void setNameArticleTest() {

		Article article = new Article("unnamed", Money.of(10, "EUR"), "type", "description",
				Quantity.of(10));
		article.setName("new name");
		assertNotNull(article.getName());
	}


	@Test
	void getPriceArticleTest() {
		Article article = new Article("unnamed", Money.of(10, "EUR"), "type", "description",
				Quantity.of(10));
		assertNotNull(article.getPrice());
	}

	@Test
	void setArtArticleTest() {
		Article article = new Article("unnamed", Money.of(10, "EUR"), "type", "description",
				Quantity.of(10));
		article.setType("new type");
		assertNotNull(article.getType());
	}

	@Test
	void setDescriptionArticleTest() {
		Article article = new Article("unnamed", Money.of(10, "EUR"), "type", "description",
				Quantity.of(10));
		article.setDescription("new description");
		assertNotNull(article.getDescription());
	}

	@Test
	void setSufficientQuantityArticleTest() {
		Article article = new Article("unnamed", Money.of(10, "EUR"), "type", "description",
				Quantity.of(10));
		article.setSufficientQuantity(Quantity.of(0));
		assertNotNull(article.getSufficientQuantity());
	}


	@Test
	void setDiscountArticleTest() {
		LocalDate startDate = LocalDate.of(2019, 12, 1);
		LocalDate endDate = LocalDate.of(2020, 6, 15);
		Discount discount = new Discount(startDate, endDate, 0);
		Article article = new Article("unnamed", Money.of(10, "EUR"), "type", "description",
				Quantity.of(10));
		article.setDiscount(discount);
		assertNotNull(article.getDiscount());
	}

	@Test
	void setStartDiscountTest() {
		LocalDate today = LocalDate.now();
		Discount discount = new Discount(null, null, 50);

		discount.setStartDate(today);
		assertNotNull(discount.getStartDate());
	}

	@Test
	void setEndDiscountTest() {
		LocalDate today = LocalDate.now();
		LocalDate endDate = LocalDate.of(2020, 6, 15);
		Discount discount = new Discount(today, endDate, 50);

		discount.setEndDate(endDate);
		assertNotNull(discount.getEndDate());
	}

	@Test
	void setPercentDiscountTest() {
		LocalDate today = LocalDate.now();
		LocalDate endDate = LocalDate.of(2020, 6, 15);
		Discount discount = new Discount(today, endDate, 100);

		discount.setPercent(0);
		assertThat(discount.getPercent());
	}

	@Test
	void getPriceWithDiscountTest() {

		LocalDate today = LocalDate.now();
		LocalDate endDate = LocalDate.of(2099, 12, 15);
		Discount discount = new Discount(today, endDate, 30);
		Article article = new Article("unnamed", Money.of(100, "EUR"), "type", "description",
				Quantity.of(10));
		article.setDiscount(discount);
		assertEquals(article.getPrice(), Money.of(70, "EUR"));
	}

	@Test
	void getDiscountStringTest() {

		LocalDate today = LocalDate.now();
		LocalDate endDate = LocalDate.of(2099, 12, 15);
		Discount discount = new Discount(today, endDate, 30);
		Article article = new Article("unnamed", Money.of(100, "EUR"), "type", "description",
				Quantity.of(10));
		assertEquals("", article.getDiscountString());
		article.setDiscount(discount);
		assertEquals("-30%", article.getDiscountString());

	}

	@Test
	void getExpireTest() {
		Article article = new Article("unnamed", Money.of(100, "EUR"), "type", "description",
				Quantity.of(10));
		LocalDate expireDate = LocalDate.of(2099, 12, 15);
		ExpiringInventoryItem expiringInventoryItem = new ExpiringInventoryItem(article, Quantity.of(50), expireDate);
		assertNotNull(expiringInventoryItem.getExpirationDate());
	}

	@Test
	void removeStockTest() {
		//	Article article = new Article("unnamed", Money.of(10, "EUR"), "type", "description",
		//		Quantity.of(10));
		// LocalDate expireDate = LocalDate.of(2099, 10, 13);
		// barManager.restockInventory(Quantity.of(100), article, expireDate);
		// barManager.removeStock(article.getId(), Quantity.of(30));
		// assertEquals(Quantity.of(70), barManager.getArticleQuantity(article));
	}

	@Test
	void detailCatalogController() throws Exception {
		ProductIdentifier id = catalog.findAll().iterator().next().getId();
		mvc.perform(get("/article/" + id).with(user("staff").roles("STAFF"))).andExpect(status().isOk())
				.andExpect(model().attributeExists("article")).andExpect(model().attributeExists("quantity"))
				.andExpect(model().attributeExists("orderable")).andExpect(model().size(3))
				.andExpect(view().name("bar/detail"));
	}

	@Test
	void catalogCatalogController() throws Exception {
		mvc.perform(get("/catalog").with(user("staff").roles("STAFF"))).andExpect(status().isOk())
				.andExpect(view().name("bar/catalog")).andExpect(model().attribute("catalog", catalog.findAll()))
				.andExpect(model().size(1));

	}

	@Test
	void stockInventoryController() throws Exception {

		//mvc.perform(get("/stock").with(user("staff").roles("STAFF"))).andExpect(status().isOk())
		//		.andExpect(model().attribute("stock", barManager.getAvailableArticles())).andExpect(view().name("bar/stock"));

		mvc.perform(get("/stock").with(user("staff").roles("STAFF"))).andExpect(status().isOk())
				.andExpect(view().name("bar/stock"));

	}

	@Test
	void getRestockArticleInventoryController() throws Exception {
		ProductIdentifier id = catalog.findAll().iterator().next().getId();
		mvc.perform(get("/article/restock/" + id).with(user("staff").roles("STAFF"))).andExpect(status().isOk())
				.andExpect(model().attributeExists("form")).andExpect(view().name("bar/restock_article"));

	}

	@Test
	void deleteInventoryController() throws Exception {
		ProductIdentifier id = catalog.findAll().iterator().next().getId();
		mvc.perform(post("/article/delete/" + id).with(user("staff").roles("STAFF")).with(csrf()))
				.andExpect(status().is(302)).andExpect(view().name("redirect:/catalog"));
	}

	@Test
	void getAddArticleInventoryController() throws Exception {
		mvc.perform(get("/article").with(user("staff").roles("STAFF"))).andExpect(status().isOk())
				.andExpect(model().attributeExists("form")).andExpect(view().name("bar/add_article"));

	}

	@Test
	void getEditArticleInventoryController() throws Exception {
		ProductIdentifier id = catalog.findAll().iterator().next().getId();
		mvc.perform(get("/article/detail/" + id).with(user("staff").roles("STAFF"))).andExpect(status().isOk())
				.andExpect(model().attributeExists("form")).andExpect(view().name("bar/edit_article"));

	}

	@Test
	void postEditArticleInventoryController() throws Exception {
		ProductIdentifier id = catalog.findAll().iterator().next().getId();
		mvc.perform(post("/article/detail/" + id + "?name=name&type=type&description=des&price=50&sufficientQuantity=50&percentDiscount=&startDiscount=&endDiscount=")
				.with(user("staff").roles("STAFF")).with(csrf())).andExpect(status().is(302))
				.andExpect(view().name("redirect:/catalog"));
	}

	@Test
	void postEditArticleErrorInventoryController() throws Exception {
		ProductIdentifier id = catalog.findAll().iterator().next().getId();
		mvc.perform(post("/article/detail/" + id + "?name=name&type=type&description=des&price=50&sufficientQuantity=-50&percentDiscount=&startDiscount=&endDiscount=")
				.with(user("staff").roles("STAFF")).with(csrf())).andExpect(status().is(200))
				.andExpect(view().name("error"));
	}

	@Test
	void postAddArticleErrorPriceInventoryController() throws Exception {
		mvc.perform(post("/article?name=name&type=type&description=des&price=-30&sufficientQuantity=50&percentDiscount=50&startDiscount=&endDiscount=&expirationDate=2050-10-03&amount=100")
				.with(user("staff").roles("STAFF")).with(csrf())).andExpect(status().is(200))
				.andExpect(view().name("error"));
	}

	@Test
	void postAddArticleErrorDiscountInventoryController() throws Exception {
		mvc.perform(post("/article?name=name&type=type&description=des&price=30&sufficientQuantity=50&percentDiscount=-50&startDiscount=&endDiscount=&expirationDate=2050-10-03&amount=100")
				.with(user("staff").roles("STAFF")).with(csrf())).andExpect(status().is(200))
				.andExpect(view().name("error"));
	}

	@Test
	void postAddArticleErrorQuantityInventoryController() throws Exception {
		mvc.perform(post("/article?name=name&type=type&description=des&price=30&sufficientQuantity=50&percentDiscount=50&startDiscount=209&endDiscount=&expirationDate=03.10.2069&amount=-100")
				.with(user("staff").roles("STAFF")).with(csrf())).andExpect(status().is(200))
				.andExpect(view().name("error"));
	}

	@Test
	void postAddArticleWithDate1InventoryController() throws Exception {
		mvc.perform(post("/article?name=name&type=type&description=des&price=50&sufficientQuantity=50&percentDiscount=&startDiscount=03.10.2018&endDiscount=03.10.2030&expirationDate=03.10.2050&amount=100")
				.with(user("staff").roles("STAFF")).with(csrf())).andExpect(status().is(302))
				.andExpect(view().name("redirect:/catalog"));
	}

	@Test
	void postAddArticleWithDate2InventoryController() throws Exception {
		mvc.perform(post("/article?name=name&type=type&description=des&price=50&sufficientQuantity=50&percentDiscount=50&startDiscount=2018-10-13&endDiscount=2030-12-23&expirationDate=2050-10-03&amount=100")
				.with(user("staff").roles("STAFF")).with(csrf())).andExpect(status().is(302))
				.andExpect(view().name("redirect:/catalog"));
	}

	@Test
	void postAddArticleWithDate3InventoryController() throws Exception {
		mvc.perform(post("/article?name=name&type=type&description=des&price=50&sufficientQuantity=50&percentDiscount=50&startDiscount=&endDiscount=&expirationDate=2050-10-03&amount=100")
				.with(user("staff").roles("STAFF")).with(csrf())).andExpect(status().is(302))
				.andExpect(view().name("redirect:/catalog"));
	}

	@Test
	void ordersBarController() throws Exception {

		mvc.perform(get("/orders").with(user("staff").roles("STAFF"))).andExpect(status().isOk())
				.andExpect(model().attributeExists("ordersCompleted")).andExpect(view().name("bar/orders"));

	}

	@Test
	void reordersInventoryController() throws Exception {

		mvc.perform(get("/reorders").with(user("staff").roles("STAFF"))).andExpect(status().isOk())
				.andExpect(model().attributeExists("reorders")).andExpect(view().name("bar/reorders"));

	}

	@Test
	void postReordersArticleInventoryController() throws Exception {
		ProductIdentifier id = catalog.findAll().iterator().next().getId();
		mvc.perform(post("/article/restock/" + id + "?expirationDate=2050-10-03&amount=100")
				.with(user("staff").roles("STAFF")).with(csrf())).andExpect(status().is(302))
				.andExpect(view().name("redirect:/catalog"));
	}

	@Test
	void cartItemsBarController() throws Exception {

		mvc.perform(get("/cart_items").with(user("staff").roles("STAFF"))).andExpect(status().isOk())
				.andExpect(view().name("bar/cart_items"));

	}

	@Test
	void getSellingCatalogBarController() throws Exception {
		mvc.perform(get("/sell_catalog").with(user("staff").roles("STAFF")).with(csrf()))
				.andExpect(status().is(200)).andExpect(view().name("bar/sell_catalog"));

	}

	@Test
	void getCheckoutBarController() throws Exception {
		mvc.perform(get("/checkout").with(user("staff").roles("STAFF")).with(csrf()))
				.andExpect(status().is(200)).andExpect(view().name("bar/checkout"));

	}

	@Test
	void postCheckoutBarController() throws Exception {
		mvc.perform(post("/checkout?customerId=2&paymentMethod=1").with(user("staff").roles("STAFF")).with(csrf()))
				.andExpect(status().is(302)).andExpect(view().name("redirect:/"));

	}

	@Test
	void AddItemBarController() throws Exception {
		ProductIdentifier pid = catalog.findAll().iterator().next().getId();
		mvc.perform(post("/addItemToCart?pid=" + pid + "&number=" + 1).with(user("staff").roles("STAFF")).with(csrf()))
				.andExpect(status().is(302)).andExpect(view().name("redirect:/sell_catalog"));
	}


}
