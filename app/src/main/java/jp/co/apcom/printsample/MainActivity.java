package jp.co.apcom.printsample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		PrintView print = (PrintView)findViewById(R.id.print);
		print.setAdapter(new MyPrintDocumentAdapter(this), PrintAttributes.MediaSize.JPN_HAGAKI);
		doPrint();
	}

	private void doPrint() {
		if(PrintHelper.systemSupportsPrint()) {
			PrintManager printManager = (PrintManager)getSystemService(PRINT_SERVICE);
			printManager.print("sampleJob", new MyPrintDocumentAdapter(this), null);
		} else {
			Toast.makeText(this, "この端末では印刷をサポートしていません", Toast.LENGTH_SHORT).show();
		}
	}

	static final float HAGAKI_ZIP_PADDING_RIGHT = 8;
	static final float HAGAKI_ZIP_PADDING_TOP = 12;

	static final float HAGAKI_ZIP_WIDTH = 5.7f;
	static final float HAGAKI_ZIP_HEIGHT = 8f;
	static final float HAGAKI_ZIP_GAP = 1.3f;
	static final float HAGAKI_ZIP_DIVIDER = 1.9f;

	private class MyPrintDocumentAdapter extends BasePrintDocumentAdapter {
		private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

		private final ToZipCode toZipCode = new ToZipCode();
		private final Text toAddress = new Text();
		private final Text toName = new Text();

		public MyPrintDocumentAdapter(Context context) {
			super(context);

			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.RED);
			paint.setTextSize(12);

			toAddress.setColor(Color.BLACK);
			toAddress.setSize(12);
			toAddress.setTypeface(Typeface.createFromAsset(getAssets(), "KouzanMouhituFontOTF.otf"));
			toAddress.setText("東京都千代田区神田一ノ十七ノ四");

			toName.setColor(Color.BLACK);
			toName.setSize(24);
			toName.setTypeface(Typeface.createFromAsset(getAssets(), "KouzanMouhituFontOTF.otf"));
			toName.setText("あいう　えお　様");
		}

		@Override
		protected int calcPageCount(PrintAttributes attrs) {
			return 1;
		}

		@Override
		protected void drawPage(int index, PdfDocument.PageInfo info, Canvas canvas) {
			toZipCode.draw(info, canvas);

			float middle = info.getPageWidth() - mm2pt(HAGAKI_ZIP_PADDING_RIGHT);
			float top = (int)(mm2pt(HAGAKI_ZIP_PADDING_TOP) * 3);
			toAddress.draw(middle, top, info, canvas);

			middle = info.getPageWidth() / 2;
			top = (int)(mm2pt(HAGAKI_ZIP_PADDING_TOP) * 3);
			toName.draw(middle, top, info, canvas);
		}
	}

	private void drawText(Canvas canvas, String text, RectF rect, Paint paint) {
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		float x = rect.left + (rect.width() - bounds.width()) / 2;
		float y = rect.top + (rect.height() - bounds.height()) / 2 + bounds.height();
		canvas.drawText(text, x, y, paint);
	}

	float mm2pt(float value) {
		// mm ---> point
		return value * 2.83465f;
	}

	private class ToZipCode {
		private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

		String zipCode = "1350063";

		public ToZipCode() {
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.RED);
			paint.setTextSize(12);
		}

		public void draw(PdfDocument.PageInfo info, Canvas canvas) {
			float paddingTop = mm2pt(HAGAKI_ZIP_PADDING_TOP);
			float paddingRight = mm2pt(HAGAKI_ZIP_PADDING_RIGHT);
			float zipWidth = mm2pt(HAGAKI_ZIP_WIDTH);
			float zipHeight = mm2pt(HAGAKI_ZIP_HEIGHT);
			float zipDivider = mm2pt(HAGAKI_ZIP_DIVIDER);
			float zipGap = mm2pt(HAGAKI_ZIP_GAP);

			float right = info.getPageWidth() - paddingRight;
			float left = right - zipWidth;
			float top = paddingTop;
			float bottom = paddingTop + zipHeight;
			RectF rect = new RectF(left, top, right, bottom);
			canvas.drawRect(rect, paint);

			int i = zipCode.length() - 1;
			String s = zipCode.substring(i, i + 1);
			drawText(canvas, s, rect, paint);

			rect.offsetTo(rect.left - zipGap - zipWidth, rect.top);
			canvas.drawRect(rect, paint);

			--i;
			s = zipCode.substring(i, i + 1);
			drawText(canvas, s, rect, paint);

			rect.offsetTo(rect.left - zipGap - zipWidth, rect.top);
			canvas.drawRect(rect, paint);

			--i;
			s = zipCode.substring(i, i + 1);
			drawText(canvas, s, rect, paint);

			rect.offsetTo(rect.left - zipGap - zipWidth, rect.top);
			canvas.drawRect(rect, paint);

			--i;
			s = zipCode.substring(i, i + 1);
			drawText(canvas, s, rect, paint);

			rect.offsetTo(rect.left - zipDivider - zipWidth, rect.top);
			canvas.drawRect(rect, paint);

			--i;
			s = zipCode.substring(i, i + 1);
			drawText(canvas, s, rect, paint);

			rect.offsetTo(rect.left - zipGap - zipWidth, rect.top);
			canvas.drawRect(rect, paint);

			--i;
			s = zipCode.substring(i, i + 1);
			drawText(canvas, s, rect, paint);

			rect.offsetTo(rect.left - zipGap - zipWidth, rect.top);
			canvas.drawRect(rect, paint);

			--i;
			s = zipCode.substring(i, i + 1);
			drawText(canvas, s, rect, paint);
		}
	}

	private class Text {
		private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

		private String text = null;

		public void setText(String text) {
			this.text = text;
		}

		public void setColor(int color) {
			paint.setColor(color);
		}

		public void setSize(float size) {
			paint.setTextSize(size);
		}

		public void setTypeface(Typeface typeface) {
			paint.setTypeface(typeface);
		}

		public void draw(float middle, float top, PdfDocument.PageInfo info, Canvas canvas) {
			float offset = 0;
			float x = 0;
			float y = top;
			float maxWidth = 0;
			Paint.FontMetrics metrics = paint.getFontMetrics();
			for(int i = 0; i < text.length(); ++ i) {
				String s = text.substring(i, i + 1);
				if(s.equals("\n")) {
					offset -= maxWidth;
					y = top;
				} else {
					float width = paint.measureText(s);
					maxWidth = Math.max(maxWidth, width);
					x = (int)(offset + middle - width / 2);
					canvas.drawText(s, x, y, paint);
					y += metrics.bottom - metrics.top;
				}
			}
		}
	}
}
