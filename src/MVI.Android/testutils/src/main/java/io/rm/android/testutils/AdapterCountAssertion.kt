package io.rm.android.testutils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class AdapterCountAssertion(private val count: Int) : ViewAssertion {
    override fun check(view: View, noViewFoundException: NoMatchingViewException?) {
        noViewFoundException?.let {
            throw it
        }

        assertTrue(view is RecyclerView)
        assertEquals(count, (view as RecyclerView).adapter?.itemCount)
    }
}