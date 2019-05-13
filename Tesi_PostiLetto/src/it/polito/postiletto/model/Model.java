package it.polito.postiletto.model;

import java.time.LocalDate;
import java.util.*;

import it.polito.postiletto.db.DatiDAO;

public class Model {

	private DatiDAO datidao;
	private float n;
	
	private Map<LocalDate, Integer> occ;
	
	public Model() {
		datidao=new DatiDAO();
	}
	
	public String previsione(LocalDate inizio, LocalDate fine, String reparto) {
		// stampare previsione e fare i due grafici
		
		Map<LocalDate, Integer> domanda= occupazioneReparto(inizio.minusMonths(1), fine, reparto);
		//System.out.println(domanda);
		String previsione="Nessuna previsione.\n";
		int nmax=datidao.getNMaxPosti(reparto);
		int somma=0;
		int ngiorni=0;
		
		if(inizio.getMonth().equals(fine.getMonth())) {
			//do previsione in un mese solo, cioè il mese prec al mio e confronto
			previsione=this.prevedi(inizio, fine, domanda);
		}
		
		else {
			//do prev in due mesi
		}
		
		//fai previsione su base occ
		
		while(inizio.isBefore(fine)||inizio.equals(fine)) {
			//do
			somma+=domanda.get(inizio);
			ngiorni++;
			//incremento
			inizio=inizio.plusDays(1);
		}
		System.out.println(somma+" "+ngiorni+" "+nmax);
		n=((float)((float)somma/ngiorni)/nmax)*100; //INSERISCI %OCC al posto di 5
		
		return previsione;
	}
	
	// FINITO
	private String prevedi(LocalDate inizio, LocalDate fine, Map<LocalDate, Integer> domandaMeseCorrente ) {
		Map<LocalDate, Double> stime=new HashMap<LocalDate, Double>();
		Map<LocalDate, Double> best=new HashMap<LocalDate, Double>();
		double alfa; //scelgo il valore più adatto
		double alfaBest=-1;
		double msdMin=10000;
		double msd=0.0;
		double f;
		int numero=0;
		int diffGiorni=fine.getDayOfMonth()-inizio.getDayOfMonth();
		String risultato="";
		LocalDate in;
		
		for(alfa=0; alfa<1; alfa+=0.1) { //provo tutte le alfa e trovo la migliore
			stime.clear();
			in=inizio;
			stime.put(inizio.minusDays(1), domandaMeseCorrente.get(inizio.minusMonths(1)).doubleValue());
			
			for(int i=1; i<=diffGiorni+1; i++) { //previsione su tutti i giorni utili
				f=alfa*domandaMeseCorrente.get(inizio.plusDays(i-1))+(1-alfa)*stime.get(inizio.minusDays(1));
				stime.put(inizio.plusDays(i-1), f);
			}
			
			//calcolo l'MSD per ogni alfa e mi salvo il risultato più acccurato
			while(in.isBefore(fine)||in.equals(fine)) {
				//do
				msd+=Math.pow(stime.get(in)-domandaMeseCorrente.get(in), 2);
				numero++;
				//incremento
				in=in.plusDays(1);
			}
			msd=msd/numero;
			
			if(msd<msdMin) {
				best.putAll(stime);
				msdMin=msd;
				alfaBest=alfa;
			}
		}
		
		while(inizio.isBefore(fine)||inizio.equals(fine)) {
			//do
			risultato+=inizio+": previsione= "+stime.get(inizio)+", occupazione reale= "+domandaMeseCorrente.get(inizio)+" (Alpha="+alfaBest+", MSD="+msdMin+")\n";
			//incremento
			inizio=inizio.plusDays(1);
		}
		
		return risultato;
	}

	
	
	public float datiTortaPrev() {
		return n;
	}

	
	
	// FINITO
	
	public Map<LocalDate, Integer> occupazioneReparto(LocalDate in, LocalDate fin, String reparto) {
		
		occ=new TreeMap<LocalDate, Integer>();
		
		while(in.isBefore(fin)||in.equals(fin)) {
			//do
			LocalDate ora=LocalDate.of(in.getYear(), in.getMonthValue(), in.getDayOfMonth());
			occ.put(ora , datidao.occupazioneRepartoPrev(ora, reparto));
			//incremento
			in=in.plusDays(1);
		}
		return occ;
	}

	public String simulazione(LocalDate inizio, LocalDate fine, String reparto) {
		
		// mi guardo anche i tot (2?) giorni prima e dopo per sapere se posso spostare il ricovero di qualche giorno
		this.occupazioneReparto(inizio.minusDays(2), fine.plusDays(2), reparto);
		
		return occ.toString();
	}

}
