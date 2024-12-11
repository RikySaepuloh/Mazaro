package id.esaku.mazaro

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebViewApp()
        }
    }
}

@Composable
fun WebViewApp() {
    SplashScreen()
}

@Composable
fun SplashScreen() {
    // Variabel untuk kontrol animasi splash
    var isSplashFinished by remember { mutableStateOf(false) }

    // Menggunakan Lottie untuk animasi splash
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.launcher)) // Ganti dengan path animasi Lottie Anda
    val progress by animateLottieCompositionAsState(composition)

    // Setelah animasi selesai, pindah ke halaman utama
    LaunchedEffect(progress) {
        if (progress == 1f) {
            isSplashFinished = true
        }
    }

    if (!isSplashFinished) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (composition != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Menampilkan animasi Lottie
                    Text(
                        text = "Mazaro", // Ganti dengan nama aplikasi Anda
                        style = TextStyle(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00A69C)
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LottieAnimation(
                        composition = composition,
                        progress = progress,
                        modifier = Modifier.size(400.dp) // Sesuaikan ukuran animasi
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Menampilkan nama aplikasi setelah animasi berhasil dimuat

                    // Menampilkan nama aplikasi dalam dua baris
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Masjid Al-Azhar", // Ganti dengan nama aplikasi Anda
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00A69C)
                            )
                        )
                        Text(
                            text = "Podomoro", // Ganti dengan nama aplikasi Anda
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00A69C)
                            )
                        )
                    }
                }
            } else {
                // Opsi jika animasi belum siap
            }
        }
    } else {
        WebViewScreen(url = "https://esaku.id/mobile/login")  // Halaman utama setelah splash screen
    }
}

@Composable
fun WebViewScreen(url: String) {
    var isLoading by remember { mutableStateOf(true) }
    var showExitDialog by remember { mutableStateOf(false) }
//    var initialLoad by remember { mutableStateOf(true) } // Variabel untuk mencegah muat ulang

    // Menangani back press
    val context = LocalContext.current
    val activity = LocalContext.current as Activity

    val webView = remember { WebView(context) }
    webView.clearCache(true)
    webView.clearHistory()
    val cookieManager = CookieManager.getInstance()
    cookieManager.setAcceptCookie(true) // Mengizinkan cookies
    cookieManager.setAcceptThirdPartyCookies(webView, true) // Mengizinkan cookies pihak ketiga
    webView.apply {
        settings.apply {
//                        javaScriptCanOpenWindowsAutomatically = true
            allowFileAccess = true
            javaScriptEnabled = true
            domStorageEnabled = true
            safeBrowsingEnabled = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
//                        loadWithOverviewMode = true
//                        useWideViewPort = true
//                        allowFileAccess = true
        }



        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                isLoading = true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                isLoading = false
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                isLoading = false
                view?.loadData(
                    "<html><body><h3>Halaman tidak dapat dimuat. Periksa koneksi internet Anda.</h3></body></html>",
                    "text/html", "UTF-8"
                )
                Toast.makeText(context, "Gagal memuat halaman: ${error?.description}", Toast.LENGTH_SHORT).show()
            }
        }


        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(message: ConsoleMessage?): Boolean {
                Log.d("WebViewConsole", "Message from JS: ${message?.message()}")
                return super.onConsoleMessage(message)
            }
        }
    }
//    webView.setLayerType(View.LAYER_TYPE_HARDWARE, null) // Gunakan GPU untuk rendering
//    webView.clearCache(true) // Bersihkan cache saat tidak diperlukan


    DisposableEffect(Unit) {
        onDispose {
            webView.destroy() // Membersihkan resource WebView
        }
    }

    // Tangani back action jika ada halaman sebelumnya di WebView
    BackHandler {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            showExitDialog = true // Jika tidak ada halaman sebelumnya, tampilkan dialog keluar
        }
    }

    // Menampilkan dialog konfirmasi keluar
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(text = "Konfirmasi Keluar") },
            text = { Text(text = "Apakah Anda yakin ingin keluar dari aplikasi?") },
            confirmButton = {
                Button(
                    onClick = {
                        activity.finish() // Menutup aplikasi
                    }
                ) {
                    Text("Ya")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showExitDialog = false }
                ) {
                    Text("Tidak")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                webView.apply {

//                    if (initialLoad) {
                        loadUrl(url) // Hanya memuat halaman sekali
//                        initialLoad = false
//                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isLoading) {
//        if (!isLoading) {
//            IconButton(
//                onClick = { webView.reload() },
                CircularProgressIndicator(modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp))
//            ) {
//                Icon(Icons.Default.Refresh, contentDescription = "Reload")
//            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WebViewApp()
}
