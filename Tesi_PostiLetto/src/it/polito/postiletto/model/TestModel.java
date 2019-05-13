package it.polito.postiletto.model;

import java.time.LocalDate;

public class TestModel {

	public static void main(String[] args) {
		Model m=new Model();
		
		m.occupazioneReparto(LocalDate.of(2018, 06, 1), LocalDate.of(2018, 06, 30), "CARDIOLOGIA");
	}

}
