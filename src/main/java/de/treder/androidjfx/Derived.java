package de.treder.androidjfx;

import android.content.Intent;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafxports.android.FXActivity;

/**
 * Created by Jerry on 24.09.2015.
 */
public class Derived extends FXActivity {
    Label wifiStatus;
    public void setLabel(Label text)
    {
        wifiStatus = text;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        Platform.runLater(() -> {
            if (scanResult != null) {
                this.wifiStatus.setText(scanResult.getContents());
            } else {
                this.wifiStatus.setText("ERROR");
            }
            // else continue with any other code you need in the method
        });
    }
}
