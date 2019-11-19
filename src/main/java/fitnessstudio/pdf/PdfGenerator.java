package fitnessstudio.pdf;

import com.itextpdf.layout.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface PdfGenerator {

	public static Document generatePdf(Map<String, Object> map, Document d) throws IOException {

		if(map.get("type").equals("payslip")){
			return PayslipPdfGenerator.generatePdf(map,d);
		} else {
			//invoice
			return null;
		}

	}

	static String getGermanMonth(int num) {

		if(num < 1 || num > 12) {
			throw new IllegalArgumentException();
		}

		String[] months = {"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"};
		return months[num-1];
	}
}
