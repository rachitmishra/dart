package in.dart;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Home extends FragmentActivity {

	private boolean exit = false;
	private GoogleMap map;
	private EditText dateSowing;
	private EditText dateSurvey;
	private Spinner spinner;
	private ImageButton mapType;
	private Database db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		mapType = (ImageButton) findViewById(R.id.mapType);
		try {
			map = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			map.setMyLocationEnabled(true);
			MapsInitializer.initialize(this);
		} catch (GooglePlayServicesNotAvailableException e) {
			Log.w("ceeq-dev", "Google Play Services not present on device");
		} catch (InflateException e) {
			Log.w("ceeq-dev", "OpenGL not supported on device.");
		} catch (NullPointerException e) {
			Log.w("ceeq-dev", "Google Play Services not present on device");
		}
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(28.6377,
				77.1571), 11.0f));
		map.setOnMapClickListener(new MapClick());
		mapType.setOnClickListener(new MapType());
		db = new Database(this);
		db.getWritableDatabase();
	}

	public class MapType implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			switch (map.getMapType()) {
			case GoogleMap.MAP_TYPE_NORMAL:
				map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
				break;
			case GoogleMap.MAP_TYPE_TERRAIN:
				map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				break;
			case GoogleMap.MAP_TYPE_SATELLITE:
				map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				break;
			}

		}
	}

	public class MapClick implements OnMapClickListener {

		@Override
		public void onMapClick(LatLng location) {
			map.addMarker(new MarkerOptions().position(location));
			map.setOnMarkerClickListener(new MarkerClick());
		}
	}

	public class MarkerClick implements OnMarkerClickListener,
			DatePickerDialog.OnDateSetListener {
		int textToUpdate;

		@Override
		public boolean onMarkerClick(final Marker clicked) {
			AlertDialog.Builder alert = new AlertDialog.Builder(Home.this);
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(R.layout.dialog, null);
			Button save = (Button) v.findViewById(R.id.save);
			dateSowing = (EditText) v.findViewById(R.id.dateSowing);
			dateSurvey = (EditText) v.findViewById(R.id.dateSurvey);
			spinner = (Spinner) v.findViewById(R.id.spinner);
			ArrayAdapter<CharSequence> adapter = ArrayAdapter
					.createFromResource(Home.this, R.array.options,
							android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
						int pos, long id) {
					parent.setTag(R.string.spinner_value, parent
							.getItemAtPosition(pos).toString());
				}

				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
			alert.setView(v);
			final AlertDialog dialog = alert.create();
			dialog.show();

			save.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (dateSowing.length() != 0 & dateSurvey.length() != 0) {
						db.addData(dateSowing.getText().toString(), dateSurvey
								.getText().toString(), (String) spinner
								.getTag(R.string.spinner_value), clicked
								.getPosition().latitude,
								clicked.getPosition().longitude);
						Toast.makeText(Home.this, "Data saved.",
								Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					} else {
						Toast.makeText(Home.this, "Please, fill all fields.",
								Toast.LENGTH_SHORT).show();
					}
				}
			});

			dateSowing.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					textToUpdate = 1;
					Calendar c = new GregorianCalendar();
					int year = c.get(Calendar.YEAR);
					int month = c.get(Calendar.MONTH);
					int day = c.get(Calendar.DAY_OF_MONTH);
					new DatePickerDialog(Home.this, Home.MarkerClick.this,
							year, month, day).show();
				}
			});

			dateSurvey.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					textToUpdate = 2;
					Calendar c = Calendar.getInstance();
					int year = c.get(Calendar.YEAR);
					int month = c.get(Calendar.MONTH);
					int day = c.get(Calendar.DAY_OF_MONTH);
					new DatePickerDialog(Home.this, Home.MarkerClick.this,
							year, month, day).show();
				}
			});
			return false;

		}

		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {
			switch (textToUpdate) {
			case 1:
				dateSowing.setText(day + "-" + (month + 1) + "-" + year);
				break;
			case 2:
				dateSurvey.setText(day + "-" + (month + 1) + "-" + year);
				break;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		AlertDialog.Builder builder;
		LayoutInflater inflater = this.getLayoutInflater();
		builder = new AlertDialog.Builder(this);
		switch (item.getItemId()) {

		case R.id.view:
			startActivity(new Intent(this, Show.class));
			break;

		case R.id.about:
			builder.setView(inflater.inflate(R.layout.support, null)).create()
					.show();
			break;

		case R.id.exit:
			this.finish();
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		if (exit)
			Home.this.finish();
		else {
			Toast.makeText(this, "Press Back again to Exit.",
					Toast.LENGTH_SHORT).show();
			exit = true;
		}

	}

}
