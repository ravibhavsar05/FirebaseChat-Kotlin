package com.kanhasoft.firedb.common;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatImageView;
import com.kanhasoft.firedb.R;

import java.util.LinkedList;

public class MessageBar {

    private static final String STATE_MESSAGES = "net.simonvt.messagebar.MessageBar.messages";
    private static final String STATE_CURRENT_MESSAGE = "net.simonvt.messagebar.MessageBar.currentMessage";
    private static final int ANIMATION_DURATION = 500;
    private static final int HIDE_DELAY = 3000;
    private View mContainer;
    private TextView mTextView;
    private TextView mbMessageTitle;
    private AppCompatImageView mbIcon;
    private LinkedList<Message> mMessages = new LinkedList<Message>();
    private Message mCurrentMessage;
    private boolean mShowing;
    private OnMessageClickListener mClickListener;
    private Handler mHandler;
    private AlphaAnimation mFadeInAnimation;
    private AlphaAnimation mFadeOutAnimation;
    private Activity activity;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mContainer.startAnimation(mFadeOutAnimation);
        }
    };
    private final View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mClickListener != null && mCurrentMessage != null) {
                mClickListener.onMessageClick(mCurrentMessage.mToken);
                mCurrentMessage = null;
                mHandler.removeCallbacks(mHideRunnable);
                mHideRunnable.run();
            }
        }
    };

    public MessageBar(Activity activity) {
        this.activity = activity;
        ViewGroup container = (ViewGroup) activity.findViewById(android.R.id.content);
        View v = activity.getLayoutInflater().inflate(R.layout.mb__messagebar, container);
        init(v);
    }

    public MessageBar(View v) {
        init(v);
    }

    private void init(View v) {
        mContainer = v.findViewById(R.id.mbContainer);
        mContainer.setVisibility(View.GONE);
        mTextView = (TextView) v.findViewById(R.id.mbMessage);
        mbMessageTitle = (TextView) v.findViewById(R.id.mbMessageTitle);
        mbIcon = (AppCompatImageView) v.findViewById(R.id.mbIcon);


        mFadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
        mFadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
        mFadeOutAnimation.setDuration(ANIMATION_DURATION);
        mFadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Message nextMessage = mMessages.poll();

                if (nextMessage != null) {
                    show(nextMessage);
                } else {
                    mCurrentMessage = null;
                    mContainer.setVisibility(View.GONE);
                    mShowing = false;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mHandler = new Handler();
    }

    public void showWithColor(String msessage, String ColorString) {


        mShowing = true;
        mContainer.setVisibility(View.VISIBLE);
        mContainer.setBackgroundColor(Color.parseColor(ColorString));
        if (ColorString.equals(activity.getResources().getString(R.string.msg_color_success_color))) {
            mbMessageTitle.setText("Success");
            mbIcon.setImageDrawable(activity.getResources().getDrawable(R.drawable.msg_check_mark));
        } else if (ColorString.equals(activity.getResources().getString(R.string.msg_color_warning_color))) {
            mbMessageTitle.setText("Alert");
            mbIcon.setImageDrawable(activity.getResources().getDrawable(R.drawable.msg_warning));
        }
        mCurrentMessage = new Message(msessage, "", 0, null);

        mTextView.setText(Utils.INSTANCE.getValue(mCurrentMessage.mMessage));


        mFadeInAnimation.setDuration(ANIMATION_DURATION);

        mContainer.startAnimation(mFadeInAnimation);
        mHandler.postDelayed(mHideRunnable, HIDE_DELAY);
    }

    public void showWithColorAndTime(String msessage, String ColorString, int animationTime) {


        mShowing = true;
        mContainer.setVisibility(View.VISIBLE);
        mContainer.setBackgroundColor(Color.parseColor(ColorString));
        mCurrentMessage = new Message(msessage, "", 0, null);

        mTextView.setText(mCurrentMessage.mMessage);


        mFadeInAnimation.setDuration(animationTime);

        mContainer.startAnimation(mFadeInAnimation);
        mHandler.postDelayed(mHideRunnable, HIDE_DELAY);
    }

    public void show(String message) {
        show(message, null);
    }

    public void show(String message, String actionMessage) {
        show(message, actionMessage, 0);
    }

    public void show(String message, String actionMessage, int actionIcon) {
        show(message, actionMessage, actionIcon, null);
    }

    public void show(String message, String actionMessage, int actionIcon, Parcelable token) {
        Message m = new Message(message, actionMessage, actionIcon, token);
        if (mShowing) {
            mMessages.add(m);
        } else {
            show(m);
        }
    }

    private void show(Message message) {
        show(message, false);
    }

    private void show(Message message, boolean immediately) {
        mShowing = true;
        mContainer.setVisibility(View.VISIBLE);
        mCurrentMessage = message;
        mTextView.setText(message.mMessage);
        if (message.mActionMessage != null) {
            mTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);

        }

        if (immediately) {
            mFadeInAnimation.setDuration(0);
        } else {
            mFadeInAnimation.setDuration(ANIMATION_DURATION);
        }
        mContainer.startAnimation(mFadeInAnimation);
        mHandler.postDelayed(mHideRunnable, HIDE_DELAY);
    }

    public void setOnClickListener(OnMessageClickListener listener) {
        mClickListener = listener;
    }

    public void clear() {
        mMessages.clear();
        mHideRunnable.run();
    }

    public void onRestoreInstanceState(Bundle state) {
        Message currentMessage = state.getParcelable(STATE_CURRENT_MESSAGE);
        if (currentMessage != null) {
            show(currentMessage, true);
            Parcelable[] messages = state.getParcelableArray(STATE_MESSAGES);
            for (Parcelable p : messages) {
                mMessages.add((Message) p);
            }
        }
    }

    public Bundle onSaveInstanceState() {
        Bundle b = new Bundle();

        b.putParcelable(STATE_CURRENT_MESSAGE, mCurrentMessage);

        final int count = mMessages.size();
        final Message[] messages = new Message[count];
        int i = 0;
        for (Message message : mMessages) {
            messages[i++] = message;
        }

        b.putParcelableArray(STATE_MESSAGES, messages);

        return b;
    }

    public interface OnMessageClickListener {

        void onMessageClick(Parcelable token);
    }

    private static class Message implements Parcelable {

        public static final Creator<Message> CREATOR = new Creator<Message>() {
            public Message createFromParcel(Parcel in) {
                return new Message(in);
            }

            public Message[] newArray(int size) {
                return new Message[size];
            }
        };
        final String mMessage;
        final String mActionMessage;
        final int mActionIcon;
        final Parcelable mToken;

        public Message(String message, String actionMessage, int actionIcon, Parcelable token) {
            mMessage = message;
            mActionMessage = actionMessage;
            mActionIcon = actionIcon;
            mToken = token;
        }

        public Message(Parcel p) {
            mMessage = p.readString();
            mActionMessage = p.readString();
            mActionIcon = p.readInt();
            mToken = p.readParcelable(getClass().getClassLoader());
        }

        public void writeToParcel(Parcel out, int flags) {
            out.writeString(mMessage);
            out.writeString(mActionMessage);
            out.writeInt(mActionIcon);
            out.writeParcelable(mToken, 0);
        }

        public int describeContents() {
            return 0;
        }
    }
}
