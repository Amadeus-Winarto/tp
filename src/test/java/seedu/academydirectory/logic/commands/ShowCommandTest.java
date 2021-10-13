package seedu.academydirectory.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.academydirectory.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.academydirectory.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.academydirectory.testutil.Assert.assertThrows;
import static seedu.academydirectory.testutil.TypicalStudents.getTypicalAcademyDirectory;

import org.junit.jupiter.api.Test;

import seedu.academydirectory.commons.core.Messages;
import seedu.academydirectory.commons.core.index.Index;
import seedu.academydirectory.model.AcademyDirectory;
import seedu.academydirectory.model.Model;
import seedu.academydirectory.model.ModelManager;
import seedu.academydirectory.model.UserPrefs;

public class ShowCommandTest {

    private final Model model = new ModelManager(getTypicalAcademyDirectory(), new UserPrefs());
    private final String validAssessmentName1 = "RA1";
    private final String validAssessmentName2 = "MIDTERM";
    private final Integer validGrade = 10;

    @Test
    public void constructor_nullStudent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ShowCommand(null));
    }

    @Test
    void execute_showGrade_success() {
        ShowCommand showCommand = new ShowCommand(validAssessmentName1);
        String expectedMessage = ShowCommand.displayResult(model.getFilteredStudentList(), validAssessmentName1);
        Model expectedModel = new ModelManager(new AcademyDirectory(model.getAcademyDirectory()), new UserPrefs());
        assertCommandSuccess(showCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredStudentList().size() + 1);
        GradeCommand gradeCommand = new GradeCommand(outOfBoundIndex, validAssessmentName1, validGrade);

        assertCommandFailure(gradeCommand, model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final ShowCommand standardCommand = new ShowCommand(validAssessmentName1);

        // same values -> returns true
        ShowCommand commandWithSameValues =
                new ShowCommand(validAssessmentName1);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different assessment -> returns false
        assertFalse(standardCommand.equals(
                new ShowCommand(validAssessmentName2)));
    }
}
