package com.turskyi.coffeewithcotlin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.text.NumberFormat

/**
 * This app displays an order form to order coffee.
 */
class MainActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {

    private var quantity: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        whipped_cream_checkbox.setOnCheckedChangeListener(this)
        chocolate_checkbox.setOnCheckedChangeListener(this)

        increment.setOnClickListener {
            increment()
        }

        decrement.setOnClickListener {
            decrement()
        }

        order.setOnClickListener {
            submitOrder()
        }
    }

    /**
     * This method is called when the order button is clicked.
     */
    private fun submitOrder() {
        //Get user's name
        val name = name_field.text.toString()

        // Figure out if the user wants whipped cream topping
        val hasWhippedCream = whipped_cream_checkbox.isChecked

        // Figure out if the user wants chocolate topping
        val hasChocolate = chocolate_checkbox.isChecked

        // Calculate the price
        val price: Int = calculatePrice(hasWhippedCream, hasChocolate)

        // Display the order summary on the screen
        val message = createOrderSummary(name, price, hasWhippedCream, hasChocolate)

        // Use an intent to launch an email app.
        // Send the order summary in the email body.
        val intent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts("mailto", "dmitriy.turskiy@gmail.com", ""))
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.order_summary_email_subject, name))
        intent.putExtra(Intent.EXTRA_TEXT, message)

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }

//        or like this:
//        try {
//            startActivity(intent)
//        } catch (ex: ActivityNotFoundException) {
//            Toast.makeText(this, "application not found", Toast.LENGTH_SHORT).show()
//        }
    }

    /**
     * Create summary of the order.
     *
     * @param name            on the order
     * @param price           of the order
     * @param addWhippedCream is whether or not to add whipped cream to the coffee
     * @param addChocolate    is whether or not to add chocolate to the coffee
     * @return text summary
     */
    private fun createOrderSummary(name: String, price: Int, addWhippedCream: Boolean, addChocolate: Boolean): String {
        var priceMessage = getString(R.string.order_summary_name, name)

        priceMessage += "\n" + getString(R.string.order_summary_quantity, quantity)

        if (addWhippedCream) {
            priceMessage += "\n" + getString(R.string.order_summary_whipped_cream)
        }
        if (addChocolate) {
            priceMessage += "\n" + getString(R.string.order_summary_chocolate)
        }
        priceMessage += "\n" + getString(
            R.string.order_summary_price,
            NumberFormat.getCurrencyInstance().format(price.toLong())
        )
        priceMessage += "\n" + getString(R.string.thank_you)
        return priceMessage
    }

    /**
     * This method is called when the plus button is clicked.
     */
    private fun increment() {
        if (quantity == 100) {
            return
        }
        quantity += 1
        displayQuantity(quantity)
        displayPrice()
    }

    /**
     * This method is called when the minus button is clicked.
     */
    private fun decrement() {
        if (quantity == 0) {
            return
        }
        quantity -= 1
        displayQuantity(quantity)
        displayPrice()
    }

    /**
     * This method displays the given quantity value on the screen.
     */
    private fun displayQuantity(numberOfCoffees: Int) {
        quantity_text_view.text = "$numberOfCoffees"
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        Toast.makeText(this, "here", Toast.LENGTH_LONG).show()
        displayPrice()
    }

    /**
     * This method displays the given price on the screen.
     */
    private fun displayPrice() {
        // Figure out if the user wants whipped cream topping
        val hasWhippedCream = whipped_cream_checkbox.isChecked

        // Figure out if the user wants chocolate topping
        val hasChocolate = chocolate_checkbox.isChecked

        // Calculate the price
        val price = calculatePrice(hasWhippedCream, hasChocolate)

        // Display the order summary on the screen
        val message = getString(
            R.string.order_summary_price,
            NumberFormat.getCurrencyInstance().format(price.toLong())
        )
        displayMessage(message)
    }

    /**
     * This method displays the given text on the screen.
     */
    private fun displayMessage(message: String) {
        price_text_view.text = message
    }

    /**
     * Calculates the price of the order.
     *
     * @param addWhippedCream is whether or not we should include whipped cream topping in the price
     * @param addChocolate    is whether or not we should include chocolate topping in the price
     * @return total price
     */
    private fun calculatePrice(addWhippedCream: Boolean, addChocolate: Boolean): Int {
        // First calculate the price of one cup of coffee
        var basePrice = 5

        // If the user wants whipped cream, add $1 per cup
        if (addWhippedCream) {
            basePrice += 1
        }

        // If the user wants chocolate, add $2 per cup
        if (addChocolate) {
            basePrice += 2
        }
        // Calculate the total order price by multiplying by the quantity
        return quantity * basePrice
    }
}
