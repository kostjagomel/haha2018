package com.intervale.testbvg.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: alexkovalev
 * Date: 11.04.12
 * Time: 5:36
 */
@SuppressLint("AppCompatCustomView")
public class EditTextFormatted extends EditText {

    public static final String CHANGED_CHAR_IN_MASK = "daAxXzh"; // символы в маске на месте вводимых значений

    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_FORMATTED_INVISIBLE_MASK = 1; // без отображения маски ввода
    public static final int TYPE_FORMATTED_VISIBLE_MASK = 2; // с отображением маски ввода

    private static Pattern numberPattern = Pattern.compile("[0-9]");
    private static Pattern lettersPattern = Pattern.compile("[a-zA-Z]");
    private static Pattern lettersAndNumberPattern = Pattern.compile("[a-zA-Z0-9]");
    private static Pattern lettersAndWildcardsPattern = Pattern.compile("[a-zA-Z[[:punct:]]]");
    private static Pattern lettersNumberWildcardsPattern = Pattern.compile("[a-zA-Z0-9[[:punct:]]]");
    private static Pattern allLetters = Pattern.compile(".");

    private String mask;
    private ArrayList<String> maskCharList;
    private StringBuilder textWithoutMask;
    private boolean isTextSelected;
    private int maxLengthTextWithoutMask;
    private int cursorPosition;
    private int type;
    private List<TextWatcher> textWatchers = new ArrayList<TextWatcher>();
    private List<TextWatcher> stableTextWatchers = new ArrayList<TextWatcher>();
    private OnFocusChangeListener onFocusChangeListener;

    public EditTextFormatted(Context context) {
        super(context);
        init();
    }

    public EditTextFormatted(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditTextFormatted(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        maskCharList = null;
        mask = null;
        cursorPosition = 0;
        isTextSelected = false;
        type = TYPE_FORMATTED_VISIBLE_MASK;
        textWithoutMask = new StringBuilder();

        super.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!TextUtils.isEmpty(mask) && !TextUtils.isEmpty(getHint())) {
                    if (!hasFocus && textWithoutMask.length() == 0) {
                        setTextSilent("");
                    } else {
                        setTextSilent(getTextWithMask());
                    }
                }
                if (onFocusChangeListener != null) {
                    onFocusChangeListener.onFocusChange(v, hasFocus);
                }
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (maskCharList == null) return;
                if (getSelectionEnd() - getSelectionStart() != 0) {
                    setTextSelected(true);
                }
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (maskCharList == null) return;
                if (s.length() <= 0 && before <= 0 && count <= 0) {
                    return;
                }
                if (getTextWithMask(maskCharList, textWithoutMask.toString(), type).equals(s.toString())) { // Если текст меняли мы, то ничего не делаем
                    return;
                }
                if (before > 0) {
                    deleteInTextWithoutMask(start, before);
                }
                if (count > 0) {
                    insertInTextWithoutMask(start, count);
                }
                if (textWithoutMask.length() > maxLengthTextWithoutMask) {
                    textWithoutMask.setLength(maxLengthTextWithoutMask);
                }
                setTextSilent(getTextWithMask(maskCharList, textWithoutMask.toString(), type));
                setSelection(getPositionCursor(cursorPosition));
            }

