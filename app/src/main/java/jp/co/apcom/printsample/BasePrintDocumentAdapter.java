package jp.co.apcom.printsample;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;

import java.io.FileOutputStream;
import java.io.IOException;

public abstract class BasePrintDocumentAdapter extends PrintDocumentAdapter {
	private final Context context;

	private int count = 0;
	private PrintedPdfDocument doc = null;

	public BasePrintDocumentAdapter(Context context) {
		this.context = context;
	}

	@Override
	public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, final LayoutResultCallback callback, Bundle extras) {
		cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
			@Override
			public void onCancel() {
				callback.onLayoutCancelled();
			}
		});

		int pageCount = calcPageCount(newAttributes);
		doc = new PrintedPdfDocument(context, newAttributes);
		PrintDocumentInfo info = new PrintDocumentInfo.Builder(context.getApplicationInfo().loadLabel(context.getPackageManager()).toString() + ".pdf")
				.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
				.setPageCount(pageCount)
				.build();

		callback.onLayoutFinished(info, pageCount != count);
		count = pageCount;
	}

	@Override
	public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, final WriteResultCallback callback) {
		cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
			@Override
			public void onCancel() {
				doc.close();
				doc = null;
				callback.onWriteCancelled();
			}
		});

		for(int i = 0; i < count; ++ i) {
			if(pageRangesContainPage(i, pages)) {
				PdfDocument.Page page = doc.startPage(i);
				drawPage(i, page.getInfo(), page.getCanvas());
				doc.finishPage(page);
			}
		}

		try {
			doc.writeTo(new FileOutputStream(destination.getFileDescriptor()));
			callback.onWriteFinished(pages);
		} catch(IOException e) {
			callback.onWriteFailed(e.toString());
		} finally {
			doc.close();
			doc = null;
		}
	}


	private boolean pageRangesContainPage(int index, PageRange[] ranges) {
		for(PageRange range : ranges) {
			if(index >= range.getStart() && index <= range.getEnd()) {
				return true;
			}
		}
		return false;
	}

	protected abstract int calcPageCount(PrintAttributes attrs);
	protected abstract void drawPage(int index, PdfDocument.PageInfo info, Canvas canvas);
}
