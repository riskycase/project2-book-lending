package com.riskycase.twoVandaH.ui

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.riskycase.twoVandaH.R
import com.squareup.picasso.Picasso
import java.lang.StringBuilder

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
        if(book.getCoverURL() != "")
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
        holder.content.setOnClickListener { view ->
            val db = FirebaseFirestore.getInstance()
            val currentUser = Firebase.auth.currentUser
            AlertDialog.Builder(view.context)
                .setTitle("Stop sharing this book?")
                .setMessage("${book.getTitle()} will not be available for others to read anymore")
                .setPositiveButton("YES") { dialog, _ ->
                    if (book.getReader() != "")
                        db.collection("users").document(book.getReader()).update("reading", "")
                    db.collection("books").document(book.getId()).delete()
                    dialog.dismiss()
                }
                .setNegativeButton("NO") { dialog, _ -> // Do nothing
                    dialog.dismiss()
                }
                .show()
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val content: LinearLayout = view.findViewById(R.id.book_info)
        val cover: ImageView = view.findViewById(R.id.book_cover)
        val title: TextView = view.findViewById(R.id.book_title)
        val author: TextView = view.findViewById(R.id.book_author)
        val publishDate: TextView = view.findViewById(R.id.book_year)
        val pages: TextView = view.findViewById(R.id.book_pages)
        val reader: TextView = view.findViewById(R.id.book_reader)
    }
}