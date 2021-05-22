package com.riskycase.twoVandaH

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class ReadingBookFragment : Fragment() {

    private lateinit var currentUser: FirebaseUser
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentUser = Firebase.auth.currentUser!!
        db = FirebaseFirestore.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_reading_book, container, false)

        db.collection("users").document(currentUser!!.email.toString()).get().addOnCompleteListener { it ->
            val currentlyReading = it.result?.get("reading").toString()
            db.collection("books").document(currentlyReading).get().addOnCompleteListener { snapshot ->
                db.collection("users").document(snapshot.result?.get("owner") as String).get().addOnCompleteListener { owner ->
                    container!!.findViewById<TextView>(R.id.book_owner).text = "Belongs to ${owner.result?.get("name")}"
                }

                container!!.findViewById<Button>(R.id.return_botton).setOnClickListener {
                    AlertDialog.Builder(view.context)
                        .setTitle("Return this book?")
                        .setMessage("Are you sure you want to return ${snapshot.result?.get("name") as CharSequence?}?")
                        .setPositiveButton("YES") { dialog, _ ->
                            db.collection("users").document(Firebase.auth.currentUser!!.email.toString()).set(
                                mapOf(Pair("reading", "")), SetOptions.merge())
                            db.collection("books").document(snapshot.result?.id.toString()).set(
                                mapOf(Pair("reader", "")), SetOptions.merge())
                            dialog.dismiss()
                        }
                        .setNegativeButton("NO") { dialog, _ -> // Do nothing
                            dialog.dismiss()
                        }
                        .show()
                }

                container!!.findViewById<TextView>(R.id.book_title).text =
                    snapshot.result?.get("name") as CharSequence?
                container!!.findViewById<TextView>(R.id.book_author).text =
                    snapshot.result?.get("author") as CharSequence?
                container!!.findViewById<TextView>(R.id.book_year).text =
                    snapshot.result?.get("publishDate") as CharSequence?
                container!!.findViewById<TextView>(R.id.book_pages).text =
                    snapshot.result?.get("pages") as CharSequence?
                if(snapshot.result?.get("coverURL") == "")
                    container.findViewById<ImageView>(R.id.book_cover).setImageResource(R.drawable.ic_book_cover)
                else
                    Picasso.get().load(snapshot.result?.get("coverURL") as String).into(container.findViewById<ImageView>(R.id.book_cover))
            }
        }

        // Inflate the layout for this fragment
        return view
    }
}