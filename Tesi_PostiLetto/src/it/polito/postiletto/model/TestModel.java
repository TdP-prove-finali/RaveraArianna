package it.polito.postiletto.model;

import java.time.LocalDate;

public class TestModel {

	public static void main(String[] args) {
		Model m=new Model();
		m.creaPrevisioni("CARDIOLOGIA", 0.75);
		
		m.occupazioneReparto(LocalDate.of(2018, 06, 1), LocalDate.of(2018, 06, 30), "CARDIOLOGIA");
		System.out.println((LocalDate.of(2018, 06, 1)).getMonth());
		for(Row r:m.previsione(LocalDate.of(2018, 06, 1), LocalDate.of(2018, 06, 30), "CARDIOLOGIA"))
			System.out.println(r.getData()+" "+r.getDomanda()+" "+r.getPrevisione());
		
	}

}
