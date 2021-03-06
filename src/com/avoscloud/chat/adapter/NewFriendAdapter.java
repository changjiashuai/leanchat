package com.avoscloud.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.avoscloud.chat.R;
import com.avoscloud.chat.avobject.AddRequest;
import com.avoscloud.chat.avobject.User;
import com.avoscloud.chat.base.App;
import com.avoscloud.chat.service.CloudService;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.ui.view.ViewHolder;
import com.avoscloud.chat.util.NetAsyncTask;
import com.avoscloud.chat.util.Utils;

import java.util.List;

public class NewFriendAdapter extends BaseListAdapter<AddRequest> {

  public NewFriendAdapter(Context context, List<AddRequest> list) {
    super(context, list);
    // TODO Auto-generated constructor stub
  }

  @Override
  public View getView(int position, View conView, ViewGroup parent) {
    // TODO Auto-generated method stub
    if (conView == null) {
      LayoutInflater mInflater = LayoutInflater.from(ctx);
      conView = mInflater.inflate(R.layout.item_add_friend, null);
    }
    final AddRequest addRequest = datas.get(position);
    TextView nameView = ViewHolder.findViewById(conView, R.id.name);
    ImageView avatarView = ViewHolder.findViewById(conView, R.id.avatar);
    final Button addBtn = ViewHolder.findViewById(conView, R.id.add);

    String avatarUrl = addRequest.getFromUser().getAvatarUrl();
    UserService.displayAvatar(avatarUrl,avatarView);

    int status = addRequest.getStatus();
    if (status == AddRequest.STATUS_WAIT) {
      addBtn.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(View arg0) {
          // TODO Auto-generated method stub
          agreeAdd(addBtn, addRequest);
        }
      });
    } else if (status == AddRequest.STATUS_DONE) {
      toAgreedTextView(addBtn);
    }
    nameView.setText(addRequest.getFromUser().getUsername());
    return conView;
  }

  public void toAgreedTextView(Button addBtn) {
    addBtn.setText(R.string.agreed);
    addBtn.setBackgroundDrawable(null);
    addBtn.setTextColor(Utils.getColor(R.color.base_color_text_black));
    addBtn.setEnabled(false);
  }

  private void agreeAdd(final Button addBtn, final AddRequest addRequest) {
    final User fromUser = addRequest.getFromUser();
    new NetAsyncTask(ctx) {
      @Override
      protected void doInBack() throws Exception {
        CloudService.addFriendForBoth(User.curUser(), fromUser);
        addRequest.setStatus(AddRequest.STATUS_DONE);
        addRequest.save();
      }

      @Override
      protected void onPost(Exception e) {
        if (e != null) {
          Utils.toast(App.ctx.getString(R.string.addFailed), e.getMessage());
        } else {
          toAgreedTextView(addBtn);
        }
      }
    }.execute();
  }
}
