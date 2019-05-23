package it.polito.postiletto.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;

public class PieChartController {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private PieChart pie;

	private ObservableList<Data> dati;

	@FXML
	void initialize() {
		assert pie != null : "fx:id=\"pie\" was not injected: check your FXML file 'PieChart.fxml'.";

	}

	public void setDataForPieChart(ObservableList<Data> dati, String titolo) {
		this.dati = dati;
		this.pie.setData(this.dati);
		this.pie.setTitle(titolo);

	}
}
