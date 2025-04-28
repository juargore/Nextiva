package com.nextiva.nextivaapp.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.databinding.ViewAvatarBinding;
import com.nextiva.nextivaapp.android.db.model.DbPresence;
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager;
import com.nextiva.nextivaapp.android.models.AvatarInfo;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Created by joedephillipo on 2/13/18.
 */
@AndroidEntryPoint
public class AvatarView extends FrameLayout {

    private ImageView mAvatarImageView;
    private ImageView mAvatarStateImageView;
    private ImageView mAvatarPresenceImageView;
    private AvatarInfo mCurrentAvatarInfo;

    @Inject
    protected AvatarManager mAvatarManager;

    public AvatarView(Context context) {
        this(context, null);
    }

    public AvatarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AvatarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(@NonNull Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        bindViews(inflater);
    }

    private void bindViews(LayoutInflater inflater) {
        ViewAvatarBinding binding = ViewAvatarBinding.inflate(inflater, this, true);

        mAvatarImageView = binding.avatarImageView;
        mAvatarStateImageView = binding.avatarStateImageView;
        mAvatarPresenceImageView = binding.avatarPresenceImageView;
    }

    public void setAvatar(@NonNull AvatarInfo avatarInfo, boolean shouldShowPresence) {
        mCurrentAvatarInfo = avatarInfo;
        mAvatarImageView.setImageBitmap(mAvatarManager.getBitmap(avatarInfo));

        if (avatarInfo.getPresence() != null && shouldShowPresence) {
            mAvatarPresenceImageView.setVisibility(VISIBLE);
            mAvatarPresenceImageView.setImageBitmap(mAvatarManager.getPresenceBitmap(avatarInfo));

        } else {
            mAvatarPresenceImageView.setVisibility(GONE);
        }
    }

    public void setAvatar(@NonNull AvatarInfo avatarInfo) {
        setAvatar(avatarInfo, true);
    }

    public void updatePresence(DbPresence presence) {
        if (mCurrentAvatarInfo != null) {
            mCurrentAvatarInfo.setPresence(presence);

            if (mCurrentAvatarInfo.getPresence() != null) {
                mAvatarPresenceImageView.setVisibility(VISIBLE);
                mAvatarPresenceImageView.setImageBitmap(mAvatarManager.getPresenceBitmap(mCurrentAvatarInfo));

            } else {
                mAvatarPresenceImageView.setVisibility(GONE);
            }
        }
    }

    public void setState(Context context, @Enums.AvatarState.StateType String state, boolean isNightModeEnabled) {
        switch (state) {
            case Enums.AvatarState.STATE_LOCKED:
                mAvatarStateImageView.setImageDrawable(ContextCompat.getDrawable(context, isNightModeEnabled ? R.drawable.avatar_night_locked_circle : R.drawable.avatar_locked_circle));
                mAvatarStateImageView.setVisibility(VISIBLE);
                break;

            case Enums.AvatarState.STATE_SELECTED:
                mAvatarStateImageView.setImageDrawable(ContextCompat.getDrawable(context, isNightModeEnabled ? R.drawable.avatar_night_selected_circle : R.drawable.avatar_selected_circle));
                mAvatarStateImageView.setVisibility(VISIBLE);
                break;

            case Enums.AvatarState.STATE_NOT_SELECTED:
            default:
                mAvatarStateImageView.setVisibility(GONE);
                break;
        }
    }
}
