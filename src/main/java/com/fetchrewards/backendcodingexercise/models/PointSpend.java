package com.fetchrewards.backendcodingexercise.models;

public class PointSpend {
	
	private int points;
	
	public PointSpend() {}
	
	public PointSpend(int points) {
		this.setPoints(points);
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}
}