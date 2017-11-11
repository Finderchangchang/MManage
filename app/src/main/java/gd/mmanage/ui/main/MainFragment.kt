package gd.mmanage.ui.main

import android.os.Bundle
import android.widget.Button

import butterknife.BindView
import gd.mmanage.R
import gd.mmanage.base.BaseFragment
import gd.mmanage.databinding.FragMainBinding

/**
 * Created by Administrator on 2017/11/11.
 */

class MainFragment : BaseFragment<FragMainBinding>() {
    @BindView(R.id.ll1)
    internal var mUseModule1: Button? = null

    override fun init(savedInstanceState: Bundle) {

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