package jp.co.apcom.printsample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.print.PrintAttributes;
import android.util.AttributeSet;
import android.view.View;

public class PrintView extends View {
	private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Matrix matrix = new Matrix();

	private PdfDocument.PageInfo info;
	private BasePrintDocumentAdapter adapter = null;

	public PrintView(Context context) {
		this(context, null);
	}

	public PrintView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint.setColor(Color.WHITE);

	}

	public BasePrintDocumentAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(BasePrintDocumentAdapter adapter, PrintAttributes.MediaSize size) {
		this.adapter = adapter;
		info = new PdfDocument.PageInfo.Builder(size.getWidthMils() * 72 / 1000, size.getHeightMils() * 72 / 1000, 1).create();
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		BasePrintDocumentAdapter adapter = getAdapter();
		if(adapter == null) return;

		float width = getWidth();
		float height = getHeight();
		float pageWidth = info.getPageWidth();
		float pageHeight = info.getPageHeight();
		float s = Math.min(width / pageWidth, height / pageHeight);

		float x = (width - pageWidth * s) / 2;
		float y = (height - pageHeight * s) / 2;
		matrix.reset();
		matrix.postScale(s, s);
		matrix.postTranslate(x, y);

		canvas.save();
		canvas.concat(matrix);
		canvas.drawRect(0, 0, pageWidth, pageHeight, paint);
		adapter.drawPage(1, info, canvas);
		canvas.restore();
	}
}
