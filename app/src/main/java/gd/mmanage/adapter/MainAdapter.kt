package gd.mmanage.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import gd.mmanage.ui.main.MainFragment
import gd.mmanage.ui.main.UserFragment
import gd.mmanage.ui.main.VehicleFragment
import java.util.ArrayList

/**
 * Created by Administrator on 2017/11/11.
 */

class MainAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    internal var list: MutableList<Fragment> = ArrayList()

    init {
        list.add(MainFragment())
        //list.add(VehicleFragment())
        list.add(UserFragment())

    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    override fun getItem(position: Int): Fragment {
        return list[position]
    }

    /**
     * Return the number of views available.
     */
    override fun getCount(): Int {
        return list.size
    }
}
