package com.example.techtrain.railway

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.techtrain.railway.android.MainScreen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class S13 {

    private val testScope = TestScope()

    @OptIn(ExperimentalTestApi::class)
    @get:Rule
    val composeTestRule = createComposeRule(testScope.backgroundScope.coroutineContext)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test() = runTest {
        composeTestRule.setContent {
            MaterialTheme {
                MainScreen()
            }
        }

        composeTestRule.onNode(hasText("1"))
            .assertExists("画面にText Composableがありません。")

        testScope.advanceTimeBy(5000L)
        composeTestRule.awaitIdle()

        composeTestRule.onNode(hasText("5"))
            .assertExists("5秒後にText Composableに5が表示されていません。")

        testScope.advanceTimeBy(5000L)
        composeTestRule.awaitIdle()

        composeTestRule.onNode(hasText("10"))
            .assertExists("10秒後にText Composableに10が表示されていません。")
    }
}
