package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nextiva.nextivaapp.android.FullscreenImageActivity;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.SmsMessageListItem;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager;
import com.nextiva.nextivaapp.android.managers.interfaces.NextivaMediaPlayer;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.nextivaapp.android.util.MessageUtil;
import com.nextiva.nextivaapp.android.view.AvatarView;
import com.nextiva.nextivaapp.android.view.LoaderImageView;

import java.util.Objects;

import javax.inject.Inject;

public abstract class SmsMessageViewHolder extends MessageBaseViewHolder<SmsMessageListItem> implements
        View.OnClickListener {

    @Nullable
    protected LinearLayout mSentLayout;
    @Nullable
    protected AvatarView mAvatarView;
    protected TextView mMessageTextView;
    protected ConstraintLayout mMessageContainer;
    protected LoaderImageView mMessageImageView;
    protected TextView mDatetimeTextView;
    @Nullable
    protected TextView mUINameTextView;
    @Nullable
    protected TextView mFailedMessageTextView;
    @Nullable
    protected TextView mRetryMessageTextView;
    @Nullable
    protected ImageView mFailedMessageIcon;
    protected TextView mFileNotSupportedTextView;
    protected TextView mFileNameTextView;
    protected ConstraintLayout mAudioAttachmentConstraint;
    protected TextView mAudioAttachmentFileName;
    protected ImageButton mAudioAttachmentPlayButton;
    protected ImageButton mAudioSpeakerButton;
    protected SeekBar mAudioAttachmentSeekBar;
    protected TextView mAudioAttachmentDurationText;
    protected ProgressBar audioAttachmentProgressBar;

    @Inject
    protected AvatarManager mAvatarManager;
    @Inject
    protected DbManager mDbManager;
    @Inject
    protected SettingsManager mSettingsManager;
    @Inject
    protected NextivaMediaPlayer nextivaMediaPlayer;

    protected RequestListener<Bitmap> glideBitmapListener = new RequestListener<Bitmap>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
            if (mMessageImageView.getImageView() != null) {
                mMessageImageView.getImageView().getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                mMessageImageView.getImageView().getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            return false;
        }
    };

    protected RequestListener<GifDrawable> glideGifListener = new RequestListener<GifDrawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
            if (mMessageImageView.getImageView() != null) {
                mMessageImageView.getImageView().getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            return false;
        }
    };

    SmsMessageViewHolder(View itemView, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        super(itemView, context, masterListListener);

        if (this instanceof SmsMessageReceivedViewHolder) {
            ((SmsMessageReceivedViewHolder) this).bindViews(itemView);

        } else if (this instanceof SmsMessageSentViewHolder) {
            ((SmsMessageSentViewHolder) this).bindViews(itemView);
        }

        mMessageTextView.setTextIsSelectable(true);
        mMessageTextView.setOnClickListener(this);
        mMessageImageView.setOnClickListener(this);

        if (mSentLayout != null) {
            mSentLayout.setOnClickListener(this);
        }
    }

    // --------------------------------------------------------------------------------------------
    // View.OnClickListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v) {
        if (mMasterListListener != null && mListItem != null) {
            if (v.getId() == R.id.list_item_chat_message_image_view) {
                if (mListItem.getData().getAttachments() != null &&
                        mListItem.getData().getAttachments().size() > 0 &&
                        !TextUtils.isEmpty(mListItem.getData().getAttachments().get(0).getLink()) &&
                        !TextUtils.isEmpty(mListItem.getData().getAttachments().get(0).getContentType()) &&
                        MessageUtil.isFileTypeSupported(Objects.requireNonNull(mListItem.getData().getAttachments().get(0).getContentType()))) {
                    Intent fullscreenImageActivityIntent = FullscreenImageActivity.Companion.newIntent(mContext,
                            Objects.requireNonNull(mListItem.getData().getAttachments().get(0).getLink()), Objects.requireNonNull(mListItem.getData().getAttachments().get(0).getContentType()));
                    mContext.startActivity(fullscreenImageActivityIntent);
                }

            } else if (Objects.requireNonNull(mListItem.getData().getSentStatus()) == Enums.SMSMessages.SentStatus.FAILED) {
                mMasterListListener.onResendFailedSmsMessageClicked(mListItem);

            } else {
                mListItem.setShowTimeSeparator(!mListItem.getShowTimeSeparator());
                mMasterListListener.onSmsMessageListItemDatetimeVisibilityToggled(mListItem);
            }
        }
    }
    // ------------------------------------------------------
}
