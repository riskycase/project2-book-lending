package com.riskycase.twoVandaH

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


class AddBookActivity : AppCompatActivity() {

    private lateinit var title: String
    private lateinit var author: String
    private lateinit var pages: String
    private lateinit var publishDate: String
    private lateinit var ISBN: String
    private lateinit var coverURL: String
    private var valid: Boolean = false
    private lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        queue = Volley.newRequestQueue(this)

        setContentView(R.layout.activity_add_book_actvity)

        showInfo(false)

        val searchButton = findViewById<Button>(R.id.search_button)
        val addButton = findViewById<FloatingActionButton>(R.id.add_button)
        val ISBNTextView = findViewById<EditText>(R.id.ISBNEntry)

        searchButton.setOnClickListener {
            loadBook(true)
        }

        ISBNTextView.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> loadBook(true)
            }
            return@setOnEditorActionListener true
        }

        ISBNTextView.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if(text.length == 10 || text.length == 13)
                    loadBook(false)
            }
        }

        addButton.setOnClickListener { view ->
            if(valid){

                val auth = Firebase.auth

                FirebaseFirestore.getInstance().collection("books").whereEqualTo(
                    "owner",
                    auth.currentUser!!.email
                ).whereEqualTo("ISBN", ISBN).get().addOnCompleteListener{
                    val count = it.result!!.size()
                    if(count > 0) {
                        val builder = AlertDialog.Builder(this)

                        builder.setTitle("Identical book found")
                        builder.setMessage("We found $count other book${if (count == 1) "" else "s"} with the same ISBN number, do you wish to share multiple copies of $title ?")

                        builder.setPositiveButton("YES") { dialog, which -> // Do nothing but close the dialog
                            dialog.dismiss()
                            addBook()
                        }

                        builder.setNegativeButton(
                            "NO"
                        ) { dialog, which -> // Do nothing
                            dialog.dismiss()
                            finish()
                        }

                        builder.create().show()
                    }
                    else
                        addBook()
                }
            }
            else
                Snackbar.make(view, "Please enter a valid ISBN number", Snackbar.LENGTH_SHORT).setAnimationMode(
                    Snackbar.ANIMATION_MODE_SLIDE
                ).show()
        }
    }

    private fun addBook() {

        // Create a new book with info
        val book: MutableMap<String, String> = HashMap()
        book["name"] = title
        book["author"] = author
        book["publishDate"] = publishDate
        book["pages"] = pages
        book["ISBN"] = ISBN
        book["coverURL"] = coverURL
        book["owner"] = Firebase.auth.currentUser!!.email.toString()
        book["reader"] = ""

                FirebaseFirestore.getInstance().collection("books")
                    .document("${Firebase.auth.currentUser!!.email.toString()}-${System.currentTimeMillis()}")
                    .set(book)

                finish()
        Toast.makeText(applicationContext, "Added book $title successfully", Toast.LENGTH_SHORT).show()
    }

    private fun loadBook(flashError: Boolean) {
        Toast.makeText(applicationContext, "Querying database", Toast.LENGTH_SHORT).show()
        ISBN = findViewById<EditText>(R.id.ISBNEntry).text.toString()
        val stringRequest = StringRequest(Request.Method.GET,
            "https://openlibrary.org/isbn/${ISBN}.json",
            { response ->
                try {
                    showInfo(false)
                    val responseObject = JSONObject(response)
                    loadAuthor(responseObject.getJSONArray("authors"))
                    if (responseObject.has("covers")) {
                        coverURL = "https://covers.openlibrary.org/b/id/${
                            responseObject.getJSONArray(
                                "covers"
                            )[0]
                        }-M.jpg"
                        Picasso.get().load(coverURL).into(findViewById<ImageView>(R.id.book_cover))
                    } else {
                        findViewById<ImageView>(R.id.book_cover).setImageResource(R.drawable.ic_book_cover)
                        coverURL = ""
                    }
                    title = responseObject.getString("title")
                    pages =
                        if (responseObject.has("number_of_pages")) "${responseObject.getInt("number_of_pages")} pages"
                        else ""
                    publishDate = responseObject.getString("publish_date")
                    findViewById<TextView>(R.id.book_title).text = title
                    findViewById<TextView>(R.id.book_pages).text = pages
                    findViewById<TextView>(R.id.book_year).text = publishDate
                    showInfo(true)
                } catch (e: Exception) {
                    showInfo(false)
                }
            },
            {
                showInfo(false)
                if (flashError)
                    Toast.makeText(
                        applicationContext,
                        "We couldn't find the information for that ISBN",
                        Toast.LENGTH_SHORT
                    ).show()
            })
        queue.add(stringRequest)
    }

    private fun loadAuthor(jsonArray: JSONArray) {
        val stringRequest = StringRequest(Request.Method.GET, "https://openlibrary.org${
            jsonArray.getJSONObject(
                0
            ).getString("key")
        }.json",
            { response ->
                author = JSONObject(response).getString("name")
                findViewById<TextView>(R.id.book_author).text = author
            },
            {
                showInfo(false)
                throw Exception("Author not found")
            })
        queue.add(stringRequest)
    }

    private fun showInfo(show: Boolean) {
        findViewById<LinearLayout>(R.id.book_info).visibility = if (show) LinearLayout.VISIBLE else LinearLayout.INVISIBLE
        findViewById<FloatingActionButton>(R.id.add_button).visibility = if(show) FloatingActionButton.VISIBLE else FloatingActionButton.GONE
        valid = show
    }
}
