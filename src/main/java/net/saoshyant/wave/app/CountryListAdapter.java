package net.saoshyant.wave.app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

import net.saoshyant.wave.R;
import net.saoshyant.wave.R.drawable;

public class CountryListAdapter extends BaseAdapter {

	List<Country> countries;
	LayoutInflater inflater;

	private int getResId(String drawableName) {

		try {
			Class<drawable> res = R.drawable.class;
			Field field = res.getField(drawableName);
			return field.getInt(null);
		} catch (Exception e) {
			Log.e("CountryCodePicker", "Failure to get drawable id.", e);
		}
		return -1;
	} 
	
	public CountryListAdapter(Context context, List<Country> countries) {
		super();
		this.countries = countries;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return countries.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View cellView = convertView;
		Cell cell;
		Country country = countries.get(position);

		if (convertView == null) {
			cell = new Cell();
			cellView = inflater.inflate(R.layout.row, null);
			cell.textView = (TextView) cellView.findViewById(R.id.row_title);
			cell.imageView = (ImageView) cellView.findViewById(R.id.row_icon);
			cellView.setTag(cell);
		} else {
			cell = (Cell) cellView.getTag();
		}

		cell.textView.setText(country.getName());
 
		String drawableName = "flag_"
				+ country.getCode().toLowerCase(Locale.ENGLISH);
		cell.imageView.setImageResource(getResId(drawableName));
		return cellView;
	}
 
	static class Cell {
		public TextView textView;
		public ImageView imageView;
	}

}