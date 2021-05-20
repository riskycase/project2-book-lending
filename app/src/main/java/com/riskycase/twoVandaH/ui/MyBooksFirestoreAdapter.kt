package com.riskycase.twoVandaH.ui

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.riskycase.twoVandaH.R
import com.squareup.picasso.Picasso

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyBooksFirestoreAdapter(options: FirestoreRecyclerOptions<Book>)
    : FirestoreRecyclerAdapter<Book, MyBooksFirestoreAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_my_books, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, book: Book) {
        if(book.getCoverURL() == "")
            holder.cover.setBackgroundResource(R.drawable.ic_book_cover)
        else
            Picasso.get().load(book.getCoverURL()).into(holder.cover)
        holder.title.text = book.getTitle()
        holder.author.text = book.getAuthor()
        holder.pages.text = book.getPages()
        holder.publishDate.text = book.getPublishDate()
        if(book.getReader() == "")
            holder.reader.setText(R.string.available_text)
        else
            FirebaseFirestore.getInstance().collection("users").document(book.getReader()).get().addOnSuccessListener {
                holder.reader.text = "With ${it.get("name")}"
            }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cover: ImageView = view.findViewById(R.id.book_cover)
        val title: TextView = view.findViewById(R.id.book_title)
        val author: TextView = view.findViewById(R.id.book_author)
        val publishDate: TextView = view.findViewById(R.id.book_year)
        val pages: TextView = view.findViewById(R.id.book_pages)
        val reader: TextView = view.findViewById(R.id.book_reader)
    }
}