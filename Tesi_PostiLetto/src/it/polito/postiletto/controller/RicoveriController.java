package it.polito.postiletto.controller;

import java.net.URL;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.postiletto.model.Model;
import it.polito.postiletto.model.Row;
import javafx.collections.*;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
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
    private PieChart torta;

    @FXML
    private Tab tabRicovero;

    @FXML
    private DatePicker dateInizioRicovero;

    @FXML
    private DatePicker dateFineRicovero;

    @FXML
    private ComboBox<String> cbRepartoRicovero;
    
    @FXML
    private Slider alphaSimulazione;

    @FXML
    private Button btnSimulazione;

    @FXML
    private Label labelInfo1;

    @FXML
    private TableView<Row> tabellaS;

    @FXML
    private TableColumn<Row, LocalDate> colDataS;

    @FXML
    private TableColumn<Row, Double> colPrevS;

    @FXML
    private TableColumn<Row, Integer> colOccS;

    @FXML
    private TableColumn<Row, Double> colMSDS;
    
    @FXML
    private TextArea txtSimulazione;

    @FXML
    private Button btnCancella;
    
    
    private ObservableList<PieChart.Data> datiTorta;
    private String titoloTorta="Statistiche occupazione";
    
    private Stage secondaryStage;


    @FXML
    void doCancella(ActionEvent event) {
    	cbRepartoPrevisione.setValue("");
    	cbRepartoRicovero.setValue("");
    	
    	dateInizioPrevisione.setValue(null);
    	dateFinePrevisione.setValue(null);
    	datiTorta = FXCollections.observableArrayList(new PieChart.Data("reparto", 100));
		torta.setData(datiTorta);
		
		ObservableList<Row> righe=FXCollections.observableArrayList();
		tabellaP.setItems(righe);
		
		labelInfo.setText("");
		
    	txtSimulazione.clear();
    	
    	dateInizioRicovero.setValue(null);
    	dateFineRicovero.setValue(null);
    }

    //FINITO
    @FXML
    void doPrevisione(ActionEvent event) {
    	LocalDate inizio=dateInizioPrevisione.getValue();
    	LocalDate fine=dateFinePrevisione.getValue();
    	String reparto=cbRepartoPrevisione.getValue();
    	double alfa=alpha.getValue();

		model.creaPrevisioni(reparto, alfa);
    	
    	if(alfa==0.0||inizio==null||fine==null||reparto==null||reparto=="") {
    		labelInfo.setText("ATTENZIONE! Inserire tutti i dati.");
    	}
    	else if(fine.isBefore(inizio)) {
    		labelInfo.setText("ATTENZIONE! Inserire le date correttamente.");
    	}
    	else {
    		
    		ObservableList<Row> righe=FXCollections.observableArrayList();
    		for(Row r:model.previsione(inizio, fine, reparto)) {
    			righe.add(r);
    		}
    		
    		colDataP.setCellValueFactory(new PropertyValueFactory<Row, LocalDate>("data"));
            colPrevP.setCellValueFactory(new PropertyValueFactory<Row, Double>("previsione"));
            colOccP.setCellValueFactory(new PropertyValueFactory<Row, Integer>("domanda"));
            colMSDP.setCellValueFactory(new PropertyValueFactory<Row, Double>("msd"));
            
    		tabellaP.setItems(righe);
    		
    		titoloTorta="Statistiche occupazione media del reparto di "+reparto.toLowerCase();
    		datiTorta = FXCollections.observableArrayList(new PieChart.Data("Posti occupati", model.datiTortaPrev()), new PieChart.Data("Posti liberi", 100-(model.datiTortaPrev())));
    		torta.setData(datiTorta);
    	}
    }

    @FXML
    void doSimulazione(ActionEvent event) {
    	LocalDate inizio=dateInizioRicovero.getValue();
    	LocalDate fine=dateFineRicovero.getValue();
    	String reparto=cbRepartoRicovero.getValue();
    	double alfa=alpha.getValue();

		model.creaPrevisioni(reparto, alfa);
    	
		if(alfa==0.0||inizio==null||fine==null||reparto==null||reparto=="") {
    		labelInfo1.setText("ATTENZIONE! Inserire tutti i dati.");
    	}
    	else if(fine.isBefore(inizio)) {
    		labelInfo1.setText("ATTENZIONE! Inserire le date correttamente.");
    	}
    	else {
    		
    		ObservableList<Row> righe=FXCollections.observableArrayList();
    		for(Row r:model.simulazione(inizio, fine, reparto)) {
    			righe.add(r);
    		}
    		
    		colDataS.setCellValueFactory(new PropertyValueFactory<Row, LocalDate>("data"));
            colPrevS.setCellValueFactory(new PropertyValueFactory<Row, Double>("previsione"));
            colOccS.setCellValueFactory(new PropertyValueFactory<Row, Integer>("domanda"));
            colMSDS.setCellValueFactory(new PropertyValueFactory<Row, Double>("msd"));
            
    		tabellaS.setItems(righe);
    		
    		String result=model.doIpotesi(model.simulazione(inizio, fine, reparto), reparto);
    		txtSimulazione.appendText(result);
    	}
    }
    
    // FINITO
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
        assert torta != null : "fx:id=\"torta\" was not injected: check your FXML file 'PostiLetto.fxml'.";
        assert tabRicovero != null : "fx:id=\"tabRicovero\" was not injected: check your FXML file 'PostiLetto.fxml'.";
        assert dateInizioRicovero != null : "fx:id=\"dateInizioRicovero\" was not injected: check your FXML file 'PostiLetto.fxml'.";
        assert dateFineRicovero != null : "fx:id=\"dateFineRicovero\" was not injected: check your FXML file 'PostiLetto.fxml'.";
        assert cbRepartoRicovero != null : "fx:id=\"cbRepartoRicovero\" was not injected: check your FXML file 'PostiLetto.fxml'.";
        assert alphaSimulazione != null : "fx:id=\"alphaSimulazione\" was not injected: check your FXML file 'PostiLetto.fxml'.";
        assert btnSimulazione != null : "fx:id=\"btnSimulazione\" was not injected: check your FXML file 'PostiLetto.fxml'.";
        assert labelInfo1 != null : "fx:id=\"labelInfo1\" was not injected: check your FXML file 'PostiLetto.fxml'.";
        assert tabellaS != null : "fx:id=\"tabellaS\" was not injected: check your FXML file 'PostiLetto.fxml'.";
        assert colDataS != null : "fx:id=\"colDataS\" was not injected: check your FXML file 'PostiLetto.fxml'.";
        assert colPrevS != null : "fx:id=\"colPrevS\" was not injected: check your FXML file 'PostiLetto.fxml'.";
        assert colOccS != null : "fx:id=\"colOccS\" was not injected: check your FXML file 'PostiLetto.fxml'.";
        assert colMSDS != null : "fx:id=\"colMSDS\" was not injected: check your FXML file 'PostiLetto.fxml'.";
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


