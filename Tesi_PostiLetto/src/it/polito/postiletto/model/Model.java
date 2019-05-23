package it.polito.postiletto.model;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import it.polito.postiletto.db.DatiDAO;

public class Model {

	private DatiDAO datidao;
	private float n;
	private Map<LocalDate, Double> previsioni;
	private Map<LocalDate, Integer> domanda;
	private Map<LocalDate, Double> msd;
	private Map<LocalDate, Double> previsioniRicovero;
	private Map<LocalDate, Integer> domandaRicovero;
	private boolean post;
	private char colore;
	
	
	public Model() {
		datidao=new DatiDAO();
	}
	
	//FINITO, lo richiamano subito entrambe le funzionalità per creare tutte le previsioni da giugno a dicembre
	public void creaPrevisioni(String reparto, double alfa) {
		msd=new HashMap<LocalDate, Double>();
		previsioni=new HashMap<LocalDate, Double>();
		domanda=this.occupazioneReparto(LocalDate.of(2018, 06, 1), LocalDate.of(2018, 12, 31), reparto);
		double err =0.0;
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

		//da luglio a gennaio uso F(t-1) quindi quelle della settimana precendente
		LocalDate inizio=LocalDate.of(2018, 07, 1);
		LocalDate fine=LocalDate.of(2018, 12, 31);
		
		while(inizio.isBefore(fine)||inizio.equals(fine)) {
			//do
			double f=alfa*domanda.get(inizio) + (1-alfa)*previsioni.get(inizio.minusWeeks(1));
			previsioni.put(inizio, f);
			err+=Math.pow(previsioni.get(inizio)-domanda.get(inizio), 2);
			numero++;
			err=err/numero;
			msd.put(inizio, err);
			//incremento
			inizio=inizio.plusDays(1);
		}
	}
	
	//FINITO, usato da creaPrevisione
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
	
	//FINITO, lo richiama la funzione di simulazione per creare la previsione dei gennaio 
	public void creaPrevisioniDiGennaio(String reparto, Double alfa) {
		previsioniRicovero=new HashMap<LocalDate, Double>();
		domandaRicovero=this.occupazioneReparto(LocalDate.of(2018, 12, 1), LocalDate.of(2019, 01, 31), reparto);
		
		//per giugno F(t)=A(t)
		LocalDate inizioDic=LocalDate.of(2018, 12, 1);
		LocalDate fineDic=LocalDate.of(2018, 12, 31);
		
		while(inizioDic.isBefore(fineDic)||inizioDic.equals(fineDic)) {
			//do
			previsioniRicovero.put(inizioDic, (double)previsioni.get(inizioDic));
			//incremento
			inizioDic=inizioDic.plusDays(1);
		}
		
		//da luglio a gennaio uso F(t-1) quindi quelle del mese precendente
		LocalDate inizio=LocalDate.of(2019, 01, 1);
		LocalDate fine=LocalDate.of(2019, 01, 31);
		
		//trovo alfa migliore
		while(inizio.isBefore(fine)||inizio.equals(fine)) {
			//do
			int uno=domandaRicovero.get(inizio);
			double due=previsioniRicovero.get(inizio.minusMonths(1));
			double f=alfa*domandaRicovero.get(inizio) + (1-alfa)*previsioniRicovero.get(inizio.minusMonths(1));
			previsioniRicovero.put(inizio, f);
			//incremento
			inizio=inizio.plusDays(1);
		}	
	}
	
	//FINITO, riempie la tabella della previsione  CHIEDO PER AGGIORNAMENTO TABELLA
	public List<Row> previsione(LocalDate inizio, LocalDate fine, String reparto) {
		// stampare previsione e fare i due grafici
	
		List<Row> previsione=new ArrayList<Row>();
		int nmax=datidao.getNMaxPosti(reparto);
		int somma=0;
		int ngiorni=0;
		LocalDate in=inizio;
		
		//do previsione
		while(in.isBefore(fine)||in.equals(fine)) {
			Row r;
			Month mese=in.getMonth();
			if(in.getMonth().toString().equals("JUNE"))
				r= new Row(in,previsioni.get(in),domanda.get(in),0);
			else
				r= new Row(in,previsioni.get(in),domanda.get(in),msd.get(in));
			previsione.add(r);
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
		n=((float)((float)somma/ngiorni)/nmax)*100;
		return previsione;
	}
	
	//FINITO, riempie la torta
	public float datiTortaPrev() {
		return n;
	}

	//FINITO, riempie la tabella della simulazione
	public List<Row2> simulazione(LocalDate inizio, LocalDate fine, String reparto) {
		List<Row2> previsione=new ArrayList<Row2>();
		LocalDate in=inizio; //piu o meno i giorni che sono disposto a posticipare o anticipare etc
		
		//do previsione
		while(in.isBefore(fine)||in.equals(fine)) {
			Row2 r= new Row2(in,previsioniRicovero.get(in),domandaRicovero.get(in));
			previsione.add(r);
			in=in.plusDays(1);
		}
		return previsione;
	}
	
	// riempie la text area della simulazione
	public String doIpotesi(List<Row2> simulazione, String reparto) {
		Map<LocalDate, Double> situazionePrevista=new HashMap<LocalDate, Double>();
		Map<LocalDate, Integer> situazioneCerta=new HashMap<LocalDate, Integer>();
		List<LocalDate> dateRischio=new ArrayList<LocalDate>();
		List<LocalDate> dateProblema=new ArrayList<LocalDate>();
		int max=datidao.getNMaxPosti(reparto);
		String result="";
		post=false;
		int prob=0;
		int probCerto=0;
		
		for(Row2 r:simulazione) {
			situazionePrevista.put(r.getData(), (r.getPrevisione())); //giorno e posti liberi secondo domanda+previsione
			if(r.getPrevisione()>=max) {
				dateRischio.add(r.getData());
				prob++;
			}
		}
		for(Row2 r:simulazione) {
			situazioneCerta.put(r.getData(), (r.getDomanda())); //giorno e posti liberi secondo domanda
			if(r.getDomanda()>=max) {
				dateProblema.add(r.getData());
				prob++;
				probCerto++;
			}
		}
		
		if(prob==0) {
			colore='v';
			result="ACCETTATO.\nNon sono previsti problemi di sovraffollamento del reparto "+reparto.toLowerCase()+" per le date indicate.\n";
		}
			else {
			String r="";
			if(probCerto==0) {
				for(LocalDate ld:dateRischio)
					r+="\n"+ld;
				colore='g';
				result="ACCETTATO.\nIl ricovero viene accettato in quanto l'occupazione certa non supera la capienza massima.\nBisogna però prestare"
						+ "attenzione perchè seconda la previsione ci saranno prooblemi di sovraffollamento nelle date:"+r;
			}
			else{
				for(LocalDate ld:dateProblema)
					r+="\n"+ld;
				colore='r';
				post=true;
				result="PROBLEMA!\nNon ci sono posti disponibili nelle date:"+r+"\nSe possibile, provare a posticipare il ricovero di alcuni giorni o"
						+ "selezionare altre date.\nNel caso in cui questo non sia possibile ci troviamo costretti a dover rifiutare il ricovero.";
			}
		}
		return result;
	}
	
	public boolean posticipa() {
		return post;
	}

	public Integer occMax(String reparto) {
		return datidao.getNMaxPosti(reparto);
	}

	public char getColore() {
		return colore;
	}
}
