package com.example.newsappeim


import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.newsappeim.databinding.ActivityMainBackupBinding
import com.example.newsappeim.repositories.MovieRepository
import com.example.newsappeim.services.MovieService
import com.example.newsappeim.viewmodels.MainViewModel
import com.example.newsappeim.viewmodels.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

//class MainActivity : AppCompatActivity() {
//
//    private val TAG = "MainActivity"
//
//    private lateinit var binding: ActivityMainBackupBinding
//    private lateinit var viewModel: MainViewModel
//
//    private val retrofitService = MovieService.getInstance()
//
//    private val adapter = MainAdapter()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//
////        setContentView(R.layout.activity_main)
////
////        binding = ActivityMainBackupBinding.inflate(layoutInflater)
////        setContentView(binding.root)
////        viewModel = ViewModelProvider(this, ViewModelFactory(MovieRepository(retrofitService))).get(MainViewModel::class.java)
////
////
////        binding.recyclerview.adapter = adapter
////        viewModel.movieList.observe(this) {
////            Log.d(TAG, "onCreate: $it")
////            adapter.setMovieList(it)
////        }
////        viewModel.errorMessage.observe(this, Observer {
////        })
////        viewModel.getAllMovies()
//    }
//}


class MainActivityBackup : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBackupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBackupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}