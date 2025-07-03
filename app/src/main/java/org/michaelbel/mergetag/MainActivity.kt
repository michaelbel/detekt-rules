package org.michaelbel.mergetag

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates.notNull
import org.michaelbel.mergetag.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity: ComponentActivity(R.layout.activity_main) {

    private var binding: ActivityMainBinding by notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}