package com.riskycase.twoVandaH

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class MainActivity: AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        // Initialize Firebase Auth
        auth = Firebase.auth

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
                R.id.nav_available_books, R.id.nav_my_books
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        db = FirebaseFirestore.getInstance()

        auth.addAuthStateListener { fba ->
            if(fba.currentUser == null) {
                navView.menu.findItem(R.id.nav_sign_out).isVisible = false
                navView.menu.findItem(R.id.nav_sign_in).isVisible = true
                navView.getHeaderView(0).findViewById<TextView>(R.id.userName).text = getString(R.string.signed_out)
                navView.getHeaderView(0).findViewById<TextView>(R.id.userMail).text = ""
                navView.getHeaderView(0).findViewById<ImageView>(R.id.userIcon).setImageResource(R.drawable.default_user)
                fab.visibility = FloatingActionButton.GONE
            }
            else {
                navView.menu.findItem(R.id.nav_sign_out).isVisible = true
                navView.menu.findItem(R.id.nav_sign_in).isVisible = false
                navView.getHeaderView(0).findViewById<TextView>(R.id.userName).text = auth.currentUser!!.displayName
                navView.getHeaderView(0).findViewById<TextView>(R.id.userMail).text = auth.currentUser!!.email
                fab.visibility = FloatingActionButton.VISIBLE
                Picasso.get().load(auth.currentUser!!.photoUrl).into(
                    navView.getHeaderView(0).findViewById<ImageView>(
                        R.id.userIcon
                    )
                )
                // Create a new user with a name and email
                val user: MutableMap<String, String> = HashMap()
                user["name"] = auth.currentUser!!.displayName.toString()
                user["email"] = auth.currentUser!!.email.toString()

                db.collection("users")
                    .document(auth.currentUser!!.email.toString())
                    .set(user)
            }
        }

        navView.setNavigationItemSelectedListener{ item ->
            if(item.itemId == R.id.nav_sign_out)
                auth.signOut()
            else if (item.itemId == R.id.nav_sign_in)
                signIn()
            else if(item.itemId == R.id.nav_available_books){
                navController.popBackStack()
                navController.navigate(R.id.nav_available_books)
                findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)
            }
            else if(item.itemId == R.id.nav_my_books){
                navController.popBackStack()
                navController.navigate(R.id.nav_my_books)
                findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}