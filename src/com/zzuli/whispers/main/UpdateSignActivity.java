package com.zzuli.whispers.main;

import cn.bmob.v3.listener.UpdateListener;

import com.zzuli.whispers.bean.User;
import com.zzuli.whispers.main.HeaderLayout.onRightImageButtonClickListener;

import android.os.Bundle;
import android.widget.EditText;

public class UpdateSignActivity extends ActivityBase {
	EditText edit_sign;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_sign);
		initView();
	}

	private void initView() {
		initTopBarForBoth("个性签名", R.drawable.base_action_bar_true_bg_selector,
				new onRightImageButtonClickListener() {

					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						String sign = edit_sign.getText().toString();
						if (sign.equals("")) {
							ShowToast("请填写个性签名!");
							return;
						}
						updateInfo(sign);
					}
				});
		edit_sign = (EditText) findViewById(R.id.edit_nick);
	}

	/** 修改资料
	  * updateInfo
	  * @Title: updateInfo
	  * @return void
	  * @throws
	  */
	private void updateInfo(String sign) {
		final User user = userManager.getCurrentUser(User.class);
		User u = new User();
        u.setSignature(sign);
		u.setHight(110);
		u.setObjectId(user.getObjectId());
		u.update(this, new UpdateListener() {
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				final User c = userManager.getCurrentUser(User.class);
				ShowToast("修改成功:"+c.getNick()+",height = "+c.getHight());
				finish();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("onFailure:" + arg1);
			}
		});
	}
}
