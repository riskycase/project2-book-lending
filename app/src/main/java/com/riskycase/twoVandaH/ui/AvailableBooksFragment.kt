package com.riskycase.twoVandaH.ui

import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.riskycase.twoVandaH.R
import kotlin.math.max
import kotlin.math.roundToInt


/**
 * A fragment representing a list of Items.
 */
class AvailableBooksFragment : Fragment() {

    private lateinit var adapterOptions: FirestoreRecyclerOptions<Book>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapterOptions = FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(FirebaseFirestore.getInstance().collection("books")
                    .whereNotEqualTo("owner", Firebase.auth.currentUser!!.email)
                    .whereEqualTo("reader", "")
                ) { snapshot ->
                    Book(snapshot.id,
                            snapshot["name"] as String,
                            snapshot["author"] as String,
                            snapshot["pages"] as String,
                            snapshot["publishDate"] as String,
                            snapshot["ISBN"] as String,
                            snapshot["coverURL"] as String,
                            snapshot["owner"] as String,
                            snapshot["reader"] as String)
                }.setLifecycleOwner(this).build()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_available_books_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = GridLayoutManager(context, max((resources.configuration.screenWidthDp/188.0).roundToInt(), 1))
                adapter = AvailableBookFirestoreAdapter(adapterOptions)
            }
        }
        return view
    }
}