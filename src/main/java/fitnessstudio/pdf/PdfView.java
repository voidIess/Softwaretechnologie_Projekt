package fitnessstudio.pdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @see AbstractView#renderMergedOutputModel(Map, HttpServletRequest, HttpServletResponse)
 */
@Component("pdfView")
public class PdfView extends AbstractView {

	@Override
	protected void renderMergedOutputModel(Map<String, Object> map, @NotNull HttpServletRequest httpServletRequest,
										   @NotNull HttpServletResponse httpServletResponse) throws Exception {

		if(map.get("type") == "payslip"){
			httpServletResponse.setHeader("Content-Disposition", "attachment; filename=Gehaltsabrechnung.pdf");
		} else {
			httpServletResponse.setHeader("Content-Disposition", "attachment; filename=Rechnung.pdf");
		}

		PdfWriter pdfWriter = new PdfWriter(httpServletResponse.getOutputStream());
		PdfDocument pdf = new PdfDocument(pdfWriter);
		Document document = new Document(pdf);

		document = PdfGenerator.generatePdf(map, document);
		document.close();

	}
}
