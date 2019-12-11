package fitnessstudio.barmanagement;


import org.javamoney.moneta.Money;
import org.jetbrains.annotations.NotNull;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Objects;

@Controller
public class InventoryController {

	private static final Logger LOG = LoggerFactory.getLogger(CatalogDataInitializer.class);

	private static final String REDIRECT_CATALOG = "redirect:/catalog";
	private static final String ERROR = "error";
	private static final String STATUS = "status";

	private final DiscountRepository discountRepository;
	private final BarManager barManager;

	@Autowired
	ApplicationEventPublisher applicationEventPublisher;

	public InventoryController(UniqueInventory<UniqueInventoryItem> inventory, ArticleCatalog catalog,
							   DiscountRepository discountRepository, BarManager barManager) {

		this.discountRepository = discountRepository;
		this.barManager = barManager;
	}

	@PreAuthorize("hasRole('STAFF')")
	@GetMapping("/reorders")
	public String reorders(Model model) {
		model.addAttribute("reorders", barManager.getLowStockArticles());
		model.addAttribute("available", barManager.getLowStockArticles().iterator().hasNext());
		return "bar/reorders";
	}

//----------------------------------------Add article-------------------------------------------------------------------

	@PreAuthorize("hasRole('STAFF')")
	@GetMapping("/article")
	public String addArticle(Model model, CreateArticleForm form) {
		model.addAttribute("form", form);
		return "bar/add_article";
	}

	@PreAuthorize("hasRole('STAFF')")
	@PostMapping("/article")
	public String addArticle(@Valid CreateArticleForm form, Model model) throws DateTimeParseException {
		if (getError(form, model)) return ERROR;

		Discount discount = new Discount();
		discountRepository.save(discount);
		Article article = new Article(form.getName(),
			Money.of(new BigDecimal(form.getPrice()), "EUR"),
			form.getArt(),
			form.getDescription(), discount
			, Quantity.of(10));

		barManager.addNewArticleToCatalog(article);
		LocalDate expirationDate = passDate(form.getExpirationDate());
		Quantity initialQuantity = Quantity.of(Integer.parseInt(form.getAmount()));
		LOG.info(form.getAmount() + " was passed to " +String.valueOf(initialQuantity));
		barManager.restockInventory(initialQuantity, article, expirationDate);
		// inventory.save(new UniqueInventoryItem(article, Quantity.of(Integer.parseInt(form.getNumber()))));
		applicationEventPublisher.publishEvent(this);
		return REDIRECT_CATALOG;
	}

//----------------------------------------delete article----------------------------------------------------------------

	@PreAuthorize("hasRole('STAFF')")
	@PostMapping("/article/delete/{id}")
	public String removeArticle(@PathVariable ProductIdentifier id) {

		barManager.removeArticleFromCatalog(id);
		return REDIRECT_CATALOG;
	}

//----------------------------------------edit article-------------------------------------------------------------------

	@PreAuthorize("hasRole('STAFF')")
	@GetMapping("/article/detail/{id}")
	public String editArticle(@PathVariable ProductIdentifier id, Model model) {


		model.addAttribute("id", id);
		model.addAttribute("form", getArticleForm(barManager.getById(id)));

		return "bar/edit_article";
	}

	// post mapping for editing
	@PreAuthorize("hasRole('STAFF')")
	@PostMapping("/article/detail/{id}")
	public String editArticle(@PathVariable ProductIdentifier id, @Valid ArticleForm form, Model model) {
		if (getError(form, model)) return ERROR;

		barManager.editArticle(id, form.getName(), form.getArt(), form.getDescription(),
			Money.of(new BigDecimal(form.getPrice()), "EUR"),
			Quantity.of(Double.parseDouble(form.getSufficientQuantity())));

		applicationEventPublisher.publishEvent(this);
		return REDIRECT_CATALOG;
	}

	// for keeping previous value in input field
	@NotNull
	private ArticleForm getArticleForm(Article article) {

		return new ArticleForm() {
			@Override
			public @NotEmpty String getName() {
				return article.getName();
			}

			@Override
			public @NotEmpty String getArt() {
				return article.getArt();
			}

			@Override
			public @NotEmpty String getDescription() {
				return article.getDescription();
			}

			@Override
			public @NotEmpty @Digits(fraction = 2, integer = 5) String getPrice() {
				return String.valueOf(article.getPrice().getNumber());
			}


			@Override
			public @NotEmpty @Digits(fraction = 0, integer = 5) String getSufficientQuantity() {
				return String.valueOf(article.getSufficientQuantity().getAmount().intValue());
			}
		};
	}

	//-----------------------------------------------------------------------------------------------------------


	private boolean getError(@Valid ArticleForm form, Model model) {
		if (Integer.parseInt(form.getSufficientQuantity()) < 0) {
			model.addAttribute(ERROR, "Article should more than 0");
			model.addAttribute(STATUS, "400");
			return true;
		} else if (Double.parseDouble(form.getPrice()) < 0) {
			model.addAttribute(ERROR, "Price should more than 0 EUR");
			model.addAttribute(STATUS, "400");
			return true;
		}
		return false;
	}

	@GetMapping("/stock")
	@PreAuthorize("hasRole('STAFF')")
	public String stock(Model model) {

		model.addAttribute("stock", barManager.getAvailableArticles());

		return "/bar/stock";
	}

	private LocalDate passDate(String input) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
			return LocalDate.parse(input, formatter);

		} catch (DateTimeParseException e) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			return LocalDate.parse(input, formatter);
		}
	}


	// convert String input to Date
	/*private static class Date {
		private @Valid ArticleForm form;
		private LocalDate startDate;
		private LocalDate endDate;
		private LocalDate expirationDate;

		public Date(@Valid ArticleForm form) {
			this.form = form;
		}

		public LocalDate getStartDate() {
			return startDate;
		}

		public LocalDate getEndDate() {
			return endDate;
		}

		public LocalDate getExpirationDate() {
			return expirationDate;
		}

		public Date invoke() {

			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
				startDate = LocalDate.parse(form.getStartDiscount(), formatter);
				endDate = LocalDate.parse(form.getEndDiscount(), formatter);
				expirationDate = LocalDate.parse(form.getExpirationDate(), formatter);
				return this;

			} catch (DateTimeParseException e) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				startDate = LocalDate.parse(form.getStartDiscount(), formatter);
				endDate = LocalDate.parse(form.getEndDiscount(), formatter);
				expirationDate = LocalDate.parse(form.getExpirationDate(), formatter);
				return this;
			}

		}
	}*/
}
