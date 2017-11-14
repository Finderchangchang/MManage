package gd.mmanage.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout

import butterknife.Bind
import gd.mmanage.R
import gd.mmanage.base.BaseFragment
import gd.mmanage.base.BaseFragments
import gd.mmanage.databinding.FragMainBinding
import gd.mmanage.ui.car_manage.AddPersonActivity

/**
 * Created by Administrator on 2017/11/11.
 */

class MainFragment : BaseFragment<FragMainBinding>() {
    var ll4: LinearLayout? = null
    override fun load_view(view: View?) {
        ll4 = view!!.findViewById(R.id.ll4) as LinearLayout
    }

    var main_context: HomeActivity? = null

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState);
        main_context = HomeActivity.context
        //跳转到
        ll4!!.setOnClickListener {
            startActivity(Intent(context, AddPersonActivity::class.java))
        }
    }

    /**
     * 延时加载
     */
    override fun onDelayLoad() {

    }

    override fun setLayoutId(): Int {
        return R.layout.frag_main
    }


    override fun dataCallback(result: Int, obj: Any) {

    }

    companion object {
        fun newInstance(): MainFragment {
            val args = Bundle()
            val fragment = MainFragment()
            fragment.arguments = args
            return fragment
        }
    }
}