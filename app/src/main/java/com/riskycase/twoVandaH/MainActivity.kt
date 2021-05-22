package com.riskycase.twoVandaH

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class MainActivity: AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            startActivity(Intent(this, AddBookActivity::class.java))
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_available_books, R.id.nav_my_books, R.id.nav_reading
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        db.collection("users").document(auth.currentUser!!.email.toString()).addSnapshotListener { value, _ ->
            if(value?.get("reading") != "") {
                navView.menu.findItem(R.id.nav_reading).isVisible = true
                fab.visibility = FloatingActionButton.GONE
                navController.popBackStack()
                navController.navigate(R.id.nav_reading)
                findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)
            }
            else {
                navView.menu.findItem(R.id.nav_reading).isVisible = false
                if (navView.menu.findItem(R.id.nav_reading).isChecked) {
                    fab.visibility = FloatingActionButton.VISIBLE
                    navController.popBackStack()
                    navController.navigate(R.id.nav_available_books)
                    findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)
                }
            }
        }

        if(auth.currentUser == null) {
            navView.getHeaderView(0).findViewById<TextView>(R.id.userName).text = getString(R.string.signed_out)
            navView.getHeaderView(0).findViewById<TextView>(R.id.userMail).text = ""
            navView.getHeaderView(0).findViewById<ImageView>(R.id.userIcon).setImageResource(R.drawable.default_user)
        }
        else {
            navView.getHeaderView(0).findViewById<TextView>(R.id.userName).text =
                auth.currentUser!!.displayName
            navView.getHeaderView(0).findViewById<TextView>(R.id.userMail).text =
                auth.currentUser!!.email
            Picasso.get().load(auth.currentUser!!.photoUrl).into(
                navView.getHeaderView(0).findViewById<ImageView>(
                    R.id.userIcon
                )
            )

        }

        navView.setNavigationItemSelectedListener{ item ->
            when (item.itemId) {
                R.id.nav_sign_out -> {
                    auth.signOut()
                    startActivity(Intent(this, LaunchActivity::class.java))
                    finish()
                }
                R.id.nav_available_books -> {
                    fab.visibility = FloatingActionButton.VISIBLE
                    navController.popBackStack()
                    navController.navigate(R.id.nav_available_books)
                    findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)
                }
                R.id.nav_my_books -> {
                    fab.visibility = FloatingActionButton.VISIBLE
                    Snackbar.make(navView, "Click on any book to stop sharing it", Snackbar.LENGTH_SHORT).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show()
                    navController.popBackStack()
                    navController.navigate(R.id.nav_my_books)
                    findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)
                }
                R.id.nav_reading -> {
                    fab.visibility = FloatingActionButton.GONE
                    navController.popBackStack()
                    navController.navigate(R.id.nav_reading)
                    findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)
                }
            }
            return@setNavigationItemSelectedListener true
        }

    }

    override fun onBackPressed() {
        if(findViewById<DrawerLayout>(R.id.drawer_layout).isDrawerOpen(GravityCompat.START))
            findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}