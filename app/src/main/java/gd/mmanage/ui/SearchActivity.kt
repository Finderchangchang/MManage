package gd.mmanage.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import gd.mmanage.R
import gd.mmanage.base.BaseActivity

class SearchActivity : BaseActivity<>() {
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_search
    }

}
