package fitnessstudio.bar;

import fitnessstudio.AbstractIntegrationTests;
import fitnessstudio.barmanagement.Article;
import fitnessstudio.barmanagement.ArticleCatalog;
import fitnessstudio.barmanagement.Discount;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BarIntegrationTest extends AbstractIntegrationTests {

	@Autowired
	MockMvc mvc;

	@Autowired
	ArticleCatalog catalog;

	@Autowired
	UniqueInventory<UniqueInventoryItem> inventory;

	@Test
	void setNameArticleTest() {

		LocalDate startDate = LocalDate.of(2019, 12, 1);
		LocalDate endDate = LocalDate.of(2020, 6, 15);
		LocalDate expireDate = LocalDate.of(2030, 10, 3);
		Discount discount = new Discount(startDate, endDate, 0);
		Article article = new Article("unnamed", Money.of(10, "EUR"), "type", "description", expireDate, discount);

		article.setName("new name");
		assertNotNull(article.getName());
	}


	@Test
	void getPriceArticleTest() {

		LocalDate startDate = LocalDate.of(2019, 12, 1);
		LocalDate endDate = LocalDate.of(2020, 6, 15);
		LocalDate expireDate = LocalDate.of(2030, 10, 3);
		Discount discount = new Discount(startDate, endDate, 0);
		Article article = new Article("unnamed", Money.of(10, "EUR"), "type", "description", expireDate, discount);

		assertNotNull(article.getPrice());
	}

	@Test
	void setArtArticleTest() {
		LocalDate startDate = LocalDate.of(2019, 12, 1);
		LocalDate endDate = LocalDate.of(2020, 6, 15);
		LocalDate expireDate = LocalDate.of(2030, 10, 3);
		Discount discount = new Discount(startDate, endDate, 0);
		Article article = new Article("unnamed", Money.of(10, "EUR"), "type", "description", expireDate, discount);

		article.setType("new type");
		assertNotNull(article.getType());
	}

	@Test
	void setDescriptionArticleTest() {
		LocalDate startDate = LocalDate.of(2019, 12, 1);
		LocalDate endDate = LocalDate.of(2020, 6, 15);
		LocalDate expireDate = LocalDate.of(2030, 10, 3);
		Discount discount = new Discount(startDate, endDate, 0);
		Article article = new Article("unnamed", Money.of(10, "EUR"), "type", "description", expireDate, discount);

		article.setDescription("new description");
		assertNotNull(article.getDescription());
	}

	@Test
	void setExpireArticleTest() {

		LocalDate startDate = LocalDate.of(2019, 12, 1);
		LocalDate endDate = LocalDate.of(2020, 6, 15);
		LocalDate today = LocalDate.now();
		LocalDate expireDate = LocalDate.of(2030, 10, 3);
		Discount discount = new Discount(startDate, endDate, 0);

		Article article = new Article("unnamed", Money.of(10, "EUR"), "type", "description", today, discount);
		article.setExpirationDate(expireDate);
		assertNotNull(article.getExpirationDate());
	}

	@Test
	void setDiscountArticleTest() {
		LocalDate startDate = LocalDate.of(2019, 12, 1);
		LocalDate endDate = LocalDate.of(2020, 6, 15);
		LocalDate expireDate = LocalDate.of(2030, 10, 3);
		Discount discount = new Discount(startDate, endDate, 0);
		Article article = new Article("unnamed", Money.of(10, "EUR"), "type", "description", expireDate, null);

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
		LocalDate expireDate = LocalDate.of(2030, 10, 3);
		Discount discount = new Discount(today, endDate, 30);
		Article article = new Article("unnamed", Money.of(100, "EUR"), "type", "description", expireDate, discount);
		assertEquals(article.getPrice(), Money.of(70, "EUR"));
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
		mvc.perform(get("/stock").with(user("staff").roles("STAFF"))).andExpect(status().isOk())
				.andExpect(model().attribute("stock", inventory.findAll())).andExpect(view().name("bar/stock"));

	}

}
