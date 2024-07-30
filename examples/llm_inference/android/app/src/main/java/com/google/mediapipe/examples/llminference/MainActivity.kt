package com.google.mediapipe.examples.llminference

import WorkFromHomeScreen
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.mediapipe.examples.llminference.ui.theme.LLMInferenceTheme

const val START_SCREEN = "start_screen"
const val CHAT_SCREEN = "chat_screen"
const val WFH_SCREEN = "wfh_screen"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        if (!hasPermissions(this)) {
            Log.i("Main", "ABC No permission. Requesting permission")
//            requestStoragePermission()
//            startLocationPermissionRequest()

        } else {
            Log.i("Main", "ABC Permission granted")
        }
        //This request works
        requestPermissions(this)





        setContent {
            LLMInferenceTheme {
                Scaffold(
                    topBar = { AppBar() }
                ) { innerPadding ->
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background ,
                    ) {
                        val navController = rememberNavController()
                        var chatViewModel : ChatViewModel = viewModel(
                            factory = ChatViewModel.getFactory(LocalContext.current.applicationContext)
                        )
//                        val chatViewModel : ChatViewModel = ChatViewModel(ChatViewModel.getFactory())

                        NavHost(
                            navController = navController,
                            startDestination = START_SCREEN,

                        ) {
                            composable(START_SCREEN) {
                                LoadingRoute(
                                    onModelLoaded = {
                                        navController.navigate(CHAT_SCREEN) {
                                            popUpTo(START_SCREEN) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            }

                            composable(CHAT_SCREEN) {
                                ChatRoute(navController = navController, chatViewModel = chatViewModel)
                            }

                            composable(WFH_SCREEN) {
                                WorkFromHomeScreen(navController = navController, chatViewModel = chatViewModel)
                            }


                        }

                    }
                }
            }
        }
    }

    // TopAppBar is marked as experimental in Material 3
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppBar() {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
            )
//            Box(
//                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
//            ) {
//                Text(
//                    text = stringResource(R.string.disclaimer),
//                    textAlign = TextAlign.Center,
//                    style = MaterialTheme.typography.bodyMedium,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp)
//                )
//            }
        }
    }

    fun hasPermissions(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.i("VERSION", "CORRECT VERSION HAHA")
            Environment.isExternalStorageManager()
        } else {
            Log.i("VERSION", "INCORRECT VERSION HAHA")

            ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestPermissions(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:" + activity.packageName)
                activity.startActivityForResult(intent, REQUEST_CODE_MANAGE_STORAGE)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                activity.startActivityForResult(intent, REQUEST_CODE_MANAGE_STORAGE)
            }
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_READ_STORAGE)
        }

    }




    companion object {
        const val REQUEST_CODE_READ_STORAGE = 1001
        const val REQUEST_CODE_MANAGE_STORAGE = 1002
    }
}
