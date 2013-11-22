package in.dart;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "content";
	private static final String TABLE_NAME = "markers";
	private static final String SOD = "sowing_date";
	private static final String SUD = "survey_date";
	private static final String STD = "stage_date";
	private static final String LAT = "latitude";
	private static final String LON = "longitude";

	public Database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
				+ " (id INTEGER PRIMARY KEY, " + SOD + " TEXT, " + SUD
				+ " TEXT, " + STD + " TEXT, " + LAT + " DOUBLE, " + LON
				+ " DOUBLE);";
		db.execSQL(CREATE_TABLE);
	}

	public void addData(String sowingDate, String surveyDate, String stageData,
			double latitude, double longitude) {
		SQLiteDatabase db = this.getWritableDatabase();

		String INSERT_DATA = "INSERT or replace INTO " + TABLE_NAME + "(" + SOD
				+ "," + SUD + "," + STD + "," + LAT + "," + LON + ")"
				+ " VALUES('" + sowingDate + "','" + surveyDate + "','"
				+ stageData + "'," + latitude + "," + longitude + ");";
		db.execSQL(INSERT_DATA);
		db.close();
	}

	public List<Markers> getAllData() {
		SQLiteDatabase db = this.getWritableDatabase();
		List<Markers> data = new ArrayList<Markers>();
		String getAllQuery = "SELECT  * FROM " + TABLE_NAME;
		Cursor cursor = db.rawQuery(getAllQuery, null);
		while (cursor.moveToNext()) {
			data.add(new Markers(cursor.getString(1), cursor.getString(2),
					cursor.getString(3),
					Double.parseDouble(cursor.getString(4)), Double
							.parseDouble(cursor.getString(5))));
		}
		cursor.close();
		return data;
	}

	public int getDataCount() {
		int count = 0;
		String countQuery = "SELECT  * FROM " + TABLE_NAME;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		count = cursor.getCount();
		cursor.close();
		return count;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	public void resetTables() {
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(TABLE_NAME, null, null);
		db.close();
	}

}
