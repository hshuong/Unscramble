package com.example.unscramble.ui.test

import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.getUnscrambledWord
import com.example.unscramble.ui.GameViewModel
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNotEquals

class GameViewModelTest {
    private val viewModel = GameViewModel()

    // Success Path
    @Test
    fun gameViewModel_CorrectWordGuessed_ScoreUpdatedAndErrorFlagUnset() {
        // lay UiState ra lan dau de lay tu da bi xao tron do game tao ra
        var currentGameUiState = viewModel.uiState.value
        // lay ra tu goc chua xao tron from tu bi xao tron trong UiState cua game
        val correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
        // gia lap tu nguoi dung nhap vao la tu dung
        viewModel.updateUserGuess(correctPlayerWord)
        // kiem tra tu gia lap nguoi dung nhap vao, se lam thay doi UiState
        viewModel.checkUserGuess()

        // UiState da thay doi nen phai doc lai UiState, do checkUserGuess goi den
        // updateGameState(score) nen uiState da thay doi: isGuessedWordWrong, score, tu
        currentGameUiState = viewModel.uiState.value
        // Assert that checkUserGuess() method updates isGuessedWordWrong is updated correctly.
        // Doan dung thi UiState la isGuessedWordWrong bang false
        assertFalse(currentGameUiState.isGuessedWordWrong)
        // Assert that score is updated correctly.
        // Mot lan doan dung duoc cong SCORE_AFTER_FIRST_CORRECT_ANSWER diem, la 20 diem
        // Do unit test nay chi gia lap nguoi dung doan 1 lan cho nen diem luon la 20 vi
        // dang gia lap truong hop doan dung
        assertEquals(SCORE_AFTER_FIRST_CORRECT_ANSWER, currentGameUiState.score)
    }

    // Error Path
    @Test
    fun gameViewModel_IncorrectGuess_ErrorFlagSet() {
        // Given an incorrect word as input, ko co trong WordsData.kt
        val incorrectPlayerWord = "and"

        // khong can doc UiState nhu o Success Path vi ko gia lap
        // tu dung do nguoi dung nhap vao
        viewModel.updateUserGuess(incorrectPlayerWord)
        viewModel.checkUserGuess()

        val currentGameUiState = viewModel.uiState.value
        // Assert that score is unchanged
        // Mot lan doan dung duoc cong SCORE_AFTER_FIRST_CORRECT_ANSWER diem, la 20 diem
        // Do unit test nay chi gia lap nguoi dung doan 1 lan cho nen diem luon la 0 vi
        // dang gia lap truong hop doan sai
        assertEquals(0, currentGameUiState.score)
        // Assert that checkUserGuess() method updates isGuessedWordWrong correctly
        // Doan sai thi UiState la isGuessedWordWrong bang true
        assertTrue(currentGameUiState.isGuessedWordWrong)
    }

    @Test
    fun gameViewModel_Initialization_FirstWordLoaded() {
        val gameUiState = viewModel.uiState.value
        val unScrambledWord = getUnscrambledWord(gameUiState.currentScrambledWord)

        // Assert that current word is scrambled.
        assertNotEquals(unScrambledWord, gameUiState.currentScrambledWord)
        // Assert that current word count is set to 1.
        assertTrue(gameUiState.currentWordCount == 1)
        // Assert that initially the score is 0.
        assertTrue(gameUiState.score == 0)
        // hoac danh gia bang assertEquals
        // assertEquals(0, gameUiState.score)
        // Assert that the wrong word guessed is false.
        assertFalse(gameUiState.isGuessedWordWrong)
        // Assert that game is not over.
        assertFalse(gameUiState.isGameOver)
    }

    @Test
    fun gameViewModel_AllWordsGuessed_UiStateUpdatedCorrectly() {
        var expectedScore = 0
        var currentGameUiState = viewModel.uiState.value
        var correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
        // Day kiem tra nguoi dung doan dung nen trong repeat, chi la cac lan doan dung,
        // ko co lan nao doan sai
        repeat(MAX_NO_OF_WORDS) {
            expectedScore += SCORE_INCREASE
            viewModel.updateUserGuess(correctPlayerWord)
            viewModel.checkUserGuess()
            currentGameUiState = viewModel.uiState.value
            correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
            // Assert that after each correct answer, score is updated correctly.
            assertEquals(expectedScore, currentGameUiState.score)
        }
        // Assert that after all questions are answered, the current word count is up-to-date.
        assertEquals(MAX_NO_OF_WORDS, currentGameUiState.currentWordCount)
        // Assert that after 10 questions are answered, the game is over.
        assertTrue(currentGameUiState.isGameOver)
    }

    @Test
    fun gameViewModel_WordSkipped_ScoreUnchangedAndWordCountIncreased() {
        var currentGameUiState = viewModel.uiState.value
        val correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()

        currentGameUiState = viewModel.uiState.value
        val lastWordCount = currentGameUiState.currentWordCount
        viewModel.skipWord()
        currentGameUiState = viewModel.uiState.value
        // Assert that score remains unchanged after word is skipped.
        assertEquals(SCORE_AFTER_FIRST_CORRECT_ANSWER, currentGameUiState.score)
        // Assert that word count is increased by 1 after word is skipped.
        assertEquals(lastWordCount + 1, currentGameUiState.currentWordCount)
    }

    companion object {
        private const val SCORE_AFTER_FIRST_CORRECT_ANSWER = SCORE_INCREASE
    }
}