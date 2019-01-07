package sample;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    @FXML
    private GridPane gridPane;

    @FXML
    private Label labelOrar;

    @FXML
    private ChoiceBox<Group> selectClasa;

    @FXML
    private Button pdfButton;

    public static byte[][] timetable;
    Map<Professor, Color> map = new HashMap<>();

    public void showOrarClasa(Group c){
        gridPane.getChildren().forEach(ch ->{
            if(ch instanceof Label){
                ((Label) ch).setText("");
                ((Label)ch).setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        } );
        String[] days = new String[]{"LUNI", "MARTI", "MIERCURI", "JOI", "VINERI"};
        String[] hours = new String[]{"8-9", "9-10", "10-11", "11-12", "12-13", "13-14", "14-15"};
        for(int i = 0; i < 5; i++){
            Label l = new Label(days[i]);
            l.setAlignment(Pos.CENTER);
            l.setFont(Font.font("Arial", FontWeight.BOLD, 15));
            l.setStyle("-fx-border-color: gray;");
            l.setMaxHeight(80);
            l.setMaxWidth(120);
            GridPane.setFillHeight(l,true);
            GridPane.setFillWidth(l, true);
            gridPane.add(l,i+1,0);
        }
        for(int i = 0; i < 7; i++){
            Label l = new Label(hours[i]);
            l.setAlignment(Pos.CENTER);
            l.setFont(Font.font("Arial", FontWeight.BOLD, 15));
            l.setStyle("-fx-border-color: gray;");
            l.setMaxHeight(80);
            l.setMaxWidth(120);
            GridPane.setFillHeight(l,true);
            GridPane.setFillWidth(l, true);
            gridPane.add(l,0,i+1);
        }
        for(byte hour = 0; hour < Input.nrHours * 5; hour++){
            for(byte prof = 0; prof < Input.nrProf ; prof++){
                if(timetable[prof][hour] == c.id){
                    Label l = new Label(Input.profs.get(prof).toString());
                    l.setWrapText(true);
                    l.setBackground(new Background(new BackgroundFill(map.get(Input.profs.get(prof)), CornerRadii.EMPTY, Insets.EMPTY)));
                    l.setAlignment(Pos.CENTER);
                    l.setFont(new Font(18));
                    l.setStyle("-fx-border-color: gray;");
                    l.setMaxHeight(80);
                    l.setMaxWidth(120);
                    GridPane.setFillHeight(l,true);
                    GridPane.setFillWidth(l, true);
                    gridPane.add(l,hour / Input.nrHours + 1,hour % Input.nrHours + 1);
                }
            }
        }
    }


    public void initialize(URL u, ResourceBundle r){
        for(Professor p: Input.profs.values()){
            double c1 = 1 - new Random().nextDouble()/1.5;//red
            double c2 = 1 - new Random().nextDouble()/1.4;//green
            double c3 = 1 - new Random().nextDouble()/1.2;//blue
            Color c = new Color(c1,c2,c3,1);
            map.put(p,c);
        }
        selectClasa.setItems(FXCollections.observableArrayList(Input.groups.values()));
        selectClasa.setValue(FXCollections.observableArrayList(Input.groups.values()).get(0));
        labelOrar.setText(("ORAR CLASA " + selectClasa.getValue()).toUpperCase());
        showOrarClasa(selectClasa.getValue());
        selectClasa.setOnAction(e->{
            labelOrar.setText(("ORAR CLASA " + selectClasa.getValue()).toUpperCase());
            showOrarClasa(selectClasa.getValue());
        });
    }

}
