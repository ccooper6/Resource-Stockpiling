package seng201.team0.unittests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng201.team0.models.DifficultySelection;
import seng201.team0.services.DifficultySelectionService;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DifficultySelectionServiceTest {
    private DifficultySelectionService difficultySelectionServiceTest;

    @BeforeEach
    void setupTest() {
        difficultySelectionServiceTest = new DifficultySelectionService();
    }

    @Test
    void testDifficultySelection() {
        assertEquals("Easy", difficultySelectionServiceTest.getDifficultySelection());
        difficultySelectionServiceTest.setDifficultySelection("Medium");
        assertEquals("Medium", difficultySelectionServiceTest.getDifficultySelection());
    }
}
