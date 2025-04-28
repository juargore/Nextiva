/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import androidx.annotation.AnimRes;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.util.LogUtil;

import java.util.Objects;

/**
 * Switch and animated buttons with Image and Text.
 * <p>
 * Created by Thaddeus Dannar on 5/25/18.
 */
@SuppressWarnings("unused")
public class ActiveCallButtonView extends ConstraintLayout {
    final private int ATTRIBUTE_NOT_SET = 0;

    private ImageSwitcher mActiveCallButtonImageSwitcher;
    private ImageView mActiveCallButtonFirstImageView;
    private ImageView mActiveCallButtonSecondImageView;
    private TextSwitcher mActiveCallButtonTextSwitcher;
    private TextView mActiveCallButtonFirstTextView;
    private TextView mActiveCallButtonSecondTextView;
    private int mPresentImageResource;


    /**
     * Switch and animated buttons with Image and Text.
     * <p>
     */
    public ActiveCallButtonView(@NonNull Context context) {
        this(context, null);
    }

    /**
     * Switch and animated buttons with Image and Text.
     */
    public ActiveCallButtonView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Objects.requireNonNull(inflater).inflate(R.layout.view_active_call_button, this, true);

        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {

        mActiveCallButtonImageSwitcher = findViewById(R.id.active_call_image_switcher);
        mActiveCallButtonFirstImageView = findViewById(R.id.active_call_first_image_view);
        mActiveCallButtonSecondImageView = findViewById(R.id.active_call_second_image_view);
        mActiveCallButtonTextSwitcher = findViewById(R.id.active_call_button_text_switcher);
        mActiveCallButtonFirstTextView = findViewById(R.id.active_call_button_first_text_view);
        mActiveCallButtonSecondTextView = findViewById(R.id.active_call_button_second_text_view);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ActiveCallButtonView,
                0, 0);

        try {
            setIconImage(typedArray.getResourceId(R.styleable.ActiveCallButtonView_imageSrc, ATTRIBUTE_NOT_SET));
            setText(typedArray.getResourceId(R.styleable.ActiveCallButtonView_text, ATTRIBUTE_NOT_SET));
            setImageTint(typedArray.getResourceId(R.styleable.ActiveCallButtonView_imageTint, ATTRIBUTE_NOT_SET));
            setTextColor(typedArray.getResourceId(R.styleable.ActiveCallButtonView_subTextColor, ATTRIBUTE_NOT_SET));
            setImageContentDescription(typedArray.getResourceId(R.styleable.ActiveCallButtonView_contentDescription, ATTRIBUTE_NOT_SET));
            setImageAnimationIn(typedArray.getResourceId(R.styleable.ActiveCallButtonView_imageAnimationIn, ATTRIBUTE_NOT_SET));
            setImageAnimationOut(typedArray.getResourceId(R.styleable.ActiveCallButtonView_imageAnimationOut, ATTRIBUTE_NOT_SET));
            setTextAnimationIn(typedArray.getResourceId(R.styleable.ActiveCallButtonView_textAnimationIn, ATTRIBUTE_NOT_SET));
            setTextAnimationOut(typedArray.getResourceId(R.styleable.ActiveCallButtonView_textAnimationOut, ATTRIBUTE_NOT_SET));


        } finally {
            typedArray.recycle();
        }

