package client;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.collections.ObservableList;

public class ClientApplication extends Application {

    private ArrayList<Thread> threads;
    private String clientName;
    private String clientTo;
    private ObservableList<String> chatLogList;

    private void setClientName(String cliName) {
        this.clientName = cliName;
    }

    private void setClientTo(String cliTo) {
        this.clientTo = cliTo;
    }

    private String getClientName() {
        return this.clientName;
    }

    private String getClientTo() {
        return this.clientTo;
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO Auto-generated method stub
        threads = new ArrayList<Thread>();
        primaryStage.setTitle("JavaFX Chat Client");
        primaryStage.setScene(LogintApp(primaryStage));
        primaryStage.show();
    }

    public Scene LogintApp(Stage primaryStage) {
        /* Make the root pane and set properties */
        GridPane rootPane = new GridPane();
        rootPane.setPadding(new Insets(20));
        rootPane.setVgap(10);
        rootPane.setHgap(10);
        rootPane.setAlignment(Pos.CENTER);

        /* Make the text fields and set properties */
        TextField nameField = new TextField();


        /* Make the labels and set properties */
        Label nameLabel = new Label("Name ");

        Label errorLabel = new Label();

        /* Make the button and its handler */
        Button EnterToTheChat = new Button("Enter");
        EnterToTheChat.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent Event) {

                /* Instantiate the client class and start it's thread */
                Client client;
                try {
                    setClientName(nameField.getText());
                    client = new Client(nameField.getText());
                    Thread clientThread = new Thread(client);
                    clientThread.setDaemon(true); //Daemon threads means a program finishes but the thread is still running but the jv_m can exist
                    clientThread.start();
                    threads.add(clientThread);

                    /* Change the scene of the primaryStage */
                    primaryStage.close();
                    primaryStage.setScene(ClientChat(client));
                    primaryStage.show();
                } catch (ConnectException e) {
                    errorLabel.setTextFill(Color.RED);
                    errorLabel.setText("The server is not started");
                } catch (Exception e) {
                    errorLabel.setTextFill(Color.RED);
                    errorLabel.setText("please enter your name");
                }

            }
        });

        // Add the components to the root pane arguments are (Node, Column Number, Row Number)
        rootPane.add(nameField, 0, 0);
        rootPane.add(nameLabel, 1, 0);

        rootPane.add(EnterToTheChat, 0, 3, 2, 1);
        rootPane.add(errorLabel, 0, 4);
        /* Make the Scene and return it */
        return new Scene(rootPane, 400, 400);
    }

    public Scene ClientChat(Client client) {
        // Make the root pane and set properties

        GridPane rootPane = new GridPane();
        rootPane.setPadding(new Insets(20));
        rootPane.setAlignment(Pos.CENTER);
        rootPane.setHgap(10);
        rootPane.setVgap(10);

        // Make the Chat's listView and set it's source to the Client's chatLog ArrayList
        ListView<String> chatListView = new ListView<String>();
        chatListView.setItems(client.chatLog);
        for (String each : client.chatLog) {
            System.out.println(each);
        }
        // Make the chat text box and set it's action to send a message to the server
        TextField chatTextField = new TextField();

        TextField chatTo = new TextField();
        chatTextField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                client.writeToServer(chatTextField.getText() + ";" + chatTo.getText());
                System.out.println(chatTextField.getText() + ";" + chatTo.getText());
                setClientTo(chatTo.getText());
                //for(int i = 0; i<=chatListView.getText().length; i++)
//                                chatLogList(chatListView.getSelectionModel().getSelectedItems());
                chatLogList = chatListView.getSelectionModel().getSelectedItems();

                chatTextField.clear();
            }
        });

        /* Add the components to the root pane */
        rootPane.add(chatListView, 0, 0);
        rootPane.add(chatTextField, 0, 1);
        rootPane.add(chatTo, 1, 1);

        /* Make and return the scene */
        return new Scene(rootPane, 400, 400);

    }
}
