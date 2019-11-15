package fitnessstudio.pdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;

import java.io.IOException;
import java.util.Map;

public interface PdfGenerator {

	static Document generatePdf(Map<String, Object> map, Document d) throws IOException {

		if(map.get("type").equals("payslip")){
			return PayslipPdfGenerator.generatePdf(map,d);
		} else {
			//invoice
			return null;
		}

	}
}