        setContentDescription(context);
    }


    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    //---SET TEXT

    /**
     * Displays on the currently showing text view. To animate use {@link #switchText(String)}
     * <p>
     *
     * @param text string to display on the currently showing text view.
     */
    private void setText(@NonNull final String text) {
        mActiveCallButtonTextSwitcher.setCurrentText(text);
    }

    /**
     * Displays on the currently showing text view. To animate use {@link #switchText(String)}
     * <p>
     *
     * @param resourceId string resource to display on the currently showing text view.
     */
    private void setText(@StringRes final int resourceId) {
        if (resourceId != ATTRIBUTE_NOT_SET) {
            setText(getContext().getText(resourceId).toString());
        }
    }
    //---SET TEXT END


    //---SET IMAGE

    /**
     * Displays on the currently showing imageview. To animate use {@link #switchIconImage(int)}
     * <p>
     *
     * @param iconImageResource resource to display on the currently showing image view.
     */
    private void setIconImage(@DrawableRes final int iconImageResource) {
        if (iconImageResource != ATTRIBUTE_NOT_SET) {
            ((ImageView) mActiveCallButtonImageSwitcher.getCurrentView()).setImageResource(iconImageResource);
            requestLayout();
        }
    }
    //---SET IMAGE END


    //---SET IMAGE TINT

    /**
     * Tint image on the button
     *
     * @param imageTintColor the color to tint the image.
     */
    public void setImageTint(@ColorRes final int imageTintColor) {
        if (imageTintColor != ATTRIBUTE_NOT_SET) {
            setTintOnImageView(mActiveCallButtonFirstImageView, imageTintColor);
            setTintOnImageView(mActiveCallButtonSecondImageView, imageTintColor);
        }
    }

    /**
     * Sets the tint color on the image view.
     * <p>
     *
     * @param imageView      image view you wish to apply the tint.
     * @param imageTintColor the color to tint the image.
     */
    private void setTintOnImageView(@NonNull final ImageView imageView, @ColorRes final int imageTintColor) {
        if (imageTintColor != ATTRIBUTE_NOT_SET) {
            imageView.setColorFilter(ContextCompat.getColor(getContext(), imageTintColor), PorterDuff.Mode.SRC_ATOP);
            requestLayout();
        }
    }

    //---SET IMAGE TINT END

    //---SET IMAGE TEXT COLOR

    private void setTextColor(@ColorRes final int textTintColor) {
        if (textTintColor != ATTRIBUTE_NOT_SET) {
            setColorOnTextView(mActiveCallButtonFirstTextView, textTintColor);
            setColorOnTextView(mActiveCallButtonSecondTextView, textTintColor);
        }
    }

    private void setColorOnTextView(@NonNull final TextView textView, final int textTintColor) {
        if (textTintColor != ATTRIBUTE_NOT_SET) {
            textView.setTextColor(textTintColor);
            requestLayout();
        }
    }
    //---SET TEXT COLOR END


    //---SET CONTENT DESCRIPTION
    private void setImageContentDescription(@StringRes final int contentDescriptionResource) {
        if (contentDescriptionResource != ATTRIBUTE_NOT_SET) {
            setFirstImageContentDescription(contentDescriptionResource);
        }
    }

    private void setFirstImageContentDescription(@StringRes final int contentDescriptionResource) {
        if (contentDescriptionResource != ATTRIBUTE_NOT_SET) {
            setContentDescriptionOnImageView(mActiveCallButtonFirstImageView, getContext().getText(contentDescriptionResource));
        }

    }

    private void setContentDescriptionOnImageView(@NonNull final ImageView imageView, @NonNull final CharSequence contentDescription) {
        imageView.setContentDescription(contentDescription);
        requestLayout();
    }

