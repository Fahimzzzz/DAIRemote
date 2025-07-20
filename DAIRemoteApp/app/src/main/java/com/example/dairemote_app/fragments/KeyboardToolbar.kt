package com.example.dairemote_app.fragments

import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.dairemote_app.R

class KeyboardToolbar internal constructor(
    var moreOpts: Int,
    var modifier1: Int,
    var modifier2: Int,
    var modifier3: Int,
    var modifier4: Int,
    var modifier5: Int,
    private val keyboardTextInputView: TextView,
    private val keyboardToolbar: Toolbar,
    private val keyboardExtraBtnsLayout: GridLayout,
    private val p1RowsButtons: Array<TextView>,
    private val p2RowsButtons: Array<TextView>
) {
    private val keyCombination = StringBuilder()
    private var winActive = false
    private var ctrlActive = false
    private var shiftActive = false
    private var altActive = false
    private var fnActive = false
    private var modifierToggled = false
    private val p1Keys = arrayOf(
        "{INS}",
        "{DEL}",
        "{PRTSC}",
        "{TAB}",
        "{UP}",
        "{ESC}",
        "UP",
        "DOWN",
        "MUTE",
        "{LEFT}",
        "{DOWN}",
        "{RIGHT}"
    )
    private val p2Keys = arrayOf(
        "{F1}",
        "{F2}",
        "{F3}",
        "{F4}",
        "{F5}",
        "{F6}",
        "{F7}",
        "{F8}",
        "{F9}",
        "{F10}",
        "{F11}",
        "{F12}"
    )
    private var parenthesesCount = 0
    private var currentPageIndex = 0

    fun GetParenthesesCount(): Int {
        return this.parenthesesCount
    }

    fun GetKeyboardToolbar(): Toolbar {
        return this.keyboardToolbar
    }

    fun GeyKeyboardLayout(): GridLayout {
        return this.keyboardExtraBtnsLayout
    }

    fun GetModifierToggled(): Boolean {
        return this.modifierToggled
    }

    fun SetModifierToggled(toggled: Boolean) {
        this.modifierToggled = toggled
    }

    fun GetKeys(page: Int): Array<String> {
        if (page == 0) {
            return p1Keys
        }
        return p2Keys
    }

    fun GetButtons(page: Int): Array<TextView> {
        if (page == 0) {
            return p1RowsButtons
        }
        return p2RowsButtons
    }

    fun GetCurrentToolbarPage(): Int {
        return this.currentPageIndex
    }

    fun SetCurrentToolbarPage(page: Int) {
        this.currentPageIndex = page
    }

    fun NextToolbarPage(): Int {
        SetCurrentToolbarPage((GetCurrentToolbarPage() + 1) % 2)
        return GetCurrentToolbarPage()
    }

    fun GetKeyboardTextView(): TextView {
        return this.keyboardTextInputView
    }

    fun GetKeyCombination(): StringBuilder {
        return this.keyCombination
    }

    fun AppendKeyCombination(character: String?) {
        keyCombination.append(character)
    }

    fun AppendKeyCombination(character: Char) {
        keyCombination.append(character)
    }

    fun AddParentheses() {
        if (GetParenthesesCount() > 0) {
            for (i in 0 until parenthesesCount) {
                AppendKeyCombination(")")
            }
            keyboardTextInputView.text = ""
        }
    }

    fun ToggleKeyboardToolbar(open: Boolean) {
        if (open) {
            if (GetKeyboardToolbar().visibility != View.VISIBLE) {
                GetKeyboardToolbar().visibility = View.VISIBLE
                GeyKeyboardLayout().visibility = View.VISIBLE
                keyboardExtraSetRowVisibility(GetCurrentToolbarPage())
            }
        } else {
            if (GetKeyboardToolbar().visibility == View.VISIBLE) {
                GetKeyboardToolbar().visibility = View.GONE
                GeyKeyboardLayout().visibility = View.GONE
            }
        }
    }

    fun keyboardExtraSetRowVisibility(pageIndex: Int) {
        // Hide buttons for the current page
        for (button in if ((pageIndex == 0)) GetButtons(1) else GetButtons(0)) {
            button.visibility = View.GONE
        }

        // Show buttons for the current page
        for (button in if ((pageIndex == 0)) GetButtons(0) else GetButtons(1)) {
            button.visibility = View.VISIBLE
        }
    }

    fun KeyboardToolbarModifier(viewID: Int): Boolean {
        return when (viewID) {
            R.id.moreOpt -> {
                return true
            }

            R.id.winKey -> {
                winActive = !winActive
                KeyboardToolbarModifierHandler(
                    winActive,
                    "WIN(",
                    "Win("
                ) { /* No need for action here */ }
            }

            R.id.fnKey -> {
                fnActive = !fnActive
                KeyboardToolbarModifierHandler(
                    fnActive,
                    "FN+",
                    null
                ) { /* No need for action here */ }
            }

            R.id.altKey -> {
                altActive = !altActive
                KeyboardToolbarModifierHandler(
                    altActive,
                    "%(",
                    "Alt("
                ) { /* No need for action here */ }
            }

            R.id.ctrlKey -> {
                ctrlActive = !ctrlActive
                KeyboardToolbarModifierHandler(
                    ctrlActive,
                    "^(",
                    "Ctrl("
                ) { /* No need for action here */ }
            }

            R.id.shiftKey -> {
                shiftActive = !shiftActive
                KeyboardToolbarModifierHandler(
                    shiftActive,
                    "+(",
                    "Shift("
                ) { /* No need for action here */ }
            }

            else -> false
        }
    }

    private fun KeyboardToolbarModifierHandler(
        condition: Boolean,
        keyComb: String,
        keyboardText: String?,
        activationAction: Runnable
    ): Boolean {
        if (condition) {
            activationAction.run()
            keyCombination.append(keyComb)
            if (keyboardText != null) {
                keyboardTextInputView.append(keyboardText)
            }
            parenthesesCount += 1
            return true
        }
        return false
    }

    fun setupButtonClickListeners(listener: View.OnClickListener) {
        // Set click listeners for all buttons
        p1RowsButtons.forEach { button ->
            button.setOnClickListener(listener)
        }

        p2RowsButtons.forEach { button ->
            button.setOnClickListener(listener)
        }
    }

    fun ResetKeyboardModifiers() {
        winActive = false
        ctrlActive = false
        shiftActive = false
        altActive = false
        fnActive = false
        parenthesesCount = 0
        keyCombination.setLength(0)
    }
}
