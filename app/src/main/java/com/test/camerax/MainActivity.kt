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
import com.test.camerax.extensions.hasPermissions
import com.test.camerax.extensions.requestRuntimePermissions
import com.test.camerax.ui.screens.CameraScreen
import com.test.camerax.ui.screens.PictureScreen
import com.test.camerax.ui.theme.CameraXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasPermissions(CAMERA_PERMISSIONS)) {
            requestRuntimePermissions(CAMERA_PERMISSIONS)
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



    companion object {
        val CAMERA_PERMISSIONS = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO
        )
    }
}