//---SET CONTENT DESCRIPTION END


    //---SET TEXT ANIMATION

    /**
     * @param animationResource The resource to set the animation on.
     */
    public void setTextAnimation(@AnimRes final int animationResource) {
        if (animationResource != ATTRIBUTE_NOT_SET) {
            try {
                setAnimationInOnTextSwitcher(mActiveCallButtonTextSwitcher, animationResource);
                setAnimationOutOnTextSwitcher(mActiveCallButtonTextSwitcher, animationResource);
            } catch (Resources.NotFoundException e) {
                LogUtil.d("Animation not found: " + e);
            }
        }

    }

    private void setTextAnimationIn(@AnimRes final int animationInResource) {
        if (animationInResource != ATTRIBUTE_NOT_SET) {
            setTextAnimationIn(mActiveCallButtonTextSwitcher, animationInResource);
        }
    }

    private void setTextAnimationIn(TextSwitcher textSwitcher, @AnimRes final int animationInResource) {

        if (animationInResource != ATTRIBUTE_NOT_SET) {
            setAnimationInOnTextSwitcher(textSwitcher, animationInResource);
        }

    }

    private void setTextAnimationOut(@AnimRes final int animationOutResource) {

        if (animationOutResource != ATTRIBUTE_NOT_SET) {
            setAnimationInOnTextSwitcher(mActiveCallButtonTextSwitcher, animationOutResource);
        }

    }

    private void setAnimationInOnTextSwitcher(@NonNull final TextSwitcher textSwitcher, @AnimRes final int animationResource) {
        if (animationResource != ATTRIBUTE_NOT_SET) {
            textSwitcher.setInAnimation(getContext(), animationResource);
        }
    }

    private void setAnimationOutOnTextSwitcher(@NonNull final TextSwitcher textSwitcher, @AnimRes final int animationResource) {
        if (animationResource != ATTRIBUTE_NOT_SET) {
            textSwitcher.setOutAnimation(getContext(), animationResource);
        }
    }
    //---SET TEXT ANIMATION END


    //---SET IMAGE ANIMATION

    /**
     * Set the In and Out Animations to the same animation resource.
     * <p>
     *
     * @param animationResource Resource for the animation
     */
    @SuppressWarnings("unused")
    public void setImageAnimation(@AnimRes final int animationResource) {
        if (animationResource != ATTRIBUTE_NOT_SET) {
            try {
                setAnimationInOnImageSwitcher(mActiveCallButtonImageSwitcher, animationResource);
                setAnimationOutOnImageSwitcher(mActiveCallButtonImageSwitcher, animationResource);
            } catch (Resources.NotFoundException e) {
                LogUtil.d("Animation not found: " + e);
            }
        }

    }

    /**
     * Set the Animation for the incoming image on the image switcher.
     * <p>
     *
     * @param animationInResource Resource for the animation.
     */
    private void setImageAnimationIn(@AnimRes final int animationInResource) {

        if (animationInResource != ATTRIBUTE_NOT_SET) {
            try {
                setAnimationInOnImageSwitcher(mActiveCallButtonImageSwitcher, animationInResource);
            } catch (Resources.NotFoundException e) {
                LogUtil.d("Animation not found: " + e);
            }
        }

    }

    /**
     * Set the Animation for the outgoing image on the image switcher.
     * <p>
     *
     * @param animationOutResource Resource for the animation.
     */
    private void setImageAnimationOut(@AnimRes final int animationOutResource) {
        if (animationOutResource != ATTRIBUTE_NOT_SET) {
            try {
                setAnimationInOnImageSwitcher(mActiveCallButtonImageSwitcher, animationOutResource);
            } catch (Resources.NotFoundException e) {
                LogUtil.d("Animation not found: " + e);
            }
        }

    }

    private void setAnimationInOnImageSwitcher(@NonNull final ImageSwitcher imageSwitcher, @AnimRes final int animationResource) {

        if (animationResource != ATTRIBUTE_NOT_SET) {
            imageSwitcher.setInAnimation(getContext(), animationResource);
        }
    }

    private void setAnimationOutOnImageSwitcher(@NonNull final ImageSwitcher imageSwitcher, @AnimRes final int animationResource) {
        if (animationResource != ATTRIBUTE_NOT_SET) {
            imageSwitcher.setOutAnimation(getContext(), animationResource);
        }
    }
    //---SET IMAGE ANIMATION END


    //--- SWITCH IMAGE AND TEXT

    private void switchIconImage(@DrawableRes final int image) {

        if (mPresentImageResource != image && image != ATTRIBUTE_NOT_SET) {
            mActiveCallButtonImageSwitcher.setImageResource(image);
            mPresentImageResource = image;
        }

        LogUtil.d(mActiveCallButtonImageSwitcher.getDisplayedChild() + " : " + image);
    }

    private void switchText(@NonNull String text) {

        if (mActiveCallButtonTextSwitcher.getTag() != text) {
            mActiveCallButtonTextSwitcher.setText(text);
            mActiveCallButtonTextSwitcher.setTag(text);
        }
    }

    private void switchText(@NonNull CharSequence text) {
        if (mActiveCallButtonTextSwitcher.getTag() != text) {
            mActiveCallButtonTextSwitcher.setText(text);
            mActiveCallButtonTextSwitcher.setTag(text);
        }
    }

    /**
     * Image and Text to switch button to.
     * <p>
     *
     * @param image Drawable Image Resource to switch to.
     * @param text  Text to display below the image.
     */
    public void switchIconImageText(@DrawableRes final int image, @NonNull String text) {
        switchIconImage(image);
        switchText(text);
    }

    /**
     * Image and Text to switch button to.
     * <p>
     *
     * @param image Drawable Image Resource to switch to.
     * @param text  Text to display below the image.
     */
    public void switchIconImageText(@DrawableRes final int image, @NonNull CharSequence text) {
        switchIconImage(image);
        switchText(text);
    }

    //--- SWITCH IMAGE AND TEXT END


    private void disableEnableControls(boolean enable, ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup) {
                disableEnableControls(enable, (ViewGroup) child);
            }
        }
    }

    public void setContentDescription(Context context) {
        String contentDescription = context.getString(getContentDescriptionStringId());

        setContentDescription(contentDescription);
        mActiveCallButtonImageSwitcher.setContentDescription(context.getString(R.string.active_call_image_switcher_content_description, contentDescription));
        mActiveCallButtonFirstImageView.setContentDescription(context.getString(R.string.active_call_first_image_view_content_description, contentDescription));
        mActiveCallButtonSecondImageView.setContentDescription(context.getString(R.string.active_call_second_image_view_content_description, contentDescription));
        mActiveCallButtonTextSwitcher.setContentDescription(context.getString(R.string.active_call_text_switcher_content_description, contentDescription));
        mActiveCallButtonFirstTextView.setContentDescription(context.getString(R.string.active_call_first_text_view_content_description, contentDescription));
        mActiveCallButtonSecondTextView.setContentDescription(context.getString(R.string.active_call_second_text_view_content_description, contentDescription));

        disableEnableControls(this.isEnabled(), this);
    }

    private int getContentDescriptionStringId() {
        int id = getId();
        if (id == R.id.active_call_mute_button) {
            return R.string.active_call_mute_button_content_description;
        } else if (id == R.id.active_call_hold_button) {
            return R.string.active_call_hold_button_content_description;
        } else if (id == R.id.active_call_speaker_button) {
            return R.string.active_call_speaker_button_content_description;
        } else if (id == R.id.active_call_keypad_button) {
            return R.string.active_call_keypad_button_content_description;
        } else if (id == R.id.active_call_video_add_participant_button) {
            return mPresentImageResource == R.drawable.ic_person_add ?
                    R.string.active_call_add_participants_button_content_description :
                    R.string.active_call_video_button_content_description;
        } else if (id == R.id.active_call_new_call_swap_button) {
            return mPresentImageResource == R.drawable.ic_swap_calls ?
                    R.string.active_call_swap_call_button_content_description :
                    R.string.active_call_new_call_button_content_description;
        } else if (id == R.id.active_call_end_call_button) {
            return R.string.active_call_end_call_button_content_description;
        } else if (id == R.id.active_call_more_button) {
            return R.string.active_call_more_button_content_description;
        } else if (id == R.id.active_call_transfer_complete_button) {
            return R.string.active_call_complete_transfer_button_content_description;
        } else if (id == R.id.active_call_transfer_end_button) {
            return R.string.active_call_end_transfer_button_content_description;
        } else {
            return -1;
        }
    }
}
