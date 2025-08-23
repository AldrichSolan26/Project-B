import java.util.Arrays;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

// MVC STRUCTURE
// MODEL (LibraryModel): Represents the data
// VIEW (): Handles the layout and presentation
// CONTROLLER: Connects the View and Model, handling
// logic and user interactions.

public class MVC extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    //to comment
    @Override
    public void start(Stage stage) throws Exception {
        ObservableList<Book> booking = FXCollections.observableArrayList(Arrays.asList(Booklist.list));
        Library library = new Library(booking); 
        AppModel model = new AppModel(library);
        AppController controller = new AppController(model);
        AppView view = new AppView(controller, model, stage);

        stage.setScene(view.getScene());
        stage.show();

    }

}