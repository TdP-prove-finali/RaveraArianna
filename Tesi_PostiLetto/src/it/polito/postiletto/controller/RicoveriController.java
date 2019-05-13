package it.polito.postiletto.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import it.polito.postiletto.model.Model;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
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
    private Button btnPrevisione;
    
    @FXML
    private TextArea txtPrevisione;

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
    private Button btnSimulazione;
    
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
    	txtPrevisione.clear();
    	dateInizioPrevisione.setValue(null);
    	dateFinePrevisione.setValue(null);
    	datiTorta = FXCollections.observableArrayList(new PieChart.Data("reparto", 100));
		torta.setData(datiTorta);
    }

    @FXML
    void doPrevisione(ActionEvent event) {
    	LocalDate inizio=dateInizioPrevisione.getValue();
    	LocalDate fine=dateFinePrevisione.getValue();
    	String reparto=cbRepartoPrevisione.getValue();
    	if(inizio==null||fine==null||reparto==null||reparto==""||fine.isBefore(inizio)) {
    		txtPrevisione.appendText("Inserire correttamente tutti i dati.\n");
    	}
    	else {
    		txtPrevisione.appendText(model.previsione(inizio, fine, reparto));
    		titoloTorta="Statistiche occupazione reparto di "+reparto.toLowerCase();
    		datiTorta = FXCollections.observableArrayList(new PieChart.Data("Posti occupati", model.datiTortaPrev()), new PieChart.Data("Posti liberi", 100-(model.datiTortaPrev())));
    		torta.setData(datiTorta);
    	}
    }

    @FXML
    void doSimulazione(ActionEvent event) {
    	LocalDate inizio=dateInizioRicovero.getValue();
    	LocalDate fine=dateFineRicovero.getValue();
    	String reparto=cbRepartoRicovero.getValue();
    	
    	if(inizio==null||fine==null||reparto==null||reparto==""||fine.isBefore(inizio)) {
    		txtSimulazione.appendText("Inserire correttamente tutti i dati.\n");
    	}
    	else {
    		txtSimulazione.appendText(model.simulazione(inizio, fine, reparto));
    	}
    }
    
    // finito
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
        assert btnPrevisione != null : "fx:id=\"btnPrevisione\" was not injected: check your FXML file 'PostiLetto.fxml'.";
        assert txtPrevisione != null : "fx:id=\"txtPrevisione\" was not injected: check your FXML file 'PostiLetto.fxml'.";
        assert torta != null : "fx:id=\"torta\" was not injected: check your FXML file 'PostiLetto.fxml'.";
        assert tabRicovero != null : "fx:id=\"tabRicovero\" was not injected: check your FXML file 'PostiLetto.fxml'.";
        assert dateInizioRicovero != null : "fx:id=\"dateInizioRicovero\" was not injected: check your FXML file 'PostiLetto.fxml'.";
        assert dateFineRicovero != null : "fx:id=\"dateFineRicovero\" was not injected: check your FXML file 'PostiLetto.fxml'.";
        assert cbRepartoRicovero != null : "fx:id=\"cbRepartoRicovero\" was not injected: check your FXML file 'PostiLetto.fxml'.";
        assert btnSimulazione != null : "fx:id=\"btnSimulazione\" was not injected: check your FXML file 'PostiLetto.fxml'.";
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


