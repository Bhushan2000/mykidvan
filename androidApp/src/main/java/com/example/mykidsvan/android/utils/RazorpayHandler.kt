package com.example.mykidsvan.android.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.authapp.presentation.viewmodel.AuthViewModel
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RazorpayHandler(
    private val activity: Activity,
    private val viewModel: AuthViewModel // <-- pass it in constructor
) : PaymentResultListener {

    companion object {
        // Lambda variables
        var onPaymentSuccessCallbackStatic: (String?) -> Unit = {}
        var onPaymentErrorCallbackStatic: (Int, String?) -> Unit = { _, _ -> }

        // Functions that trigger the above lambdas
        fun triggerPaymentSuccess(paymentId: String?) {
            onPaymentSuccessCallbackStatic.invoke(paymentId)
        }

        fun triggerPaymentError(code: Int, response: String?) {
            onPaymentErrorCallbackStatic.invoke(code, response)
        }
    }



    // Updated: Accepts error code and message
    var onPaymentSuccessCallback: (() -> Unit)? = null
    var onPaymentFailureCallback: ((code: Int, message: String?) -> Unit)? = null

    fun initiatePayment(amountInPaise: Int, userId: Int) {
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_BVJygtmA6ljXBB")

        val options = JSONObject().apply {
            put("name", "Assign Vehicle Owner")
            put("description", "Vehicle Owner Request Fee")
            put("currency", "INR")
            put("amount", amountInPaise.toString()) // Always pass string value

            val prefill = JSONObject().apply {
                put("email", "user@example.com")
                put("contact", "9999999999")
            }
            put("prefill", prefill)
        }

        // Razorpay success callback
        onPaymentSuccessCallbackStatic = {
            onPaymentSuccessCallback?.invoke()
        }

        onPaymentErrorCallbackStatic = { code, message ->
            onPaymentFailureCallback?.invoke(code, message)
        }

        checkout.open(activity, options)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        razorpayPaymentID?.let {

            val currentDate = LocalDate.now()
            val paymentDate = currentDate.format(DateTimeFormatter.ISO_DATE)
            val expireDate = currentDate.plusYears(1).format(DateTimeFormatter.ISO_DATE)

            val userId = viewModel.userId.value

            userId?.let { it1 ->
                viewModel.updatePaymentStatus(
                    id = it1.toInt(),
                    transactionId = it,
                    amount = "500.00",
                    paymentStatus = "Paid",
                    expireDate = expireDate,
                    paymentDate = paymentDate,
                    assignStatus = "Assigned",
                    assignDate = paymentDate
                )
            }

            onPaymentSuccessCallback?.invoke()
        }
    }

    override fun onPaymentError(code: Int, response: String?) {
        onPaymentFailureCallback?.invoke(code, response)
        Toast.makeText(activity, "Payment Failed: $response", Toast.LENGTH_LONG).show()
    }
}
