package machine.enigma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class JTextFieldLimit extends PlainDocument {

    /**
     * Serial ID for the PlainDocument.
     */
    private static final long serialVersionUID = -7389064483330913826L;

    private final int limit;

    private final Pattern alpha = Pattern.compile("[a-zA-Z]");
    private Matcher match;

    public JTextFieldLimit(final int limit) {
        super();
        this.limit = limit;
    }

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