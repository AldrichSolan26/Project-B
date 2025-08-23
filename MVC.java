import java.util.Arrays;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

public class MVC extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        ObservableList<Book> booking = FXCollections.observableArrayList(Arrays.asList(Booklist.list));
        LibraryModel library = new LibraryModel(booking);

        UserModel model = new UserModel(library);
        LibraryController controller = new LibraryController(model);
        LibraryView view = new LibraryView(controller, model, stage);

        stage.setScene(view.getScene());
        stage.show();

    }

}