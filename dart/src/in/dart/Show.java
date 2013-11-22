package in.dart;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class Show extends Activity {

	private Database db;
	private ExpandableListView ev;
	private ExpandableListAdapter ea;
	private XmlSerializer serializer;
	private StringWriter writer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show);
		db = new Database(this);

		ev = (ExpandableListView) findViewById(R.id.data);

		try {
			if (db.getDataCount() != 0) {
				ea = new ListAdapter(db.getAllData(), db.getDataCount());
				for (int position = 1; position <= ea.getGroupCount(); position++)
					ev.expandGroup(position - 1);
			} else {
				ea = new ListAdapter(db.getDataCount());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		ev.setAdapter(ea);

		setupActionBar();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setDisplayShowHomeEnabled(false);
		}
	}

	public class ListAdapter extends BaseExpandableListAdapter {

		public int totalData;
		public LayoutInflater inflater;
		public Context context;
		public List<Markers> data;

		public ListAdapter(List<Markers> data, int totalData) {
			this.data = data;
			this.totalData = totalData;
			this.inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public ListAdapter(int totalData) {
			this.totalData = totalData;
			this.inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public View getChildView(int groupPosition, final int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView lat, lon, sod, sud, std;

			if (convertView == null) {
				convertView = inflater
						.inflate(R.layout.app_i_list_status, null);
			}

			sod = (TextView) convertView.findViewById(R.id.sod);
			sod.setText(data.get(childPosition).getSowingDate());
			sud = (TextView) convertView.findViewById(R.id.sud);
			sud.setText(data.get(childPosition).getSurveyDate());
			std = (TextView) convertView.findViewById(R.id.std);
			std.setText(data.get(childPosition).getCropStage());
			lat = (TextView) convertView.findViewById(R.id.lat);
			lat.setText(data.get(childPosition).getLatitude() + "");
			lon = (TextView) convertView.findViewById(R.id.lon);
			lon.setText(data.get(childPosition).getLongitude() + "");

			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return data.size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		@Override
		public int getGroupCount() {
			return 1;
		}

		@Override
		public void onGroupCollapsed(int groupPosition) {
			super.onGroupCollapsed(groupPosition);
		}

		@Override
		public void onGroupExpanded(int groupPosition) {
			super.onGroupExpanded(groupPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater
						.inflate(R.layout.app_o_list_status, null);
			}

			TextView header = (TextView) convertView
					.findViewById(R.id.n_header);
			header.setText("Total Data");
			TextView totalCounter = (TextView) convertView
					.findViewById(R.id.n_count);
			totalCounter.setText(totalData + "");
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.show, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.show:
			exportData();
			break;
		}
		return false;
	}

	public void exportData() {
		List<Markers> data = db.getAllData();
		serializer = Xml.newSerializer();
		writer = new StringWriter();
		if (db.getDataCount() != 0) {
			try {
				serializer.setOutput(writer);
				serializer.startDocument("UTF-8", true);
				serializer.startTag("", "MARKERS");

				for (Markers marker : data) {
					serializer.startTag("", "MARKER");
					serializer.startTag("", "SOWING_DATE");
					serializer.text(marker.getSowingDate());
					serializer.endTag("", "SOWING_DATE");
					serializer.startTag("", "SURVEY_DATE");
					serializer.text(marker.getSurveyDate());
					serializer.endTag("", "SURVEY_DATE");
					serializer.startTag("", "CROP_STAGE");
					serializer.text(marker.getCropStage());
					serializer.endTag("", "CROP_STAGE");
					serializer.startTag("", "LATITUDE");
					serializer.text(marker.getLatitude() + "");
					serializer.endTag("", "LATITUDE");
					serializer.startTag("", "LONGITUDE");
					serializer.text(marker.getLongitude() + "");
					serializer.endTag("", "LONGITUDE");
					serializer.endTag("", "MARKER");
				}

				serializer.endTag("", "MARKERS");
				serializer.endDocument();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				writeToFile(writer.toString());
			}
		}
	}

	public boolean writeToFile(String text) {
		DataOutputStream out;
		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsoluteFile(), getFileName());
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			out = new DataOutputStream(fos);
			out.writeBytes(text);
			out.close();
			return true;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String getFileName() {
		Calendar now = Calendar.getInstance();
		String name = now.get(Calendar.DATE) + "_" + now.get(Calendar.MONTH)
				+ "_" + now.get(Calendar.YEAR) + "_" + now.get(Calendar.HOUR)
				+ "_" + now.get(Calendar.MINUTE) + "_"
				+ now.get(Calendar.SECOND);
		return "Crop Data" + name + ".xml";
	}
}
