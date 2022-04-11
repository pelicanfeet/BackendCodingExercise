package com.fetchrewards.backendcodingexercise.models;

import java.sql.Timestamp;

public class Transaction {
	
	private String payer;
	private int points;
	private String timestampString;
	private Timestamp timestamp;

	public Transaction() {}

	public Transaction(String payer, int points, Timestamp timestamp) {
		this.setPayer(payer);
		this.setPoints(points);
		this.setTimestamp(timestamp);
	}

	public String getPayer() {
		return payer;
	}

	public void setPayer(String payer) {
		this.payer = payer;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public String getTimestampString() {
		return timestampString;
	}

	public void setTimestampString(String timestampString) {
		this.timestampString = timestampString;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
}