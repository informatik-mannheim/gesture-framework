package hs_mannheim.sysplace;

import android.text.Editable;
import android.text.TextWatcher;

import hs_mannheim.gestureframework.messaging.Packet;
import hs_mannheim.gestureframework.model.ISysplaceContext;
import hs_mannheim.gestureframework.model.Selection;

/**
 * Just a helper to class to update the {@link Selection} depending on an {@link android.widget.EditText} element.
 */
public class SysplaceTextWatcher implements TextWatcher {
    private final ISysplaceContext mSysplaceContext;

    public SysplaceTextWatcher(ISysplaceContext sysplaceContext) {
        mSysplaceContext = sysplaceContext;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // ignore
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().isEmpty()) {
            mSysplaceContext.select(Selection.Empty);
        } else {
            mSysplaceContext.select(new Selection(new Packet(s.toString())));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        // ignore
    }
}
