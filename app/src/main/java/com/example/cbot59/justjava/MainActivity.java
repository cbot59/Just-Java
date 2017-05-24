package com.example.cbot59.justjava;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {
    // Initializie quantity to one, increment/decrement works well
    int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * This method is called when the order button is clicked.
     */
    public void submitOrder(View view) {
        // Figure out if the user wants whipped cream topping
        CheckBox whippedCreamCheckBox = (CheckBox) findViewById(R.id.whipped_cream_checkbox);
        boolean hasWhippedCream = whippedCreamCheckBox.isChecked();

        // Figure out if the user wants whipped cream topping
        CheckBox chocolateCheckBox = (CheckBox) findViewById(R.id.chocolate_checkbox);
        boolean hasChocolate = chocolateCheckBox.isChecked();

        //Log.i("MainActivity", "Has whipped cream? " + hasWhippedCream);

        EditText nameTxt = (EditText) findViewById(R.id.name_text);
        String name = nameTxt.getText().toString();
        //Log.v("MainActivity", "Name --> " + name);

        // Calculate the price
        int price = calculatePrice(hasWhippedCream, hasChocolate);

        // Create order summary
        String priceMessage = createOrderSummary(price, hasWhippedCream, hasChocolate, name);

        // Sending order summary as an intent to email apps
        String emailSubject = getResources().getString(R.string.email_subject, name);
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        intent.putExtra(Intent.EXTRA_TEXT, priceMessage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

        // Display the order summary on the screen
//        displayMessage(priceMessage);
    }

    /**
     * This method displays the given quantity value on the screen.
     */
    private void display(int number) {
        TextView quantityTextView = (TextView) findViewById(R.id.quantity_text_view);
        quantityTextView.setText(Integer.toString(number));
    }

    /**
     * This method is called when the plus button is clicked.
     */
    public void increment(View view) {
        if (quantity == 100) {
            // Shows an error messages as a toast
            Toast.makeText(this, "You cannot have more than 100 coffees", Toast.LENGTH_SHORT).show();
            // Exit this method early because there's nothing left to to do;
            return;
        }
        quantity = quantity + 1;
        display(quantity);
    }

    /**
     * This method is called when the minus button is clicked.
     */
    public void decrement(View view) {
        if (quantity == 1) {
            // Shows an error messages as a toast
            Toast.makeText(this, "You cannot have less than 1 coffee", Toast.LENGTH_SHORT).show();
            // Exit this method early because there's nothing left to to do;
            return;
        }
        quantity = quantity - 1;
        display(quantity);
    }

    /**
     * Calculates the price of the order
     *
     * @param whippedCream is whether or not the user wants whipped cream topping
     * @param chocolate    is whether or not the user wants chocolate topping
     * @return total price
     */
    private int calculatePrice(boolean whippedCream, boolean chocolate) {
        // Price of 1 cup of coffee
        int basePrice = 5;

        // Add $1 if the user wants whipped cream
        if (whippedCream) {
            basePrice += 1;
        }

        // Add $2 if the user wants chocolate
        if (chocolate) {
            basePrice += 2;
        }

        // Calculate the total order price by multiplying quantity
        return quantity * basePrice;
    }

    /**
     * Create summary of the order.
     *
     * @param price        of the order
     * @param whippedCream is whether or not the user wants whipped cream topping
     * @param chocolate    is whether or not the user wants chocolate
     * @param name         of the customer
     * @return text summary
     */
    private String createOrderSummary(int price, boolean whippedCream, boolean chocolate, String name) {
        String currency = NumberFormat.getCurrencyInstance().format(price);
        String priceMessage = getResources().getString(R.string.order_summary_header);
        priceMessage += "\n" + getResources().getString(R.string.order_summary_name, name) ;
        priceMessage += "\n" + getResources().getString(R.string.order_summary_whipCream, translateBoolean(whippedCream));
        priceMessage += "\n" + getResources().getString(R.string.order_summary_chocolate, translateBoolean(chocolate));
        priceMessage += "\n" + getResources().getString(R.string.order_summary_quantity, quantity);
        priceMessage += "\n" + getResources().getString(R.string.order_summary_total, currency);
        priceMessage += "\n" + getResources().getString(R.string.thanks);
        return priceMessage;
    }

    /**
     * Function to convert boolean values to localized true/false strings
     * @author Balázs Lengyák on StackOverflow
     * @param b boolean to be translated
     * @return String containing localized true/false
     */
    private String translateBoolean(boolean b) {
        String trueFalse = Boolean.toString(b).toUpperCase(); // true / false reserved for keyword, make it UPPERCASE
        String packageName = getPackageName();
        int resId = getResources().getIdentifier(trueFalse, "string", packageName);

        // this is to make sure that we got a valid resId else getString() will force close
        if (resId > 0) {
            return getString(resId);
        }
        else {
            return trueFalse; // provide a fallback 'true/false' if translation is not available
        }
    }
}
