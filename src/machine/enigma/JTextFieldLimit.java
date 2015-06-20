package machine.enigma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * This is a document filter that I use to prevent the user from entering more
 * than one letter in any field. The string will only display if the input is a
 * single alphabetic character.
 *
 * @author John Agapeyev
 *
 */
public class JTextFieldLimit extends PlainDocument {

    /**
     * Serial ID for the PlainDocument.
     */
    private static final long serialVersionUID = -7389064483330913826L;

    /**
     * Limit of characters the user can enter
     */
    private final int limit;

    /**
     * Alphabetic regular expression
     */
    private final Pattern alpha = Pattern.compile("[a-zA-Z]");
    private Matcher match;

    /**
     * Constructor that takes a limit as a parameter
     *
     * @param limit
     *            The character limit to be used.
     */
    public JTextFieldLimit(final int limit) {
        super();
        this.limit = limit;
    }

    /**
     * Checks if the string will be over the limit and if it's alphabetic. If
     * so, add the string to the TextField.
     */
    @Override
    public void insertString(final int offset, final String str,
            final AttributeSet attr) throws BadLocationException {
        if (str == null) {
            return;
        }

        match = alpha.matcher(str);

        if ((getLength() + str.length()) <= limit && match.matches()) {
            super.insertString(offset, str.toUpperCase(), attr);
        }
    }
}