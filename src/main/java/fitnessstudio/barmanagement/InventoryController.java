package fitnessstudio.barmanagement;


import org.javamoney.moneta.Money;
import org.jetbrains.annotations.NotNull;
import org.salespointframework.catalog.ProductIdentifier;
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

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
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
	private static final int MAX_PERCENT_DISCOUNT = 100;

	private final BarManager barManager;
	private final DiscountRepository discountRepository;

	@Autowired
	private
	ApplicationEventPublisher applicationEventPublisher;

	public InventoryController(DiscountRepository discountRepository, BarManager barManager) {

		this.barManager = barManager;
		this.discountRepository = discountRepository;
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
	@Transactional
	public String addArticle(@Valid CreateArticleForm form, Model model) throws DateTimeParseException {
		if (getError((ArticleForm) form, model)) {
			return ERROR;
		}
		if (getError((QuantityForm) form, model)) {
			return ERROR;
		}

		Date date = new Date(form).invoke();
		LocalDate startDate = date.getStartDate();
		LocalDate endDate = date.getEndDate();


		Article article = new Article(form.getName(),
				Money.of(new BigDecimal(form.getPrice()), "EUR"),
				form.getType(),
				form.getDescription(),
				Quantity.of(Double.parseDouble(form.getSufficientQuantity())));

		String percent = form.getPercentDiscount();
		if (percent.isBlank()) {
			percent = "0";
		}

		Discount discount = new Discount(startDate, endDate, Integer.parseInt(percent));
		article.setDiscount(discount);
		discountRepository.save(discount);

		barManager.addNewArticleToCatalog(article);
		LocalDate expirationDate = passDate(form.getExpirationDate());
		Quantity initialQuantity = Quantity.of(Integer.parseInt(form.getAmount()));
		barManager.restockInventory(initialQuantity, article, expirationDate);
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
	//-------------------------------------restockArticle---------------------------------------------------------------

	@PreAuthorize("hasRole('STAFF')")
	@GetMapping("/article/restock/{id}")
	public String restockArticle(@PathVariable ProductIdentifier id, QuantityForm form, Model model) {

		model.addAttribute("id", id);
		model.addAttribute("form", form);

		return "bar/restock_article";
	}

	@PreAuthorize("hasRole('STAFF')")
	@PostMapping("/article/restock/{id}")
	public String restockArticlePost(@PathVariable ProductIdentifier id, @Valid QuantityForm form, Model model) {
		if (getError(form, model)) {
			return ERROR;
		}
		Article article = barManager.getById(id);

		LocalDate expirationDate = passDate(form.getExpirationDate());
		Quantity initialQuantity = Quantity.of(Integer.parseInt(form.getAmount()));
		barManager.restockInventory(initialQuantity, article, expirationDate);
		applicationEventPublisher.publishEvent(this);
		return REDIRECT_CATALOG;
	}

//----------------------------------------edit article-----------------------------------------------------------------

	@PreAuthorize("hasRole('STAFF')")
	@GetMapping("/article/detail/{id}")
	public String editArticle(@PathVariable ProductIdentifier id, Model model) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.GERMANY);

		model.addAttribute("id", id);
		model.addAttribute("form", getArticleForm(barManager.getById(id), formatter));

		return "bar/edit_article";
	}

	// post mapping for editing
	@PreAuthorize("hasRole('STAFF')")
	@PostMapping("/article/detail/{id}")
	public String editArticle(@PathVariable ProductIdentifier id, @Valid ArticleForm form, Model model) {
		if (getError(form, model)) {
			return ERROR;
		}

		String percent = form.getPercentDiscount();
		if (percent.equals("")) {
			percent = "0";
		}
		Date date = new Date(form).invoke();
		LocalDate startDate = date.getStartDate();
		LocalDate endDate = date.getEndDate();


		barManager.editArticle(id, form.getName(), form.getType(), form.getDescription(),
				Money.of(new BigDecimal(form.getPrice()), "EUR"),
				Quantity.of(Double.parseDouble(form.getSufficientQuantity())),
				startDate, Integer.parseInt(percent), endDate);


		applicationEventPublisher.publishEvent(this);
		return REDIRECT_CATALOG;
	}

	// for keeping previous value in input field
	@NotNull
	private ArticleForm getArticleForm(Article article, DateTimeFormatter formatter) {

		return new ArticleForm() {
			@Override
			public @NotEmpty String getName() {
				return article.getName();
			}

			@Override
			public @NotEmpty String getType() {
				return article.getType();
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
			public String getPercentDiscount() {
				return String.valueOf(article.getDiscount().getPercent());
			}

			@Override
			public String getStartDiscount() {
				if (article.getDiscount().getStartDate().format(formatter).equals("2000-01-01")) {
					return null;
				}
				return Objects.requireNonNull(article.getDiscount().getStartDate().format(formatter));
			}

			@Override
			public String getEndDiscount() {
				if (article.getDiscount().getEndDate().format(formatter).equals("2099-01-01")) {
					return null;
				}
				return Objects.requireNonNull(article.getDiscount().getEndDate().format(formatter));
			}

			@Override
			public @NotEmpty @Digits(fraction = 0, integer = 5) String getSufficientQuantity() {
				return String.valueOf(article.getSufficientQuantity().getAmount().intValue());
			}
		};
	}

	//-----------------------------------------------------------------------------------------------------------


	private boolean getError(@Valid ArticleForm form, Model model) {
		String percent = form.getPercentDiscount();
		if (percent.isBlank()) {
			percent = "0";
		}
		if (Integer.parseInt(form.getSufficientQuantity()) < 0 || Double.parseDouble(form.getPrice()) < 0) {
			model.addAttribute(ERROR, "Article should more than 0 or Price should more than 0 EUR");
			model.addAttribute(STATUS, "400");
			return true;

		} else if (Integer.parseInt(percent) < 0 || Integer.parseInt(percent) > MAX_PERCENT_DISCOUNT) {
			model.addAttribute(ERROR, "Discount should in 0-100 percent");
			model.addAttribute(STATUS, "400");
			return true;
		}
		return false;
	}

	private boolean getError(@Valid QuantityForm form, Model model) {
		if (Integer.parseInt(form.getAmount()) <= 0) {
			model.addAttribute(ERROR, "more than 0 should be added");
			model.addAttribute(STATUS, "400");
			return true;
		}
		return false;
	}

	@GetMapping("/stock")
	@PreAuthorize("hasRole('STAFF')")
	public String stock(Model model) {

		model.addAttribute("stock", barManager.getAvailableArticles());

		return "bar/stock";
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
	private static class Date {
		private @Valid ArticleForm articleForm;

		private LocalDate startDate;
		private LocalDate endDate;

		public Date(@Valid ArticleForm allform) {
			this.articleForm = allform;
		}

		public LocalDate getStartDate() {
			return startDate;
		}

		public LocalDate getEndDate() {
			return endDate;
		}


		public Date invoke() {

			String formStartDiscount = articleForm.getStartDiscount();
			String formEndDiscount = articleForm.getEndDiscount();

			try {
				if (formStartDiscount.equals("")) {
					formStartDiscount = "2000-01-01";
				}
				if (formEndDiscount.equals("")) {
					formEndDiscount = "2099-01-01";
				}
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				startDate = LocalDate.parse(formStartDiscount, formatter);
				endDate = LocalDate.parse(formEndDiscount, formatter);
				return this;

			} catch (DateTimeParseException e) {

				if (formStartDiscount.equals("")) {
					formStartDiscount = "01.01.2000";
				}
				if (formEndDiscount.equals("")) {
					formEndDiscount = "01.01.2099";
				}

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
				startDate = LocalDate.parse(formStartDiscount, formatter);
				endDate = LocalDate.parse(formEndDiscount, formatter);
				return this;
			}

		}
	}

}
