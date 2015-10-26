package com.zzuli.whispers.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.zzuli.whispers.main.CaptureActivity;
import com.zzuli.whispers.main.FragmentBase;
import com.zzuli.whispers.main.HowOldActivity;
import com.zzuli.whispers.main.NearbyPeopleActivity;
import com.zzuli.whispers.main.QCommunityActivity;
import com.zzuli.whispers.main.R;
import com.zzuli.whispers.main.SensorActivity;


/**
 * 发现
 * @ClassName: FindFragment
 * @Description: TODO
 */

public class FindFragment extends FragmentBase implements OnClickListener {

	private View v;
	private TextView txt_pengyouquan;
	private TextView txt_saoyisao;
	private TextView txt_yaoyiyao;
	private TextView how_old;
	private TextView nearby;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		    v = inflater.inflate(R.layout.fragment_find, container, false);
			initViews();
			initEvents();
		return v;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initTopBarForOnlyTitle("发现");
	}

	private void initEvents() {
		txt_pengyouquan.setOnClickListener(this);
		txt_saoyisao.setOnClickListener(this);
		txt_yaoyiyao.setOnClickListener(this);
		how_old.setOnClickListener(this);
		nearby.setOnClickListener(this);
	}

	private void initViews() {
		txt_pengyouquan=(TextView) v.findViewById(R.id.txt_pengyouquan);
		txt_saoyisao=(TextView) v.findViewById(R.id.txt_saoyisao);
		txt_yaoyiyao=(TextView)v.findViewById(R.id.txt_yaoyiyao);
		how_old=(TextView) v.findViewById(R.id.how_old);
		nearby=(TextView) v.findViewById(R.id.nearby);
	
	}

	public <T> void intentAction(Activity context, Class<T> cls) {
		Intent intent = new Intent(context, cls);
		startActivity(intent);
		context.overridePendingTransition(R.anim.push_left_in,
				R.anim.push_left_out);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txt_pengyouquan:
			intentAction(getActivity(), QCommunityActivity.class);
			break;
		case R.id.txt_saoyisao:
			intentAction(getActivity(), CaptureActivity.class);
			break;
		case R.id.txt_yaoyiyao:
			intentAction(getActivity(), SensorActivity.class);
			break;
		case R.id.how_old:
			intentAction(getActivity(), HowOldActivity.class);
			break;
		case R.id.nearby:
			intentAction(getActivity(), NearbyPeopleActivity.class);
			break;
		}
		
	}
}
