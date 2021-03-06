package com.avoscloud.chat.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.avoscloud.chat.avobject.User;
import com.avoscloud.chat.base.C;
import com.avoscloud.chat.util.ChatUtils;
import com.avoscloud.chat.R;
import com.avoscloud.chat.util.NetAsyncTask;
import com.avoscloud.chat.util.Utils;

public class LoginActivity extends BaseActivity implements OnClickListener {
  EditText usernameEdit, passwordEdit;
  Button loginBtn;
  TextView registerBtn;

  private LoginBroadcastReceiver receiver = new LoginBroadcastReceiver();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    init();
    registerReceiver();
  }

  private void registerReceiver() {
    IntentFilter filter = new IntentFilter();
    filter.addAction(C.ACTION_REGISTER_FINISH);
    registerReceiver(receiver, filter);
  }

  private void init() {
    usernameEdit = (EditText) findViewById(R.id.et_username);
    passwordEdit = (EditText) findViewById(R.id.et_password);
    loginBtn = (Button) findViewById(R.id.btn_login);
    registerBtn = (TextView) findViewById(R.id.btn_register);
    loginBtn.setOnClickListener(this);
    registerBtn.setOnClickListener(this);
  }

  public class LoginBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent != null && C.ACTION_REGISTER_FINISH.equals(intent.getAction())) {
        finish();
      }
    }
  }

  @Override
  public void onClick(View v) {
    if (v == registerBtn) {
      Utils.goActivity(ctx, RegisterActivity.class);
    } else {
      login();
    }
  }

  private void login() {
    final String name = usernameEdit.getText().toString();
    final String password = passwordEdit.getText().toString();

    if (TextUtils.isEmpty(name)) {
      Utils.toast(R.string.username_cannot_null);
      return;
    }

    if (TextUtils.isEmpty(password)) {
      Utils.toast(R.string.password_can_not_null);
      return;
    }

    new NetAsyncTask(ctx) {
      @Override
      protected void doInBack() throws Exception {
        User.logIn(name, password);
      }

      @Override
      protected void onPost(Exception e) {
        if (e != null) {
          Utils.toast(e.getMessage());
        } else {
          ChatUtils.updateUserLocation();
          Intent intent = new Intent(LoginActivity.this, MainActivity.class);
          startActivity(intent);
          finish();
        }
      }
    }.execute();

  }

  @Override
  protected void onDestroy() {
    // TODO Auto-generated method stub
    super.onDestroy();
    unregisterReceiver(receiver);
  }
}
