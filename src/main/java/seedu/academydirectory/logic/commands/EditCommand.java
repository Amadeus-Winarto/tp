package seedu.academydirectory.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.academydirectory.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.academydirectory.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.academydirectory.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.academydirectory.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.academydirectory.logic.parser.CliSyntax.PREFIX_TELEGRAM;
import static seedu.academydirectory.model.Model.PREDICATE_SHOW_ALL_STUDENTS;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.academydirectory.commons.core.Messages;
import seedu.academydirectory.commons.core.index.Index;
import seedu.academydirectory.commons.util.CollectionUtil;
import seedu.academydirectory.logic.commands.exceptions.CommandException;
import seedu.academydirectory.model.Model;
import seedu.academydirectory.model.student.Assessment;
import seedu.academydirectory.model.student.Email;
import seedu.academydirectory.model.student.Name;
import seedu.academydirectory.model.student.Phone;
import seedu.academydirectory.model.student.Student;
import seedu.academydirectory.model.student.StudioRecord;
import seedu.academydirectory.model.student.Telegram;
import seedu.academydirectory.model.tag.Tag;

/**
 * Edits the details of an existing student in the academy directory.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String HELP_MESSAGE = "### Editing a student : `edit`\n"
            + "\n"
            + "Tutors will be able to edit their tutees.\n"
            + "\n"
            + "Format: `edit INDEX [n/NAME] [e/EMAIL] [t/TELE_HANDLE] [p/PHONE_NUMBER]`\n"
            + "\n"
            + "* Edits the tutee at the specified `INDEX`. The index refers to the index number shown "
            + "in the displayed student list. The index **must be a positive integer** 1, 2, 3, …\u200B\n"
            + "* At least one of the optional fields must be provided.\n"
            + "* Existing values will be updated to the input values.\n"
            + "\n"
            + "Examples:\n"
            + "* `edit 1 p/91234567`  Edits the phone number "
            + "of the 1st student to be `91234567` and `e0425205@u.nus.edu` respectively.\n"
            + "* `edit 2 n/Aaron Tan`  Edits the name of the 2nd student to be Aaron Tan.\n";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the student identified "
            + "by the index number used in the displayed student list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_TELEGRAM + "TELEGRAM] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com"
            + PREFIX_TELEGRAM + "@johndoe";

    public static final String MESSAGE_EDIT_STUDENT_SUCCESS = "Edited Student: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_STUDENT = "This student already exists in the academy directory.";

    private final Index index;
    private final EditStudentDescriptor editStudentDescriptor;

    /**
     * @param index of the student in the filtered student list to edit
     * @param editStudentDescriptor details to edit the student with
     */
    public EditCommand(Index index, EditStudentDescriptor editStudentDescriptor) {
        requireNonNull(index);
        requireNonNull(editStudentDescriptor);

        this.index = index;
        this.editStudentDescriptor = new EditStudentDescriptor(editStudentDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Student> lastShownList = model.getFilteredStudentList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
        }

        Student studentToEdit = lastShownList.get(index.getZeroBased());
        Student editedStudent = createEditedStudent(studentToEdit, editStudentDescriptor);

        if (!studentToEdit.isSameStudent(editedStudent) && model.hasStudent(editedStudent)) {
            throw new CommandException(MESSAGE_DUPLICATE_STUDENT);
        }

        model.setStudent(studentToEdit, editedStudent);
        model.updateFilteredStudentList(PREDICATE_SHOW_ALL_STUDENTS);
        return new CommandResult(String.format(MESSAGE_EDIT_STUDENT_SUCCESS, editedStudent));
    }

    /**
     * Creates and returns a {@code Student} with the details of {@code studentToEdit}
     * edited with {@code editStudentDescriptor}.
     */
    private static Student createEditedStudent(Student studentToEdit, EditStudentDescriptor editStudentDescriptor) {
        assert studentToEdit != null;

        Name updatedName = editStudentDescriptor.getName().orElse(studentToEdit.getName());
        Phone updatedPhone = editStudentDescriptor.getPhone().orElse(studentToEdit.getPhone());
        Email updatedEmail = editStudentDescriptor.getEmail().orElse(studentToEdit.getEmail());
        Telegram updatedTelegram = editStudentDescriptor.getTelegram().orElse(studentToEdit.getTelegram());
        Set<Tag> updatedTags = editStudentDescriptor.getTags().orElse(studentToEdit.getTags());
        Assessment updatedAssessment = editStudentDescriptor.getAssessment().orElse(studentToEdit.getAssessment());
        StudioRecord updatedStudioRecord =
                editStudentDescriptor.getStudioRecord().orElse(studentToEdit.getStudioRecord());

        return new Student(
                updatedName,
                updatedPhone,
                updatedEmail,
                updatedTelegram,
                updatedStudioRecord,
                updatedAssessment,
                updatedTags);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        // state check
        EditCommand e = (EditCommand) other;
        return index.equals(e.index)
                && editStudentDescriptor.equals(e.editStudentDescriptor);
    }

    /**
     * Stores the details to edit the student with. Each non-empty field value will replace the
     * corresponding field value of the student.
     */
    public static class EditStudentDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Telegram telegram;
        private Set<Tag> tags;
        private StudioRecord studioRecord;
        private Assessment assessment;

        public EditStudentDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditStudentDescriptor(EditStudentDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setTelegram(toCopy.telegram);
            setTags(toCopy.tags);
            setAssessment(toCopy.assessment);
            setStudioRecord(toCopy.studioRecord);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, telegram, tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setTelegram(Telegram telegram) {
            this.telegram = telegram;
        }

        public Optional<Telegram> getTelegram() {
            return Optional.ofNullable(telegram);
        }

        public void setAssessment(Assessment assessment) {
            this.assessment = assessment;
        }

        public Optional<Assessment> getAssessment() {
            return Optional.ofNullable(assessment);
        }

        public void setStudioRecord(StudioRecord studioRecord) {
            this.studioRecord = studioRecord;
        }

        public Optional<StudioRecord> getStudioRecord() {
            return Optional.ofNullable(studioRecord);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditStudentDescriptor)) {
                return false;
            }

            // state check
            EditStudentDescriptor e = (EditStudentDescriptor) other;

            return getName().equals(e.getName())
                    && getPhone().equals(e.getPhone())
                    && getEmail().equals(e.getEmail())
                    && getTelegram().equals(e.getTelegram())
                    && getTags().equals(e.getTags());
        }
    }
}
