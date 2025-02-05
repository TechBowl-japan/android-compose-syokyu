package com.example.techtrain.railway

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasInsertTextAtCursorAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.DialogNavigator
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.techtrain.railway.android.Root
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@RunWith(AndroidJUnit4::class)
class S12 {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test() {
        val navController = NavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        navController.navigatorProvider.addNavigator(DialogNavigator())
        composeTestRule.setContent {
            MaterialTheme {
                Root(navController)
            }
        }

        composeTestRule.onNode(hasInsertTextAtCursorAction())
            .assertExists("TextField Composableが定義されていないか複数定義されています。")
            .performTextInput("TechTrain")

        composeTestRule.onNode(hasClickAction() and !hasInsertTextAtCursorAction())
            .assertExists("Button Composableが定義されていないか複数定義されています。")
            .performClick()

        assertNotEquals(
            "firstScreen",
            navController.currentDestination?.route,
            "ボタンクリック後に別画面に遷移していません。"
        )
        assertEquals(
            1,
            navController.currentDestination?.arguments?.size,
            "新しいページに引数が無いもしくは2個以上の引数が定義されています。"
        )

        composeTestRule.onNode(hasText("TechTrain"))
            .assertExists("Text ComposableにTextFieldで入力された文字列が設定されていません。")

        composeTestRule.onNode(hasInsertTextAtCursorAction()).assertDoesNotExist()
    }
}
