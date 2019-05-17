package it.polito.postiletto.model;

import java.time.LocalDate;
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
	private Map<LocalDate, Double> previsioniGennaio;
	private boolean post;
	
	
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

		//da luglio a gennaio uso F(t-1) quindi quelle del mese precendente
		LocalDate inizio=LocalDate.of(2018, 07, 1);
		LocalDate fine=LocalDate.of(2018, 12, 31);
		
		while(inizio.isBefore(fine)||inizio.equals(fine)) {
			//do
			double f=alfa*domanda.get(inizio) + (1-alfa)*previsioni.get(inizio.minusWeeks(1));
			previsioni.put(inizio, f);
			if(inizio.getMonth().equals("JANUARY")) {
				previsioniGennaio.put(inizio, f);
			}
			
			//do
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
			double f=alfa*domandaRicovero.get(inizio) + (1-alfa)*previsioniRicovero.get(inizio.minusMonths(1));
			previsioniRicovero.put(inizio, f);
			//incremento
			inizio=inizio.plusDays(1);
		}	
	}
	
	//FINITO, riempie la tabella della previsione
	public List<Row> previsione(LocalDate inizio, LocalDate fine, String reparto) {
		// stampare previsione e fare i due grafici
	
		List<Row> previsione=new ArrayList<Row>();
		int nmax=datidao.getNMaxPosti(reparto);
		int somma=0;
		int ngiorni=0;
		LocalDate in=inizio;
		
		//do previsione
		while(in.isBefore(fine)||in.equals(fine)) {
			Row r= new Row(in,previsioni.get(in),domanda.get(in),msd.get(in));
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
		int max=datidao.getNMaxPosti(reparto);
		String result="";
		post=false;
		
		for(Row2 r:simulazione) {
			situazionePrevista.put(r.getData(), (max-(r.getPrevisione()+r.getDomanda()))); //giorno e posti liberi secondo domanda+previsione
		}
		for(Row2 r:simulazione) {
			situazioneCerta.put(r.getData(), (max-(r.getDomanda()))); //giorno e posti liberi secondo domanda
		}
		
		for(LocalDate data:situazionePrevista.keySet()) {
			if(situazionePrevista.get(data)<=max) {
				//siamo a posto
			}
			else {
				//secondo la previsione non ci sono posti a sufficienza
				if(situazioneCerta.get(data)<=max) {
					//il posto ce ma devo valutare come muovermi se si avvera la mia previsione
				}
				else {
					//non c'è sicuramente posto: cerco una soluzione.
					//Se è un ricovero "spostabile" valuto altri reparti,
					//valuto se è possibile posticiparlo se è programmato e valuto disponibilità giorni seguenti
					//(apro opzione posticipa di tot giorni!)
					post=true; //ripremere avvia simulazione
					//se no rifiuto.
				}
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

}
