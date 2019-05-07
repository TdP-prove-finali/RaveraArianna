package it.polito.postiletto.model;

import java.time.LocalDate;

public class Dati {
	
	LocalDate accettazione;
	LocalDate dimissione;
	String reparto;
	
	public Dati(LocalDate accettazione, LocalDate dimissione, String reparto) {
		this.accettazione = accettazione;
		this.dimissione = dimissione;
		this.reparto = reparto;
	}
	
	public LocalDate getAccettazione() {
		return accettazione;
	}
	public void setAccettazione(LocalDate accettazione) {
		this.accettazione = accettazione;
	}
	public LocalDate getDimissione() {
		return dimissione;
	}
	public void setDimissione(LocalDate dimissione) {
		this.dimissione = dimissione;
	}
	public String getReparto() {
		return reparto;
	}
	public void setReparto(String reparto) {
		this.reparto = reparto;
	}
	

}
