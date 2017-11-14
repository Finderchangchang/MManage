package gd.mmanage.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Button
import butterknife.Bind
import gd.mmanage.R
import gd.mmanage.base.BaseFragment
import gd.mmanage.databinding.FragUserBinding

/**
 * Created by Administrator on 2017/11/11.
 */

class UserFragment : BaseFragment<FragUserBinding>() {
    override fun load_view(view: View?) {

    }

    override fun init(savedInstanceState: Bundle?) {

    }

    @Bind(R.id.ll1)
    internal var mUseModule1: Button? = null

    /**
     * 延时加载
     */
    override fun onDelayLoad() {

    }

    override fun setLayoutId(): Int {
        return R.layout.frag_user
    }


    override fun dataCallback(result: Int, obj: Any) {

    }

    companion object {
        fun newInstance(): UserFragment {
            val args = Bundle()
            val fragment = UserFragment()
            fragment.arguments = args
            return fragment
        }
    }
}