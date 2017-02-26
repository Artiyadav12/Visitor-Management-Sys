package visMan;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.Paper;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SplitPane;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Node;

import javafx.event.ActionEvent;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import visMan.database.CheckIn;
import visMan.utils.Utils;

public class PrintUserCardController implements Initializable{
	private Visitor currentVisitor;
	@FXML
	private SplitPane printableRegionRoot;
	@FXML
	private Label nameField;
	@FXML
	private Label ageField;
	@FXML
	private Label contactField;
	@FXML
	private Label purposeField;
	@FXML
	private Label uidField;
	@FXML
	private Label categoryField;
	@FXML
	private ImageView visitorImage;
	@FXML
	private ChoiceBox paperSize;
	@FXML
	private Button printButton;
	@FXML
	private Button cancelButton;
	static boolean insertedInDB=false;
	// Event Listener on Button[#printButton].onAction
	@FXML
	public void printCard(ActionEvent event){
		// TODO Autogenerated
		//Insert in db  if variable false and then set variable true
		//Generate UID and Print in any case of variable
			Visitor tempVisitor;
			CheckIn ch = new CheckIn();
			tempVisitor=ch.alreadyInserted(currentVisitor);
			if(tempVisitor==null){
			currentVisitor=ch.insertNewUser(currentVisitor);
				if(currentVisitor==null || currentVisitor.getuID()==0){
					System.out.println("DB Error!");
				}
				else{
					uidField.setText("UID : "+ String.format("%09d", currentVisitor.getuID()));
					try{
						Files.copy(Paths.get("temp.jpg"), Paths.get(Main.IMGDB+"/"+currentVisitor.getuID()+".jpg"), StandardCopyOption.REPLACE_EXISTING);
					}
					catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
			else{
				uidField.setText("UID : "+ String.format("%09d", tempVisitor.getuID()));
				try{
					Files.copy(Paths.get("temp.jpg"), Paths.get(Main.IMGDB+"/"+tempVisitor.getuID()+".jpg"), StandardCopyOption.REPLACE_EXISTING);
				}
				catch (Exception e) {
					// TODO: handle exception
				}
			}
			Utils.print(printableRegionRoot, (Paper)paperSize.getValue());
		
		cancelGoBack(new ActionEvent());
	}
	// Event Listener on Button[#cancelButton].onAction
	@FXML
	public void cancelGoBack(ActionEvent event) {
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        currentStage.close();
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		paperSize.getItems().addAll(Paper.A4,Paper.A6);
		paperSize.getSelectionModel().selectFirst();
//		printButton.requestFocus();
		// TODO Auto-generated method stub
		
	}
	public void initData(Visitor visitor){
		this.currentVisitor=visitor;
		this.nameField.setText(visitor.getName());
		this.contactField.setText(visitor.getContact());
		try{
	        File file = new File("temp.jpg");
	        Image image = new Image(file.toURI().toString());
			this.visitorImage.setImage(image);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.now();
		LocalDate dob  = LocalDate.parse(visitor.getDateOfBirth(),dtf);
		if(ChronoUnit.YEARS.between(dob,localDate)!=0)
			ageField.setText(ChronoUnit.YEARS.between(dob,localDate) + " years");
		else if(ChronoUnit.MONTHS.between(dob,localDate)!=0)
			ageField.setText(ChronoUnit.MONTHS.between(dob,localDate) + " months");
		else

			ageField.setText(ChronoUnit.DAYS.between(dob,localDate) + " days");
		purposeField.setText(visitor.getPurpose());
		uidField.setText("UID : "+visitor.getuID());
		String category = visitor.getCategory();
		switch (category) {
		case "C":
			category="Casual";			
			break;
		case "V":
			category="Vendor";
			break;
		case "O":
			category="Others";
			break;
		default:
			break;
		}
		categoryField.setText("Category : " + category);
		printButton.requestFocus();
	}
}
