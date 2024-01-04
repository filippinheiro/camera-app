package com.test.camerax

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.test.camerax.extensions.getParcelableSafe
import com.test.camerax.extensions.hasCameraAndAudioPermissions
import com.test.camerax.extensions.requestCameraAndAudioRuntimePermissions
import com.test.camerax.ui.screens.CameraScreen
import com.test.camerax.ui.screens.PictureScreen
import com.test.camerax.ui.theme.CameraXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasCameraAndAudioPermissions()) {
            requestCameraAndAudioRuntimePermissions()
        }

        setContent {
            CameraXTheme {
                NavHost(
                    navController = rememberNavController(),
                    startDestination = "camera"
                ) {
                    composable("camera") {
                        CameraScreen()
                    }

                    composable(
                        "picture/{image}",
                        arguments = listOf(
                            navArgument("image") {
                                type = NavType.ParcelableType(Bitmap::class.java)
                            }
                        )
                    ) {
                        PictureScreen(
                            image = it.arguments?.getParcelableSafe<Bitmap>("image")!!
                        )
                    }
                }
            }
        }
    }
}