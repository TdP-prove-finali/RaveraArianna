package it.polito.postiletto.model;

import java.time.LocalDate;
import java.util.*;

import it.polito.postiletto.db.DatiDAO;

public class Model {

	private DatiDAO datidao;
	private float n;
	
	private Map<String, Integer> maxReparto=new TreeMap<String, Integer>();
	private Map<LocalDate, Integer> occ;
	
	public Model() {
		datidao=new DatiDAO();
	}
	
	public String previsione(LocalDate inizio, LocalDate fine, String reparto) {
		// stampare previsione e fare i due grafici
		n=0;
		String previsione="Nessuna previsione.\n";
		int nmax=datidao.getNMaxPosti(reparto);
		
		Map<LocalDate, Integer> occ= occupazioneReparto(inizio, fine, reparto);
		
		//fai previsione su base occ
		n=((float)5/nmax)*100; //INSERISCI %OCC al posto di 5
		
		return previsione;
	}
	
	public float datiTortaPrev() {
		return n;
	}

	private Map<LocalDate, Integer> occupazioneReparto(LocalDate in, LocalDate fin, String reparto) {
		
		/*LocalDate inizio = LocalDate.of(2018,06,01);
		LocalDate fineacc = LocalDate.of(2018,12,31);
		LocalDate finedim = LocalDate.of(2019,04,11);*/
		occ=new TreeMap<LocalDate, Integer>();
		
		while(in.isBefore(fin)||in.equals(fin)) {
			//do
			occ.put(in, datidao.occupazioneRepartoNovDic(in, reparto));
			
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
