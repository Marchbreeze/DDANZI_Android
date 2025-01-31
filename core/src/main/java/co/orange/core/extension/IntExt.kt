package co.orange.core.extension

import android.content.Context
import android.util.TypedValue
import java.text.DecimalFormat

fun Int.dpToPx(context: Context): Int {
    val metrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), metrics).toInt()
}

fun Int.setPriceForm(): String {
    val decimal = DecimalFormat("#,###")
    return decimal.format(this) + "원"
}

fun Int.setOverThousand(): String =
    if (this < 1000) {
        this.toString()
    } else {
        "999+"
    }
