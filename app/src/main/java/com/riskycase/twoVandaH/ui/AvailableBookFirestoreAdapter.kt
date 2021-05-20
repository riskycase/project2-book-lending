package com.riskycase.twoVandaH.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.riskycase.twoVandaH.R
import com.squareup.picasso.Picasso


/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class AvailableBookFirestoreAdapter(options: FirestoreRecyclerOptions<Book>)
    : FirestoreRecyclerAdapter<Book, AvailableBookFirestoreAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_available_books, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, book: Book) {
        if(book.getCoverURL() == "")
            holder.cardView.setBackgroundResource(R.drawable.ic_book_cover)
        else
            Picasso.get().load(book.getCoverURL()).into(holder.cardBackground)
        holder.titleView.text = book.getTitle()

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view.findViewById(R.id.card)
        val cardBackground: ImageView = view.findViewById(R.id.card_background)
        val titleView: TextView = view.findViewById(R.id.book_title)
    }
}