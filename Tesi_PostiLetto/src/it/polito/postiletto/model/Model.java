package it.polito.postiletto.model;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;

import it.polito.postiletto.db.DatiDAO;

public class Model {

	private DatiDAO datidao;
	private float n;
	private Map<LocalDate, Double> previsioni;
	Map<LocalDate, Integer> domanda;
	
	
	public Model() {
		datidao=new DatiDAO();
	}
	
	//FINITO
	public void creaPrevisioni(String reparto, double alfa) {
		
		previsioni=new HashMap<LocalDate, Double>();
		domanda=this.occupazioneReparto(LocalDate.of(2018, 06, 1), LocalDate.of(2018, 12, 31), reparto);
		
		double msd=0.0;
		int numero=0;
			
		//per giugno F(t)=A(t)
		LocalDate inizioGiugno=LocalDate.of(2018, 06, 1);
		LocalDate fineGiugno=LocalDate.of(2018, 06, 30);
			
		while(inizioGiugno.isBefore(fineGiugno)||inizioGiugno.equals(fineGiugno)) {
			//do
			previsioni.put(inizioGiugno, (double)domanda.get(inizioGiugno));
			//incremento
			inizioGiugno=inizioGiugno.plusDays(1);
		}

		//da luglio a gennaio uso F(t-1) quindi quelle del mese precendente
		LocalDate inizio=LocalDate.of(2018, 07, 1);
		LocalDate fine=LocalDate.of(2018, 12, 31);
		
		while(inizio.isBefore(fine)||inizio.equals(fine)) {
			//do
			double f=alfa*domanda.get(inizio) + (1-alfa)*previsioni.get(inizio.minusWeeks(1));
			previsioni.put(inizio, f);
			//incremento
			inizio=inizio.plusDays(1);
		}
			
		LocalDate in=LocalDate.of(2018, 07, 1);
		
		//calcolo l'MSD per ogni alfa e mi salvo il risultato più acccurato
		while(in.isBefore(fine)||in.equals(fine)) {
			//do
			msd+=Math.pow(previsioni.get(in)-domanda.get(in), 2);
			numero++;
			//incremento
			in=in.plusDays(1);
		}
		msd=msd/numero;
	}
	
	//FINITO
	public String previsione(LocalDate inizio, LocalDate fine, String reparto) {
		// stampare previsione e fare i due grafici
	
		String previsione="";
		int nmax=datidao.getNMaxPosti(reparto);
		int somma=0;
		int ngiorni=0;
		LocalDate in=inizio;
		
		//do previsione
		while(in.isBefore(fine)||in.equals(fine)) {
			previsione+=in+" "+previsioni.get(in)+"\n";
			
			in=in.plusDays(1);
		}
		
		//per il grafo, occupazione media del reparto nel periodo
		in=inizio;
		while(in.isBefore(fine)||in.equals(fine)) {
			//do
			somma+=previsioni.get(in);
			ngiorni++;
			//incremento
			in=in.plusDays(1);
		}
		System.out.println(somma+" "+ngiorni+" "+nmax);
		n=((float)((float)somma/ngiorni)/nmax)*100;
		
		return previsione;
	}
	
	//FINITO
	public float datiTortaPrev() {
		return n;
	}

	// FINITO
	public Map<LocalDate, Integer> occupazioneReparto(LocalDate in, LocalDate fin, String reparto) {
		
		Map<LocalDate, Integer> occ=new TreeMap<LocalDate, Integer>();
		
		while(in.isBefore(fin)||in.equals(fin)) {
			//do
			occ.put(in , datidao.occupazioneRepartoPrev(in, reparto));
			//incremento
			in=in.plusDays(1);
		}
		return occ;
	}
	

	public String simulazione(LocalDate inizio, LocalDate fine, String reparto) {
		
		// mi guardo anche i tot (2?) giorni prima e dopo per sapere se posso spostare il ricovero di qualche giorno
		Map<LocalDate, Integer> occ= occupazioneReparto(inizio.minusDays(2), fine.plusDays(2), reparto);
		
		return occ.toString();
	}

}
