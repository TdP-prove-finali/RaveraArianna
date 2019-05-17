package it.polito.postiletto.controller;

import java.net.URL;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.postiletto.model.Model;
import it.polito.postiletto.model.Row;
import it.polito.postiletto.model.Row2;
import javafx.collections.*;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class RicoveriController {
	
	Model model;

	@FXML
    private ResourceBundle resources;

    @FXML
    private URL location;
    
	@FXML
    private Tab tabForecasting;

    @FXML
    private DatePicker dateInizioPrevisione;

    @FXML
    private DatePicker dateFinePrevisione;

    @FXML
    private ComboBox<String> cbRepartoPrevisione;

    @FXML
    private Slider alpha;

    @FXML
    private Button btnPrevisione;

    @FXML
    private Label labelInfo;
    
    @FXML
    private TableView<Row> tabellaP;
    
    @FXML
    private TableColumn<Row, LocalDate> colDataP;

    @FXML
    private TableColumn<Row, Double> colPrevP;

    @FXML
    private TableColumn<Row, Integer> colOccP;

    @FXML
    private TableColumn<Row, Double> colMSDP;
    
    @FXML
    private LineChart<String, Double> grafo;

    @FXML
    private CategoryAxis giorniGrafo;

    @FXML
    private NumberAxis occupazioneGrafo;

    @FXML
    private PieChart torta;

    @FXML
    private Tab tabRicovero;

    @FXML
    private DatePicker dateInizioRicovero;

    @FXML
    private DatePicker dateFineRicovero;

    @FXML
    private Label txt1;

    @FXML
    private Label txt2;

    @FXML
    private ChoiceBox<Integer> giorniPost;

    @FXML
    private ComboBox<String> cbRepartoRicovero;
    
    @FXML
    private Slider alphaSimulazione;

    @FXML
    private Button btnSimulazione;

    @FXML
    private Label labelInfo1;

    @FXML
    private TableView<Row2> tabellaS;

    @FXML
    private TableColumn<Row2, LocalDate> colDataS;

    @FXML
    private TableColumn<Row2, Double> colPrevS;

    @FXML
    private TableColumn<Row2, Integer> colOccS;
    
    @FXML
    private TextArea txtSimulazione;

    @FXML
    private Button btnCancella;
    
    
    private ObservableList<PieChart.Data> datiTorta;
    private String titoloTorta="Statistiche occupazione";
    
    private Stage secondaryStage;


    //FINITO
    @FXML
    void doCancella(ActionEvent event) {
    	cbRepartoPrevisione.setValue("");
    	cbRepartoRicovero.setValue("");
    	
    	dateInizioPrevisione.setValue(null);
    	dateFinePrevisione.setValue(null);
    	datiTorta = FXCollections.observableArrayList(new PieChart.Data("reparto", 100));
		torta.setData(datiTorta);

    	grafo.getData().clear();;
		
		ObservableList<Row> righe=FXCollections.observableArrayList();
		tabellaP.setItems(righe);
		ObservableList<Row2> righe2=FXCollections.observableArrayList();
		tabellaS.setItems(righe2);
		
		labelInfo.setText("");
		labelInfo1.setText("");
		
    	txtSimulazione.clear();
    	
    	dateInizioRicovero.setValue(null);
    	dateFineRicovero.setValue(null);
    	
    	txt1.setVisible(false);
		txt2.setVisible(false);
		giorniPost.setVisible(false);
    }

    // Rivedi grafo e problema torta che scompare
    @FXML
    void doPrevisione(ActionEvent event) {
    	LocalDate inizio=dateInizioPrevisione.getValue();
    	LocalDate fine=dateFinePrevisione.getValue();
    	String reparto=cbRepartoPrevisione.getValue();
    	double alfa=alpha.getValue();
    	XYChart.Series<String, Double> series = new XYChart.Series<>();
    	
    	grafo.getData().clear();

		model.creaPrevisioni(reparto, alfa);
    	
    	if(alfa==0.0||inizio==null||fine==null||reparto==null||reparto=="") {
    		labelInfo.setText("ATTENZIONE! Inserire tutti i dati.");
    		labelInfo.setTextFill(Color.RED);
    	}
    	else if(fine.isBefore(inizio)) {
    		labelInfo.setText("ATTENZIONE! Inserire le date correttamente.");
    		labelInfo.setTextFill(Color.RED);
    	}
    	else {
    		labelInfo.setText("Posti letto disponibili nel reparto: "+model.occMax(reparto));
    		labelInfo.setTextFill(Color.BLACK);
    		ObservableList<Row> righe=FXCollections.observableArrayList();
    		tabellaP.setItems(righe);
    		for(Row r:model.previsione(inizio, fine, reparto)) {
    			righe.add(r);
    		}
    		
    		colDataP.setCellValueFactory(new PropertyValueFactory<Row, LocalDate>("data"));
            colPrevP.setCellValueFactory(new PropertyValueFactory<Row, Double>("previsione"));
            colOccP.setCellValueFactory(new PropertyValueFactory<Row, Integer>("domanda"));
            colMSDP.setCellValueFactory(new PropertyValueFactory<Row, Double>("msd"));
            
    		tabellaP.setItems(righe);
    		
    		titoloTorta="Statistiche occupazione media del reparto di "+reparto.toLowerCase();
    		if(model.datiTortaPrev()>=100)
    			datiTorta = FXCollections.observableArrayList(new PieChart.Data("Posti occupati", 100));
    		else
    			datiTorta = FXCollections.observableArrayList(new PieChart.Data("Posti occupati", model.datiTortaPrev()), new PieChart.Data("Posti liberi", 100-(model.datiTortaPrev())));
    		torta.setData(datiTorta);
    		
            //populating the series with data
            for(Row r:model.previsione(inizio, fine, reparto)) {
            	series.getData().add(new XYChart.Data<String, Double>(r.getData().getDayOfMonth()+"",r.getPrevisione()));
            }
            grafo.getData().add(series);
    	}
    }

    @FXML
    void doSimulazione(ActionEvent event) {
    	LocalDate inizio=dateInizioRicovero.getValue();
    	LocalDate fine=dateFineRicovero.getValue();
    	String reparto=cbRepartoRicovero.getValue();
    	double alfa=alphaSimulazione.getValue();
    	int post;
    	if(giorniPost.isVisible())
    		post=giorniPost.getValue();

		model.creaPrevisioni(reparto, alfa);
		model.creaPrevisioniDiGennaio(reparto, alfa);
    	
		if(alfa==0.0||inizio==null||fine==null||reparto==null||reparto=="") {
    		labelInfo1.setText("ATTENZIONE! Inserire tutti i dati.");
    		labelInfo1.setTextFill(Color.RED);
    	}
    	else if(fine.isBefore(inizio)) {
    		labelInfo1.setText("ATTENZIONE! Inserire le date correttamente.");
    		labelInfo1.setTextFill(Color.RED);
    	}
    	else {
    		labelInfo1.setText("Posti letto disponibili nel reparto: "+model.occMax(reparto));
    		labelInfo1.setTextFill(Color.BLACK);
    		if(giorniPost!=null) {
    			//calcola con posticipazione
    		}
    		ObservableList<Row2> righe=FXCollections.observableArrayList();
    		for(Row2 r:model.simulazione(inizio, fine, reparto)) {
    			righe.add(r);
    		}
    		
    		colDataS.setCellValueFactory(new PropertyValueFactory<Row2, LocalDate>("data"));
            colPrevS.setCellValueFactory(new PropertyValueFactory<Row2, Double>("previsione"));
            colOccS.setCellValueFactory(new PropertyValueFactory<Row2, Integer>("domanda"));
            
    		tabellaS.setItems(righe);
    		
    		String result=model.doIpotesi(model.simulazione(inizio, fine, reparto), reparto);
    		txtSimulazione.appendText(result);
    		
    		//se non c'è posto chiedo se posso posticipare
    		if(model.posticipa()==true) {
    			txt1.setVisible(true);
    			txt2.setVisible(true);
    			giorniPost.setVisible(true);
    		}
    	}
    }
    
    //FINITO
    @FXML
    void doEspandiGrafo(MouseEvent event) {
    	
    	try {
			secondaryStage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("PieChart.fxml"));
			BorderPane children = (BorderPane) loader.load();
			Scene scene = new Scene(children);

			PieChartController controller = (PieChartController) loader.getController();

			ObservableList<PieChart.Data> dati = FXCollections.observableArrayList(this.datiTorta);
			controller.setDataForPieChart(dati, titoloTorta);
			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			secondaryStage.setScene(scene);
			secondaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}

    }

    @FXML
    void initialize() {
    	 assert tabForecasting != null : "fx:id=\"tabForecasting\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert dateInizioPrevisione != null : "fx:id=\"dateInizioPrevisione\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert dateFinePrevisione != null : "fx:id=\"dateFinePrevisione\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert cbRepartoPrevisione != null : "fx:id=\"cbRepartoPrevisione\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert alpha != null : "fx:id=\"alpha\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert btnPrevisione != null : "fx:id=\"btnPrevisione\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert labelInfo != null : "fx:id=\"labelInfo\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert tabellaP != null : "fx:id=\"tabellaP\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert colDataP != null : "fx:id=\"colDataP\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert colPrevP != null : "fx:id=\"colPrevP\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert colOccP != null : "fx:id=\"colOccP\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert colMSDP != null : "fx:id=\"colMSDP\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert grafo != null : "fx:id=\"grafo\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert giorniGrafo != null : "fx:id=\"giorniGrafo\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert occupazioneGrafo != null : "fx:id=\"occupazioneGrafo\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert torta != null : "fx:id=\"torta\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert tabRicovero != null : "fx:id=\"tabRicovero\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert dateInizioRicovero != null : "fx:id=\"dateInizioRicovero\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert dateFineRicovero != null : "fx:id=\"dateFineRicovero\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert txt1 != null : "fx:id=\"txt1\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert txt2 != null : "fx:id=\"txt2\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert giorniPost != null : "fx:id=\"giorniPost\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert cbRepartoRicovero != null : "fx:id=\"cbRepartoRicovero\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert alphaSimulazione != null : "fx:id=\"alphaSimulazione\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert btnSimulazione != null : "fx:id=\"btnSimulazione\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert labelInfo1 != null : "fx:id=\"labelInfo1\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert tabellaS != null : "fx:id=\"tabellaS\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert colDataS != null : "fx:id=\"colDataS\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert colPrevS != null : "fx:id=\"colPrevS\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert colOccS != null : "fx:id=\"colOccS\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert txtSimulazione != null : "fx:id=\"txtSimulazione\" was not injected: check your FXML file 'PostiLetto.fxml'.";
         assert btnCancella != null : "fx:id=\"btnCancella\" was not injected: check your FXML file 'PostiLetto.fxml'.";

     	
        datiTorta = FXCollections.observableArrayList(new PieChart.Data("reparto", 100));
		torta.setData(datiTorta);
		
		LocalDate minDate = LocalDate.of(2018, 07, 1);
		LocalDate maxDate = LocalDate.of(2018, 12, 31);
		dateInizioPrevisione.setDayCellFactory(d -> new DateCell() {
			public void updateItem(LocalDate item, boolean empty) {
				super.updateItem(item, empty);
				setDisable(item.isAfter(maxDate) || item.isBefore(minDate));
               }
			}
		);
		dateFinePrevisione.setDayCellFactory(d -> new DateCell() {
			public void updateItem(LocalDate item, boolean empty) {
				super.updateItem(item, empty);
				setDisable(item.isAfter(maxDate) || item.isBefore(minDate));
               }
			}
		);
		
		LocalDate minDa = LocalDate.of(2019, 01, 01);
		LocalDate maxDa = LocalDate.of(2019, 01, 31);
		dateInizioRicovero.setDayCellFactory(d -> new DateCell() {
			public void updateItem(LocalDate item, boolean empty) {
				super.updateItem(item, empty);
				setDisable(item.isAfter(maxDa) || item.isBefore(minDa));
               }
			}
		);
		dateFineRicovero.setDayCellFactory(d -> new DateCell() {
			public void updateItem(LocalDate item, boolean empty) {
				super.updateItem(item, empty);
				setDisable(item.isAfter(maxDa) || item.isBefore(minDa));
               }
			}
		);
    }
    
    public void setModel(Model model) {
		this.model = model; 
		cbRepartoPrevisione.getItems().addAll("","CARDIOLOGIA","CHIRURGIA","GINECOLOGIA","MEDICINA","NEUROLOGIA","NIDO","ORTOPEDIA","PEDIATRIA","PSICHIATRIA","RIANIMAZIONE","U.T.I.C","UROLOGIA");
		cbRepartoRicovero.getItems().addAll("","CARDIOLOGIA","CHIRURGIA","GINECOLOGIA","MEDICINA","NEUROLOGIA","NIDO","ORTOPEDIA","PEDIATRIA","PSICHIATRIA","RIANIMAZIONE","U.T.I.C","UROLOGIA");
    }
}


