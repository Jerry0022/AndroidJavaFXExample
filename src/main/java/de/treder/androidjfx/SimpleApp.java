package de.treder.androidjfx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class SimpleApp extends Application {
    private final String[] days = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        final Random random = new Random();
        Button btn = new Button();
        btn.setText("Guess a day");
        final Label answer = new Label("no input received");
        answer.setTranslateY(50);
        btn.setOnAction(event -> {
            int day = random.nextInt(7);
            answer.setText(days[day]);
        });
        Label wifiStatus = new Label("unknown");
        wifiStatus.setTranslateY(100);

        StackPane root = new javafx.scene.layout.StackPane();
        root.getChildren().addAll(btn, answer, wifiStatus);

        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, screen.getWidth(), screen.getHeight());

        primaryStage.setTitle("JavaFX Porting");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Listen to wifi status
        Derived.getInstance().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Platform.runLater(() -> {
                    NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    // TODO Improve
                    String text = "Wifi is " + (info.isConnected() ? "on!" : "off!");
                    if (!wifiStatus.getText().equalsIgnoreCase(text)) {
                        wifiStatus.setText(text);
                        ScaleTransition trans = new ScaleTransition(Duration.seconds(1));
                        trans.setFromX(0.3);
                        trans.setFromY(0.3);
                        trans.setToX(1.0);
                        trans.setToY(1.0);
                        trans.setNode(wifiStatus);
                        trans.play();
                    }
                });
            }
        }, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        // Initial update
        wifiStatus.setText("Wifi is " + (isConnected() ? "on!" : "off!"));

        ((Derived) Derived.getInstance()).setLabel(wifiStatus);
        btn.setOnAction(event -> {
            Derived.getInstance().runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(Derived.getInstance(), "Start Scanner", Toast.LENGTH_LONG).show();
                    IntentIntegrator integrator = new IntentIntegrator(Derived.getInstance());
                    integrator.initiateScan();
                }
            });

        });

        /*
        Log.turnAllOff();
        de.mixedfx.network.ConnectivityManager network = new de.mixedfx.network.ConnectivityManager(new User() {
            private String id;
            {
                id = UUID.randomUUID().toString();
            }

            @Override
            public void mergeMe(User user) {
            }

            @Override
            public void setMeUp() {
            }

            @Override
            public Object getIdentifier() {
                return this.id;
            }
        });

        network.otherUsers.addListener(new ListChangeListener<User>() {
            @Override
            public void onChanged(Change c) {
                while(c.next())
                {
                    if(c.wasAdded())
                        wifiStatus.setText((String) ((User) c.getAddedSubList().get(0)).getIdentifier());
                }
            }
        });

        network.start();
        */
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) Derived.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo.isConnected();
    }
}