package com.diandian.coolco.emilie.fragment;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.dialog.ProgressDialog;
import com.diandian.coolco.emilie.utility.Event;
import com.diandian.coolco.emilie.utility.NetworkManager;
import com.diandian.coolco.emilie.utility.SuperToastUtil;

import roboguice.inject.InjectView;

/**
 * A placeholder fragment containing a simple view.
 */
public class FeedbackFragment extends BaseFragment {

    private final static int MAX_NUM = 240;

    @InjectView(R.id.feedback_container)
    private View feedbackContainer;

    @InjectView(R.id.feedback)
    private EditText feedbackEditText;

    /**
     * Store the remaining number of character can be typed
     */
    @InjectView(R.id.feedback_count)
    private TextView countTextView;

    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            int count = MAX_NUM - s.length();
            countTextView.setText(count + "");
        }

    };

    public FeedbackFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feedback, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        feedbackEditText.addTextChangedListener(watcher);
    }

    public void onEventMainThread(Event.SendFeedBackEvent event) {
        //Hide the Android Soft Keyboard
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(feedbackEditText.getWindowToken(), 0);

        if (TextUtils.isEmpty(feedbackEditText.getText())) {
            shake();
            SuperToastUtil.showToast(getActivity(), "请输入反馈意见");
        } else {
            if (NetworkManager.isOnline(getActivity())) {
                final Dialog dialog = ProgressDialog.show(getActivity(), "正在提交反馈...");
                feedbackEditText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        SuperToastUtil.showToast(getActivity(), "提交成功，感谢您的反馈");
                        getActivity().finish();
                    }
                }, 1000);
            } else {
                SuperToastUtil.showToast(getActivity(), getString(R.string.no_network_tip));
                getActivity().finish();

            }
        }
    }

    private void shake() {
        int margin = ((ViewGroup.MarginLayoutParams) feedbackContainer.getLayoutParams()).leftMargin;
        ObjectAnimator animator = ObjectAnimator.ofFloat(feedbackContainer, "translationX", 0, -margin, margin, -margin, margin, 0);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(1000);
        animator.start();
//        YoYo.with(Techniques.Shake).duration(1000).playOn(feedbackContainer);
    }


}