            public void afterTextChanged(Editable s) {

            }
        };
        addTextChangedListener(textWatcher);
    }

    private void setTextSelected(boolean b) {
        isTextSelected = b;
    }

    public void setMask(String mask) {
        this.mask = mask;
        maskCharList = createMaskCharList(mask);
        maxLengthTextWithoutMask = 0;
        textWithoutMask.setLength(0);
        for (int i = 0; i < maskCharList.size(); i++) {
            if (isPositionForInput(maskCharList, i)) {
                maxLengthTextWithoutMask++;
            }
        }
        setText(getTextWithMask(maskCharList, textWithoutMask.toString(), type));
        setSelection(getPositionCursor(0));
    }

    public static ArrayList<String> createMaskCharList(String mask) {
        ArrayList<String> maskList = new ArrayList<String>();
        if (mask != null) {
            for (int i = 0; i < mask.length(); i++) {
                if (mask.charAt(i) != '\\') {
                    maskList.add(mask.substring(i, i + 1));
                } else {
                    maskList.add(mask.substring(i, i + 2));
                    i++;
                }
            }
        }
        return maskList;
    }

    public String getTextWithoutMask() {
        return textWithoutMask.toString();
    }

    public static String formatValue(String value, String mask) {
        return getTextWithMask(createMaskCharList(mask), value, TYPE_FORMATTED_INVISIBLE_MASK).trim();
    }

    public String getTextWithMask() {
        return getTextWithMask(maskCharList, textWithoutMask.toString(), type);
    }

    private static String getTextWithMask(ArrayList<String> maskList, String textWithoutMask, int type) {
        int indexTextWithoutMask = 0;
        StringBuilder textWithMask = new StringBuilder();
        for (int i = 0; i < maskList.size(); i++) {
            if (isPositionForInput(maskList, i)) {
                if (textWithoutMask.length() > indexTextWithoutMask) {
                    if (isHiddenCharacter(maskList, i)) {
                        textWithMask.append("X");
                    } else {
                        textWithMask.append(textWithoutMask.charAt(indexTextWithoutMask));
                    }
                    indexTextWithoutMask++;
                } else {
                    if (type == TYPE_FORMATTED_VISIBLE_MASK) {
                        textWithMask.append('_');
                    } else {
                        break;
                    }
                }
            } else {
                textWithMask.append(getVisibleCharInMask(maskList, i));
            }
        }
        return textWithMask.toString();
    }

    private static boolean isHiddenCharacter(ArrayList<String> maskList, int position) {
        return "h".equals(maskList.get(position));
    }

    private int getPositionCursor(int positionInTextWithoutMask) {
        int position = 0;
        int i;
        for (i = 0; i < maskCharList.size(); i++) {
            if (isPositionForInput(maskCharList, i)) {
                if (positionInTextWithoutMask == position) {
                    break;
                } else {
                    position++;
                }
            }
        }
        return Math.min(getText().length(), i);
    }

    protected void onSelectionChanged(int selStart, int selEnd) {
        if (maskCharList == null) return;
        if (textWithoutMask != null || getText().length() > 1) {
            int maxPosition = getPositionCursor(textWithoutMask.length());
            if (selStart > maxPosition || selEnd > maxPosition) {
                if (selStart > maxPosition) {
                    selStart = maxPosition;
                }
                if (selEnd > maxPosition) {
                    selEnd = maxPosition;
                }
                setSelection(selStart, selEnd);
            }
            if (selEnd != selStart) {
                setTextSelected(true);
            }
        }
    }

    // проверка, является ли вводимым символ с индексом i в маске
    private static boolean isPositionForInput(ArrayList<String> maskList, int position) {
        boolean result = false;
        if (position < maskList.size() && !maskList.get(position).contains("\\") && CHANGED_CHAR_IN_MASK.contains(maskList.get(position))) {
            result = true;
        }
        return result;
    }

    private void deleteInTextWithoutMask(int start, int count) {
        int indexDeleteStart = 0;
        int indexDeleteStop = 0;
        for (int i = 0; i < start + count; i++) {
            if (isPositionForInput(maskCharList, i)) {
                if (i < start) {
                    indexDeleteStart++;
                }
                indexDeleteStop++;
            }
        }
        if (!isTextSelected && !isPositionForInput(maskCharList, start) && start > 0
                && count <= 1 && indexDeleteStart > 0) {
            indexDeleteStart--;
        }
        if (indexDeleteStop > textWithoutMask.length()) {
            indexDeleteStop = textWithoutMask.length();
        }
        if (indexDeleteStop > indexDeleteStart) {
            textWithoutMask.delete(indexDeleteStart, indexDeleteStop);
        }

        cursorPosition = indexDeleteStart;
        setTextSelected(false);
    }

    private void insertInTextWithoutMask(int start, int count) {
        if((start+count)>getText().length()) return;

        StringBuilder addedText = new StringBuilder(getText().subSequence(start, start + count));
        // если номер телефона
        cutUnnessesarySymbols(addedText);

        int indexInTextWithoutMask = 0;
        for (int i = 0; i < start && i < maskCharList.size()
                && indexInTextWithoutMask < textWithoutMask.length(); i++) {
            if (isPositionForInput(maskCharList, i)) {
                indexInTextWithoutMask++;
            }
        }
        cursorPosition = indexInTextWithoutMask;

        for (int i = 0; i < addedText.length() && textWithoutMask.length() < maxLengthTextWithoutMask; i++) {
            if (isCorrectCharForThisPosition(maskCharList, getPositionCursor(indexInTextWithoutMask), addedText.charAt(i))) {
                textWithoutMask.insert(indexInTextWithoutMask, addedText.charAt(i));
                indexInTextWithoutMask++;
                cursorPosition++;
            }
        }
    }

    private void cutUnnessesarySymbols(StringBuilder addedText) {
        if (getMask().startsWith("+7") && textWithoutMask.length() == 0) {
            if (addedText.length() == 11 && addedText.charAt(0) == '8') {
                addedText.delete(0, 1);
            }
            if (addedText.length() == 11 && addedText.charAt(0) == '7') {
                addedText.delete(0, 1);
            }
            if (addedText.length() == 12 && addedText.toString().startsWith("+7")) {
                addedText.delete(0, 2);
            }
        }
    }

    private static boolean isCorrectCharForThisPosition(ArrayList<String> maskList, int index, char inputChar) {
        switch (maskList.get(index).charAt(0)) {
            case 'd': {
                return numberPattern.matcher(Character.toString(inputChar)).matches();
            }
            case 'a': {
                return lettersPattern.matcher(Character.toString(inputChar)).matches();
            }
            case 'A': {
                return lettersAndNumberPattern.matcher(Character.toString(inputChar)).matches();
            }
            case 'x': {
                return lettersAndWildcardsPattern.matcher(Character.toString(inputChar)).matches();
            }
            case 'X': {
                return lettersNumberWildcardsPattern.matcher(Character.toString(inputChar)).matches();
            }
            case 'z': {
                return allLetters.matcher(Character.toString(inputChar)).matches();
            }
            default: {
                return true;
            }
        }
    }

    public int getType() {
        return type;
    }

    public String getMask() {
        return mask;
    }

    public static String getVisibleCharInMask(ArrayList<String> maskList, int i) {
        return maskList.get(i).replaceFirst("\\\\", "");
    }

    public void setType(int type) {
        this.type = type;
    }

    public static boolean isFormatCorrect(String enteredText, String mask) {
        ArrayList<String> maskList = createMaskCharList(mask);
        if (enteredText.length() > maskList.size()) return false;

        int counter = 0;
        char textChar;
        for (int i = 0; i < maskList.size(); i++) {
            if (i == enteredText.length()) break;
            textChar = enteredText.charAt(counter);
            if (isPositionForInput(maskList, i)) {
                if (!isCorrectCharForThisPosition(maskList, i, textChar)) {
                    return false;
                }
                counter++;
            }
        }
        return true;
    }

    public void addTextChangedListener(TextWatcher watcher) {
        textWatchers.add(watcher);
        super.addTextChangedListener(watcher);
    }

    public void addStableTextChangedListener(TextWatcher watcher) {
        stableTextWatchers.add(watcher);
        super.addTextChangedListener(watcher);
    }

    public void removeTextChangedListener(TextWatcher watcher) {
        textWatchers.remove(watcher);
        stableTextWatchers.remove(watcher);
        super.removeTextChangedListener(watcher);
    }

    public void setTextSilent(String text) {
        for (TextWatcher textWatcher : textWatchers) {
            super.removeTextChangedListener(textWatcher);
        }
        setText(text);
        for (TextWatcher textWatcher : textWatchers) {
            super.addTextChangedListener(textWatcher);
        }
    }

    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        onFocusChangeListener = l;
    }
}
