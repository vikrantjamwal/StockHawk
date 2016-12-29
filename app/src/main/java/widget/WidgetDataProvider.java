package widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract.Quote;

public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private Cursor cursor;
    private Intent intent;

    public WidgetDataProvider(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    private void initCursor() {
        if (cursor != null) {
            cursor.close();
        }
        final long identityToken = Binder.clearCallingIdentity();

        cursor = context.getContentResolver().query(Quote.URI,
                Quote.QUOTE_COLUMNS, null, null, null);
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onCreate() {
        initCursor();
        if (cursor != null) {
            cursor.moveToFirst();
        }
    }

    @Override
    public void onDataSetChanged() {
        initCursor();
    }

    @Override
    public void onDestroy() {
        cursor.close();
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.list_item_quote);
        cursor.moveToPosition(i);
        remoteViews.setTextViewText(R.id.symbol, cursor.getString(cursor.getColumnIndex(Quote.COLUMN_SYMBOL)));
        remoteViews.setTextViewText(R.id.price, cursor.getString(cursor.getColumnIndex(Quote.COLUMN_PRICE)));
        remoteViews.setTextViewText(R.id.change, cursor.getString(cursor.getColumnIndex(Quote.COLUMN_PERCENTAGE_CHANGE)) + "%");
        if (cursor.getFloat(Quote.POSITION_ABSOLUTE_CHANGE) > 0) {
            remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
        } else {
            remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
        }
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
