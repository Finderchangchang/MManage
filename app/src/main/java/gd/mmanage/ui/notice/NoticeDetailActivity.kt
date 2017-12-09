package gd.mmanage.ui.notice

import android.os.Bundle
import com.arialyy.frame.module.AbsModule
import com.google.gson.Gson
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.config.command
import gd.mmanage.databinding.ActivityPartDetailBinding
import gd.mmanage.model.CodeModel
import gd.mmanage.model.NormalRequest
import gd.mmanage.model.NoticeModel
import gd.mmanage.model.PartsModel
import gd.mmanage.ui.parts.PratsModule
import kotlinx.android.synthetic.main.activity_notice_detail.*
import net.tsz.afinal.FinalDb

/**
 * 通知详情
 * @author 杰
 * */
class NoticeDetailActivity : BaseActivity<ActivityPartDetailBinding>(), AbsModule.OnCallback {
    var model: NoticeModel? = null
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        model = intent.getSerializableExtra("model") as NoticeModel
        title_tv.text = model!!.NewsTitle
        web.loadData(model!!.NewsContent, "text/html", "UTF-8")
    }

    override fun onSuccess(result: Int, success: Any?) {

    }

    override fun onError(result: Int, error: Any?) {

    }

    override fun setLayoutId(): Int {
        return R.layout.activity_notice_detail
    }
}