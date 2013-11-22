package in.dart;

public class Markers {
	private String sowingDate;
	private String surveyDate;
	private String cropStage;
	private double latitude;
	private double longitude;

	public Markers(String sowingDate, String surveyDate, String cropStage,
			double latitude, double longitude) {
		this.sowingDate = sowingDate;
		this.surveyDate = surveyDate;
		this.cropStage = cropStage;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getSowingDate() {
		return sowingDate;
	}

	public String getSurveyDate() {
		return surveyDate;
	}

	public String getCropStage() {
		return cropStage;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
}
