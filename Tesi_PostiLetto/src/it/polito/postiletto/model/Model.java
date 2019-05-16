package it.polito.postiletto.model;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;

import it.polito.postiletto.db.DatiDAO;

public class Model {

	private DatiDAO datidao;
	private float n;
	private Map<LocalDate, Double> previsioni;
	private Map<LocalDate, Integer> domanda;
	private Map<LocalDate, Double> msd;
	
	
	public Model() {
		datidao=new DatiDAO();
	}
	
	//FINITO
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
			
			//do
			err+=Math.pow(previsioni.get(inizio)-domanda.get(inizio), 2);
			numero++;
			err=err/numero;
			msd.put(inizio, err);
			
			//incremento
			inizio=inizio.plusDays(1);
		}
	}
	
	//FINITO
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
	

	public List<Row> simulazione(LocalDate inizio, LocalDate fine, String reparto) {
		
		// mi guardo anche i tot (2?) giorni prima e dopo per sapere se posso spostare il ricovero di qualche giorno
		List<Row> previsione=new ArrayList<Row>();
		int nmax=datidao.getNMaxPosti(reparto);
		int somma=0;
		int ngiorni=0;
		LocalDate in=inizio; //piu o meno i giorni che sono disposto a posticipare o anticipare etc
		
		//do previsione
		while(in.isBefore(fine)||in.equals(fine)) {
			Row r= new Row(in,previsioni.get(in),domanda.get(in),msd.get(in));
			previsione.add(r);
			in=in.plusDays(1);
		}
		
		return previsione;
	}

	public String doIpotesi(List<Row> simulazione, String reparto) {
		Map<LocalDate, Double> situazionePrevista=new HashMap<LocalDate, Double>();
		Map<LocalDate, Integer> situazioneCerta=new HashMap<LocalDate, Integer>();
		int max=datidao.getNMaxPosti(reparto);
		String result="";
		
		for(Row r:simulazione) {
			situazionePrevista.put(r.getData(), (max-(r.getPrevisione()+r.getDomanda()))); //giorno e posti liberi secondo domanda+previsione
		}
		for(Row r:simulazione) {
			situazioneCerta.put(r.getData(), (max-(r.getDomanda()))); //giorno e posti liberi secondo domanda
		}
		
		return result;
	}

}
