package it.polito.postiletto.model;

import java.time.LocalDate;

public class Row2 {
	private LocalDate data;
	private double previsione;
	private int domanda;
	
	public Row2(LocalDate data, double previsione, int domanda) {
		this.data = data;
		this.previsione = previsione;
		this.domanda = domanda;
	}
	
	public LocalDate getData() {
		return data;
	}
	public void setData(LocalDate data) {
		this.data = data;
	}
	public double getPrevisione() {
		return previsione;
	}
	public void setPrevisione(double previsione) {
		this.previsione = previsione;
	}
	public int getDomanda() {
		return domanda;
	}
	public void setDomanda(int domanda) {
		this.domanda = domanda;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Row2 other = (Row2) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}
	
	

}
