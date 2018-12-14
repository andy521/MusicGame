package com.fotoable.piano.fragment;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


/**
 * Fragment适配器
 * @author Tercel
 *
 */
public class MainFragmentAdapter extends FragmentStatePagerAdapter{
	private List<Fragment> data;		
	public MainFragmentAdapter(FragmentManager fm) {
		super(fm);

	}

	public void setData(List<Fragment> data){
		this.data = data;
		notifyDataSetChanged();
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public Fragment getItem(int position) {
		return data.get(position);
	}

	@Override
	public int getCount() {
		return data == null ? 0 : data.size();
	}
}
