package seedu.lovebook.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.lovebook.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.lovebook.logic.parser.CliSyntax.PREFIX_AGE;
import static seedu.lovebook.logic.parser.CliSyntax.PREFIX_GENDER;
import static seedu.lovebook.logic.parser.CliSyntax.PREFIX_HEIGHT;
import static seedu.lovebook.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.lovebook.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import seedu.lovebook.commons.core.index.Index;
import seedu.lovebook.logic.commands.EditCommand;
import seedu.lovebook.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.lovebook.logic.parser.exceptions.ParseException;
import seedu.lovebook.model.tag.Tag;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_AGE, PREFIX_GENDER, PREFIX_HEIGHT, PREFIX_TAG);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_AGE, PREFIX_GENDER, PREFIX_HEIGHT);

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editPersonDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_AGE).isPresent()) {
            editPersonDescriptor.setAge(ParserUtil.parseAge(argMultimap.getValue(PREFIX_AGE).get()));
        }
        if (argMultimap.getValue(PREFIX_GENDER).isPresent()) {
            editPersonDescriptor.setGender(ParserUtil.parseGender(argMultimap.getValue(PREFIX_GENDER).get()));
        }
        if (argMultimap.getValue(PREFIX_HEIGHT).isPresent()) {
            editPersonDescriptor.setHeight(ParserUtil.parseAddress(argMultimap.getValue(PREFIX_HEIGHT).get()));
        }
        parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG)).ifPresent(editPersonDescriptor::setTags);

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editPersonDescriptor);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contain only one element which is an empty string, it will be parsed into a
     * {@code Set<Tag>} containing zero tags.
     */
    private Optional<Set<Tag>> parseTagsForEdit(Collection<String> tags) throws ParseException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> tagSet = tags.size() == 1 && tags.contains("") ? Collections.emptySet() : tags;
        return Optional.of(ParserUtil.parseTags(tagSet));
    }

}
