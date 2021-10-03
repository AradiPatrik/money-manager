package com.aradipatrik.claptrap.common.util

import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import kotlin.properties.Delegates

object ViewDelegates {
  fun <T, U> viewGroupObservableProperty(
    views: Lazy<List<T>>,
    initialValue: U,
    onNewValue: T.(oldValue: U, newValue: U) -> Unit
  ) = Delegates.observable(initialValue) { _, oldValue, newValue ->
    views.value.forEach {
      onNewValue(it, oldValue, newValue)
    }
  }

  fun settingTextInputLayoutContent(
    initialValue: String = "",
    view: () -> TextInputLayout
  ) = settingEditTextContent(initialValue) { view().editText!! }

  fun settingEditTextContent(
    initialValue: String = "",
    view: () -> EditText
  ) = Delegates.observable(initialValue) { _, _, newValue ->
    if (view().text.toString() != newValue) {
      view().setText(newValue)
    }
  }
}
