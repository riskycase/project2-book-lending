package com.riskycase.twoVandaH.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.riskycase.twoVandaH.R
import com.squareup.picasso.Picasso

class AvailableBookFirestoreAdapter(options: FirestoreRecyclerOptions<Book>)
    : FirestoreRecyclerAdapter<Book, AvailableBookFirestoreAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_available_books, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, book: Book) {
        val layoutParams = holder.cardView.layoutParams
        layoutParams.height = (1.5*layoutParams.width).toInt()
        holder.cardView.layoutParams = layoutParams
        if(book.getCoverURL() == "")
            holder.cardBackground.setImageResource(R.drawable.ic_book_cover)
        else
            Picasso.get().load(book.getCoverURL()).into(holder.cardBackground)
        holder.titleView.text = book.getTitle()
        holder.cardView.setOnClickListener { view ->
            val db = FirebaseFirestore.getInstance()
            val currentUser = Firebase.auth.currentUser
            db.collection("users").document(currentUser!!.email.toString()).get().addOnCompleteListener { it ->
                val currentlyReading = it.result?.get("reading")
                if (currentlyReading.toString() == ""){
                    db.collection("users").document(book.getOwner()).get().addOnCompleteListener { owner ->
                        val ownerName = owner.result?.get("name")
                        AlertDialog.Builder(view.context)
                            .setTitle("Borrow this book?")
                            .setMessage("We will let the $ownerName know that you would like to read ${book.getTitle()}")
                            .setPositiveButton("YES") { dialog, _ ->
                                db.collection("users").document(Firebase.auth.currentUser!!.email.toString()).set(
                                    mapOf(Pair("reading", book.getId())), SetOptions.merge())
                                db.collection("books").document(book.getId()).set(
                                    mapOf(Pair("reader", Firebase.auth.currentUser!!.email.toString())), SetOptions.merge())
                                dialog.dismiss()
                            }
                            .setNegativeButton("NO") { dialog, _ -> // Do nothing
                                dialog.dismiss()
                            }
                            .show()
                    }
                }
                else {
                    AlertDialog.Builder(view.context)
                        .setTitle("Can't borrow this book")
                        .setMessage("You have already borrowed a book, return that book to read this one")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }.show()
                }
            }
        }

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view.findViewById(R.id.card)
        val cardBackground: ImageView = view.findViewById(R.id.card_background)
        val titleView: TextView = view.findViewById(R.id.book_title)
    }
}