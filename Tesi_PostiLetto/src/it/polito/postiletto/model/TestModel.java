package it.polito.postiletto.model;

import java.time.LocalDate;

public class TestModel {

	public static void main(String[] args) {
		Model m=new Model();
		m.creaPrevisioni("CARDIOLOGIA", 0.75);
		
		System.out.println(m.previsione(LocalDate.of(2018, 07, 1), LocalDate.of(2018, 07, 30), "CARDIOLOGIA"));
		
	}

}
