package gd.mmanage.ui.message

import com.arialyy.frame.module.AbsModule
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.databinding.ActivitySearchPartsBinding

/**
 * 通知通过查询
 * */
class MessageDetailActivity : BaseActivity<ActivitySearchPartsBinding>(), AbsModule.OnCallback {
    override fun onSuccess(result: Int, success: Any?) {

    }

    override fun onError(result: Int, error: Any?) {

    }

    override fun setLayoutId(): Int {
        return R.layout.activity_message_detail
    }
}