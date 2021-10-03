package com.aradipatrik.claptrap.backdrop.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.content.res.getStringOrThrow
import com.aradipatrik.claptrap.R
import com.aradipatrik.claptrap.databinding.ViewBackdropMenuItemBinding
import com.aradipatrik.claptrap.theme.widget.ViewThemeUtil.getDimenValue
import com.aradipatrik.claptrap.theme.widget.ViewThemeUtil.inflateAndAddUsing
import com.aradipatrik.claptrap.theme.widget.ViewThemeUtil.withStyleable
import kotlinx.coroutines.flow.filter
import ru.ldralighieri.corbind.view.clicks

class BackdropBackLayerMenuItemView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  @AttrRes defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
  private lateinit var iconDrawable: Drawable
  private lateinit var text: String
  private val deactivatedAlpha by lazy { context.getDimenValue(R.dimen.alpha_medium) }
  private val clickAreaActiveAlpha by lazy { context.getDimenValue(R.dimen.alpha_low) }
  private val binding = inflateAndAddUsing(ViewBackdropMenuItemBinding::inflate)
  var shouldGenerateClickEvents = true

  init {
    withStyleable(R.styleable.BackdropBackLayerMenuItemView, attrs) {
      iconDrawable = getDrawableOrThrow(R.styleable.BackdropBackLayerMenuItemView_icon)
      text = getStringOrThrow(R.styleable.BackdropBackLayerMenuItemView_title)
    }

    initView()
  }

  private fun initView() {
    binding.backdropMenuItemIcon.setImageDrawable(iconDrawable)
    binding.backdropMenuItemText.text = text
  }

  fun activate() {
    binding.backdropMenuItemIcon.alpha = 1.0f
    binding.backdropMenuItemText.alpha = 1.0f
    binding.backdropMenuItemTapArea.isClickable = false
    binding.backdropMenuItemTapArea.alpha = clickAreaActiveAlpha
  }

  fun deactivate() {
    binding.backdropMenuItemIcon.alpha = deactivatedAlpha
    binding.backdropMenuItemText.alpha = deactivatedAlpha
    binding.backdropMenuItemTapArea.isClickable = true
    binding.backdropMenuItemTapArea.alpha = 0.0f
  }

  val clicks = binding.backdropMenuItemTapArea.clicks()
    .filter { shouldGenerateClickEvents }
}
