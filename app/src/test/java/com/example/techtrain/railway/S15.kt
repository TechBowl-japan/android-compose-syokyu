package com.example.techtrain.railway

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.techtrain.railway.android.Book
import com.example.techtrain.railway.android.MainScreen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.reflect.full.functions
import kotlin.reflect.typeOf
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


@RunWith(AndroidJUnit4::class)
class S15 {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalTestApi::class)
    @get:Rule
    val composeTestRule = createComposeRule(testDispatcher)


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test() = runTest(testDispatcher) {
        val context = ApplicationProvider.getApplicationContext<Application>()

        val info = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_PERMISSIONS
        )
        val permissions = info.requestedPermissions?.toSet() ?: emptySet()
        assertTrue(
            permissions.containsAll(
                setOf(
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE
                )
            ), "AndroidManifestに必要なパーミッションが書かれていない"
        )

        val retrofitClass = try {
            Class.forName("retrofit2.Retrofit")
        } catch (_: ClassNotFoundException) {
            null
        }
        assertNotNull(retrofitClass, "Retrofitが依存関係に含まれていません。")
        val moshiConverterFactoryClass = try {
            Class.forName("retrofit2.converter.moshi.MoshiConverterFactory")
        } catch (_: ClassNotFoundException) {
            null
        }
        assertNotNull(
            moshiConverterFactoryClass,
            "Retrofit converter-moshiが依存関係に含まれていません。"
        )

        try {
            // BooksService クラスを取得
            val serviceClass =
                Class.forName("com.example.techtrain.railway.android.BooksService").kotlin
            assertNotNull(serviceClass, "BooksService クラスが存在しません。")

            // publicBooks メソッドが存在するかを確認
            val method = serviceClass.functions.find { it.name == "publicBooks" }
            assertNotNull(method, "BooksService に publicBooks メソッドが存在しません。")
            assertTrue(method.isSuspend, "publicBooksはsuspend関数になっていません。")
            assertEquals(
                typeOf<List<Book>>(),
                method.returnType,
                "publicBooksの戻り値がList<Book>ではありません。"
            )
        } catch (e: Exception) {
            fail("com.example.techtrain.railway.android.BooksService クラスが見つかりません。")
        }

        composeTestRule.setContent {
            MaterialTheme {
                MainScreen()
            }
        }

        runCurrent()

        composeTestRule.waitUntil(5000) {
            val text = composeTestRule.onAllNodes(hasTestTag("Text")).fetchSemanticsNodes()
                .first().config[SemanticsProperties.Text].first().toString()
            val bookList =
                Regex("""Book\(id=([^,]+), title=([^,]+), url=([^,]+), detail=([^,]+), review=([^,]+), reviewer=([^\)]+)\)""")
                    .findAll(text)
                    .map { match ->
                        Book(
                            id = match.groupValues[1],
                            title = match.groupValues[2],
                            url = match.groupValues[3],
                            detail = match.groupValues[4],
                            review = match.groupValues[5],
                            reviewer = match.groupValues[6]
                        )
                    }.toList()
            bookList.isNotEmpty()
        }
    }
}
