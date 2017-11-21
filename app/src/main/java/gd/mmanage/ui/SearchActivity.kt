package gd.mmanage.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.databinding.ActivitySearchBinding
import gd.mmanage.databinding.ActivitySearchPartsBinding

/**
 * 查询页面功能
 * */
class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_search
    }

}
