package com.android.gastove;

import com.android.gastove.ui.CalendarFrgmt;
import com.android.gastove.ui.IndexFrgmt;
import com.android.gastove.ui.RctCallsFrgmt;
import com.android.gastove.util.Const;
import com.android.gastove.util.SharedPrefsData;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;


public class TabAdapter extends FragmentPagerAdapter
{
	public static final String TAG = TabAdapter.class.getSimpleName();
	public static final String[] TITLES = new String[] { "记录", "今天", "日历" };
	Fragment mFragment = null;
	boolean[] fragmentsUpdateFlag = { false, false, false };
	private Context mContext;
	FragmentManager fm;
	public TabAdapter(FragmentManager fm,Context mContext)
	{
		super(fm);
		this.fm=fm;
		this.mContext = mContext;
		
	}

	@Override
	public Fragment getItem(int arg0)
	{
		switch (arg0) {
		case 0:
//			String flag = new SharedPrefsData(mContext).getSharedData(Const.SHARED_IN_OUT);
//			if (Const.SHARED_IN.endsWith(flag)) {
//				MainFragment mfragment = new MainFragment(arg0);
//				mFragment = mfragment;
//			} else {
		        RctCallsFrgmt fragment0 = RctCallsFrgmt.getInstance();
		        mFragment = fragment0;
//			}
			break;
		case 1:
	        IndexFrgmt fragment1 = IndexFrgmt.getInstance();
	        mFragment = fragment1;
			break;
		case 2:
	        CalendarFrgmt fragment2 = CalendarFrgmt.getInstance();
	        mFragment = fragment2;
			break;
		default:
			MainFragment mfragment = new MainFragment(arg0);
			mFragment = mfragment;
			break;
		}
		return mFragment;
	}
	@Override

	public Object instantiateItem(ViewGroup container,int position) {
		   //得到缓存的fragment
	    Fragment fragment = (Fragment)super.instantiateItem(container,
	           position);
//		if (position == 1) {
	   //得到tag ❶
	    String fragmentTag = fragment.getTag();         
	   if (fragmentsUpdateFlag[position %fragmentsUpdateFlag.length]) {
	      //如果这个fragment需要更新
	       FragmentTransaction ft =fm.beginTransaction();
	      //移除旧的fragment
	       ft.remove(fragment);
	      //换成新的fragment
	       fragment = new IndexFrgmt();
	      //添加新fragment时必须用前面获得的tag ❶
	       ft.add(container.getId(), fragment, fragmentTag);
	       ft.attach(fragment);
	       ft.commit();
	      

	      //复位更新标志
//	      fragmentsUpdateFlag[position %fragmentsUpdateFlag.length] =false;
	    }
          return fragment;

	}
/*
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if (position != 0) return mFragment;
		String flag = new SharedPrefsData(mContext).getSharedData(Const.SHARED_IN_OUT);
		if (Const.SHARED_IN.endsWith(flag)) {
			MainFragment mfragment = new MainFragment(position);
			mFragment = mfragment;
		} else {
	        RctCallsFrgmt fragment0 = (RctCallsFrgmt) super.instantiateItem(container, position);
	        mFragment = fragment0;
		}
		
	    return mFragment;
	}

	@Override
	public int getItemPosition(Object object) {
		return PagerAdapter.POSITION_NONE;
	}
*/
	@Override
	public CharSequence getPageTitle(int position)
	{
		return TITLES[position % TITLES.length];
	}

	@Override
	public int getCount()
	{
		return TITLES.length;
	}

}
