package com.riskycase.twoVandaH.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.riskycase.twoVandaH.R

/**
 * A fragment representing a list of Items.
 */
class MyBookFragment : Fragment() {

    private var columnCount = 1
    private lateinit var adapterOptions: FirestoreRecyclerOptions<Book>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapterOptions = FirestoreRecyclerOptions.Builder<Book>()
            .setQuery(FirebaseFirestore.getInstance().collection("books").whereEqualTo("owner", Firebase.auth.currentUser!!.email)) { snapshot ->
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

        arguments?.let {
            columnCount = it.getInt(AvailableBooksFragment.ARG_COLUMN_COUNT)
        }

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_books_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyBooksFirestoreAdapter(adapterOptions)
            }
        }
        return view
    }

    companion object {

        const val ARG_COLUMN_COUNT = "column-count"

        fun newInstance(columnCount: Int) =
                MyBookFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}