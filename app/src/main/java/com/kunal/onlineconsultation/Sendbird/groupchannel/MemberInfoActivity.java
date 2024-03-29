package com.kunal.onlineconsultation.Sendbird.groupchannel;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.kunal.onlineconsultation.R;
import com.kunal.onlineconsultation.Sendbird.ImageUtils;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;


import static com.kunal.onlineconsultation.Sendbird.SyncManagerUtils.getMyUserId;


public class MemberInfoActivity extends BaseActivity {

    private static final String CONNECTION_HANDLER_ID = "CONNECTION_HANDLER_MEMBER_INFO";

    private String mChannelUrl;
    private String mUserId;
    private GroupChannel mChannel;
    private Member mMember;

    private ImageView mImageViewProfile;
    private TextView mTextViewNickname;
    private RelativeLayout mRelativeLayoutBlockedByMe;
    private SwitchCompat mSwitchBlockedByMe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_member_info);

        mChannelUrl = getIntent().getStringExtra(MemberListActivity.EXTRA_CHANNEL_URL);
        mUserId = getIntent().getStringExtra(MemberListActivity.EXTRA_USER_ID);
        String profileUrl = getIntent().getStringExtra(MemberListActivity.EXTRA_USER_PROFILE_URL);
        String nickname = getIntent().getStringExtra(MemberListActivity.EXTRA_USER_NICKNAME);
        boolean blockedByMe = getIntent().getBooleanExtra(MemberListActivity.EXTRA_USER_BLOCKED_BY_ME, false);

        Toolbar toolbar = findViewById(R.id.toolbar_member_info);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left_white_24_dp);
        }

        mImageViewProfile = findViewById(R.id.image_view_profile);
        mTextViewNickname = findViewById(R.id.text_view_nickname);

        mRelativeLayoutBlockedByMe = findViewById(R.id.relative_layout_blocked_by_me);
        if (mUserId != null && mUserId.equals(getMyUserId())) {
            mRelativeLayoutBlockedByMe.setVisibility(View.GONE);
        } else {
            mRelativeLayoutBlockedByMe.setVisibility(View.VISIBLE);
        }

        mSwitchBlockedByMe = findViewById(R.id.switch_blocked_by_me);
        mSwitchBlockedByMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSwitchBlockedByMe.isChecked()) {
                    SendBird.blockUser(mMember, new SendBird.UserBlockHandler() {
                        @Override
                        public void onBlocked(User user, SendBirdException e) {
                            if (e != null) {
                                mSwitchBlockedByMe.setChecked(false);
                                return;
                            }
                        }
                    });
                } else {
                    SendBird.unblockUser(mMember, new SendBird.UserUnblockHandler() {
                        @Override
                        public void onUnblocked(SendBirdException e) {
                            if (e != null) {
                                mSwitchBlockedByMe.setChecked(true);
                                return;
                            }
                        }
                    });
                }
            }
        });

        refreshUser(profileUrl, nickname, blockedByMe);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserFromUrl(mChannelUrl);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getUserFromUrl(String url) {
        GroupChannel.getChannel(url, new GroupChannel.GroupChannelGetHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {
                    // Error!
                    return;
                }

                mChannel = groupChannel;

                refreshChannel();
            }
        });
    }

    private void refreshChannel() {
        mChannel.refresh(new GroupChannel.GroupChannelRefreshHandler() {
            @Override
            public void onResult(SendBirdException e) {
                if (e != null) {
                    // Error!
                    return;
                }

                for (Member member : mChannel.getMembers()) {
                    if (member.getUserId().equals(mUserId)) {
                        mMember = member;
                        break;
                    }
                }

                refreshUser(mMember.getProfileUrl(), mMember.getNickname(), mMember.isBlockedByMe());
            }
        });
    }

    private void refreshUser(String profileUrl, String nickname, boolean isBlockedByMe) {
        ImageUtils.displayRoundImageFromUrl(this, profileUrl, mImageViewProfile);
        mTextViewNickname.setText(nickname);
        mSwitchBlockedByMe.setChecked(isBlockedByMe);
    }
}
