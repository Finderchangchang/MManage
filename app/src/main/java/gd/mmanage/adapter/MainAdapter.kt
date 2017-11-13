package gd.mmanage.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import gd.mmanage.ui.main.MainFragment
import gd.mmanage.ui.main.UserFragment

/**
 * Created by Administrator on 2017/11/11.
 */

class MainAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private var fragment = arrayOf<Fragment>(MainFragment(), MainFragment(), UserFragment())

    override fun getItem(position: Int): Fragment {
        return fragment[position]
    }

    override fun getCount(): Int {
        return fragment.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return ""
    }
}
